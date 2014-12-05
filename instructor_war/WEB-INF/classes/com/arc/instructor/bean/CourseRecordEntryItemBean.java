package com.arc.instructor.bean;

import java.sql.Date;

public class CourseRecordEntryItemBean {
    
    private int totalStudents;
    
    private boolean skipStudentDetails;
    
    private String courseRecordSheetNumber;
    private String contactInformation;
    private String organization;
    private String course;
    
    private Date offeringEndDate;
    
    public int getTotalStudents() {
        return totalStudents;
    }
    
    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }
    
    public boolean isSkipStudentDetails() {
        return skipStudentDetails;
    }
    
    public void setSkipStudentDetails(boolean skipStudentDetails) {
        this.skipStudentDetails = skipStudentDetails;
    }
    
    public String getCourseRecordSheetNumber() {
        return courseRecordSheetNumber;
    }
    
    public void setCourseRecordSheetNumber(String courseRecordSheetNumber) {
        this.courseRecordSheetNumber = courseRecordSheetNumber;
    }
    
    public String getContactInformation() {
        return contactInformation;
    }
    
    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
    
    public String getOrganization() {
        return organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public Date getOfferingEndDate() {
        return offeringEndDate;
    }
    
    public void setOfferingEndDate(Date offeringEndDate) {
        this.offeringEndDate = offeringEndDate;
    }
    
}
