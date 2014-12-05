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

import net.sf.json.JSONObject;

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

import com.arc.instructor.utils.DropDown;
import com.arc.instructor.utils.SabaHelper;
import com.sun.xml.bind.v2.schemagen.xmlschema.List;

@Controller
@RequestMapping(value={"/addressList"})
public class ListController {

		private final Log logger = LogFactory.getLog(getClass());
		
		private List list;
		
		private Jaxb2Marshaller jaxbMarshaller;

	    public ListController() {
	    	logger.debug("ListController Constructor ");	
	    	
			ApplicationContext context = new ClassPathXmlApplicationContext("jaxb-configuration.xml");
			
			this.jaxbMarshaller = (Jaxb2Marshaller)context.getBean("jaxbMarshaller");
			
			Map<String, Boolean> marshallerProperties = new HashMap<String, Boolean>();
			marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshallerProperties.put(Marshaller.JAXB_FRAGMENT, true);
			this.jaxbMarshaller.setMarshallerProperties(marshallerProperties);
			
	    }
	    
	    @RequestMapping(method=RequestMethod.GET)
		public void getAddressList(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type,@RequestParam("zip") String zip,@RequestParam("state") String state, ModelMap model ){
	    	logger.debug("ListController getAddressList");
			logger.debug("ListController Type: "+ type);
			logger.debug("ListController State: "+ state);
			logger.debug("ListController Zip: "+ zip);


			ArrayList<String> addressList = new ArrayList<String>();
			
			
			if(!zip.equals("")){
				addressList = getSabaHelper().getCityStateCounty(zip);
			} else {
				addressList = getSabaHelper().getCountyList(state);
			}
			
	    	if(type.contains("xml")){
	    		getXmlResults(request, response, "addressList", addressList);
	    	} else {
	    		getJsonResults(request, response, "addressList", addressList);
	    	}
	    }
	    
	    
	    
		private void getXmlResults(HttpServletRequest request, HttpServletResponse response, String rootNode, ArrayList<String> listOfStrings) {
			logger.debug("ListController getXmlResults");
			
			StringBuffer xml = new StringBuffer();
	    	
			xml.append("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>");
			xml.append("<"+rootNode+">");
			
			for (String value : listOfStrings)
	        {
				DropDown dropDown = new DropDown();
				dropDown.setKey(value);
				dropDown.setValue(value);
				
		    	Writer outWriter = new StringWriter();  
		    	StreamResult result = new StreamResult( outWriter );  
				this.jaxbMarshaller.marshal(dropDown, result);  
				StringWriter sw = (StringWriter) result.getWriter(); 
				StringBuffer sb = sw.getBuffer(); 
				xml.append(sb);
	        }

			xml.append("</"+rootNode+">");
			
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

		private void getJsonResults(HttpServletRequest request, HttpServletResponse response,  String rootNode, ArrayList<String> listOfStrings) {
			logger.debug("ListController getJsonResults");
			
			ArrayList <JSONObject> jsonList = new ArrayList<JSONObject>();
			
			JSONObject optionJsonObj;
	
			for (String value : listOfStrings)
	        {
				optionJsonObj = new JSONObject();
				optionJsonObj.accumulate("key", value);
				optionJsonObj.accumulate("value", value);
				jsonList.add(optionJsonObj);
	        }

			
			JSONObject sendObj = new JSONObject();
	        sendObj.put(rootNode, jsonList);
	        
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

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}

		private SabaHelper getSabaHelper(){
			
			return new SabaHelper();
		}
		
}

