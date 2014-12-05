package com.arc.instructor.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


@SuppressWarnings("serial")
public class CourseRecord implements Serializable {
	
	private boolean precreate;
	private boolean studentDetails;
	private boolean aquaticCourseDisplay;
	private boolean courseRequiresStudentDetails;
	private boolean certificatesIssued;
	private String sabaMessage;
	private String errorMessage;
	private String action;
	private String sheetNumber;
	private String sheetNumberID;
	private String status;
	private String createDate;
	private String courseName;
	private String courseID;
	private String courseVersion;
	private String courseCategory;
	private String endDate;
	private String organizationName;
	private String organizationID;
	private String feeOption;
	private String totalStudents;
	private String totalSuccessful;
	private String totalUnsuccessful;
	private String totalNotEvaluated;
	private String trainingCenterName;
	private String streetAddress;
	private String city;
	private String state;
	private String zipCode;
	private String county;
	private String certificate;
	private String comments;
	private String contactID;
	private String contactFirstName;
	private String contactLastName;
	private String contactUserName;
	private String approverFirstName;
	private String approverLastName;
	private String approverUserName;
	private String approverComments;
	private String approvedDate;
	private String unitCode;
	private String offeringNumber;
	private String orderNumber;
	private String originalPrice;
	private String finalPrice;
	private String couponCode;
	private String certificatesIssuedOn;
	
	private List<People> instructors;
	private List<Student> students;
	private List<CourseComponent> courseComponents;
	
	private Statistics statistics;
	
	private Payment payment;

    public CourseRecord() {

    }

	public String getSheetNumber() {
		return sheetNumber;
	}


	public void setSheetNumber(String sheetNumber) {
		this.sheetNumber = sheetNumber;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(String organizationID) {
		this.organizationID = organizationID;
	}

	public String getAction() {
		if(action == null)
			setAction("Create");
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFeeOption() {
		return feeOption;
	}

	public void setFeeOption(String feeOption) {
		this.feeOption = feeOption;
	}

	public boolean isStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(boolean studentDetails) {
		this.studentDetails = studentDetails;
	}

	public String getTotalStudents() {
		return totalStudents;
	}

	public void setTotalStudents(String totalStudents) {
		this.totalStudents = totalStudents;
	}

	public String getTotalSuccessful() {
		if(totalSuccessful == null)
			setTotalSuccessful("0");
		return totalSuccessful;
	}

	public void setTotalSuccessful(String totalSuccessful) {
		this.totalSuccessful = totalSuccessful;
	}

	public String getTotalUnsuccessful() {
		if(totalUnsuccessful == null)
			setTotalUnsuccessful("0");
		return totalUnsuccessful;
	}

	public void setTotalUnsuccessful(String totalUnsuccessful) {
		this.totalUnsuccessful = totalUnsuccessful;
	}

	public String getTotalNotEvaluated() {
		if(totalNotEvaluated == null)
			setTotalNotEvaluated("0");
		return totalNotEvaluated;
	}

	public void setTotalNotEvaluated(String totalNotEvaluated) {
		this.totalNotEvaluated = totalNotEvaluated;
	}

	public String getTrainingCenterName() {
		return trainingCenterName;
	}

	public void setTrainingCenterName(String trainingCenterName) {
		this.trainingCenterName = trainingCenterName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		if(state == null)
			return "";
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public List<People> getInstructors() {
		return instructors;
	}

	public void setInstructors(List<People> instructors) {
		this.instructors = instructors;
	}
	
	public boolean getIsInstructor() {
		if(this.instructors != null && this.instructors.size() > 0)
			return true;
		
		return false;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
	public boolean getIsStudents() {
		if(this.students != null && this.students.size() > 0)
			return true;
		
		return false;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getSabaMessage() {
		return sabaMessage;
	}

	public void setSabaMessage(String sabaMessage) {
		this.sabaMessage = sabaMessage;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getCourseVersion() {
		return courseVersion;
	}

	public void setCourseVersion(String courseVersion) {
		this.courseVersion = courseVersion;
	}

	public boolean isCourseRequiresStudentDetails() {
		return courseRequiresStudentDetails;
	}

	public void setCourseRequiresStudentDetails(boolean courseRequiresStudentDetails) {
		this.courseRequiresStudentDetails = courseRequiresStudentDetails;
	}

	public String getCourseCategory() {
		return courseCategory;
	}

	public void setCourseCategory(String courseCategory) {
		this.courseCategory = courseCategory;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Statistics getStatistics() {
		if(statistics == null)
			setStatistics(new Statistics());
		return statistics;
	}

	@Autowired
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
	
	public Payment getPayment() {
		if(payment == null)
			setPayment(new Payment());
		return payment;
	}

	@Autowired
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getSheetNumberID() {
		return sheetNumberID;
	}

	public void setSheetNumberID(String sheetNumberID) {
		this.sheetNumberID = sheetNumberID;
	}

	public String getContactID() {
		return contactID;
	}

	public void setContactID(String contactID) {
		this.contactID = contactID;
	}

	public String getContactFirstName() {
		return contactFirstName;
	}

	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}

	public String getContactLastName() {
		return contactLastName;
	}

	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}

	public String getContactUserName() {
		return contactUserName;
	}

	public void setContactUserName(String contactUserName) {
		this.contactUserName = contactUserName;
	}

	public String getApproverFirstName() {
		return approverFirstName;
	}

	public void setApproverFirstName(String approverFirstName) {
		this.approverFirstName = approverFirstName;
	}

	public String getApproverLastName() {
		return approverLastName;
	}

	public void setApproverLastName(String approverLastName) {
		this.approverLastName = approverLastName;
	}

	public String getApproverUserName() {
		return approverUserName;
	}

	public void setApproverUserName(String approverUserName) {
		this.approverUserName = approverUserName;
	}

	public String getApproverComments() {
		return approverComments;
	}

	public void setApproverComments(String approverComments) {
		this.approverComments = approverComments;
	}

	public String getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public boolean isAquaticCourseDisplay() {
		return aquaticCourseDisplay;
	}

	public void setAquaticCourseDisplay(boolean aquaticCourseDisplay) {
		this.aquaticCourseDisplay = aquaticCourseDisplay;
	}

	public List<CourseComponent> getCourseComponents() {
		return courseComponents;
	}

	public void setCourseComponents(List<CourseComponent> courseComponents) {
		this.courseComponents = courseComponents;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getOfferingNumber() {
		return offeringNumber;
	}

	public void setOfferingNumber(String offeringNumber) {
		this.offeringNumber = offeringNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public String getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}
	
	public String getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}
	
	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public boolean isCertificatesIssued() {
		return certificatesIssued;
	}

	public void setCertificatesIssued(boolean certificatesIssued) {
		this.certificatesIssued = certificatesIssued;
	}

	public String getCertificatesIssuedOn() {
		return certificatesIssuedOn;
	}

	public void setCertificatesIssuedOn(String certificatesIssuedOn) {
		this.certificatesIssuedOn = certificatesIssuedOn;
	}
	
	public boolean isPreCreate() {
		return precreate;
	}

	public void setPreCreate(boolean precreate) {
		this.precreate = precreate;
	}
}