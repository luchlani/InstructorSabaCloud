package com.arc.instructor.controller;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

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
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/creditcard"})
public class CreditCardController {
	private final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public void insertCCTrack(HttpServletRequest request, HttpServletResponse response, @RequestParam("crsID") String crsID, ModelMap model) {
		
		User user = (User) request.getSession().getAttribute("user");
		
    	logger.debug("CreditCardController insertCCTrack");
		logger.debug("CreditCardController CrsID: "+ crsID);
		
		String cc_msg = "Success";
		
		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		if(!message.equals("")){
			logger.debug("CreditCardController Error forcing SABA authentication");
			cc_msg = "Error forcing SABA authentication";
		}
		
		// Process purchase order
		cc_msg = getSabaHelper().insertCCTrack(crsID);
        // Send response
        response.reset();
        response.setContentType("text/plain");
        //response.setCharacterEncoding("UTF-8");
        try {
        	response.getWriter().write(cc_msg);
	        response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String processSubmit(HttpServletRequest request, ModelMap model) {
		logger.debug("CreditCardController processSubmit");
		
		User user = (User) request.getSession().getAttribute("user");
		String username = user.getUsername();
		model.addAttribute("username", username);
		
		/*
		 *  Force Saba Authentication, otherwise an exception is thrown (see below)
		 *  com.saba.exception.SabaRuntimeException : (305) Internal Error - A fatal exception occurred.
		 *  javax.security.auth.login.LoginException: java.lang.NullPointerException
		 */
		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		if(!message.equals("")){
			logger.debug("CreditCardController Error forcing SABA authentication");
			return "redirect:/authentication/login.html";
		}
		
		CourseRecord courseRecord = (CourseRecord) request.getSession().getAttribute("courseRecord");
		if (courseRecord == null) {
			logger.debug("courseRecord null");
			return "redirect:/search/courseRecordSearch.html";
			// TODO: Figure out where this needs to go
		} else {
			logger.debug("courseRecord " + courseRecord.getSheetNumberID());
		}
		
		//System.out.println("processSubmit() request: " + request);
		//System.out.println("processSubmit() reasonCode: " + request.getParameter("reasonCode"));
		
		String reasonCode = request.getParameter("reason_code");
		if (reasonCode.equals("100")) {  // Success
			// Save payment info to courseRecord in session for print summary screen
			courseRecord.getPayment().setPaymentTypeID("CC");
			courseRecord.getPayment().setPaymentReference(request.getParameter("req_card_number"));
			courseRecord.getPayment().setCardAccountNumber(request.getParameter("req_card_number"));
			courseRecord.getPayment().setTotalPrice(request.getParameter("req_amount"));
			
			String msg = getSabaHelper().paymentProcessing(request);
			if (!msg.equals("Success")) {
				logger.debug("Error in payment processing");

				courseRecord.getPayment().setPaymentTypeID("CC");
				courseRecord.setAction("PaymentProcessingError");
				courseRecord.getPayment().setSabaErrMsg(msg);
		    	request.getSession().setAttribute("courseRecord", courseRecord);
		    	return "redirect:/payment/courseRecordPaymentDetails.html";
			}	
		} else {
			courseRecord.getPayment().setPaymentTypeID("CC");
			courseRecord.setAction("Reject");
			
			//Get the Error message
			String errMsg = request.getParameter("message");
			if(request.getParameter("invalid_fields")!=null)
			{
				errMsg += ": " + request.getParameter("invalid_fields");
			}
			courseRecord.getPayment().setCreditCardErrMsg(errMsg);
			//End - Get error message
			
			
	    	request.getSession().setAttribute("courseRecord", courseRecord);
	    	return "redirect:/payment/courseRecordPaymentDetails.html";
		}
		
		courseRecord.setAction("Confirmation");
    	//model.addAttribute("courseRecordPayment", courseRecord);
		request.getSession().setAttribute("courseRecordPayment", courseRecord);
		return "redirect:/course/courseRecordSheet.html";
	}
	
	
	/** Ajax call to get Signature and signed_date_time fields
	 *
	 */
	@RequestMapping(value = {"generateSignatureAndInsertCCTrack"})
	public void generateSignature(HttpServletRequest request, HttpServletResponse response, ModelMap map)
	{
		//Get the Parameter Map to sign
		Map<String, String> paramsToSign = new HashMap<String, String>();
		String merchantId = request.getParameter("merchantId");
		Enumeration enumeration = request.getParameterNames();
		while(enumeration.hasMoreElements())
        {
            String paramName = (String)enumeration.nextElement();
            String paramValue = (String)request.getParameter(paramName);
            if(!paramName.equals("merchantId"))
            {
				paramsToSign.put(paramName, paramValue);
			}
        }
		
		
		
		//Generate the Signature and timestamp
		Map<String, String> returnValues = getSabaHelper().generateSignature(merchantId, paramsToSign);
        String time = "";
        String signature = "";
		if(returnValues.get("signature")!=null && returnValues.get("signed_date_time")!=null)
		{
			time = returnValues.get("signed_date_time");
			signature = returnValues.get("signature");
		}
		
		//Insert Row into CC Track
		boolean rowInserted = false;
		try{
			if(request.getParameter("merchant_defined_data3")!=null)
			{
				String crsId = request.getParameter("merchant_defined_data3");
				User user = (User) request.getSession().getAttribute("user");
				getSabaHelper().forceSabaLogin(user);
				getSabaHelper().insertCCTrack(crsId);
				rowInserted = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(!rowInserted)
		{
			time = "";
			signature = "";
		}
		
		
		//Send the JSON Response
        JSONObject jsonObj = new JSONObject();		
		jsonObj.accumulate("signature", signature);
		jsonObj.accumulate("signed_date_time", time);

		response.reset();
        response.setContentType("application/json");
		try 
		{
        	response.getWriter().write(jsonObj.toString());
	        response.getWriter().flush();
	    }
	    catch (IOException e) 
		{
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
		logger.debug("CreditCardController SABA isLogin is " + getSabaHelper().islogin());
		logger.debug("CreditCardController forcing SABA authentication");
		
		return getSabaHelper().login(username, password);
	}
	
	private SabaHelper getSabaHelper(){
		
		return new SabaHelper();
	}

}
