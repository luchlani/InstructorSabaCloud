package com.arc.instructor.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.arc.instructor.model.CourseRecordSearch;
import com.arc.instructor.model.People;
import com.arc.instructor.model.User;
import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;


@Controller
@RequestMapping(value={"/existingCourseRecordList"})
public class ExistingCourseRecordController {

		private final Log logger = LogFactory.getLog(getClass());
		

	    public ExistingCourseRecordController() {
	    	logger.debug("ExistingCourseRecordController Constructor ");		
	    }
	    
	    @RequestMapping(method=RequestMethod.GET)
		public void getExistingCourseRecordList(HttpServletRequest request, HttpServletResponse response, ModelMap model ){
	    	logger.debug("ExistingCourseRecordController getExistingCourseRecordList");
	    	
    		User user = (User) request.getSession().getAttribute("user");
    		
    		boolean showAll = (request.getParameter("showAll").equals("true")?true:false);
    		
    		request.getSession().setAttribute("showAllRecords", request.getParameter("showAll"));
    		
    		ArrayList<CourseRecordSearch> existingSabaCourseRecordList = getSabaHelper().getSearchResultSet(user.getPersonID(), user.getFeeOption(), showAll);
       		
    		JSONObject existingCourseRecordListJsonObj;

			ArrayList <JSONObject> existingCourseRecordList = new ArrayList<JSONObject>();


			logger.debug("Existing Saba Course Record List Size: " + existingSabaCourseRecordList.size());

			int existingCourseRecordCount = 0;
			for (CourseRecordSearch existingCourseRecord : existingSabaCourseRecordList)
	        {
				existingCourseRecordListJsonObj = new JSONObject();
				
				logger.debug("Sheet Number Name: " + existingCourseRecord.getSheetNumber());
				existingCourseRecordListJsonObj.accumulate("id", existingCourseRecord.getSheetNumberID());
				existingCourseRecordListJsonObj.accumulate("sheetNumber", existingCourseRecord.getSheetNumber());
				existingCourseRecordListJsonObj.accumulate("organizationName", existingCourseRecord.getOrganizationName());
				existingCourseRecordListJsonObj.accumulate("organizationID", existingCourseRecord.getOrganizationID());
				existingCourseRecordListJsonObj.accumulate("courseName", existingCourseRecord.getCourseName());
				existingCourseRecordListJsonObj.accumulate("endDate", existingCourseRecord.getEndDate());
				existingCourseRecordListJsonObj.accumulate("status", existingCourseRecord.getStatus());
				
				StringBuffer actions = new StringBuffer();
				
				if(user.getFeeOption().equals(user.NON_FEE_BASED)){
					actions.append("<table><tr><td style=\"padding: 4px;\"><a href=\"javascript:void(0);\" title=\"View\" onclick=\"viewRow("+existingCourseRecord.getSheetNumber()+")\" name=\"view\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon my-icon-eye\"></span></a></td></tr></table>");
	        	}
				
				//logger.debug("Existing CR null: " + (existingCourseRecord==null?"yes":"no"));
				//logger.debug("Existing CR isCertificatesIssued: " + existingCourseRecord.isCertificatesIssued());
				//logger.debug("Existing CR getStatus: " + existingCourseRecord.getStatus());
				
				if(user.getFeeOption().equals(user.FEE_BASED) && existingCourseRecord.isCertificatesIssued() && (existingCourseRecord.getStatus().equals("Draft") || existingCourseRecord.getStatus().equals("Rejected"))){
					actions.append("<table><tr><td style=\"padding: 1px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"View\" onclick=\"viewRow("+existingCourseRecord.getSheetNumber()+");\" name=\"view\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon my-icon-eye\"></span></a>");
					actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 1px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"Copy\" onclick=\"copyRow("+existingCourseRecord.getSheetNumber()+");\" name=\"copy\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-copy\" ></span></a>");
					actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 5px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"Edit\" onclick=\"editRow("+existingCourseRecord.getSheetNumber()+");\" name=\"edit\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-pencil\" ></span></a>");
					actions.append("</td><td style=\"padding: 4px;\">");
					actions.append("&nbsp;");
					actions.append("</td></tr></table>");
				}
				
				if(user.getFeeOption().equals(user.FACILITY_FEE_BASED) && (existingCourseRecord.getStatus().equals("Draft") || existingCourseRecord.getStatus().equals("Rejected"))){
                    actions.append("<table><tr><td style=\"padding: 1px;\">");
                    actions.append("<a href=\"javascript:void(0);\" title=\"View\" onclick=\"viewRow("+existingCourseRecord.getSheetNumber()+");\" name=\"view\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon my-icon-eye\"></span></a>");
                    actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 1px;\">");
                    actions.append("<a href=\"javascript:void(0);\" title=\"Copy\" onclick=\"copyRow("+existingCourseRecord.getSheetNumber()+");\" name=\"copy\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-copy\" ></span></a>");
                    actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 5px;\">");
                    actions.append("<a href=\"javascript:void(0);\" title=\"Edit\" onclick=\"editRow("+existingCourseRecord.getSheetNumber()+");\" name=\"edit\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-pencil\" ></span></a>");
                    actions.append("</td><td style=\"padding: 4px;\">");
                    actions.append("&nbsp;");
                    actions.append("</td></tr></table>");
                }
				
				if(user.getFeeOption().equals(user.FEE_BASED) && !existingCourseRecord.isCertificatesIssued() && (existingCourseRecord.getStatus().equals("Draft") || existingCourseRecord.getStatus().equals("Rejected"))){
					actions.append("<table><tr><td style=\"padding: 4px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"View\" onclick=\"viewRow("+existingCourseRecord.getSheetNumber()+");\" name=\"view\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon my-icon-eye\"></span></a>");
					actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 4px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"Copy\" onclick=\"copyRow("+existingCourseRecord.getSheetNumber()+");\" name=\"copy\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-copy\" ></span></a>");
					actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 4px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"Edit\" onclick=\"editRow("+existingCourseRecord.getSheetNumber()+");\" name=\"edit\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-pencil\" ></span></a>");
					actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 5px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"Delete\" onclick=\"deleteRow('"+existingCourseRecord.getSheetNumber()+"', '"+existingCourseRecord.getSheetNumberID()+"');\" name=\"delete\" data-params=\""+existingCourseRecord.getSheetNumberID()+ "\"><span class=\"ui-icon ui-icon-trash\" ></span></a>");
					actions.append("</td></tr></table>");
				}
				
				
				if(user.getFeeOption().equals(user.FEE_BASED) && !existingCourseRecord.getStatus().equals("Draft") && !existingCourseRecord.getStatus().equals("Rejected")){
					actions.append("<table><tr><td style=\"padding: 0px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"View\" onclick=\"viewRow("+existingCourseRecord.getSheetNumber()+");\" name=\"view\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon my-icon-eye\"></span></a>");
					actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 5px;\">");
					actions.append("<a href=\"javascript:void(0);\" title=\"Copy\" onclick=\"copyRow("+existingCourseRecord.getSheetNumber()+");\" name=\"copy\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-copy\" ></span></a>");
					actions.append("</td><td style=\"padding: 4px;\">&nbsp;");
					actions.append("</td><td style=\"padding: 5px;\">&nbsp;");
					actions.append("</td></tr></table>");
				}
				
				if(user.getFeeOption().equals(user.FACILITY_FEE_BASED) && !existingCourseRecord.getStatus().equals("Draft") && !existingCourseRecord.getStatus().equals("Rejected")){
                    actions.append("<table><tr><td style=\"padding: 0px;\">");
                    actions.append("<a href=\"javascript:void(0);\" title=\"View\" onclick=\"viewRow("+existingCourseRecord.getSheetNumber()+");\" name=\"view\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon my-icon-eye\"></span></a>");
                    actions.append("</td><td style=\"border-left: 1px solid #C0C0C0; padding: 5px;\">");
                    actions.append("<a href=\"javascript:void(0);\" title=\"Copy\" onclick=\"copyRow("+existingCourseRecord.getSheetNumber()+");\" name=\"copy\" data-params=\""+existingCourseRecord.getSheetNumber()+ "\"><span class=\"ui-icon ui-icon-copy\" ></span></a>");
                    actions.append("</td><td style=\"padding: 4px;\">&nbsp;");
                    actions.append("</td><td style=\"padding: 5px;\">&nbsp;");
                    actions.append("</td></tr></table>");
                }
				
				existingCourseRecordListJsonObj.accumulate("action", actions.toString());

				
				existingCourseRecordList.add(existingCourseRecordListJsonObj);
				existingCourseRecordCount++;
	        }
			

					    			        
			JSONObject sendObj = new JSONObject();
			//sendObj.accumulate("sEcho", "10");
	        sendObj.accumulate("iTotalRecords", ""+existingCourseRecordCount);
	        sendObj.accumulate("iTotalDisplayRecords", "10");
	        if(!existingCourseRecordList.isEmpty()){
	        	sendObj.put("aaData", existingCourseRecordList);
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

		private SabaHelper getSabaHelper(){
			
			return new SabaHelper();
		}
		
}

