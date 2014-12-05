package com.arc.instructor.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/paylater"})
public class PayLaterController {
	private final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public void paymentPendingNotification(HttpServletRequest request, HttpServletResponse response, @RequestParam("crsID") String crsID, ModelMap model) {
		
		User user = (User) request.getSession().getAttribute("user");
		
    	logger.debug("PayLaterController paymentPendingNotification");
		logger.debug("PayLaterController CrsID: "+ crsID);
		
		String pl_msg = "Success";
		
		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		if(!message.equals("")){
			logger.debug("PayLaterController Error forcing SABA authentication");
			pl_msg = "Error forcing SABA authentication";
		}
		
		// Process purchase order
		pl_msg = getSabaHelper().paymentPendingNotification(crsID);
		
        // Send response
        response.reset();
        response.setContentType("text/plain");
        //response.setCharacterEncoding("UTF-8");
        try {
        	response.getWriter().write(pl_msg);
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
		logger.debug("PayLaterController SABA isLogin is " + getSabaHelper().islogin());
		logger.debug("PayLaterController forcing SABA authentication");
		
		return getSabaHelper().login(username, password);
	}
	
	private SabaHelper getSabaHelper(){
		
		return new SabaHelper();
	}

}
