package com.arc.instructor.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.arc.instructor.model.User;
import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/purchaseorder"})
public class PurchaseOrderController {
	private final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public void getPurchaseOrderList(HttpServletRequest request, HttpServletResponse response, @RequestParam("orgID") String orgID, @RequestParam("amt") String amt, ModelMap model) {
		
		User user = (User) request.getSession().getAttribute("user");
		
    	logger.debug("PurchaseOrderController getPurchaseOrderList");
		logger.debug("PurchaseOrderController OrgID: "+ orgID);
		
		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		if(!message.equals("")){
			logger.debug("PurchaseOrderController Error forcing SABA authentication");
		}

		List<DropDown> purchaseOrderList = getSabaHelper().getPurchaseOrderList(orgID, amt);
		
		ArrayList <JSONObject> jsonList = new ArrayList<JSONObject>();
		JSONObject optionJsonObj;
		for (DropDown item : purchaseOrderList)
        {
			optionJsonObj = new JSONObject();
			optionJsonObj.accumulate("key", item.getKey());
			optionJsonObj.accumulate("value", item.getValue());
			optionJsonObj.accumulate("label", item.getValue());
			jsonList.add(optionJsonObj);
        }
		
		// Create JSON object with necessary information
		JSONObject sendObj = new JSONObject();
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
	
	@RequestMapping(method=RequestMethod.GET)
	public void processPurchaseOrder(HttpServletRequest request, HttpServletResponse response, @RequestParam("crsID") String crsID, @RequestParam("poID") String poID, ModelMap model) {
		
		User user = (User) request.getSession().getAttribute("user");
		
    	logger.debug("PurchaseOrderController processPurchaseOrder");
		logger.debug("PurchaseOrderController CrsID: "+ crsID);
		logger.debug("PurchaseOrderController PoID: "+ poID);
		
		String po_msg;
		
		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		if(!message.equals("")){
			logger.debug("PurchaseOrderController Error forcing SABA authentication");
			po_msg = "Error forcing SABA authentication";
		}
		
		// Process purchase order
		po_msg = getSabaHelper().addPurchaseOrder(crsID, poID);
		
		//System.out.println("SabaHelper addPurchaseOrder returned: " + po_msg);
		
        // Send response
        response.reset();
        response.setContentType("text/plain");
        //response.setCharacterEncoding("UTF-8");
        try {
        	response.getWriter().write(po_msg);
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
	private String forceSabaAuthentication(String username, String password){
		//TODO Need a better way to keep Saba session authenticated
		// The SabaHelper.islogin() returns true but there are still authentication errors
		// when saving course record sheet. Forcing authentication to correct Saba session.
		/* Debugging */
		logger.debug("PurchaseOrderController SABA isLogin is " + getSabaHelper().islogin());
		logger.debug("PurchaseOrderController forcing SABA authentication");
		
		return getSabaHelper().login(username, password);
	}
	
	private SabaHelper getSabaHelper(){
		
		return new SabaHelper();
	}

}
