package com.arc.instructor.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.CourseRecordList;
import com.arc.instructor.model.CourseRecordSearch;
import com.arc.instructor.model.People;
import com.arc.instructor.model.Student;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.CourseValidator;
import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;
import com.arc.instructor.utils.StudentValidator;

@Controller
@RequestMapping(value={"/courseRecordSheet", "/preCreateCourseRecordSheet"})
@SessionAttributes({ "courseRecord", "courseRecordList" })

/**
 * 
 * @author John Cordero NCS Technologies, Inc.
 *
 */
public class CourseController  {

	private final Log logger = LogFactory.getLog(getClass());
	
	CourseValidator courseValidator;
	StudentValidator studentValidator;
	

    public CourseController() {
    	logger.debug("CourseController constructor");
    }
    
	@Autowired
	public  CourseController(CourseValidator courseValidator, StudentValidator studentValidator) {
		this.courseValidator = courseValidator;
		this.studentValidator = studentValidator;
	}
    
	/**
	 * @param request
	 * @param model
	 * @return Model View
	 */
	@RequestMapping(method=RequestMethod.GET) 
	public String initForm(HttpServletRequest request, ModelMap model){
		logger.debug("CourseController initForm");
		
		User user = (User) request.getSession().getAttribute("user");
		String personID =  user.getPersonID();
		CourseRecord courseRecordPayment = (CourseRecord) request.getSession().getAttribute("courseRecordPayment");
		CourseRecordSearch courseRecordSearch = (CourseRecordSearch) request.getSession().getAttribute("courseRecordSearch");
		request.getSession().setAttribute("courseRecord", null);
		request.getSession().setAttribute("studentList", null);
		request.getSession().setAttribute("instructorList", null);
		request.getSession().setAttribute("courseRecordSearch", null);
		request.getSession().setAttribute("courseRecordPayment", null);
		
		// navigation from payment controller
		if(courseRecordPayment != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("courseRecord", courseRecordPayment);
	    	model.addAttribute("precreate", "No");
	    	request.getSession().setAttribute("studentList", courseRecordPayment);
	    	request.getSession().setAttribute("instructorList", courseRecordPayment);
	    	
			if (courseRecordPayment.getAction().equals("Payment")) {
				// If Back button is clicked on the payment page
				logger.debug("CourseController Payment");
				courseRecordPayment.setAction("Payment");
				return "/courseRecordStudentInfoSheet";
			} else if (courseRecordPayment.getAction().equals("Confirmation")) {
				// If Submit button is clicked on the payment page
				logger.debug("CourseController Confirmation");
				return "/courseRecordConfirmation";
			}
		}
		
		String action = null;
		String sheetNumber = null;
		
		boolean canChangeOrganization = true;
		
		if(courseRecordSearch != null){
			action = courseRecordSearch.getAction();
			sheetNumber = courseRecordSearch.getSheetNumber();
		}
		
		CourseRecord courseRecord = new CourseRecord();
		
		if(action != null && (action.equals("Edit") || action.equals("Copy"))){
			
    		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
    		if(!message.equals("")){
    			logger.debug("CourseController Error forcing SABA authentication");
    			return "redirect:/authentication/login.html";
    		}
    		
	  		courseRecord =  getSabaHelper().getCourseRecordDetails( getSabaHelper().getViewCourseRecordSheetNumberID(user.getPersonID(), sheetNumber, user.getFeeOption(), true));
       		courseRecord =  getSabaHelper().getCourseRecordInstructors(courseRecord);
			courseRecord.setSheetNumber(sheetNumber);
			model.addAttribute("sheetNumber", courseRecord.getSheetNumber());
			logger.debug("PersonID (" + user.getPersonID() + ") == ContactID (" +courseRecord.getContactID()  + ")");
			
			if(action.equals("Edit")){
				courseRecord.setAction("Edit");
				if(!user.getPersonID().equals(courseRecord.getContactID())){
					canChangeOrganization = false;
				}
			} else if(action.equals("Copy")){
				courseRecord.setAction("Create");
				courseRecord.setSheetNumberID(null);
				courseRecord.setSheetNumber(null);
				courseRecord.setCertificatesIssued(false);
				model.addAttribute("countyList", getSabaHelper().getCountyList(courseRecord.getState()));
			}
			
			courseRecord.setFeeOption(user.getFeeOption());

    		courseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(courseRecord.getCourseID()));
    		// Fee based Course Record Sheet
    	    if(courseRecord.getFeeOption().equals(user.FEE_BASED)) {
    	    	courseRecord.setCourseComponents(getSabaHelper().getCourseComponent(courseRecord.getCourseID()));
    	    	courseRecord =  getSabaHelper().getCourseRecordStudents(courseRecord);
    	    	request.getSession().setAttribute("studentList", courseRecord);
    	    }
    		
			request.getSession().setAttribute("instructorList", courseRecord);
		} else {
			courseRecord.setAction("Create");
		}
		
		courseRecord.setFeeOption(user.getFeeOption());
		
		//if(courseRecord == null)
		//	return "redirect:/authentication/login.html";

		model.addAttribute("username", user.getUsername());
		model.addAttribute("action", courseRecord.getAction());
		model.addAttribute("feeOption", courseRecord.getFeeOption());
		model.addAttribute("canChangeOrganization", canChangeOrganization);
		
		model.addAttribute("organizationList", populateOrganizationList(personID, courseRecord));
		
		if(courseRecord.getFeeOption().equals(user.FACILITY_FEE_BASED)) {
    		// set date to today
            Date date = new Date();
            String modifiedDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
            courseRecord.setEndDate(modifiedDate);
		}
				
		logger.debug("CourseController Initialize CourseRecordList");
		model.addAttribute("courseRecordList", new CourseRecordList());

		
		model.addAttribute("courseRecord", courseRecord);
		
    	if(request.getRequestURI().contains("preCreateCourseRecordSheet")) {
    		model.addAttribute("precreate", "Yes");
    		courseRecord.setPreCreate(true);
    	} else {
    		model.addAttribute("precreate", "No");
    		courseRecord.setPreCreate(false);
    	}
    	
    	// Fee based Course Record Sheet
	    if(courseRecord.getFeeOption().equals(user.FEE_BASED)) {
	    	// if certificates were issued, go directly to payment page
	    	if(courseRecord.isCertificatesIssued()) {
	    		// remove from model so they don't show up in url after redirect
	    		model.remove("action");
	    		model.remove("sheetNumber");
	    		model.remove("countyList");
	    		model.remove("precreate");
	    		model.remove("canChangeOrganization");
	    		model.remove("feeOption");
		    	model.remove("username");
	    		
	    		courseRecord.setAction("Confirmation");
	        	request.getSession().setAttribute("courseRecord", courseRecord);
	        	return "redirect:/payment/courseRecordPaymentDetails.html";
			}
	    }
    	
    	return "courseRecordSheet";
	}
	
	/**
	 * 
	 * @param request
	 * @param model
	 * @param courseRecordList
	 * @param courseRecord
	 * @param result
	 * @param status
	 * @return Model View
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String processSubmit(HttpServletRequest request, ModelMap model, @ModelAttribute("courseRecordList") CourseRecordList courseRecordList, @ModelAttribute("courseRecord") CourseRecord courseRecord, BindingResult result, SessionStatus status) {
 
		logger.debug("******** CourseController processSubmit");
		User user = (User) request.getSession().getAttribute("user");
		String username = user.getUsername();
		String personID =  user.getPersonID();
		model.addAttribute("username", username);
		boolean precreate = false;

		boolean canChangeOrganization = true;
		
	   	logger.debug("******** Action: "+courseRecord.getAction());
	   	
    	if(courseRecord.getAction().equals("Completed"))
    		return "redirect:/search/courseRecordSearch.html";
    	
    	if(request.getRequestURI().contains("preCreateCourseRecordSheet")){
    		precreate = true;
    		courseRecord.setStudentDetails(true);
    		model.addAttribute("precreate", "Yes");
    		courseRecord.setPreCreate(true);
    	}else{
    		model.addAttribute("precreate", "No");
    		courseRecord.setPreCreate(false);
    	}
    	
    	model.addAttribute("organizationList", populateOrganizationList(personID, courseRecord));
    	
    	if(courseRecord.getContactID()!=null && !user.getPersonID().equals(courseRecord.getContactID())){
			canChangeOrganization = false;
		}
   		model.addAttribute("canChangeOrganization", canChangeOrganization);
	   	
	   	if(courseRecord.getAction().equals("Restart")){
	   		courseRecord.setAction("Edit");
	   		logger.debug("******** Return back to CRE page");
	   		return "courseRecordSheet"; 
	   	}
	   	
	   	logger.debug("Username: "+ username);
    	logger.debug("Fee Option: "+courseRecord.getFeeOption());
	    logger.debug("Sheet Number: " + courseRecord.getSheetNumber());
    	logger.debug("OrganizationName: " + courseRecord.getOrganizationID());
    	logger.debug("Course Code: " + courseRecord.getCourseID());
    	logger.debug("City: " + courseRecord.getCity());
    	logger.debug("State: " + courseRecord.getState());
    	logger.debug("Show Student Details: " + courseRecord.isStudentDetails());
	
    	if(((CourseRecord) request.getSession().getAttribute("instructorList")) != null && ((CourseRecord) request.getSession().getAttribute("instructorList")).getIsInstructor()){
    		courseRecord.setInstructors(((CourseRecord) request.getSession().getAttribute("instructorList")).getInstructors());
    		logger.debug("Setting Instructor List size : " + courseRecord.getInstructors().size());
    	} else {
    		courseRecord.setInstructors(new ArrayList<People>());
    		logger.debug("Initializing Instructor List size : " + courseRecord.getInstructors().size());
    	}
    	
    	if(courseRecord.getFeeOption().equals(user.FEE_BASED) && !courseRecord.isStudentDetails()){
        	if(((CourseRecord) request.getSession().getAttribute("studentList")) != null && ((CourseRecord) request.getSession().getAttribute("studentList")).getIsStudents()){
        		courseRecord.setStudents(((CourseRecord) request.getSession().getAttribute("studentList")).getStudents());
        		logger.debug("Setting Student List size : " + courseRecord.getStudents().size());
        	} else {
        		courseRecord.setStudents(new ArrayList<Student>());
        		logger.debug("Initializing Student List size : " + courseRecord.getStudents().size());
        	}    		
    	}

		logger.debug("CourseController CourseRecordList size " + courseRecordList.getCourseRecordListCount());
		
		// Facility-fee based Course Record Sheet
        if(courseRecord.getFeeOption().equals(user.FACILITY_FEE_BASED)){
            logger.debug("******** FACILITY-FEE BASED THREAD");
            if(courseRecord.getAction().equals("Create") || courseRecord.getAction().equals("Edit")){
                logger.debug("******** ACTION CREATE OR EDIT");
                courseValidator.validate(courseRecord, result);
                                
                if (result.hasErrors()) {
                    logger.debug("CourseController validation has errors");
                    model.addAttribute("countyList", getSabaHelper().getCountyList(courseRecord.getState()));
                    return "/courseRecordSheet";
                } else {
                    
                    logger.debug("CourseController validation was successful");
                    if(courseRecord.getAction().equals("Create")) {
                    	courseRecord.setContactID(personID);
                    	courseRecord.setOrganizationName(getSabaHelper().getOrganizationName(personID, courseRecord.getOrganizationID()));
                    }
                    courseRecord.setCourseName(getSabaHelper().getCourseName(courseRecord.getCourseID(), courseRecord.getFeeOption()));
                    courseRecord.setStudentDetails(false);
                    courseRecord.setTotalStudents("1");
                    
                    logger.debug("CourseController saving course record sheet");
                                            
                    String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
                    if(!message.equals("")){
                        logger.debug("CourseController Error forcing SABA authentication");
                        return "redirect:/authentication/login.html";
                    }
                    
                    if (courseRecord.getAction().equals("Edit")) {
                        logger.debug("Course Controller Updating Course Record " + courseRecord.getCourseID());
                        
                        String sabaResponse = getSabaHelper().updateSabaFacilityFeeBasedCourseRecordSheet(courseRecord);
                        
                        if(sabaResponse.contains("Error")){
                            //courseRecord.setAction(sabaResponse);
                            result.rejectValue("sabaMessage", null, null, "Error Saving Course Record Entry. Please contact your administrator");
                            return "/courseRecordSheet";
                        } else {
                            logger.debug("CourseController Update Course Record " + courseRecord.getSheetNumber() + " : " + sabaResponse);
                            courseRecord = getSabaHelper().getCourseRecordDetails( courseRecord.getSheetNumberID() );
                        }
                    } else {
                        logger.debug("CourseController Saving Course Record " + courseRecord.getCourseID());
                        String sabaResponse = getSabaHelper().createSabaFacilityFeeBasedCourseRecordSheet(courseRecord);
                        
                        if(sabaResponse.contains("Error")){
                            //courseRecord.setAction(sabaResponse);
                            result.rejectValue("sabaMessage", null, null, "Error Creating Course Record Entry. Please contact your administrator");
                            return "redirect:/error/showError.html";
                        } else {
                            logger.debug("CourseController Saved Course Record " + sabaResponse);
                            courseRecord.setSheetNumberID(sabaResponse);
                            courseRecord = getSabaHelper().getCourseRecordDetails( courseRecord.getSheetNumberID() );
                        }
                    }
                    
                    //System.out.println("course POST set user fee option: " + user.getFeeOption());
                    courseRecord.setFeeOption(user.getFeeOption());
                    courseRecord.setAction("Confirmation");
                    
                    // remove unwanted data from model before redirect
                    model.remove("countyList");
                    model.remove("canChangeOrganization");
                    model.remove("precreate");
                    model.remove("username");
                    
                    // add course record to model and session
                    model.addAttribute("courseRecord", courseRecord);
                    request.getSession().setAttribute("courseRecord", courseRecord);
                    
                    return "redirect:/payment/courseRecordPaymentDetails.html";
                }
            } else if (courseRecord.getAction().equals("Confirmation")){
                logger.debug("******** ACTION CONFIRMATION");
                courseRecord.setFeeOption(user.getFeeOption());
                courseRecord.setAction("Print");
                return "/courseRecordConfirmation";
            } else if (courseRecord.getAction().equals("Print")){
                logger.debug("******** ACTION PRINT");
                
                CourseRecord paymentCourseRecord = new CourseRecord();

                // Reload Course Record with Payment Info
                logger.debug("******** RELOAD CRE TO PICKUP PAYMENT INFO");
                
                String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
                if(!message.equals("")){
                    logger.debug("CourseController Error forcing SABA authentication");
                    return "redirect:/authentication/login.html";
                }
                
                paymentCourseRecord.setSheetNumberID(courseRecord.getSheetNumberID());
                paymentCourseRecord =  getSabaHelper().getCourseRecordDetails( paymentCourseRecord.getSheetNumberID() );
                paymentCourseRecord.setFeeOption(user.getFeeOption());
                
                if (paymentCourseRecord.getPayment().getPaymentTypeID()==null || (paymentCourseRecord.getPayment().getPaymentTypeID()!=null && paymentCourseRecord.getPayment().getPaymentTypeID().equals(""))) {
                    paymentCourseRecord.getPayment().setPaymentTypeID("NP");
                    paymentCourseRecord.getPayment().setTotalPrice(courseRecord.getPayment().getTotalPrice());  // pull it from session before overwriting, should be 0.00
                }
                
                // the price is lost if payType is PL because findCRS does not return it
                if (paymentCourseRecord.getPayment().getPaymentTypeID()!=null && paymentCourseRecord.getPayment().getPaymentTypeID().equals("PL"))
                    paymentCourseRecord.getPayment().setTotalPrice(courseRecord.getPayment().getTotalPrice());  // pull it from session before overwriting
                
                courseRecord.setAction("Completed");
                
                model.addAttribute("courseRecord", paymentCourseRecord);
                request.getSession().setAttribute("courseRecord", paymentCourseRecord);

                return "/courseRecordPrintSummary";
            }
            
        } else if (courseRecord.getFeeOption().equals(user.NON_FEE_BASED)) {  // Non-fee based Course Record Sheet
	    	logger.debug("******** NON-FEE BASED THREAD");
	    	if(courseRecord.getAction().equals("Create") || courseRecord.getAction().equals("More")){
	    		logger.debug("******** ACTION CREATE OR MORE");
	    		courseValidator.validate(courseRecord, result);
	    			    		
	    		if (result.hasErrors()) {
	    			logger.debug("CourseController validation has errors");
	    			model.addAttribute("countyList", getSabaHelper().getCountyList(courseRecord.getState()));
	    			courseRecord.setAction("Create");
	    			return "/courseRecordSheet";
	    		} else {
	    			
	    			logger.debug("CourseController validation was successful");
	    			courseRecord.setContactID(personID);
		    		courseRecord.setCourseName(getSabaHelper().getCourseName(courseRecord.getCourseID(), courseRecord.getFeeOption()));
		    		courseRecord.setOrganizationName(getSabaHelper().getOrganizationName(personID, courseRecord.getOrganizationID()));
	    			
	    			if(courseRecord.getAction().equals("More")){
	    				//TODO: Save multiple Course Record Sheets
		    			courseRecord.setAction("Confirmation");
		    			courseRecordList.addCourseRecordList(courseRecord);
		    			courseRecord = clone(courseRecord);
			    		logger.debug("CourseController Adding to CourseRecordList size " + courseRecordList.getCourseRecordListCount());
	    				
			    		request.getSession().setAttribute("instructorList", courseRecord);
			    		model.addAttribute("courseRecord", courseRecord);
			    		model.addAttribute("courseRecordList", courseRecordList);
	    				
	    				return "/courseRecordSheet";
	    			} else {
		    			courseRecord.setAction("Confirmation");
		    			courseRecordList.addCourseRecordList(courseRecord);
		    			
			    		logger.debug("CourseController saving course record sheet(s)");
			    					    		
			    		ArrayList<CourseRecord> courseRecordCollection = new ArrayList<CourseRecord>();
			    		logger.debug("CourseController CourseRecordList size " + courseRecordList.getCourseRecordListCount());
			    		
			    		for (CourseRecord  entry : courseRecordList.getCourseRecordList())
			    		{
			        		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
			        		if(!message.equals("")){
			        			logger.debug("CourseController Error forcing SABA authentication");
			        			return "redirect:/authentication/login.html";
			        		}
			        		
			        		
			        		if(entry.getSheetNumber() == null || entry.getSheetNumber().equals("")){
				        		logger.debug("CourseController Saving Course Record " + entry.getCourseID());
				        		String sabaResponse = getSabaHelper().createSabaCourseRecordSheet(entry);
				        		
				        		if(sabaResponse.contains("Error")){
				        			courseRecord.setAction(sabaResponse);
				        			result.rejectValue("sabaMessage", null, null, "Error Saving Course Record Entry. Please contact your administrator");
				        			return "/courseRecordSheet";
				        		} else {
				        			logger.debug("CourseController Saved Course Record " + sabaResponse);
				        			entry.setSheetNumber(sabaResponse);
				        		}
			        		}
			        		courseRecordCollection.add(entry);
			    		}
			    		
			    		ArrayList<CourseRecord> sortedCourseRecordList = getSabaHelper().sortCourseRecordList(courseRecordCollection);
			    		
			    		CourseRecordList printCourseRecordList = new CourseRecordList();
			    		printCourseRecordList.setCourseRecordList(sortedCourseRecordList);
			    		
			    		model.addAttribute("courseRecordList", printCourseRecordList);

		    			return "/courseRecordConfirmation";
	    			}
	    		}
	    	} else if(courseRecord.getAction().equals("Confirmation") || courseRecord.getAction().equals("Print")){
	    		logger.debug("******** ACTION CONFIRMATION OR PRINT");
	    		logger.debug("CourseController Non-fee thread complete");
	    		courseRecord.setAction("Completed");	
	    		status.setComplete();
	    		
	    		if(courseRecordList != null && courseRecordList.getCourseRecordListCount() > 0)
	    			return "/courseRecordPrintSummary";

	    		return "redirect:/search/courseRecordSearch.html";
	    	} 
	    	
	    	
	    } else {
		    // Fee based Course Record Sheet
	    	logger.debug("******** FEE BASED THREAD");
		    if((courseRecord.getAction().equals("Create") || courseRecord.getAction().equals("Edit")) && !courseRecord.isStudentDetails()){
		    	logger.debug("******** ACTION CREATE OR EDIT, REQUIRE STUDENT DETAILS");
	    		courseValidator.validate(courseRecord, result);
	    		
	    		if (!result.hasErrors()) {
		    		if(courseRecord.getAction().equals("Create")){
						courseRecord.setContactID(personID);
			    		courseRecord.setCourseName(getSabaHelper().getCourseName(courseRecord.getCourseID(), courseRecord.getFeeOption()));
			    		courseRecord.setOrganizationName(getSabaHelper().getOrganizationName(personID, courseRecord.getOrganizationID()));
		    		}
		    		
		    		if(courseRecord.getCourseID() != null && !courseRecord.getCourseID().equals("")){
			    		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
			    		if(!message.equals("")){
			    			logger.debug("CourseController Error forcing SABA authentication");
			    			return "redirect:/authentication/login.html";
			    		}
			    		courseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(courseRecord.getCourseID()));
		    			courseRecord.setCourseComponents(getSabaHelper().getCourseComponent(courseRecord.getCourseID()));
		    		}
		    		
		            //Validate if Course requires Student Details
		            logger.debug("Validating courseRecord isStudentDetails: "
		                    + courseRecord.isStudentDetails());
		   
		            logger.debug("Course Requires Student Details "+ courseRecord.isCourseRequiresStudentDetails());
		            if(courseRecord.isCourseRequiresStudentDetails() && courseRecord.isStudentDetails()){
		            	
		                if(!result.hasErrors())
		                	result.rejectValue("sabaMessage", null, null, "Please correct the form errors below to submit course record entry");
	
	        			result.rejectValue("studentDetails", "error.courseRecord.invalid-student-details",
		                        null, "Course Requires Student Details");
		            }
		            
		            //Set totals for student to null always because student details are required.
		            //if Changing skip student on copy this will all course record sheet to update
        			courseRecord.setTotalSuccessful(null);
        			courseRecord.setTotalUnsuccessful(null);
        			courseRecord.setTotalNotEvaluated(null);
	    		}
		            
	    		if (result.hasErrors()) {
	    			logger.debug("CourseController validation has errors");
	    			model.addAttribute("countyList", getSabaHelper().getCountyList(courseRecord.getState()));
	    			model.addAttribute("courseRecord", courseRecord);
	    			return "/courseRecordSheet";
	    		} else {
	    			logger.debug("CourseController validation was successful");
	    			//Create or Update Course Record Sheet depending on action
	    			logger.debug("CourseController saving course record sheet");
		    		
	        		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
	        		if(!message.equals("")){
	        			logger.debug("CourseController Error forcing SABA authentication");
	        			return "redirect:/authentication/login.html";
	        		}
	        		
	    			if(courseRecord.getAction().equals("Edit")){
		        		logger.debug("CourseController Updating Course Record " + courseRecord.getCourseID());
		        		String sabaResponse = getSabaHelper().updateSabaFeeBasedCourseRecordSheet(courseRecord);
		        		
		        		if(sabaResponse.contains("Error")){
		        			courseRecord.setAction(sabaResponse);
		        			result.rejectValue("sabaMessage", null, null, "Error Saving Course Record Entry. Please contact your administrator");
		        			model.addAttribute("courseRecord", courseRecord);
		        			return "/courseRecordSheet";
		        		} else {
		        			logger.debug("CourseController Update Course Record " + courseRecord.getSheetNumber() + " : " + sabaResponse);
		        		}
		        		
		        		// Set student details
		        		logger.debug("CourseController Update Course Component");
		        		courseRecord.setFeeOption(user.getFeeOption());
		        		courseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(courseRecord.getCourseID()));
		        		// Fee based Course Record Sheet
		        	    if(courseRecord.getFeeOption().equals(user.FEE_BASED)) {
		        	    	courseRecord.setCourseComponents(getSabaHelper().getCourseComponent(courseRecord.getCourseID()));
		        	    	courseRecord =  getSabaHelper().getCourseRecordStudents(courseRecord);
		        	    	request.getSession().setAttribute("studentList", courseRecord);
		        	    }
	               		
	    			} else {
		        		logger.debug("CourseController Creating Course Record " + courseRecord.getCourseID());
		        		String sabaResponse = getSabaHelper().createSabaFeeBasedCourseRecordSheet(courseRecord);
		        		
		        		if(sabaResponse.contains("Error")){
		        			courseRecord.setAction("Create");
		        			result.rejectValue("sabaMessage", null, null, sabaResponse.substring(6));
		        			model.addAttribute("courseRecord", courseRecord);
		        			//return "redirect:/error/showError.html";
		        			return "/courseRecordSheet";
		        		} else {
		        			logger.debug("CourseController Saved Course Record " + sabaResponse);
		        			courseRecord.setSheetNumberID(sabaResponse);
		        	  		courseRecord =  getSabaHelper().getCourseRecordDetails( courseRecord.getSheetNumberID() );
		               		courseRecord =  getSabaHelper().getCourseRecordInstructors(courseRecord);
				    		courseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(courseRecord.getCourseID()));
			    			courseRecord.setCourseComponents(getSabaHelper().getCourseComponent(courseRecord.getCourseID()));
		               		courseRecord.setFeeOption(user.getFeeOption());
		        		}	    				
	    			}
	    			
	    			if(courseRecord.isCertificatesIssued()) {
	    				courseRecord.setAction("Payment");  // go straight to student review page
	    			} else {
	    				courseRecord.setAction("Summary");
	    			}
			    	model.addAttribute("courseRecord", courseRecord);
			    	request.getSession().setAttribute("studentList", courseRecord);
			    	return "/courseRecordStudentInfoSheet";
	    		}
	    		
		    } else if((courseRecord.getAction().equals("Create") || courseRecord.getAction().equals("Edit")) && courseRecord.isStudentDetails()){
		    	logger.debug("******** ACTION CREATE OR EDIT, NO STUDENT DETAILS");
		    		//Pass student validation for preCreate
			    	if(precreate){
			    		logger.debug("CourseController Pre Create Course Record");
			    		courseRecord.setStudentDetails(false);
			    		courseRecord.setTotalStudents("1");
			    	}
			    	
			    	courseValidator.validate(courseRecord, result);

		    		if (!result.hasErrors()) {
			    		if(courseRecord.getAction().equals("Create")){
							courseRecord.setContactID(personID);
				    		courseRecord.setCourseName(getSabaHelper().getCourseName(courseRecord.getCourseID(), courseRecord.getFeeOption()));
				    		courseRecord.setOrganizationName(getSabaHelper().getOrganizationName(personID, courseRecord.getOrganizationID()));
			    		}
		
		
			    		if(courseRecord.getCourseID() != null && !courseRecord.getCourseID().equals("")){
				    		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
				    		if(!message.equals("")){
				    			logger.debug("CourseController Error forcing SABA authentication");
				    			return "redirect:/authentication/login.html";
				    		}
				    		courseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(courseRecord.getCourseID()));
			    			courseRecord.setCourseComponents(getSabaHelper().getCourseComponent(courseRecord.getCourseID()));
			    		}
			    		
			            //Course requires Student Details
			            logger.debug("Validating courseRecord isStudentDetails: "
			                    + courseRecord.isStudentDetails());
			            
			            logger.debug("Course Requires Student Details "+ courseRecord.isCourseRequiresStudentDetails());
			            if(courseRecord.isCourseRequiresStudentDetails() && courseRecord.isStudentDetails()){
			            	
			                if(!result.hasErrors())
			                	result.rejectValue("sabaMessage", null, null, "Please correct the form errors below to submit course record entry");
		
		        			result.rejectValue("studentDetails", "error.courseRecord.invalid-student-details",
			                        null, "Course Requires Student Details");
		        			
		        			courseRecord.setTotalSuccessful(null);
		        			courseRecord.setTotalUnsuccessful(null);
		        			courseRecord.setTotalNotEvaluated(null);
			            }
		    		}
		            
		    		if (result.hasErrors()) {
		    			logger.debug("CourseController validation has errors");
		    			model.addAttribute("countyList", getSabaHelper().getCountyList(courseRecord.getState()));
		    			return "/courseRecordSheet";
		    		} else {
		    			logger.debug("CourseController validation was successful");
		     			//Create or Update Course Record Sheet depending on action
		    			logger.debug("CourseController saving course record sheet");
		    			
		        		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		        		if(!message.equals("")){
		        			logger.debug("CourseController Error forcing SABA authentication");
		        			return "redirect:/authentication/login.html";
		        		}
		    			
		        		
		    			if(courseRecord.getAction().equals("Edit")){
			        		logger.debug("Course Controller Updating Course Record " + courseRecord.getCourseID());
			        		String sabaResponse = getSabaHelper().updateSabaFeeBasedCourseRecordSheet(courseRecord);
			        		
			        		if(sabaResponse.contains("Error")){
			        			//courseRecord.setAction(sabaResponse);
			        			result.rejectValue("sabaMessage", null, null, "Error Saving Course Record Entry. Please contact your administrator");
			        			return "/courseRecordSheet";
			        		} else {
			        			logger.debug("CourseController Update Course Record " + courseRecord.getSheetNumber() + " : " + sabaResponse);
			        		}
		    			} else {
		    				if(precreate)
		    					logger.debug("Course Controller Pre Creating Course Record " + courseRecord.getCourseID());	
		    				else
			        			logger.debug("Course Controller Creating Course Record " + courseRecord.getCourseID());		        		
			        		String sabaResponse = getSabaHelper().createSabaFeeBasedCourseRecordSheet(courseRecord);
			        		
			        		if(sabaResponse.contains("Error")){
			        			courseRecord.setAction("Create");
			        			result.rejectValue("sabaMessage", null, null, sabaResponse.substring(6));
			        			//return "redirect:/error/showError.html";
			        			return "/courseRecordSheet";
			        		} else {
			        			logger.debug("CourseController Saved Course Record " + sabaResponse);
			        			courseRecord.setSheetNumberID(sabaResponse);
			        	  		courseRecord =  getSabaHelper().getCourseRecordDetails( courseRecord.getSheetNumberID() );
			               		courseRecord =  getSabaHelper().getCourseRecordInstructors(courseRecord);
					    		courseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(courseRecord.getCourseID()));
			               		courseRecord.setFeeOption(user.getFeeOption());
			        		}
		    			}
		    						    	
		    			logger.debug("CourseController Course Record Action set to " + courseRecord.getAction());
		    			
				    	courseRecord.setAction("Confirmation");
				    	
				    	if(precreate){
				    		model.addAttribute("precreate", "Yes");
				    		
				    		/* No longer sending pre Create Notification
			    			logger.debug("CourseController sending Pre create notification");
			    			
			    			String sabaResponse = getSabaHelper().courseRecordSheetPreCreateNotification(courseRecord.getSheetNumberID());

			        		if(sabaResponse.contains("Error")){
			        			courseRecord.setAction(sabaResponse);
			        			result.rejectValue("sabaMessage", null, null, "Error Sending Pre Create Notification. Please contact your administrator");
			        			model.addAttribute("courseRecord", courseRecord);
			        			return "/courseRecordSheet";
			        		} else {
			        			logger.debug("CourseController Sent Pre Create Notification " + courseRecord.getSheetNumber() + " : " + sabaResponse);
			        		}
			        		*/
				    		
				    	} else {
					    	model.remove("countyList");
					    	model.remove("precreate");
					    	model.remove("canChangeOrganization");
					    	model.remove("username");
				    	}
				    	
				    	model.addAttribute("courseRecord", courseRecord);
				    	request.getSession().setAttribute("courseRecord", courseRecord);

				    	if(precreate)
				    		return "/courseRecordConfirmation";
				    	else
				    		return "redirect:/payment/courseRecordPaymentDetails.html";
		    		}	    		
		    } else if (courseRecord.getAction().equals("Summary")){
		    	logger.debug("******** ACTION SUMMARY");
		    	studentValidator.validate(courseRecord, result);
		    				
	    		if (result.hasErrors()) {
			    	courseRecord.setAction("Summary");
			    	model.addAttribute("courseRecord", courseRecord);
			    	request.getSession().setAttribute("studentList", courseRecord);
			    	return "/courseRecordStudentInfoSheet";
	    		} else {
	    			
		    		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		    		if(!message.equals("")){
		    			logger.debug("CourseController Error forcing SABA authentication");
		    			return "redirect:/authentication/login.html";
		    		}
		    		
		    		// add components
		    		courseRecord.setCourseComponents(getSabaHelper().getCourseComponent(courseRecord.getCourseID()));

		    		List<Student> processedStudents = new ArrayList<Student>();
		    		
		    		for(int s=0; s < courseRecord.getStudents().size(); s++){
		    			String studentID = null;
		    			Student student = courseRecord.getStudents().get(s);
		    			if(courseRecord.getStudents().get(s).getId() == null){
		    				if(student.getFirstName() != null && student.getLastName() != null)
		    					studentID = getSabaHelper().addCourseRecordStudents(courseRecord.getSheetNumberID(), student);
		    			} else {
			 				logger.debug("CourseController update Student : " + courseRecord.getSheetNumberID() + " Student " + student.getId());
			 				studentID = getSabaHelper().updateCourseRecordStudents(courseRecord.getSheetNumberID(), student);
		    			}
		    			
		    			if(studentID != null && !studentID.equals("Error")){
		    				courseRecord.getStudents().get(s).setId(studentID);
		    				 processedStudents.add(courseRecord.getStudents().get(s));
		    			} else {
		    				if(studentID != null && studentID.equals("Error"))
		    					logger.error("Error processing Student (" + student.getId() + ") "+ student.getFirstName() + " " + student.getLastName());
		    			}
		    		}
		    		
					int totalStudents = 0;
					try {
		            	totalStudents = Integer.parseInt(courseRecord.getTotalStudents());
		            } catch(NumberFormatException e) {
		               logger.error("Error parsing Total Students count " + e.getMessage());
		            }
		    		
					logger.debug("CourseController totalStudents="+totalStudents);
		    		courseRecord.setStudents(processedStudents);
		    		int currentStudentListCount = courseRecord.getStudents().size();
		    		logger.debug("Total Students [" + totalStudents + "] = [" + currentStudentListCount + "] Student List Size");
		    		if(totalStudents != currentStudentListCount){
		    			logger.debug("CourseController setTotalStudents="+currentStudentListCount);
		    			courseRecord.setTotalStudents(""+currentStudentListCount);
		    			
		        		message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		        		if(!message.equals("")){
		        			logger.debug("CourseController Error forcing SABA authentication");
		        			return "redirect:/authentication/login.html";
		        		}		    			
		        		logger.debug("CourseController Updating Course Record " + courseRecord.getCourseID());
		        		String sabaResponse = getSabaHelper().updateSabaFeeBasedCourseRecordSheet(courseRecord);
		        		
		        		if(sabaResponse.contains("Error")){
		        			courseRecord.setAction(sabaResponse);
		        			result.rejectValue("sabaMessage", null, null, "Error Saving Course Record Entry. Please contact your administrator");
		        			model.addAttribute("courseRecord", courseRecord);
		        			return "/courseRecordSheet";
		        		} else {
		        			logger.debug("CourseController Update Course Record " + courseRecord.getSheetNumber() + " : " + sabaResponse);
		        		}
		    		}
	
			    	courseRecord.setAction("Payment");
			    	model.addAttribute("courseRecord", courseRecord);
			    	request.getSession().setAttribute("studentList", courseRecord);	
			    	return "/courseRecordStudentInfoSheet";
	    		}
		    } else if (courseRecord.getAction().equals("Payment")){
		    	logger.debug("******** ACTION PAYMENT");
		    	model.remove("countyList");
		    	model.remove("canChangeOrganization");
		    	model.remove("precreate");
		    	model.remove("username");
		    	

	    		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
	    		if(!message.equals("")){
	    			logger.debug("CourseController Error forcing SABA authentication");
	    			return "redirect:/authentication/login.html";
	    		}

	    		logger.debug("CourseController ARE CERTS ISSUED?");
	    		if(!courseRecord.isCertificatesIssued()) {
	    			//Issue Student Certificates
	    			logger.debug("CourseController NO.... ISSUE CERTS!");
	    			String sabaResponse = getSabaHelper().issueStudentCertificates(courseRecord.getSheetNumberID());

	        		if(!sabaResponse.equals("Successful")){
	        			courseRecord.setAction("Payment");
	        			result.rejectValue("errorMessage", null, null, sabaResponse);
	        			model.addAttribute("courseRecord", courseRecord);
	        			return "/courseRecordStudentInfoSheet";
	        		} else {
	        			logger.debug("CourseController Issued Student Certificates for course record " + courseRecord.getSheetNumber() + " : " + sabaResponse);
	        		}
        		
	    		}
		    	
		    	courseRecord.setAction("Confirmation");
		    	//return "/courseRecordPaymentDetails";
		    	request.getSession().setAttribute("courseRecord", courseRecord);
		    	return "redirect:/payment/courseRecordPaymentDetails.html";
		    } else if (courseRecord.getAction().equals("Confirmation")){
		    	logger.debug("******** ACTION CONFIRMATION");
		    	courseRecord.setAction("Print");
		    	return "/courseRecordConfirmation";
		    } else if (courseRecord.getAction().equals("Print")){
		    	logger.debug("******** ACTION PRINT");
		    	
		    	CourseRecord paymentCourseRecord = new CourseRecord();

		    		// Reload Course Record with Payment Info
		    		logger.debug("******** RELOAD CRE TO PICKUP PAYMENT INFO");
		    		
		    		String message = forceSabaAuthentication(user.getUsername(), user.getPassword());
		    		if(!message.equals("")){
		    			logger.debug("CourseController Error forcing SABA authentication");
		    			return "redirect:/authentication/login.html";
		    		}
		    		
		    		paymentCourseRecord.setSheetNumberID(courseRecord.getSheetNumberID());
		    		paymentCourseRecord =  getSabaHelper().getCourseRecordDetails( paymentCourseRecord.getSheetNumberID() );
		    		paymentCourseRecord =  getSabaHelper().getCourseRecordInstructors(paymentCourseRecord);
		    		paymentCourseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(paymentCourseRecord.getCourseID()));
		    		paymentCourseRecord.setCourseComponents(getSabaHelper().getCourseComponent(paymentCourseRecord.getCourseID()));
		    		paymentCourseRecord.setFeeOption(user.getFeeOption());
		    		
		    		if (paymentCourseRecord.getPayment().getPaymentTypeID()==null || (paymentCourseRecord.getPayment().getPaymentTypeID()!=null && paymentCourseRecord.getPayment().getPaymentTypeID().equals(""))) {
		    			paymentCourseRecord.getPayment().setPaymentTypeID("NP");
		    			paymentCourseRecord.getPayment().setTotalPrice(courseRecord.getPayment().getTotalPrice());  // pull it from session before overwriting, should be 0.00
		    		}
		    		
		    		// the price is lost if payType is PL because findCRS does not return it
		    		if (paymentCourseRecord.getPayment().getPaymentTypeID()!=null && paymentCourseRecord.getPayment().getPaymentTypeID().equals("PL"))
		    			paymentCourseRecord.getPayment().setTotalPrice(courseRecord.getPayment().getTotalPrice());  // pull it from session before overwriting
		    		
		    		// Don't forget to reload the instructor and student lists
		    		if(((CourseRecord) request.getSession().getAttribute("instructorList")) != null && ((CourseRecord) request.getSession().getAttribute("instructorList")).getIsInstructor()){
		    			paymentCourseRecord.setInstructors(((CourseRecord) request.getSession().getAttribute("instructorList")).getInstructors());
		        		logger.debug("Setting Instructor List size : " + paymentCourseRecord.getInstructors().size());
		        	} else {
		        		courseRecord.setInstructors(new ArrayList<People>());
		        		logger.debug("Initializing Instructor List size : " + paymentCourseRecord.getInstructors().size());
		        	}
		        	
		        	if(paymentCourseRecord.getFeeOption().equals(user.FEE_BASED) && !paymentCourseRecord.isStudentDetails()){
		            	if(((CourseRecord) request.getSession().getAttribute("studentList")) != null && ((CourseRecord) request.getSession().getAttribute("studentList")).getIsStudents()){
		            		paymentCourseRecord.setStudents(((CourseRecord) request.getSession().getAttribute("studentList")).getStudents());
		            		logger.debug("Setting Student List size : " + paymentCourseRecord.getStudents().size());
		            	} else {
		            		courseRecord.setStudents(new ArrayList<Student>());
		            		logger.debug("Initializing Student List size : " + paymentCourseRecord.getStudents().size());
		            	}    		
		        	}
		    	
		    	courseRecord.setAction("Completed");
		    	
		    	model.addAttribute("courseRecord", paymentCourseRecord);
		    	request.getSession().setAttribute("courseRecord", paymentCourseRecord);

		    	return "/courseRecordPrintSummary";
		    }
		    	
	    }
	    
	    return "/courseRecordSheet";
	}
	
	public List<DropDown> populateOrganizationList(String personID, CourseRecord courseRecord) {
		//System.out.println("populateOrganizationList()");
		//System.out.println("populateOrganizationList() unitCode="+courseRecord.getUnitCode());
		
		if (courseRecord != null && courseRecord.getUnitCode() != null && !courseRecord.getUnitCode().equals("")) {
			return getSabaHelper().getOrganizationListWithUnitCode(personID, courseRecord.getUnitCode());
		} else {
			return getSabaHelper().getOrganizationList(personID);
		}
	}
	
	/**
	 * 
	 * @param request
	 * @return List<DropDown>
	 */
	@ModelAttribute("courseCodeList")
	public List<DropDown> populateCourseList(@ModelAttribute("courseRecord") CourseRecord courseRecord) {

		if (courseRecord.getFeeOption().equals("Fee"))
			return getSabaHelper().getFeeBasedCourseCodeList(courseRecord.getCourseCategory(), courseRecord.getCourseName());
		else if (courseRecord.getFeeOption().equals("Facility-fee"))
		    return getSabaHelper().getFacilityBasedCourseCodeList();
		
		return getSabaHelper().getCourseCodeList(courseRecord.getCourseCategory());
	}
	
	/**
	 * 
	 * @param request
	 * @return List<DropDown>
	 */
	@ModelAttribute("aquaticsCourseCodeList")
	public List<DropDown> populateAquaticsCourseList() {

		return getSabaHelper().getAquaticsCourseCodeList();
	}

	/**
	 * 
	 * @param request
	 * @return List<DropDown>
	 */
	@ModelAttribute("stateList")
	public List<DropDown> populateStateList() {
		
		return getSabaHelper().getStateList();
	}
	
	/**
	 * 
	 * @param request
	 * @return List<String>
	 */
	@ModelAttribute("countyList")
	public List<String> populateCountyList(@ModelAttribute("courseRecord") CourseRecord courseRecord) {
		
		return getSabaHelper().getCountyList(courseRecord.getState());
	}
	

	@ModelAttribute("courseCategoryList")
	public List<DropDown> populateCourseCategoryList() {
	
		return getSabaHelper().getCourseCategoryList();
	}
	
	@ModelAttribute("paymentTypeList")
	public List<DropDown> populatePaymentTypeList(@ModelAttribute("courseRecord") CourseRecord courseRecord) {
	    //System.out.println("course populatePaymentTypeList() courseRecord: " + courseRecord);
	    //System.out.println("course populatePaymentTypeList() fee option: " + courseRecord.getFeeOption());
		return getSabaHelper().getPaymentTypeList(courseRecord.getFeeOption());
	}
	
	@ModelAttribute("purchaseOrderList")
	public List<DropDown> populatePurchaseOrderList() {
		
		ArrayList<DropDown> purchaseOrderList = new ArrayList<DropDown>();
		
		// Add this option to empty list
		DropDown dd = new DropDown();
	    dd.setKey("");
	    dd.setValue("Select Purchase Order");
	    purchaseOrderList.add(0, dd);
	    
		return purchaseOrderList;
	}
	
	/**
	 * 
	 * @param courseRecord
	 * @return courseRecord New Course Record
	 */
	private CourseRecord clone(CourseRecord courseRecord){
		CourseRecord newCourseRecord = new CourseRecord();
		
		newCourseRecord.setAction("Create");
		newCourseRecord.setAquaticCourseDisplay(courseRecord.isAquaticCourseDisplay());
		newCourseRecord.setFeeOption(courseRecord.getFeeOption());
		newCourseRecord.setContactID(courseRecord.getContactID());
		newCourseRecord.setOrganizationID(courseRecord.getOrganizationID());
		newCourseRecord.setOrganizationName(courseRecord.getOrganizationName());
		newCourseRecord.setCourseID(null);
		newCourseRecord.setCourseName(null);
		newCourseRecord.setTrainingCenterName(courseRecord.getTrainingCenterName());
		newCourseRecord.setStreetAddress(courseRecord.getStreetAddress());
		newCourseRecord.setCity(courseRecord.getCity());
		newCourseRecord.setState(courseRecord.getState());
		newCourseRecord.setZipCode(courseRecord.getZipCode());
		newCourseRecord.setCounty(courseRecord.getCounty());
		newCourseRecord.setTotalStudents(null);
		newCourseRecord.setTotalSuccessful(null);
		newCourseRecord.setTotalUnsuccessful(null);
		newCourseRecord.setTotalNotEvaluated(null);
		newCourseRecord.setEndDate(courseRecord.getEndDate());	
		
		ArrayList<People> newInstructorList = new ArrayList<People>();
		
        for (People instructor : courseRecord.getInstructors())
        {
        	People newInstructor = new People();
        	newInstructor.setId(instructor.getId());
        	newInstructor.setUserName(instructor.getUserName());
        	newInstructor.setFirstName(instructor.getFirstName());
        	newInstructor.setLastName(instructor.getLastName());
        	newInstructorList.add(newInstructor);
        }
		newCourseRecord.setInstructors(newInstructorList);
		newCourseRecord.setStatistics(courseRecord.getStatistics());
		
		return newCourseRecord;
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
		logger.debug("CourseController SABA isLogin is " + getSabaHelper().islogin());
		logger.debug("CourseController forcing SABA authentication");
		
		return getSabaHelper().login(username, password);
	}
	
	/**
	 * 
	 * @return SabaHelper
	 */
	private SabaHelper getSabaHelper(){
		
		return new SabaHelper();
	}
	
}
