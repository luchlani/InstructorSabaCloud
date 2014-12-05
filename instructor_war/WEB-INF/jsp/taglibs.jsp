<%@ page session="false"%>

<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="str" uri="http://jakarta.apache.org/taglibs/string-1.1" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%!
	public boolean isBrowserIE8OrLess( String Information ) {
		String browserInformation[] = (getBrowserInfo(Information)).split("-");
		if (browserInformation == null || browserInformation.length == 0)
			return false;
		String browserType = browserInformation[0];
		String version = browserInformation[1];
		double browserVersion = 0.0;
		 
		
		try{
			browserVersion = Double.parseDouble(version.trim()); 
    	} catch (NumberFormatException nfe) {
      		System.out.println("NumberFormatException: " + nfe.getMessage());
    	}
		
		System.out.println("Browser " + browserType + "  Version " + browserVersion);
		
		if(browserType.equals("MSIE") && browserVersion < 9)
			return true;
		
		return false;
	}

	public String  getBrowserInfo( String Information ) {
          String browsername = "";
          String browserversion = "";
          String browser = Information  ;
          if(browser.contains("MSIE")){
              String subsString = browser.substring( browser.indexOf("MSIE"));
              String Info[] = (subsString.split(";")[0]).split(" ");
              if (Info == null || Info.length == 0)
        			return "";
              browsername = Info[0];
              browserversion = Info[1];
           }
         else if(browser.contains("Firefox")){

              String subsString = browser.substring( browser.indexOf("Firefox"));
              String Info[] = (subsString.split(" ")[0]).split("/");
              browsername = Info[0];
              browserversion = Info[1];
         }
         else if(browser.contains("Chrome")){

              String subsString = browser.substring( browser.indexOf("Chrome"));
              String Info[] = (subsString.split(" ")[0]).split("/");
              browsername = Info[0];
              browserversion = Info[1];
         }
         else if(browser.contains("Opera")){

              String subsString = browser.substring( browser.indexOf("Opera"));
              String Info[] = (subsString.split(" ")[0]).split("/");
              browsername = Info[0];
              browserversion = Info[1];
         }
         else if(browser.contains("Safari")){

              String subsString = browser.substring( browser.indexOf("Safari"));
              String Info[] = (subsString.split(" ")[0]).split("/");
              browsername = Info[0];
              browserversion = Info[1];
         }          
    	return browsername + "-" + browserversion;
	}

%> 

