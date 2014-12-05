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
@RequestMapping(value={"/reset","/change"})
public class PasswordController {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	public String initForm(HttpServletRequest request, ModelMap model){
		logger.debug("PasswordController initForm");
		
		if (request.getRequestURI().contains("reset")) {
			logger.debug("PasswordController reset page");
			return "passwordReset";
		} else if (request.getRequestURI().contains("change")) {
			logger.debug("PasswordController change page");
			User user = (User) request.getSession().getAttribute("user");
			if (getSabaHelper().islogin() == false || user == null || user.getUsername() == null || user.getUsername().equals("")) {
				return "redirect:/authentication/login.html";
			}
			
			if (request.getSession().getAttribute("passwdmsg") != null) {
				String msg = request.getSession().getAttribute("passwdmsg").toString();
				model.addAttribute("message", msg);
			} else {
				model.addAttribute("message", "");
			}
			
			return "passwordChange";
		}
		
		return "redirect:/authentication/login.html";
	}
	
	@RequestMapping(method =RequestMethod.POST)
	public String processSubmit(HttpServletRequest request, ModelMap model) {
		logger.debug("PasswordController processSubmit");
		
		String action = request.getParameter("action");
		if (action == null) {
			return "redirect:/authentication/login.html";
		}
		
		model.addAttribute("action", action);
		
		if (action.equals("reset")) {
			logger.debug("PasswordController reset");
			
			String email = request.getParameter("email");
			logger.debug("PasswordController email: " + email);
			
			String msg = getSabaHelper().resetPassword(email);
			if (msg.equals("Successful")) {
				model.addAttribute("message", "Your password has been reset and a temporary password has been emailed to you.");
			} else {
				model.addAttribute("message", msg);
				return "passwordReset";
			}
		} else if (action.equals("change")) {
			logger.debug("PasswordController change");
			
			if (getSabaHelper().islogin() == false) {
				model.addAttribute("message", "Failed to change password.  Login session expired.");
			}
			
			String oldpasswd = request.getParameter("oldpasswd");
			String newpasswd1 = request.getParameter("newpasswd1");
			String newpasswd2 = request.getParameter("newpasswd2");
			
			if (newpasswd1.equals(newpasswd2)) {
				User user = (User) request.getSession().getAttribute("user");
				
				String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
				if(!message.equals("")){
					logger.debug("PasswordController Error forcing SABA authentication");
					return "redirect:/authentication/login.html";
				}
				
				String msg = getSabaHelper().changePassword(oldpasswd, newpasswd1);
				if (msg.equals("Successful")) {
					model.addAttribute("message", "Your password has been successfully changed.");
				} else {
					model.addAttribute("message", msg);
					return "passwordChange";
				}
			} else {
				model.addAttribute("message", "Error: The passwords do not match");
				return "passwordChange";
			}
		}
		
		return "passwordConfirmation";
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
