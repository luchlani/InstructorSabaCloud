package com.arc.instructor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.CourseRecordSearch;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;


@Controller
@RequestMapping(value={"/courseRecordSearch"})
@SessionAttributes("courseRecordSearch")
public class SearchController {

	private final Log logger = LogFactory.getLog(getClass());
	 

    public SearchController() {
    	logger.debug("SearchController Constructor ");	
    }

	@RequestMapping(method = RequestMethod.GET)
	public String initForm(HttpServletRequest request,ModelMap model){
		logger.debug("SearchController initForm");
    	User user = (User) request.getSession().getAttribute("user");
		request.getSession().setAttribute("courseRecord", null);
    	CourseRecordSearch courseRecordSearch = new CourseRecordSearch();
    	
    	String showAll = "false";
    	if (request.getSession().getAttribute("showAllRecords") != null) {
    		showAll = request.getSession().getAttribute("showAllRecords").toString();
    	} else {
    		request.getSession().setAttribute("showAllRecords", showAll);
    	}
    	
    	model.addAttribute("user", user);
    	model.addAttribute("courseRecordSearch", courseRecordSearch);
    	model.addAttribute("feeOption", user.getFeeOption());
    	model.addAttribute("showAll", showAll);
    	
		return "courseRecordSearch";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(HttpServletRequest request,ModelMap model, @ModelAttribute("courseRecordSearch") CourseRecordSearch courseRecordSearch, BindingResult result, SessionStatus status) {
 
		logger.debug("SearchController processSubmit");

	   	logger.debug("Action: "+courseRecordSearch.getAction());
    	logger.debug("Fee Option: "+courseRecordSearch.getFeeOption());
    	
    	model.remove("crStatusList");
    	model.remove("crCourseCodeList");
    	model.remove("crSheetNumberList");
    	
    	if(courseRecordSearch.getAction().equals("Delete")){
    		logger.debug("Delete Course Record Sheet Number: " + courseRecordSearch.getSheetNumberID());
    	   	User user = (User) request.getSession().getAttribute("user");
    		request.getSession().setAttribute("courseRecord", null);
        	model.addAttribute("user", user);
        	model.addAttribute("courseRecordSearch", courseRecordSearch);
        	model.addAttribute("feeOption", user.getFeeOption());   
        	
        	//Delete Course Record
    		logger.debug("CourseController SABA isLogin is " + getSabaHelper().islogin());
    		logger.debug("CourseController forcing SABA authentication");
    		
    		String message =  getSabaHelper().login(user.getUsername(), user.getPassword());
    		if(!message.equals("")){
    			logger.debug("CourseController Error forcing SABA authentication");
    			return "redirect:/authentication/login.html";
    		}
    		
    		message = getSabaHelper().deleteCourseRecordSheet(courseRecordSearch.getSheetNumberID());
    		
    		if(message.contains("Error")){
    			courseRecordSearch.setAction(message);
    			result.rejectValue("sabaMessage", null, null, "Error Deleting Course Record Sheet ("+ courseRecordSearch.getSheetNumber() + "). Please contact your administrator");
    		} 
    		
    		return "/courseRecordSearch";
    	}
    		    	
    	if(courseRecordSearch.getAction().equals("Edit") || courseRecordSearch.getAction().equals("Copy")){
    		logger.debug(courseRecordSearch.getAction() + " Course Record Sheet");
	    	logger.debug("Sheet Number: " + courseRecordSearch.getSheetNumber());
	    	logger.debug("OrganizationName: " + courseRecordSearch.getOrganizationName());
	    	logger.debug("Course Code: " + courseRecordSearch.getCourseCode());
	    	request.getSession().setAttribute("courseRecordSearch", courseRecordSearch);
    	} else {
    		logger.debug("Creating New Course Record Sheet");
    	}
    	
    	 return "redirect:/course/courseRecordSheet.html";
	}
	
	
	@ModelAttribute("crStatusList")
	public List<String> populateCourseRecordStatusList() {
	
		return getSabaHelper().getStatusList();
	}
	
	@ModelAttribute("crCourseCodeList")
	public List<String> populateCourseRecordCodeList(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		return getSabaHelper().getCourseCodeAutoCompleteList(user.getPersonID(), user.getFeeOption(), false);
	}
	
	@ModelAttribute("crSheetNumberList")
	public List<String> populateSheetNumberList(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		return getSabaHelper().getSheetNoList(user.getPersonID(), user.getFeeOption(), false);
	}
	
	
	private SabaHelper getSabaHelper(){
		
		return new SabaHelper();
	}

}