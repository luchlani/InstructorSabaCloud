package com.arc.instructor.model;

import java.util.Comparator;

/**
 * 
 *
 */
public class OfferingSearch {
	
	private String sabaOfferingId;
	private String offeringNo;
	private String courseName;
	private String startDate;
	private String status;
	private String orgName;
	private String delType;

    public OfferingSearch(String value) {
    	sabaOfferingId = value;
    }

	public String getSabaOfferingId() {
		return sabaOfferingId;
	}	
	
	public String getOfferingNo() {
		return offeringNo;
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	public String getOrgName() {
		return orgName;
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getDeliveryType() {
		return delType;
	}
	
	public void setDeliveryType(String value){
		delType = value;
	}

	public void setOfferingNo(String value) {
		offeringNo = value;
	}
	
	public void setCourseName(String value) {
		courseName = value;
	}
	
	public void setOrgName(String value) {
		orgName = value;
	}
	
	public void setStartDate(String value) {
		startDate = value;
	}
	
	public void setStatus(String value) {
		status = value;
	}

}