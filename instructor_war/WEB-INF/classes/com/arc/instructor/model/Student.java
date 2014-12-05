package com.arc.instructor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Student extends People implements Serializable {
	
	final String LABEL_ID = "ID";
	final String LABEL_USERNAME 	= "Username";
	final String LABEL_FIRSTNAME	= "First Name";
	final String LABEL_LASTNAME 	= "Last Name";
	final String LABEL_EMAIL 		= "Email";
	final String LABEL_PHONE 		= "Phone Number";
	final String LABEL_ZIPCODE 		= "Postal Code";
	
	List<CourseComponent> courseComponents;
	
	public Student(){
		
		super();
	}
	
	public List<CourseComponent> getCourseComponents(){
		return courseComponents;
	}
	
	public void setCourseComponents(List<CourseComponent> courseComponents){
		this.courseComponents = courseComponents;
	}
	
	public void setCourseComponentValue(String componentID, String componentValue){
		
		for(int c=0; c < this.courseComponents.size(); c++){
			if(this.courseComponents.get(c).getCourseComponentID().equals(componentID))
				this.courseComponents.get(c).setCourseComponentValue(componentValue);
		}
	}
	
	public List<String> getCourseComponentsLabels(){
		List<String> labels = new ArrayList<String>();
		
        for (CourseComponent courseComponent : this.courseComponents) {
        	labels.add(courseComponent.getCourseComponentLabel());
        }
		
		return labels;
	}
	
	public String getCourseComponentID(String courseComponentLabel){
		String courseComponentID = null;
		
        for (CourseComponent courseComponent : this.courseComponents) {
        	if(courseComponent.getCourseComponentLabel().equals(courseComponentLabel))
        		courseComponentID = courseComponent.getCourseComponentID();
        }
		return courseComponentID;
	}
	
	public List<CourseComponent> cloneCourseComponents(List<CourseComponent> courseComponentsList) {
	    List<CourseComponent> clone = new ArrayList<CourseComponent>(courseComponentsList.size());
	    for(CourseComponent courseComponent: courseComponentsList){
	    	CourseComponent newCourseComponent = new CourseComponent();
	    	newCourseComponent.setCourseComponentID(courseComponent.getCourseComponentID());
	    	newCourseComponent.setCourseComponentLabel(courseComponent.getCourseComponentLabel());
	    	newCourseComponent.setCourseComponentValue(courseComponent.getCourseComponentValue());
	    	clone.add(newCourseComponent);
	    }
	    
	    return clone;
	}

}
