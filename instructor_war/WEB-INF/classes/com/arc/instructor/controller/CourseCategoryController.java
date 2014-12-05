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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/courseCategoryList"})
@SessionAttributes("instructor")
public class CourseCategoryController {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    public CourseCategoryController() {
        logger.debug("CourseCategoryController Constructor ");
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public void getCourseCategoryList(HttpServletRequest request, HttpServletResponse response, @RequestParam("courseCategory") String courseCategory, @RequestParam("courseName") String courseName, @RequestParam("feeOption") String feeOption, ModelMap model ) {
        logger.debug("CourseCategoryController getCourseCategoryList");
        logger.debug("CourseCategoryController Course Category: "+ courseCategory);
        
        if (courseName.equals("%"))
            courseName = "";
        
        List<DropDown> courseCodeList = null;
        if (feeOption.equals("Fee")) {
            courseCodeList =  getSabaHelper().getFeeBasedCourseCodeList(courseCategory, courseName);
        } else if (feeOption.equals("Non-fee")) {
            courseCodeList =  getSabaHelper().getNonFeeBasedCourseCodeList(courseCategory, courseName);
        }
        List<DropDown> courseCodeStudentRequiredList = null;
        if (feeOption.equals("Fee")) {
            courseCodeStudentRequiredList =  getSabaHelper().getFeeBasedCourseCodeStudentRequiredList(courseCodeList);
        }
        
        JSONObject courseCodeListJsonObj;
        
        ArrayList <JSONObject> courseCategoryList = new ArrayList<JSONObject>();
        ArrayList <JSONObject> courseCategoryStudentRequiredList = new ArrayList<JSONObject>();
        
        logger.debug("Course Category Size: " + courseCodeList.size());
        
        for (DropDown courseOption : courseCodeList) {
            courseCodeListJsonObj = new JSONObject();
            
            logger.debug("Course Name: " + courseOption.getValue());
            courseCodeListJsonObj.accumulate("id", courseOption.getKey());
            courseCodeListJsonObj.accumulate("value", courseOption.getValue());
            courseCodeListJsonObj.accumulate("label", courseOption.getValue());
            courseCategoryList.add(courseCodeListJsonObj);
        }
        
        if (feeOption.equals("Fee")) {
            for (DropDown courseOption : courseCodeStudentRequiredList) {
                courseCodeListJsonObj = new JSONObject();
                
                logger.debug("Course Name: " + courseOption.getKey());
                courseCodeListJsonObj.accumulate("id", courseOption.getKey());
                courseCodeListJsonObj.accumulate("studentRequired", courseOption.getValue());
                courseCategoryStudentRequiredList.add(courseCodeListJsonObj);
            }
        }
        
        JSONObject sendObj = new JSONObject();
        sendObj.put("availableCourses", courseCategoryList);
        if (feeOption.equals("Fee")) {
            sendObj.put("courseStudentsRequired", courseCategoryStudentRequiredList);
        }
        
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
    
    private SabaHelper getSabaHelper() {
        return new SabaHelper();
    }
    
}
