package com.arc.instructor.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

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

import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.People;
import com.arc.instructor.model.Student;
import com.arc.instructor.model.User;
import com.arc.instructor.model.CourseComponent;
import com.arc.instructor.utils.SabaHelper;

import com.sun.xml.bind.v2.schemagen.xmlschema.List;


@Controller
@RequestMapping(value={"/courseRecordEntry"})
public class ViewController {

		private final Log logger = LogFactory.getLog(getClass());
		
		private List list;
		
	    public ViewController() {
	    	logger.debug("ViewController Constructor ");				
	    }
	    
	    @RequestMapping(method=RequestMethod.GET)
		public void getCourseRecordEntry(HttpServletRequest request, HttpServletResponse response, @RequestParam("sheetNumber") String sheetNumber, ModelMap model ){
	    	logger.debug("ViewController getCourseRecordEntry");
			logger.debug("ViewController Sheet Number: "+ sheetNumber);

			User user = ((User) request.getSession().getAttribute("user"));
			
			logger.debug("CourseController SABA isLogin is " + getSabaHelper().islogin());
			logger.debug("CourseController forcing SABA authentication");
			
			String message = getSabaHelper().login(user.getUsername(), user.getPassword());
			
    		if(!message.equals("")){
    			logger.error("CourseController Error forcing SABA authentication");
    		}

			
       		CourseRecord courseRecord =  getSabaHelper().getCourseRecordDetails( getSabaHelper().getViewCourseRecordSheetNumberID(user.getPersonID(), sheetNumber, user.getFeeOption(), true));
       		courseRecord.setCourseRequiresStudentDetails(getSabaHelper().isCourseRequiresStudentDetails(courseRecord.getCourseID()));
       		courseRecord.setCourseComponents(getSabaHelper().getCourseComponent(courseRecord.getCourseID()));
       		courseRecord.setFeeOption(user.getFeeOption());
       		courseRecord =  getSabaHelper().getCourseRecordInstructors(courseRecord);
       		courseRecord =  getSabaHelper().getCourseRecordStudents(courseRecord);
       		
    		JSONObject instructorJsonObj;
			ArrayList <JSONObject> instructorList = new ArrayList<JSONObject>();
			JSONObject studentJsonObj;
			ArrayList <JSONObject> studentList = new ArrayList<JSONObject>();

			logger.debug("isInstructor : " + courseRecord.getIsInstructor());
			
			if(courseRecord.getIsInstructor()){
				logger.debug("Course Record Instructor Roster Size: " + courseRecord.getInstructors().size());

				for (People instructor : courseRecord.getInstructors())
		        {
					instructorJsonObj = new JSONObject();
					
					logger.debug("Course Record Instructor: " + instructor.getFirstName() + " " + instructor.getLastName());
					instructorJsonObj.accumulate("id", instructor.getId());
					instructorJsonObj.accumulate("userName", instructor.getUserName());
			    	instructorJsonObj.accumulate("firstName", instructor.getFirstName());
					instructorJsonObj.accumulate("lastName", instructor.getLastName());					
					instructorList.add(instructorJsonObj);
		        }
					
			}
			
			if (courseRecord.getIsStudents()) {
				logger.debug("Course Record Student Roster Size: " + courseRecord.getStudents().size());

				for (Student student : courseRecord.getStudents())
		        {
					studentJsonObj = new JSONObject();
					logger.debug("Course Record Student: " + student.getFirstName() + " " + student.getLastName());
					studentJsonObj.accumulate("id", student.getId());
					studentJsonObj.accumulate("userName", student.getUserName());
					studentJsonObj.accumulate("firstName", student.getFirstName());
					studentJsonObj.accumulate("lastName", student.getLastName());
					studentJsonObj.accumulate("email", (student.getEmail()!=null?student.getEmail():""));
					studentJsonObj.accumulate("phoneNumber", (student.getPhoneNumber()!=null?student.getPhoneNumber():""));
					studentJsonObj.accumulate("addlInfo", (student.getAddlInfo()!=null?student.getAddlInfo():""));
					
					ArrayList <JSONObject> courseComponentList = new ArrayList<JSONObject>();
					for(CourseComponent comp : student.getCourseComponents())
					{
						JSONObject courseComponentJsonObj = new JSONObject();
						courseComponentJsonObj.accumulate("courseComponentLabel", comp.getCourseComponentLabel());
						courseComponentJsonObj.accumulate("courseComponentValue", comp.getCourseComponentValue());
						courseComponentList.add(courseComponentJsonObj);
					}
					studentJsonObj.accumulate("courseComponents", courseComponentList);
					
					studentList.add(studentJsonObj);
		        }
			}
    		
			JSONObject courseRecordObj = new JSONObject();
			//courseRecordObj.accumulate("username", username);
			//courseRecordObj.accumulate("username",courseRecord.getContactID());
			//if(user.getFeeOption().equals(user.NON_FEE_BASED))
			//	courseRecordObj.accumulate("username", courseRecord.getApproverUserName());
			//else
				courseRecordObj.accumulate("username", courseRecord.getContactUserName());
			courseRecordObj.accumulate("sheetNumber", courseRecord.getSheetNumber());
			courseRecordObj.accumulate("organizationName", courseRecord.getOrganizationName());
			courseRecordObj.accumulate("courseName", courseRecord.getCourseName());
			courseRecordObj.accumulate("createDate", courseRecord.getCreateDate());
			courseRecordObj.accumulate("city", courseRecord.getCity());
			courseRecordObj.accumulate("endDate", courseRecord.getEndDate());
			courseRecordObj.accumulate("state", courseRecord.getState());
			courseRecordObj.accumulate("trainingCenterName", courseRecord.getTrainingCenterName());
			courseRecordObj.accumulate("streetAddress", courseRecord.getStreetAddress());
			courseRecordObj.accumulate("county", courseRecord.getCounty());
			courseRecordObj.accumulate("zipCode", courseRecord.getZipCode());
			courseRecordObj.accumulate("totalStudents", courseRecord.getTotalStudents());
			
			if (courseRecord.getComments() != null && !courseRecord.getComments().equals("null")) {
				courseRecordObj.accumulate("comments", courseRecord.getComments());
			} else {
				courseRecordObj.accumulate("comments", "");
			}
			
			courseRecordObj.accumulate("instructors", instructorList);
			
			courseRecordObj.accumulate("students", studentList);
			
			courseRecordObj.accumulate("status", courseRecord.getStatus());
			
			// add payment information
			courseRecordObj.accumulate("couponCode", courseRecord.getPayment().getPromotionalCode());
			courseRecordObj.accumulate("originalPrice", courseRecord.getPayment().getOriginalPrice());
			courseRecordObj.accumulate("finalPrice", courseRecord.getPayment().getFinalPrice());
			
			if (courseRecord.getPayment().getPaymentType() != null) {
				courseRecordObj.accumulate("paymentType", courseRecord.getPayment().getPaymentType());
			} else {
				courseRecordObj.accumulate("paymentType", "");
			}
			if (courseRecord.getPayment().getPaymentReference() != null) {
				courseRecordObj.accumulate("paymentReference", courseRecord.getPayment().getPaymentReference());
			} else {
				courseRecordObj.accumulate("paymentReference", "");
			}
			
			if (courseRecord.getPayment().getPurchaseOrder() != null) {		/* Deprecated, now using paymentReference */
				courseRecordObj.accumulate("purchaseOrder", courseRecord.getPayment().getPurchaseOrder());
			} else {
				courseRecordObj.accumulate("purchaseOrder", "");
			}
			if (courseRecord.getPayment().getCardAccountNumber() != null) {		/* Deprecated, now using paymentReference */
				courseRecordObj.accumulate("cardAccountNumber", courseRecord.getPayment().getCardAccountNumber());
			} else {
				courseRecordObj.accumulate("cardAccountNumber", "");
			}

			// If total price is null or blank, calculate it from base price and total student
			String totalAmount = courseRecord.getPayment().getTotalPrice();
			if (totalAmount != null && !totalAmount.equals("")) {
				courseRecordObj.accumulate("totalPrice", totalAmount);
			} else {
				String amountPerStudent;
				if (courseRecord.getPayment().getPromotionalCode() != null && !courseRecord.getPayment().getPromotionalCode().equals("")) {
					amountPerStudent = courseRecord.getPayment().getFinalPrice();
				} else {
					amountPerStudent = courseRecord.getPayment().getOriginalPrice();
				}
				
				DecimalFormat df = new DecimalFormat("#.00"); 
				Double totalPrice = 0.0;
				if (amountPerStudent!=null && courseRecord.getTotalStudents()!=null) {
					totalPrice = Double.parseDouble(amountPerStudent) * Double.parseDouble(courseRecord.getTotalStudents());
					totalPrice = Math.round(totalPrice * 100.0) / 100.0;
				}
				courseRecordObj.accumulate("totalPrice", df.format(totalPrice).toString());
				totalAmount = df.format(totalPrice).toString();
			}
			
			// add approver information
			courseRecordObj.accumulate("offeringNumber", courseRecord.getOfferingNumber());
			courseRecordObj.accumulate("orderNumber", courseRecord.getOrderNumber());
			courseRecordObj.accumulate("approverName", courseRecord.getApproverFirstName()+" "+courseRecord.getApproverLastName());
			courseRecordObj.accumulate("approvedDate", courseRecord.getApprovedDate());
			if (courseRecord.getApproverComments() != null && !courseRecord.getApproverComments().equals("null")) {
				courseRecordObj.accumulate("approverComments", courseRecord.getApproverComments());
			} else {
				courseRecordObj.accumulate("approverComments", "");
			}
			
			courseRecordObj.accumulate("crsID", courseRecord.getSheetNumberID());
			courseRecordObj.accumulate("issuedCertificates", (courseRecord.isCertificatesIssued()?"true":"false"));
			courseRecordObj.accumulate("isStudentDetails", (courseRecord.isStudentDetails()?"true":"false"));

			//Determine if Price=0 or Call Billing Contact was chosen or payment has been made
			courseRecordObj.accumulate("showCertificates", showCertificates(courseRecord, totalAmount)?"true":"false");

			JSONObject sendObj = new JSONObject();
	        sendObj.put("courseRecord", courseRecordObj);
	        
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

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}

		private SabaHelper getSabaHelper(){
			
			return new SabaHelper();
		}
		
		private boolean showCertificates(CourseRecord courseRecord, String totalAmount)
		{
			//Certificates are not issued
			if(!courseRecord.isCertificatesIssued() || courseRecord.getStatus().equals("Cancelled"))
			{
				return false;
			}
			
			//CRS is approved.. no need to check payment as it must have been already made, if required.
			if("Approved".equals(courseRecord.getStatus()))
			{
				return true;
			}
			
			//Check if Call-Billing-Contact was chosen
			if("PL".equals( courseRecord.getPayment().getPaymentTypeID()))
			{
				return true;
			}

			//Check if calculated total, after coupon if any, is zero
			if(Double.parseDouble(totalAmount) == 0.0)
			{
				return true;
			}
			return false;
		}
}

