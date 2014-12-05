package com.arc.instructor;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;


public class SabaRESTAPIWrapper
{
  public static final String kSabaApiUrl = "https://na1t1.sabacloud.com/Saba/api";
  public static final String kSite = "TNBTNT079";
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  
  public static void main(String[] args) throws Exception
  {
    SabaRESTAPIWrapper wrapper = new SabaRESTAPIWrapper();
    String certificate = null;
    Map<String, String> loginInfo = wrapper.authenticate("luchlani", "welcome0");
    //System.out.println("Login Info=" + loginInfo);
    
    Map<String, String> orgs = wrapper.getOrganizations(loginInfo.get("username"), loginInfo.get("certificate"));
    System.out.println(orgs);
    //Map<String, String> courses = wrapper.getReachCourses(cert, "B1");
    //System.out.println(courses);
  }
  
  
  /** Authenticates credentials and returns Id, username and Certificate. 
      Returns a Map containing id, username and certificate
  */
  public Map<String, String> authenticate(String username, String password) throws Exception
  {
    HttpHeaders headers = new HttpHeaders();
    headers.set("user", username);
    headers.set("password", password);
    headers.set("site", kSite);
    HttpEntity entity = new HttpEntity(headers);

    RestTemplate restTemplate = new RestTemplate();
    
    try
    {
      ResponseEntity<String> response = restTemplate.exchange(kSabaApiUrl + "/login", HttpMethod.GET, entity, String.class);
      JSONObject object = new JSONObject(response.getBody());
      String certificate =  object.getString("certificate");

      String responseStr = executeSabaAPI("/component/people/username=" + username + ":(securityRoles)?type=external", certificate);
      
      object = new JSONObject(cleanDupSecRoles(responseStr));
      String userId = object.getString("id");
      JSONArray secRoles = object.getJSONArray("securityRoles");
      for(int i = 0 ; i < secRoles.length() ; i++)
      {
        JSONObject secRole = secRoles.getJSONObject(i);
        String secRoleName = secRole.getString("name");
        if(secRoleName.equals("Learning Admin - Instructor"))
        {
          Map<String, String> person = new HashMap<String, String>();
          person.put("id", userId);
          person.put("username", username);
          person.put("certificate", certificate);
          return person;
        }
      }
      throw new Exception("Learning Admin - Instructor role not granted. Please contact the System Administrator");
    }
    catch(HttpClientErrorException ex)
    {
      if(ex.getMessage().startsWith("401 "))
      {
        throw new Exception("Invalid username/password");
      }
      else
      {
        throw ex;
      }
    }
  }
  
  public Map<String, String> getReachCourses(String certificate, String category) throws JSONException, ParseException
  {
    RestTemplate restTemplate = new RestTemplate();
    String findReachCoursesUrl = kSabaApiUrl + "/course?includeDetails=true&q=(";
    
    String today = dateFormat.format(new Date());
    String conditions = "";
    conditions = addCondition(conditions, "custom1", category);
    conditions = addCondition(conditions, "custom2", "false");
    conditions = addCondition(conditions, "custom3", "true");
    conditions = addCondition(conditions, "custom5", "false");
    conditions = addCondition(conditions, "custom6", "0");
    conditions = addCondition(conditions, "custom7", "true");
    conditions = addCondition(conditions, "avail_from", today, "=le=");
    
    //GAP: Need to add condition for 
    //- Avail from and Disc from of Delivery Type
    //- Delivery type of Classroom only
    
    String response = restTemplate.getForObject(kSabaApiUrl + "/component/course?includeDetails=true&count=1000&SabaCertificate=" + certificate + "&q=(" + conditions + ")", String.class);
    Map<String, String> courses = new HashMap<String, String>();
    JSONObject respObj = new JSONObject(response);
    JSONArray results = respObj.getJSONArray("results");    
    for(int i = 0; i < results.length() ; i++)
    { 
      JSONObject record = results.getJSONObject(i);
      
      if(isDiscontinued(record))
      {
        //Skip the row if course is discontinued
        continue;
      }
      
      JSONObject customValues = record.getJSONObject("customValues");
      if(!customValues.isNull("custom8"))
      {
        boolean facFeeCourse = customValues.getBoolean("custom8");
        if(facFeeCourse)
        {
          //Skip the row if course is Facility Fee course
          continue;
        }
      }

      String id = record.getString("id");
      String title = record.getString("title") + " - " + record.getString("course_no") + " " + (record.isNull("version") ? "" : record.getString("version"));
      courses.put(id, title);
    }
    return courses;
  }
  
  public Map<String, String> getOrganizations(String username, String certificate) throws Exception
  {
    String responseStr = executeSabaAPI("/component/people/username=" + username + "?type=external", certificate);
    System.out.println(responseStr);
    JSONObject object = new JSONObject(cleanDupSecRoles(responseStr));
    
    Map<String, String> map = new HashMap<String, String>();
   if(!object.isNull("home_company_id"))
    {
      JSONObject homeCompany = object.getJSONObject("home_company_id");
      map.put(homeCompany.getString("id"), homeCompany.getString("displayName"));
    }
    
    if(!object.isNull("company_id"))
    { 
      JSONObject company = object.getJSONObject("company_id");
      map.put(company.getString("id"), company.getString("displayName"));
    }
    
    return map;
  
  }
  
  private static String addCondition(String fullConditionString, String newCondition, String value, String operator)
  {
    String returnStr = fullConditionString==null ? "" : fullConditionString;
    
    if(returnStr.length()> 0) 
    {
      returnStr += ",";
    }
    
    return returnStr + newCondition + operator + value;
  }
  
  private static String addCondition(String fullConditionString, String newCondition, String value)
  {
    return addCondition(fullConditionString, newCondition, value, "==");
  }
  
  
  private boolean isDiscontinued(JSONObject record) throws JSONException
  {
    if(record.isNull("disc_from")) return false;
    
    String discFromStr = record.getString("disc_from").substring(0,10);
    try
    {
      Date discFrom = dateFormat.parse(discFromStr);
      if(discFrom.before(new Date()))
      {
        return true;
      }
      return false;
    }
    catch(ParseException pe)
    {
      System.out.println("Error parsing disc_from date: " + discFromStr + " - " + pe.getMessage());
      return false;
    }
  }
  
  
  private String cleanDupSecRoles(String input)
  {
    if(input.indexOf("securityRoles")==-1) return input;
    if(input.indexOf("securityRoles")==input.lastIndexOf("securityRoles")) return input;
    
    String temp = input.replaceAll("\"securityRoles\":null,", "");
    String output = temp.replaceAll(",\"securityRoles\":null", "");
    System.out.println(output);
    return output;
  }
  
  private String executeSabaAPI(String uri, String certificate)
  {
      HttpHeaders headers = new HttpHeaders();
      headers.set("SabaCertificate", certificate);
      HttpEntity entity = new HttpEntity(headers);
      RestTemplate restTemplate = new RestTemplate();
      
      ResponseEntity<String> response = restTemplate.exchange(kSabaApiUrl + uri, HttpMethod.GET, entity, String.class);
      return response.getBody();
  
  }
}