package com.arc.instructor.utils;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
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
   
    Map<String, String> orgs = wrapper.getOrganizations(loginInfo.get("username"), loginInfo.get("certificate"));
    System.out.println(orgs);
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
    HttpEntity<String> entity = new HttpEntity<String>(headers);

    RestTemplate restTemplate = new RestTemplate();
    
    try
    {
      ResponseEntity<String> response = restTemplate.exchange(kSabaApiUrl + "/login", HttpMethod.GET, entity, String.class);
      JSONObject object = JSONObject.fromObject(response.getBody());
      String certificate =  object.getString("certificate");

      String responseStr = executeSabaAPI("/component/people/username=" + username + ":(securityRoles)?type=external", certificate);
      
      object = JSONObject.fromObject(cleanDupSecRoles(responseStr));
      String userId = object.getString("id");
      List secRoles = object.getJSONArray("securityRoles");
      Iterator i = secRoles.iterator();
      while(i.hasNext())
      {
        JSONObject secRole = (JSONObject)i.next();
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
    JSONObject respObj = JSONObject.fromObject(response);
    List results = respObj.getJSONArray("results"); 
    Iterator i = results.iterator();   
    while(i.hasNext())
    { 
      JSONObject record = (JSONObject)i.next();
      if(isDiscontinued(record))
      {
        //Skip the row if course is discontinued
        continue;
      }
      
      JSONObject customValues = record.getJSONObject("customValues");
      if(customValues.get("custom8")!=null)
      {
        boolean facFeeCourse = customValues.getBoolean("custom8");
        if(facFeeCourse)
        {
          //Skip the row if course is Facility Fee course
          continue;
        }
      }

      String id = record.getString("id");
      String title = record.getString("title") + " - " + record.getString("course_no") + " " + (record.get("version")==null ? "" : record.getString("version"));
      courses.put(id, title);
    }
    return courses;
  }
  
  public Map<String, String> getOrganizations(String username, String certificate) throws Exception
  {
    String responseStr = executeSabaAPI("/component/people/username=" + username + "?type=external", certificate);
    System.out.println(responseStr);
    JSONObject object = JSONObject.fromObject(cleanDupSecRoles(responseStr));
    
    Map<String, String> map = new HashMap<String, String>();
   if(object.get("home_company_id")!=null)
    {
      JSONObject homeCompany = object.getJSONObject("home_company_id");
      map.put(homeCompany.getString("id"), homeCompany.getString("displayName"));
    }
    
    if(object.get("company_id")!=null)
    { 
      JSONObject company = object.getJSONObject("company_id");
      map.put(company.getString("id"), company.getString("displayName"));
    }
    
    return map;
  }
  
  
  //////////////////////////////////// SKELETON APIs ///////////////////////////////////////////////////////////////
  	/**
	 * Authenticates user with Saba.	
	 * @param userName
	 * @param passWord
	 *
	 * @throws Exception, Exception
	 * Exception is thrown when a user without CR Administrator privilege tries to log in.
	 */
	public void authenticateUser(String userName, String passWord) throws Exception, Exception
	{
	 throw new Exception("Not Implemented.");
  }
	
	/**
	 * Logs out a particular user
	 */
	public void logout() throws Exception
	{
	 throw new Exception("Not Implemented.");
  }
	
	/**
	 * Returns the Saba personId for the logged on user (ex persn000000000001080)
	 * @return personId
	 * @throws Exception
	 */
	public String getPersonId() throws Exception
	{
		throw new Exception("Not Implemented.");
	}
	/**
	 * Checks whether a person has been authenticated previously
	 * @return true if authenticated and false if not
	 */
	public boolean isAuthenticated() throws Exception
	{
		throw new Exception("Not Implemented.");
	}
	
	/**
	 * Finds Instructor for a particular organization
	 * @param organizationId
	 * @return Map<String, String[]> A map with key-value pair of instructor ID and an array containing instructorDetail consisting of first name and last name.
	 * @throws Exception
	 * @throws Exception
	 */
	public Map<String, String[]> findInstructor(String organizationId) throws Exception, Exception
	{
		throw new Exception("Not Implemented.");
	}
	
	/**
	 * Returns values to populate the summary page. 
	 * @param crsId Course record sheet id (ex. crrsh000000000001040)
	 * @return Map<String, String> where keys are:
	 * crsId, crsNo, orgId, status, contact_id, contact_fname, contact_lname
	 * end_date, trainingCenterName, address, city, state, zip, county, unit_code, total_students, total_successful, total_unsuccessful, total_not_evaluated
	 * org_name, course_id, course_title, course_no, course_version
	 * offering_no, order_no, approved_on, approver_comment, approver_username, approver_fname, approver_lname 
	 * date_submitted, trainingCenterName, contact_username, skip_students, original_price, final_price, coupon_code, certs_issued, certs_issued_on, holder_paytype, holder_po_id    
	 * <pre> If the payment details exists and payment type is purchase order then the following additional fields are returned in the map
	 * payType (== PO), poId, and purchaseOrderName
	 * If the payment type is cc, then the following additional fields are returned with the map
	 *  card_first_name, card_last_name, card_address1, card_address2, card_city, card_state, card_zip, card_type, card_no
	 * If no payment detail exists, then the map will return either values for holder_paytype and holder_po_id or null for these fields
	 * </pre>
	 * @throws Exception
	 */
	
	public Map<String, String> findCRS(String crsId) throws Exception
	{
		throw new Exception("Not Implemented.");
	}


	/**
	 * Finds Instructor based on either one of Instructor first name, last name, number or user name AND organization AND endDate. (crsId is optional)
	 * @param instructorDetails A map with key-value pair of instructorUserName, instructorFName, instructorLName, instructorNo, organizationId
	 * Either one of instructorUserName, instructorFName, instructorLName, and instructorNo is sufficient. However, organizationId is mandatory
	 * The search is based on begins with criteria
	 * @return Map<String, String[]> A map with key-value pair of instructor ID and an array containing instructorDetail consisting of first name and last name.
	 * @throws Exception Generic exception is thrown when no organization ID is passed to the method
	 */
	
	public Map<String, String[]> findInstructor(Map<String, String> instructorDetails) throws Exception
	{
		throw new Exception("Not Implemented.");
	}
	
	/**
	 * Adds Instructors to a course record sheet
	 * @param crsId
	 * @param instructorIds (A list of instructorIds)
	 * @throws Exception
	 */
	public void addInstructor(String crsId, List<String> instructorIds) throws Exception
	{
		throw new Exception("Not Implemented.");
	}
	
	/**
	 * Adds a single instructor to a course record sheet.
	 * @param crsId
	 * @param personId
	 * @throws Exception
	 */
	public void addInstructor(String crsId, String personId) throws Exception
	{
	 throw new Exception("Not Implemented.");
	}
	
	/**
	 * Remove a single instructor from a course record sheet
	 * @param crsId
	 * @param personId
	 * @throws Exception
	 */
	public void removeInstructor(String crsId, String personId) throws Exception
	{
		  throw new Exception("Not Implemented.");
	}
	
	/**
	 * Finds all non-fee courses to populate the course drop down menu
	 * @return course A map with key-value pair of courseId and course name
	 * @throws Exception
	 */
	public Map<String, String> findCourse() throws Exception
	{
		throw new Exception("Not Implemented.");
	}
	
	
	
	/**
	 * Finds all non-fee courses filtered by the FOCIS Subject Area passed as a filter. 
	 * @return course A map with key-value pair of courseId and course name
	 * @throws Exception
	 */
	public Map<String, String> findCourse(String filter) throws Exception
	{
		throw new Exception("Not Implemented.");
	}
	

	/**
	 * Find courses for fee based CRS based on the filter provided.
	 * @param filter
	 * @return Map<String, String[]> the string array consist of 1) at index 0: course name and 2) at index 1: skip students flag
	 * @throws Exception
	 */
	public Map<String, String[]> findCourseForFeeBasedCrs(String filter) throws Exception
	{
	 throw new Exception("Not Implemented.");
  }
	
	/**
	 * Finds all non-fee courses for aquatics
	 * @return course A map with key-value pair of courseId and courseName
	 * @throws SabaException
	 * 
	 */
	@Deprecated	 
	public Map<String, String> findCourseForAquatics() throws Exception
	{
		return findCourse("A3");
	}
	
	
	/**
	 * Finds Courses to populate the course drop down menu for Facility Fee Option
	 * @return course A map with key-value pair of courseId and course name
	 * @throws Exception
	 */
	public Map<String, String> findCoursesForFacilityFee() throws Exception
	{
	 throw new Exception("Not Implemented.");
  }
	
	/**
	 * Returns course components for ARC course
	 * @param courseId
	 * @return Map<String, String> where key is the course Id and value is the course component associated.
	 * @throws Exception
	 */
	public Map<String, String> getCourseComponent(String courseId) throws Exception
	{
	 throw new Exception("Not Implemented.");
  }
	
	
	/**
	 * Returns a map of organization based on the contact person that is logged in
	 * @param id for the person logged in 
	 * @return organization A map of key-value pair consisting of organizationId and organization name
	 * Returns empty map if no records are found
	 * @throws Exception
	 */
	public Map<String,String> getOrgsForContact(String contactId) throws Exception 
	{
	 throw new Exception("Not Implemented.");
  }
	
	public Map<String, String[]> getOrgsForContactWUnitCode(String contactId) throws Exception
	{
	 throw new Exception("Not Implemented.");
  }
	
	
	/**
	 * Returns a Map with CRS id as the key and CRS detail as value 
	 * @param Id of the person logged in
	 * @return CRSDetail 
	 * CRSDetail is a map that has CRS number (ex crrsh000000000001000) as key and the following fields as value
	 * 1) CRS number (ex 00001000)
	 * 2) Organization name
	 * 3) Organization number (ex 00000)
	 * 4) Course number (ex cours000000000001000)
	 * 5) Course name
	 * 6) End Date for the course
	 * 7) Status (Approved/draft/submitted/rejected)
	 * 8) Certs Issued (Will always be Y)
	 * 9) Contact Id     	 
	 * Returns empty map if no record is found
	 * @throws Exception
	 */
	public Map<String, String[]> findCRSForLandingPage(String contactId, boolean onlyContactsCRS) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Returns a Map<String, String[] consisting of FeeBased Crs to populate the search page
	 * @param contactId
	 * @return Map<String, String[]> where key is the crsId and value is a string array of crs number, organization
	 * name, organization number, course name, course version, end date, crs status, certs_issued and contact_id
	 * @throws Exception
	 */
	public Map<String, String[]> findFeeBasedCRSForLandingPage(String contactId, boolean onlyContactsCRS) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Returns a Map<String, String[] consisting of Offerings belonging to the contact's organizations to populate the search page
	 * @param contactId
	 * @return Map<String, String[]> where key is the sabaOfferingId and value is a string array of 
	 * offering No, course, Delivery Type, org name, org number, start date, end date, status
	 * @throws Exception
	 */
	public Map<String, String[]> findOfferings(String contactId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
		/**
	 * Returns a Map<String, String[] consisting of FacilityFee Crs to populate the search page
	 * @param contactId String
	 * @param onlyContactCRS boolean	 
	 * @return Map<String, String[]> where key is the crsId and value is a string array of crs number, organization
	 * name, organization number, course name, course version, end date, crs status, certs_issued and contact_id
	 * @throws Exception
	 */
	public Map<String, String[]> findFacilityFeeCRSForLandingPage(String contactId, boolean onlyContactsCRS) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Method returns a Map of instructors for a particular course record sheet id
	 * @param crsId (ex crrsh00000000001404)
	 * @return Map<String, String[]> Key-value pair of instructor id and instructor first name, last name and user name respectively
	 * @throws Exception
	 */
	public Map<String, String[]> findInstructorForCrs(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Method returns a Map of instructors for a particular Offering
	 * @param offeringId (ex class000000000001000)
	 * @return Map<String, String[]> Key-value pair of instructor id and instructor first name, last name and user name respectively
	 * @throws Exception
	 */
	public Map<String, String[]> findInstructorForOffering(String offeringId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	
	/**
	 * Returns city, state and county for a given zip code
	 * @param zip
	 * @return Map<String, String[]> where the key is the zip code and the value is an array of city, state, and county 
	 * Returns an empty map if no zip is passed
	 * @throws Exception
	 */
	public Map<String, String[]> findState(String zip) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Creates an Anonymous Non-Fee Course RecordSheet. Returns CRS No (unlike other APIs that return IDs)
	 * @param Map<String, String> fields, Map extraFields, List instructorIds
	 * Map<String, String> fields contains the following keys
	 * 				contact_id, orgId, course_id, address, city, state, zip, county, noOfStudents, totalSuccessful, totalUnsuccessful, totalNotEvaluated
	 * 				end_date, trainingCenterName
	 * Map extraFields contains the following keys
	 * 				target_children, target_adults, target_seniors, target_low_income, target_military, target_minority, target_functional_needs
  	 *				other_2013_learn_to_swim, other_americorps, other_rating_member, 
	 * @throws Exception
	 */
	public String createAnonymousNonFeeCrs(Map<String, String> fields, Map extraFields, List instructorIds) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Creates crs. Expects a Map<String, String> and List of instructorIds
	 * The map should contain following key/values
	 * 	contact_id, orgId, course_id, address, city, state, zip, county, skipStudents, noOfStudents, totalSuccessful, totalUnsuccessful, totalNotEvaluated
	 *  end_date, trainingCenterName
	 *  Pass in payType and poId if its a pre-create CRS.
	 * @param fields
	 * @param instructorIds
	 * @return
	 * @throws Exception
	 */
	public String createCrs(Map<String, String> fields, List instructorIds) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * 
	 * @param crsId
	 * @throws Exception
	 */
	public void deleteCRS(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	/**
	 * Updates existing CRS. Status has to be draft or rejected. CrsId, contact_id, orgId and course_id cannot be changed. 
	 * If certificates have already been issued, noOfStudents and 
	 * skipStudents cannot be changed. 
	 * @param fields
	 * 	crsId, contact_id, orgId, course_id, address, city, state, zip, county, noOfStudents, totalSuccessful, totalUnsuccessful, totalNotEvaluated
	 * 				end_date, trainingCenterName
	 * @throws Exception
	 */
	public void updateCrs(Map<String, String> fields) throws Exception
	{throw new Exception("Not Implemented.");
	}
	/**
	 * Returns the purchase orders for an organization
	 * @param orgId 
	 * @param amount
	 * @return Map<String, String[]> where key is the purchase order id and value is a string array of purchase order no and terms.
	 * @throws Exception
	 */
	public Map<String, String[]> getPurchaseOrder(String orgId, String amount) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Checks if skipStudents is allowed for a particular course
	 * @param courseId
	 * @return true is skipStudents is allowed and false if skipStudents is not allowed
	 * @throws Exception
	 */
	public boolean isSkipStudentsAllowed(String courseId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Adds student to a specified CRS.
	 * @param crsId
	 * @param studentDetail A Map<String, String> with the following keys:
	 * 		1)first_name, 2)last_name, 3)email, 4)phone, 5)addl_info
	 * @param transcripts A Map<String, String> where the key is the component ID and the value is one of Successful, Unsuccessful or 
	 * Not Evaluated.
	 * @return String this is the entry Id. 
	 * @throws Exception
	 */
	public String addStudents(String crsId, Map<String, String> studentDetail, Map<String, String> transcripts) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Updates existing student record for a specified CRS.
	 * @param crsId
	 * @param studentDetail A Map<String, String> with the following keys:
	 * 		1)entryId, 2)first_name, 3)last_name, 4)email, 5)phone, 6)addl_info
	 * @param transcripts A Map<String, String> where the key is the component ID and the value is one of Successful, Unsuccessful or 
	 * Not Evaluated.
	 * @throws Exception
	 */
	public void updateStudents(String crsId, Map<String, String> studentDetail, Map<String, String> transcripts) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Returns student detail for a specified crs
	 * @param crsId
	 * @return Map<String,Map<String,String>> where the key is the student Id and Map<String,String> is as follows:
	 *  1) first_name, first name, 2) last_name, last name, 3) email, email, 4) phone_number, phone
	 * 5) course component ID, [successful, unsuccessful or not evaluated]...this depends on the number of course
	 * components present for the course associated with the crs.
	 * 
	 * @throws Exception
	 */
	public Map<String, Map<String, String>> getStudents(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Removes student from a given CRS.
	 * @param crsId
	 * @param studentId (the entry id for a student ex crsen00000000001000)
	 * @throws Exception
	 */
	public void removeStudents (String crsId, String studentId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Applies a coupon to a given crs
	 * @param crsId
	 * @param couponCode
	 * @throws Exception
	 */
	public void applyCouponToCrs(String crsId, String couponCode) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Removes a specified coupon from  a given CRS
	 * @param crsId
	 * @throws Exception
	 */
	public void removeCouponFromCrs(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	

	/**
	 * Issues certificates to students in a Course record sheet.
	 * @param crsId
	 * @throws Exception
	 */
	public void issueCertificateToStudents(String crsId) throws Exception, Exception
	{throw new Exception("Not Implemented.");
	}
	
	public List paymentProcessing(HttpServletRequest request) throws Exception
	{throw new Exception("Not Implemented.");
	}
	/**
	 * Creates a signature to validate credit card information sent to CyberSource
	 * @param amount
	 * @param currency
	 * @param transactionType
	 * @param crsId
	 * @return
	 * @throws Exception
	 */
	public HashMap getCCTransactionSignature(String amount, String currency, String transactionType, String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Required to call this function prior to creating credit card transaction
	 * @param crsId
	 * @throws Exception
	 */
	public void insertCCTrack(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Get the static values for ECommerce - profile_id, access_key and security_key
	 * @return HashMap<key, value>: key: property key, value: property value
	 * @throws Exception
	 */
	public Map<String, String> getCCStaticValues(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Get the value for Signature and Timestamp
	 * @param Map<String, String> - All the parameters along with their values required to be signed. 
	 * @return Map<String, String> with 2 keys - signature and signed_date_time
	 * @throws Exception
	 */
	public Map<String, String> generateSignature(String merchantId, Map<String, String> paramsToSign) 
	{throw new RuntimeException("Not Implemented.");
	}
	
	/**
	 * Sends out an email notifying that a CRS has been precreated
	 * @param crsId
	 * @throws SabaException
	 */
	@Deprecated	
	public void crsPreCreateNotification(String crsId) throws Exception
	{
		
	}
	/**
	 * Adds a purchase order to a fee based CRS.
	 * @param crsId
	 * @param purchaseOrderId
	 * @throws Exception
	 */
	public void addPurchaseOrder(String crsId, String purchaseOrderId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	
	/**
	 * Returns a list of of courses for scheduling ILT/Blended offerings
	 * @return courses A list of String arrays. Each array has 2 values - Id and Course title+course_no+version.
	 * Returns empty list if no records are found
	 * @throws Exception
	 */
	public List<String[]> getCoursesForSessionOfferings(boolean isBlended, String category) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Returns a map of courses for scheduling online offerings
	 * @return courses A map of key-value pair consisting of courseId and courseTitle+courseNo+courseVersion
	 * Returns empty map if no records are found
	 * @throws Exception
	 */
	public Map<String, String> getCoursesForOnlineOfferings() throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	
	
	/**
	 * Returns a map of delivery types based on the courseId
	 * @param courseId  
	 * @return deliveryTypes A map of key-value pair consisting of deliveryId and delivery name
	 * Returns empty map if no records are found
	 * @throws Exception
	 */
	public Map<String, String> getSessionDeliveryTypes(String courseId) throws Exception
	{throw new Exception("Not Implemented.");
	}	 
	
	
	
	/**
	 * Returns the AP Fee for the courseId
	 * @param courseId  
	 * @return String the course AP price
	 * @throws Exception
	 */
	public String getPriceForCourse(String courseId) throws Exception
	{throw new Exception("Not Implemented.");
	}	 
	
	
	/**
	 * Returns a map of Purchase orders based on the orgId
	 * @param Org Id
	 * @return pos A map of key-value pair consisting of poId and PO No
	 * Returns empty map if no records are found
	 * @throws Exception
	 */
	public Map<String, String> getFacilities(String courseId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Returns a map of course categories that have atleast one course for the specified delivery typemarked as Available for AP.
	 * Course Categories on Instructor Portal equate to Focis Subject Areas in Saba. Focis Subject Area is an LOV field on Custom1 on Course table. 	 
	 * @return categories A List of String[]. Each String[] has 2 items - 0=key(A1, A2, etc) and 1=value ( A2-Preparedness, etc)
	 * Returns empty map if no records are found
	 * @throws Exception
	 */
	public List<String[]> getCourseCategories(boolean isBlended) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
		 	 	 	 	 	 	 	
	
	public Map<String, String> getPaymentDetail(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Sends out an email notifying that payment is pending for a crs
	 * @param crsId
	 * @throws Exception
	 */
	public void paymentPendingNotification(String crsId) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Approve the course record without payment. Applies only for a course record whose total amount is zero
	 * @param crsId
	 * @throws Exception
	 */
     public void approveNoCostNoPaymentCR(String crsId) throws Exception
     {throw new Exception("Not Implemented.");
     }
     
     
     /**
      * Create a Facility Fee course record. This course record does not require instructors. Also, it always assumes the Skip students flag is on and
      * there is only 1 student. 
      * @param fields Map<String, String> 
	  * The map should contain following key/values
	  * contact_id, orgId, course_id, trainingCenterName, address, city, state, zip, county, end_date.
	  * @return crsId String
	  * @throws Exception
	  */
	  public String createFacilityFeeCRS(Map<String, String> fields) throws Exception
	  {throw new Exception("Not Implemented.");
	  }	     
	  
	  
	  

	  /**
      * Get Details about a Session-based Offering. 
      * @param String offeringId	       
	  * @return Map<String, String> Offering fields as follows:
	  * offeringNo, contactId, contactUsername, orgId, courseId, courseName, deliveryType, deliveryTypeName, startDate, endDate, 
	  * facility, maxCount, studCount (No of students registered), poId, price, couponCode
	  * @throws Exception
	  */
	  public Map<String, String> getSessionOfferingDetail(String offeringId) throws Exception
	  {throw new Exception("Not Implemented.");
	  }	          
	  
	 
	/**
	 * Removes an Instructor from an offering. 
	 */ 	 	
	public void removeInstructorFromOffering(String offeringId, String personId) throws  Exception
	{throw new Exception("Not Implemented.");
	}
	
	
	/**
	 * Adds an Instructor to an offering. 
	 */ 	 	
	public void addInstructorToOffering(String offeringId, String personId) throws  Exception
	{throw new Exception("Not Implemented.");
	}
	
	     
		 
	/**
      * Create a WBT Offering. 
      * @param fields Map<String, String>: Attribute values for WBT Offering.  The map should contain following key/values
	  * contactId, orgId, courseId, availFrom, discFrom and po_id.
      * @return offeringId String
	  * @throws Exception
	  */
	  public String createOnlineOffering(Map<String, String> fields) throws Exception
	  {throw new Exception("Not Implemented.");
	  }	
	  
	  
	  
	  /**
      * Updates an Online Offering. 
      * @param fields Map<String, String>: Attribute values for Offering.  The map only uses the following key/values. 
	  * offeringId, orgId, availFrom, discFrom, poId.
      * @throws Exception
	  */
	  public void updateOnlineOffering(Map<String, String> fields) throws Exception
	  {throw new Exception("Not Implemented.");
	  }	
	  
	  
	  
	 /**
      * Get Details about an Online Offering. 
      * @param String offeringId	       
	  * @return Map<String, String> Offering fields as follows:
	  * offeringNo, contactId, contactUsername, orgId, courseId, courseName, availFrom, discFrom, poId, price 
	  * @throws Exception
	  */
	  public Map<String, String> getOnlineOfferingDetail(String offeringId) throws Exception
	  {throw new Exception("Not Implemented.");
	  }	          
	  
	  
	  
	
	/** Get Completed registrations for public offering to be displayed on Print Certificates Page
	 *
	 */
	 public List<Map<String, String>> getCompletedRegistrations(String offeringId) throws Exception
	 {throw new Exception("Not Implemented.");
	 }
	
	              	
	/**
	 * Reset the user's password. Tries to uniquely identify the user based on their Email in Saba. 
	 * If no users are found for this email, the value returned is 0. And it means no email was sent and no password was reset. 
	 * If more than one users are found for the same email, then the value returned is the number of users found. No email is sent to anyone and no password is reset. 
	 * If exactly one user was found, the value returned is 1. Also, this user's password is reset and the reset password is emailed to the user.
	 * If there was an exception when resetting the password, an exception will be thrown.      
	 * @param email (Email of the user whose password is to be reset). Case-insensitive
	 * @return int (Number of users with this email).           	 
	 * @throws Exception
	 */
	public int resetPassword(String email) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	/**
	 * Check if user must change password. This method checks for the logged-in user, hence user must first be logged in before calling this method.      
	 * @return String (Reason why password needs to be changed). If Password change is not required a blank string is returned.           	 
	 * @throws Exception
	 */
	public String needPasswordChange() throws Exception
	{throw new Exception("Not Implemented.");
 	}
	
	/**
	 * Save the new password for the logged-in user.
	 * @param oldPassword (Old Password). 
	 * @param newPassword (New Password). 
	 * @throws Exception
	 */
	public void changePassword(String oldPassword, String newPassword) throws Exception
	{throw new Exception("Not Implemented.");
  }
	
	
	/**
	 * Check if the user exists based on username provided. Returns the first name and last name of the user
	 * @param username (Can be in any case). 
	 * @return Map (Map with 2 items, first_name and last_name). 
	 * @throws Exception
	 */
    public Map<String, String> findUser(String username) throws Exception
    {throw new Exception("Not Implemented.");
    }
    
    
    /**
	 * Get the First name and Last name of a user with supplied Id
	 * @return Map (Map with 2 items, first_name and last_name). 
	 * @throws Exception
	 */
    public Map<String, String> getName(String id) throws Exception
    {throw new Exception("Not Implemented.");
    }
	
	
	 /** Mark Offering as Delivered
	  *
	  *
	  */
	public void markDelivered(String offeringId) throws Exception
	{throw new Exception("Not Implemented.");
	}	  	  	 
	 
	
	/**
	 * Sends an email to the instructor with a list of students and their certificate link
	 * @param crsId
	 * @throws Exception
	 * @throws Exception
	 */
	public void sendDeepLinkEmail(String crsId) throws Exception 
	{throw new Exception("Not Implemented.");
	}
	

	public String getInitialURL(String siteName) throws Exception
	{throw new Exception("Not Implemented.");
	}
	
	//////////////////////////////////////////////////////// SKELETON APIs
  
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
    if(record.get("disc_from")==null) return false;
    
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
      HttpEntity<String> entity = new HttpEntity<String>(headers);
      RestTemplate restTemplate = new RestTemplate();
      
      ResponseEntity<String> response = restTemplate.exchange(kSabaApiUrl + uri, HttpMethod.GET, entity, String.class);
      return response.getBody();
  
  }
}