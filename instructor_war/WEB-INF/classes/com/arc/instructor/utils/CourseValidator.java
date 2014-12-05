package com.arc.instructor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.arc.instructor.model.CourseRecord;


public class CourseValidator implements Validator {

    private final Log logger = LogFactory.getLog(getClass());

    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return CourseRecord.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        CourseRecord courseRecord = (CourseRecord) obj;
        logger.debug("Validating Course Record Entry");

        if (courseRecord == null) {
        	errors.rejectValue("sabaMessage", null, null, "Please fill in requred fields");
        } else {
            logger.debug("Validating courseRecord Organization: "
                    + courseRecord.getOrganizationID());
            if (courseRecord.getOrganizationID() == null || courseRecord.getOrganizationID().equals("")) {
                errors.rejectValue("organizationID", "error.courseRecord.invalid-organization",
                        null, "Incorrect Organization ID");
            }
            logger.debug("Validating courseRecord Course: "
                    + courseRecord.getCourseID());
            if (courseRecord.getCourseID() == null || courseRecord.getCourseID().equals("")) {
                errors.rejectValue("courseID", "error.courseRecord.invalid-course",
                        null, "Incorrect Course ID");
            }
            logger.debug("Validating courseRecord Course Ending Date: "
                    + courseRecord.getEndDate());
            if (courseRecord.getEndDate() == null || courseRecord.getEndDate().equals("")) {
                errors.rejectValue("endDate", "error.courseRecord.invalid-endDate",
                        null, "Incorrect Course Ending Date");
            } else {
               	try{
               		
               		if(!courseRecord.getEndDate().matches("\\d{2}/\\d{2}/\\d{4}")){
               			errors.rejectValue("endDate", null,
                                null, "Course Ending Date must have a format of MM/DD/YYYY");
               		} else {
               		
               			// check date on fee non precreate page
               			if(courseRecord.getFeeOption().equals("Fee") && !courseRecord.isPreCreate()){
               				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		            		Date now = new Date();
		            		dateFormat.setLenient(false);
		                	Date endDate = dateFormat.parse(courseRecord.getEndDate());
		                	
		                	//dateFormat.format(now);
		                	logger.debug("Validating courseRecord Offering End Date: " + endDate + " to "+  now + " " + endDate.equals(now) + " " + endDate.after(now) );
		                	if(endDate.equals(now) || endDate.after(now) ){
		                		errors.rejectValue("endDate", null,
		                                null, "Course Ending Date must be earlier than today's date");
		                	}
               			}
               			
               			if (courseRecord.getFeeOption().equals("Non-fee") || courseRecord.getFeeOption().equals("Facility-fee")){
		            		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		            		Date now = new Date();
		            		dateFormat.setLenient(false);
		                	Date endDate = dateFormat.parse(courseRecord.getEndDate());
		                	
		                	//dateFormat.format(now);
		                	logger.debug("Validating courseRecord Offering End Date: " + endDate + " to "+  now + " " + endDate.equals(now) + " " + endDate.after(now) );
		                	if(endDate.equals(now) || endDate.after(now) ){
		                		errors.rejectValue("endDate", null,
		                                null, "Course Ending Date must be earlier than today's date");
		                	}
               			}
               		}
         
            	} catch(ParseException ex){
            		//ex.printStackTrace();
            		 errors.rejectValue("endDate", null,
                             null, "Course Ending Date must be a valid date");
            	} catch(IllegalArgumentException ie){
            		//ie.printStackTrace();
           		 	errors.rejectValue("endDate", "error.courseRecord.invalid-endDate",
                            null, "Incorrect Course Ending Date");
            	} catch(Exception e){
            		//e.printStackTrace();
           		 	errors.rejectValue("endDate", "error.courseRecord.invalid-endDate",
                            null, "Incorrect Course Ending Date");
            	} 
            	
            }
            
            if(!courseRecord.getFeeOption().equals("Facility-fee")) {
            
                int totalStudents = 0;
                logger.debug("Validating courseRecord Total Students: "
                        + courseRecord.getTotalStudents());
                if (courseRecord.getTotalStudents() == null) {
                    errors.rejectValue("totalStudents", "error.courseRecord.invalid-totalStudents",
                            null, "Incorrect Total Student Count");
                } else {
                    try {
                    	totalStudents = Integer.parseInt(courseRecord.getTotalStudents());
                    	if(totalStudents < 1 || totalStudents > 500){
                            errors.rejectValue("totalStudents", null,
                                    null, "Total Student Count must be greater than zero and less than 500");
                    	}
                    } catch(NumberFormatException e) {
                        errors.rejectValue("totalStudents", "error.courseRecord.invalid-totalStudents",
                                null, "Incorrect Total Student Count");
                   }
                }
                int totalSuccessful = 0;
                logger.debug("Validating courseRecord Total Successful: "
                        + courseRecord.getTotalSuccessful());
                if (courseRecord.getTotalSuccessful() == null) {
                    errors.rejectValue("totalSuccessful", "error.courseRecord.invalid-totalSuccessful",
                            null, "Incorrect Total Successful Count");
                } else {
                    try {
                    	totalSuccessful = Integer.parseInt(courseRecord.getTotalSuccessful());
                    } catch(NumberFormatException e) {
                        errors.rejectValue("totalSuccessful", "error.courseRecord.invalid-totalSuccessful",
                                null, "Incorrect Total Successful Count");
                   }
                }
                int totalUnsuccessful = 0;
                logger.debug("Validating courseRecord Total Unsuccessful: "
                        + courseRecord.getTotalUnsuccessful());
                if (courseRecord.getTotalUnsuccessful() == null) {
                    errors.rejectValue("totalUnsuccessful", "error.courseRecord.invalid-totalUnsuccessful",
                            null, "Incorrect Total Unsuccessful Count");
                } else {
                    try {
                    	totalUnsuccessful = Integer.parseInt(courseRecord.getTotalUnsuccessful());
                    } catch(NumberFormatException e) {
                        errors.rejectValue("totalUnsuccessful", "error.courseRecord.invalid-totalUnsuccessful",
                                null, "Incorrect Total Unsuccessful Count");
                   }
                }
                int totalNotEvaluated = 0;
                logger.debug("Validating courseRecord Total Not Evaluated: "
                        + courseRecord.getTotalNotEvaluated());
                if (courseRecord.getTotalNotEvaluated() == null) {
                    errors.rejectValue("totalNotEvalutotalUnsuccessfulated", "error.courseRecord.invalid-totalNotEvaluated",
                            null, "Incorrect Total Not Evaluated Count");
                } else {
                    try {
                    	totalNotEvaluated = Integer.parseInt(courseRecord.getTotalNotEvaluated());
                    } catch(NumberFormatException e) {
                        errors.rejectValue("totalNotEvaluated", "error.courseRecord.invalid-totalNotEvaluated",
                                null, "Incorrect Total Not Evaluated Count");
                   }
                }
                
                if(courseRecord.isStudentDetails() || courseRecord.getFeeOption().equals("Non-fee")){
    	            if(totalStudents != (totalSuccessful + totalUnsuccessful + totalNotEvaluated))
    	                errors.rejectValue("totalStudents", null,
    	                        null, "Sub Totals Total Successful, Total Unsuccessful and Total Not Evaluated must equal Total Students");
                }
            
            }
            
            logger.debug("Validating courseRecord Address Name: "
                    + courseRecord.getTrainingCenterName());
            if (courseRecord.getTrainingCenterName() == null || courseRecord.getTrainingCenterName().equals("")) {
                errors.rejectValue("trainingCenterName", "error.courseRecord.invalid-trainingCenterName",
                        null, "Incorrect Address Name");
            }
            
            logger.debug("Validating courseRecord Street Address: "
                    + courseRecord.getStreetAddress());
            if (courseRecord.getStreetAddress() == null || courseRecord.getStreetAddress().equals("")) {
                errors.rejectValue("streetAddress", "error.courseRecord.invalid-streetAddress",
                        null, "Incorrect Street Address");
            }  
            
            logger.debug("Validating courseRecord Postal Code: "
                    + courseRecord.getZipCode());
            if (courseRecord.getZipCode() == null || courseRecord.getZipCode().equals("") || courseRecord.getZipCode().length() != 5) {
                errors.rejectValue("zipCode", "error.courseRecord.invalid-zipCode",
                        null, "Incorrect Postal Code");
            } else {
                try {
                	Integer.parseInt(courseRecord.getZipCode());
                	SabaHelper sabaHelper = new SabaHelper();
                	ArrayList<String> addressInformation = sabaHelper.getCityStateCounty(courseRecord.getZipCode());
                	if(!addressInformation.isEmpty()){
                		if(addressInformation.get(0) != null && !addressInformation.get(0).equals(""))
                			courseRecord.setCity(addressInformation.get(0));
                		if(addressInformation.get(1) != null && !addressInformation.get(1).equals(""))
                			courseRecord.setState(addressInformation.get(1));
                		if(addressInformation.get(2) != null && !addressInformation.get(2).equals("") )
                			courseRecord.setCounty(addressInformation.get(2));
                	}
                } catch(NumberFormatException e) {
                    errors.rejectValue("zipCode", "error.courseRecord.invalid-zipCode",
                            null, "Incorrect Postal Code");
               }
            }
            
            logger.debug("Validating courseRecord City: "
                    + courseRecord.getCity());
            if (courseRecord.getCity() == null || courseRecord.getCity().equals("")) {
                errors.rejectValue("city", "error.courseRecord.invalid-city",
                        null, "Incorrect City");
            } 
            
            logger.debug("Validating courseRecord State: "
                    + courseRecord.getState());
            if (courseRecord.getState() == null || courseRecord.getState().equals("") || courseRecord.getState().equals("State")) {
                errors.rejectValue("state", "error.courseRecord.invalid-state",
                        null, "Incorrect State");
            }  
            
            if(!courseRecord.getFeeOption().equals("Facility-fee")) {
            
                logger.debug("Validating courseRecord Instructor: "
                        + courseRecord.getIsInstructor());
                if (!courseRecord.getIsInstructor()) {
                    errors.rejectValue("errorMessage", "error.courseRecord.invalid-instructor",
                            null, "Incorrect Instructor Count");
                }
                
                if (courseRecord.getPayment().getPaymentTypeID() != null) {
                	logger.debug("Validating courseRecord Payment Type: "
                            + courseRecord.getPayment().getPaymentTypeID());
                	if (courseRecord.getPayment().getPaymentTypeID().equals("")) {
                		errors.rejectValue("payment.paymentTypeID", "error.courseRecord.invalid-payment-type",
                				null, "Incorrect Payment Type");
                	} else if (courseRecord.getPayment().getPaymentTypeID().equals("PO") && courseRecord.getPayment().getPurchaseOrderID()!=null && courseRecord.getPayment().getPurchaseOrderID().equals("")) {
                		errors.rejectValue("payment.purchaseOrderID", "error.courseRecord.invalid-purchase-order",
                				null, "Incorrect Purchase Order");
                	}
                }
            
            }
            
            if(errors.hasErrors())
            	errors.rejectValue("sabaMessage", null, null, "Please correct the form errors below to submit course record entry");
        }
    }

}