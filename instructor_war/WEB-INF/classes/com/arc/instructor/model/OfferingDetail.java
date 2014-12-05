package com.arc.instructor.model;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.arc.instructor.model.People;
import com.saba.ARC.custom.OfferingSession;


public class OfferingDetail {

	//Offering variables
	private String sabaOfferingId;
	private String offeringNo;
	private String contactId;
	private String courseId;
	private String courseName;
	private String deliveryType;
	private String deliveryTypeName;
	private String orgId;
	private String orgName;
	private String startDate;
	private String endDate;
	private String facilityId;
	private String facilityName;
	private String status;
	private String maxCount;
	private String studCount;
	private String price;
	private String poId;
	private String poNumber;
	private String contactUsername;
	private List<People> instructorList;
	private List<OfferingSession> sessionList;
	private String deeplink;
	private String availFrom;
	private String discFrom;
	private String couponCode;
	private String discountedPrice;
	private boolean isBlended;
	private boolean canDeliver;
	private boolean showCouponNote;
	
	//Form variables
	private String courseCategory;
	private String sabaMessage;
	private String errorMessage;
	private String offeringAction;


	public String getSabaOfferingId() {
		return sabaOfferingId;
	}
	public void setSabaOfferingId(String value) {
		sabaOfferingId = value;
	}
	
	
	
	public String getOfferingNo() {
		return offeringNo;
	}
	public void setOfferingNo(String value) {
		offeringNo = value;
	}
	
	

	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String value) {
		courseId = value;
	}
	

	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String value) {
		courseName = value;
	}


	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String value){
		deliveryType = value;
	}
	
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String value) {
		orgId = value;
	}
	
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String value) {
		startDate = value;
	}
	
	
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String value) {
		facilityId = value;
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String value) {
		status = value;
	}
	
	
	public String getMaxCount(){
		return maxCount;
	}
	public void setMaxCount(String value){
		maxCount = value;
	}
	
	
	public String getContactId(){
		return contactId;
	}
	public void setContactId(String value){
		contactId = value;
	}


	public String getPrice(){
		return price;
	}
	public void setPrice(String value){
		price = value;
	}
	
	
	public String getPoId(){
		return poId;
	}
	public void setPoId(String value){
		poId = value;
	}
	
	
	public List<People> getInstructors(){
		return instructorList;
	}
	public void setInstructors(List<People> value) {
		instructorList = value;
	}
	
	
	public List<OfferingSession> getSessions(){
		return sessionList;
	}
	public void setSessions(List<OfferingSession> value) {
		sessionList = value;
	}		
	
	
	public String getCourseCategory(){
		return courseCategory;
	}
	public void setCourseCategory(String value){
		courseCategory = value;
	}
	
	
	public String getSabaMessage() {
		return sabaMessage;
	}
	public void setSabaMessage(String value) {
		sabaMessage = value;
	}
	
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String value) {
		errorMessage = value;
	}
	
	
	public String getOfferingAction() {
		return offeringAction;
	}
	public void setOfferingAction(String value) {
		offeringAction = value;
	}
	
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String value) {
		endDate = value;
	}
	
	
	public String getStudCount() {
		return studCount;
	}
	public void setStudCount(String value) {
		studCount = value;
	}
	
	
	public String getContactUsername() {
		return contactUsername;
	}
	public void setContactUsername(String value) {
		contactUsername = value;
	}
	
	
	public String getDeliveryTypeName() {
		return deliveryTypeName;
	}
	public void setDeliveryTypeName(String value) {
		deliveryTypeName = value;
	}
	
	public String getDeeplink() {
		return deeplink;
	}
	public void setDeeplink(String value) {
		deeplink = value;
	}	
	

	public String getAvailFrom() {
		return availFrom;
	}
	public void setAvailFrom(String value) {
		availFrom = value;
	}	
	

	public String getDiscFrom() {
		return discFrom;
	}
	public void setDiscFrom(String value) {
		discFrom = value;
	}	
	
	
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String value) {
		orgName = value;
	}
	
	
	public String getFacilityName() {
		return facilityName;
	}
	public void setFacilityName(String value) {
		facilityName = value;
	}
	
	
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String value) {
		poNumber = value;
	}
	
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String value) {
		couponCode = value;
	}
	
	public String getDiscountedPrice() {
		return discountedPrice;
	}
	public void setDiscountedPrice(String value) {
		discountedPrice = value;
	}
	
	public boolean isOnline(){
		return "Online".equals(deliveryTypeName);
	}
	
	public String getBlended(){
		return String.valueOf(isBlended);
	}
	public void setBlended(String value){
		isBlended = new Boolean(value).booleanValue();	
	}
	
	public boolean getCanDeliver() throws ParseException{
		 SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		 
		 return 	!isOnline() && 
		 			"100".equals(status) && 
					Integer.parseInt(studCount) > 0 && 
					format.parse(startDate).before(new Date()); 
	}
	
	public boolean getShowCouponNote(){

		//If Edit Mode and not in Open state, don't show note
		if(sabaOfferingId!=null && !sabaOfferingId.equals("") && !"100".equals(status)) return false;

		//If couponCode has already been provided, no need to show note
		if(couponCode!=null && !couponCode.equals("")) return false;
		
		//If price is not present, then don't show the note
		if(price==null || price.equals("") || price.startsWith("0.0")) return false;
		
		//For all other cases, show the note
		return true;
	}
	
}