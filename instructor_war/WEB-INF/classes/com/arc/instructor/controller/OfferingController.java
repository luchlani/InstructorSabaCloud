package com.arc.instructor.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.JSON;

import com.arc.instructor.model.User;
import com.arc.instructor.model.OfferingSearch;
import com.arc.instructor.model.OfferingDetail;
import com.arc.instructor.model.People;
import com.arc.instructor.model.CourseComponent;
import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;
import com.arc.instructor.RosterEntryDetail;
import com.saba.ARC.custom.RosterEntryComponent;
import com.saba.ARC.custom.OfferingSession;



@Controller
public class OfferingController {

	
	//Static variables for Menu
	private static final String itemNotCurrent = "<li class=\"nav-item\">";
	private static final String itemCurrent = "<li class=\"nav-item current\">";
	private static final String itemEnd = "</li>";
	
	private static final String searchItemLink = "<a class=\"nav-link\" href=\"/instructor/offering/offeringSearch.html\"><span>Set Up Classes</span></a>";
	private static final String blendedItemLink ="<a class=\"nav-link\" href=\"/instructor/offering/blendedOffering.html\"><span>Create a Blended Learning Class</span></a>";
	private static final String iltItemLink = "<a class=\"nav-link\" href=\"/instructor/offering/iltOffering.html\"><span title=\"Set up WSI instructor-led classes here. To set up WSI Blended Learning classes, go to the Blended Learning tab.\">Create a WSI Instructor-Led Only Class</span></a>";                
		                


	private SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    public OfferingController() 
	{
    		
    }





	/*****************************************     GET CALLS (non-Ajax) - Used to display pages   ************************************************/
	
	/** Shows the Search Offerings Page 
	 *  The page will make asynchronous calls to fetch the data
	 */	 	
	@RequestMapping(value = {"offeringSearch"}, method = RequestMethod.GET)
	public String initForm(HttpServletRequest request,ModelMap model)
	{
    	User user = (User) request.getSession().getAttribute("user");
    	try
		{
			getSabaHelper().forceSabaLogin(user);
		}
		catch(Exception e)
		{
			return handleLoginFailedException(request, e);
		}
    	model.addAttribute("user", user);
		return "offeringSearch";
	}
	
	
	/** Show an empty Create Session Offering Page or Edit Session Offering page
	 * For New offering, the mappings blendedOffering and iltOffering are used to determine whether offering is blended.
	 * For existing offering, the delivery type from the offering is used to determine whether offering is blended. 	 
	*/	
	@RequestMapping(value = {"blendedOffering", "iltOffering", "scheduledOffering"}, method = RequestMethod.GET)
	public String initClassroomOffering(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception
	{
		try
		{
			User user = (User) request.getSession().getAttribute("user");
			
			String action = (String)request.getParameter("action");
			if(action==null || action.equals(""))
			{
				action = "New";
			}
			if(action.equals("GetDetail"))
			{
				String offeringId = (String)request.getParameter("offeringId");
				if(offeringId==null || offeringId.trim().equals(""))
				{
					throw new IllegalArgumentException("No Offering Id provided");
				}
				
				OfferingDetail offDetail;
				try
				{
					getSabaHelper().forceSabaLogin(user);
					offDetail = getSabaHelper().getSessionOfferingDetail(offeringId);
				}
				catch(Exception e)
				{
					return handleGetException("Error retrieving details for offering.", model, user, e);
				}
				offDetail.setOfferingAction("Edit");
				
				offDetail.setDeeplink(getDeeplink(request, offeringId));
	
				model.addAttribute("offeringDetail", offDetail);
				boolean isBlended = Boolean.parseBoolean(offDetail.getBlended());
				String pageTitle = isBlended ? "Edit Blended Learning Class" : "Edit WSI Instructor-Led Class";
				model.addAttribute("pageTitle", pageTitle);
				model.addAttribute("username", user.getUsername());
	
				//Add dropdown lists
				String orgId = offDetail.getOrgId();
				model.addAttribute("organizationList", getSabaHelper().getOrganizationList(user.getPersonID()));
				model.addAttribute("facilityList", getSabaHelper().getFacilities(orgId));
				model.addAttribute("purchaseOrderList", getPurchaseOrderList(orgId, "0.0"));
				
				//Add instructors and sessions
				model.addAttribute("javaSessionArray", sessionArrayToJSON(offDetail.getSessions(), showDelete(offDetail)));
				model.addAttribute("javaInstructorArray", instrArrayToJSON(offDetail.getInstructors(), showDelete(offDetail)));
				 
				return "offeringDetail";
			}
			
			//Action = Create Offering.
			boolean isBlended = request.getRequestURI().contains("blendedOffering");
			String pageTitle = isBlended ? "Create Blended Learning Class" : "Create WSI Instructor-Led Class";
			model.addAttribute("pageTitle", pageTitle);
			model.addAttribute("pageText", "Please enter the information below. You must enter required class information (* items) in order to save.");
			model.addAttribute("username", user.getUsername());
			
			//Org List
			getSabaHelper().forceSabaLogin(user);
			model.addAttribute("organizationList", getSabaHelper().getOrganizationList(user.getPersonID()));
			
			//Category List
			if(isBlended)
			{
				List<DropDown> categoryList = getSabaHelper().getCourseCategoriesForBlended_APTool(); 
				model.addAttribute("courseCategoryList", categoryList);
			}
			
			//Delivery Type List
			//model.addAttribute("deliveryTypeList", getEmptyList("Select Course first"));
	
			//Facility List
			model.addAttribute("facilityList", getEmptyList("Select Organization first"));
			
			//Purchase Order List
			model.addAttribute("purchaseOrderList", getEmptyList("Select Organization first"));
			
			//Empty OfferingDetail
			OfferingDetail offDetail = new OfferingDetail();
			offDetail.setOfferingAction("Create");
			offDetail.setBlended(String.valueOf(isBlended));
			model.addAttribute("offeringDetail",offDetail);		
	
			return "offeringDetail";
		}
		catch(Exception e)
		{
			return handleCatchallException(request, model, e);
		}
	}
	
	
	
	/** Show the Create new WBT Offering Page
	*
	* Show an empty Create Online Offering Page 
	*/	
	@RequestMapping(value = {"onlineOffering"}, method = RequestMethod.GET)
	public String initOnlineOffering(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception
	{
		try
		{
			User user = (User) request.getSession().getAttribute("user");
			
			String action = (String)request.getParameter("action");
			
			if(action==null || action.equals(""))
			{
				action = "New";
			}
			if(action.equals("GetDetail"))
			{
				String offeringId = (String)request.getParameter("offeringId");
				if(offeringId==null || offeringId.trim().equals(""))
				{
					throw new IllegalArgumentException("No Offering Id provided");
				}
				
				OfferingDetail offDetail;
				try
				{
					getSabaHelper().forceSabaLogin(user);
					offDetail = getSabaHelper().getOnlineOfferingDetail(offeringId);
				}
				catch(Exception e)
				{
					return handleGetException("Error retrieving details for offering.", model, user, e);
				}
				offDetail.setOfferingAction("Edit");
				offDetail.setDeeplink(getDeeplink(request, offeringId));
	
				model.addAttribute("offeringDetail", offDetail);
				model.addAttribute("pageTitle", "Edit Online Offering");
				//model.addAttribute("pageText", "Please enter the information below");
				model.addAttribute("username", user.getUsername());
	
				//Add dropdown lists
				String orgId = offDetail.getOrgId();
				model.addAttribute("organizationList", getSabaHelper().getOrganizationList(user.getPersonID()));
				model.addAttribute("purchaseOrderList", getSabaHelper().getPurchaseOrderList(orgId, "0.0"));
				
				return "onlineDetail";
			}
			
			//Action = Create Offering.
			model.addAttribute("pageTitle", "Create Online Offering");
			model.addAttribute("pageText", "Please enter the information below");
			model.addAttribute("username", user.getUsername());
			
			//Org List
			getSabaHelper().forceSabaLogin(user);
			model.addAttribute("organizationList", getSabaHelper().getOrganizationList(user.getPersonID()));
			
			//Category List
			model.addAttribute("courseCategoryList", getSabaHelper().getCourseCategoryList());
			
			//Purchase Order List
			model.addAttribute("purchaseOrderList", getEmptyList("Select Organization first"));
			
			//Empty OfferingDetail
			OfferingDetail offDetail = new OfferingDetail();
			offDetail.setOfferingAction("Create");
			model.addAttribute("offeringDetail",offDetail);		
	
			return "onlineDetail";
		}
		catch(Exception e)
		{
			return handleCatchallException(request, model, e);
		}
	}
	
	
	
	
	/** Show the Roster Page
	*
	*/	
	@RequestMapping(value = {"roster"}, method = RequestMethod.GET)
	public String showRoster(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception
	{
		try
		{
			User user = (User) request.getSession().getAttribute("user");
			
			String offeringId = (String)request.getParameter("offeringId");
			if(offeringId==null || offeringId.trim().equals(""))
			{
				throw new IllegalArgumentException("No Offering Id provided");
			}
			
			OfferingDetail offDetail;
			try
			{
				getSabaHelper().forceSabaLogin(user);
				offDetail = getSabaHelper().getOfferingDetail(offeringId);
				addComponentInformation(model, offDetail);
			}
			catch(Exception e)
			{
				return handleGetException("Error showing Offering Roster.", model, user, e);
			}
			model.addAttribute("offeringDetail", offDetail);
			model.addAttribute("pageTitle", "Roster");
			model.addAttribute("username", user.getUsername());
			return "roster";
		}
		catch(Exception e)
		{
			return handleCatchallException(request, model, e);
		}
	}
	
	
	
	
	/** Show the Print Certificates Page
	*
	*/	
	@RequestMapping(value = {"certificates"}, method = RequestMethod.GET)
	public String showCertificates(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception
	{
		try
		{
			User user = (User) request.getSession().getAttribute("user");
			
			String offeringId = (String)request.getParameter("offeringId");
			if(offeringId==null || offeringId.trim().equals(""))
			{
				throw new IllegalArgumentException("No Offering Id provided");
			}
			OfferingDetail offDetail;
			try
			{
				getSabaHelper().forceSabaLogin(user);
				offDetail = getSabaHelper().getOfferingDetail(offeringId);
				List<Map<String, String>> students = getSabaHelper().getCompletedRegistrations(offeringId);
				model.addAttribute("javaStudentArray", studentArrayToJSON(students));
			}
			catch(Exception e)
			{
				return handleGetException("Error showing Offering Certificates.", model, user, e);
			}
			model.addAttribute("offeringDetail", offDetail);
			model.addAttribute("pageTitle", "Roster");
			model.addAttribute("username", user.getUsername());
			return "offeringCertificates";
		}
		catch(Exception e)
		{
			return handleCatchallException(request, model, e);
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/*****************************************  POST CALLS (non-Ajax) used to Save data  ****************************************************/
	
	
	
	/** Process various actions for Session-based Offering.
	 * CLOSE - Show the Search Offerings Page.
	 * CREATE - Create a Session-based Offering.
	 * EDIT - Update a Session-based Offering.
	 * ROSTER - Show the Roster page.	 	 	 	
	 */
	@RequestMapping(value = {"blendedOffering", "iltOffering", "scheduledOffering"}, method = RequestMethod.POST)
	public String scheduledOffering(HttpServletRequest request, ModelMap model, @ModelAttribute("offeringDetail") OfferingDetail offeringDetail) throws Exception
	{
		try
		{
			User user = (User) request.getSession().getAttribute("user");
			model.addAttribute("username", user.getUsername());
			
			String action = offeringDetail.getOfferingAction(); 
			
			if(action.equals("Close"))
			{
				return "redirect:/offering/offeringSearch.html";
			}
			
			if(action.equals("Create"))
			{
				offeringDetail.setContactId(user.getPersonID());
				
				String instrField = request.getParameter("instrArrayHiddenField");
				String sessionsField = request.getParameter("sessionArrayHiddenField");
							
				//Instructors
				List<People> instructors = new ArrayList<People>();
				for(int i=0 ; i < instrField.length() ; i = i+20)
				{
					People instr = new People();
					instr.setId(instrField.substring(i, i+20));
					instructors.add(instr);
				}
				offeringDetail.setInstructors(instructors);
				
				//Sessions
				List<OfferingSession> sessions = new ArrayList<OfferingSession>();
				
				for(int i=0 ; i < sessionsField.length() ; i = i+26)
				{
					String strSession = sessionsField.substring(i,i+26);
					String strDate = strSession.substring(0, 10);
					String strStartTime = strSession.substring(10,18);
					String strEndTime = strSession.substring(18);
					
					OfferingSession session = new OfferingSession();
					session.setDate(format.parse(strDate));
					session.setStartTime(get24HrTime(strStartTime));
					session.setEndTime(get24HrTime(strEndTime));
					sessions.add(session);
				}
				offeringDetail.setSessions(sessions);
				
				//Create Offering
				String offeringId;
				try
				{
					getSabaHelper().forceSabaLogin(user);
					offeringId = getSabaHelper().createSessionOffering(offeringDetail);
				}
				catch(Exception e)
				{
					handleScheduledException("CreateSession", request, model, offeringDetail, user, e);
					return "offeringDetail";
				}
	
				OfferingDetail newDetail = getSabaHelper().getSessionOfferingDetail(offeringId);			
				newDetail.setDeeplink(getDeeplink(request, offeringId));
				model.addAttribute("offeringDetail", newDetail);
				return "offeringConfirmation";
			}
			
			if(action.equals("Edit"))
			{
				//Sessions
				String sessionsField = request.getParameter("sessionArrayHiddenField");
				List<OfferingSession> sessions = new ArrayList<OfferingSession>();
				for(int i=0 ; i < sessionsField.length() ; i = i+26)
				{
					String strSession = sessionsField.substring(i,i+26);
					String strDate = strSession.substring(0, 10);
					String strStartTime = strSession.substring(10,18);
					String strEndTime = strSession.substring(18);
					
					OfferingSession session = new OfferingSession();
					session.setDate(format.parse(strDate));
					session.setStartTime(get24HrTime(strStartTime));
					session.setEndTime(get24HrTime(strEndTime));
					sessions.add(session);
				}
				offeringDetail.setSessions(sessions);
				
				//Update Offering
				try
				{
					getSabaHelper().forceSabaLogin(user);
					getSabaHelper().updateSessionOffering(offeringDetail);
				}
				catch(Exception e)
				{
					handleScheduledException("EditSession", request, model, offeringDetail, user, e);
					return "offeringDetail";
				}
				
				OfferingDetail newDetail = getSabaHelper().getSessionOfferingDetail(offeringDetail.getSabaOfferingId());
				newDetail.setDeeplink(getDeeplink(request, offeringDetail.getSabaOfferingId()));
				model.addAttribute("offeringDetail", newDetail);
				return "offeringConfirmation";
			}
			
			return "redirect:/offering/offeringSearch.html";
		}
		catch(Exception e)
		{
			return handleCatchallException(request, model, e);
		}
	}

	
	
	
	/** Process various actions for an Online Offering.
	 * CLOSE - Show the Search Offerings Page.
	 * CREATE - Create an Online Offering.
	 * EDIT - Update an Online Offering.
	 */
	@RequestMapping(value = {"onlineOffering"}, method = RequestMethod.POST)
	public String onlineOffering(HttpServletRequest request, ModelMap model, @ModelAttribute("offeringDetail") OfferingDetail offeringDetail) throws Exception
	{
		try
		{
			User user = (User) request.getSession().getAttribute("user");
			model.addAttribute("username", user.getUsername());
			
			String action = offeringDetail.getOfferingAction(); 
			
			if(action.equals("Close"))
			{
				return "redirect:/offering/offeringSearch.html";
			}
			
			if(action.equals("Create"))
			{
				offeringDetail.setContactId(user.getPersonID());
				
				//Create Offering
				String offeringId;
				try
				{
					getSabaHelper().forceSabaLogin(user);
					offeringId = getSabaHelper().createOnlineOffering(offeringDetail);
				}
				catch(Exception e)
				{
					handleOnlineException("CreateOnline", request, model, offeringDetail, user, e);
					return "onlineDetail";
				}
				OfferingDetail newDetail = getSabaHelper().getOnlineOfferingDetail(offeringId);
				newDetail.setDeeplink(getDeeplink(request, offeringId));
				model.addAttribute("offeringDetail", newDetail);
				return "offeringConfirmation";
			}
			
			if(action.equals("Edit"))
			{
				//Update Offering
				try
				{
					getSabaHelper().forceSabaLogin(user);
					getSabaHelper().updateOnlineOffering(offeringDetail);
				}
				catch(Exception e)
				{
					handleOnlineException("EditOnline", request, model, offeringDetail, user, e);
					return "onlineDetail";
				}
				OfferingDetail newDetail = getSabaHelper().getOnlineOfferingDetail(offeringDetail.getSabaOfferingId());
				newDetail.setDeeplink(getDeeplink(request, offeringDetail.getSabaOfferingId()));
				model.addAttribute("offeringDetail", newDetail);
				return "offeringConfirmation";
			}
			
			if(action.equals("Roster"))
			{
				return "offeringRoster";
			}
			
			return "redirect:/offering/offeringSearch.html";
		}
		catch(Exception e)
		{
			return handleCatchallException(request, model, e);
		}
	}	
	
	
	
	/** Used for Saving Roster or marking delivered
	 */
	@RequestMapping(value = {"roster"}, method = RequestMethod.POST)
	public String saveRoster(HttpServletRequest request, ModelMap model) throws Exception
	{
		try
		{
			User user = (User) request.getSession().getAttribute("user");
					
			//Attempt to Login
			try
			{
				getSabaHelper().forceSabaLogin(user);
			}
			catch(Exception e)
			{
				return handleLoginFailedException(request, e);
			}
			
			//Check if Need to mark deliver or save roster
			String offeringId = request.getParameter("offeringId");
			String action = request.getParameter("offeringAction"); 
			if("MarkDelivered".equals(action))
			{
				return markDelivered(request, model);
			}
			
			if("SaveRoster".equals(action))
			{
				//Build the new roster detail.
				Map<String, List<RosterEntryComponent>> allCompletions = new HashMap<String, List<RosterEntryComponent>>();
				
				Enumeration e = request.getParameterNames();
				while(e.hasMoreElements())
				{
					String paramName = (String)e.nextElement();
					if(paramName.length()!=40 || !paramName.startsWith("regdw"))
					{
						continue;
					}
					String regId =  paramName.substring(0,20);
					
					//Form the RosterEntryComponent
					RosterEntryComponent completion = new RosterEntryComponent();
					completion.setComponentId(paramName.substring(20));
					completion.setStatus(request.getParameter(paramName));
					
					//Add the component to the overall map
					List<RosterEntryComponent> list = allCompletions.get(regId);
					if(list==null)
					{
						list = new ArrayList<RosterEntryComponent>();
					}
					list.add(completion);
					allCompletions.put(regId, list);
				}
				
				try
				{
					getSabaHelper().saveRoster(allCompletions);
				}
				catch(Exception ex)
				{
					handleRosterException(request, model, offeringId, user, ex);
					return "roster";
				}
				
				return populateRoster(model, user, offeringId, true);
			}	
			
			//Action Not recognized...simply show the Roster as is
			return populateRoster(model, user, offeringId, false);
		}
		catch(Exception e)
		{
			return handleCatchallException(request, model, e);
		}
	}
	
	
	public String markDelivered(HttpServletRequest request, ModelMap model) throws Exception
	{
		User user = (User) request.getSession().getAttribute("user");
		String offeringId = request.getParameter("offeringId");
		
		try
		{
			getSabaHelper().markDelivered(offeringId);
		}
		catch(Exception ex)
		{
			handleRosterException(request, model, offeringId, user, ex);
			return "roster";
		}	
		
		return populateRoster(model, user, offeringId, false);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***********************************************************     AJAX   CALLS    ******************************************/
	
	/** AJAX CALL: Used to Search offerings and return data in JSON format
	 * Used for populating the Search Results table on Offering Search page
	 */	 	
	@RequestMapping(value = {"doOfferingSearch"}, method=RequestMethod.GET)
	public void doOfferingSearch(HttpServletRequest request, HttpServletResponse response, ModelMap model)
	{
		//Re-authenticate User
		User user = (User) request.getSession().getAttribute("user");
		
		ArrayList<OfferingSearch> offeringList;
		
		try
		{
			getSabaHelper().forceSabaLogin(user);
			//Populate search results data
			offeringList = getSabaHelper().getOfferingSearchResultSet(user.getPersonID());
		}
		catch(Exception e)
		{
			logError("doOfferingSearch", e);
			JSONObject sendObj = new JSONObject();	
        	sendObj.accumulate("iTotalRecords", "0");
        	sendObj.accumulate("iTotalDisplayRecords", "10");
        	sendObj.accumulate("aaData", "[]");
	     	sendJsonObj(sendObj, response);
	     	return;
		}
		int count = 0;
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		for (OfferingSearch offering : offeringList)
        {
			JSONObject record = new JSONObject();
			record.accumulate("offeringId", offering.getOfferingNo());
			record.accumulate("courseName", offering.getCourseName());
			record.accumulate("delType", offering.getDeliveryType());
			record.accumulate("organizationName", offering.getOrgName());
			record.accumulate("startDate", offering.getStartDate());
			record.accumulate("status", offering.getStatus());
			
			String isOnline = offering.getDeliveryType().equals("Online") ? "true" : "false";
			
			//Create the Actions column
			StringBuffer actions = new StringBuffer();
			actions.append("<table><tr><td style=\"padding-right: 6px;\">");
			actions.append("<a href=\"javascript:void(0);\" title=\"Edit Class\" onclick=\"editOffering('"+offering.getSabaOfferingId()+"','" + isOnline + "');\" name=\"edit\" data-params=\""+offering.getSabaOfferingId()+ "\"><img src=\"../css/images/edit.png\"/></a>");
			actions.append("</td>");
			if(offering.getStatus()==null || !offering.getStatus().equals("Cancelled"))
			{
				actions.append("<td style=\"padding-left: 6px; padding-right: 6px;\">");
				actions.append("<a href=\"javascript:void(0);\" title=\"Manage Students\" onclick=\"viewRoster('"+offering.getSabaOfferingId()+"');\" name=\"roster\" data-params=\""+offering.getSabaOfferingId()+ "\"><img src=\"../css/images/roster.png\"/></a>");
				actions.append("</td><td style=\"padding-left: 6px;\">");
				actions.append("<a href=\"javascript:void(0);\" title=\"Print Certificates\" onclick=\"printCertificates('"+offering.getSabaOfferingId()+"');\" name=\"printCertificates\" data-params=\""+offering.getSabaOfferingId()+ "\"><img src=\"../css/images/certificates.png\"/></a>");
				actions.append("</td>");
			}
			actions.append("</tr></table>");
			record.accumulate("action", actions.toString());

			list.add(record);
			count++;
		}
		

		//Send JSON Object from Search results
		JSONObject sendObj = new JSONObject();	
        sendObj.accumulate("iTotalRecords", ""+count);
        sendObj.accumulate("iTotalDisplayRecords", "10");
        if(!list.isEmpty())
		{
        	sendObj.put("aaData", list);
        } 
		else 
		{
        	sendObj.accumulate("aaData", "[]");
        }
     	
     	sendJsonObj(sendObj, response);
	}
	
	
	
	/** AJAX CALL: Used to populate Autocomplete dropdowns for Filters on Search offerings page
	 * Returns the List of offeringNos, courses and delTypes in JSON format
	 */	
	@RequestMapping(value = {"getOfferingList"}, method = RequestMethod.GET)
	public void getOfferingList(HttpServletRequest request, HttpServletResponse response, ModelMap model)
	{
		User user = (User) request.getSession().getAttribute("user");
		
		
		Set<String> courses = new TreeSet<String>();
		Set<String> offerings = new TreeSet<String>();
		Set<String> delTypes = new TreeSet<String>();
		
		List<OfferingSearch> offeringsList;
		try
		{
			getSabaHelper().forceSabaLogin(user);
			offeringsList = getSabaHelper().getOfferingSearchResultSet(user.getPersonID());
		}
		catch(Exception e)
		{
			offeringsList = new ArrayList<OfferingSearch>();
			logError("getOfferingList", e);
		}
		
		for(OfferingSearch offering : offeringsList)
		{
			courses.add(offering.getCourseName());
			offerings.add(offering.getOfferingNo());
			delTypes.add(offering.getDeliveryType());
		}
		
  		
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.accumulate("courseList", courses);
		jsonObj.accumulate("offeringNoList", offerings);
		jsonObj.accumulate("delTypeList", delTypes);
		
     	sendJsonObj(jsonObj, response);
	}
	

	/** AJAX CALL: Get ILT Course List on Create/Edit Session Offering Page.
	 */	
	@RequestMapping(value = {"getCourseListForILT"})
	public void getCourseListForILT(HttpServletRequest request, HttpServletResponse response, ModelMap map)
	{
		boolean isBlended = false;
		String category = "";//Get all courses
		getCourseList(response, isBlended, category);
	}
	
	
	
	/** AJAX CALL: Get Blended Course List on Create/Edit Session Offering Page.
	 */	
	@RequestMapping(value = {"getCourseListForBlended"})
	public void getCourseListForBlended(HttpServletRequest request, HttpServletResponse response, ModelMap map)
	{
		boolean isBlended = true;
		String category = request.getParameter("category");
		getCourseList(response, isBlended, category);
	}
	
	
	
	/** AJAX CALL: Get Course List on Create/Edit Online Offering Page.
	 */	
	@RequestMapping(value = {"getCourseListForOnline"})
	public void getCourseListForOnline(HttpServletRequest request, HttpServletResponse response, ModelMap map)
	{
		List<DropDown> list;
		try
		{
			list = getSabaHelper().getCoursesForOnlineOfferings();
		}
		catch(Exception e)
		{
			logError("getCourseListForOnline", e);
			list = new ArrayList<DropDown>();
		}
		 
		JSONObject jsonObj = new JSONObject();
		jsonObj.accumulate("courseList", list);
		
		sendJsonObj(jsonObj, response);
	}
	

	/** AJAX CALL: Get the available session-based delivery types for chosen course on Create/Edit Session Offering page.
	 */	
	@RequestMapping(value = {"getDeliveryTypeList"})
	public void getDeliveryTypeList(HttpServletRequest request, HttpServletResponse response, ModelMap map)
	{
		List<DropDown> list;
		
		try
		{
			list = getSabaHelper().getSessionDeliveryTypes(request.getParameter("courseId"));
		}
		catch(Exception e)
		{
			logError("getDeliveryTypeList", e);
			list = new ArrayList<DropDown>();
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.accumulate("deliveryTypeList", list);
		
		sendJsonObj(jsonObj, response);
	}
	
	
	/** AJAX CALL: Get the AP Fee for chosen course.
	 */	
	@RequestMapping(value = {"getPriceForCourse"})
	public void getPriceForCourse(HttpServletRequest request, HttpServletResponse response, ModelMap map) 
	{
		String returnStr;
		try
		{
			returnStr = getSabaHelper().getPriceForCourse(request.getParameter("courseId"));
		}
		catch(Exception e)
		{
			logError("getPriceForCourse", e);
			returnStr = "Error fetching price";
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.accumulate("price", returnStr);
		
		sendJsonObj(jsonObj, response);
	}
	
	
	/** AJAX CALL: Get the available Purchase Orders for the chosen organization on Create/Edit Offering page.
	 */
	@RequestMapping(value = {"getPurchaseOrderList"})
	public void getPurchaseOrderList(HttpServletRequest request, HttpServletResponse response, ModelMap map)
	{
		User user = (User) request.getSession().getAttribute("user");
		
		List<DropDown> list;
		try
		{
			getSabaHelper().forceSabaLogin(user);
			list = getPurchaseOrderList(request.getParameter("orgId"),"0.0");
		}
		catch(Exception e)
		{
			logError("getPurchaseOrderList", e);
			list = new ArrayList<DropDown>();
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.accumulate("purchaseOrderList", list);
		
		sendJsonObj(jsonObj, response);
	}
	 	
	
	
	/** AJAX CALL: Get the available facilities for chosen organization on Create/Edit Session Offering page.
	 */
	@RequestMapping(value = {"getFacilityList"})
	public void getFacilityList(HttpServletRequest request, HttpServletResponse response, ModelMap map)
	{
		List<DropDown> list;;
		try
		{
			list = getSabaHelper().getFacilities(request.getParameter("orgId"));
		}
		catch(Exception e)
		{
			logError("getFacilityList", e);
			list = new ArrayList<DropDown>();
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.accumulate("facilityList", list);
		
		sendJsonObj(jsonObj, response);
	}
	
	
	/** AJAX CALL: Search instructors for a specific organization based on First name and last name provided. 
	 * Used in the instructor search screen. 
	 */
	@RequestMapping(value={"searchInstructor"})
	public void searchInstructor(HttpServletRequest request, HttpServletResponse response, ModelMap map) throws Exception
	{
		List<People> instructors;
		try
		{
		 	instructors = getSabaHelper().getAvailableOfferingInstructors(
														request.getParameter("orgId"), request.getParameter("firstName"), 
														request.getParameter("lastName"), request.getParameter("offeringId"));
		}
		catch(Exception e)
		{
			logError("searchInstructor", e);
			instructors = new ArrayList<People>();
		}
														
		JSONArray instrList = new JSONArray();
		for(People instructor : instructors)
		{
			JSONObject obj = new JSONObject();
			obj.accumulate("id", instructor.getId());
			obj.accumulate("firstName", instructor.getFirstName());
			obj.accumulate("lastName", instructor.getLastName());
			obj.accumulate("userName", instructor.getUserName());
			instrList.element(obj);
		}
		
		sendJsonObj(instrList, response);
	} 	 
	
		
	/** AJAX CALL: Delete an Instructor from an existing offering. 
	 */
	@RequestMapping(value={"deleteInstructor"})
	public void deleteInstructor(HttpServletRequest request, HttpServletResponse response, ModelMap map) throws Exception
	{
		User user = (User) request.getSession().getAttribute("user");
		
		String offeringId = request.getParameter("offeringId");
		String personId = request.getParameter("personId");
		
		getSabaHelper().forceSabaLogin(user);
		if(request.getParameter("personId")==null)
		{
			getSabaHelper().deleteAllInstructorsFromOffering(offeringId);
		}
		else
		{
			getSabaHelper().deleteInstructorFromOffering(offeringId, personId);
		}
		
		 
	} 	 
	
	
	/** AJAX CALL: Adds an Instructor to an existing offering. 
	 */
	@RequestMapping(value={"addInstructor"})
	public void addInstructor(HttpServletRequest request, HttpServletResponse response, ModelMap map) throws Exception
	{
		User user = (User) request.getSession().getAttribute("user");
		getSabaHelper().forceSabaLogin(user);
		getSabaHelper().addInstructorToOffering(request.getParameter("offeringId"), request.getParameter("personId"));
	} 	 	
	 



	















	/***************************************************************     EXCEPTION HANDLING      *************************************************/	
	
	private void handleScheduledException(String mode, HttpServletRequest request, ModelMap model, OfferingDetail offeringDetail, User user, Exception e) throws Exception
	{
		boolean isBlended = Boolean.parseBoolean(offeringDetail.getBlended());
		if(mode.equals("CreateSession"))
		{
			offeringDetail.setSabaOfferingId(null);
			offeringDetail.setOfferingAction("Create");
			String pageTitle = isBlended ? "Create Blended Learning Class" : "Create WSI Instructor-Led Class";
			model.addAttribute("pageTitle", pageTitle);
			model.addAttribute("pageText", "Please enter the information below. You must enter required class information (* items) in order to save. ");
			String courseId = offeringDetail.getCourseId();
			//model.addAttribute("deliveryTypeList", getSabaHelper().getSessionDeliveryTypes(courseId));
			offeringDetail.setPrice(getSabaHelper().getPriceForCourse(courseId));
			if(isBlended)
			{
				model.addAttribute("courseCategoryList", getSabaHelper().getCourseCategoriesForBlended_APTool());
			}
			
		}
		else if(mode.equals("EditSession"))
		{
			offeringDetail.setOfferingAction("Edit");
			String pageTitle = isBlended ? "Edit Blended Learning Class" : "Edit WSI Instructor-Led Class";
			model.addAttribute("pageTitle", pageTitle);
			//Replace the Detail object with that from Saba
			OfferingDetail oldDetail = offeringDetail;
			offeringDetail = getSabaHelper().getSessionOfferingDetail(oldDetail.getSabaOfferingId());
			
			//Now set the fields that may have changed on the UI
			offeringDetail.setDeeplink(getDeeplink(request, offeringDetail.getSabaOfferingId()));
			offeringDetail.setOfferingAction("Edit");
			offeringDetail.setOrgId(oldDetail.getOrgId());
			offeringDetail.setFacilityId(oldDetail.getFacilityId());
			offeringDetail.setPoId(oldDetail.getPoId());
			offeringDetail.setStartDate(oldDetail.getStartDate());
			offeringDetail.setMaxCount(oldDetail.getMaxCount());
			offeringDetail.setCouponCode(oldDetail.getCouponCode());
		}
		
		offeringDetail.setSabaMessage(e.getMessage());
		model.addAttribute("offeringDetail", offeringDetail);
		model.addAttribute("username", user.getUsername());
		model.addAttribute("organizationList", getSabaHelper().getOrganizationList(user.getPersonID()));
		
		String orgId = offeringDetail.getOrgId();
		model.addAttribute("facilityList", getSabaHelper().getFacilities(orgId));
		model.addAttribute("purchaseOrderList", getPurchaseOrderList(orgId, "0.0"));
		
		model.addAttribute("javaSessionArray", sessionArrayToJSON(offeringDetail.getSessions(), showDelete(offeringDetail)));
		model.addAttribute("javaInstructorArray", instrArrayToJSON(offeringDetail.getInstructors(), showDelete(offeringDetail)));
	}

	private void handleOnlineException(String mode, HttpServletRequest request, ModelMap model, OfferingDetail offeringDetail, User user, Exception e) throws Exception
	{
		if(mode.equals("CreateOnline"))
		{
			offeringDetail.setSabaOfferingId(null);
			offeringDetail.setOfferingAction("Create");
			model.addAttribute("pageTitle", "Create Online Offering");
			model.addAttribute("pageText", "Please enter the information below");
			String courseId = offeringDetail.getCourseId();
			offeringDetail.setPrice(getSabaHelper().getPriceForCourse(courseId));
		}
		else if(mode.equals("EditOnline"))
		{
			offeringDetail.setOfferingAction("Edit");
			model.addAttribute("pageTitle", "Edit Classroom Offering");
			
			//Replace the Detail object with that from Saba
			String offeringId = offeringDetail.getSabaOfferingId();
			OfferingDetail oldDetail = offeringDetail;
			offeringDetail = getSabaHelper().getOnlineOfferingDetail(offeringId);
			
			//Now set the fields that may have changed on the UI
			offeringDetail.setDeeplink(getDeeplink(request, offeringId));
			offeringDetail.setOfferingAction("Edit");
			offeringDetail.setOrgId(oldDetail.getOrgId());
			offeringDetail.setPoId(oldDetail.getPoId());
			offeringDetail.setAvailFrom(oldDetail.getAvailFrom());
			offeringDetail.setDiscFrom(oldDetail.getDiscFrom());
		}
		
		offeringDetail.setSabaMessage(e.getMessage());
		model.addAttribute("offeringDetail", offeringDetail);
		model.addAttribute("username", user.getUsername());
		model.addAttribute("organizationList", getSabaHelper().getOrganizationList(user.getPersonID()));
		
		String orgId = offeringDetail.getOrgId();
		model.addAttribute("purchaseOrderList", getSabaHelper().getPurchaseOrderList(orgId, "0.0"));
	}
	
	private void handleRosterException(HttpServletRequest request, ModelMap model, String offeringId, User user, Exception e) throws Exception
	{
		//Populate the Model
		OfferingDetail offDetail = getSabaHelper().getOfferingDetail(offeringId);
		offDetail.setSabaMessage(e.getMessage());
		model.addAttribute("offeringDetail", offDetail);
		model.addAttribute("pageTitle", "Roster");
		model.addAttribute("username", user.getUsername());
		
		//Add the Component Information
		addComponentInformation(model, offDetail);
	}
	
	
	private String handleGetException(String errLabel, ModelMap model, User user, Exception e) throws Exception
	{
		model.addAttribute("username", user.getUsername());
		model.addAttribute("errLabel", errLabel);
		model.addAttribute("errMsg", e.getMessage());
		System.out.println("OfferingController: Get Exception = ");
		e.printStackTrace();
		return "offeringError";
	} 
	
	private String handleLoginFailedException(HttpServletRequest request, Exception e)
	{
		String redirectUrl = request.getScheme() + "://" + request.getServerName() + "/instructor/authentication/login.html?feeOption=Scheduling";
		return "redirect:" + redirectUrl;
	}
	
	private String handleCatchallException(HttpServletRequest request, ModelMap model, Exception e)
	{
		e.printStackTrace();
		model.clear();
		String redirectUrl = request.getScheme() + "://" + request.getServerName() + "/instructor/authentication/login.html?feeOption=Scheduling";
		return "redirect:" + redirectUrl;
	}
	


















   /**************************************************************     STATIC UTILITY METHODS FOR JSP    *****************************************/
   public static String printMenuItems(HttpServletRequest request, String menuItem) throws Exception
   {
		StringBuffer buffer = new StringBuffer();
		
		boolean isScheduledMenuItem = (menuItem != null) && menuItem.equals("scheduledOffering");
		
		//By Default, set option as Blended. If offeringDetail found and blended is set to false, then set variable to false
		boolean isBlended = true;
		OfferingDetail offDetail = (OfferingDetail)request.getAttribute("offeringDetail");
		if(offDetail!=null && offDetail.getBlended().equals("false"))
		{
			isBlended = false;
		}
		
		//Add Search Menu Item
		buffer.append(!isScheduledMenuItem ? itemCurrent : itemNotCurrent);
		buffer.append(searchItemLink);
		buffer.append(itemEnd);
		
		//Add Blended Menu Item
		buffer.append((isScheduledMenuItem && isBlended) ? itemCurrent : itemNotCurrent);
		buffer.append(blendedItemLink);
		buffer.append(itemEnd);
		
		//Add ILT Menu Item	
		buffer.append((isScheduledMenuItem && !isBlended) ? itemCurrent : itemNotCurrent);
		buffer.append(iltItemLink);
		buffer.append(itemEnd);
		
		return buffer.toString();
   }








	/***************************************************************     PRIVATE UTILITY METHODS      *******************************************/


	private String populateRoster(ModelMap model, User user, String offeringId, boolean isSave) throws Exception
	{
		//Populate the Model
		OfferingDetail offDetail = getSabaHelper().getOfferingDetail(offeringId);
		if(isSave)
		{
			offDetail.setSabaMessage("Success");
		}
		model.addAttribute("offeringDetail", offDetail);
		model.addAttribute("pageTitle", "Roster");
		model.addAttribute("username", user.getUsername());
		
		//Add the Component Information
		addComponentInformation(model, offDetail);
		
		return "roster";
	}
	
	
	
	private List<DropDown> getPurchaseOrderList(String orgId, String amount) 
	{
		//Check if Org is null
		if(orgId == null || orgId.trim().equals(""))
		{
			return getEmptyList(" - Select Organization First - ");
		}
		
		
		//Else, get the list of purchase orders
		List<DropDown> list = getSabaHelper().getPurchaseOrderList(orgId, amount);
		if(list!=null && list.size()==1)
		{
			DropDown dd = list.get(0);
			if(dd.getKey()==null || dd.getKey().equals(""))
			{
				return getEmptyList("No Purchase Orders for Organization");
			}
		}
		return list;
	}
	
	
	
	
	
	private void getCourseList(HttpServletResponse response, boolean isBlended, String category)
	{
		List<DropDown> list;
		try
		{
			list = getSabaHelper().getCoursesForSessionOfferings(isBlended, category);
		}
		catch(Exception e)
		{
			String delivery = isBlended ? "Blended" : "Classroom";
			logError("getCourseListFor" + delivery, e);
			list = new ArrayList<DropDown>();
		}
		 
		JSONObject jsonObj = new JSONObject();
		jsonObj.accumulate("courseList", list);
		
		sendJsonObj(jsonObj, response);
	}
	
	
	
	private void addComponentInformation(ModelMap model, OfferingDetail offDetail) throws Exception
	{
		List<CourseComponent> componentHeaders =  getSabaHelper().getCourseComponent(offDetail.getCourseId());
		model.addAttribute("javaCompHeaderArray", compHeaderArrayToJSON(componentHeaders, offDetail.getDeliveryTypeName()));
		List<RosterEntryDetail> roster = getSabaHelper().getRoster(offDetail.getSabaOfferingId());
		model.addAttribute("javaRosterArray", rosterArrayToJSON(roster, componentHeaders, offDetail));
	}

	private void sendJsonObj(JSON sendObj, HttpServletResponse response)
	{
		response.reset();
        response.setContentType("application/json");
        try 
		{
        	response.getWriter().write(sendObj.toString());
	        response.getWriter().flush();
	    }
	    catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private String sessionArrayToJSON(List<OfferingSession> sessions, boolean showDelete)
	{
		String kAction1 = "<a id='delete' href='javascript:deleteSession(\"";
		String kAction2 = "\")' name='delete'><span class='ui-icon ui-icon-trash'></span></a>";
		
		List<JSONObject> list = new ArrayList<JSONObject>();
		for(OfferingSession session : sessions)
		{
			JSONObject obj = new JSONObject();
			String dateStr = format.format(session.getDate());
			String uniqueId = dateStr + session.getStartTime() + session.getEndTime();
			obj.accumulate("uniqueId", uniqueId);
			obj.accumulate("date", dateStr);
			obj.accumulate("startTime", get12HrTime(session.getStartTime()));
			obj.accumulate("endTime", get12HrTime(session.getEndTime()));
			
			String actionStr = "";
			if(showDelete)
			{	
				actionStr = kAction1 + uniqueId + kAction2;
			}
			obj.accumulate("action", actionStr); 
			list.add(obj);
		}
		return list.toString();
	}
	
	private String instrArrayToJSON(List<People> instructors, boolean showDelete) throws Exception
	{
		if(instructors==null) return "[]";
		
		String kAction1 = "<a id='delete' href='javascript:deleteInstructor(\"";
		String kAction2 = "\")' name='delete'><span class='ui-icon ui-icon-trash'></span></a>";
		
		List<JSONObject> list = new ArrayList<JSONObject>();
		for(People person : instructors)
		{
			JSONObject obj = new JSONObject();
			obj.accumulate("personID", person.getId());
			
			if(person.getFirstName()==null && person.getFirstName()==null)
			{
				People personFromSaba = getSabaHelper().getPerson(person.getId());
				person.setFirstName(personFromSaba.getFirstName());
				person.setLastName(personFromSaba.getLastName());
			} 
			obj.accumulate("firstName", person.getFirstName());
			obj.accumulate("lastName", person.getLastName());
			
			String actionStr = "";
			if(showDelete)
			{
				actionStr = kAction1 + person.getId() + kAction2;
			}
			obj.accumulate("action", actionStr);
			list.add(obj);
		}
		return list.toString();
	}
	
	
	private String rosterArrayToJSON(List<RosterEntryDetail> roster, List<CourseComponent> compHeaders, OfferingDetail offDetail) throws Exception
	{
		if(roster==null) return "[]";
		
		String deliveryTypeName = offDetail.getDeliveryTypeName();
		String status = offDetail.getStatus();
		//Select List for each component
		
		
		List<JSONObject> list = new ArrayList<JSONObject>();
		for(RosterEntryDetail student : roster)
		{
			JSONObject obj = new JSONObject();
			obj.accumulate("regId", student.getRegId());
			obj.accumulate("firstName", student.getFirstName());
			obj.accumulate("lastName", student.getLastName());
			obj.accumulate("username", student.getUsername());
			
			//Add Online Completed? flag if blended/online
			if(deliveryTypeName.equals("Blended Learning") || deliveryTypeName.equals("Online"))
			{
				obj.accumulate("isOnlineSuccessful", student.isOnlineSuccessful() ? "Yes" : "No");
			}
			
			//Show components if session-based
			if(!deliveryTypeName.equals("Online"))
			{
				//Add course components
				List<RosterEntryComponent> components = student.getComponents();
				for(RosterEntryComponent component : components)
				{
					String html;
					if(component.isDropped())//Adhoc transcript for component was deleted from saba.
					{
						html = "Dropped";
					}
					else if(status.equals("200"))//Offering is delivered
					{
						html = component.getStatus();
					}
					else if(deliveryTypeName.equals("Blended Learning") && student.isOnlineSuccessful()==false)//Online portion not completed
					{
						html = component.getStatus();
					}
					else
					{
						html = getSelectHtml(student.getRegId(), component.getComponentId(), component.getStatus());
					}
					obj.accumulate(component.getComponentId(), html);
				}
				
				//Add rest of course components that are not present on the transcript and show status as not evaluated.
				for(CourseComponent component : compHeaders)
				{
					if(!obj.has(component.getCourseComponentID()))
					{
						String html;
						if(status.equals("200"))
						{
							html = "Not Evaluated";
						}
						else if(deliveryTypeName.equals("Blended Learning") && student.isOnlineSuccessful()==false)//Online portion not completed
						{
							html = "Not Evaluated";
						}
						else
						{
							html = getSelectHtml(student.getRegId(), component.getCourseComponentID(), "Not Evaluated");
						}
						obj.accumulate(component.getCourseComponentID(), html);	
					}
				}
			}
			
			
			list.add(obj);
		}
		return list.toString();
	}
	
	
	private String compHeaderArrayToJSON(List<CourseComponent> courseComps, String deliveryTypeName) throws Exception
	{
		if(courseComps==null) return "[]";
		
		List<JSONObject> list = new ArrayList<JSONObject>();
		
		//Add First name, last name, username and OnlineSuccess headers
		list.add(getColumnObject("firstName", "First Name"));
		list.add(getColumnObject("lastName", "Last Name"));
		list.add(getColumnObject("username", "Username"));
		if(deliveryTypeName.equals("Blended Learning") || deliveryTypeName.equals("Online"))
		{
			list.add(getColumnObject("isOnlineSuccessful", "Online Portion Completed?"));
		}
		
		if(!deliveryTypeName.equals("Online"))
		{
			for(CourseComponent comp : courseComps)
			{
				list.add(getColumnObject(comp.getCourseComponentID(), comp.getCourseComponentLabel()));
			}
		}
		return list.toString();
	}
	
	
	private String getSelectHtml(String regId, String compId, String status) throws Exception
	{
		String selectHtml = "<select name=\"" + regId + compId + "\" class=\"input-tiny\">";
		selectHtml += "<option title='Met all course requirements'>Successful</option>";
		selectHtml += "<option title='Attended but did not meet all course requirements'>Unsuccessful</option>";
		selectHtml += "<option title='Not tested on skills and/or knowledge;  No show'>Not Evaluated</option>";
		selectHtml += "</select>";
		
		if(status==null || (!status.equals("Successful") && !status.equals("Unsuccessful") && !status.equals("Not Evaluated")))
		{
			throw new IllegalArgumentException("Invalid Status: " + status);
		}
		
		return selectHtml.replaceAll(">" + status + "</option>", " selected>" + status + "</option>");
	}
	
	private JSONObject getColumnObject(String columnName, String columnUILabel)
	{
		JSONObject obj = new JSONObject();
		obj.accumulate("mData", columnName);
		obj.accumulate("sName", columnName);
		obj.accumulate("sTitle", columnUILabel);
		return obj;
	}
	
	
	private String studentArrayToJSON(List<Map<String, String>> students)
	{
		if(students==null) return "[]";
		
		List<JSONObject> list = new ArrayList<JSONObject>();
		
		for(Map<String, String> student : students)
		{
			JSONObject obj = new JSONObject();
			obj.accumulate("username", student.get("username"));
			obj.accumulate("fullname", student.get("fullname"));
			String certId = student.get("certId");
			String expandedId = student.get("expandedId");
			
			String fullCertJSFunction = "showCertificates('FullCertificate','t=" + certId + "');";
			String walletCertJSFunction = "showCertificates('WalletCertificate','certificate_mode=student&t=" + expandedId + "');";
			
			
			StringBuffer fullCertLink = new StringBuffer();
			fullCertLink.append("<table><tr><td style=\"text-align: center; padding: 1px;\">");
			fullCertLink.append("<a href=\"javascript:void(0);\" onclick=\"" + fullCertJSFunction + "\">8.5 x 11 Certificate</a>");
			fullCertLink.append("</td></tr></table>");
			obj.accumulate("fullCertificate", fullCertLink.toString());
			
			StringBuffer walletCertLink = new StringBuffer();
			walletCertLink.append("<table><tr><td style=\"text-align: center; padding: 1px;\">");
			walletCertLink.append("<a href=\"javascript:void(0);\" onclick=\"" + walletCertJSFunction + "\">Wallet Certificate</a>");
			walletCertLink.append("</td></tr></table>");
			obj.accumulate("walletCertificate", walletCertLink.toString());
			
			 
			list.add(obj);
		}
		return list.toString();
		
	}
	
	//Input paramter is in the form 05:00 PM. It must be converted to 17:00
	//Also, Input represents 12:30 AM, which must be converted to 00:30
	private String get24HrTime(String strTime)
	{
		String time = strTime.trim();
		if(time.length() != 8)
		{
			throw new IllegalArgumentException("Invalid Session Time");
		}
		
		String hrs = time.substring(0,2);
		String mins = time.substring(3,5);
		String ampm = time.substring(6);
		
		
		if(ampm.equals("AM"))
		{
			if(hrs.equals("12"))
			{
				return "00" + ":" + mins;
			}
			else
			{
				return hrs + ":" + mins;
			}
		}
		else
		{
			if(hrs.equals("12"))
			{
				return hrs + ":" + mins;
			}
			else
			{
				int iHrs = Integer.parseInt(hrs) + 12;
				return iHrs + ":" + mins;
			}
		}
	}
	
	//Input paramter is in the form 17:00. It must be converted to 05:00 PM
	//Also, Input represents 00:30, which must be converted to 12:30 AM
	private String get12HrTime(String strTime) 
	{
		String time = strTime.trim();
		if(time.length() != 5)
		{
			throw new IllegalArgumentException("Invalid Session Time=" + strTime);
		}
		
		String hrsStr = time.substring(0,2);
		String minStr = time.substring(3);
		int hrs = Integer.parseInt(hrsStr);
		int mins = Integer.parseInt(minStr);
		
		if(hrs < 0 || hrs > 23 || mins < 0 || mins > 59)
		{
			throw new IllegalArgumentException("Invalid Session Time=" + strTime);
		}
		
		if(hrs < 12)
		{
			if(hrs==0)
			{
				return "12:" + minStr + " AM" ;
			}
			else
			{
				return hrsStr + ":" + minStr + " AM";
			}
		}
		else
		{
			if(hrs==12)
			{
				return "12:" + minStr + " PM" ;
			}
			else
			{
				hrs = hrs-12;
				hrsStr = (hrs < 10) ? ("0" + hrs) : ("" + hrs);
				return hrsStr + ":" + minStr + " PM";
			}
		}
	}
	
	private String getDeeplink(HttpServletRequest request, String offeringId) throws Exception
	{
		String uri = getSabaHelper().getRegistrationDeeplinkUri(offeringId);
		String http_or_https = request.getScheme();
    	String server_name = request.getServerName();
    	String deeplink = http_or_https + "://" + server_name + uri; 
		return deeplink;
	} 
	
	private boolean showDelete(OfferingDetail detail)
	{
		if(detail.getSabaOfferingId()==null || detail.getSabaOfferingId().trim().equals(""))
		{
			return true;
		}
		
		if(detail.getStatus()==null || detail.getStatus().trim().equals("") || detail.getStatus().equals("100"))
		{
			return true;	
		}
		
		return false;
	
	}
	
	private void logError(String method, Exception e)
	{
		System.out.println("Exception in OfferingController." + method + ":" + e.getMessage());
		e.printStackTrace();
	}
	
	private List<DropDown> getEmptyList(String defaultValue)
	{
		List<DropDown> emptyList = new ArrayList<DropDown>();
		DropDown dd = new DropDown();
	    dd.setKey("");
	    dd.setValue(defaultValue);
		emptyList.add(dd);
		return emptyList;
	}
	
	private SabaHelper getSabaHelper()
	{
		return new SabaHelper();
	}
	
	private boolean isEmpty(String value)
	{
		if(value==null) return true;
		
		value = value.trim();
		if(value.equals("")) return true;
		
		return false;
	
	}
	
}