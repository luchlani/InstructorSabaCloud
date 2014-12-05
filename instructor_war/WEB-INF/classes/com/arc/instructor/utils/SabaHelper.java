package com.arc.instructor.utils;


import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import com.arc.instructor.model.CourseComponent;
import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.CourseRecordSearch;
import com.arc.instructor.model.People;
import com.arc.instructor.model.Student;
import com.arc.instructor.model.User;


public class SabaHelper {
	
	public String login(String username, String password) { 
		
		String message = "";

		try{
			getSabaWrapper().authenticateUser(username, password);
		} catch(Exception e){
			message = e.getMessage();
			e.printStackTrace();
		}
		
		return message;
	}
	
	public void forceSabaLogin(User user)
	{
		String message = login(user.getUsername(), user.getPassword());
		
		if(!message.equals(""))
		{
			System.out.println("Error forcing SABA authentication");
		}
	}

	public  boolean islogin() { 
		
		boolean success = false;
		
		try {
			success =  getSabaWrapper().isAuthenticated();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;

	}
	
	public String resetPassword(String email) { 
		
		String msg = "Successful";
		
		try {
			Integer rtn = getSabaWrapper().resetPassword(email);
			//System.out.println("resetPassword: " + rtn);
			if (rtn == 0) {
				msg = "Could not find email address in the system.";
			} else if (rtn == 1) {
				msg = "Successful";
			} else if (rtn >= 2) {
				msg = "Multiple occurrences of email was found in the system.  Please contact your administrator."; 
			} else {
				msg = "Error resetting password.  Please contact your administrator.";
			}
		} catch (Exception e) {
			msg = e.getMessage();
			e.printStackTrace();
		}

		return msg;
	}
	
	public String needPasswordChange() { 
		
		String msg = "no";
		
		try {
			msg = getSabaWrapper().needPasswordChange();
			//System.out.println("needPasswordChange: " + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return msg;
	}
	
	public String changePassword(String oldPassword, String newPassword) { 
		
		String msg = "Successful";
		
		try {
			getSabaWrapper().changePassword(oldPassword, newPassword);
		} catch (Exception e) {
			msg = e.getMessage();
			e.printStackTrace();
		}

		return msg;
	}

	public  String getUserPersonID() { 
		
		String personID = null;
		
		try {
			personID = getSabaWrapper().getPersonId();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return personID;

	}
	
	public  ArrayList<String>  getStatusList() { 
		
		ArrayList<String> statusList = new ArrayList<String>();
		
		statusList.add("Approved");	     
		statusList.add("Cancelled");
		statusList.add("Draft");
		statusList.add("Rejected");
		statusList.add("Submitted");
		
		return statusList;

	}

	public  ArrayList<String>  getSheetNoList(String personID, String feeOption, boolean showAll) { 
		
		ArrayList<String> sheetNoList = new ArrayList<String>();
		
		ArrayList<CourseRecordSearch> CourseRecordSheetNumberList = getSearchResultSet(personID, feeOption, showAll);
		
		for (CourseRecordSearch courseRecordSheet : CourseRecordSheetNumberList)
        {
			sheetNoList.add(courseRecordSheet.getSheetNumber());

        }
				
		return sheetNoList;

	}
	
	
	public  List<DropDown>  getCourseCodeList(String courseCategory) { 
		
		List<DropDown> courseCodeList = new ArrayList<DropDown>();
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		sabaResponse = findSabaCourses(courseCategory);
		
		DropDown dd;
		
		for (Map.Entry<String, String> entry : sabaResponse.entrySet())
		{
			//System.out.println("CourseID: " + entry.getKey() + " CourseName: "+ entry.getValue());
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    dd.setValue(entry.getValue());
		    courseCodeList.add(dd);
		}
		
		Collections.sort(courseCodeList, new DropDownComparator());
		
		return courseCodeList;
	}
	
	
	
    public List<DropDown> getNonFeeBasedCourseCodeList(String courseCategory, String courseFilter) { 
        
        List<DropDown> courseCodeList = new ArrayList<DropDown>();
        Map<String, String> sabaResponse = new HashMap<String, String>();
        
        sabaResponse = findSabaCourses(courseCategory);
        
        DropDown dd;
        String lowerCaseCourseFilter = courseFilter != null?courseFilter.toLowerCase():"";
        
        for (Map.Entry<String, String> entry : sabaResponse.entrySet())
        {
            //System.out.println("CourseID: " + entry.getKey() + " CourseInfo: "+ entry.getValue());
            String courseName = entry.getValue();
            String lowerCaseCourseName = courseName.toLowerCase();
            
            
            if(!lowerCaseCourseFilter.equals("")){
               if(lowerCaseCourseName.startsWith(lowerCaseCourseFilter) || 
                  lowerCaseCourseName.equalsIgnoreCase(lowerCaseCourseFilter)
                ){
                    dd = new DropDown();
                    dd.setKey(entry.getKey());
                    dd.setValue(courseName);
                    courseCodeList.add(dd);
                 }
            } else {
                dd = new DropDown();
                dd.setKey(entry.getKey());
                dd.setValue(courseName);
                courseCodeList.add(dd);
            }
        }
        
        Collections.sort(courseCodeList, new DropDownComparator());
        return (courseCodeList.size() > 100?courseCodeList.subList(0, 100):courseCodeList);
    }
	
	public  List<DropDown>  getFacilityBasedCourseCodeList() { 
        
        List<DropDown> courseCodeList = new ArrayList<DropDown>();
        Map<String, String> sabaResponse = new HashMap<String, String>();
        
        sabaResponse = findSabaFacilityBasedCourses();
        
        DropDown dd;
        
        for (Map.Entry<String, String> entry : sabaResponse.entrySet())
        {
            //System.out.println("CourseID: " + entry.getKey() + " CourseName: "+ entry.getValue());
            dd = new DropDown();
            dd.setKey(entry.getKey());
            dd.setValue(entry.getValue());
            courseCodeList.add(dd);
        }
        
        Collections.sort(courseCodeList, new DropDownComparator());
        
        return courseCodeList;
    }
	
	
	public List<DropDown> getCoursesForSessionOfferings(boolean isBlended, String category)
	{
		List<DropDown> list = new ArrayList<DropDown>();
		List<String[]> courses = findCoursesForSessionOfferings(isBlended, category);
		
		for(String[] course : courses)
		{
			list.add( dropdown(course[0], course[1]) );
		}
		return list;
	}
	
	
	public String getPriceForCourse(String courseId) throws Exception
	{
		return getSabaWrapper().getPriceForCourse(courseId);
	}
	
	public List<DropDown> getFacilities(String orgId)
	{
		List<DropDown> list = new ArrayList<DropDown>();

		//Check if Org Id is passed
		if(orgId == null || orgId.trim().equals(""))
		{
			list.add(dropdown("", " - Select Organization First - "));
			return list;
		}
		
		
		Map<String, String> facilities = findFacilities(orgId);
		if(facilities.isEmpty())
		{
			list.add(dropdown("", "No Facilities for Organization"));
			return list;
		}
		
		list.add(dropdown("", " - Select Facility - "));
		for(Map.Entry<String, String> entry : facilities.entrySet())
		{
			list.add( dropdown(entry.getKey(), entry.getValue()) );
		}
		return list;
	}
	
	public  List<DropDown>  getFeeBasedCourseCodeStudentRequiredList(List<DropDown> courseCodeList) { 
		
		List<DropDown> courseCodeStudentRequiredList = new ArrayList<DropDown>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		sabaResponse = findSabaFeeBasedCourses(null);
		
		DropDown dd;
		
		for (DropDown courseObject :  courseCodeList){
			String[] courseInfo =  sabaResponse.get(courseObject.getKey());
		    dd = new DropDown();
		    dd.setKey(courseObject.getKey());
		    dd.setValue(courseInfo[1]);
		    courseCodeStudentRequiredList.add(dd);
		}

		Collections.sort(courseCodeStudentRequiredList, new DropDownComparator());
		

		return courseCodeStudentRequiredList;

	}
	
	public  List<DropDown>  getFeeBasedCourseCodeList(String courseCategory, String courseFilter) { 
		
		List<DropDown> courseCodeList = new ArrayList<DropDown>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		sabaResponse = findSabaFeeBasedCourses(courseCategory);
		
		DropDown dd;
		String lowerCaseCourseFilter = courseFilter != null?courseFilter.toLowerCase():"";
		
		for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{
			//System.out.println("CourseID: " + entry.getKey() + " CourseInfo: "+ entry.getValue());
			String[] courseInfo = entry.getValue();
			String courseName = courseInfo[0];
			String lowerCaseCourseName = courseName.toLowerCase();
			
			if(!lowerCaseCourseFilter.equals("")){
			   if(lowerCaseCourseName.startsWith(lowerCaseCourseFilter) || 
			      lowerCaseCourseName.equalsIgnoreCase(lowerCaseCourseFilter)
			    ){
				    dd = new DropDown();
				    dd.setKey(entry.getKey());
				    dd.setValue(courseName);
				    courseCodeList.add(dd);
			   	 }
			} else {
			    dd = new DropDown();
			    dd.setKey(entry.getKey());
			    dd.setValue(courseName);
			    courseCodeList.add(dd);	
			}
		}
		
		Collections.sort(courseCodeList, new DropDownComparator());
		return (courseCodeList.size() > 100?courseCodeList.subList(0, 100):courseCodeList);
	}
	
	public  List<DropDown>  getAquaticsCourseCodeList() { 
		
		List<DropDown> courseCodeList = new ArrayList<DropDown>();
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		sabaResponse = findCourseForAquatics();
		
		DropDown dd;
		
		for (Map.Entry<String, String> entry : sabaResponse.entrySet())
		{
			//System.out.println("CourseID: " + entry.getKey() + " CourseName: "+ entry.getValue());
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    dd.setValue(entry.getValue());
		    courseCodeList.add(dd);
		}
		

		Collections.sort(courseCodeList, new DropDownComparator());
		
		//dd = new DropDown();
	    //dd.setKey("");
	    //dd.setValue("Select Course Code");	
	    //courseCodeList.add(0, dd);
		
		return courseCodeList;

	}
	
	public  List<String> getCourseCodeAutoCompleteList(String personID, String feeOption, boolean showAll){
		
		
		List<String> courseCodeList = new ArrayList<String>();
		ArrayList<CourseRecordSearch> searchResults =  getSearchResultSet(personID, feeOption, showAll);
		
		Collections.sort(searchResults, new CourseCodeComparator());
		
		for (CourseRecordSearch results : searchResults)
        {
			if(!courseCodeList.contains(results.getCourseName())){
				courseCodeList.add(results.getCourseName());
			}
			
        }
		
		return courseCodeList;
	}
	
	
	public String getViewCourseRecordSheetNumberID(String personID, String sheetNumber, String feeOption, boolean showAll){
		
		
		String sheetNumberID = null;
		ArrayList<CourseRecordSearch> searchResults =  getSearchResultSet(personID, feeOption, showAll);
		
		for (CourseRecordSearch results : searchResults)
        {
			if(results.getSheetNumber().equals(sheetNumber)){
				sheetNumberID = results.getSheetNumberID();
				break;
			}
			
        }
		
		return sheetNumberID;
	}
	
	public  String getCourseName(String courseID, String feeOption){
		
		String courseName = null;
		
		Map<String, String> sabaResponse = new HashMap<String, String>();
		Map<String, String[]> sabaFeeBasedResponse = new HashMap<String, String[]>();
		
		if (feeOption.equals("Non-fee")) {
			sabaResponse = findSabaCourses();
			courseName = sabaResponse.get(courseID);
		} else if (feeOption.equals("Facility-fee")) {
		    sabaResponse = findSabaFacilityBasedCourses();
		    courseName = sabaResponse.get(courseID);
		} else {
            sabaFeeBasedResponse = findSabaFeeBasedCourses(null);
            String[] courseInfo = sabaFeeBasedResponse.get(courseID);
            courseName = courseInfo[0];
        }
		
		return courseName;
		
	}
	
	public  List<DropDown>  getOrganizationList(String personID) { 
		
		List<DropDown> organizationList = new ArrayList<DropDown>();

		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		sabaResponse = findSabaOrganizations(personID);
		
		DropDown dd;
		
		for (Map.Entry<String, String> entry : sabaResponse.entrySet())
		{
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    dd.setValue(entry.getValue());
		    //System.out.println("getOrganizationList() key: " + entry.getKey() + " value ="+entry.getValue()); 
		    organizationList.add(dd);
		}
			
		Collections.sort(organizationList, new DropDownComparator());
		
		dd = new DropDown();
	    dd.setKey("");
	    dd.setValue("Select Organization");
	    organizationList.add(0, dd);

		return organizationList;

	}
	
	public  List<DropDown>  getOrganizationListWithUnitCode(String personID, String unitCode) { 
		
		List<DropDown> organizationList = new ArrayList<DropDown>();

		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		sabaResponse = findSabaOrganizationsWithUnitCode(personID);
		
		DropDown dd;
		
		for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{
		    String[] data = entry.getValue();
		    if (data.length == 2) {
		    	if (data[1].equals(unitCode)) {
		    		dd = new DropDown();
				    dd.setKey(entry.getKey());
				    dd.setValue(data[0]);
				    organizationList.add(dd);
		    	}
		    }
		}
			
		Collections.sort(organizationList, new DropDownComparator());
		
		dd = new DropDown();
	    dd.setKey("");
	    dd.setValue("Select Organization");
	    organizationList.add(0, dd);

		return organizationList;

	}
	
	public  String getOrganizationName(String personID, String organizationID){
		
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		sabaResponse = findSabaOrganizations(personID);
		
		return sabaResponse.get(organizationID);		
		
	}
	
	public  ArrayList<String>  getCertificatesList() { 

		ArrayList<String> certificatesList = new ArrayList<String>();

		certificatesList.add("Select One");
		certificatesList.add("Certificates will be printed by customer");
		certificatesList.add("Send certificates to customer");
		certificatesList.add("No certificates needed");

		return certificatesList;

	}

	public  String  applyCouponToCRS(String crsID, String code) { 
		
		String msg = "Coupon successfully applied.";
		//System.out.println("applyCoupontoCRS() crsID: " + crsID + ", coupon: "+code);
		
		try {
			getSabaWrapper().applyCouponToCrs(crsID, code);
		} catch (Exception e) {
			msg = e.getMessage();
			//System.out.println("applyCoupontoCRS() Exception message="+msg);
			e.printStackTrace();
		}

		return msg;
	}
	
	public  String  removeCouponFromCRS(String crsID) { 
		
		String msg = "Coupon successfully removed.";
		//System.out.println("applyCoupontoCRS() crsID: " + crsID);
		
		try {
			getSabaWrapper().removeCouponFromCrs(crsID);
		} catch (Exception e) {
			msg = e.getMessage();
			//System.out.println("applyCoupontoCRS() Exception message="+msg);
			e.printStackTrace();
		}

		return msg;
	}
	
	public  List<DropDown>  getPurchaseOrderList(String orgID, String amount) { 
		
		List<DropDown> purchaseOrderList = new ArrayList<DropDown>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		//System.out.println("getPurchaseOrderList() orgID: " + orgID + ", amount: "+amount); 
		
		try {
			sabaResponse = getSabaWrapper().getPurchaseOrder(orgID, amount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DropDown dd;
		
		for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    String[] data = entry.getValue();
	    	for(int d=0; d<data.length; d++){

	    		//System.out.println("getPurchaseOrderList() key: " + entry.getKey() + " data("+d+") value ="+data[d]); 
	    
	    		switch (d) {
		            case 0: dd.setValue(data[d]);
		                    break;   
		            default:
		            		break;
	    		}
	    	}
		    
		    purchaseOrderList.add(dd);
		}

		Collections.sort(purchaseOrderList, new DropDownComparator());
		
		dd = new DropDown();
	    dd.setKey("");
	    dd.setValue("Select Purchase Order");
	    purchaseOrderList.add(0, dd);
		
		return purchaseOrderList;
	}
	
	// helper function to get purchase order name given an id
	public  String  getPurchaseOrderName(String orgID, String purchaseOrderID) {
		
		String name = "";
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		if (orgID == null || purchaseOrderID == null) {
			return "";
		}
		
		try {
			// amount passed in is 0.0 since we want a list of all purchase orders returned
			sabaResponse = getSabaWrapper().getPurchaseOrder(orgID, "0.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{
			if (entry.getKey().equals(purchaseOrderID)) {
				String[] data = entry.getValue();
		    	for(int d=0; d<data.length; d++){
		    		switch (d) {
		            	case 0: name = data[d];
		                    break;   
		            	default:
		            		break;
		    		}
		    	}
			}
		}
		
		return name;
	}
	
	public String addPurchaseOrder(String crsID, String purchaseOrderID){
		//System.out.println("addPurchaseOrder() crsID: " + crsID + " purchaseOrderID: " + purchaseOrderID);
		
		String msg = "Success";
		try {
			getSabaWrapper().addPurchaseOrder(crsID, purchaseOrderID);
		} catch (Exception e) {
			msg = e.getMessage();
			//System.out.println("addPurchaseOrder() Exception message="+msg);
			e.printStackTrace();
		}
		
		return msg;
	}
	
	public Map<String, String> getCCStaticValues(String crsId)
	{
		
		Map<String, String> valuesMap = new HashMap<String, String>();
		try
		{
			valuesMap = getSabaWrapper().getCCStaticValues(crsId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return valuesMap;
	}
	
	
	
	public Map<String, String> getCCTransactionSignature(String amount, String currency, String transactionType, String crsId) {
		//System.out.println("getCCTransactionSignature() amount: " + amount + ", currency: " + currency + ", transactionType: " + transactionType + ", crsId: " + crsId);
		
		Map<String, String> ccTransactionMap = new HashMap<String, String>();
		HashMap sabaResponse = new HashMap();

		try {
			sabaResponse = getSabaWrapper().getCCTransactionSignature(amount, currency, transactionType, crsId);
			//System.out.println("getCCTransactionSignature() sabaResponse="+sabaResponse);
		} catch (Exception e) {
			//System.out.println("getCCTransactionSignature() Exception message="+e.getMessage());
			e.printStackTrace();
		}
		
		Set set = sabaResponse.entrySet();
		Iterator i = set.iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry)i.next();
			//System.out.print(entry.getKey() + ": ");
			//System.out.println(entry.getValue());
			if (entry.getValue() != null) {
				ccTransactionMap.put(entry.getKey().toString(), entry.getValue().toString());
			} else {
				ccTransactionMap.put(entry.getKey().toString(), "");
			}
		}

		return ccTransactionMap;
	}
	
	public String insertCCTrack(String crsId) {
		//System.out.println("insertCCTrack() crsId: " + crsId);
		
		String msg = "Success";
		try {
			getSabaWrapper().insertCCTrack(crsId);
		} catch (Exception e) {
			msg = e.getMessage();
			//System.out.println("insertCCTrack() Exception message="+e.getMessage());
			e.printStackTrace();
		}
		return msg;
	}
	
	public String paymentPendingNotification(String crsId) {
		//System.out.println("paymentPendingNotification() crsId: " + crsId);
		
		String msg = "Success";
		try {
			getSabaWrapper().paymentPendingNotification(crsId);
		} catch (Exception e) {
			msg = e.getMessage();
			//System.out.println("paymentPendingNotification() Exception message="+e.getMessage());
			e.printStackTrace();
		}
		return msg;
	}
	
	public String approveNoCostNoPaymentCR(String crsId) {
		//System.out.println("approveNoCostNoPaymentCR() crsId: " + crsId);
		
		String msg = "Success";
		try {
			getSabaWrapper().approveNoCostNoPaymentCR(crsId);
		} catch (Exception e) {
			msg = e.getMessage();
			e.printStackTrace();
		}
		return msg;
	}
	
	
  public String paymentProcessing(HttpServletRequest request) {
		//System.out.println("paymentProcessing()");
		
		String msg = "Success";
		try {
			List result = getSabaWrapper().paymentProcessing(request);
			//System.out.println("paymentProcessing() result: "+result);
		} catch (Exception e) {
			msg = e.getMessage();
			//System.out.println("paymentProcessing() Exception message="+e.getMessage());
			e.printStackTrace();
		}
		return msg;
	}
	
	public String getTotalPrice(CourseRecord courseRecord) {
		return getTotalPrice(courseRecord.getPayment().getPromotionalCode(),
				courseRecord.getPayment().getOriginalPrice(),
				courseRecord.getPayment().getFinalPrice(),
				courseRecord.getTotalStudents());
	}
	
	public String getTotalPrice(String promotionalCode, String originalPrice, String finalPrice, String totalStudents) {
		String amountPerStudent;
		if (promotionalCode == null || promotionalCode.equals("")) {
			amountPerStudent = originalPrice;
		} else {
			amountPerStudent = finalPrice;
		}
		
		DecimalFormat df = new DecimalFormat("#.00"); 
		Double totalPrice = 0.0;
		if (amountPerStudent != null && totalStudents != null) {
			totalPrice = Double.parseDouble(amountPerStudent) * Double.parseDouble(totalStudents);
			totalPrice = Math.round(totalPrice * 100.0) / 100.0;
		}
		return df.format(totalPrice).toString();
	}
	
	public  String  issueStudentCertificates(String sheetNumberID) { 
		
		String msg = "Successful";
		
		try {
			getSabaWrapper().issueCertificateToStudents(sheetNumberID);
		} catch (Exception e) {
			String defaultMsg = "Error Issuing Student Certificates for Course Record Entry. Please contact your administrator";
			msg = handleException(e, sheetNumberID, defaultMsg);
		}

		return msg;
	}
	
	
	private String handleException(Exception e, String context, String message)
	{
		String msg = message;
		
		Throwable rootCause = getRootCause(e);
		if(rootCause instanceof Exception)
		{
			msg = rootCause.getMessage();
		}
		
		System.out.println("SabaHelper-context: " + context);
		e.printStackTrace();
		return msg;
	}
	
	//Go upto 10 levels deep to determine what is the root cause.
	private Throwable getRootCause(Exception e)
	{
		if(e==null) return e;
		
		Throwable cause = e;
		int i=0;
		System.out.println("getRootCause-" + i + ": " + cause);
		while(cause.getCause() != null && i < 10)
		{
			cause = cause.getCause();
			System.out.println("getRootCause-" + i + ": " + cause);
			i++;
		}
		return cause;
	}
	
	public Map<String, String> findUser(String first, String last, String email) {
		
		Map<String, String> sabaResponse = new HashMap<String, String>();

		try {
			//System.out.println("findUser() email="+email);
			sabaResponse = getSabaWrapper().findUser(email);
			//System.out.println("findUser() sabaResponse="+sabaResponse);
		} catch (Exception e) {
			//System.out.println("findUser() Exception message="+e.getMessage());
			e.printStackTrace();
		}

		return sabaResponse;
	}
	
	public People getPerson(String id) throws Exception
	{
		Map<String, String> sabaResponse = getSabaWrapper().getName(id);
		
		People user = new People();
		user.setId(id);
		user.setFirstName(sabaResponse.get("first_name"));
		user.setLastName(sabaResponse.get("last_name"));
		
		return user;
	}
	
	public ArrayList<DropDown> getCourseCategoryList(){
		
		ArrayList<DropDown> courseCategoryList = new ArrayList<DropDown>();		
		
		Map<String, String> courseCategoryMap = new HashMap<String, String>();
		
		courseCategoryMap.put("A1", "A1 - First Aid, CPR, AED");
		courseCategoryMap.put("A2", "A2 - Preparedness");
		courseCategoryMap.put("A3", "A3 - Aquatics and Water Safety");
		courseCategoryMap.put("A4", "A4 - Caregiving");
		courseCategoryMap.put("A5", "A5 - PH&SS Administration and Instructor Training");
		courseCategoryMap.put("A6", "A6 - HIV/AIDS Prevention Education");
		courseCategoryMap.put("A7", "A7 - Ready Rating");
		courseCategoryMap.put("B1", "B1 - Disaster Courses");
		courseCategoryMap.put("C1", "C1 - Human Resources Courses");
		courseCategoryMap.put("D1", "D1 - Service to the Armed Forces");
		courseCategoryMap.put("E1", "E1 - Volunteers");
		courseCategoryMap.put("F1", "F1 - International Services");
		courseCategoryMap.put("G1", "G1 - Finance");
		courseCategoryMap.put("H1", "H1 - Information Technology");
		courseCategoryMap.put("None", "None");


		DropDown dd;
		
		for (Map.Entry<String, String> entry : courseCategoryMap.entrySet())
		{
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    dd.setValue(entry.getValue());
		    courseCategoryList.add(dd);
		}
		
		Collections.sort(courseCategoryList, new DropDownComparator());
		
		dd = new DropDown();
	    dd.setKey("");
	    dd.setValue("Select One");
	    courseCategoryList.add(0, dd);
	    
	    return courseCategoryList;
		
	}
	
	public  ArrayList<DropDown>  getStateList() { 

		ArrayList<DropDown> stateList = new ArrayList<DropDown>();		
		
		Map<String, String> stateMap = new HashMap<String, String>();		
		stateMap.put("AL","Alabama");
		stateMap.put("AK","Alaska");
		stateMap.put("AS","American Samoa");
		stateMap.put("AZ","Arizona");
		stateMap.put("AR","Arkansas");
		stateMap.put("CA","California");
		stateMap.put("CO","Colorado");
		stateMap.put("CT","Connecticut");
		stateMap.put("DE","Delaware");
		stateMap.put("DC","District of Columbia");
		stateMap.put("FL","Florida");
		stateMap.put("GA","Georgia");
		stateMap.put("GU","Guam");
		stateMap.put("HI","Hawaii");
		stateMap.put("ID","Idaho");
		stateMap.put("IL","Illinois");
		stateMap.put("IN","Indiana");
		stateMap.put("IA","Iowa");
		stateMap.put("KS","Kansas");
		stateMap.put("KY","Kentucky");
		stateMap.put("LA","Louisiana");
		stateMap.put("ME","Maine");
		stateMap.put("MD","Maryland");
		stateMap.put("MA","Massachusetts");
		stateMap.put("MI","Michigan");
		stateMap.put("MN","Minnesota");
		stateMap.put("MS","Mississippi");
		stateMap.put("MO","Missouri");
		stateMap.put("MT","Montana");
		stateMap.put("NE","Nebraska");
		stateMap.put("NV","Nevada");
		stateMap.put("NH","New Hampshire");
		stateMap.put("NJ","New Jersey");
		stateMap.put("NM","New Mexico");
		stateMap.put("NY","New York");
		stateMap.put("NC","North Carolina");
		stateMap.put("ND","North Dakota");
		stateMap.put("MP","Northern Marianas Islands");
		stateMap.put("OH","Ohio");
		stateMap.put("OK","Oklahoma");
		stateMap.put("OR","Oregon");
		stateMap.put("PA","Pennsylvania");
		stateMap.put("PR","Puerto Rico");
		stateMap.put("RI","Rhode Island");
		stateMap.put("SC","South Carolina");
		stateMap.put("SD","South Dakota");
		stateMap.put("TN","Tennessee");
		stateMap.put("TX","Texas");
		stateMap.put("UT","Utah");
		stateMap.put("VT","Vermont");
		stateMap.put("VI","Virgin Islands");
		stateMap.put("VA","Virginia");
		stateMap.put("WA","Washington");
		stateMap.put("WV","West Virginia");
		stateMap.put("WI","Wisconsin");
		stateMap.put("WY","Wyoming");
		
		DropDown dd;
		
		for (Map.Entry<String, String> entry : stateMap.entrySet())
		{
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    dd.setValue(entry.getValue());
		    stateList.add(dd);
		}
		
		Collections.sort(stateList, new DropDownComparator());
		
		dd = new DropDown();
	    dd.setKey("");
	    dd.setValue("Select State");
	    stateList.add(0, dd);

		return stateList;
	
	}
	
	public  ArrayList<String>  getCountyList(String stateCode) { 

		ArrayList<String> countyList = new ArrayList<String>();

		countyList.add("Select County");
		if(stateCode.equals("AK")){
			countyList.add("AK-Aleutians East Borough");
			countyList.add("AK-Aleutians West Census Area");
			countyList.add("AK-Anchorage Municipality");
			countyList.add("AK-Bethel Census Area");
			countyList.add("AK-Bristol Bay Borough");
			countyList.add("AK-Denali Borough");
			countyList.add("AK-Dillingham Census Area");
			countyList.add("AK-Fairbanks North Star Borough");
			countyList.add("AK-Haines Borough");
			countyList.add("AK-Hoonah Angoon");
			countyList.add("AK-Juneau City and Borough");
			countyList.add("AK-Kenai Peninsula Borough");
			countyList.add("AK-Ketchikan Gateway Borough");
			countyList.add("AK-Kodiak Island Borough");
			countyList.add("AK-Lake and Peninsula Borough");
			countyList.add("AK-Matanuska-Susitna Borough");
			countyList.add("AK-Nome Census Area");
			countyList.add("AK-North Slope Borough");
			countyList.add("AK-Northwest Arctic Borough");
			countyList.add("AK-Petersburg County");
			countyList.add("AK-Prince of Wales-Outer Ketchikan");
			countyList.add("AK-Sitka City and Borough");
			countyList.add("AK-Skagway-Hoonah-Angoon Census Area");
			countyList.add("AK-Southeast Fairbanks Census Area");
			countyList.add("AK-Valdez-Cordova Census Area");
			countyList.add("AK-Wade Hampton Census Area");
			countyList.add("AK-Wrangell-Petersburg Census Area");
			countyList.add("AK-Yakutat City and Borough");
			countyList.add("AK-Yukon-Koyukuk Census Area");
		} else if(stateCode.equals("AL")){
			countyList.add("AL-Autauga County");
			countyList.add("AL-Baldwin County");
			countyList.add("AL-Barbour County");
			countyList.add("AL-Bibb County");
			countyList.add("AL-Blount County");
			countyList.add("AL-Bullock County");
			countyList.add("AL-Butler County");
			countyList.add("AL-Calhoun County");
			countyList.add("AL-Chambers County");
			countyList.add("AL-Cherokee County");
			countyList.add("AL-Chilton County");
			countyList.add("AL-Choctaw County");
			countyList.add("AL-Clarke County");
			countyList.add("AL-Clay County");
			countyList.add("AL-Cleburne County");
			countyList.add("AL-Coffee County");
			countyList.add("AL-Colbert County");
			countyList.add("AL-Conecuh County");
			countyList.add("AL-Coosa County");
			countyList.add("AL-Covington County");
			countyList.add("AL-Crenshaw County");
			countyList.add("AL-Cullman County");
			countyList.add("AL-Dale County");
			countyList.add("AL-Dallas County");
			countyList.add("AL-De Kalb County");
			countyList.add("AL-Elmore County");
			countyList.add("AL-Escambia County");
			countyList.add("AL-Etowah County");
			countyList.add("AL-Fayette County");
			countyList.add("AL-Franklin County");
			countyList.add("AL-Geneva County");
			countyList.add("AL-Greene County");
			countyList.add("AL-Hale County");
			countyList.add("AL-Henry County");
			countyList.add("AL-Houston County");
			countyList.add("AL-Jackson County");
			countyList.add("AL-Jefferson County");
			countyList.add("AL-Lamar County");
			countyList.add("AL-Lauderdale County");
			countyList.add("AL-Lawrence County");
			countyList.add("AL-Lee County");
			countyList.add("AL-Limestone County");
			countyList.add("AL-Lowndes County");
			countyList.add("AL-Macon County");
			countyList.add("AL-Madison County");
			countyList.add("AL-Marengo County");
			countyList.add("AL-Marion County");
			countyList.add("AL-Marshall County");
			countyList.add("AL-Mobile County");
			countyList.add("AL-Monroe County");
			countyList.add("AL-Montgomery County");
			countyList.add("AL-Morgan County");
			countyList.add("AL-Perry County");
			countyList.add("AL-Pickens County");
			countyList.add("AL-Pike County");
			countyList.add("AL-Randolph County");
			countyList.add("AL-Russell County");
			countyList.add("AL-Saint Clair County");
			countyList.add("AL-Shelby County");
			countyList.add("AL-Sumter County");
			countyList.add("AL-Talladega County");
			countyList.add("AL-Tallapoosa County");
			countyList.add("AL-Tuscaloosa County");
			countyList.add("AL-Walker County");
			countyList.add("AL-Washington County");
			countyList.add("AL-Wilcox County");
			countyList.add("AL-Winston County");
		} else if(stateCode.equals("AR")){
			countyList.add("AR-Arkansas County");
			countyList.add("AR-Ashley County");
			countyList.add("AR-Baxter County");
			countyList.add("AR-Benton County");
			countyList.add("AR-Boone County");
			countyList.add("AR-Bradley County");
			countyList.add("AR-Calhoun County");
			countyList.add("AR-Carroll County");
			countyList.add("AR-Chicot County");
			countyList.add("AR-Clark County");
			countyList.add("AR-Clay County");
			countyList.add("AR-Cleburne County");
			countyList.add("AR-Cleveland County");
			countyList.add("AR-Columbia County");
			countyList.add("AR-Conway County");
			countyList.add("AR-Craighead County");
			countyList.add("AR-Crawford County");
			countyList.add("AR-Crittenden County");
			countyList.add("AR-Cross County");
			countyList.add("AR-Dallas County");
			countyList.add("AR-Desha County");
			countyList.add("AR-Drew County");
			countyList.add("AR-Faulkner County");
			countyList.add("AR-Franklin County");
			countyList.add("AR-Fulton County");
			countyList.add("AR-Garland County");
			countyList.add("AR-Grant County");
			countyList.add("AR-Greene County");
			countyList.add("AR-Hempstead County");
			countyList.add("AR-Hot Spring County");
			countyList.add("AR-Howard County");
			countyList.add("AR-Independence County");
			countyList.add("AR-Izard County");
			countyList.add("AR-Jackson County");
			countyList.add("AR-Jefferson County");
			countyList.add("AR-Johnson County");
			countyList.add("AR-Lafayette County");
			countyList.add("AR-Lawrence County");
			countyList.add("AR-Lee County");
			countyList.add("AR-Lincoln County");
			countyList.add("AR-Little River County");
			countyList.add("AR-Logan County");
			countyList.add("AR-Lonoke County");
			countyList.add("AR-Madison County");
			countyList.add("AR-Marion County");
			countyList.add("AR-Miller County");
			countyList.add("AR-Mississippi County");
			countyList.add("AR-Monroe County");
			countyList.add("AR-Montgomery County");
			countyList.add("AR-Nevada County");
			countyList.add("AR-Newton County");
			countyList.add("AR-Ouachita County");
			countyList.add("AR-Perry County");
			countyList.add("AR-Phillips County");
			countyList.add("AR-Pike County");
			countyList.add("AR-Poinsett County");
			countyList.add("AR-Polk County");
			countyList.add("AR-Pope County");
			countyList.add("AR-Prairie County");
			countyList.add("AR-Pulaski County");
			countyList.add("AR-Randolph County");
			countyList.add("AR-Saint Francis County");
			countyList.add("AR-Saline County");
			countyList.add("AR-Scott County");
			countyList.add("AR-Searcy County");
			countyList.add("AR-Sebastian County");
			countyList.add("AR-Sevier County");
			countyList.add("AR-Sharp County");
			countyList.add("AR-Stone County");
			countyList.add("AR-Union County");
			countyList.add("AR-Van Buren County");
			countyList.add("AR-Washington County");
			countyList.add("AR-White County");
			countyList.add("AR-Woodruff County");
			countyList.add("AR-Yell County");
		} else if(stateCode.equals("AS")){
			countyList.add("AS-American Samoa");
		} else if(stateCode.equals("AZ")){
			countyList.add("AZ-Apache County");
			countyList.add("AZ-Cochise County");
			countyList.add("AZ-Coconino County");
			countyList.add("AZ-Gila County");
			countyList.add("AZ-Graham County");
			countyList.add("AZ-Greenlee County");
			countyList.add("AZ-La Paz County");
			countyList.add("AZ-Maricopa County");
			countyList.add("AZ-Mohave County");
			countyList.add("AZ-Navajo County");
			countyList.add("AZ-Pima County");
			countyList.add("AZ-Pinal County");
			countyList.add("AZ-Santa Cruz County");
			countyList.add("AZ-Yavapai County");
			countyList.add("AZ-Yuma County");
		} else if(stateCode.equals("CA")){
			countyList.add("CA-Alameda County");
			countyList.add("CA-Alpine County");
			countyList.add("CA-Amador County");
			countyList.add("CA-Butte County");
			countyList.add("CA-Calaveras County");
			countyList.add("CA-Colusa County");
			countyList.add("CA-Contra Costa County");
			countyList.add("CA-Del Norte County");
			countyList.add("CA-El Dorado County");
			countyList.add("CA-Fresno County");
			countyList.add("CA-Glenn County");
			countyList.add("CA-Humboldt County");
			countyList.add("CA-Imperial County");
			countyList.add("CA-Inyo County");
			countyList.add("CA-Kern County");
			countyList.add("CA-Kings County");
			countyList.add("CA-Lake County");
			countyList.add("CA-Lassen County");
			countyList.add("CA-Los Angeles County");
			countyList.add("CA-Madera County");
			countyList.add("CA-Marin County");
			countyList.add("CA-Mariposa County");
			countyList.add("CA-Mendocino County");
			countyList.add("CA-Merced County");
			countyList.add("CA-Modoc County");
			countyList.add("CA-Mono County");
			countyList.add("CA-Monterey County");
			countyList.add("CA-Napa County");
			countyList.add("CA-Nevada County");
			countyList.add("CA-Orange County");
			countyList.add("CA-Placer County");
			countyList.add("CA-Plumas County");
			countyList.add("CA-Riverside County");
			countyList.add("CA-Sacramento County");
			countyList.add("CA-San Benito County");
			countyList.add("CA-San Bernardino County");
			countyList.add("CA-San Diego County");
			countyList.add("CA-San Francisco County");
			countyList.add("CA-San Joaquin County");
			countyList.add("CA-San Luis Obispo County");
			countyList.add("CA-San Mateo County");
			countyList.add("CA-Santa Barbara County");
			countyList.add("CA-Santa Clara County");
			countyList.add("CA-Santa Cruz County");
			countyList.add("CA-Shasta County");
			countyList.add("CA-Sierra County");
			countyList.add("CA-Siskiyou County");
			countyList.add("CA-Solano County");
			countyList.add("CA-Sonoma County");
			countyList.add("CA-Stanislaus County");
			countyList.add("CA-Sutter County");
			countyList.add("CA-Tehama County");
			countyList.add("CA-Trinity County");
			countyList.add("CA-Tulare County");
			countyList.add("CA-Tuolumne County");
			countyList.add("CA-Ventura County");
			countyList.add("CA-Yolo County");
			countyList.add("CA-Yuba County");
		} else if(stateCode.equals("CO")){
			countyList.add("CO-Adams County");
			countyList.add("CO-Alamosa County");
			countyList.add("CO-Arapahoe County");
			countyList.add("CO-Archuleta County");
			countyList.add("CO-Baca County");
			countyList.add("CO-Bent County");
			countyList.add("CO-Boulder County");
			countyList.add("CO-Broomfield County");
			countyList.add("CO-Chaffee County");
			countyList.add("CO-Cheyenne County");
			countyList.add("CO-Clear Creek County");
			countyList.add("CO-Conejos County");
			countyList.add("CO-Costilla County");
			countyList.add("CO-Crowley County");
			countyList.add("CO-Custer County");
			countyList.add("CO-Delta County");
			countyList.add("CO-Denver County");
			countyList.add("CO-Dolores County");
			countyList.add("CO-Douglas County");
			countyList.add("CO-Eagle County");
			countyList.add("CO-El Paso County");
			countyList.add("CO-Elbert County");
			countyList.add("CO-Fremont County");
			countyList.add("CO-Garfield County");
			countyList.add("CO-Gilpin County");
			countyList.add("CO-Grand County");
			countyList.add("CO-Gunnison County");
			countyList.add("CO-Hinsdale County");
			countyList.add("CO-Huerfano County");
			countyList.add("CO-Jackson County");
			countyList.add("CO-Jefferson County");
			countyList.add("CO-Kiowa County");
			countyList.add("CO-Kit Carson County");
			countyList.add("CO-La Plata County");
			countyList.add("CO-Lake County");
			countyList.add("CO-Larimer County");
			countyList.add("CO-Las Animas County");
			countyList.add("CO-Lincoln County");
			countyList.add("CO-Logan County");
			countyList.add("CO-Mesa County");
			countyList.add("CO-Mineral County");
			countyList.add("CO-Moffat County");
			countyList.add("CO-Montezuma County");
			countyList.add("CO-Montrose County");
			countyList.add("CO-Morgan County");
			countyList.add("CO-Otero County");
			countyList.add("CO-Ouray County");
			countyList.add("CO-Park County");
			countyList.add("CO-Phillips County");
			countyList.add("CO-Pitkin County");
			countyList.add("CO-Prowers County");
			countyList.add("CO-Pueblo County");
			countyList.add("CO-Rio Blanco County");
			countyList.add("CO-Rio Grande County");
			countyList.add("CO-Routt County");
			countyList.add("CO-Saguache County");
			countyList.add("CO-San Juan County");
			countyList.add("CO-San Miguel County");
			countyList.add("CO-Sedgwick County");
			countyList.add("CO-Summit County");
			countyList.add("CO-Teller County");
			countyList.add("CO-Washington County");
			countyList.add("CO-Weld County");
			countyList.add("CO-Yuma County");
		} else if(stateCode.equals("CT")){
			countyList.add("CT-Fairfield County");
			countyList.add("CT-Hartford County");
			countyList.add("CT-Litchfield County");
			countyList.add("CT-Middlesex County");
			countyList.add("CT-New Haven County");
			countyList.add("CT-New London County");
			countyList.add("CT-Tolland County");
			countyList.add("CT-Windham County");
		} else if(stateCode.equals("DC")){
			countyList.add("DC-District Of Columbia");
		} else if(stateCode.equals("DE")){
			countyList.add("DE-Kent County");
			countyList.add("DE-New Castle County");
			countyList.add("DE-Sussex County");
		} else if(stateCode.equals("FL")){
			countyList.add("FL-Alachua County");
			countyList.add("FL-Baker County");
			countyList.add("FL-Bay County");
			countyList.add("FL-Bradford County");
			countyList.add("FL-Brevard County");
			countyList.add("FL-Broward County");
			countyList.add("FL-Calhoun County");
			countyList.add("FL-Charlotte County");
			countyList.add("FL-Citrus County");
			countyList.add("FL-Clay County");
			countyList.add("FL-Collier County");
			countyList.add("FL-Columbia County");
			countyList.add("FL-De Soto County");
			countyList.add("FL-Dixie County");
			countyList.add("FL-Duval County");
			countyList.add("FL-Escambia County");
			countyList.add("FL-Flagler County");
			countyList.add("FL-Franklin County");
			countyList.add("FL-Gadsden County");
			countyList.add("FL-Gilchrist County");
			countyList.add("FL-Glades County");
			countyList.add("FL-Gulf County");
			countyList.add("FL-Hamilton County");
			countyList.add("FL-Hardee County");
			countyList.add("FL-Hendry County");
			countyList.add("FL-Hernando County");
			countyList.add("FL-Highlands County");
			countyList.add("FL-Hillsborough County");
			countyList.add("FL-Holmes County");
			countyList.add("FL-Indian River County");
			countyList.add("FL-Jackson County");
			countyList.add("FL-Jefferson County");
			countyList.add("FL-Lafayette County");
			countyList.add("FL-Lake County");
			countyList.add("FL-Lee County");
			countyList.add("FL-Leon County");
			countyList.add("FL-Levy County");
			countyList.add("FL-Liberty County");
			countyList.add("FL-Madison County");
			countyList.add("FL-Manatee County");
			countyList.add("FL-Marion County");
			countyList.add("FL-Martin County");
			countyList.add("FL-Miami-Dade County");
			countyList.add("FL-Monroe County");
			countyList.add("FL-Nassau County");
			countyList.add("FL-Okaloosa County");
			countyList.add("FL-Okeechobee County");
			countyList.add("FL-Orange County");
			countyList.add("FL-Osceola County");
			countyList.add("FL-Palm Beach County");
			countyList.add("FL-Pasco County");
			countyList.add("FL-Pinellas County");
			countyList.add("FL-Polk County");
			countyList.add("FL-Putnam County");
			countyList.add("FL-Saint Johns County");
			countyList.add("FL-Saint Lucie County");
			countyList.add("FL-Santa Rosa County");
			countyList.add("FL-Sarasota County");
			countyList.add("FL-Seminole County");
			countyList.add("FL-Sumter County");
			countyList.add("FL-Suwannee County");
			countyList.add("FL-Taylor County");
			countyList.add("FL-Union County");
			countyList.add("FL-Volusia County");
			countyList.add("FL-Wakulla County");
			countyList.add("FL-Walton County");
			countyList.add("FL-Washington County");
		} else if(stateCode.equals("FM")){
			countyList.add("FM-Federated States Of Micro");
		} else if(stateCode.equals("GA")){
			countyList.add("GA-Appling County");
			countyList.add("GA-Atkinson County");
			countyList.add("GA-Bacon County");
			countyList.add("GA-Baker County");
			countyList.add("GA-Baldwin County");
			countyList.add("GA-Banks County");
			countyList.add("GA-Barrow County");
			countyList.add("GA-Bartow County");
			countyList.add("GA-Ben Hill County");
			countyList.add("GA-Berrien County");
			countyList.add("GA-Bibb County");
			countyList.add("GA-Bleckley County");
			countyList.add("GA-Brantley County");
			countyList.add("GA-Brooks County");
			countyList.add("GA-Bryan County");
			countyList.add("GA-Bulloch County");
			countyList.add("GA-Burke County");
			countyList.add("GA-Butts County");
			countyList.add("GA-Calhoun County");
			countyList.add("GA-Camden County");
			countyList.add("GA-Candler County");
			countyList.add("GA-Carroll County");
			countyList.add("GA-Catoosa County");
			countyList.add("GA-Charlton County");
			countyList.add("GA-Chatham County");
			countyList.add("GA-Chattahoochee County");
			countyList.add("GA-Chattooga County");
			countyList.add("GA-Cherokee County");
			countyList.add("GA-Clarke County");
			countyList.add("GA-Clay County");
			countyList.add("GA-Clayton County");
			countyList.add("GA-Clinch County");
			countyList.add("GA-Cobb County");
			countyList.add("GA-Coffee County");
			countyList.add("GA-Colquitt County");
			countyList.add("GA-Columbia County");
			countyList.add("GA-Cook County");
			countyList.add("GA-Coweta County");
			countyList.add("GA-Crawford County");
			countyList.add("GA-Crisp County");
			countyList.add("GA-Dade County");
			countyList.add("GA-Dawson County");
			countyList.add("GA-DeKalb County");
			countyList.add("GA-Decatur County");
			countyList.add("GA-Dodge County");
			countyList.add("GA-Dooly County");
			countyList.add("GA-Dougherty County");
			countyList.add("GA-Douglas County");
			countyList.add("GA-Early County");
			countyList.add("GA-Echols County");
			countyList.add("GA-Effingham County");
			countyList.add("GA-Elbert County");
			countyList.add("GA-Emanuel County");
			countyList.add("GA-Evans County");
			countyList.add("GA-Fannin County");
			countyList.add("GA-Fayette County");
			countyList.add("GA-Floyd County");
			countyList.add("GA-Forsyth County");
			countyList.add("GA-Franklin County");
			countyList.add("GA-Fulton County");
			countyList.add("GA-Gilmer County");
			countyList.add("GA-Glascock County");
			countyList.add("GA-Glynn County");
			countyList.add("GA-Gordon County");
			countyList.add("GA-Grady County");
			countyList.add("GA-Greene County");
			countyList.add("GA-Gwinnett County");
			countyList.add("GA-Habersham County");
			countyList.add("GA-Hall County");
			countyList.add("GA-Hancock County");
			countyList.add("GA-Haralson County");
			countyList.add("GA-Harris County");
			countyList.add("GA-Hart County");
			countyList.add("GA-Heard County");
			countyList.add("GA-Henry County");
			countyList.add("GA-Houston County");
			countyList.add("GA-Irwin County");
			countyList.add("GA-Jackson County");
			countyList.add("GA-Jasper County");
			countyList.add("GA-Jeff Davis County");
			countyList.add("GA-Jefferson County");
			countyList.add("GA-Jenkins County");
			countyList.add("GA-Johnson County");
			countyList.add("GA-Jones County");
			countyList.add("GA-Lamar County");
			countyList.add("GA-Lanier County");
			countyList.add("GA-Laurens County");
			countyList.add("GA-Lee County");
			countyList.add("GA-Liberty County");
			countyList.add("GA-Lincoln County");
			countyList.add("GA-Long County");
			countyList.add("GA-Lowndes County");
			countyList.add("GA-Lumpkin County");
			countyList.add("GA-Macon County");
			countyList.add("GA-Madison County");
			countyList.add("GA-Marion County");
			countyList.add("GA-McDuffie County");
			countyList.add("GA-McIntosh County");
			countyList.add("GA-Meriwether County");
			countyList.add("GA-Miller County");
			countyList.add("GA-Mitchell County");
			countyList.add("GA-Monroe County");
			countyList.add("GA-Montgomery County");
			countyList.add("GA-Morgan County");
			countyList.add("GA-Murray County");
			countyList.add("GA-Muscogee County");
			countyList.add("GA-Newton County");
			countyList.add("GA-Oconee County");
			countyList.add("GA-Oglethorpe County");
			countyList.add("GA-Paulding County");
			countyList.add("GA-Peach County");
			countyList.add("GA-Pickens County");
			countyList.add("GA-Pierce County");
			countyList.add("GA-Pike County");
			countyList.add("GA-Polk County");
			countyList.add("GA-Pulaski County");
			countyList.add("GA-Putnam County");
			countyList.add("GA-Quitman County");
			countyList.add("GA-Rabun County");
			countyList.add("GA-Randolph County");
			countyList.add("GA-Richmond County");
			countyList.add("GA-Rockdale County");
			countyList.add("GA-Schley County");
			countyList.add("GA-Screven County");
			countyList.add("GA-Seminole County");
			countyList.add("GA-Spalding County");
			countyList.add("GA-Stephens County");
			countyList.add("GA-Stewart County");
			countyList.add("GA-Sumter County");
			countyList.add("GA-Talbot County");
			countyList.add("GA-Taliaferro County");
			countyList.add("GA-Tattnall County");
			countyList.add("GA-Taylor County");
			countyList.add("GA-Telfair County");
			countyList.add("GA-Terrell County");
			countyList.add("GA-Thomas County");
			countyList.add("GA-Tift County");
			countyList.add("GA-Toombs County");
			countyList.add("GA-Towns County");
			countyList.add("GA-Treutlen County");
			countyList.add("GA-Troup County");
			countyList.add("GA-Turner County");
			countyList.add("GA-Twiggs County");
			countyList.add("GA-Union County");
			countyList.add("GA-Upson County");
			countyList.add("GA-Walker County");
			countyList.add("GA-Walton County");
			countyList.add("GA-Ware County");
			countyList.add("GA-Warren County");
			countyList.add("GA-Washington County");
			countyList.add("GA-Wayne County");
			countyList.add("GA-Webster County");
			countyList.add("GA-Wheeler County");
			countyList.add("GA-White County");
			countyList.add("GA-Whitfield County");
			countyList.add("GA-Wilcox County");
			countyList.add("GA-Wilkes County");
			countyList.add("GA-Wilkinson County");
			countyList.add("GA-Worth County");
		} else if(stateCode.equals("GU")){
			countyList.add("GU-Federated States Of Micro");
			countyList.add("GU-Guam");
			countyList.add("GU-Marshall Islands");
			countyList.add("GU-Palau");
		} else if(stateCode.equals("HI")){
			countyList.add("HI-Hawaii County");
			countyList.add("HI-Honolulu County");
			countyList.add("HI-Kalawao County");
			countyList.add("HI-Kauai County");
			countyList.add("HI-Maui County");
		} else if(stateCode.equals("IA")){
			countyList.add("IA-Adair County");
			countyList.add("IA-Adams County");
			countyList.add("IA-Allamakee County");
			countyList.add("IA-Appanoose County");
			countyList.add("IA-Audubon County");
			countyList.add("IA-Benton County");
			countyList.add("IA-Black Hawk County");
			countyList.add("IA-Boone County");
			countyList.add("IA-Bremer County");
			countyList.add("IA-Buchanan County");
			countyList.add("IA-Buena Vista County");
			countyList.add("IA-Butler County");
			countyList.add("IA-Calhoun County");
			countyList.add("IA-Carroll County");
			countyList.add("IA-Cass County");
			countyList.add("IA-Cedar County");
			countyList.add("IA-Cerro Gordo County");
			countyList.add("IA-Cherokee County");
			countyList.add("IA-Chickasaw County");
			countyList.add("IA-Clarke County");
			countyList.add("IA-Clay County");
			countyList.add("IA-Clayton County");
			countyList.add("IA-Clinton County");
			countyList.add("IA-Crawford County");
			countyList.add("IA-Dallas County");
			countyList.add("IA-Davis County");
			countyList.add("IA-Decatur County");
			countyList.add("IA-Delaware County");
			countyList.add("IA-Des Moines County");
			countyList.add("IA-Dickinson County");
			countyList.add("IA-Dubuque County");
			countyList.add("IA-Emmet County");
			countyList.add("IA-Fayette County");
			countyList.add("IA-Floyd County");
			countyList.add("IA-Franklin County");
			countyList.add("IA-Fremont County");
			countyList.add("IA-Greene County");
			countyList.add("IA-Grundy County");
			countyList.add("IA-Guthrie County");
			countyList.add("IA-Hamilton County");
			countyList.add("IA-Hancock County");
			countyList.add("IA-Hardin County");
			countyList.add("IA-Harrison County");
			countyList.add("IA-Henry County");
			countyList.add("IA-Howard County");
			countyList.add("IA-Humboldt County");
			countyList.add("IA-Ida County");
			countyList.add("IA-Iowa County");
			countyList.add("IA-Jackson County");
			countyList.add("IA-Jasper County");
			countyList.add("IA-Jefferson County");
			countyList.add("IA-Johnson County");
			countyList.add("IA-Jones County");
			countyList.add("IA-Keokuk County");
			countyList.add("IA-Kossuth County");
			countyList.add("IA-Lee County");
			countyList.add("IA-Linn County");
			countyList.add("IA-Louisa County");
			countyList.add("IA-Lucas County");
			countyList.add("IA-Lyon County");
			countyList.add("IA-Madison County");
			countyList.add("IA-Mahaska County");
			countyList.add("IA-Marion County");
			countyList.add("IA-Marshall County");
			countyList.add("IA-Mills County");
			countyList.add("IA-Mitchell County");
			countyList.add("IA-Monona County");
			countyList.add("IA-Monroe County");
			countyList.add("IA-Montgomery County");
			countyList.add("IA-Muscatine County");
			countyList.add("IA-O'Brien County");
			countyList.add("IA-Osceola County");
			countyList.add("IA-Page County");
			countyList.add("IA-Palo Alto County");
			countyList.add("IA-Plymouth County");
			countyList.add("IA-Pocahontas County");
			countyList.add("IA-Polk County");
			countyList.add("IA-Pottawattamie County");
			countyList.add("IA-Poweshiek County");
			countyList.add("IA-Ringgold County");
			countyList.add("IA-Sac County");
			countyList.add("IA-Scott County");
			countyList.add("IA-Shelby County");
			countyList.add("IA-Sioux County");
			countyList.add("IA-Story County");
			countyList.add("IA-Tama County");
			countyList.add("IA-Taylor County");
			countyList.add("IA-Union County");
			countyList.add("IA-Van Buren County");
			countyList.add("IA-Wapello County");
			countyList.add("IA-Warren County");
			countyList.add("IA-Washington County");
			countyList.add("IA-Wayne County");
			countyList.add("IA-Webster County");
			countyList.add("IA-Winnebago County");
			countyList.add("IA-Winneshiek County");
			countyList.add("IA-Woodbury County");
			countyList.add("IA-Worth County");
			countyList.add("IA-Wright County");
		} else if(stateCode.equals("ID")){
			countyList.add("ID-Ada County");
			countyList.add("ID-Adams County");
			countyList.add("ID-Bannock County");
			countyList.add("ID-Bear Lake County");
			countyList.add("ID-Benewah County");
			countyList.add("ID-Bingham County");
			countyList.add("ID-Blaine County");
			countyList.add("ID-Boise County");
			countyList.add("ID-Bonner County");
			countyList.add("ID-Bonneville County");
			countyList.add("ID-Boundary County");
			countyList.add("ID-Butte County");
			countyList.add("ID-Camas County");
			countyList.add("ID-Canyon County");
			countyList.add("ID-Caribou County");
			countyList.add("ID-Cassia County");
			countyList.add("ID-Clark County");
			countyList.add("ID-Clearwater County");
			countyList.add("ID-Custer County");
			countyList.add("ID-Elmore County");
			countyList.add("ID-Franklin County");
			countyList.add("ID-Fremont County");
			countyList.add("ID-Gem County");
			countyList.add("ID-Gooding County");
			countyList.add("ID-Idaho County");
			countyList.add("ID-Jefferson County");
			countyList.add("ID-Jerome County");
			countyList.add("ID-Kootenai County");
			countyList.add("ID-Latah County");
			countyList.add("ID-Lemhi County");
			countyList.add("ID-Lewis County");
			countyList.add("ID-Lincoln County");
			countyList.add("ID-Madison County");
			countyList.add("ID-Minidoka County");
			countyList.add("ID-Nez Perce County");
			countyList.add("ID-Oneida County");
			countyList.add("ID-Owyhee County");
			countyList.add("ID-Payette County");
			countyList.add("ID-Power County");
			countyList.add("ID-Shoshone County");
			countyList.add("ID-Teton County");
			countyList.add("ID-Twin Falls County");
			countyList.add("ID-Valley County");
			countyList.add("ID-Washington County");
		} else if(stateCode.equals("IL")){
			countyList.add("IL-Adams County");
			countyList.add("IL-Alexander County");
			countyList.add("IL-Bond County");
			countyList.add("IL-Boone County");
			countyList.add("IL-Brown County");
			countyList.add("IL-Bureau County");
			countyList.add("IL-Calhoun County");
			countyList.add("IL-Carroll County");
			countyList.add("IL-Cass County");
			countyList.add("IL-Champaign County");
			countyList.add("IL-Christian County");
			countyList.add("IL-Clark County");
			countyList.add("IL-Clay County");
			countyList.add("IL-Clinton County");
			countyList.add("IL-Coles County");
			countyList.add("IL-Cook County");
			countyList.add("IL-Crawford County");
			countyList.add("IL-Cumberland County");
			countyList.add("IL-Dekalb County");
			countyList.add("IL-Dewitt County");
			countyList.add("IL-Douglas County");
			countyList.add("IL-Dupage County");
			countyList.add("IL-Edgar County");
			countyList.add("IL-Edwards County");
			countyList.add("IL-Effingham County");
			countyList.add("IL-Fayette County");
			countyList.add("IL-Ford County");
			countyList.add("IL-Franklin County");
			countyList.add("IL-Fulton County");
			countyList.add("IL-Gallatin County");
			countyList.add("IL-Greene County");
			countyList.add("IL-Grundy County");
			countyList.add("IL-Hamilton County");
			countyList.add("IL-Hancock County");
			countyList.add("IL-Hardin County");
			countyList.add("IL-Henderson County");
			countyList.add("IL-Henry County");
			countyList.add("IL-Iroquois County");
			countyList.add("IL-Jackson County");
			countyList.add("IL-Jasper County");
			countyList.add("IL-Jefferson County");
			countyList.add("IL-Jersey County");
			countyList.add("IL-Jo Daviess County");
			countyList.add("IL-Johnson County");
			countyList.add("IL-Kane County");
			countyList.add("IL-Kankakee County");
			countyList.add("IL-Kendall County");
			countyList.add("IL-Knox County");
			countyList.add("IL-La Salle County");
			countyList.add("IL-Lake County");
			countyList.add("IL-Lawrence County");
			countyList.add("IL-Lee County");
			countyList.add("IL-Livingston County");
			countyList.add("IL-Logan County");
			countyList.add("IL-Macon County");
			countyList.add("IL-Macoupin County");
			countyList.add("IL-Madison County");
			countyList.add("IL-Marion County");
			countyList.add("IL-Marshall County");
			countyList.add("IL-Mason County");
			countyList.add("IL-Massac County");
			countyList.add("IL-McDonough County");
			countyList.add("IL-McHenry County");
			countyList.add("IL-McLean County");
			countyList.add("IL-Menard County");
			countyList.add("IL-Mercer County");
			countyList.add("IL-Monroe County");
			countyList.add("IL-Montgomery County");
			countyList.add("IL-Morgan County");
			countyList.add("IL-Moultrie County");
			countyList.add("IL-Ogle County");
			countyList.add("IL-Peoria County");
			countyList.add("IL-Perry County");
			countyList.add("IL-Piatt County");
			countyList.add("IL-Pike County");
			countyList.add("IL-Pope County");
			countyList.add("IL-Pulaski County");
			countyList.add("IL-Putnam County");
			countyList.add("IL-Randolph County");
			countyList.add("IL-Richland County");
			countyList.add("IL-Rock Island County");
			countyList.add("IL-Saint Clair County");
			countyList.add("IL-Saline County");
			countyList.add("IL-Sangamon County");
			countyList.add("IL-Schuyler County");
			countyList.add("IL-Scott County");
			countyList.add("IL-Shelby County");
			countyList.add("IL-Stark County");
			countyList.add("IL-Stephenson County");
			countyList.add("IL-Tazewell County");
			countyList.add("IL-Union County");
			countyList.add("IL-Vermilion County");
			countyList.add("IL-Wabash County");
			countyList.add("IL-Warren County");
			countyList.add("IL-Washington County");
			countyList.add("IL-Wayne County");
			countyList.add("IL-White County");
			countyList.add("IL-Whiteside County");
			countyList.add("IL-Will County");
			countyList.add("IL-Williamson County");
			countyList.add("IL-Winnebago County");
			countyList.add("IL-Woodford County");
		} else if(stateCode.equals("IN")){
			countyList.add("IN-Adams County");
			countyList.add("IN-Allen County");
			countyList.add("IN-Bartholomew County");
			countyList.add("IN-Benton County");
			countyList.add("IN-Blackford County");
			countyList.add("IN-Boone County");
			countyList.add("IN-Brown County");
			countyList.add("IN-Carroll County");
			countyList.add("IN-Cass County");
			countyList.add("IN-Clark County");
			countyList.add("IN-Clay County");
			countyList.add("IN-Clinton County");
			countyList.add("IN-Crawford County");
			countyList.add("IN-Daviess County");
			countyList.add("IN-De Kalb County");
			countyList.add("IN-Dearborn County");
			countyList.add("IN-Decatur County");
			countyList.add("IN-Delaware County");
			countyList.add("IN-Dubois County");
			countyList.add("IN-Elkhart County");
			countyList.add("IN-Fayette County");
			countyList.add("IN-Floyd County");
			countyList.add("IN-Fountain County");
			countyList.add("IN-Franklin County");
			countyList.add("IN-Fulton County");
			countyList.add("IN-Gibson County");
			countyList.add("IN-Grant County");
			countyList.add("IN-Greene County");
			countyList.add("IN-Hamilton County");
			countyList.add("IN-Hancock County");
			countyList.add("IN-Harrison County");
			countyList.add("IN-Hendricks County");
			countyList.add("IN-Henry County");
			countyList.add("IN-Howard County");
			countyList.add("IN-Huntington County");
			countyList.add("IN-Jackson County");
			countyList.add("IN-Jasper County");
			countyList.add("IN-Jay County");
			countyList.add("IN-Jefferson County");
			countyList.add("IN-Jennings County");
			countyList.add("IN-Johnson County");
			countyList.add("IN-Knox County");
			countyList.add("IN-Kosciusko County");
			countyList.add("IN-La Porte County");
			countyList.add("IN-LaGrange County");
			countyList.add("IN-Lake County");
			countyList.add("IN-Lawrence County");
			countyList.add("IN-Madison County");
			countyList.add("IN-Marion County");
			countyList.add("IN-Marshall County");
			countyList.add("IN-Martin County");
			countyList.add("IN-Miami County");
			countyList.add("IN-Monroe County");
			countyList.add("IN-Montgomery County");
			countyList.add("IN-Morgan County");
			countyList.add("IN-Newton County");
			countyList.add("IN-Noble County");
			countyList.add("IN-Ohio County");
			countyList.add("IN-Orange County");
			countyList.add("IN-Owen County");
			countyList.add("IN-Parke County");
			countyList.add("IN-Perry County");
			countyList.add("IN-Pike County");
			countyList.add("IN-Porter County");
			countyList.add("IN-Posey County");
			countyList.add("IN-Pulaski County");
			countyList.add("IN-Putnam County");
			countyList.add("IN-Randolph County");
			countyList.add("IN-Ripley County");
			countyList.add("IN-Rush County");
			countyList.add("IN-Scott County");
			countyList.add("IN-Shelby County");
			countyList.add("IN-Spencer County");
			countyList.add("IN-St Joseph County");
			countyList.add("IN-Starke County");
			countyList.add("IN-Steuben County");
			countyList.add("IN-Sullivan County");
			countyList.add("IN-Switzerland County");
			countyList.add("IN-Tippecanoe County");
			countyList.add("IN-Tipton County");
			countyList.add("IN-Union County");
			countyList.add("IN-Vanderburgh County");
			countyList.add("IN-Vermillion County");
			countyList.add("IN-Vigo County");
			countyList.add("IN-Wabash County");
			countyList.add("IN-Warren County");
			countyList.add("IN-Warrick County");
			countyList.add("IN-Washington County");
			countyList.add("IN-Wayne County");
			countyList.add("IN-Wells County");
			countyList.add("IN-White County");
			countyList.add("IN-Whitley County");
		} else if(stateCode.equals("KS")){
			countyList.add("KS-Allen County");
			countyList.add("KS-Anderson County");
			countyList.add("KS-Atchison County");
			countyList.add("KS-Barber County");
			countyList.add("KS-Barton County");
			countyList.add("KS-Bourbon County");
			countyList.add("KS-Brown County");
			countyList.add("KS-Butler County");
			countyList.add("KS-Chase County");
			countyList.add("KS-Chautauqua County");
			countyList.add("KS-Cherokee County");
			countyList.add("KS-Cheyenne County");
			countyList.add("KS-Clark County");
			countyList.add("KS-Clay County");
			countyList.add("KS-Cloud County");
			countyList.add("KS-Coffey County");
			countyList.add("KS-Comanche County");
			countyList.add("KS-Cowley County");
			countyList.add("KS-Crawford County");
			countyList.add("KS-Decatur County");
			countyList.add("KS-Dickinson County");
			countyList.add("KS-Doniphan County");
			countyList.add("KS-Douglas County");
			countyList.add("KS-Edwards County");
			countyList.add("KS-Elk County");
			countyList.add("KS-Ellis County");
			countyList.add("KS-Ellsworth County");
			countyList.add("KS-Finney County");
			countyList.add("KS-Ford County");
			countyList.add("KS-Franklin County");
			countyList.add("KS-Geary County");
			countyList.add("KS-Gove County");
			countyList.add("KS-Graham County");
			countyList.add("KS-Grant County");
			countyList.add("KS-Gray County");
			countyList.add("KS-Greeley County");
			countyList.add("KS-Greenwood County");
			countyList.add("KS-Hamilton County");
			countyList.add("KS-Harper County");
			countyList.add("KS-Harvey County");
			countyList.add("KS-Haskell County");
			countyList.add("KS-Hodgeman County");
			countyList.add("KS-Jackson County");
			countyList.add("KS-Jefferson County");
			countyList.add("KS-Jewell County");
			countyList.add("KS-Johnson County");
			countyList.add("KS-Kearny County");
			countyList.add("KS-Kingman County");
			countyList.add("KS-Kiowa County");
			countyList.add("KS-Labette County");
			countyList.add("KS-Lane County");
			countyList.add("KS-Leavenworth County");
			countyList.add("KS-Lincoln County");
			countyList.add("KS-Linn County");
			countyList.add("KS-Logan County");
			countyList.add("KS-Lyon County");
			countyList.add("KS-Marion County");
			countyList.add("KS-Marshall County");
			countyList.add("KS-McPherson County");
			countyList.add("KS-Meade County");
			countyList.add("KS-Miami County");
			countyList.add("KS-Mitchell County");
			countyList.add("KS-Montgomery County");
			countyList.add("KS-Morris County");
			countyList.add("KS-Morton County");
			countyList.add("KS-Nemaha County");
			countyList.add("KS-Neosho County");
			countyList.add("KS-Ness County");
			countyList.add("KS-Norton County");
			countyList.add("KS-Osage County");
			countyList.add("KS-Osborne County");
			countyList.add("KS-Ottawa County");
			countyList.add("KS-Pawnee County");
			countyList.add("KS-Phillips County");
			countyList.add("KS-Pottawatomie County");
			countyList.add("KS-Pratt County");
			countyList.add("KS-Rawlins County");
			countyList.add("KS-Reno County");
			countyList.add("KS-Republic County");
			countyList.add("KS-Rice County");
			countyList.add("KS-Riley County");
			countyList.add("KS-Rooks County");
			countyList.add("KS-Rush County");
			countyList.add("KS-Russell County");
			countyList.add("KS-Saline County");
			countyList.add("KS-Scott County");
			countyList.add("KS-Sedgwick County");
			countyList.add("KS-Seward County");
			countyList.add("KS-Shawnee County");
			countyList.add("KS-Sheridan County");
			countyList.add("KS-Sherman County");
			countyList.add("KS-Smith County");
			countyList.add("KS-Stafford County");
			countyList.add("KS-Stanton County");
			countyList.add("KS-Stevens County");
			countyList.add("KS-Sumner County");
			countyList.add("KS-Thomas County");
			countyList.add("KS-Trego County");
			countyList.add("KS-Wabaunsee County");
			countyList.add("KS-Wallace County");
			countyList.add("KS-Washington County");
			countyList.add("KS-Wichita County");
			countyList.add("KS-Wilson County");
			countyList.add("KS-Woodson County");
			countyList.add("KS-Wyandotte County");
		} else if(stateCode.equals("KY")){
			countyList.add("KY-Adair County");
			countyList.add("KY-Allen County");
			countyList.add("KY-Anderson County");
			countyList.add("KY-Ballard County");
			countyList.add("KY-Barren County");
			countyList.add("KY-Bath County");
			countyList.add("KY-Bell County");
			countyList.add("KY-Boone County");
			countyList.add("KY-Bourbon County");
			countyList.add("KY-Boyd County");
			countyList.add("KY-Boyle County");
			countyList.add("KY-Bracken County");
			countyList.add("KY-Breathitt County");
			countyList.add("KY-Breckinridge County");
			countyList.add("KY-Bullitt County");
			countyList.add("KY-Butler County");
			countyList.add("KY-Caldwell County");
			countyList.add("KY-Calloway County");
			countyList.add("KY-Campbell County");
			countyList.add("KY-Carlisle County");
			countyList.add("KY-Carroll County");
			countyList.add("KY-Carter County");
			countyList.add("KY-Casey County");
			countyList.add("KY-Christian County");
			countyList.add("KY-Clark County");
			countyList.add("KY-Clay County");
			countyList.add("KY-Clinton County");
			countyList.add("KY-Crittenden County");
			countyList.add("KY-Cumberland County");
			countyList.add("KY-Daviess County");
			countyList.add("KY-Edmonson County");
			countyList.add("KY-Elliott County");
			countyList.add("KY-Estill County");
			countyList.add("KY-Fayette County");
			countyList.add("KY-Fleming County");
			countyList.add("KY-Floyd County");
			countyList.add("KY-Franklin County");
			countyList.add("KY-Fulton County");
			countyList.add("KY-Gallatin County");
			countyList.add("KY-Garrard County");
			countyList.add("KY-Grant County");
			countyList.add("KY-Graves County");
			countyList.add("KY-Grayson County");
			countyList.add("KY-Green County");
			countyList.add("KY-Greenup County");
			countyList.add("KY-Hancock County");
			countyList.add("KY-Hardin County");
			countyList.add("KY-Harlan County");
			countyList.add("KY-Harrison County");
			countyList.add("KY-Hart County");
			countyList.add("KY-Henderson County");
			countyList.add("KY-Henry County");
			countyList.add("KY-Hickman County");
			countyList.add("KY-Hopkins County");
			countyList.add("KY-Jackson County");
			countyList.add("KY-Jefferson County");
			countyList.add("KY-Jessamine County");
			countyList.add("KY-Johnson County");
			countyList.add("KY-Kenton County");
			countyList.add("KY-Knott County");
			countyList.add("KY-Knox County");
			countyList.add("KY-Larue County");
			countyList.add("KY-Laurel County");
			countyList.add("KY-Lawrence County");
			countyList.add("KY-Lee County");
			countyList.add("KY-Leslie County");
			countyList.add("KY-Letcher County");
			countyList.add("KY-Lewis County");
			countyList.add("KY-Lincoln County");
			countyList.add("KY-Livingston County");
			countyList.add("KY-Logan County");
			countyList.add("KY-Lyon County");
			countyList.add("KY-Madison County");
			countyList.add("KY-Magoffin County");
			countyList.add("KY-Marion County");
			countyList.add("KY-Marshall County");
			countyList.add("KY-Martin County");
			countyList.add("KY-Mason County");
			countyList.add("KY-McCracken County");
			countyList.add("KY-McCreary County");
			countyList.add("KY-McLean County");
			countyList.add("KY-Meade County");
			countyList.add("KY-Menifee County");
			countyList.add("KY-Mercer County");
			countyList.add("KY-Metcalfe County");
			countyList.add("KY-Monroe County");
			countyList.add("KY-Montgomery County");
			countyList.add("KY-Morgan County");
			countyList.add("KY-Muhlenberg County");
			countyList.add("KY-Nelson County");
			countyList.add("KY-Nicholas County");
			countyList.add("KY-Ohio County");
			countyList.add("KY-Oldham County");
			countyList.add("KY-Owen County");
			countyList.add("KY-Owsley County");
			countyList.add("KY-Pendleton County");
			countyList.add("KY-Perry County");
			countyList.add("KY-Pike County");
			countyList.add("KY-Powell County");
			countyList.add("KY-Pulaski County");
			countyList.add("KY-Robertson County");
			countyList.add("KY-Rockcastle County");
			countyList.add("KY-Rowan County");
			countyList.add("KY-Russell County");
			countyList.add("KY-Scott County");
			countyList.add("KY-Shelby County");
			countyList.add("KY-Simpson County");
			countyList.add("KY-Spencer County");
			countyList.add("KY-Taylor County");
			countyList.add("KY-Todd County");
			countyList.add("KY-Trigg County");
			countyList.add("KY-Trimble County");
			countyList.add("KY-Union County");
			countyList.add("KY-Warren County");
			countyList.add("KY-Washington County");
			countyList.add("KY-Wayne County");
			countyList.add("KY-Webster County");
			countyList.add("KY-Whitley County");
			countyList.add("KY-Wolfe County");
			countyList.add("KY-Woodford County");
		} else if(stateCode.equals("LA")){
			countyList.add("LA-Acadia Parish");
			countyList.add("LA-Allen Parish");
			countyList.add("LA-Ascension Parish");
			countyList.add("LA-Assumption Parish");
			countyList.add("LA-Avoyelles Parish");
			countyList.add("LA-Beauregard Parish");
			countyList.add("LA-Bienville Parish");
			countyList.add("LA-Bossier Parish");
			countyList.add("LA-Caddo Parish");
			countyList.add("LA-Calcasieu Parish");
			countyList.add("LA-Caldwell Parish");
			countyList.add("LA-Cameron Parish");
			countyList.add("LA-Catahoula Parish");
			countyList.add("LA-Claiborne Parish");
			countyList.add("LA-Concordia Parish");
			countyList.add("LA-De Soto Parish");
			countyList.add("LA-East Baton Rouge Parish");
			countyList.add("LA-East Carroll Parish");
			countyList.add("LA-East Feliciana Parish");
			countyList.add("LA-Evangeline Parish");
			countyList.add("LA-Franklin Parish");
			countyList.add("LA-Grant Parish");
			countyList.add("LA-Iberia Parish");
			countyList.add("LA-Iberville Parish");
			countyList.add("LA-Jackson Parish");
			countyList.add("LA-Jefferson Davis Parish");
			countyList.add("LA-Jefferson Parish");
			countyList.add("LA-La Salle Parish");
			countyList.add("LA-Lafayette Parish");
			countyList.add("LA-Lafourche Parish");
			countyList.add("LA-Lincoln Parish");
			countyList.add("LA-Livingston Parish");
			countyList.add("LA-Madison Parish");
			countyList.add("LA-Morehouse Parish");
			countyList.add("LA-Natchitoches Parish");
			countyList.add("LA-Orleans Parish");
			countyList.add("LA-Ouachita Parish");
			countyList.add("LA-Plaquemines Parish");
			countyList.add("LA-Pointe Coupee Parish");
			countyList.add("LA-Rapides Parish");
			countyList.add("LA-Red River Parish");
			countyList.add("LA-Richland Parish");
			countyList.add("LA-Sabine Parish");
			countyList.add("LA-Saint Bernard Parish");
			countyList.add("LA-Saint Charles Parish");
			countyList.add("LA-Saint Helena Parish");
			countyList.add("LA-Saint James Parish");
			countyList.add("LA-Saint Landry Parish");
			countyList.add("LA-Saint Martin Parish");
			countyList.add("LA-Saint Mary Parish");
			countyList.add("LA-Saint Tammany Parish");
			countyList.add("LA-St John The Baptist Parish");
			countyList.add("LA-Tangipahoa Parish");
			countyList.add("LA-Tensas Parish");
			countyList.add("LA-Terrebonne Parish");
			countyList.add("LA-Union Parish");
			countyList.add("LA-Vermilion Parish");
			countyList.add("LA-Vernon Parish");
			countyList.add("LA-Washington Parish");
			countyList.add("LA-Webster Parish");
			countyList.add("LA-West Baton Rouge Parish");
			countyList.add("LA-West Carroll Parish");
			countyList.add("LA-West Feliciana Parish");
			countyList.add("LA-Winn Parish");
		} else if(stateCode.equals("MA")){
			countyList.add("MA-Barnstable County");
			countyList.add("MA-Berkshire County");
			countyList.add("MA-Bristol County");
			countyList.add("MA-Dukes County");
			countyList.add("MA-Essex County");
			countyList.add("MA-Franklin County");
			countyList.add("MA-Hampden County");
			countyList.add("MA-Hampshire County");
			countyList.add("MA-Middlesex County");
			countyList.add("MA-Nantucket County");
			countyList.add("MA-Norfolk County");
			countyList.add("MA-Plymouth County");
			countyList.add("MA-Suffolk County");
			countyList.add("MA-Worcester County");
		} else if(stateCode.equals("MD")){
			countyList.add("MD-Allegany County");
			countyList.add("MD-Anne Arundel County");
			countyList.add("MD-Baltimore City");
			countyList.add("MD-Baltimore County");
			countyList.add("MD-Calvert County");
			countyList.add("MD-Caroline County");
			countyList.add("MD-Carroll County");
			countyList.add("MD-Cecil County");
			countyList.add("MD-Charles County");
			countyList.add("MD-Dorchester County");
			countyList.add("MD-Frederick County");
			countyList.add("MD-Garrett County");
			countyList.add("MD-Harford County");
			countyList.add("MD-Howard County");
			countyList.add("MD-Kent County");
			countyList.add("MD-Montgomery County");
			countyList.add("MD-Prince George's County");
			countyList.add("MD-Queen Anne's County");
			countyList.add("MD-Saint Mary's County");
			countyList.add("MD-Somerset County");
			countyList.add("MD-Talbot County");
			countyList.add("MD-Washington County");
			countyList.add("MD-Wicomico County");
			countyList.add("MD-Worcester County");
		} else if(stateCode.equals("ME")){
			countyList.add("ME-Androscoggin County");
			countyList.add("ME-Aroostook County");
			countyList.add("ME-Cumberland County");
			countyList.add("ME-Franklin County");
			countyList.add("ME-Hancock County");
			countyList.add("ME-Kennebec County");
			countyList.add("ME-Knox County");
			countyList.add("ME-Lincoln County");
			countyList.add("ME-Oxford County");
			countyList.add("ME-Penobscot County");
			countyList.add("ME-Piscataquis County");
			countyList.add("ME-Sagadahoc County");
			countyList.add("ME-Somerset County");
			countyList.add("ME-Waldo County");
			countyList.add("ME-Washington County");
			countyList.add("ME-York County");
		} else if(stateCode.equals("MH")){
			countyList.add("MH-Marshall Islands");
		} else if(stateCode.equals("MI")){
			countyList.add("MI-Alcona County");
			countyList.add("MI-Alger County");
			countyList.add("MI-Allegan County");
			countyList.add("MI-Alpena County");
			countyList.add("MI-Antrim County");
			countyList.add("MI-Arenac County");
			countyList.add("MI-Baraga County");
			countyList.add("MI-Barry County");
			countyList.add("MI-Bay County");
			countyList.add("MI-Benzie County");
			countyList.add("MI-Berrien County");
			countyList.add("MI-Branch County");
			countyList.add("MI-Calhoun County");
			countyList.add("MI-Cass County");
			countyList.add("MI-Charlevoix County");
			countyList.add("MI-Cheboygan County");
			countyList.add("MI-Chippewa County");
			countyList.add("MI-Clare County");
			countyList.add("MI-Clinton County");
			countyList.add("MI-Crawford County");
			countyList.add("MI-Delta County");
			countyList.add("MI-Dickinson County");
			countyList.add("MI-Eaton County");
			countyList.add("MI-Emmet County");
			countyList.add("MI-Genesee County");
			countyList.add("MI-Gladwin County");
			countyList.add("MI-Gogebic County");
			countyList.add("MI-Grand Traverse County");
			countyList.add("MI-Gratiot County");
			countyList.add("MI-Hillsdale County");
			countyList.add("MI-Houghton County");
			countyList.add("MI-Huron County");
			countyList.add("MI-Ingham County");
			countyList.add("MI-Ionia County");
			countyList.add("MI-Iosco County");
			countyList.add("MI-Iron County");
			countyList.add("MI-Isabella County");
			countyList.add("MI-Jackson County");
			countyList.add("MI-Kalamazoo County");
			countyList.add("MI-Kalkaska County");
			countyList.add("MI-Kent County");
			countyList.add("MI-Keweenaw County");
			countyList.add("MI-Lake County");
			countyList.add("MI-Lapeer County");
			countyList.add("MI-Leelanau County");
			countyList.add("MI-Lenawee County");
			countyList.add("MI-Livingston County");
			countyList.add("MI-Luce County");
			countyList.add("MI-Mackinac County");
			countyList.add("MI-Macomb County");
			countyList.add("MI-Manistee County");
			countyList.add("MI-Marquette County");
			countyList.add("MI-Mason County");
			countyList.add("MI-Mecosta County");
			countyList.add("MI-Menominee County");
			countyList.add("MI-Midland County");
			countyList.add("MI-Missaukee County");
			countyList.add("MI-Monroe County");
			countyList.add("MI-Montcalm County");
			countyList.add("MI-Montmorency County");
			countyList.add("MI-Muskegon County");
			countyList.add("MI-Newaygo County");
			countyList.add("MI-Oakland County");
			countyList.add("MI-Oceana County");
			countyList.add("MI-Ogemaw County");
			countyList.add("MI-Ontonagon County");
			countyList.add("MI-Osceola County");
			countyList.add("MI-Oscoda County");
			countyList.add("MI-Otsego County");
			countyList.add("MI-Ottawa County");
			countyList.add("MI-Presque Isle County");
			countyList.add("MI-Roscommon County");
			countyList.add("MI-Saginaw County");
			countyList.add("MI-Saint Clair County");
			countyList.add("MI-Saint Joseph County");
			countyList.add("MI-Sanilac County");
			countyList.add("MI-Schoolcraft County");
			countyList.add("MI-Shiawassee County");
			countyList.add("MI-Tuscola County");
			countyList.add("MI-Van Buren County");
			countyList.add("MI-Washtenaw County");
			countyList.add("MI-Wayne County");
			countyList.add("MI-Wexford County");
		} else if(stateCode.equals("MN")){
			countyList.add("MN-Aitkin County");
			countyList.add("MN-Anoka County");
			countyList.add("MN-Becker County");
			countyList.add("MN-Beltrami County");
			countyList.add("MN-Benton County");
			countyList.add("MN-Big Stone County");
			countyList.add("MN-Blue Earth County");
			countyList.add("MN-Brown County");
			countyList.add("MN-Carlton County");
			countyList.add("MN-Carver County");
			countyList.add("MN-Cass County");
			countyList.add("MN-Chippewa County");
			countyList.add("MN-Chisago County");
			countyList.add("MN-Clay County");
			countyList.add("MN-Clearwater County");
			countyList.add("MN-Cook County");
			countyList.add("MN-Cottonwood County");
			countyList.add("MN-Crow Wing County");
			countyList.add("MN-Dakota County");
			countyList.add("MN-Dodge County");
			countyList.add("MN-Douglas County");
			countyList.add("MN-Faribault County");
			countyList.add("MN-Fillmore County");
			countyList.add("MN-Freeborn County");
			countyList.add("MN-Goodhue County");
			countyList.add("MN-Grant County");
			countyList.add("MN-Hennepin County");
			countyList.add("MN-Houston County");
			countyList.add("MN-Hubbard County");
			countyList.add("MN-Isanti County");
			countyList.add("MN-Itasca County");
			countyList.add("MN-Jackson County");
			countyList.add("MN-Kanabec County");
			countyList.add("MN-Kandiyohi County");
			countyList.add("MN-Kittson County");
			countyList.add("MN-Koochiching County");
			countyList.add("MN-Lac Qui Parle County");
			countyList.add("MN-Lake County");
			countyList.add("MN-Lake Of The Woods County");
			countyList.add("MN-Le Sueur County");
			countyList.add("MN-Lincoln County");
			countyList.add("MN-Lyon County");
			countyList.add("MN-Mahnomen County");
			countyList.add("MN-Marshall County");
			countyList.add("MN-Martin County");
			countyList.add("MN-McLeod County");
			countyList.add("MN-Meeker County");
			countyList.add("MN-Mille Lacs County");
			countyList.add("MN-Morrison County");
			countyList.add("MN-Mower County");
			countyList.add("MN-Murray County");
			countyList.add("MN-Nicollet County");
			countyList.add("MN-Nobles County");
			countyList.add("MN-Norman County");
			countyList.add("MN-Olmsted County");
			countyList.add("MN-Otter Tail County");
			countyList.add("MN-Pennington County");
			countyList.add("MN-Pine County");
			countyList.add("MN-Pipestone County");
			countyList.add("MN-Polk County");
			countyList.add("MN-Pope County");
			countyList.add("MN-Ramsey County");
			countyList.add("MN-Red Lake County");
			countyList.add("MN-Redwood County");
			countyList.add("MN-Renville County");
			countyList.add("MN-Rice County");
			countyList.add("MN-Rock County");
			countyList.add("MN-Roseau County");
			countyList.add("MN-Saint Louis County");
			countyList.add("MN-Scott County");
			countyList.add("MN-Sherburne County");
			countyList.add("MN-Sibley County");
			countyList.add("MN-Stearns County");
			countyList.add("MN-Steele County");
			countyList.add("MN-Stevens County");
			countyList.add("MN-Swift County");
			countyList.add("MN-Todd County");
			countyList.add("MN-Traverse County");
			countyList.add("MN-Wabasha County");
			countyList.add("MN-Wadena County");
			countyList.add("MN-Waseca County");
			countyList.add("MN-Washington County");
			countyList.add("MN-Watonwan County");
			countyList.add("MN-Wilkin County");
			countyList.add("MN-Winona County");
			countyList.add("MN-Wright County");
			countyList.add("MN-Yellow Medicine County");
		} else if(stateCode.equals("MO")){
			countyList.add("MO-Adair County");
			countyList.add("MO-Andrew County");
			countyList.add("MO-Atchison County");
			countyList.add("MO-Audrain County");
			countyList.add("MO-Barry County");
			countyList.add("MO-Barton County");
			countyList.add("MO-Bates County");
			countyList.add("MO-Benton County");
			countyList.add("MO-Bollinger County");
			countyList.add("MO-Boone County");
			countyList.add("MO-Buchanan County");
			countyList.add("MO-Butler County");
			countyList.add("MO-Caldwell County");
			countyList.add("MO-Callaway County");
			countyList.add("MO-Camden County");
			countyList.add("MO-Cape Girardeau County");
			countyList.add("MO-Carroll County");
			countyList.add("MO-Carter County");
			countyList.add("MO-Cass County");
			countyList.add("MO-Cedar County");
			countyList.add("MO-Chariton County");
			countyList.add("MO-Christian County");
			countyList.add("MO-Clark County");
			countyList.add("MO-Clay County");
			countyList.add("MO-Clinton County");
			countyList.add("MO-Cole County");
			countyList.add("MO-Cooper County");
			countyList.add("MO-Crawford County");
			countyList.add("MO-Dade County");
			countyList.add("MO-Dallas County");
			countyList.add("MO-Daviess County");
			countyList.add("MO-Dekalb County");
			countyList.add("MO-Dent County");
			countyList.add("MO-Douglas County");
			countyList.add("MO-Dunklin County");
			countyList.add("MO-Franklin County");
			countyList.add("MO-Gasconade County");
			countyList.add("MO-Gentry County");
			countyList.add("MO-Greene County");
			countyList.add("MO-Grundy County");
			countyList.add("MO-Harrison County");
			countyList.add("MO-Henry County");
			countyList.add("MO-Hickory County");
			countyList.add("MO-Holt County");
			countyList.add("MO-Howard County");
			countyList.add("MO-Howell County");
			countyList.add("MO-Iron County");
			countyList.add("MO-Jackson County");
			countyList.add("MO-Jasper County");
			countyList.add("MO-Jefferson County");
			countyList.add("MO-Johnson County");
			countyList.add("MO-Knox County");
			countyList.add("MO-Laclede County");
			countyList.add("MO-Lafayette County");
			countyList.add("MO-Lawrence County");
			countyList.add("MO-Lewis County");
			countyList.add("MO-Lincoln County");
			countyList.add("MO-Linn County");
			countyList.add("MO-Livingston County");
			countyList.add("MO-Macon County");
			countyList.add("MO-Madison County");
			countyList.add("MO-Maries County");
			countyList.add("MO-Marion County");
			countyList.add("MO-McDonald County");
			countyList.add("MO-Mercer County");
			countyList.add("MO-Miller County");
			countyList.add("MO-Mississippi County");
			countyList.add("MO-Moniteau County");
			countyList.add("MO-Monroe County");
			countyList.add("MO-Montgomery County");
			countyList.add("MO-Morgan County");
			countyList.add("MO-New Madrid County");
			countyList.add("MO-Newton County");
			countyList.add("MO-Nodaway County");
			countyList.add("MO-Oregon County");
			countyList.add("MO-Osage County");
			countyList.add("MO-Ozark County");
			countyList.add("MO-Pemiscot County");
			countyList.add("MO-Perry County");
			countyList.add("MO-Pettis County");
			countyList.add("MO-Phelps County");
			countyList.add("MO-Pike County");
			countyList.add("MO-Platte County");
			countyList.add("MO-Polk County");
			countyList.add("MO-Pulaski County");
			countyList.add("MO-Putnam County");
			countyList.add("MO-Ralls County");
			countyList.add("MO-Randolph County");
			countyList.add("MO-Ray County");
			countyList.add("MO-Reynolds County");
			countyList.add("MO-Ripley County");
			countyList.add("MO-Saint Charles County");
			countyList.add("MO-Saint Clair County");
			countyList.add("MO-Saint Francois County");
			countyList.add("MO-Saint Louis City");
			countyList.add("MO-Saint Louis County");
			countyList.add("MO-Sainte Genevieve County");
			countyList.add("MO-Saline County");
			countyList.add("MO-Schuyler County");
			countyList.add("MO-Scotland County");
			countyList.add("MO-Scott County");
			countyList.add("MO-Shannon County");
			countyList.add("MO-Shelby County");
			countyList.add("MO-Stoddard County");
			countyList.add("MO-Stone County");
			countyList.add("MO-Sullivan County");
			countyList.add("MO-Taney County");
			countyList.add("MO-Texas County");
			countyList.add("MO-Vernon County");
			countyList.add("MO-Warren County");
			countyList.add("MO-Washington County");
			countyList.add("MO-Wayne County");
			countyList.add("MO-Webster County");
			countyList.add("MO-Worth County");
			countyList.add("MO-Wright County");
		} else if(stateCode.equals("MP")){
			countyList.add("MP-Northern Mariana Islands");
		} else if(stateCode.equals("MS")){
			countyList.add("MS-Adams County");
			countyList.add("MS-Alcorn County");
			countyList.add("MS-Amite County");
			countyList.add("MS-Attala County");
			countyList.add("MS-Benton County");
			countyList.add("MS-Bolivar County");
			countyList.add("MS-Calhoun County");
			countyList.add("MS-Carroll County");
			countyList.add("MS-Chickasaw County");
			countyList.add("MS-Choctaw County");
			countyList.add("MS-Claiborne County");
			countyList.add("MS-Clarke County");
			countyList.add("MS-Clay County");
			countyList.add("MS-Coahoma County");
			countyList.add("MS-Copiah County");
			countyList.add("MS-Covington County");
			countyList.add("MS-Desoto County");
			countyList.add("MS-Forrest County");
			countyList.add("MS-Franklin County");
			countyList.add("MS-George County");
			countyList.add("MS-Greene County");
			countyList.add("MS-Grenada County");
			countyList.add("MS-Hancock County");
			countyList.add("MS-Harrison County");
			countyList.add("MS-Hinds County");
			countyList.add("MS-Holmes County");
			countyList.add("MS-Humphreys County");
			countyList.add("MS-Issaquena County");
			countyList.add("MS-Itawamba County");
			countyList.add("MS-Jackson County");
			countyList.add("MS-Jasper County");
			countyList.add("MS-Jefferson County");
			countyList.add("MS-Jefferson Davis County");
			countyList.add("MS-Jones County");
			countyList.add("MS-Kemper County");
			countyList.add("MS-Lafayette County");
			countyList.add("MS-Lamar County");
			countyList.add("MS-Lauderdale County");
			countyList.add("MS-Lawrence County");
			countyList.add("MS-Leake County");
			countyList.add("MS-Lee County");
			countyList.add("MS-Leflore County");
			countyList.add("MS-Lincoln County");
			countyList.add("MS-Lowndes County");
			countyList.add("MS-Madison County");
			countyList.add("MS-Marion County");
			countyList.add("MS-Marshall County");
			countyList.add("MS-Monroe County");
			countyList.add("MS-Montgomery County");
			countyList.add("MS-Neshoba County");
			countyList.add("MS-Newton County");
			countyList.add("MS-Noxubee County");
			countyList.add("MS-Oktibbeha County");
			countyList.add("MS-Panola County");
			countyList.add("MS-Pearl River County");
			countyList.add("MS-Perry County");
			countyList.add("MS-Pike County");
			countyList.add("MS-Pontotoc County");
			countyList.add("MS-Prentiss County");
			countyList.add("MS-Quitman County");
			countyList.add("MS-Rankin County");
			countyList.add("MS-Scott County");
			countyList.add("MS-Sharkey County");
			countyList.add("MS-Simpson County");
			countyList.add("MS-Smith County");
			countyList.add("MS-Stone County");
			countyList.add("MS-Sunflower County");
			countyList.add("MS-Tallahatchie County");
			countyList.add("MS-Tate County");
			countyList.add("MS-Tippah County");
			countyList.add("MS-Tishomingo County");
			countyList.add("MS-Tunica County");
			countyList.add("MS-Union County");
			countyList.add("MS-Walthall County");
			countyList.add("MS-Warren County");
			countyList.add("MS-Washington County");
			countyList.add("MS-Wayne County");
			countyList.add("MS-Webster County");
			countyList.add("MS-Wilkinson County");
			countyList.add("MS-Winston County");
			countyList.add("MS-Yalobusha County");
			countyList.add("MS-Yazoo County");
		} else if(stateCode.equals("MT")){
			countyList.add("MT-Beaverhead County");
			countyList.add("MT-Big Horn County");
			countyList.add("MT-Blaine County");
			countyList.add("MT-Broadwater County");
			countyList.add("MT-Carbon County");
			countyList.add("MT-Carter County");
			countyList.add("MT-Cascade County");
			countyList.add("MT-Chouteau County");
			countyList.add("MT-Custer County");
			countyList.add("MT-Daniels County");
			countyList.add("MT-Dawson County");
			countyList.add("MT-Deer Lodge County");
			countyList.add("MT-Fallon County");
			countyList.add("MT-Fergus County");
			countyList.add("MT-Flathead County");
			countyList.add("MT-Gallatin County");
			countyList.add("MT-Garfield County");
			countyList.add("MT-Glacier County");
			countyList.add("MT-Golden Valley County");
			countyList.add("MT-Granite County");
			countyList.add("MT-Hill County");
			countyList.add("MT-Jefferson County");
			countyList.add("MT-Judith Basin County");
			countyList.add("MT-Lake County");
			countyList.add("MT-Lewis and Clark County");
			countyList.add("MT-Liberty County");
			countyList.add("MT-Lincoln County");
			countyList.add("MT-Madison County");
			countyList.add("MT-McCone County");
			countyList.add("MT-Meagher County");
			countyList.add("MT-Mineral County");
			countyList.add("MT-Missoula County");
			countyList.add("MT-Musselshell County");
			countyList.add("MT-Park County");
			countyList.add("MT-Petroleum County");
			countyList.add("MT-Phillips County");
			countyList.add("MT-Pondera County");
			countyList.add("MT-Powder River County");
			countyList.add("MT-Powell County");
			countyList.add("MT-Prairie County");
			countyList.add("MT-Ravalli County");
			countyList.add("MT-Richland County");
			countyList.add("MT-Roosevelt County");
			countyList.add("MT-Rosebud County");
			countyList.add("MT-Sanders County");
			countyList.add("MT-Sheridan County");
			countyList.add("MT-Silver Bow County");
			countyList.add("MT-Stillwater County");
			countyList.add("MT-Sweet Grass County");
			countyList.add("MT-Teton County");
			countyList.add("MT-Toole County");
			countyList.add("MT-Treasure County");
			countyList.add("MT-Valley County");
			countyList.add("MT-Wheatland County");
			countyList.add("MT-Wibaux County");
			countyList.add("MT-Yellowstone County");
		} else if(stateCode.equals("NC")){
			countyList.add("NC-Alamance County");
			countyList.add("NC-Alexander County");
			countyList.add("NC-Alleghany County");
			countyList.add("NC-Anson County");
			countyList.add("NC-Ashe County");
			countyList.add("NC-Avery County");
			countyList.add("NC-Beaufort County");
			countyList.add("NC-Bertie County");
			countyList.add("NC-Bladen County");
			countyList.add("NC-Brunswick County");
			countyList.add("NC-Buncombe County");
			countyList.add("NC-Burke County");
			countyList.add("NC-Cabarrus County");
			countyList.add("NC-Caldwell County");
			countyList.add("NC-Camden County");
			countyList.add("NC-Carteret County");
			countyList.add("NC-Caswell County");
			countyList.add("NC-Catawba County");
			countyList.add("NC-Chatham County");
			countyList.add("NC-Cherokee County");
			countyList.add("NC-Chowan County");
			countyList.add("NC-Clay County");
			countyList.add("NC-Cleveland County");
			countyList.add("NC-Columbus County");
			countyList.add("NC-Craven County");
			countyList.add("NC-Cumberland County");
			countyList.add("NC-Currituck County");
			countyList.add("NC-Dare County");
			countyList.add("NC-Davidson County");
			countyList.add("NC-Davie County");
			countyList.add("NC-Duplin County");
			countyList.add("NC-Durham County");
			countyList.add("NC-Edgecombe County");
			countyList.add("NC-Forsyth County");
			countyList.add("NC-Franklin County");
			countyList.add("NC-Gaston County");
			countyList.add("NC-Gates County");
			countyList.add("NC-Graham County");
			countyList.add("NC-Granville County");
			countyList.add("NC-Greene County");
			countyList.add("NC-Guilford County");
			countyList.add("NC-Halifax County");
			countyList.add("NC-Harnett County");
			countyList.add("NC-Haywood County");
			countyList.add("NC-Henderson County");
			countyList.add("NC-Hertford County");
			countyList.add("NC-Hoke County");
			countyList.add("NC-Hyde County");
			countyList.add("NC-Iredell County");
			countyList.add("NC-Jackson County");
			countyList.add("NC-Johnston County");
			countyList.add("NC-Jones County");
			countyList.add("NC-Lee County");
			countyList.add("NC-Lenoir County");
			countyList.add("NC-Lincoln County");
			countyList.add("NC-Macon County");
			countyList.add("NC-Madison County");
			countyList.add("NC-Martin County");
			countyList.add("NC-McDowell County");
			countyList.add("NC-Mecklenburg County");
			countyList.add("NC-Mitchell County");
			countyList.add("NC-Montgomery County");
			countyList.add("NC-Moore County");
			countyList.add("NC-Nash County");
			countyList.add("NC-New Hanover County");
			countyList.add("NC-Northampton County");
			countyList.add("NC-Onslow County");
			countyList.add("NC-Orange County");
			countyList.add("NC-Pamlico County");
			countyList.add("NC-Pasquotank County");
			countyList.add("NC-Pender County");
			countyList.add("NC-Perquimans County");
			countyList.add("NC-Person County");
			countyList.add("NC-Pitt County");
			countyList.add("NC-Polk County");
			countyList.add("NC-Randolph County");
			countyList.add("NC-Richmond County");
			countyList.add("NC-Robeson County");
			countyList.add("NC-Rockingham County");
			countyList.add("NC-Rowan County");
			countyList.add("NC-Rutherford County");
			countyList.add("NC-Sampson County");
			countyList.add("NC-Scotland County");
			countyList.add("NC-Stanly County");
			countyList.add("NC-Stokes County");
			countyList.add("NC-Surry County");
			countyList.add("NC-Swain County");
			countyList.add("NC-Transylvania County");
			countyList.add("NC-Tyrrell County");
			countyList.add("NC-Union County");
			countyList.add("NC-Vance County");
			countyList.add("NC-Wake County");
			countyList.add("NC-Warren County");
			countyList.add("NC-Washington County");
			countyList.add("NC-Watauga County");
			countyList.add("NC-Wayne County");
			countyList.add("NC-Wilkes County");
			countyList.add("NC-Wilson County");
			countyList.add("NC-Yadkin County");
			countyList.add("NC-Yancey County");
		} else if(stateCode.equals("ND")){
			countyList.add("ND-Adams County");
			countyList.add("ND-Barnes County");
			countyList.add("ND-Benson County");
			countyList.add("ND-Billings County");
			countyList.add("ND-Bottineau County");
			countyList.add("ND-Bowman County");
			countyList.add("ND-Burke County");
			countyList.add("ND-Burleigh County");
			countyList.add("ND-Cass County");
			countyList.add("ND-Cavalier County");
			countyList.add("ND-Dickey County");
			countyList.add("ND-Divide County");
			countyList.add("ND-Dunn County");
			countyList.add("ND-Eddy County");
			countyList.add("ND-Emmons County");
			countyList.add("ND-Foster County");
			countyList.add("ND-Golden Valley County");
			countyList.add("ND-Grand Forks County");
			countyList.add("ND-Grant County");
			countyList.add("ND-Griggs County");
			countyList.add("ND-Hettinger County");
			countyList.add("ND-Kidder County");
			countyList.add("ND-LaMoure County");
			countyList.add("ND-Logan County");
			countyList.add("ND-McHenry County");
			countyList.add("ND-McIntosh County");
			countyList.add("ND-McKenzie County");
			countyList.add("ND-McLean County");
			countyList.add("ND-Mercer County");
			countyList.add("ND-Morton County");
			countyList.add("ND-Mountrail County");
			countyList.add("ND-Nelson County");
			countyList.add("ND-Oliver County");
			countyList.add("ND-Pembina County");
			countyList.add("ND-Pierce County");
			countyList.add("ND-Ramsey County");
			countyList.add("ND-Ransom County");
			countyList.add("ND-Renville County");
			countyList.add("ND-Richland County");
			countyList.add("ND-Rolette County");
			countyList.add("ND-Sargent County");
			countyList.add("ND-Sheridan County");
			countyList.add("ND-Sioux County");
			countyList.add("ND-Slope County");
			countyList.add("ND-Stark County");
			countyList.add("ND-Steele County");
			countyList.add("ND-Stutsman County");
			countyList.add("ND-Towner County");
			countyList.add("ND-Traill County");
			countyList.add("ND-Walsh County");
			countyList.add("ND-Ward County");
			countyList.add("ND-Wells County");
			countyList.add("ND-Williams County");
		} else if(stateCode.equals("NE")){
			countyList.add("NE-Adams County");
			countyList.add("NE-Antelope County");
			countyList.add("NE-Arthur County");
			countyList.add("NE-Banner County");
			countyList.add("NE-Blaine County");
			countyList.add("NE-Boone County");
			countyList.add("NE-Box Butte County");
			countyList.add("NE-Boyd County");
			countyList.add("NE-Brown County");
			countyList.add("NE-Buffalo County");
			countyList.add("NE-Burt County");
			countyList.add("NE-Butler County");
			countyList.add("NE-Cass County");
			countyList.add("NE-Cedar County");
			countyList.add("NE-Chase County");
			countyList.add("NE-Cherry County");
			countyList.add("NE-Cheyenne County");
			countyList.add("NE-Clay County");
			countyList.add("NE-Colfax County");
			countyList.add("NE-Cuming County");
			countyList.add("NE-Custer County");
			countyList.add("NE-Dakota County");
			countyList.add("NE-Dawes County");
			countyList.add("NE-Dawson County");
			countyList.add("NE-Deuel County");
			countyList.add("NE-Dixon County");
			countyList.add("NE-Dodge County");
			countyList.add("NE-Douglas County");
			countyList.add("NE-Dundy County");
			countyList.add("NE-Fillmore County");
			countyList.add("NE-Franklin County");
			countyList.add("NE-Frontier County");
			countyList.add("NE-Furnas County");
			countyList.add("NE-Gage County");
			countyList.add("NE-Garden County");
			countyList.add("NE-Garfield County");
			countyList.add("NE-Gosper County");
			countyList.add("NE-Grant County");
			countyList.add("NE-Greeley County");
			countyList.add("NE-Hall County");
			countyList.add("NE-Hamilton County");
			countyList.add("NE-Harlan County");
			countyList.add("NE-Hayes County");
			countyList.add("NE-Hitchcock County");
			countyList.add("NE-Holt County");
			countyList.add("NE-Hooker County");
			countyList.add("NE-Howard County");
			countyList.add("NE-Jefferson County");
			countyList.add("NE-Johnson County");
			countyList.add("NE-Kearney County");
			countyList.add("NE-Keith County");
			countyList.add("NE-Keya Paha County");
			countyList.add("NE-Kimball County");
			countyList.add("NE-Knox County");
			countyList.add("NE-Lancaster County");
			countyList.add("NE-Lincoln County");
			countyList.add("NE-Logan County");
			countyList.add("NE-Loup County");
			countyList.add("NE-Madison County");
			countyList.add("NE-McPherson County");
			countyList.add("NE-Merrick County");
			countyList.add("NE-Morrill County");
			countyList.add("NE-Nance County");
			countyList.add("NE-Nemaha County");
			countyList.add("NE-Nuckolls County");
			countyList.add("NE-Otoe County");
			countyList.add("NE-Pawnee County");
			countyList.add("NE-Perkins County");
			countyList.add("NE-Phelps County");
			countyList.add("NE-Pierce County");
			countyList.add("NE-Platte County");
			countyList.add("NE-Polk County");
			countyList.add("NE-Red Willow County");
			countyList.add("NE-Richardson County");
			countyList.add("NE-Rock County");
			countyList.add("NE-Saline County");
			countyList.add("NE-Sarpy County");
			countyList.add("NE-Saunders County");
			countyList.add("NE-Scotts Bluff County");
			countyList.add("NE-Seward County");
			countyList.add("NE-Sheridan County");
			countyList.add("NE-Sherman County");
			countyList.add("NE-Sioux County");
			countyList.add("NE-Stanton County");
			countyList.add("NE-Thayer County");
			countyList.add("NE-Thomas County");
			countyList.add("NE-Thurston County");
			countyList.add("NE-Valley County");
			countyList.add("NE-Washington County");
			countyList.add("NE-Wayne County");
			countyList.add("NE-Webster County");
			countyList.add("NE-Wheeler County");
			countyList.add("NE-York County");
		} else if(stateCode.equals("NH")){
			countyList.add("NH-Belknap County");
			countyList.add("NH-Carroll County");
			countyList.add("NH-Cheshire County");
			countyList.add("NH-Coos County");
			countyList.add("NH-Grafton County");
			countyList.add("NH-Hillsborough County");
			countyList.add("NH-Merrimack County");
			countyList.add("NH-Rockingham County");
			countyList.add("NH-Strafford County");
			countyList.add("NH-Sullivan County");
		} else if(stateCode.equals("NJ")){
			countyList.add("NJ-Atlantic County");
			countyList.add("NJ-Bergen County");
			countyList.add("NJ-Burlington County");
			countyList.add("NJ-Camden County");
			countyList.add("NJ-Cape May County");
			countyList.add("NJ-Cumberland County");
			countyList.add("NJ-Essex County");
			countyList.add("NJ-Gloucester County");
			countyList.add("NJ-Hudson County");
			countyList.add("NJ-Hunterdon County");
			countyList.add("NJ-Mercer County");
			countyList.add("NJ-Middlesex County");
			countyList.add("NJ-Monmouth County");
			countyList.add("NJ-Morris County");
			countyList.add("NJ-Ocean County");
			countyList.add("NJ-Passaic County");
			countyList.add("NJ-Salem County");
			countyList.add("NJ-Somerset County");
			countyList.add("NJ-Sussex County");
			countyList.add("NJ-Union County");
			countyList.add("NJ-Warren County");
		} else if(stateCode.equals("NM")){
			countyList.add("NM-Bernalillo County");
			countyList.add("NM-Catron County");
			countyList.add("NM-Chaves County");
			countyList.add("NM-Cibola County");
			countyList.add("NM-Colfax County");
			countyList.add("NM-Curry County");
			countyList.add("NM-De Baca County");
			countyList.add("NM-Dona Ana County");
			countyList.add("NM-Eddy County");
			countyList.add("NM-Grant County");
			countyList.add("NM-Guadalupe County");
			countyList.add("NM-Harding County");
			countyList.add("NM-Hidalgo County");
			countyList.add("NM-Lea County");
			countyList.add("NM-Lincoln County");
			countyList.add("NM-Los Alamos County");
			countyList.add("NM-Luna County");
			countyList.add("NM-McKinley County");
			countyList.add("NM-Mora County");
			countyList.add("NM-Otero County");
			countyList.add("NM-Quay County");
			countyList.add("NM-Rio Arriba County");
			countyList.add("NM-Roosevelt County");
			countyList.add("NM-San Juan County");
			countyList.add("NM-San Miguel County");
			countyList.add("NM-Sandoval County");
			countyList.add("NM-Santa Fe County");
			countyList.add("NM-Sierra County");
			countyList.add("NM-Socorro County");
			countyList.add("NM-Taos County");
			countyList.add("NM-Torrance County");
			countyList.add("NM-Union County");
			countyList.add("NM-Valencia County");
		} else if(stateCode.equals("NV")){
			countyList.add("NV-Carson City");
			countyList.add("NV-Churchill County");
			countyList.add("NV-Clark County");
			countyList.add("NV-Douglas County");
			countyList.add("NV-Elko County");
			countyList.add("NV-Esmeralda County");
			countyList.add("NV-Eureka County");
			countyList.add("NV-Humboldt County");
			countyList.add("NV-Lander County");
			countyList.add("NV-Lincoln County");
			countyList.add("NV-Lyon County");
			countyList.add("NV-Mineral County");
			countyList.add("NV-Nye County");
			countyList.add("NV-Pershing County");
			countyList.add("NV-Storey County");
			countyList.add("NV-Washoe County");
			countyList.add("NV-White Pine County");
		} else if(stateCode.equals("NY")){
			countyList.add("NY-Albany County");
			countyList.add("NY-Allegany County");
			countyList.add("NY-Bronx County");
			countyList.add("NY-Broome County");
			countyList.add("NY-Cattaraugus County");
			countyList.add("NY-Cayuga County");
			countyList.add("NY-Chautauqua County");
			countyList.add("NY-Chemung County");
			countyList.add("NY-Chenango County");
			countyList.add("NY-Clinton County");
			countyList.add("NY-Columbia County");
			countyList.add("NY-Cortland County");
			countyList.add("NY-Delaware County");
			countyList.add("NY-Dutchess County");
			countyList.add("NY-Erie County");
			countyList.add("NY-Essex County");
			countyList.add("NY-Franklin County");
			countyList.add("NY-Fulton County");
			countyList.add("NY-Genesee County");
			countyList.add("NY-Greene County");
			countyList.add("NY-Hamilton County");
			countyList.add("NY-Herkimer County");
			countyList.add("NY-Jefferson County");
			countyList.add("NY-Kings County");
			countyList.add("NY-Lewis County");
			countyList.add("NY-Livingston County");
			countyList.add("NY-Madison County");
			countyList.add("NY-Monroe County");
			countyList.add("NY-Montgomery County");
			countyList.add("NY-Nassau County");
			countyList.add("NY-New York County");
			countyList.add("NY-Niagara County");
			countyList.add("NY-Oneida County");
			countyList.add("NY-Onondaga County");
			countyList.add("NY-Ontario County");
			countyList.add("NY-Orange County");
			countyList.add("NY-Orleans County");
			countyList.add("NY-Oswego County");
			countyList.add("NY-Otsego County");
			countyList.add("NY-Putnam County");
			countyList.add("NY-Queens County");
			countyList.add("NY-Rensselaer County");
			countyList.add("NY-Richmond County");
			countyList.add("NY-Rockland County");
			countyList.add("NY-Saint Lawrence County");
			countyList.add("NY-Saratoga County");
			countyList.add("NY-Schenectady County");
			countyList.add("NY-Schoharie County");
			countyList.add("NY-Schuyler County");
			countyList.add("NY-Seneca County");
			countyList.add("NY-Steuben County");
			countyList.add("NY-Suffolk County");
			countyList.add("NY-Sullivan County");
			countyList.add("NY-Tioga County");
			countyList.add("NY-Tompkins County");
			countyList.add("NY-Ulster County");
			countyList.add("NY-Warren County");
			countyList.add("NY-Washington County");
			countyList.add("NY-Wayne County");
			countyList.add("NY-Westchester County");
			countyList.add("NY-Wyoming County");
			countyList.add("NY-Yates County");
		} else if(stateCode.equals("OH")){
			countyList.add("OH-Adams County");
			countyList.add("OH-Allen County");
			countyList.add("OH-Ashland County");
			countyList.add("OH-Ashtabula County");
			countyList.add("OH-Athens County");
			countyList.add("OH-Auglaize County");
			countyList.add("OH-Belmont County");
			countyList.add("OH-Brown County");
			countyList.add("OH-Butler County");
			countyList.add("OH-Carroll County");
			countyList.add("OH-Champaign County");
			countyList.add("OH-Clark County");
			countyList.add("OH-Clermont County");
			countyList.add("OH-Clinton County");
			countyList.add("OH-Columbiana County");
			countyList.add("OH-Coshocton County");
			countyList.add("OH-Crawford County");
			countyList.add("OH-Cuyahoga County");
			countyList.add("OH-Darke County");
			countyList.add("OH-Defiance County");
			countyList.add("OH-Delaware County");
			countyList.add("OH-Erie County");
			countyList.add("OH-Fairfield County");
			countyList.add("OH-Fayette County");
			countyList.add("OH-Franklin County");
			countyList.add("OH-Fulton County");
			countyList.add("OH-Gallia County");
			countyList.add("OH-Geauga County");
			countyList.add("OH-Greene County");
			countyList.add("OH-Guernsey County");
			countyList.add("OH-Hamilton County");
			countyList.add("OH-Hancock County");
			countyList.add("OH-Hardin County");
			countyList.add("OH-Harrison County");
			countyList.add("OH-Henry County");
			countyList.add("OH-Highland County");
			countyList.add("OH-Hocking County");
			countyList.add("OH-Holmes County");
			countyList.add("OH-Huron County");
			countyList.add("OH-Jackson County");
			countyList.add("OH-Jefferson County");
			countyList.add("OH-Knox County");
			countyList.add("OH-Lake County");
			countyList.add("OH-Lawrence County");
			countyList.add("OH-Licking County");
			countyList.add("OH-Logan County");
			countyList.add("OH-Lorain County");
			countyList.add("OH-Lucas County");
			countyList.add("OH-Madison County");
			countyList.add("OH-Mahoning County");
			countyList.add("OH-Marion County");
			countyList.add("OH-Medina County");
			countyList.add("OH-Meigs County");
			countyList.add("OH-Mercer County");
			countyList.add("OH-Miami County");
			countyList.add("OH-Monroe County");
			countyList.add("OH-Montgomery County");
			countyList.add("OH-Morgan County");
			countyList.add("OH-Morrow County");
			countyList.add("OH-Muskingum County");
			countyList.add("OH-Noble County");
			countyList.add("OH-Ottawa County");
			countyList.add("OH-Paulding County");
			countyList.add("OH-Perry County");
			countyList.add("OH-Pickaway County");
			countyList.add("OH-Pike County");
			countyList.add("OH-Portage County");
			countyList.add("OH-Preble County");
			countyList.add("OH-Putnam County");
			countyList.add("OH-Richland County");
			countyList.add("OH-Ross County");
			countyList.add("OH-Sandusky County");
			countyList.add("OH-Scioto County");
			countyList.add("OH-Seneca County");
			countyList.add("OH-Shelby County");
			countyList.add("OH-Stark County");
			countyList.add("OH-Summit County");
			countyList.add("OH-Trumbull County");
			countyList.add("OH-Tuscarawas County");
			countyList.add("OH-Union County");
			countyList.add("OH-Van Wert County");
			countyList.add("OH-Vinton County");
			countyList.add("OH-Warren County");
			countyList.add("OH-Washington County");
			countyList.add("OH-Wayne County");
			countyList.add("OH-Williams County");
			countyList.add("OH-Wood County");
			countyList.add("OH-Wyandot County");
		} else if(stateCode.equals("OK")){
			countyList.add("OK-Adair County");
			countyList.add("OK-Alfalfa County");
			countyList.add("OK-Atoka County");
			countyList.add("OK-Beaver County");
			countyList.add("OK-Beckham County");
			countyList.add("OK-Blaine County");
			countyList.add("OK-Bryan County");
			countyList.add("OK-Caddo County");
			countyList.add("OK-Canadian County");
			countyList.add("OK-Carter County");
			countyList.add("OK-Cherokee County");
			countyList.add("OK-Choctaw County");
			countyList.add("OK-Cimarron County");
			countyList.add("OK-Cleveland County");
			countyList.add("OK-Coal County");
			countyList.add("OK-Comanche County");
			countyList.add("OK-Cotton County");
			countyList.add("OK-Craig County");
			countyList.add("OK-Creek County");
			countyList.add("OK-Custer County");
			countyList.add("OK-Delaware County");
			countyList.add("OK-Dewey County");
			countyList.add("OK-Ellis County");
			countyList.add("OK-Garfield County");
			countyList.add("OK-Garvin County");
			countyList.add("OK-Grady County");
			countyList.add("OK-Grant County");
			countyList.add("OK-Greer County");
			countyList.add("OK-Harmon County");
			countyList.add("OK-Harper County");
			countyList.add("OK-Haskell County");
			countyList.add("OK-Hughes County");
			countyList.add("OK-Jackson County");
			countyList.add("OK-Jefferson County");
			countyList.add("OK-Johnston County");
			countyList.add("OK-Kay County");
			countyList.add("OK-Kingfisher County");
			countyList.add("OK-Kiowa County");
			countyList.add("OK-Latimer County");
			countyList.add("OK-Le Flore County");
			countyList.add("OK-Lincoln County");
			countyList.add("OK-Logan County");
			countyList.add("OK-Love County");
			countyList.add("OK-Major County");
			countyList.add("OK-Marshall County");
			countyList.add("OK-Mayes County");
			countyList.add("OK-McClain County");
			countyList.add("OK-McCurtain County");
			countyList.add("OK-McIntosh County");
			countyList.add("OK-Murray County");
			countyList.add("OK-Muskogee County");
			countyList.add("OK-Noble County");
			countyList.add("OK-Nowata County");
			countyList.add("OK-Okfuskee County");
			countyList.add("OK-Oklahoma County");
			countyList.add("OK-Okmulgee County");
			countyList.add("OK-Osage County");
			countyList.add("OK-Ottawa County");
			countyList.add("OK-Pawnee County");
			countyList.add("OK-Payne County");
			countyList.add("OK-Pittsburg County");
			countyList.add("OK-Pontotoc County");
			countyList.add("OK-Pottawatomie County");
			countyList.add("OK-Pushmataha County");
			countyList.add("OK-Roger Mills County");
			countyList.add("OK-Rogers County");
			countyList.add("OK-Seminole County");
			countyList.add("OK-Sequoyah County");
			countyList.add("OK-Stephens County");
			countyList.add("OK-Texas County");
			countyList.add("OK-Tillman County");
			countyList.add("OK-Tulsa County");
			countyList.add("OK-Wagoner County");
			countyList.add("OK-Washington County");
			countyList.add("OK-Washita County");
			countyList.add("OK-Woods County");
			countyList.add("OK-Woodward County");
		} else if(stateCode.equals("OR")){
			countyList.add("OR-Baker County");
			countyList.add("OR-Benton County");
			countyList.add("OR-Clackamas County");
			countyList.add("OR-Clatsop County");
			countyList.add("OR-Columbia County");
			countyList.add("OR-Coos County");
			countyList.add("OR-Crook County");
			countyList.add("OR-Curry County");
			countyList.add("OR-Deschutes County");
			countyList.add("OR-Douglas County");
			countyList.add("OR-Gilliam County");
			countyList.add("OR-Grant County");
			countyList.add("OR-Harney County");
			countyList.add("OR-Hood River County");
			countyList.add("OR-Jackson County");
			countyList.add("OR-Jefferson County");
			countyList.add("OR-Josephine County");
			countyList.add("OR-Klamath County");
			countyList.add("OR-Lake County");
			countyList.add("OR-Lane County");
			countyList.add("OR-Lincoln County");
			countyList.add("OR-Linn County");
			countyList.add("OR-Malheur County");
			countyList.add("OR-Marion County");
			countyList.add("OR-Morrow County");
			countyList.add("OR-Multnomah County");
			countyList.add("OR-Polk County");
			countyList.add("OR-Sherman County");
			countyList.add("OR-Tillamook County");
			countyList.add("OR-Umatilla County");
			countyList.add("OR-Union County");
			countyList.add("OR-Wallowa County");
			countyList.add("OR-Wasco County");
			countyList.add("OR-Washington County");
			countyList.add("OR-Wheeler County");
			countyList.add("OR-Yamhill County");
		} else if(stateCode.equals("PA")){
			countyList.add("PA-Adams County");
			countyList.add("PA-Allegheny County");
			countyList.add("PA-Armstrong County");
			countyList.add("PA-Beaver County");
			countyList.add("PA-Bedford County");
			countyList.add("PA-Berks County");
			countyList.add("PA-Blair County");
			countyList.add("PA-Bradford County");
			countyList.add("PA-Bucks County");
			countyList.add("PA-Butler County");
			countyList.add("PA-Cambria County");
			countyList.add("PA-Cameron County");
			countyList.add("PA-Carbon County");
			countyList.add("PA-Centre County");
			countyList.add("PA-Chester County");
			countyList.add("PA-Clarion County");
			countyList.add("PA-Clearfield County");
			countyList.add("PA-Clinton County");
			countyList.add("PA-Columbia County");
			countyList.add("PA-Crawford County");
			countyList.add("PA-Cumberland County");
			countyList.add("PA-Dauphin County");
			countyList.add("PA-Delaware County");
			countyList.add("PA-Elk County");
			countyList.add("PA-Erie County");
			countyList.add("PA-Fayette County");
			countyList.add("PA-Forest County");
			countyList.add("PA-Franklin County");
			countyList.add("PA-Fulton County");
			countyList.add("PA-Greene County");
			countyList.add("PA-Huntingdon County");
			countyList.add("PA-Indiana County");
			countyList.add("PA-Jefferson County");
			countyList.add("PA-Juniata County");
			countyList.add("PA-Lackawanna County");
			countyList.add("PA-Lancaster County");
			countyList.add("PA-Lawrence County");
			countyList.add("PA-Lebanon County");
			countyList.add("PA-Lehigh County");
			countyList.add("PA-Luzerne County");
			countyList.add("PA-Lycoming County");
			countyList.add("PA-McKean County");
			countyList.add("PA-Mercer County");
			countyList.add("PA-Mifflin County");
			countyList.add("PA-Monroe County");
			countyList.add("PA-Montgomery County");
			countyList.add("PA-Montour County");
			countyList.add("PA-Northampton County");
			countyList.add("PA-Northumberland County");
			countyList.add("PA-Perry County");
			countyList.add("PA-Philadelphia County");
			countyList.add("PA-Pike County");
			countyList.add("PA-Potter County");
			countyList.add("PA-Schuylkill County");
			countyList.add("PA-Snyder County");
			countyList.add("PA-Somerset County");
			countyList.add("PA-Sullivan County");
			countyList.add("PA-Susquehanna County");
			countyList.add("PA-Tioga County");
			countyList.add("PA-Union County");
			countyList.add("PA-Venango County");
			countyList.add("PA-Warren County");
			countyList.add("PA-Washington County");
			countyList.add("PA-Wayne County");
			countyList.add("PA-Westmoreland County");
			countyList.add("PA-Wyoming County");
			countyList.add("PA-York County");
		} else if(stateCode.equals("PR")){
			countyList.add("PR-Adjuntas");
			countyList.add("PR-Aguada");
			countyList.add("PR-Aguadilla");
			countyList.add("PR-Aguas Buenas");
			countyList.add("PR-Aibonito");
			countyList.add("PR-Anasco");
			countyList.add("PR-Arecibo");
			countyList.add("PR-Arroyo");
			countyList.add("PR-Barceloneta");
			countyList.add("PR-Barranquitas");
			countyList.add("PR-Bayamon");
			countyList.add("PR-Cabo Rojo");
			countyList.add("PR-Caguas");
			countyList.add("PR-Camuy");
			countyList.add("PR-Canovanas");
			countyList.add("PR-Carolina");
			countyList.add("PR-Catano");
			countyList.add("PR-Cayey");
			countyList.add("PR-Ceiba");
			countyList.add("PR-Ciales");
			countyList.add("PR-Cidra");
			countyList.add("PR-Coamo");
			countyList.add("PR-Comerio");
			countyList.add("PR-Corozal");
			countyList.add("PR-Culebra");
			countyList.add("PR-Dorado");
			countyList.add("PR-Fajardo");
			countyList.add("PR-Florida");
			countyList.add("PR-Guanica");
			countyList.add("PR-Guayama");
			countyList.add("PR-Guayanilla");
			countyList.add("PR-Guaynabo");
			countyList.add("PR-Gurabo");
			countyList.add("PR-Hatillo");
			countyList.add("PR-Hormigueros");
			countyList.add("PR-Humacao");
			countyList.add("PR-Isabela");
			countyList.add("PR-Jayuya");
			countyList.add("PR-Juana Diaz");
			countyList.add("PR-Juncos");
			countyList.add("PR-Lajas");
			countyList.add("PR-Lares");
			countyList.add("PR-Las Marias");
			countyList.add("PR-Las Piedras");
			countyList.add("PR-Loiza");
			countyList.add("PR-Luquillo");
			countyList.add("PR-Manati");
			countyList.add("PR-Maricao");
			countyList.add("PR-Maunabo");
			countyList.add("PR-Mayaguez");
			countyList.add("PR-Moca");
			countyList.add("PR-Morovis");
			countyList.add("PR-Naguabo");
			countyList.add("PR-Naranjito");
			countyList.add("PR-Orocovis");
			countyList.add("PR-Patillas");
			countyList.add("PR-Penuelas");
			countyList.add("PR-Ponce");
			countyList.add("PR-Quebradillas");
			countyList.add("PR-Rincon");
			countyList.add("PR-Rio Grande");
			countyList.add("PR-Sabana Grande");
			countyList.add("PR-Salinas");
			countyList.add("PR-San German");
			countyList.add("PR-San Juan");
			countyList.add("PR-San Lorenzo");
			countyList.add("PR-San Sebastian");
			countyList.add("PR-Santa Isabel");
			countyList.add("PR-Toa Alta");
			countyList.add("PR-Toa Baja");
			countyList.add("PR-Trujillo Alto");
			countyList.add("PR-Utuado");
			countyList.add("PR-Vega Alta");
			countyList.add("PR-Vega Baja");
			countyList.add("PR-Vieques");
			countyList.add("PR-Villalba");
			countyList.add("PR-Yabucoa");
			countyList.add("PR-Yauco");
		} else if(stateCode.equals("PW")){
			countyList.add("PW-Palau County");
		} else if(stateCode.equals("RI")){
			countyList.add("RI-Bristol County");
			countyList.add("RI-Kent County");
			countyList.add("RI-Newport County");
			countyList.add("RI-Providence County");
			countyList.add("RI-Washington County");
		} else if(stateCode.equals("SC")){
			countyList.add("SC-Abbeville County");
			countyList.add("SC-Aiken County");
			countyList.add("SC-Allendale County");
			countyList.add("SC-Anderson County");
			countyList.add("SC-Bamberg County");
			countyList.add("SC-Barnwell County");
			countyList.add("SC-Beaufort County");
			countyList.add("SC-Berkeley County");
			countyList.add("SC-Calhoun County");
			countyList.add("SC-Charleston County");
			countyList.add("SC-Cherokee County");
			countyList.add("SC-Chester County");
			countyList.add("SC-Chesterfield County");
			countyList.add("SC-Clarendon County");
			countyList.add("SC-Colleton County");
			countyList.add("SC-Darlington County");
			countyList.add("SC-Dillon County");
			countyList.add("SC-Dorchester County");
			countyList.add("SC-Edgefield County");
			countyList.add("SC-Fairfield County");
			countyList.add("SC-Florence County");
			countyList.add("SC-Georgetown County");
			countyList.add("SC-Greenville County");
			countyList.add("SC-Greenwood County");
			countyList.add("SC-Hampton County");
			countyList.add("SC-Horry County");
			countyList.add("SC-Jasper County");
			countyList.add("SC-Kershaw County");
			countyList.add("SC-Lancaster County");
			countyList.add("SC-Laurens County");
			countyList.add("SC-Lee County");
			countyList.add("SC-Lexington County");
			countyList.add("SC-Marion County");
			countyList.add("SC-Marlboro County");
			countyList.add("SC-McCormick County");
			countyList.add("SC-Newberry County");
			countyList.add("SC-Oconee County");
			countyList.add("SC-Orangeburg County");
			countyList.add("SC-Pickens County");
			countyList.add("SC-Richland County");
			countyList.add("SC-Saluda County");
			countyList.add("SC-Spartanburg County");
			countyList.add("SC-Sumter County");
			countyList.add("SC-Union County");
			countyList.add("SC-Williamsburg County");
			countyList.add("SC-York County");
		} else if(stateCode.equals("SD")){
			countyList.add("SD-Aurora County");
			countyList.add("SD-Beadle County");
			countyList.add("SD-Bennett County");
			countyList.add("SD-Bon Homme County");
			countyList.add("SD-Brookings County");
			countyList.add("SD-Brown County");
			countyList.add("SD-Brule County");
			countyList.add("SD-Buffalo County");
			countyList.add("SD-Butte County");
			countyList.add("SD-Campbell County");
			countyList.add("SD-Charles Mix County");
			countyList.add("SD-Clark County");
			countyList.add("SD-Clay County");
			countyList.add("SD-Codington County");
			countyList.add("SD-Corson County");
			countyList.add("SD-Custer County");
			countyList.add("SD-Davison County");
			countyList.add("SD-Day County");
			countyList.add("SD-Deuel County");
			countyList.add("SD-Dewey County");
			countyList.add("SD-Douglas County");
			countyList.add("SD-Edmunds County");
			countyList.add("SD-Fall River County");
			countyList.add("SD-Faulk County");
			countyList.add("SD-Grant County");
			countyList.add("SD-Gregory County");
			countyList.add("SD-Haakon County");
			countyList.add("SD-Hamlin County");
			countyList.add("SD-Hand County");
			countyList.add("SD-Hanson County");
			countyList.add("SD-Harding County");
			countyList.add("SD-Hughes County");
			countyList.add("SD-Hutchinson County");
			countyList.add("SD-Hyde County");
			countyList.add("SD-Jackson County");
			countyList.add("SD-Jerauld County");
			countyList.add("SD-Jones County");
			countyList.add("SD-Kingsbury County");
			countyList.add("SD-Lake County");
			countyList.add("SD-Lawrence County");
			countyList.add("SD-Lincoln County");
			countyList.add("SD-Lyman County");
			countyList.add("SD-Marshall County");
			countyList.add("SD-McCook County");
			countyList.add("SD-McPherson County");
			countyList.add("SD-Meade County");
			countyList.add("SD-Mellette County");
			countyList.add("SD-Miner County");
			countyList.add("SD-Minnehaha County");
			countyList.add("SD-Moody County");
			countyList.add("SD-Pennington County");
			countyList.add("SD-Perkins County");
			countyList.add("SD-Potter County");
			countyList.add("SD-Roberts County");
			countyList.add("SD-Sanborn County");
			countyList.add("SD-Shannon County");
			countyList.add("SD-Spink County");
			countyList.add("SD-Stanley County");
			countyList.add("SD-Sully County");
			countyList.add("SD-Todd County");
			countyList.add("SD-Tripp County");
			countyList.add("SD-Turner County");
			countyList.add("SD-Union County");
			countyList.add("SD-Walworth County");
			countyList.add("SD-Yankton County");
			countyList.add("SD-Ziebach County");
		} else if(stateCode.equals("TN")){
			countyList.add("TN-Anderson County");
			countyList.add("TN-Bedford County");
			countyList.add("TN-Benton County");
			countyList.add("TN-Bledsoe County");
			countyList.add("TN-Blount County");
			countyList.add("TN-Bradley County");
			countyList.add("TN-Campbell County");
			countyList.add("TN-Cannon County");
			countyList.add("TN-Carroll County");
			countyList.add("TN-Carter County");
			countyList.add("TN-Cheatham County");
			countyList.add("TN-Chester County");
			countyList.add("TN-Claiborne County");
			countyList.add("TN-Clay County");
			countyList.add("TN-Cocke County");
			countyList.add("TN-Coffee County");
			countyList.add("TN-Crockett County");
			countyList.add("TN-Cumberland County");
			countyList.add("TN-Davidson County");
			countyList.add("TN-Decatur County");
			countyList.add("TN-Dekalb County");
			countyList.add("TN-Dickson County");
			countyList.add("TN-Dyer County");
			countyList.add("TN-Fayette County");
			countyList.add("TN-Fentress County");
			countyList.add("TN-Franklin County");
			countyList.add("TN-Gibson County");
			countyList.add("TN-Giles County");
			countyList.add("TN-Grainger County");
			countyList.add("TN-Greene County");
			countyList.add("TN-Grundy County");
			countyList.add("TN-Hamblen County");
			countyList.add("TN-Hamilton County");
			countyList.add("TN-Hancock County");
			countyList.add("TN-Hardeman County");
			countyList.add("TN-Hardin County");
			countyList.add("TN-Hawkins County");
			countyList.add("TN-Haywood County");
			countyList.add("TN-Henderson County");
			countyList.add("TN-Henry County");
			countyList.add("TN-Hickman County");
			countyList.add("TN-Houston County");
			countyList.add("TN-Humphreys County");
			countyList.add("TN-Jackson County");
			countyList.add("TN-Jefferson County");
			countyList.add("TN-Johnson County");
			countyList.add("TN-Knox County");
			countyList.add("TN-Lake County");
			countyList.add("TN-Lauderdale County");
			countyList.add("TN-Lawrence County");
			countyList.add("TN-Lewis County");
			countyList.add("TN-Lincoln County");
			countyList.add("TN-Loudon County");
			countyList.add("TN-Macon County");
			countyList.add("TN-Madison County");
			countyList.add("TN-Marion County");
			countyList.add("TN-Marshall County");
			countyList.add("TN-Maury County");
			countyList.add("TN-McMinn County");
			countyList.add("TN-McNairy County");
			countyList.add("TN-Meigs County");
			countyList.add("TN-Monroe County");
			countyList.add("TN-Montgomery County");
			countyList.add("TN-Moore County");
			countyList.add("TN-Morgan County");
			countyList.add("TN-Obion County");
			countyList.add("TN-Overton County");
			countyList.add("TN-Perry County");
			countyList.add("TN-Pickett County");
			countyList.add("TN-Polk County");
			countyList.add("TN-Putnam County");
			countyList.add("TN-Rhea County");
			countyList.add("TN-Roane County");
			countyList.add("TN-Robertson County");
			countyList.add("TN-Rutherford County");
			countyList.add("TN-Scott County");
			countyList.add("TN-Sequatchie County");
			countyList.add("TN-Sevier County");
			countyList.add("TN-Shelby County");
			countyList.add("TN-Smith County");
			countyList.add("TN-Stewart County");
			countyList.add("TN-Sullivan County");
			countyList.add("TN-Sumner County");
			countyList.add("TN-Tipton County");
			countyList.add("TN-Trousdale County");
			countyList.add("TN-Unicoi County");
			countyList.add("TN-Union County");
			countyList.add("TN-Van Buren County");
			countyList.add("TN-Warren County");
			countyList.add("TN-Washington County");
			countyList.add("TN-Wayne County");
			countyList.add("TN-Weakley County");
			countyList.add("TN-White County");
			countyList.add("TN-Williamson County");
			countyList.add("TN-Wilson County");
		} else if(stateCode.equals("TX")){
			countyList.add("TX-Anderson County");
			countyList.add("TX-Andrews County");
			countyList.add("TX-Angelina County");
			countyList.add("TX-Aransas County");
			countyList.add("TX-Archer County");
			countyList.add("TX-Armstrong County");
			countyList.add("TX-Atascosa County");
			countyList.add("TX-Austin County");
			countyList.add("TX-Bailey County");
			countyList.add("TX-Bandera County");
			countyList.add("TX-Bastrop County");
			countyList.add("TX-Baylor County");
			countyList.add("TX-Bee County");
			countyList.add("TX-Bell County");
			countyList.add("TX-Bexar County");
			countyList.add("TX-Blanco County");
			countyList.add("TX-Borden County");
			countyList.add("TX-Bosque County");
			countyList.add("TX-Bowie County");
			countyList.add("TX-Brazoria County");
			countyList.add("TX-Brazos County");
			countyList.add("TX-Brewster County");
			countyList.add("TX-Briscoe County");
			countyList.add("TX-Brooks County");
			countyList.add("TX-Brown County");
			countyList.add("TX-Burleson County");
			countyList.add("TX-Burnet County");
			countyList.add("TX-Caldwell County");
			countyList.add("TX-Calhoun County");
			countyList.add("TX-Callahan County");
			countyList.add("TX-Cameron County");
			countyList.add("TX-Camp County");
			countyList.add("TX-Carson County");
			countyList.add("TX-Cass County");
			countyList.add("TX-Castro County");
			countyList.add("TX-Chambers County");
			countyList.add("TX-Cherokee County");
			countyList.add("TX-Childress County");
			countyList.add("TX-Clay County");
			countyList.add("TX-Cochran County");
			countyList.add("TX-Coke County");
			countyList.add("TX-Coleman County");
			countyList.add("TX-Collin County");
			countyList.add("TX-Collingsworth County");
			countyList.add("TX-Colorado County");
			countyList.add("TX-Comal County");
			countyList.add("TX-Comanche County");
			countyList.add("TX-Concho County");
			countyList.add("TX-Cooke County");
			countyList.add("TX-Coryell County");
			countyList.add("TX-Cottle County");
			countyList.add("TX-Crane County");
			countyList.add("TX-Crockett County");
			countyList.add("TX-Crosby County");
			countyList.add("TX-Culberson County");
			countyList.add("TX-Dallam County");
			countyList.add("TX-Dallas County");
			countyList.add("TX-Dawson County");
			countyList.add("TX-De Witt County");
			countyList.add("TX-Deaf Smith County");
			countyList.add("TX-Delta County");
			countyList.add("TX-Denton County");
			countyList.add("TX-Dickens County");
			countyList.add("TX-Dimmit County");
			countyList.add("TX-Donley County");
			countyList.add("TX-Duval County");
			countyList.add("TX-Eastland County");
			countyList.add("TX-Ector County");
			countyList.add("TX-Edwards County");
			countyList.add("TX-El Paso County");
			countyList.add("TX-Ellis County");
			countyList.add("TX-Erath County");
			countyList.add("TX-Falls County");
			countyList.add("TX-Fannin County");
			countyList.add("TX-Fayette County");
			countyList.add("TX-Fisher County");
			countyList.add("TX-Floyd County");
			countyList.add("TX-Foard County");
			countyList.add("TX-Fort Bend County");
			countyList.add("TX-Franklin County");
			countyList.add("TX-Freestone County");
			countyList.add("TX-Frio County");
			countyList.add("TX-Gaines County");
			countyList.add("TX-Galveston County");
			countyList.add("TX-Garza County");
			countyList.add("TX-Gillespie County");
			countyList.add("TX-Glasscock County");
			countyList.add("TX-Goliad County");
			countyList.add("TX-Gonzales County");
			countyList.add("TX-Gray County");
			countyList.add("TX-Grayson County");
			countyList.add("TX-Gregg County");
			countyList.add("TX-Grimes County");
			countyList.add("TX-Guadalupe County");
			countyList.add("TX-Hale County");
			countyList.add("TX-Hall County");
			countyList.add("TX-Hamilton County");
			countyList.add("TX-Hansford County");
			countyList.add("TX-Hardeman County");
			countyList.add("TX-Hardin County");
			countyList.add("TX-Harris County");
			countyList.add("TX-Harrison County");
			countyList.add("TX-Hartley County");
			countyList.add("TX-Haskell County");
			countyList.add("TX-Hays County");
			countyList.add("TX-Hemphill County");
			countyList.add("TX-Henderson County");
			countyList.add("TX-Hidalgo County");
			countyList.add("TX-Hill County");
			countyList.add("TX-Hockley County");
			countyList.add("TX-Hood County");
			countyList.add("TX-Hopkins County");
			countyList.add("TX-Houston County");
			countyList.add("TX-Howard County");
			countyList.add("TX-Hudspeth County");
			countyList.add("TX-Hunt County");
			countyList.add("TX-Hutchinson County");
			countyList.add("TX-Irion County");
			countyList.add("TX-Jack County");
			countyList.add("TX-Jackson County");
			countyList.add("TX-Jasper County");
			countyList.add("TX-Jeff Davis County");
			countyList.add("TX-Jefferson County");
			countyList.add("TX-Jim Hogg County");
			countyList.add("TX-Jim Wells County");
			countyList.add("TX-Johnson County");
			countyList.add("TX-Jones County");
			countyList.add("TX-Karnes County");
			countyList.add("TX-Kaufman County");
			countyList.add("TX-Kendall County");
			countyList.add("TX-Kenedy County");
			countyList.add("TX-Kent County");
			countyList.add("TX-Kerr County");
			countyList.add("TX-Kimble County");
			countyList.add("TX-King County");
			countyList.add("TX-Kinney County");
			countyList.add("TX-Kleberg County");
			countyList.add("TX-Knox County");
			countyList.add("TX-La Salle County");
			countyList.add("TX-Lamar County");
			countyList.add("TX-Lamb County");
			countyList.add("TX-Lampasas County");
			countyList.add("TX-Lavaca County");
			countyList.add("TX-Lee County");
			countyList.add("TX-Leon County");
			countyList.add("TX-Liberty County");
			countyList.add("TX-Limestone County");
			countyList.add("TX-Lipscomb County");
			countyList.add("TX-Live Oak County");
			countyList.add("TX-Llano County");
			countyList.add("TX-Loving County");
			countyList.add("TX-Lubbock County");
			countyList.add("TX-Lynn County");
			countyList.add("TX-Madison County");
			countyList.add("TX-Marion County");
			countyList.add("TX-Martin County");
			countyList.add("TX-Mason County");
			countyList.add("TX-Matagorda County");
			countyList.add("TX-Maverick County");
			countyList.add("TX-McCulloch County");
			countyList.add("TX-McLennan County");
			countyList.add("TX-McMullen County");
			countyList.add("TX-Medina County");
			countyList.add("TX-Menard County");
			countyList.add("TX-Midland County");
			countyList.add("TX-Milam County");
			countyList.add("TX-Mills County");
			countyList.add("TX-Mitchell County");
			countyList.add("TX-Montague County");
			countyList.add("TX-Montgomery County");
			countyList.add("TX-Moore County");
			countyList.add("TX-Morris County");
			countyList.add("TX-Motley County");
			countyList.add("TX-Nacogdoches County");
			countyList.add("TX-Navarro County");
			countyList.add("TX-Newton County");
			countyList.add("TX-Nolan County");
			countyList.add("TX-Nueces County");
			countyList.add("TX-Ochiltree County");
			countyList.add("TX-Oldham County");
			countyList.add("TX-Orange County");
			countyList.add("TX-Palo Pinto County");
			countyList.add("TX-Panola County");
			countyList.add("TX-Parker County");
			countyList.add("TX-Parmer County");
			countyList.add("TX-Pecos County");
			countyList.add("TX-Polk County");
			countyList.add("TX-Potter County");
			countyList.add("TX-Presidio County");
			countyList.add("TX-Rains County");
			countyList.add("TX-Randall County");
			countyList.add("TX-Reagan County");
			countyList.add("TX-Real County");
			countyList.add("TX-Red River County");
			countyList.add("TX-Reeves County");
			countyList.add("TX-Refugio County");
			countyList.add("TX-Roberts County");
			countyList.add("TX-Robertson County");
			countyList.add("TX-Rockwall County");
			countyList.add("TX-Runnels County");
			countyList.add("TX-Rusk County");
			countyList.add("TX-Sabine County");
			countyList.add("TX-San Augustine County");
			countyList.add("TX-San Jacinto County");
			countyList.add("TX-San Patricio County");
			countyList.add("TX-San Saba County");
			countyList.add("TX-Schleicher County");
			countyList.add("TX-Scurry County");
			countyList.add("TX-Shackelford County");
			countyList.add("TX-Shelby County");
			countyList.add("TX-Sherman County");
			countyList.add("TX-Smith County");
			countyList.add("TX-Somervell County");
			countyList.add("TX-Starr County");
			countyList.add("TX-Stephens County");
			countyList.add("TX-Sterling County");
			countyList.add("TX-Stonewall County");
			countyList.add("TX-Sutton County");
			countyList.add("TX-Swisher County");
			countyList.add("TX-Tarrant County");
			countyList.add("TX-Taylor County");
			countyList.add("TX-Terrell County");
			countyList.add("TX-Terry County");
			countyList.add("TX-Throckmorton County");
			countyList.add("TX-Titus County");
			countyList.add("TX-Tom Green County");
			countyList.add("TX-Travis County");
			countyList.add("TX-Trinity County");
			countyList.add("TX-Tyler County");
			countyList.add("TX-Upshur County");
			countyList.add("TX-Upton County");
			countyList.add("TX-Uvalde County");
			countyList.add("TX-Val Verde County");
			countyList.add("TX-Van Zandt County");
			countyList.add("TX-Victoria County");
			countyList.add("TX-Walker County");
			countyList.add("TX-Waller County");
			countyList.add("TX-Ward County");
			countyList.add("TX-Washington County");
			countyList.add("TX-Webb County");
			countyList.add("TX-Wharton County");
			countyList.add("TX-Wheeler County");
			countyList.add("TX-Wichita County");
			countyList.add("TX-Wilbarger County");
			countyList.add("TX-Willacy County");
			countyList.add("TX-Williamson County");
			countyList.add("TX-Wilson County");
			countyList.add("TX-Winkler County");
			countyList.add("TX-Wise County");
			countyList.add("TX-Wood County");
			countyList.add("TX-Yoakum County");
			countyList.add("TX-Young County");
			countyList.add("TX-Zapata County");
			countyList.add("TX-Zavala County");
		} else if(stateCode.equals("UT")){
			countyList.add("UT-Beaver County");
			countyList.add("UT-Box Elder County");
			countyList.add("UT-Cache County");
			countyList.add("UT-Carbon County");
			countyList.add("UT-Daggett County");
			countyList.add("UT-Davis County");
			countyList.add("UT-Duchesne County");
			countyList.add("UT-Emery County");
			countyList.add("UT-Garfield County");
			countyList.add("UT-Grand County");
			countyList.add("UT-Iron County");
			countyList.add("UT-Juab County");
			countyList.add("UT-Kane County");
			countyList.add("UT-Millard County");
			countyList.add("UT-Morgan County");
			countyList.add("UT-Piute County");
			countyList.add("UT-Rich County");
			countyList.add("UT-Salt Lake County");
			countyList.add("UT-San Juan County");
			countyList.add("UT-Sanpete County");
			countyList.add("UT-Sevier County");
			countyList.add("UT-Summit County");
			countyList.add("UT-Tooele County");
			countyList.add("UT-Uintah County");
			countyList.add("UT-Utah County");
			countyList.add("UT-Wasatch County");
			countyList.add("UT-Washington County");
			countyList.add("UT-Wayne County");
			countyList.add("UT-Weber County");
		} else if(stateCode.equals("VA")){
			countyList.add("VA-Accomack County");
			countyList.add("VA-Albemarle County");
			countyList.add("VA-Alexandria City");
			countyList.add("VA-Alleghany County");
			countyList.add("VA-Amelia County");
			countyList.add("VA-Amherst County");
			countyList.add("VA-Appomattox County");
			countyList.add("VA-Arlington County");
			countyList.add("VA-Augusta County");
			countyList.add("VA-Bath County");
			countyList.add("VA-Bedford City");
			countyList.add("VA-Bedford County");
			countyList.add("VA-Bland County");
			countyList.add("VA-Botetourt County");
			countyList.add("VA-Bristol City");
			countyList.add("VA-Brunswick County");
			countyList.add("VA-Buchanan County");
			countyList.add("VA-Buckingham County");
			countyList.add("VA-Buena Vista City");
			countyList.add("VA-Campbell County");
			countyList.add("VA-Caroline County");
			countyList.add("VA-Carroll County");
			countyList.add("VA-Charles City County");
			countyList.add("VA-Charlotte County");
			countyList.add("VA-Charlottesville City");
			countyList.add("VA-Chesapeake City");
			countyList.add("VA-Chesterfield County");
			countyList.add("VA-Clarke County");
			countyList.add("VA-Colonial Heights City");
			countyList.add("VA-Covington City");
			countyList.add("VA-Craig County");
			countyList.add("VA-Culpeper County");
			countyList.add("VA-Cumberland County");
			countyList.add("VA-Danville City");
			countyList.add("VA-Dickenson County");
			countyList.add("VA-Dinwiddie County");
			countyList.add("VA-Emporia City");
			countyList.add("VA-Essex County");
			countyList.add("VA-Fairfax City");
			countyList.add("VA-Fairfax County");
			countyList.add("VA-Falls Church City");
			countyList.add("VA-Fauquier County");
			countyList.add("VA-Floyd County");
			countyList.add("VA-Fluvanna County");
			countyList.add("VA-Franklin City");
			countyList.add("VA-Franklin County");
			countyList.add("VA-Frederick County");
			countyList.add("VA-Fredericksburg City");
			countyList.add("VA-Galax City");
			countyList.add("VA-Giles County");
			countyList.add("VA-Gloucester County");
			countyList.add("VA-Goochland County");
			countyList.add("VA-Grayson County");
			countyList.add("VA-Greene County");
			countyList.add("VA-Greensville County");
			countyList.add("VA-Halifax County");
			countyList.add("VA-Hampton City");
			countyList.add("VA-Hanover County");
			countyList.add("VA-Harrisonburg City");
			countyList.add("VA-Henrico County");
			countyList.add("VA-Henry County");
			countyList.add("VA-Highland County");
			countyList.add("VA-Hopewell City");
			countyList.add("VA-Isle of Wight County");
			countyList.add("VA-James City County");
			countyList.add("VA-King George County");
			countyList.add("VA-King William County");
			countyList.add("VA-King and Queen County");
			countyList.add("VA-Lancaster County");
			countyList.add("VA-Lee County");
			countyList.add("VA-Lexington City");
			countyList.add("VA-Loudoun County");
			countyList.add("VA-Louisa County");
			countyList.add("VA-Lunenburg County");
			countyList.add("VA-Lynchburg City");
			countyList.add("VA-Madison County");
			countyList.add("VA-Manassas City");
			countyList.add("VA-Manassas Park City");
			countyList.add("VA-Martinsville City");
			countyList.add("VA-Mathews County");
			countyList.add("VA-Mecklenburg County");
			countyList.add("VA-Middlesex County");
			countyList.add("VA-Montgomery County");
			countyList.add("VA-Nelson County");
			countyList.add("VA-New Kent County");
			countyList.add("VA-Newport News City");
			countyList.add("VA-Norfolk City");
			countyList.add("VA-Northampton County");
			countyList.add("VA-Northumberland County");
			countyList.add("VA-Norton City");
			countyList.add("VA-Nottoway County");
			countyList.add("VA-Orange County");
			countyList.add("VA-Page County");
			countyList.add("VA-Patrick County");
			countyList.add("VA-Petersburg City");
			countyList.add("VA-Pittsylvania County");
			countyList.add("VA-Poquoson City");
			countyList.add("VA-Portsmouth City");
			countyList.add("VA-Powhatan County");
			countyList.add("VA-Prince Edward County");
			countyList.add("VA-Prince George County");
			countyList.add("VA-Prince William County");
			countyList.add("VA-Pulaski County");
			countyList.add("VA-Radford City");
			countyList.add("VA-Rappahannock County");
			countyList.add("VA-Richmond City");
			countyList.add("VA-Richmond County");
			countyList.add("VA-Roanoke City");
			countyList.add("VA-Roanoke County");
			countyList.add("VA-Rockbridge County");
			countyList.add("VA-Rockingham County");
			countyList.add("VA-Russell County");
			countyList.add("VA-Salem City");
			countyList.add("VA-Scott County");
			countyList.add("VA-Shenandoah County");
			countyList.add("VA-Smyth County");
			countyList.add("VA-Southampton County");
			countyList.add("VA-Spotsylvania County");
			countyList.add("VA-Stafford County");
			countyList.add("VA-Staunton City");
			countyList.add("VA-Suffolk City");
			countyList.add("VA-Surry County");
			countyList.add("VA-Sussex County");
			countyList.add("VA-Tazewell County");
			countyList.add("VA-Virginia Beach City");
			countyList.add("VA-Warren County");
			countyList.add("VA-Washington County");
			countyList.add("VA-Waynesboro City");
			countyList.add("VA-Westmoreland County");
			countyList.add("VA-Williamsburg City");
			countyList.add("VA-Winchester City");
			countyList.add("VA-Wise County");
			countyList.add("VA-Wythe County");
			countyList.add("VA-York County");
		} else if(stateCode.equals("VI")){
			countyList.add("VI-Saint Croix");
			countyList.add("VI-Saint John");
			countyList.add("VI-Saint Thomas");
		} else if(stateCode.equals("VT")){
			countyList.add("VT-Addison County");
			countyList.add("VT-Bennington County");
			countyList.add("VT-Caledonia County");
			countyList.add("VT-Chittenden County");
			countyList.add("VT-Essex County");
			countyList.add("VT-Franklin County");
			countyList.add("VT-Grand Isle County");
			countyList.add("VT-Lamoille County");
			countyList.add("VT-Orange County");
			countyList.add("VT-Orleans County");
			countyList.add("VT-Rutland County");
			countyList.add("VT-Washington County");
			countyList.add("VT-Windham County");
			countyList.add("VT-Windsor County");
		} else if(stateCode.equals("WA")){
			countyList.add("WA-Adams County");
			countyList.add("WA-Asotin County");
			countyList.add("WA-Benton County");
			countyList.add("WA-Chelan County");
			countyList.add("WA-Clallam County");
			countyList.add("WA-Clark County");
			countyList.add("WA-Columbia County");
			countyList.add("WA-Cowlitz County");
			countyList.add("WA-Douglas County");
			countyList.add("WA-Ferry County");
			countyList.add("WA-Franklin County");
			countyList.add("WA-Garfield County");
			countyList.add("WA-Grant County");
			countyList.add("WA-Grays Harbor County");
			countyList.add("WA-Island County");
			countyList.add("WA-Jefferson County");
			countyList.add("WA-King County");
			countyList.add("WA-Kitsap County");
			countyList.add("WA-Kittitas County");
			countyList.add("WA-Klickitat County");
			countyList.add("WA-Lewis County");
			countyList.add("WA-Lincoln County");
			countyList.add("WA-Mason County");
			countyList.add("WA-Okanogan County");
			countyList.add("WA-Pacific County");
			countyList.add("WA-Pend Oreille County");
			countyList.add("WA-Pierce County");
			countyList.add("WA-San Juan County");
			countyList.add("WA-Skagit County");
			countyList.add("WA-Skamania County");
			countyList.add("WA-Snohomish County");
			countyList.add("WA-Spokane County");
			countyList.add("WA-Stevens County");
			countyList.add("WA-Thurston County");
			countyList.add("WA-Wahkiakum County");
			countyList.add("WA-Walla Walla County");
			countyList.add("WA-Whatcom County");
			countyList.add("WA-Whitman County");
			countyList.add("WA-Yakima County");
		} else if(stateCode.equals("WI")){
			countyList.add("WI-Adams County");
			countyList.add("WI-Ashland County");
			countyList.add("WI-Barron County");
			countyList.add("WI-Bayfield County");
			countyList.add("WI-Brown County");
			countyList.add("WI-Buffalo County");
			countyList.add("WI-Burnett County");
			countyList.add("WI-Calumet County");
			countyList.add("WI-Chippewa County");
			countyList.add("WI-Clark County");
			countyList.add("WI-Columbia County");
			countyList.add("WI-Crawford County");
			countyList.add("WI-Dane County");
			countyList.add("WI-Dodge County");
			countyList.add("WI-Door County");
			countyList.add("WI-Douglas County");
			countyList.add("WI-Dunn County");
			countyList.add("WI-Eau Claire County");
			countyList.add("WI-Florence County");
			countyList.add("WI-Fond du Lac County");
			countyList.add("WI-Forest County");
			countyList.add("WI-Grant County");
			countyList.add("WI-Green County");
			countyList.add("WI-Green Lake County");
			countyList.add("WI-Iowa County");
			countyList.add("WI-Iron County");
			countyList.add("WI-Jackson County");
			countyList.add("WI-Jefferson County");
			countyList.add("WI-Juneau County");
			countyList.add("WI-Kenosha County");
			countyList.add("WI-Kewaunee County");
			countyList.add("WI-La Crosse County");
			countyList.add("WI-Lafayette County");
			countyList.add("WI-Langlade County");
			countyList.add("WI-Lincoln County");
			countyList.add("WI-Manitowoc County");
			countyList.add("WI-Marathon County");
			countyList.add("WI-Marinette County");
			countyList.add("WI-Marquette County");
			countyList.add("WI-Menominee County");
			countyList.add("WI-Milwaukee County");
			countyList.add("WI-Monroe County");
			countyList.add("WI-Oconto County");
			countyList.add("WI-Oneida County");
			countyList.add("WI-Outagamie County");
			countyList.add("WI-Ozaukee County");
			countyList.add("WI-Pepin County");
			countyList.add("WI-Pierce County");
			countyList.add("WI-Polk County");
			countyList.add("WI-Portage County");
			countyList.add("WI-Price County");
			countyList.add("WI-Racine County");
			countyList.add("WI-Richland County");
			countyList.add("WI-Rock County");
			countyList.add("WI-Rusk County");
			countyList.add("WI-Saint Croix County");
			countyList.add("WI-Sauk County");
			countyList.add("WI-Sawyer County");
			countyList.add("WI-Shawano County");
			countyList.add("WI-Sheboygan County");
			countyList.add("WI-Taylor County");
			countyList.add("WI-Trempealeau County");
			countyList.add("WI-Vernon County");
			countyList.add("WI-Vilas County");
			countyList.add("WI-Walworth County");
			countyList.add("WI-Washburn County");
			countyList.add("WI-Washington County");
			countyList.add("WI-Waukesha County");
			countyList.add("WI-Waupaca County");
			countyList.add("WI-Waushara County");
			countyList.add("WI-Winnebago County");
			countyList.add("WI-Wood County");
		} else if(stateCode.equals("WV")){
			countyList.add("WV-Barbour County");
			countyList.add("WV-Berkeley County");
			countyList.add("WV-Boone County");
			countyList.add("WV-Braxton County");
			countyList.add("WV-Brooke County");
			countyList.add("WV-Cabell County");
			countyList.add("WV-Calhoun County");
			countyList.add("WV-Clay County");
			countyList.add("WV-Doddridge County");
			countyList.add("WV-Fayette County");
			countyList.add("WV-Gilmer County");
			countyList.add("WV-Grant County");
			countyList.add("WV-Greenbrier County");
			countyList.add("WV-Hampshire County");
			countyList.add("WV-Hancock County");
			countyList.add("WV-Hardy County");
			countyList.add("WV-Harrison County");
			countyList.add("WV-Jackson County");
			countyList.add("WV-Jefferson County");
			countyList.add("WV-Kanawha County");
			countyList.add("WV-Lewis County");
			countyList.add("WV-Lincoln County");
			countyList.add("WV-Logan County");
			countyList.add("WV-Marion County");
			countyList.add("WV-Marshall County");
			countyList.add("WV-Mason County");
			countyList.add("WV-McDowell County");
			countyList.add("WV-Mercer County");
			countyList.add("WV-Mineral County");
			countyList.add("WV-Mingo County");
			countyList.add("WV-Monongalia County");
			countyList.add("WV-Monroe County");
			countyList.add("WV-Morgan County");
			countyList.add("WV-Nicholas County");
			countyList.add("WV-Ohio County");
			countyList.add("WV-Pendleton County");
			countyList.add("WV-Pleasants County");
			countyList.add("WV-Pocahontas County");
			countyList.add("WV-Preston County");
			countyList.add("WV-Putnam County");
			countyList.add("WV-Raleigh County");
			countyList.add("WV-Randolph County");
			countyList.add("WV-Ritchie County");
			countyList.add("WV-Roane County");
			countyList.add("WV-Summers County");
			countyList.add("WV-Taylor County");
			countyList.add("WV-Tucker County");
			countyList.add("WV-Tyler County");
			countyList.add("WV-Upshur County");
			countyList.add("WV-Wayne County");
			countyList.add("WV-Webster County");
			countyList.add("WV-Wetzel County");
			countyList.add("WV-Wirt County");
			countyList.add("WV-Wood County");
			countyList.add("WV-Wyoming County");
		} else if(stateCode.equals("WY")){
			countyList.add("WY-Albany County");
			countyList.add("WY-Big Horn County");
			countyList.add("WY-Campbell County");
			countyList.add("WY-Carbon County");
			countyList.add("WY-Converse County");
			countyList.add("WY-Crook County");
			countyList.add("WY-Fremont County");
			countyList.add("WY-Goshen County");
			countyList.add("WY-Hot Springs County");
			countyList.add("WY-Johnson County");
			countyList.add("WY-Laramie County");
			countyList.add("WY-Lincoln County");
			countyList.add("WY-Natrona County");
			countyList.add("WY-Niobrara County");
			countyList.add("WY-Park County");
			countyList.add("WY-Platte County");
			countyList.add("WY-Sheridan County");
			countyList.add("WY-Sublette County");
			countyList.add("WY-Sweetwater County");
			countyList.add("WY-Teton County");
			countyList.add("WY-Uinta County");
			countyList.add("WY-Washakie County");
			countyList.add("WY-Weston County");
		}

		return countyList;
	
	}
	
	public  ArrayList<String>  getCountryList() { 

		ArrayList<String> countryList = new ArrayList<String>();

		// Country list from http://www.state.gov/misc/list/

		countryList.add("Afghanistan");
		countryList.add("Albania");
		countryList.add("Algeria");
		countryList.add("Andorra");
		countryList.add("Angola");
		countryList.add("Antigua and Barbuda");
		countryList.add("Argentina");
		countryList.add("Armenia");
		countryList.add("Aruba");
		countryList.add("Australia");
		countryList.add("Austria");
		countryList.add("Azerbaijan");
		countryList.add("Bahamas, The");
		countryList.add("Bahrain");
		countryList.add("Bangladesh");
		countryList.add("Barbados");
		countryList.add("Belarus");
		countryList.add("Belgium");
		countryList.add("Belize");
		countryList.add("Benin");
		countryList.add("Bhutan");
		countryList.add("Bolivia");
		countryList.add("Bosnia and Herzegovina");
		countryList.add("Botswana");
		countryList.add("Brazil");
		countryList.add("Brunei");
		countryList.add("Bulgaria");
		countryList.add("Burkina Faso");
		countryList.add("Burma");
		countryList.add("Burundi");
		countryList.add("Cambodia");
		countryList.add("Cameroon");
		countryList.add("Canada");
		countryList.add("Cape Verde");
		countryList.add("Central African Republic");
		countryList.add("Chad");
		countryList.add("Chile");
		countryList.add("China");
		countryList.add("Colombia");
		countryList.add("Comoros");
		countryList.add("Congo, Democratic Republic of the");
		countryList.add("Congo, Republic of the");
		countryList.add("Costa Rica");
		countryList.add("Cote d'Ivoire");
		countryList.add("Croatia");
		countryList.add("Cuba");
		countryList.add("Curacao");
		countryList.add("Cyprus");
		countryList.add("Czech Republic");
		countryList.add("Denmark");
		countryList.add("Djibouti");
		countryList.add("Dominica");
		countryList.add("Dominican Republic");
		countryList.add("East Timor");
		countryList.add("Ecuador");
		countryList.add("Egypt");
		countryList.add("El Salvador");
		countryList.add("Equatorial Guinea");
		countryList.add("Eritrea");
		countryList.add("Estonia");
		countryList.add("Ethiopia");
		countryList.add("Fiji");
		countryList.add("Finland");
		countryList.add("France");
		countryList.add("Gabon");
		countryList.add("Gambia, The");
		countryList.add("Georgia");
		countryList.add("Germany");
		countryList.add("Ghana");
		countryList.add("Greece");
		countryList.add("Grenada");
		countryList.add("Guatemala");
		countryList.add("Guinea");
		countryList.add("Guinea-Bissau");
		countryList.add("Guyana");
		countryList.add("Haiti");
		countryList.add("Holy See");
		countryList.add("Honduras");
		countryList.add("Hong Kong");
		countryList.add("Hungary");
		countryList.add("Iceland");
		countryList.add("India");
		countryList.add("Indonesia");
		countryList.add("Iran");
		countryList.add("Iraq");
		countryList.add("Ireland");
		countryList.add("Israel");
		countryList.add("Italy");
		countryList.add("Jamaica");
		countryList.add("Japan");
		countryList.add("Jordan");
		countryList.add("Kazakhstan");
		countryList.add("Kenya");
		countryList.add("Kiribati");
		countryList.add("Korea, North");
		countryList.add("Korea, South");
		countryList.add("Kosovo");
		countryList.add("Kuwait");
		countryList.add("Kyrgyzstan");
		countryList.add("Laos");
		countryList.add("Latvia");
		countryList.add("Lebanon");
		countryList.add("Lesotho");
		countryList.add("Liberia");
		countryList.add("Libya");
		countryList.add("Liechtenstein");
		countryList.add("Lithuania");
		countryList.add("Luxembourg");
		countryList.add("Macau");
		countryList.add("Macedonia");
		countryList.add("Madagascar");
		countryList.add("Malawi");
		countryList.add("Malaysia");
		countryList.add("Maldives");
		countryList.add("Mali");
		countryList.add("Malta");
		countryList.add("Marshall Islands");
		countryList.add("Mauritania");
		countryList.add("Mauritius");
		countryList.add("Mexico");
		countryList.add("Micronesia");
		countryList.add("Moldova");
		countryList.add("Monaco");
		countryList.add("Mongolia");
		countryList.add("Montenegro");
		countryList.add("Morocco");
		countryList.add("Mozambique");
		countryList.add("Namibia");
		countryList.add("Nauru");
		countryList.add("Nepal");
		countryList.add("Netherlands");
		countryList.add("Netherlands Antilles");
		countryList.add("New Zealand");
		countryList.add("Nicaragua");
		countryList.add("Niger");
		countryList.add("Nigeria");
		countryList.add("North Korea");
		countryList.add("Norway");
		countryList.add("Oman");
		countryList.add("Pakistan");
		countryList.add("Palau");
		countryList.add("Palestinian Territories");
		countryList.add("Panama");
		countryList.add("Papua New Guinea");
		countryList.add("Paraguay");
		countryList.add("Peru");
		countryList.add("Philippines");
		countryList.add("Poland");
		countryList.add("Portugal");
		countryList.add("Qatar");
		countryList.add("Romania");
		countryList.add("Russia");
		countryList.add("Rwanda");
		countryList.add("Saint Kitts and Nevis");
		countryList.add("Saint Lucia");
		countryList.add("Saint Vincent and the Grenadines");
		countryList.add("Samoa");
		countryList.add("San Marino");
		countryList.add("Sao Tome and Principe");
		countryList.add("Saudi Arabia");
		countryList.add("Senegal");
		countryList.add("Serbia");
		countryList.add("Seychelles");
		countryList.add("Sierra Leone");
		countryList.add("Singapore");
		countryList.add("Sint Maarten");
		countryList.add("Slovakia");
		countryList.add("Slovenia");
		countryList.add("Solomon Islands");
		countryList.add("Somalia");
		countryList.add("South Africa");
		countryList.add("South Korea");
		countryList.add("South Sudan");
		countryList.add("Spain");
		countryList.add("Sri Lanka");
		countryList.add("Sudan");
		countryList.add("Suriname");
		countryList.add("Swaziland");
		countryList.add("Sweden");
		countryList.add("Switzerland");
		countryList.add("Syria");
		countryList.add("Taiwan");
		countryList.add("Tajikistan");
		countryList.add("Tanzania");
		countryList.add("Thailand");
		countryList.add("Timor-Leste");
		countryList.add("Togo");
		countryList.add("Tonga");
		countryList.add("Trinidad and Tobago");
		countryList.add("Tunisia");
		countryList.add("Turkey");
		countryList.add("Turkmenistan");
		countryList.add("Tuvalu");
		countryList.add("Uganda");
		countryList.add("Ukraine");
		countryList.add("United Arab Emirates");
		countryList.add("United Kingdom");
		countryList.add("United States");
		countryList.add("Uruguay");
		countryList.add("Uzbekistan");
		countryList.add("Vanuatu");
		countryList.add("Venezuela");
		countryList.add("Vietnam");
		countryList.add("Yemen");
		countryList.add("Zambia");
		countryList.add("Zimbabwe");
		
		return countryList;
	}
	
	public  ArrayList<DropDown>  getMonthList() { 

		ArrayList<DropDown> monthList = new ArrayList<DropDown>();		
		
		Map<String, String> monthMap = new HashMap<String, String>();
		
		monthMap.put("01", "January");
		monthMap.put("02", "February");
		monthMap.put("03", "March");
		monthMap.put("04", "April");
		monthMap.put("05", "May");
		monthMap.put("06", "June");
		monthMap.put("07", "July");
		monthMap.put("08", "August");
		monthMap.put("09", "September");
		monthMap.put("10", "October");
		monthMap.put("11", "November");
		monthMap.put("12", "December");

		DropDown dd;
		for (Map.Entry<String, String> entry : monthMap.entrySet())
		{
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    dd.setValue(entry.getValue());
		    monthList.add(dd);
		}
		
		Collections.sort(monthList, new DropDownKeyComparator());
		
		return monthList;
	}
	
	
	public  ArrayList<DropDown>  getPaymentTypeList(String fee_option) { 

		ArrayList<DropDown> paymentTypeList = new ArrayList<DropDown>();		
		
		Map<String, String> paymentTypeMap = new HashMap<String, String>();
		
		paymentTypeMap.put("PO", "Purchase Order");
		paymentTypeMap.put("CC", "Credit Card");
		if (!fee_option.equals("Facility-fee"))  // facility fee does not need pay later
		    paymentTypeMap.put("PL", "Call Billing Contact");
		
		DropDown dd;
		for (Map.Entry<String, String> entry : paymentTypeMap.entrySet())
		{
		    dd = new DropDown();
		    dd.setKey(entry.getKey());
		    dd.setValue(entry.getValue());
		    paymentTypeList.add(dd);
		}
		
		Collections.sort(paymentTypeList, new DropDownKeyComparator());
		
		dd = new DropDown();
	    dd.setKey("");
	    dd.setValue("Select Payment Type");
	    paymentTypeList.add(0, dd);
		
		return paymentTypeList;
	}
	
	public  String  getPaymentTypeLabel(String paymentTypeID) {
		if (paymentTypeID == null) {
			return "";
		}
		
		if (paymentTypeID.equals("PO")) {
			return "Purchase Order";
		} else if (paymentTypeID.equals("CC")) {
			return "Credit Card";
		} else if (paymentTypeID.equals("PL")) {
			return "Call Billing Contact";
		} else if (paymentTypeID.equals("CK")) {
			return "Check";
		} else if (paymentTypeID.equals("WT")) {
			return "Cash";
		} else if (paymentTypeID.equals("BD")) {
			return "Tracked-in-Finance";
		} else {
			return "";
		}
	}
	
	public  ArrayList<CourseRecordSearch>  getSearchResultSet(String personID, String feeOption, boolean showAll) { 
		
		ArrayList<CourseRecordSearch> searchResultSet = new ArrayList<CourseRecordSearch>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		User user = new User();
		
		if (feeOption.equals(user.FEE_BASED))
			sabaResponse = findSabaUsersFeeCoursesRecordSheets(personID, showAll);
		else if (feeOption.equals(user.FACILITY_FEE_BASED))
		    sabaResponse = findSabaUsersFacilityCoursesRecordSheets(personID, showAll);
		else
			sabaResponse = findSabaUsersCoursesRecordSheets(personID, showAll);
		 
		for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{
			CourseRecordSearch courseRecordSearh = new CourseRecordSearch();
			courseRecordSearh.setSheetNumberID(entry.getKey());
			String[] data = entry.getValue();
	    	for(int d=0; d<data.length; d++){

	    		//System.out.println("getSearchResultSet() key: " + entry.getKey() + " data("+d+") value ="+data[d]); 
	    
	    		switch (d) {
		            case 0: courseRecordSearh.setSheetNumber(data[d]);
		                    break;
		            case 1: courseRecordSearh.setOrganizationName(data[d]);
			                break;
		            case 2: courseRecordSearh.setOrganizationID(data[d]);
	                		break;  
		            case 3: courseRecordSearh.setCourseCode(data[d]);
		            		//courseRecordSearh.setCourseCode(getCourseName(data[d]));
	        				break; 
		            case 4: courseRecordSearh.setCourseName(data[d]);
    						break;  
		            case 5: courseRecordSearh.setEndDate(data[d]);
	        				break; 
		            case 6: courseRecordSearh.setStatus(data[d]);
	        				break;
		            case 7: courseRecordSearh.setCertificatesIssued(data[d] != null && data[d].equalsIgnoreCase("Y")?true:false);
	        				break; 
		            default:
		            		break;
	    		}
	    	}
	    	
	    	searchResultSet.add( courseRecordSearh);
			
		}
		
		//Collections.sort(searchResultSet, new SearchResultComparator());		
		//Comparator<CourseRecordSearch> searchResultComparator = new SearchResultComparator();
		//Collections.sort(searchResultSet, Collections.reverseOrder(searchResultComparator));
		
		Collections.sort(searchResultSet);

		return searchResultSet;

	}
	
	public  CourseRecord  getCourseRecordStudents(CourseRecord courseRecord) { 
		
		Map<String, Map<String, String>> sabaResponse = new HashMap<String, Map<String, String>>();
		
		List<Student> studentRoster = new ArrayList<Student>();		
		
		
		//System.out.println("SabaHelper getCourseRecordStudents SheetNumberID: " + courseRecord.getSheetNumberID());
		try {
			sabaResponse = getSabaWrapper().getStudents(courseRecord.getSheetNumberID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("SabaHelper getCourseRecordStudents Saba Response: " + sabaResponse);
		
    	for (Map.Entry<String, Map<String, String>> entry : sabaResponse.entrySet())
		{
    		//System.out.println("SabaHelper getCourseRecordStudents Student ID: " + entry.getKey());
    		Student student = new Student();
    		student.setCourseComponents(student.cloneCourseComponents(courseRecord.getCourseComponents()));
    		student.setId(entry.getKey());
    		
    		Map<String, String> studentDetails = entry.getValue();
    		
    		student.setFirstName(studentDetails.get("first_name"));
    		student.setLastName(studentDetails.get("last_name"));
    		student.setEmail(studentDetails.get("email"));
    		student.setPhoneNumber(studentDetails.get("phone_number"));
    		student.setAddlInfo(studentDetails.get("addl_info"));
    		student.setPostalCode(null);
    		
    		for(Map.Entry<String, String> courseComponentEntry : entry.getValue().entrySet()) {
    			if(courseComponentEntry.getKey().contains("cours")){
        			//System.out.println("SabaHelper getCourseRecordStudents CourseComponent Elements: " + courseComponentEntry.getKey() + " : " + courseComponentEntry.getValue());
    				student.setCourseComponentValue(courseComponentEntry.getKey(), courseComponentEntry.getValue());
    			}
    		}
    		
    		studentRoster.add(student);
		}
    	
    	if(!studentRoster.isEmpty())
    		Collections.sort(studentRoster, new StudentComparator());

		
		courseRecord.setStudents(studentRoster);
		
		return courseRecord;
		 
	}
	
	
	public  String  addCourseRecordStudents(String sheetNumberID, Student student) { 
		
		String sabaResponse = null;
		
		Map<String,String> studentDetail = new HashMap<String,String>();
		Map<String,String> transcripts = new HashMap<String,String>();
			
		studentDetail.put("first_name", student.getFirstName());
		studentDetail.put("last_name", student.getLastName());
		studentDetail.put("email", student.getEmail());
		studentDetail.put("phone", student.getPhoneNumber());
		studentDetail.put("postal_code", student.getPostalCode());
		studentDetail.put("addl_info", student.getAddlInfo());
		
		for(CourseComponent courseComponent : student.getCourseComponents()){
			transcripts.put(courseComponent.getCourseComponentID(), courseComponent.getCourseComponentValue());
		}
		
		//System.out.println("SabaHelper addCourseRecordStudents SheetNumberID: " + sheetNumberID);
		//System.out.println("SabaHelper addCourseRecordStudents studentDetail Map: " + studentDetail);
		//System.out.println("SabaHelper addCourseRecordStudents transcripts Map: " + transcripts);
		

		try {
			sabaResponse = getSabaWrapper().addStudents(sheetNumberID, studentDetail, transcripts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			sabaResponse = "Error";
			e.printStackTrace();
		}
				
		return sabaResponse;
		 
	}
	
	public String updateCourseRecordStudents(String sheetNumberID, Student student) { 
		
		String sabaResponse = null;
		
		Map<String,String> studentDetail = new HashMap<String,String>();
		Map<String,String> transcripts = new HashMap<String,String>();
			
		if(student.getId() != null){
			studentDetail.put("entryId", student.getId());
			studentDetail.put("first_name", student.getFirstName());
			studentDetail.put("last_name", student.getLastName());
			studentDetail.put("email", student.getEmail());
			studentDetail.put("phone", student.getPhoneNumber());
			studentDetail.put("postal_code", student.getPostalCode());
			studentDetail.put("addl_info", student.getAddlInfo());
			
			for(CourseComponent courseComponent : student.getCourseComponents()){
				transcripts.put(courseComponent.getCourseComponentID(), courseComponent.getCourseComponentValue());
			}
			
			//System.out.println("SabaHelper updateCourseRecordStudents SheetNumberID: " + sheetNumberID);
			//System.out.println("SabaHelper updateCourseRecordStudents studentDetail Map: " + studentDetail);
			//System.out.println("SabaHelper updateCourseRecordStudents transcripts Map: " + transcripts);
			
			try {
				getSabaWrapper().updateStudents(sheetNumberID, studentDetail, transcripts);
				sabaResponse = student.getId();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				sabaResponse = "Error";
				e.printStackTrace();
			}
		
		} else {
			sabaResponse = "Error";
		}

		return sabaResponse;
		 
	}
	
	
	public boolean removeCourseRecordStudents(String sheetNumberID, String studentID) {
		
		boolean successful = true;
		
		try {
			getSabaWrapper().removeStudents(sheetNumberID, studentID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			successful = false;
			e.printStackTrace();
		}
		
		return successful;
	}
	
	
	
	public  CourseRecord  getCourseRecordInstructors(CourseRecord courseRecord) { 
			
			List<People> instructorRoster = new ArrayList<People>();
			Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
			
			try {
				sabaResponse = getSabaWrapper().findInstructorForCrs(courseRecord.getSheetNumberID());
				//System.out.println("Saba Find Instructor Response: " + sabaResponse);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    	for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
			{

		        int index = 0;
		        People instructor = new People();
		        
		        instructor.setId(entry.getKey());
		        
		        for (String value : entry.getValue())
		        {
		        	//System.out.println("getCourseRecordInstructors Key: " + entry.getKey() + " value " + value);
		        	
			        switch (index) {
			        

		            	case 0: instructor.setUserName(value);
    							break;
			            case 1: instructor.setFirstName(value);
			                    break;
			            case 2: instructor.setLastName(value);
				                break;

	  
			            default:
			            		break;
		    		}	
		    		index++;
					    	
				}
		        instructorRoster.add(instructor);
		    }
			

	    	courseRecord.setInstructors(instructorRoster);
	    	
			return courseRecord;
			
			 
	}
	
	public  CourseRecord  getAvailableCourseRecordInstructors(CourseRecord courseRecord) { 
		
		List<People> availableInstructors = new ArrayList<People>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		if(courseRecord.getOrganizationID() != null && !courseRecord.getOrganizationID().equals(""))
			sabaResponse = findSabaOrganizationsInstructors(courseRecord);
		

    	for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{

	        int index = 0;
	        People instructor = new People();
	        
	        instructor.setId(entry.getKey());
	        
	        for (String value : entry.getValue())
	        {
	        	//System.out.println("getAvailableCourseRecordInstructors Key: " + entry.getKey() + " value " + value);
	        	
		        switch (index) {
		        
		            case 0: instructor.setUserName(value);
            				break;
		            case 1: instructor.setFirstName(value);
		                    break;
		            case 2: instructor.setLastName(value);
			                break;
  
		            default:
		            		break;
	    		}	
	    		index++;
				    	
			}
	        availableInstructors.add(instructor);
	    }
		
    	
    	if(!availableInstructors.isEmpty())
    		Collections.sort(availableInstructors, new InstructorComparator());

    	courseRecord.setInstructors(availableInstructors);
    	
		return courseRecord;
		
		 
	}

	public  Map<String, String[]> findSabaOrganizationsInstructors(CourseRecord courseRecord){
		
		Map<String, String> sabaRequest = new HashMap<String, String>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		//System.out.println("findSabaOrganizationsInstructors with OrganizationID " + courseRecord.getOrganizationID() );
		
		sabaRequest.put("organization", courseRecord.getOrganizationID());
		sabaRequest.put("instructorUserName", courseRecord.getContactID());
		sabaRequest.put("instructorFName", courseRecord.getContactFirstName());
		sabaRequest.put("instructorLName", courseRecord.getContactLastName());
		
		//System.out.println("findSabaOrganizationsInstructors Request: " + sabaRequest);
		try {
			sabaResponse = getSabaWrapper().findInstructor(sabaRequest);
			//System.out.println("findSabaOrganizationsInstructors Response: " + sabaResponse);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return sabaResponse;
	}
	
	public  List<People> getAvailableOfferingInstructors(String orgId, String fname, String lname, String offeringId) throws Exception
	{
		
		Map<String, String> sabaRequest = new HashMap<String, String>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		sabaRequest.put("organization", orgId);
		sabaRequest.put("instructorFName", fname);
		sabaRequest.put("instructorLName", lname);
		
		sabaResponse = getSabaWrapper().findInstructor(sabaRequest);
		
		List<People> instructors = new ArrayList<People>();
		for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{
	        People instructor = new People();
	        instructor.setId(entry.getKey());
	        String[] instrInfo = entry.getValue();
	        instructor.setUserName(instrInfo[0]);
	        instructor.setFirstName(instrInfo[1]);
	        instructor.setLastName(instrInfo[2]);
	        instructors.add(instructor);
	    }
		
    	if(!instructors.isEmpty())
    		Collections.sort(instructors, new InstructorComparator());

    	return instructors;
	}
	
	
	public boolean removeCourseRecordInstructor(String sheetNumberID, String personID){
		boolean successful = false;

		try {
			getSabaWrapper().removeInstructor(sheetNumberID, personID);
			successful = true;
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return successful;
	}
	
	public boolean addCourseRecordInstructor(String sheetNumberID, String personID){
		boolean successful = false;

		try {
			getSabaWrapper().addInstructor(sheetNumberID, personID);
			successful = true;
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return successful;
	}

	public  Map<String, String[]> findSabaUsersCoursesRecordSheets(String personID, boolean showAll){
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		try {
			sabaResponse = getSabaWrapper().findCRSForLandingPage(personID, !showAll);
			//System.out.println("SabaHelper findCRSForLandingPage Contact (" + personID + ") Search Result size : " + sabaResponse.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}

	public  Map<String, String[]> findSabaUsersFeeCoursesRecordSheets(String personID, boolean showAll){
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		try {
			sabaResponse = getSabaWrapper().findFeeBasedCRSForLandingPage(personID, !showAll);
			//System.out.println("SabaHelper findCRSForLandingPage Contact (" + personID + ") Search Result size : " + sabaResponse.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}
	
	public  Map<String, String[]> findSabaUsersOfferings(String personID){
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		try {
			sabaResponse = getSabaWrapper().findOfferings(personID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}
	
	public  Map<String, String[]> findSabaUsersFacilityCoursesRecordSheets(String personID, boolean showAll){
        Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
        
        try {
            sabaResponse = getSabaWrapper().findFacilityFeeCRSForLandingPage(personID, !showAll);
            //System.out.println("SabaHelper findCRSForLandingPage Contact (" + personID + ") Search Result size : " + sabaResponse.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return sabaResponse;
        
    }
	
	public List<CourseComponent> getCourseComponent(String courseID){
		

		
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		try {
			sabaResponse = getSabaWrapper().getCourseComponent(courseID);
			//System.out.println("SabaHelper getCourseComponent CourseID (" + courseID + ") Course Component Map: " + sabaResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<CourseComponent> componentList = new ArrayList<CourseComponent>();
		
		for (Map.Entry<String, String> entry : sabaResponse.entrySet()) {
			CourseComponent courseComponent = new CourseComponent();
			courseComponent.setCourseComponentID(entry.getKey());
			courseComponent.setCourseComponentLabel(entry.getValue());
			componentList.add(courseComponent);
		}

		return componentList;
	}
	
	
	public  boolean isCourseRequiresStudentDetails(String courseID) { 
		
		boolean required = false;
		//System.out.println("SabaHelper isCourseRequiresStudentDetails CourseID (" + courseID + ")");

		try {
			required =  getSabaWrapper().isSkipStudentsAllowed(courseID);
			//System.out.println("SabaHelper isCourseRequiresStudentDetails CourseID (" + courseID + ") : " + required);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (!required);

	}

	public  Map<String, String> findSabaCourses(){
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		try {
			sabaResponse = getSabaWrapper().findCourse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}
	
	public  Map<String, String> findSabaCourses(String courseCategory){
        Map<String, String> sabaResponse = new HashMap<String, String>();
        
        try {
            sabaResponse = getSabaWrapper().findCourse(courseCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return sabaResponse;
        
    }
	
	public  Map<String, String> findSabaFacilityBasedCourses(){
        Map<String, String> sabaResponse = new HashMap<String, String>();
        
        try {
            sabaResponse = getSabaWrapper().findCoursesForFacilityFee();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return sabaResponse;
        
    }
	
	public  Map<String, String[]> findSabaFeeBasedCourses(String courseCategory){
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		try {
			sabaResponse = getSabaWrapper().findCourseForFeeBasedCrs(courseCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return sabaResponse;
		
	}
	
	public  Map<String, String> findCourseForAquatics(){
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		try {
			sabaResponse = getSabaWrapper().findCourseForAquatics();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}
	
	public  Map<String, String> findSabaOrganizations(String personID){
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		try {
			sabaResponse = getSabaWrapper().getOrgsForContact(personID);
			//System.out.println("SabaHelper findSabaOrganizations Contact (" + personID + ") Organization Map: " + sabaResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}
	
	public  Map<String, String[]> findSabaOrganizationsWithUnitCode(String personID){
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		try {
			sabaResponse = getSabaWrapper().getOrgsForContactWUnitCode(personID);
			//System.out.println("SabaHelper findSabaOrganizationsWithUnitCode Contact (" + personID + ") Organization Map: " + sabaResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}
	
	public Map<String, String> findSessionDeliveryTypes(String courseId)
	{
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		try {
			sabaResponse = getSabaWrapper().getSessionDeliveryTypes(courseId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
	}
	
	
	public Map<String, String> findFacilities(String orgId)
	{
		Map<String, String> sabaResponse = new HashMap<String, String>();
		
		try {
			sabaResponse = getSabaWrapper().getFacilities(orgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
	}
	
	
	public List<String[]> findCoursesForSessionOfferings(boolean isBlended, String category)
	{
		List<String[]> sabaResponse = new ArrayList<String[]>();
		
		try {
			sabaResponse = getSabaWrapper().getCoursesForSessionOfferings(isBlended, category);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sabaResponse;
	}
	
		
	

	public  String courseRecordSheetPreCreateNotification(String sheetNumberID){
		String message = "Successful";
		
		try {
			getSabaWrapper().crsPreCreateNotification(sheetNumberID);
		} catch (Exception e) {
			message = "Error";
			e.printStackTrace();
		}
		
		return message;
		
	}
	
	
	
	public  String deleteCourseRecordSheet(String sheetNumberID){
		String message = "Successful";
		
		try {
			getSabaWrapper().deleteCRS(sheetNumberID);
		} catch (Exception e) {
			message = "Error";
			e.printStackTrace();
		}
		
		return message;
		
	}
	
	
	public String createSabaFeeBasedCourseRecordSheet(CourseRecord courseRecord){
		
		String sabaResponse = null;
		
		List<String> courseRecordInstructorList = new ArrayList<String>();
		Map<String,String> courseRecordSheet = new HashMap<String,String>();
		
		courseRecordSheet.put("contact_id",courseRecord.getContactID());
		courseRecordSheet.put("orgId",courseRecord.getOrganizationID());
		courseRecordSheet.put("course_id", courseRecord.getCourseID());
		courseRecordSheet.put("skipStudents", courseRecord.isStudentDetails()?"true":"false");
		courseRecordSheet.put("trainingCenterName", courseRecord.getTrainingCenterName());
		courseRecordSheet.put("address", courseRecord.getStreetAddress());
		courseRecordSheet.put("city", courseRecord.getCity());
		courseRecordSheet.put("state", courseRecord.getState());
		courseRecordSheet.put("zip", courseRecord.getZipCode());
		courseRecordSheet.put("county", courseRecord.getCounty());
		courseRecordSheet.put("noOfStudents", courseRecord.getTotalStudents());
		courseRecordSheet.put("totalSuccessful", courseRecord.getTotalSuccessful());
		courseRecordSheet.put("totalUnsuccessful", courseRecord.getTotalUnsuccessful());
		courseRecordSheet.put("totalNotEvaluated", courseRecord.getTotalNotEvaluated());
		courseRecordSheet.put("end_date", courseRecord.getEndDate());
		courseRecordSheet.put("comments", courseRecord.getComments());
		courseRecordSheet.put("payType", courseRecord.getPayment().getPaymentTypeID());
		courseRecordSheet.put("poId", courseRecord.getPayment().getPurchaseOrderID());
		
        for (People instructor : courseRecord.getInstructors()) {
        	courseRecordInstructorList.add(instructor.getId());
        }
        
        //System.out.println("SabaHelper createSabaFeeBasedCourseRecordSheet");
		//System.out.println("SabaHelper Course Record Map: " + courseRecordSheet);
		//System.out.println("SabaHelper Course Instructor Map: " + courseRecordInstructorList);
        
		try {
			sabaResponse = getSabaWrapper().createCrs(courseRecordSheet, courseRecordInstructorList);
		} catch (Exception e) {
			String defaultMsg = "Error Creating Course Record. Please contact your administrator.";
			String context = courseRecord.getContactID() + ":" + courseRecord.getOrganizationID() + ":" + courseRecord.getCourseID() + ":" + courseRecord.getEndDate();
			sabaResponse = handleException(e, context, defaultMsg);
			sabaResponse = "Error:" + sabaResponse;
		}
		
		return sabaResponse;
	}
	
	public String updateSabaFeeBasedCourseRecordSheet(CourseRecord courseRecord){
		
		String sabaResponse = null;
		
		Map<String,String> courseRecordSheet = new HashMap<String,String>();
		
		courseRecordSheet.put("crsId", courseRecord.getSheetNumberID());
		courseRecordSheet.put("contact_id",courseRecord.getContactID());
		courseRecordSheet.put("orgId",courseRecord.getOrganizationID());
		courseRecordSheet.put("course_id", courseRecord.getCourseID());
		courseRecordSheet.put("skipStudents", courseRecord.isStudentDetails()?"true":"false");
		courseRecordSheet.put("trainingCenterName", courseRecord.getTrainingCenterName());
		courseRecordSheet.put("address", courseRecord.getStreetAddress());
		courseRecordSheet.put("city", courseRecord.getCity());
		courseRecordSheet.put("state", courseRecord.getState());
		courseRecordSheet.put("zip", courseRecord.getZipCode());
		courseRecordSheet.put("county", courseRecord.getCounty());
		courseRecordSheet.put("noOfStudents", courseRecord.getTotalStudents());
		courseRecordSheet.put("totalSuccessful", courseRecord.getTotalSuccessful());
		courseRecordSheet.put("totalUnsuccessful", courseRecord.getTotalUnsuccessful());
		courseRecordSheet.put("totalNotEvaluated", courseRecord.getTotalNotEvaluated());
		courseRecordSheet.put("comments", courseRecord.getComments());
		courseRecordSheet.put("end_date", courseRecord.getEndDate());
		courseRecordSheet.put("certs_issued", courseRecord.isCertificatesIssued()?"Y":"N");


		
		//System.out.println("SabaHelper updateSabaFeeBasedCourseRecordSheet");		
		//System.out.println("SabaHelper Course Record Map: " + courseRecordSheet);

		try {
			getSabaWrapper().updateCrs(courseRecordSheet);
			sabaResponse = "Successful";
		} catch (Exception e) {
			sabaResponse = "Error";
			e.printStackTrace();
		}
		
		return sabaResponse;
	}
	
	public String updateSabaFacilityFeeBasedCourseRecordSheet(CourseRecord courseRecord){
        
        String sabaResponse = null;
        
        Map<String,String> courseRecordSheet = new HashMap<String,String>();
        
        courseRecordSheet.put("crsId", courseRecord.getSheetNumberID());
        courseRecordSheet.put("contact_id",courseRecord.getContactID());
        courseRecordSheet.put("orgId",courseRecord.getOrganizationID());
        courseRecordSheet.put("course_id", courseRecord.getCourseID());
        courseRecordSheet.put("trainingCenterName", courseRecord.getTrainingCenterName());
        courseRecordSheet.put("address", courseRecord.getStreetAddress());
        courseRecordSheet.put("city", courseRecord.getCity());
        courseRecordSheet.put("state", courseRecord.getState());
        courseRecordSheet.put("zip", courseRecord.getZipCode());
        courseRecordSheet.put("county", courseRecord.getCounty());
        courseRecordSheet.put("end_date", courseRecord.getEndDate());
        
        // needed to avoid update error (not relevant to facility fee based course though)
        courseRecordSheet.put("skipStudents", "true");
        courseRecordSheet.put("noOfStudents", "1");
        courseRecordSheet.put("totalSuccessful", "1");
        courseRecordSheet.put("totalUnsuccessful", "0");
        courseRecordSheet.put("totalNotEvaluated", "0");
        
        //System.out.println("SabaHelper updateSabaFacilityFeeBasedCourseRecordSheet");     
        //System.out.println("SabaHelper Course Record Map: " + courseRecordSheet);
        
        try {
            getSabaWrapper().updateCrs(courseRecordSheet);
            sabaResponse = "Successful";
        } catch (Exception e) {
            sabaResponse = "Error";
            e.printStackTrace();
        }
        
        return sabaResponse;
    }
	
	public  String createSabaCourseRecordSheet(CourseRecord courseRecord){
		
		String sabaResponse = null;
		
		List<String> courseRecordInstructorList = new ArrayList<String>();
		Map<String,String> courseRecordSheet = new HashMap<String,String>();
		Map<String,String> courseRecordSheetStatistics = new HashMap<String,String>();
		
		courseRecordSheet.put("contact_id",courseRecord.getContactID());
		courseRecordSheet.put("orgId",courseRecord.getOrganizationID());
		courseRecordSheet.put("course_id", courseRecord.getCourseID());
		courseRecordSheet.put("trainingCenterName", courseRecord.getTrainingCenterName());
		courseRecordSheet.put("address", courseRecord.getStreetAddress());
		courseRecordSheet.put("city", courseRecord.getCity());
		courseRecordSheet.put("state", courseRecord.getState());
		courseRecordSheet.put("zip", courseRecord.getZipCode());
		courseRecordSheet.put("county", courseRecord.getCounty());
		courseRecordSheet.put("noOfStudents", courseRecord.getTotalStudents());
		courseRecordSheet.put("totalSuccessful", courseRecord.getTotalSuccessful());
		courseRecordSheet.put("totalUnsuccessful", courseRecord.getTotalUnsuccessful());
		courseRecordSheet.put("totalNotEvaluated", courseRecord.getTotalNotEvaluated());
		courseRecordSheet.put("end_date", courseRecord.getEndDate());
		
		courseRecordSheetStatistics.put("target_children", (courseRecord.getStatistics().isYouth()?"1":"0"));
		courseRecordSheetStatistics.put("target_adults", (courseRecord.getStatistics().isAdults()?"1":"0"));
		courseRecordSheetStatistics.put("target_low_income", (courseRecord.getStatistics().isLowIncome()?"1":"0"));
		courseRecordSheetStatistics.put("target_minority", (courseRecord.getStatistics().isMinority()?"1":"0"));
		courseRecordSheetStatistics.put("target_seniors", (courseRecord.getStatistics().isSeniors()?"1":"0"));
		courseRecordSheetStatistics.put("target_functional_needs", (courseRecord.getStatistics().isFunctionalNeeds()?"1":"0"));
		courseRecordSheetStatistics.put("target_rural", (courseRecord.getStatistics().isRural()?"1":"0"));
		courseRecordSheetStatistics.put("target_military", (courseRecord.getStatistics().isMilitary()?"1":"0"));
		courseRecordSheetStatistics.put("other_2013_learn_to_swim", ""+courseRecord.getStatistics().getIsLearnToSwim());
		courseRecordSheetStatistics.put("other_rating_member", ""+courseRecord.getStatistics().getIsRatingMember());
		courseRecordSheetStatistics.put("other_americorps", ""+courseRecord.getStatistics().getIsAmeriCorps());
	
        for (People instructor : courseRecord.getInstructors()) {
        	courseRecordInstructorList.add(instructor.getId());
        }
		
		//System.out.println("SabaHelper Course Record Map: " + courseRecordSheet);
		//System.out.println("SabaHelper Statistics Map: " + courseRecordSheetStatistics);
		//System.out.println("SabaHelper Course Instructor Map: " + courseRecordInstructorList);
		
		try {
			//System.out.println("SabaHelper createAnonymousNonFeeCrs");
			sabaResponse = getSabaWrapper().createAnonymousNonFeeCrs(courseRecordSheet, courseRecordSheetStatistics, courseRecordInstructorList);
		} catch (Exception e) {
			sabaResponse = "Error";
			e.printStackTrace();
		}
		
		return sabaResponse;
		
	}
	
	public String createSabaFacilityFeeBasedCourseRecordSheet(CourseRecord courseRecord){
        
        String sabaResponse = null;
        
        List<String> courseRecordInstructorList = new ArrayList<String>();
        Map<String,String> courseRecordSheet = new HashMap<String,String>();
        
        //fields - Map The map should contain following key/values contact_id, orgId, course_id, trainingCenterName, address, city, state, zip, county, end_date.
        courseRecordSheet.put("contact_id",courseRecord.getContactID());
        courseRecordSheet.put("orgId",courseRecord.getOrganizationID());
        courseRecordSheet.put("course_id", courseRecord.getCourseID());
        courseRecordSheet.put("trainingCenterName", courseRecord.getTrainingCenterName());
        courseRecordSheet.put("address", courseRecord.getStreetAddress());
        courseRecordSheet.put("city", courseRecord.getCity());
        courseRecordSheet.put("state", courseRecord.getState());
        courseRecordSheet.put("zip", courseRecord.getZipCode());
        courseRecordSheet.put("county", courseRecord.getCounty());
        courseRecordSheet.put("end_date", courseRecord.getEndDate());
        
        try {
            sabaResponse = getSabaWrapper().createFacilityFeeCRS(courseRecordSheet);
        } catch (Exception e) {
            sabaResponse = "Error";
            e.printStackTrace();
        }
        
        return sabaResponse;
    }
    

	public  ArrayList<String> getCityStateCounty(String zipCode){
		
		ArrayList<String> cityStateCounty = new ArrayList<String>();
		Map<String, String[]> sabaResponse = new HashMap<String, String[]>();
		
		try {
			sabaResponse = getSabaWrapper().findState(zipCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Map.Entry<String, String[]> entry : sabaResponse.entrySet())
		{
			
			for(String value : entry.getValue())
			{
				cityStateCounty.add(value);
			}
		}
		
		return cityStateCounty;
		
	}
	
	public  CourseRecord getCourseRecordDetails(String sheetNumberID){
		//System.out.println("SabaHelper getCourseRecordDetails sheetNumberID " + sheetNumberID);
		
		Map<String, String> sabaResponse = new HashMap<String, String>();
		CourseRecord courseRecord = new CourseRecord();

		try {
			sabaResponse = getSabaWrapper().findCRS(sheetNumberID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//System.out.println("SabaHelper getCourseRecordDetails sabaResponse " + sabaResponse);
		
		courseRecord.setCreateDate(sabaResponse.get("date_submitted"));
		courseRecord.setSheetNumberID(sabaResponse.get("crsId"));
		courseRecord.setSheetNumber(sabaResponse.get("crsNo"));
		courseRecord.setCourseID(sabaResponse.get("course_id"));
		courseRecord.setCourseName(sabaResponse.get("course_title"));
		courseRecord.setCourseVersion(sabaResponse.get("course_version"));
		courseRecord.setOrganizationID(sabaResponse.get("orgId"));
		courseRecord.setOrganizationName(sabaResponse.get("org_name"));
		courseRecord.setStatus(sabaResponse.get("status"));
		courseRecord.setTrainingCenterName((sabaResponse.get("trainingCenterName") == null?"N/A":sabaResponse.get("trainingCenterName")));
		courseRecord.setStreetAddress(sabaResponse.get("address"));
		courseRecord.setCity(sabaResponse.get("city"));
		courseRecord.setState(sabaResponse.get("state"));
		courseRecord.setZipCode(sabaResponse.get("zip"));
		courseRecord.setCounty(sabaResponse.get("county"));
		courseRecord.setStudentDetails(sabaResponse.get("skip_students") == null || sabaResponse.get("skip_students").equalsIgnoreCase("N")?false:true );
		courseRecord.setTotalStudents(sabaResponse.get("total_students"));
		courseRecord.setTotalSuccessful(sabaResponse.get("total_successful"));
		courseRecord.setTotalUnsuccessful(sabaResponse.get("total_unsuccessful"));
		courseRecord.setTotalNotEvaluated(sabaResponse.get("total_not_evaluated"));
		courseRecord.setComments(sabaResponse.get("comments"));
		courseRecord.setEndDate(sabaResponse.get("end_date"));
		courseRecord.setContactID(sabaResponse.get("contact_id"));
		courseRecord.setContactFirstName(sabaResponse.get("contact_fname"));
		courseRecord.setContactLastName(sabaResponse.get("contact_lname"));
		courseRecord.setContactUserName(sabaResponse.get("contact_username"));
		courseRecord.setApproverFirstName(sabaResponse.get("approver_fname"));
		courseRecord.setApproverLastName(sabaResponse.get("approver_lname"));
		courseRecord.setApproverUserName(sabaResponse.get("approver_username"));
		courseRecord.setApprovedDate(sabaResponse.get("approved_on"));
		courseRecord.setApproverComments(sabaResponse.get("approver_comment"));
		courseRecord.setUnitCode(sabaResponse.get("unit_code"));
		courseRecord.setOfferingNumber(sabaResponse.get("offering_no"));
		courseRecord.setOrderNumber(sabaResponse.get("order_no"));
		courseRecord.setOriginalPrice(sabaResponse.get("original_price"));  /* Deprecated */
		courseRecord.setFinalPrice(sabaResponse.get("final_price"));  /* Deprecated */
		courseRecord.setCouponCode(sabaResponse.get("coupon_code"));  /* Deprecated */
		courseRecord.setCertificatesIssued((sabaResponse.get("certs_issued") != null && sabaResponse.get("certs_issued").equals("Y")?true:false));
		courseRecord.setCertificatesIssuedOn(sabaResponse.get("certs_issued_on"));
		
		courseRecord.getPayment().setPromotionalCode(sabaResponse.get("coupon_code"));
		
		// set the price information
		courseRecord.getPayment().setOriginalPrice(sabaResponse.get("original_price"));
		courseRecord.getPayment().setFinalPrice(sabaResponse.get("final_price"));
		String totalAmount = sabaResponse.get("totalAmount");
		if (totalAmount != null && !totalAmount.equals("")) {
			courseRecord.getPayment().setTotalPrice(totalAmount);
		}
		
		// set the payment information
		String payType = sabaResponse.get("payType");
		if (payType != null) {
			courseRecord.getPayment().setPaymentTypeID(payType);

			if (sabaResponse.get("paymentReference") != null)
				courseRecord.getPayment().setPaymentReference(sabaResponse.get("paymentReference"));
			else
				courseRecord.getPayment().setPaymentReference("");

			if (payType.equals("PO")) {
				if (sabaResponse.get("poId")!=null && sabaResponse.get("purchaseOrderName")!=null) {
					courseRecord.getPayment().setPurchaseOrderID(sabaResponse.get("poId"));
					courseRecord.getPayment().setPurchaseOrder(sabaResponse.get("purchaseOrderName"));
				}
			}
		} else {
			// this is pay type saved from pre-create page
			payType = sabaResponse.get("holder_paytype");
			if (payType != null) {
				courseRecord.getPayment().setPaymentTypeID(payType);
				if (payType.equals("PO")) {
					if (sabaResponse.get("holder_po_id") != null) {
						courseRecord.getPayment().setPurchaseOrderID(sabaResponse.get("holder_po_id"));
						courseRecord.getPayment().setPurchaseOrder(getPurchaseOrderName(courseRecord.getOrganizationID(), courseRecord.getPayment().getPurchaseOrderID()));
					}
				}
			}	
		}
		
		// Check if approved and price 0.00, if so, remove pay type since it could only be approved with no pay API
		// I need to remove pay type because it was set in pay later and the no pay API call does not reset it.
		if (courseRecord.getStatus()!=null && courseRecord.getStatus().equals("Approved"))
		{
			// check price
			String total = courseRecord.getPayment().getTotalPrice();
			if (total == null) total = "0.00";
			if (Float.parseFloat(total) == 0.00) {
				courseRecord.getPayment().setPaymentTypeID("");
				courseRecord.getPayment().setTotalPrice("0.00");
			}
		}
		
		// use helper function to set pay type label
		courseRecord.getPayment().setPaymentType(getPaymentTypeLabel(courseRecord.getPayment().getPaymentTypeID()));
		
		// set the agreed flag if it has already been checked
		String agreed = sabaResponse.get("agreed");
		if (agreed != null && agreed.equals("Y")) {
			courseRecord.getPayment().setAgreement(true);
		} else {
			courseRecord.getPayment().setAgreement(false);
		}
		
		/*
		sabaResponse.get("course_no")
		*/
		
		return courseRecord;
	}
	
	
	
	public Map<String, String> generateSignature(String merchantId, Map<String, String> paramsToSign) 
	{
		SabaRESTAPIWrapper sabaWrapper = new SabaRESTAPIWrapper();
		return sabaWrapper.generateSignature(merchantId, paramsToSign);
	
	}
	
	
	public  ArrayList<CourseRecord>  sortCourseRecordList(ArrayList<CourseRecord> courseRecordList) { 
		
		ArrayList<CourseRecord> newCourseRecordList = new ArrayList<CourseRecord>();
		Set<CourseRecord> courseRecordSet = new LinkedHashSet<CourseRecord>(courseRecordList);
		newCourseRecordList.addAll(courseRecordSet);
		Collections.sort(newCourseRecordList, new CourseRecordResultComparator());
		return newCourseRecordList;
	}

	public  SabaRESTAPIWrapper getSabaWrapper() {
		return new SabaRESTAPIWrapper();
	}
	
	
	private DropDown dropdown(String key, String value)
	{
		DropDown dd = new DropDown();
		dd.setKey(key);
		dd.setValue(value);
		return dd;
	}
}


class DropDownComparator implements Comparator<DropDown> {
    public int compare(DropDown option1, DropDown option2) {
        return option1.getValue().compareTo(option2.getValue());
    }
}

class DropDownKeyComparator implements Comparator<DropDown> {
    public int compare(DropDown option1, DropDown option2) {
        return option1.getKey().compareTo(option2.getKey());
    }
}

class SearchResultComparator implements Comparator<CourseRecordSearch> {
    public int compare(CourseRecordSearch result1,CourseRecordSearch result2) {
        return result1.getSheetNumber().compareTo(result2.getSheetNumber());
    }
}

class CourseCodeComparator implements Comparator<CourseRecordSearch> {
    public int compare(CourseRecordSearch result1,CourseRecordSearch result2) {
        return result1.getCourseName().compareTo(result2.getCourseName());
    }
}

class InstructorComparator implements Comparator<People> {
    public int compare(People result1,People result2) {
        return result1.getLastName().compareTo(result2.getLastName());
    }
}

class CourseRecordResultComparator implements Comparator<CourseRecord> {
    public int compare(CourseRecord result1,CourseRecord result2) {
        return result1.getSheetNumber().compareTo(result2.getSheetNumber());
    }
}

class StudentComparator implements Comparator<Student> {
    public int compare(Student result1,Student result2) {
        return result1.getId().compareTo(result2.getId());
    }
}
