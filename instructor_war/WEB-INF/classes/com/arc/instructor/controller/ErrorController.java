package com.arc.instructor.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/error"})
public class ErrorController {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	public String showError(HttpServletRequest request, ModelMap model){
		logger.debug("ErrorController showError");
		
		User user = (User) request.getSession().getAttribute("user");

		model.addAttribute("feeOption", user.getFeeOption());
		
		return "courseRecordErrorPage";
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
		logger.debug("ErrorController SABA isLogin is " + getSabaHelper().islogin());
		logger.debug("ErrorController forcing SABA authentication");
		
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
