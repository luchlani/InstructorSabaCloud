package com.arc.instructor.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/coupon"})
public class CouponController {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    @RequestMapping(method=RequestMethod.GET)
    public void processCoupon(HttpServletRequest request, HttpServletResponse response, @RequestParam("action") String action, @RequestParam("orgID") String orgID, @RequestParam("crsID") String crsID, @RequestParam("code") String code, ModelMap model) {
        
        User user = (User) request.getSession().getAttribute("user");
        
        logger.debug("CouponController processCoupon");
        logger.debug("CouponController Action: "+ action);
        logger.debug("CouponController OrgID: "+ orgID);
        logger.debug("CouponController CrsID: "+ crsID);
        logger.debug("CouponController Code: "+ code);
        
        String coupon_msg = "Error";  // default message
        
        String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
        if(!message.equals("")) {
            logger.debug("CouponController Error forcing SABA authentication");
            coupon_msg = "Error forcing SABA authentication";
        }
        
        // Process coupon code
        if(action.equals("apply")) {
            coupon_msg = getSabaHelper().applyCouponToCRS(crsID, code);
            if (coupon_msg.equals("")) {
                coupon_msg = "Error applying coupon.  Please contact technical support.";
            }
        } else if(action.equals("remove")) {
            coupon_msg = getSabaHelper().removeCouponFromCRS(crsID);
            if (coupon_msg.equals("")) {
                coupon_msg = "Error removing coupon.  Please contact technical support.";
            }
        } else {
            coupon_msg = "Unknown Action";
        }
        
        // Get updated CRS fields after processing coupon
        Map<String, String> sabaResponse = new HashMap<String, String>();
        try {
            sabaResponse = getSabaHelper().getSabaWrapper().findCRS(crsID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("SabaHelper processCoupon sabaResponse " + sabaResponse);
        
        // Get new Purchase Order list given new price
        String originalPrice = sabaResponse.get("original_price");
        String finalPrice = sabaResponse.get("final_price");
        String amtPerStudent;
        if (sabaResponse.get("coupon_code") == null || sabaResponse.get("coupon_code").equals("")) {
            amtPerStudent = originalPrice;
        } else {
            amtPerStudent = finalPrice;
        }
        
        String newTotal = getSabaHelper().getTotalPrice(sabaResponse.get("coupon_code"),
                originalPrice, finalPrice, sabaResponse.get("total_students"));
                
        //Lucky - Update the CourseRecord in the session with the Coupon and Updated amounts
		CourseRecord courseRecord = (CourseRecord) request.getSession().getAttribute("courseRecord");
		if(courseRecord != null)
		{
			courseRecord.getPayment().setFinalPrice(amtPerStudent);
			courseRecord.getPayment().setTotalPrice(newTotal);
			courseRecord.getPayment().setPromotionalCode(sabaResponse.get("coupon_code"));
		}
        
        // Get new Purchase Order List
        List<DropDown> purchaseOrderList = getSabaHelper().getPurchaseOrderList(orgID, newTotal);
        
        ArrayList <JSONObject> jsonList = new ArrayList<JSONObject>();
        JSONObject optionJsonObj;
        for (DropDown item : purchaseOrderList) {
            optionJsonObj = new JSONObject();
            optionJsonObj.accumulate("key", item.getKey());
            optionJsonObj.accumulate("value", item.getValue());
            optionJsonObj.accumulate("label", item.getValue());
            jsonList.add(optionJsonObj);
        }
        
        // Get new CC Transaction Signature
        Map<String, String> ccTransaction = getSabaHelper().getCCTransactionSignature(newTotal, "usd", "authorization", crsID);
        //System.out.println("getCCTransactionSignature: " + ccTransaction);
        
        // Create JSON object with necessary information
        JSONObject sendObj = new JSONObject();
        sendObj.put("coupon_msg", coupon_msg);
        sendObj.put("coupon_code", sabaResponse.get("coupon_code"));
        sendObj.put("original_price", originalPrice);
        sendObj.put("final_price", finalPrice);
        sendObj.put("amt_per_student", amtPerStudent);
        sendObj.put("orderPage_signaturePublic", ccTransaction.get("orderPage_signaturePublic"));
        sendObj.put("orderPage_timestamp", ccTransaction.get("orderPage_timestamp"));
        sendObj.put("po_list", jsonList);
        
        // Send response
        response.reset();
        response.setContentType("application/json");
        //response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(sendObj.toString());
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param username
     * @param password
     * @return String
     */
    private String forceSabaAuthentication(String username, String password) {
        //TODO Need a better way to keep Saba session authenticated
        // The SabaHelper.islogin() returns true but there are still authentication errors
        // when saving course record sheet. Forcing authentication to correct Saba session.
        /* Debugging */
        logger.debug("CouponController SABA isLogin is " + getSabaHelper().islogin());
        logger.debug("CouponController forcing SABA authentication");
        
        return getSabaHelper().login(username, password);
    }
    
    private SabaHelper getSabaHelper() {
        return new SabaHelper();
    }
    
}
