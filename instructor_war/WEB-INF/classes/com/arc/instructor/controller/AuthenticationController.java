package com.arc.instructor.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;
import com.arc.instructor.utils.SessionValidator;

@Controller
@RequestMapping(value={"/login", "/logout"})
@SessionAttributes("user")
public class AuthenticationController {
    
    private final Log logger = LogFactory.getLog(getClass());
    
    SessionValidator sessionValidator;
    
    @Autowired
    public AuthenticationController(SessionValidator sessionValidator) {
        this.sessionValidator = sessionValidator;
    }
    
    @RequestMapping(method =RequestMethod.POST)
    public String processSubmit(HttpServletRequest request, @ModelAttribute("credentials") User user, BindingResult result, SessionStatus status) {
        logger.debug("AuthenticationController processSubmit");
        sessionValidator.validate(user, result);
        
        String feeOption = request.getParameter("feeOption");
        
        if(feeOption != null && (feeOption.equals(user.FEE_BASED) || feeOption.equals(user.NON_FEE_BASED) || feeOption.equals(user.FACILITY_FEE_BASED) || feeOption.equals(user.SCHEDULING) )) {
            user.setFeeOption(feeOption);
        } else {
            user.setFeeOption(user.NON_FEE_BASED);
        }
        
        if (result.hasErrors()) {
            logger.debug("LoginController validation has errors");
            return "login";
        } else {
            status.setComplete();
            logger.debug("LoginController validation was successful");
            
            request.getSession().setAttribute("user", user);
            
            // check if password needs to be changed
            SabaHelper sabaHelper = new SabaHelper();
            String message = sabaHelper.needPasswordChange();
            if (!message.equals("")) {
                request.getSession().setAttribute("passwdmsg", message);
                return "redirect:/password/change.html";
            }
            
            if(user.getFeeOption().equals(user.NON_FEE_BASED) || user.getFeeOption().equals(user.FACILITY_FEE_BASED))
                return "redirect:/course/courseRecordSheet.html";
            
            if(user.getFeeOption().equals(user.SCHEDULING))
            {
				return "redirect:/offering/offeringSearch.html";
            }
            	
            return "redirect:/search/courseRecordSearch.html";
        }
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String initForm(HttpServletRequest request, ModelMap model) {
        logger.debug("AuthenticationController initForm");
        
        String feeOption = request.getParameter("feeOption");
        
        if(request.getRequestURI().contains("logout")) {
            User logoutUser = (User) request.getSession().getAttribute("user");
            if(logoutUser != null)
                feeOption = logoutUser.getFeeOption();
            request.getSession().setAttribute("user", null);
            request.getSession().setAttribute("courseRecord", null);
            request.getSession().setAttribute("showAllRecords", null);
            //return "redirect:/authentication/login.html?feeOption="+feeOption;
            return "redirect:/authentication/login.html";
        }
        
        User user = new User();
        
        if(feeOption != null && ( feeOption.equals(user.FEE_BASED) || feeOption.equals(user.NON_FEE_BASED) || feeOption.equals(user.FACILITY_FEE_BASED) || feeOption.equals(user.SCHEDULING) ))
            user.setFeeOption(feeOption);
        else
            return "landingPage";
        
        model.addAttribute("credentials", user);
        
        return "login";
    }
    
}
