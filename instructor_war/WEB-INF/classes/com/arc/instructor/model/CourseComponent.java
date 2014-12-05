package com.arc.instructor.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class CourseComponent implements Serializable {
	
	private String courseComponentID;
	private String courseComponentLabel;
	private String courseComponentValue;
	
	public String getCourseComponentID() {
		return courseComponentID;
	}
	public void setCourseComponentID(String courseComponentID) {
		this.courseComponentID = courseComponentID;
	}
	public String getCourseComponentLabel() {
		return courseComponentLabel;
	}
	public void setCourseComponentLabel(String courseComponentLabel) {
		this.courseComponentLabel = courseComponentLabel;
	}
	public String getCourseComponentValue() {
		if(this.courseComponentValue == null)
			this.courseComponentValue = "Successful";
		return courseComponentValue;
	}
	public void setCourseComponentValue(String courseComponentValue) {
		this.courseComponentValue = courseComponentValue;
	}
	

	

	
}
