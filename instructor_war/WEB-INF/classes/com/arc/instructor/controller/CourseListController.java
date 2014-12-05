package com.arc.instructor.controller;

import java.io.IOException;
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

import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;


@Controller
@RequestMapping(value={"/courseListController"})
public class CourseListController {

		private final Log logger = LogFactory.getLog(getClass());
		

	    public CourseListController() {
	    	logger.debug("CourseListController Constructor ");		
	    }
	    
	    @RequestMapping(method=RequestMethod.GET)
		public void getCourseList(HttpServletRequest request, HttpServletResponse response, ModelMap model ){
	    	logger.debug("CourseListController getCourseList");
	    	
    		User user = (User) request.getSession().getAttribute("user");
    		//System.out.println("user " + user);
    		
    		boolean showAll = (request.getParameter("showAll").equals("true")?true:false);
    		//System.out.println("showAll " + showAll);
    		
    		String message =  getSabaHelper().login(user.getUsername(), user.getPassword());
    		if(!message.equals("")){
    			logger.debug("CourseController Error forcing SABA authentication");
    		}
    		
    		List<String> courseCodeList = getSabaHelper().getCourseCodeAutoCompleteList(user.getPersonID(), user.getFeeOption(), showAll);
    		//System.out.println("courseCodeList " + courseCodeList);
    		List<String> sheetNumberList = getSabaHelper().getSheetNoList(user.getPersonID(), user.getFeeOption(), showAll);
    		//System.out.println("sheetNumberList " + sheetNumberList);
       		
    		JSONObject jsonObj = new JSONObject();
    		
    		jsonObj.accumulate("crCourseCodeList", courseCodeList);
    		jsonObj.accumulate("crSheetNumberList", sheetNumberList);

    		logger.debug("Course List: "+jsonObj);
	        
	     	response.reset();
	        response.setContentType("application/json");
	        //response.setCharacterEncoding("UTF-8");
	        try {
	        	response.getWriter().write(jsonObj.toString());
		        response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		private SabaHelper getSabaHelper(){
			
			return new SabaHelper();
		}
		
}

