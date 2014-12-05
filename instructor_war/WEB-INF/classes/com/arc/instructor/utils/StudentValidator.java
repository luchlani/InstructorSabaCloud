package com.arc.instructor.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.arc.instructor.model.CourseComponent;
import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.Student;


public class StudentValidator implements Validator {

    private final Log logger = LogFactory.getLog(getClass());

    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return CourseRecord.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        CourseRecord courseRecord = (CourseRecord) obj;
        logger.debug("Validating Student Course Record Entry");

        if (!courseRecord.getIsStudents()) {
        	errors.rejectValue("errorMessage", null, null, "Course record entry requires at least one student");
        } else {

            int totalStudents = 0;
            try {
            	totalStudents = Integer.parseInt(courseRecord.getTotalStudents());
            } catch(NumberFormatException e) {
                errors.rejectValue("totalStudents", "error.courseRecord.invalid-totalStudents",
                        null, "Incorrect Total Student Count");
           }
           
           int validStudentCount = 0;
           for (int studentIndex=0; studentIndex < courseRecord.getStudents().size(); studentIndex++) {
        	   Student student = courseRecord.getStudents().get(studentIndex);
        	   
        	   boolean firstNameValid = true;
        	   boolean lastNameValid = true;
        	   
               logger.debug("Processing courseRecord Student Index : "
                       + studentIndex);
    		   
               logger.debug("Validating courseRecord Student First Name: "
                       + student.getFirstName());
               if (student.getFirstName() == null || student.getFirstName().equals("")) {
            	   firstNameValid = false;
               }
               
               logger.debug("Validating courseRecord Student Last Name: "
                       + student.getLastName());
               if (student.getLastName() == null || student.getLastName().equals("")) {
                   lastNameValid = false;
               }
               
               if(firstNameValid && lastNameValid)
            	   validStudentCount++;
            	    
               logger.debug("Validating courseRecord Student Email: "
                       + student.getEmail());
               if(student.getEmail() != null){
            	   //Validate Email 
               }
               
               logger.debug("Validating courseRecord Student Phone Number: "
                       + student.getPhoneNumber());	               
               if(student.getPhoneNumber() != null){
            	   //Validate Phone Number
               }
               
               logger.debug("Validating courseRecord Student Postal Code: "
                       + student.getPostalCode());	   	               
               if(student.getPostalCode() != null){
            	   //Validate Postal Code
               }

           }
           
           logger.debug("Valid Student Count " + validStudentCount + " == Total Students " + totalStudents);
           if(validStudentCount == 0)
    		   errors.rejectValue("errorMessage", null, null, "Course record entry requires at least one student");
        }
    }

}