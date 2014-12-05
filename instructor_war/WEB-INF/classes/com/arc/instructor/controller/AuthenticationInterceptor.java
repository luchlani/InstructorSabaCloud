package com.arc.instructor.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        
        logger.debug("AuthenticationInterceptor ");
        
        if (handler instanceof com.arc.instructor.controller.SearchController ||
            handler instanceof com.arc.instructor.controller.CourseController ||
            handler instanceof com.arc.instructor.controller.InstructorController ||
            handler instanceof com.arc.instructor.controller.ListController ||
            handler instanceof com.arc.instructor.controller.ViewController ||
            handler instanceof com.arc.instructor.controller.StudentController ||
            handler instanceof com.arc.instructor.controller.CourseCategoryController ||
            handler instanceof com.arc.instructor.controller.CouponController ||
            handler instanceof com.arc.instructor.controller.PaymentController ||
            handler instanceof com.arc.instructor.controller.ExistingCourseRecordController
            ) {
            
            User user = (User) request.getSession().getAttribute("user");
            SabaHelper sabaHelper = new SabaHelper();
            if(!sabaHelper.islogin()) {
                if(user == null) {
                    redirect(request, response, "/authentication/login.html");
                    return false;
                } else {
                    
                    String message = sabaHelper.login(user.getUsername(), user.getPassword());
                    if(!message.equals("")) {
                        redirect(request, response, "/authentication/login.html");
                        return false;
                    }
                }
            }
            
            logger.debug("AuthenticationInterceptor isLogin to Saba: " + sabaHelper.islogin());
            
            if(user == null) {
                redirect(request, response, "/authentication/login.html");
                return false;
            }
            
            logger.debug("AuthenticationInterceptor User object found for " + user.getUsername() + " (" + user.getPersonID() + ")");
            logger.debug("Fee Option: " + user.getFeeOption());
            
            if(handler instanceof com.arc.instructor.controller.CourseController) {
                CourseRecord courseRecord = (CourseRecord) request.getSession().getAttribute("courseRecord");
                if(courseRecord == null) {
                    //Navigation implemented for Non-fee Course Record Sheet
                    logger.debug("AuthenticationInterceptor Initializing CourseRecord() ");
                    courseRecord = new CourseRecord();
                    courseRecord.setAction("Create");
                    courseRecord.setFeeOption(user.getFeeOption());
                    request.getSession().setAttribute("courseRecord", courseRecord);
                    return true;
                }
            }
            
        }
        return true;
    }
    
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        logger.debug("AuthenticationInterceptor postHandle ");
    }
    
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("AuthenticationInterceptor afterCompletion ");
    }
    
    private void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException {
        logger.debug("AuthenticationInterceptor redirect "+request.getContextPath() + path);
        try {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.sendRedirect(request.getContextPath() + path);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
    
}
