package com.arc.instructor.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.PaymentValidator;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/courseRecordPaymentDetails"})
public class PaymentController {

	private final Log logger = LogFactory.getLog(getClass());
	
	PaymentValidator paymentValidator;
	
	public PaymentController() {
    	logger.debug("PaymentController constructor");
    }
	
	@Autowired
	public  PaymentController(PaymentValidator paymentValidator) {
		this.paymentValidator = paymentValidator;
		
	}
	
	/**
	 * @param request
	 * @param model
	 * @return Model View
	 */
	@RequestMapping(method=RequestMethod.GET) 
	public String initForm(HttpServletRequest request, ModelMap model){
		logger.debug("PaymentController initForm");
		
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
			logger.debug("PaymentController Error forcing SABA authentication");
			return "redirect:/authentication/login.html";
		}
		
		// Get course record from session
		CourseRecord courseRecord = (CourseRecord) request.getSession().getAttribute("courseRecord");
		if (courseRecord == null) {
			logger.debug("courseRecord null");
			return "redirect:/search/courseRecordSearch.html";
		} else {
			logger.debug("courseRecord " + courseRecord.getSheetNumberID());
		}
		
		//System.out.println("GET user fee option: " + user.getFeeOption());
		courseRecord.setFeeOption(user.getFeeOption());
		
		if (courseRecord.getAction().equals("Reject")) {  // If credit card was rejected
			model.addAttribute("card_status", "reject");
		}
		if (courseRecord.getAction().equals("PaymentProcessingError")) {  // If error during processing
			courseRecord.setAction("Error");
			model.addAttribute("courseRecord", courseRecord);
			return "/courseRecordPaymentError";
		}
		
		courseRecord.setAction("Payment");
		logger.debug("courseRecord paymentType: "+courseRecord.getPayment().getPaymentTypeID());

		model.addAttribute("courseRecord", courseRecord);
		
		logger.debug("Original Price: " + courseRecord.getPayment().getOriginalPrice());
		logger.debug("Final Price: " + courseRecord.getPayment().getFinalPrice());
		logger.debug("Coupon Code: " + courseRecord.getPayment().getPromotionalCode());

		String totalPrice = getSabaHelper().getTotalPrice(courseRecord);
		model.addAttribute("totalPrice", totalPrice);
		
		model.addAttribute("agreeChecked", (courseRecord.getPayment().getAgreement()==true?"checked":""));
		
		List<DropDown> purchaseOrderList = getSabaHelper().getPurchaseOrderList(courseRecord.getOrganizationID(), totalPrice.toString());
		model.addAttribute("purchaseOrderList", purchaseOrderList);
		
		Map<String, String> staticValues = getSabaHelper().getCCStaticValues(courseRecord.getSheetNumberID());		
		for(Map.Entry<String, String> entry : staticValues.entrySet())
		{
			model.addAttribute(entry.getKey(), entry.getValue());
		}
		
		
		Date date = new Date();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    Integer month = cal.get(Calendar.MONTH)+1;
	    Integer year = cal.get(Calendar.YEAR);
	    
	    model.addAttribute("cur_month", String.format("%02d", month));
	    model.addAttribute("cur_year", year.toString());
		
		//model.addAttribute("request_proto", request.getProtocol());
		//model.addAttribute("request_url", request.getRequestURL());
		//model.addAttribute("request_server", request.getServerName());
		//model.addAttribute("request_referer", request.getHeader("Referer"));
		
		return "/courseRecordPaymentDetails";
	}
	
	/**
	 * 
	 * @param request
	 * @param model
	 * @param courseRecord
	 * @return Model View
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String processSubmit(HttpServletRequest request, ModelMap model, @ModelAttribute("courseRecord") CourseRecord paymentCourseRecord) {
		logger.debug("PaymentController processSubmit");
		
		User user = (User) request.getSession().getAttribute("user");
		String username = user.getUsername();
		model.addAttribute("username", username);
		String action = null;
		
		if (paymentCourseRecord != null) {
			action = paymentCourseRecord.getAction();
		}
		
		logger.debug("paymentCourseRecord: "+paymentCourseRecord);
		logger.debug("paymentCourseRecord paymentType: "+paymentCourseRecord.getPayment().getPaymentTypeID());
		
		/*
		 *  Force Saba Authentication, otherwise an exception is thrown (see below)
		 *  com.saba.exception.SabaRuntimeException : (305) Internal Error - A fatal exception occurred.
		 *  javax.security.auth.login.LoginException: java.lang.NullPointerException
		 */
		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		if(!message.equals("")){
			logger.debug("PaymentController Error forcing SABA authentication");
			return "redirect:/authentication/login.html";
		}
		
		CourseRecord courseRecord = (CourseRecord) request.getSession().getAttribute("courseRecord");
		if (courseRecord == null) {
			logger.debug("courseRecord null");
			return "redirect:/search/courseRecordSearch.html";
		} else {
			logger.debug("courseRecord " + courseRecord.getSheetNumberID());
		}
		
		//System.out.println("POST user fee option: " + user.getFeeOption());
		courseRecord.setFeeOption(user.getFeeOption());
		
    	// remove these from model
    	model.remove("username");
    	model.remove("yearList");
    	model.remove("countryList");
		
		if(action != null && (action.equals("Back"))) {
			courseRecord.setAction("Payment");
	    	//model.addAttribute("courseRecordPayment", courseRecord);
	    	request.getSession().setAttribute("courseRecordPayment", courseRecord);
	    	return "redirect:/course/courseRecordSheet.html";
		}
		
		logger.debug("Username: "+ username);
	   	logger.debug("Fee Option: "+courseRecord.getFeeOption());
	    logger.debug("Sheet Number: " + courseRecord.getSheetNumber());
    	logger.debug("OrganizationName: " + courseRecord.getOrganizationID());
    	logger.debug("Course Code: " + courseRecord.getCourseID());
    	
    	// copy payment fields to courseRecord in session
    	courseRecord.getPayment().setPaymentTypeID(paymentCourseRecord.getPayment().getPaymentTypeID());
    	courseRecord.getPayment().setPaymentType(paymentCourseRecord.getPayment().getPaymentType());
    	
    	// we set this here because we don't call findCRS before the confirmation screen
    	if (paymentCourseRecord.getPayment().getPaymentTypeID().equals("PO"))
    		courseRecord.getPayment().setPaymentReference(paymentCourseRecord.getPayment().getPurchaseOrder());
    	
    	courseRecord.getPayment().setPurchaseOrderID(paymentCourseRecord.getPayment().getPurchaseOrderID());
    	courseRecord.getPayment().setPurchaseOrder(paymentCourseRecord.getPayment().getPurchaseOrder());
    	courseRecord.getPayment().setPromotionalCode(paymentCourseRecord.getPayment().getPromotionalCode());
    	courseRecord.getPayment().setTotalPrice(paymentCourseRecord.getPayment().getTotalPrice());
    	
    	courseRecord.setAction("Confirmation");
    	//model.addAttribute("courseRecordPayment", courseRecord);
    	request.getSession().setAttribute("courseRecordPayment", courseRecord);
	    return "redirect:/course/courseRecordSheet.html";
	}
	
	/**
	 * 
	 * @param request
	 * @return List<String>
	 */
	@ModelAttribute("paymentTypeList")
	public List<DropDown> populatePaymentTypeList(HttpServletRequest request) {
	    //System.out.println("payment populatePaymentTypeList() courseRecord: " + courseRecord);
	    User user = (User) request.getSession().getAttribute("user");
	    //System.out.println("payment populatePaymentTypeList() fee option: " + user.getFeeOption());
	    return getSabaHelper().getPaymentTypeList(user.getFeeOption());
	}
	
	/**
	 * 
	 * @param request
	 * @return List<DropDown>
	 */
	@ModelAttribute("stateList")
	public List<DropDown> populateStateList() {
		
		return getSabaHelper().getStateList();
	}
	
	/**
	 * 
	 * @param request
	 * @return List<String>
	 */
	@ModelAttribute("countryList")
	public List<String> populateCountryList() {
		
		return getSabaHelper().getCountryList();
	}
	
	/**
	 * 
	 * @param request
	 * @return List<String>
	 */
	@ModelAttribute("monthList")
	public ArrayList<DropDown> populateMonthList() {
		
		return getSabaHelper().getMonthList();
	}
	
	/**
	 * 
	 * @param request
	 * @return List<String>
	 */
	@ModelAttribute("yearList")
	public List<String> populateYearList() {
		
		ArrayList<String> yearList = new ArrayList<String>();
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		for (int i=year; i<=(year+20); i++) {
			yearList.add(Integer.toString(i));
		}
		
		return yearList;
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
		// when saving. Forcing authentication to correct Saba session.
		/* Debugging */
		logger.debug("PaymentController SABA isLogin is " + getSabaHelper().islogin());
		logger.debug("PaymentController forcing SABA authentication");
		
		return getSabaHelper().login(username, password);
	}
	
	/**
	 * 
	 * @return SabaHelper
	 */
	private SabaHelper getSabaHelper(){
		
		return new SabaHelper();
	}
}
