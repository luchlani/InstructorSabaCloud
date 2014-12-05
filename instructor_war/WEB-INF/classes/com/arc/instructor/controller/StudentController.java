package com.arc.instructor.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.arc.instructor.model.CourseComponent;
import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.People;
import com.arc.instructor.model.Student;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;

@Controller
@RequestMapping(value={"/studentRoster"})
@SessionAttributes("studentList")
public class StudentController {

		private final Log logger = LogFactory.getLog(getClass());
		
		private List<Student> studentList;
		
		private Jaxb2Marshaller jaxbMarshaller;

	    public StudentController() {
	    	logger.debug("StudentController Constructor ");	
	    	
			ApplicationContext context = new ClassPathXmlApplicationContext("jaxb-configuration.xml");
			
			this.jaxbMarshaller = (Jaxb2Marshaller)context.getBean("jaxbMarshaller");
			
			Map<String, Boolean> marshallerProperties = new HashMap<String, Boolean>();
			marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshallerProperties.put(Marshaller.JAXB_FRAGMENT, true);
			this.jaxbMarshaller.setMarshallerProperties(marshallerProperties);
			
	    }
	    
	    @RequestMapping(method=RequestMethod.GET)
		public void getStudentRoster(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type,@RequestParam("sheetNumber") String sheetNumber, ModelMap model ){
	    	logger.debug("StudentController getStudentRoster");
			logger.debug("StudentController Type: "+ type);
			logger.debug("StudentController Sheet Number: "+ sheetNumber);

	    	if(type.contains("xml")){
	    		getXmlResults(request, response,sheetNumber);
	    	} else {
	    		getJsonResults(request, response,sheetNumber);
	    	}
	    }
	    
	    
	    @RequestMapping(method=RequestMethod.POST)
		public void processStudentData(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type, @RequestParam("sheetNumber") String sheetNumber, @RequestParam("action") String action) {
	 
			logger.debug("StudentController processStudentData");
			logger.debug("StudentController Type: "+ type);
			logger.debug("StudentController Sheet Number: "+ sheetNumber);			
			logger.debug("StudentController Action:" + action);
			logger.debug("StudentController ID:" + request.getParameter("id"));
			logger.debug("StudentController Row ID:" + request.getParameter("row_id"));
			logger.debug("StudentController Column ID:" + request.getParameter("column"));
			logger.debug("StudentController New Value:" + request.getParameter("value"));
			
	    	if(type.contains("xml")){
	    		getXmlResults(request, response,sheetNumber);
	    	} else {
	    		getJsonResults(request, response,sheetNumber);
	    	}
						
	    }
	    
		private void getXmlResults(HttpServletRequest request, HttpServletResponse response,String sheetNumber) {
			logger.debug("StudentController getXmlResults");
			
			StringBuffer xml = new StringBuffer();
	    	
			CourseRecord courseRecord = new CourseRecord();
			courseRecord.setSheetNumber(sheetNumber);
			courseRecord =  getSabaHelper().getCourseRecordStudents(courseRecord);
			
			xml.append("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>");
			xml.append("<students>");
			
			if(courseRecord.getIsStudents()){
				logger.debug("Course Record Student Roster Size: " + courseRecord.getStudents().size());

				for (People student : courseRecord.getStudents())
		        {
					logger.debug("Course Record Student: " + student.getFirstName() + " " + student.getLastName());
			    	Writer outWriter = new StringWriter();  
			    	StreamResult result = new StreamResult( outWriter );  
					this.jaxbMarshaller.marshal(student, result);  
					StringWriter sw = (StringWriter) result.getWriter(); 
					StringBuffer sb = sw.getBuffer(); 
					xml.append(sb);
		        }
			}
			xml.append("</students>");
			
	     	response.reset();
	        response.setContentType("application/xml");
	        //response.setCharacterEncoding("UTF-8");
	        try {
	        	response.getWriter().write(xml.toString());
		        response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}

		private void getJsonResults(HttpServletRequest request, HttpServletResponse response, String sheetNumber) {
			logger.debug("StudentController getJsonResults " + request.getMethod());
			
			CourseRecord courseRecord = new CourseRecord();
			courseRecord.setSheetNumber(sheetNumber);
			
			ArrayList <JSONObject> studentList = new ArrayList<JSONObject>();
			
			User user = (User) request.getSession().getAttribute("user");
			

			logger.debug("Student session object [" + request.getSession().getAttribute("studentList")+"]");
			if(request.getSession().getAttribute("studentList") != null ){
				courseRecord = (CourseRecord) request.getSession().getAttribute("studentList");
				logger.debug("Getting session studentList " + (courseRecord.getStudents() != null?courseRecord.getStudents().size():"0"));
			}
			
			int totalStudents = 0;
			try {
            	totalStudents = Integer.parseInt(courseRecord.getTotalStudents());
            } catch(NumberFormatException e) {
               logger.error("Error parsing Total Students count " + e.getMessage());
            }
			
			if(request.getMethod().equals("POST")){	
				logger.debug("Course Record Student processing " + request.getMethod());

			
				if(request.getParameter("action") != null && request.getParameter("action").equals("Delete")){

            		logger.debug("StudentController forcing SABA authentication");
                	String message = getSabaHelper().login(user.getUsername(), user.getPassword());
	        		if(!message.equals("")){
	        			logger.error("StudentController Error forcing SABA authentication" + message);
	        		}
					if(request.getParameter("id") != null){
						if(request.getParameter("id").equals("-1")){
							logger.debug("Course Record Student Delete All Students : " + courseRecord.getStudents().size());								
							if(courseRecord.getAction() != null &&  courseRecord.getAction().equals("Summary")){
								//Delete Existing Course Students
						        for (Student student : courseRecord.getStudents()) {
						        	if(student.getId() != null){
							        	boolean successful = getSabaHelper().removeCourseRecordStudents(courseRecord.getSheetNumberID(), student.getId());
							        	if(successful)
							        		logger.debug("Delete course record student (" + courseRecord.getSheetNumberID() + ") Student " + student.getId() + " was successful");
							        	else
							        		logger.error("Error could not delete course record student (" + courseRecord.getSheetNumberID() + ") Student " + student.getId() + " failed");
						        	}
						        }
							}
							courseRecord.setStudents(new ArrayList<Student>());
						} else {
							Student student = courseRecord.getStudents().get(Integer.parseInt(request.getParameter("id")));
							courseRecord.getStudents().remove(Integer.parseInt(request.getParameter("id")));
							totalStudents = totalStudents - 1;
							logger.debug("StudentController Delete: setTotalStudents = "+totalStudents);
							courseRecord.setTotalStudents(""+totalStudents);
							if(student.getId() != null && courseRecord.getAction() != null && courseRecord.getAction().equals("Summary")){
								//Delete Existing Course Student
					        	boolean successful = getSabaHelper().removeCourseRecordStudents(courseRecord.getSheetNumberID(), student.getId());
					        	if(successful)
					        		logger.debug("Delete course record student (" + courseRecord.getSheetNumberID() + ") Student " + student.getId() + " was successful");
					        	else
					        		logger.error("Error could not delete course record student (" + courseRecord.getSheetNumberID() + ") Student " + student.getId() + " failed");
							}
						}
					}
					
				} else if(request.getParameter("action") != null && request.getParameter("action").equals("Edit")){
					String value = request.getParameter("value");
					int row_id = Integer.parseInt(request.getParameter("id"));
					int column_id = Integer.parseInt(request.getParameter("column"));
					Student student = courseRecord.getStudents().get(row_id);
					logger.debug("Student Controller Edit ID " + row_id);
					logger.debug("Student Controller Edit Column " + column_id);
					logger.debug("Student Controller Edit Value " + value);
					
					if(column_id < 8){
				        switch (column_id) {
			            case 0: student.setId(value);
			                    break;
			            case 1: student.setUserName(value);
			            		break;
			            case 2: student.setFirstName(value);
			            		break;
			            case 3: student.setLastName(value);
				                break;
			            case 4: student.setEmail(value);
			            		break;  
			            case 5: student.setPhoneNumber(value);
			    				break;  
			            case 6: student.setPostalCode(value);
			    				break;     
			    		case 7: student.setAddlInfo(value);
			    				break;     		
			            default:
				            	break;
				        }
					} else {
						int extra_columns = 8;
						for(CourseComponent courseComponent : student.getCourseComponents()){
							logger.debug("Student Controller Edit Extra Column [" +  extra_columns + "] == ["+ column_id + "] value = [" + value + "]");
							if(extra_columns == column_id){
								student.setCourseComponentValue(courseComponent.getCourseComponentID(), value);
								break;
							}
							extra_columns++;
						}	
					}
			        courseRecord.getStudents().remove(row_id);
			        courseRecord.getStudents().add(row_id, student);
			        
				} else if(request.getParameter("action") != null && request.getParameter("action").equals("Create")){
					Student student = new Student();
					student.setCourseComponents(student.cloneCourseComponents(courseRecord.getCourseComponents()));
					if(courseRecord.getIsStudents()){
						courseRecord.getStudents().add(student);
					} else {
						ArrayList<Student> studentRoster = new ArrayList<Student>();
						studentRoster.add(student);
						courseRecord.setStudents(studentRoster);
					}
					
					totalStudents = totalStudents + 1;
					logger.debug("StudentController Add: setTotalStudents = "+totalStudents);
					courseRecord.setTotalStudents(""+totalStudents);
				} 
			} else {
				if(!courseRecord.getIsStudents()){
					logger.debug("StudentController getCourseRecordStudents()");
					
	                if(courseRecord.getFeeOption() != null  && courseRecord.getFeeOption().equals(user.FEE_BASED)){
	            		logger.debug("StudentController forcing SABA authentication");
	                	String message = getSabaHelper().login(user.getUsername(), user.getPassword());
		        		if(!message.equals("")){
		        			logger.error("StudentController Error forcing SABA authentication" + message);
		        		} else {
		        			courseRecord = getSabaHelper().getCourseRecordStudents(courseRecord);
		        		}
	                }
	                
					ArrayList<Student> studentRoster = new ArrayList<Student>();
					
					// Course record has existing students
					if(courseRecord.getIsStudents()){
						for(Student student : courseRecord.getStudents()){
							studentRoster.add(student);
						}						
					}
					
					if(courseRecord.getStudents().size() < totalStudents){
						int addStudentCount = totalStudents - courseRecord.getStudents().size();
						for(int s=0; s < addStudentCount; s++){
							Student student = new Student();
							student.setCourseComponents(student.cloneCourseComponents(courseRecord.getCourseComponents()));
							studentRoster.add(student);
						}
					}
					
					courseRecord.setStudents(studentRoster);
				} else {
					
					if(courseRecord.getStudents().size() < totalStudents){
						int addStudentCount = totalStudents - courseRecord.getStudents().size();
						for(int s=0; s < addStudentCount; s++){
							Student student = new Student();
							student.setCourseComponents(student.cloneCourseComponents(courseRecord.getCourseComponents()));
							courseRecord.getStudents().add(student);
						}
					}					
				}
			}
			
			String studentColumnList = generateStudentTableColumnDefinitions(courseRecord);
			
			int studentCount = 0;
			JSONObject studentJsonObj;
			if(courseRecord.getIsStudents()){
				logger.debug("Course Record Student Roster Size: " + courseRecord.getStudents().size());
				
				List<Student> displayStudentList = new ArrayList<Student>();
				
				
				// Process Student List
				for (Student student : courseRecord.getStudents())
		        {
					student = scrubStudents(student);
					if(!courseRecord.getAction().equals("Payment")){
						displayStudentList.add(generateStudentEditForm(student, courseRecord.getCourseComponents()));
					} else {
						displayStudentList.add(student);
					}
				}
				
				for (Student displayStudent : displayStudentList)
		        {
					studentJsonObj = new JSONObject();
					
					if(displayStudent.getFirstName() != null &&  displayStudent.getLastName() != null)
						logger.debug("Course Record Student: " + displayStudent.getFirstName() + " " + displayStudent.getLastName());
											
					studentJsonObj.accumulate("id", displayStudent.getId());
					studentJsonObj.accumulate("userName", displayStudent.getUserName());
			    	studentJsonObj.accumulate("firstName", displayStudent.getFirstName());
					studentJsonObj.accumulate("lastName", displayStudent.getLastName());
					studentJsonObj.accumulate("email", displayStudent.getEmail());
					studentJsonObj.accumulate("phoneNumber", displayStudent.getPhoneNumber());
					studentJsonObj.accumulate("postalCode", displayStudent.getPostalCode());
					studentJsonObj.accumulate("addlInfo", displayStudent.getAddlInfo());
					for(CourseComponent studentCourseComponents: displayStudent.getCourseComponents()){
						studentJsonObj.accumulate(studentCourseComponents.getCourseComponentID(), studentCourseComponents.getCourseComponentValue());
					}
					
					if(!courseRecord.getAction().equals("Payment"))
						studentJsonObj.accumulate("action", "<a id='delete' href='javascript:Delete(\""+ studentCount + "\")' name='delete' data-params='' title='Click the trashcan icon next to a student to delete a student.'><span class='ui-icon ui-icon-trash'></span></a>");
					
					studentCount++;
					studentList.add(studentJsonObj);
		        }
					
			} 
			
			logger.debug("Course Record Setting StudentList: " + courseRecord.getStudents().size());
			request.getSession().setAttribute("studentList", courseRecord);
			
			JSONObject sendObj = new JSONObject();
	        sendObj.accumulate("iTotalRecords", ""+studentCount);
	        sendObj.accumulate("iTotalDisplayRecords", "10");
	        sendObj.accumulate("aoColumns", studentColumnList);
	        sendObj.accumulate("aaSorting", "[ [ 0, 'asc' ] ]");
	        if(!studentList.isEmpty()){
	        	sendObj.put("aaData", studentList);
	        } else {
	        	sendObj.accumulate("aaData", "[]");
	        }
	        

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
		

		
		private Student generateStudentEditForm(Student student, List<CourseComponent> courseComponents){

			Student cloneStudent = new Student();
			String defaultInputValue = "<input type=\"text\" class=\"input-medium\" value=\"\" >";

			String value = (student.getFirstName() != null?defaultInputValue.replace("value=\"\"", "value=\"" + student.getFirstName() + "\""):defaultInputValue);
			cloneStudent.setFirstName(value);
			value = (student.getLastName() != null?defaultInputValue.replace("value=\"\"", "value=\"" + student.getLastName() + "\""):defaultInputValue);
			cloneStudent.setLastName(value);
			value = (student.getEmail() != null?defaultInputValue.replace("value=\"\"", "value=\"" + student.getEmail() + "\""):defaultInputValue);
			cloneStudent.setEmail(value);
			value = (student.getPhoneNumber() != null?defaultInputValue.replace("value=\"\"", "value=\"" + student.getPhoneNumber() + "\""):defaultInputValue);
			cloneStudent.setPhoneNumber(value);
			value = (student.getPostalCode() != null?defaultInputValue.replace("value=\"\"", "value=\"" + student.getPostalCode() + "\""):defaultInputValue);
			cloneStudent.setPostalCode(value);
			value = (student.getAddlInfo() != null?defaultInputValue.replace("value=\"\"", "value=\"" + student.getAddlInfo() + "\""):defaultInputValue);
			cloneStudent.setAddlInfo(value);
			cloneStudent.setCourseComponents(cloneStudent.cloneCourseComponents(courseComponents));
			
			if(student.getCourseComponents() == null || student.getCourseComponents().isEmpty())
				student.setCourseComponents(student.cloneCourseComponents(courseComponents));

			for(CourseComponent component : student.getCourseComponents()){
				cloneStudent.setCourseComponentValue(component.getCourseComponentID(), "<select  class=\"input-tiny\" > <option "+ (component.getCourseComponentValue() == null || (component.getCourseComponentValue() !=null && component.getCourseComponentValue().equals("Successful"))?"Selected":"") +" >Successful</option><option "+ (component.getCourseComponentValue() !=null && component.getCourseComponentValue().equals("Unsuccessful")?"Selected":"") +" >Unsuccessful</option><option "+ (component.getCourseComponentValue() !=null && component.getCourseComponentValue().equals("Not Evaluated")?"Selected":"") +" >Not Evaluated</option></select>");
			}

			return cloneStudent;							
		}
		
		private String generateStudentTableColumnDefinitions(CourseRecord courseRecord){
			
			StringBuffer tableColumnJson = new StringBuffer();
			
			tableColumnJson.append("[");
			tableColumnJson.append("{ \"sTitle\": \"id\",\"mData\": \"id\", \"sName\": \"id\", \"sWidth\": null,\"bVisible\": false },");
			tableColumnJson.append("{ \"sTitle\": \"Username\",\"mData\": \"userName\", \"sName\": \"userName\", \"sWidth\": null,\"bVisible\": false },");
			tableColumnJson.append("{ \"sTitle\": \"First Name\",\"mData\": \"firstName\", \"sName\": \"firstName\", \"sWidth\": null },");  
			tableColumnJson.append("{ \"sTitle\": \"Last Name\",\"mData\": \"lastName\",\"sName\": \"lastName\", \"sWidth\": null },");  
			tableColumnJson.append("{ \"sTitle\": \"Email\",\"mData\": \"email\",\"sName\": \"email\", \"sWidth\": null },"); 
			tableColumnJson.append("{ \"sTitle\": \"Phone Number\",\"mData\": \"phoneNumber\",\"sName\": \"phoneNumber\", \"sWidth\": null },");
			tableColumnJson.append("{ \"sTitle\": \"Postal Code\",\"mData\": \"postalCode\",\"sName\": \"postalCode\", \"sWidth\": null,\"bVisible\": false },");
			tableColumnJson.append("{ \"sTitle\": \"Additional Info\",\"mData\": \"addlInfo\",\"sName\": \"addlInfo\", \"sWidth\": null },");
             
			for(CourseComponent courseComponent: courseRecord.getCourseComponents()){
				
				tableColumnJson.append("{ \"sTitle\": \""+ courseComponent.getCourseComponentLabel() +"\", \"mData\": \""+ courseComponent.getCourseComponentID() +"\",\"sName\": \""+ courseComponent.getCourseComponentID() +"\", \"sWidth\": null },");

			}
			
			if(!courseRecord.getAction().equals("Payment"))
				tableColumnJson.append("{ \"sTitle\": \"Action\",\"mData\": \"action\",\"sName\": \"action\", \"sWidth\": \"8%\" }"); 
        	
			tableColumnJson.append("]");
        	
			return tableColumnJson.toString();
		}
		
		private Student scrubStudents(Student student){
			
			
			student.setFirstName(scrubHtmlFormElement(student.getFirstName()));
			student.setLastName(scrubHtmlFormElement(student.getLastName()));
			student.setEmail(scrubHtmlFormElement(student.getEmail()));
			student.setPhoneNumber(scrubHtmlFormElement(student.getPhoneNumber()));
			student.setPostalCode(scrubHtmlFormElement(student.getPostalCode()));
			student.setAddlInfo(scrubHtmlFormElement(student.getAddlInfo()));
			
			List<CourseComponent> courseComponents = student.cloneCourseComponents(student.getCourseComponents());
			for(CourseComponent courseComponent : courseComponents){
				courseComponent.setCourseComponentValue(scrubHtmlFormElement(courseComponent.getCourseComponentValue()));
			}
			
			student.setCourseComponents(courseComponents);
				
			
			return student;
		}
		
		private String scrubHtmlFormElement(String html){			
			String value = html;
			Element element = null;
			
			if(html != null && !html.equals("")){
				Document document = Jsoup.parse(html, "UTF-8");
			
				logger.debug("scrubHtmlFormElement html [" + html + "]");
				
				if(html.contains("select")){
					
			        Elements options = document.select("select > option");

			        for(Element option : options)
			        {
			        	logger.debug("Option [" + option + "]");
			            if(option.text().contains("Selected"))
			            {
			            	logger.debug("Selected Option [" + option.attr("value") + "]");
			            	value = option.attr("value");
			            }
			        }
					//element = document.select("select.options[selectedIndex]").first();
					//element = document.select("option:selected").first();
					
				} else if(html.contains("input")){
						element = document.select("input").first();
						value = element.val();
				} else {
					value = html;
				}
			}
			
			if(value != null)
				value = value.trim();
			
			logger.debug("scrubHtmlFormElement value [" + value + "]");
			return value;
		}
		
		
		public List<Student> getStudentList() {
			return studentList;
		}

		public void setStudentList(List<Student> studentList) {
			this.studentList = studentList;
		}

		private SabaHelper getSabaHelper(){
			
			return new SabaHelper();
		}
		
}

