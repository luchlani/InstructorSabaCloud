package com.arc.instructor.model;

import java.util.Comparator;

/**
 * 
 * @author John Cordero NCS Technologies, Inc.
 *
 */
public class CourseRecordSearch implements Comparable<CourseRecordSearch> {
	
	private boolean certificatesIssued = false;
	private String sabaMessage;
	private String sheetNumberID;
	private String sheetNumber;
	private String status;
	private String endDate;
	private String courseCode;
	private String courseName;
	private String organizationName;
	private String organizationID;
	private String action;
	private String arrayIndex;
	private String feeOption;


    public CourseRecordSearch() {
    	
    }

    /**
     * 
     * @return String
     */
	public String getSheetNumber() {
		return sheetNumber;
	}

	/**
	 * 
	 * @param sheetNumber
	 */
	public void setSheetNumber(String sheetNumber) {
		this.sheetNumber = sheetNumber;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public String getCourseCode() {
		return courseCode;
	}


	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
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
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getArrayIndex() {
		return arrayIndex;
	}

	public void setArrayIndex(String arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	public String getFeeOption() {
		return feeOption;
	}

	public void setFeeOption(String feeOption) {
		this.feeOption = feeOption;
	}

	public String getSheetNumberID() {
		return sheetNumberID;
	}

	public void setSheetNumberID(String sheetNumberID) {
		this.sheetNumberID = sheetNumberID;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getSabaMessage() {
		return sabaMessage;
	}

	public void setSabaMessage(String sabaMessage) {
		this.sabaMessage = sabaMessage;
	}

	public boolean isCertificatesIssued() {
		return certificatesIssued;
	}

	public void setCertificatesIssued(boolean certificatesIssued) {
		this.certificatesIssued = certificatesIssued;
	}
	
	public int compareTo(CourseRecordSearch course) {
		 
		int sheetNumber = Integer.parseInt(course.getSheetNumber()); 

		// descending order
		return sheetNumber - Integer.parseInt(this.sheetNumber);
 
	}

}