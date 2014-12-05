package com.arc.instructor.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.arc.instructor.model.CourseRecord;
import com.arc.instructor.model.People;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.SabaHelper;
import com.sun.xml.bind.v2.schemagen.xmlschema.List;

@Controller
@RequestMapping(value={"/instructorRoster"})
@SessionAttributes("instructor")
public class InstructorController {

		private final Log logger = LogFactory.getLog(getClass());
		
		private List instructorList;
		
		private Jaxb2Marshaller jaxbMarshaller;

	    public InstructorController() {
	    	logger.debug("InstructorController Constructor ");	
	    	
			ApplicationContext context = new ClassPathXmlApplicationContext("jaxb-configuration.xml");
			
			this.jaxbMarshaller = (Jaxb2Marshaller)context.getBean("jaxbMarshaller");
			
			Map<String, Boolean> marshallerProperties = new HashMap<String, Boolean>();
			marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshallerProperties.put(Marshaller.JAXB_FRAGMENT, true);
			this.jaxbMarshaller.setMarshallerProperties(marshallerProperties);
			
	    }
	    
	    @RequestMapping(method=RequestMethod.GET)
		public void getInstructorRoster(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type,@RequestParam("sheetNumber") String sheetNumber, @RequestParam("organizationID") String organizationID, ModelMap model ){
	    	logger.debug("InstructorController getInstructorRoster");
			logger.debug("InstructorController Type: "+ type);
			logger.debug("InstructorController Sheet Number: "+ sheetNumber);
			logger.debug("InstructorController Organization ID: "+ organizationID);
			
	    	if(type.contains("xml")){
	    		getXmlResults(request, response,sheetNumber);
	    	} else {
	    		if(organizationID != null && !organizationID.equals("")){
	    			String userName = request.getParameter("userName");
	    			String firstName = request.getParameter("firstName");
	    			String lastName = request.getParameter("lastName");
	    			getJsonAutoCompleteList(request, response, organizationID, userName, firstName, lastName);
	    			
	    		} else {
	    			getJsonResults(request, response,sheetNumber );
	    		}
	    	}
	    }
	    
	    
	    @RequestMapping(method=RequestMethod.POST)
		public void processInstructorData(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type, @RequestParam("sheetNumber") String sheetNumber, @RequestParam("action") String action) {
	 
			logger.debug("InstructorController processInstructorData");
			logger.debug("InstructorController Type: "+ type);
			logger.debug("InstructorController Sheet Number: "+ sheetNumber);			
			logger.debug("InstructorController Action:" + action);
			logger.debug("InstructorController ID:" + request.getParameter("id"));
			logger.debug("InstructorController Row ID:" + request.getParameter("row_id"));
			logger.debug("InstructorController Column ID:" + request.getParameter("column"));
			logger.debug("InstructorController New Value:" + request.getParameter("value"));
			
	    	if(type.contains("xml")){
	    		getXmlResults(request, response,sheetNumber);
	    	} else {
	    		getJsonResults(request, response,sheetNumber);
	    	}
						
	    }
	    
		private void getXmlResults(HttpServletRequest request, HttpServletResponse response,String sheetNumber) {
			logger.debug("InstructorController getXmlResults");
			
			StringBuffer xml = new StringBuffer();
	    	
			CourseRecord courseRecord = new CourseRecord();
			courseRecord.setSheetNumber(sheetNumber);
			courseRecord = getSabaHelper().getCourseRecordInstructors(courseRecord);
			
			xml.append("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>");
			xml.append("<instructors>");
			
			if(courseRecord.getIsInstructor()){
				logger.debug("Course Record Instructor Roster Size: " + courseRecord.getInstructors().size());

				for (People instructor : courseRecord.getInstructors())
		        {
					logger.debug("Course Record Instructor: " + instructor.getFirstName() + " " + instructor.getLastName());
			    	Writer outWriter = new StringWriter();  
			    	StreamResult result = new StreamResult( outWriter );  
					this.jaxbMarshaller.marshal(instructor, result);  
					StringWriter sw = (StringWriter) result.getWriter(); 
					StringBuffer sb = sw.getBuffer(); 
					xml.append(sb);
		        }
			}
			xml.append("</instructors>");
			
	     	response.reset();
	        response.setContentType("application/xml");
	        //response.setCharacterEncoding("UTF-8");
	        try {
	        	response.getWriter().write(xml.toString());
		        response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void getJsonResults(HttpServletRequest request, HttpServletResponse response, String sheetNumber) {
			logger.debug("InstructorController getJsonResults " + request.getMethod());
			
			CourseRecord courseRecord = new CourseRecord();
			ArrayList <JSONObject> instructorList = new ArrayList<JSONObject>();
			
			User user = (User) request.getSession().getAttribute("user");
			
			JSONObject instructorJsonObj;
			logger.debug("Instructor session object [" + request.getSession().getAttribute("instructorList")+"]");
			if(request.getSession().getAttribute("instructorList") != null ){
				courseRecord = (CourseRecord) request.getSession().getAttribute("instructorList");
				logger.debug("Getting session instructorList " + courseRecord.getInstructors().size());
			}
				
			
			if(request.getMethod().equals("POST")){	
				logger.debug("Course Record Instructor processing " + request.getMethod());
				if(request.getParameter("action") != null && request.getParameter("action").equals("Delete")){
	                if(courseRecord.getFeeOption() != null && courseRecord.getAction() != null && courseRecord.getFeeOption().equals(user.FEE_BASED) && courseRecord.getAction().equals("Edit")){
		        		String message = getSabaHelper().login(user.getUsername(), user.getPassword());
		        		if(!message.equals("")){
		        			logger.error("CourseController Error forcing SABA authentication" + message);
		        		}
	                }
					if(request.getParameter("id") != null){
						if(request.getParameter("id").equals("-1")){
							logger.debug("Course Record Instructor Delete All Instructors : " + courseRecord.getInstructors().size());								
							if(courseRecord.getFeeOption() != null && courseRecord.getAction() != null && courseRecord.getFeeOption().equals(user.FEE_BASED) && courseRecord.getAction().equals("Edit")){
								//Delete Existing Course Instructors
						        for (People instructor : courseRecord.getInstructors()) {
						        	boolean successful = getSabaHelper().removeCourseRecordInstructor(courseRecord.getSheetNumberID(), instructor.getId());
						        	logger.debug("Delete course record (" + courseRecord.getSheetNumberID() + ") instructor " + instructor.getId() + " was successful " + successful);
						        }
							}
							courseRecord.setInstructors(new ArrayList<People>());
						} else {
							People instructor = courseRecord.getInstructors().get(Integer.parseInt(request.getParameter("id")));
							courseRecord.getInstructors().remove(Integer.parseInt(request.getParameter("id")));
							if(courseRecord.getFeeOption() != null && courseRecord.getAction() != null && courseRecord.getFeeOption().equals(user.FEE_BASED) && courseRecord.getAction().equals("Edit")){
								//Delete Existing Course Instructor
						        boolean successful = getSabaHelper().removeCourseRecordInstructor(courseRecord.getSheetNumberID(), instructor.getId());
						        logger.debug("Delete course record (" + courseRecord.getSheetNumberID() + ") instructor " + instructor.getId() + " was successful " + successful);
							}
						}
					}
				} else if(request.getParameter("action") != null && request.getParameter("action").equals("Edit")){
					People instructor = courseRecord.getInstructors().get(Integer.parseInt(request.getParameter("id")));
					String value = request.getParameter("value");
			        switch (Integer.parseInt(request.getParameter("column"))) {
			            case 0: instructor.setId(value);
			                    break;
			            case 1: instructor.setUserName(value);
	                    		break;
			            case 2: instructor.setFirstName(value);
	                    		break;
			            case 3: instructor.setLastName(value);
				                break;
			            case 4: instructor.setEmail(value);
		                		break;  
			            case 5: instructor.setPhoneNumber(value);
		        				break;  
			            case 6: instructor.setPostalCode(value);
		        				break;    
		
			            default:
			            		break;
			        }
			        courseRecord.getInstructors().remove(Integer.parseInt(request.getParameter("id")));
			        courseRecord.getInstructors().add(Integer.parseInt(request.getParameter("id")), instructor);
				} else if(request.getParameter("action") != null && request.getParameter("action").equals("Create")){
					logger.debug("Course Record Instructor Create Instructor : " + request.getParameter("userName")  + " " + request.getParameter("lastName"));								
					logger.debug("selectedInstructors" + request.getParameter("selectedInstructors"));
	                String jsonString = "{\"JObjects\": {\"instructorArray\":" +  request.getParameter("selectedInstructors") +  "} } ";   
	                logger.debug("JSON String : " + jsonString);
	                
	                JSONObject instructorObject = (JSONObject) JSONSerializer.toJSON(jsonString);
	                JSONObject getObject = instructorObject.getJSONObject("JObjects");
	                JSONArray getArray = getObject.getJSONArray("instructorArray");
	                
	                if(courseRecord.getFeeOption() != null && courseRecord.getAction() != null && courseRecord.getFeeOption().equals(user.FEE_BASED) && courseRecord.getAction().equals("Edit")){
		        		String message = getSabaHelper().login(user.getUsername(), user.getPassword());
		        		if(!message.equals("")){
		        			logger.error("CourseController Error forcing SABA authentication" + message);
		        		}
	                }
	                
	                for(int i = 0; i < getArray.size(); i++)   {
	                    JSONObject objects = getArray.getJSONObject(i);

	  					People instructor = new People();
						instructor.setId((String)objects.get("personID"));
						instructor.setUserName((String)objects.get("userName"));
						instructor.setFirstName((String)objects.get("firstName"));
						instructor.setLastName((String)objects.get("lastName"));
						if(courseRecord.getIsInstructor()) {
							if(!isInstructorExists(courseRecord, instructor.getId())){
								courseRecord.getInstructors().add(instructor);
								if(courseRecord.getFeeOption() != null && courseRecord.getAction() != null && courseRecord.getFeeOption().equals(user.FEE_BASED) && courseRecord.getAction().equals("Edit")){
							        boolean successful = getSabaHelper().addCourseRecordInstructor(courseRecord.getSheetNumberID(), instructor.getId());
							        logger.debug("Add course record (" + courseRecord.getSheetNumberID() + ") instructor " + instructor.getId() + " was successful " + successful);									
								}
							} else {
								courseRecord.setErrorMessage("Instructor already exists on course record entry");
							}
						} else {
							logger.debug("InstructorController Adding to new table instructor() " + instructor.getFirstName() + " " + instructor.getLastName());
							ArrayList<People> instructorRoster = new ArrayList<People>();
							instructorRoster.add(instructor);
							courseRecord.setInstructors(instructorRoster);
							if(courseRecord.getFeeOption() != null && courseRecord.getAction() != null && courseRecord.getFeeOption().equals(user.FEE_BASED) && courseRecord.getAction().equals("Edit")){
						        boolean successful = getSabaHelper().addCourseRecordInstructor(courseRecord.getSheetNumberID(), instructor.getId());
						        logger.debug("Add course record (" + courseRecord.getSheetNumberID() + ") instructor " + instructor.getId() + " was successful " + successful);								
							}
						}
	                }
				}
			} else {
				if(!courseRecord.getIsInstructor()){
					logger.debug("InstructorController getCourseRecordInstructors()");
					courseRecord = getSabaHelper().getCourseRecordInstructors(courseRecord);
				}	
			}
			

			int instructorCount = 0;

			logger.debug("isInstructor : " + courseRecord.getIsInstructor());
			
			if(courseRecord.getIsInstructor()){
				logger.debug("Course Record Instructor Roster Size: " + courseRecord.getInstructors().size());

				for (People instructor : courseRecord.getInstructors())
		        {
					instructorJsonObj = new JSONObject();
					
					logger.debug("Course Record Instructor: " + instructor.getFirstName() + " " + instructor.getLastName());
					instructorJsonObj.accumulate("id", instructor.getId());
					instructorJsonObj.accumulate("userName", instructor.getUserName());
			    	instructorJsonObj.accumulate("firstName", instructor.getFirstName());
					instructorJsonObj.accumulate("lastName", instructor.getLastName());
					instructorJsonObj.accumulate("email", instructor.getEmail());
					instructorJsonObj.accumulate("phoneNumber", instructor.getPhoneNumber());
					instructorJsonObj.accumulate("postalCode", instructor.getPostalCode());
					instructorJsonObj.accumulate("action", "<a id='delete' href='javascript:Delete("+ instructorCount + ")' name='delete' data-params=''><span class='ui-icon ui-icon-trash'></span></a>");
					
					instructorCount++;
					instructorList.add(instructorJsonObj);
		        }
					
			} 

			
			logger.debug("Course Record Setting InstructorList: " + courseRecord.getInstructors().size());
			request.getSession().setAttribute("instructorList", courseRecord);
				
			JSONObject sendObj = new JSONObject();
	        sendObj.accumulate("iTotalRecords", ""+instructorCount);
	        sendObj.accumulate("iTotalDisplayRecords", "10");
	        if(!instructorList.isEmpty()){
	        	sendObj.put("aaData", instructorList);
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
				e.printStackTrace();
			}
			
		}
		
		public void getJsonAutoCompleteList(HttpServletRequest request, HttpServletResponse response, String organizationID, String userName, String firstName, String lastName){
			logger.debug("InstructorController getJsonAutoCompleteList " + request.getMethod());
			
			CourseRecord courseRecord = new CourseRecord();
			ArrayList <JSONObject> instructorList = new ArrayList<JSONObject>();
			
			JSONObject instructorJsonObj;

			if(organizationID != null && !organizationID.equals("")){
				courseRecord.setOrganizationID(organizationID);
				courseRecord.setContactID((userName != null?userName:null));
				courseRecord.setContactLastName((lastName != null?lastName:null));
				courseRecord.setContactFirstName((firstName != null?firstName:null));
				logger.debug("InstructorController getAvailableCourseRecordInstructors()");
				courseRecord = getSabaHelper().getAvailableCourseRecordInstructors(courseRecord);
			}
					

			logger.debug("isInstructor : " + courseRecord.getIsInstructor());
			
			if(courseRecord.getIsInstructor()){
				logger.debug("Course Record Instructor Roster Size: " + courseRecord.getInstructors().size());

				for (People instructor : courseRecord.getInstructors())
		        {
					instructorJsonObj = new JSONObject();
					
					logger.debug("Course Record Instructor: " + instructor.getFirstName() + " " + instructor.getLastName());
					instructorJsonObj.accumulate("id", instructor.getId());
					instructorJsonObj.accumulate("userName", instructor.getUserName());
			    	instructorJsonObj.accumulate("firstName", instructor.getFirstName());
					instructorJsonObj.accumulate("lastName", instructor.getLastName());					
					instructorList.add(instructorJsonObj);
		        }
					
			} 
				
			JSONObject sendObj = new JSONObject();
	        if(!instructorList.isEmpty()){
	        	sendObj.put("availableInstructors", instructorList);
	        } else {
	        	sendObj.accumulate("availableInstructors", "[]");
	        }
	        
	     	response.reset();
	        response.setContentType("application/json");
	        //response.setCharacterEncoding("UTF-8");
	        try {
	        	response.getWriter().write(sendObj.toString());
		        response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			
		}
		
		public boolean isInstructorExists(CourseRecord courseRecord, String id){
			
			for (People instructor : courseRecord.getInstructors())
	        {
					if(instructor.getId().equals(id))
						return true;
	        }
			
			return false;
		}

		public List getInstructorList() {
			return instructorList;
		}

		public void setInstructorList(List instructorList) {
			this.instructorList = instructorList;
		}

		
		private SabaHelper getSabaHelper(){
			
			return new SabaHelper();
		}

		
}

