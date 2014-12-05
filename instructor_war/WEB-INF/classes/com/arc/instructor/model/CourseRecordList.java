package com.arc.instructor.model;

import java.util.ArrayList;

public class CourseRecordList {

    private ArrayList<CourseRecord> courseRecordList = new ArrayList<CourseRecord>();

	public ArrayList<CourseRecord> getCourseRecordList() {
		return courseRecordList;
	}

	public void setCourseRecordList(ArrayList<CourseRecord> courseRecordList) {
		this.courseRecordList = courseRecordList;
	}
	
	public void addCourseRecordList(CourseRecord courseRecord) {
		this.courseRecordList.add(courseRecord);
	}

	public void deleteCourseRecordList(CourseRecord courseRecord) {
		this.courseRecordList.remove(courseRecord);
	}
	
	public int getCourseRecordListCount() {
		return this.courseRecordList.size();
	}

}