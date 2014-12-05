package com.arc.instructor.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;


public class SessionValidator implements Validator {

    private final Log logger = LogFactory.getLog(getClass());

    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        User credentials = (User) obj;
        if (credentials == null) {
            errors.rejectValue("username", "error.login.not-specified", null,
                    "Value required.");
        } else {
            logger.debug("Validating user credentials for: "
                    + credentials.getUsername());
            if (credentials.getUsername() == null || credentials.getUsername().equals("") || credentials.getUsername().contains(" ") ) {
                errors.rejectValue("username", "error.login.invalid-user",
                        null, "Incorrect Username.");
            } else {
                if (credentials.getPassword() == null || credentials.getPassword().equals("") || credentials.getPassword().contains(" ")) {
                    errors.rejectValue("password", "error.login.invalid-pass",
                            null, "Incorrect Password.");
                } else {
                		//TODO: Check if not already authenticated
                		SabaHelper sabaHelper = new SabaHelper();
                		String message = sabaHelper.login(credentials.getUsername(), credentials.getPassword());
                		if(!message.equals("")){
                			errors.rejectValue("sabaMessage", null, null, message);
                		}
                		credentials.setPersonID(sabaHelper.getUserPersonID());
                }
            }

        }
    }

}