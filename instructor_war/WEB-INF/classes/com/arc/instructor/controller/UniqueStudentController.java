package com.arc.instructor.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.arc.instructor.model.CourseComponent;
import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.People;
import com.arc.instructor.model.Student;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/validateStudent"})
public class UniqueStudentController {

		private final Log logger = LogFactory.getLog(getClass());
	    
	    
	    @RequestMapping(method=RequestMethod.POST)
		public void processStudent(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type) {
	    	
	    	User user = (User) request.getSession().getAttribute("user");
	    	
	    	String first = request.getParameter("first");
	    	String last = request.getParameter("last");
	    	String email = request.getParameter("email");
	    	
	    	boolean found_user = false;
	 
			logger.debug("UniqueStudentController processStudent");
			logger.debug("UniqueStudentController First:" + request.getParameter("first"));
			logger.debug("UniqueStudentController Last:" + request.getParameter("last"));
			logger.debug("UniqueStudentController Email:" + request.getParameter("email"));
			
			String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
			if(!message.equals("")){
				logger.debug("UniqueStudentController Error forcing SABA authentication");
			}
			
			// check for Saba student
			Map<String, String> sabaResponse = getSabaHelper().findUser(first, last, email);
			
			String first_name = "";
			String last_name = "";
			if (sabaResponse.size() > 0) {
				for (Map.Entry<String, String> entry : sabaResponse.entrySet())
				{
					//System.out.println("sabaResponse() key: " + entry.getKey() + " value ="+entry.getValue());
				    if (entry.getKey().equals("first_name"))
				    	first_name = entry.getValue();
				    if (entry.getKey().equals("last_name"))
				    	last_name = entry.getValue();
				}
				
				if (first_name.toLowerCase().equals(first.toLowerCase()) && last_name.toLowerCase().equals(last.toLowerCase())) {
					found_user = true;
				}
			}
			
			JSONObject sendObj = new JSONObject();
	        sendObj.accumulate("found", (found_user?"true":"false"));
	        sendObj.accumulate("results", sabaResponse.size());
	        sendObj.accumulate("first_name", first_name);
	        sendObj.accumulate("last_name", last_name);
	        sendObj.accumulate("email", email);
			
			response.reset();
	     	response.setContentType("application/json");
	        //response.setCharacterEncoding("UTF-8");
	        try {
	        	response.getWriter().write(sendObj.toString());
		        response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			logger.debug("UniqueStudentController SABA isLogin is " + getSabaHelper().islogin());
			logger.debug("UniqueStudentController forcing SABA authentication");
			
			return getSabaHelper().login(username, password);
		}

		private SabaHelper getSabaHelper(){
			
			return new SabaHelper();
		}
		
}

