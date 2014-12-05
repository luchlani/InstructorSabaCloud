<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<%
response.setHeader("Cache-Control","no-cache no-store"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0); 
%>
<!DOCTYPE html>
<html>
<head>
	<title>American Red Cross | Course Records</title>
	
	<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
	<meta http-equiv="EXPIRES" content="0">

	<link rel="shortcut icon" href="../images/favicon.ico">
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/jquery.dataTables_themeroller.css">
	<link rel="stylesheet" href="../css/jquery.dataTables.css">
	<link rel="stylesheet" href="../css/jquery-ui-1.10.3.custom.css" />

	<style type="text/css">
	
		.active{
		    display:block;
		    visibility: visible;
		}
		
		.hide{
		    display: none;
		    visibility: hidden;
		}
		
		table.dataTable thead th {
			border-bottom-color:black;
			border-bottom-style:solid;
			border-bottom-width:1px;
			cursor:pointer;
			font-weight:bold;
			padding:2px 3px 2px 3px;
		}
		
		table.dataTable thead th div.DataTables_sort_wrapper {
  			padding-right:10px;
  			position:relative;
		}
		table.display {
			margin: 0 auto;
			width: 100%;
			clear: both;
			border-collapse: collapse;
			table-layout: fixed;
			word-wrap:break-word; 
		}
		
		a:link {
			color: #000000;
		}
		a:visited {
			color: #6D6E70;
		}
		a:hover {
			color: #C0C0C0;
		}
		a:active {
			color: #808080;
		}
		
		.min-fifty-percent {float: left; min-width: 50%;}
		
		.ui-autocomplete { position: absolute; height: 150px; font-weight: normal; font-style: normal; font-size: 11px; overflow-y: scroll; overflow-x: hidden;}

		#overlay {
		     visibility: hidden;
		     position: absolute;
		     left: 0px;
		     top: 0px;
		     width:100%;
		     height:100%;
		     text-align:center;
		     z-index: 1000;
		     background-color: #fff;
		     opacity: 0.5;
    		 filter: alpha(opacity=50);
		}
		
		#overlay div {
		     top: 50%;		     
    		 left: 50%;
		     width:400px;
		     height:75px;
		     margin: 0 auto;
		     padding:15px;
		     text-align:center;
		     background-color: #fffffff;
     		 border:2px solid #808080;
		}
		
		.dataTables_paginate .ui-state-disabled {
     		 display:none;
		}
		
		.ui-autocomplete-loading {
		     background: white url('../images/ui-anim_basic_16x16.gif') right center no-repeat;
		}
		
		tbody td, tbody th {
		  border-color: #ffffff;
		  border-style: solid;
		  border-width: 0px;
		}
			
	</style>

	
	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/jquery.dataTables.min.js"></script>
  	<script src="../js/jquery.jeditable.mini.js"></script>
  	<script src="../js/instructor.js"></script>
  	<script src="../js/payment.js"></script>
  	<script src="../js/json2.js"></script>
  	
 	<script type="text/javascript" charset="utf-8">
 	
   	 	var oTable;
   	 	
   	 	var courseStudentsRequired = new Array();
   	 	
   	 	var County = {
		    "county" : "",
		    "getCounty" : function() { return this.county; },
		    "setCounty" : function() { $('#county').val(this.county); }
		};
		
		var Organization = {
		    "organizationID" : "",
		    "getOrganization" : function() { return this.organizationID; },
		    "setOrganization" : function() { $('#organizationID').val(this.organizationID); }
		};
		
		var CourseCategory = {
		    "category" : "",
		    "getCategory" : function() { return this.category; },
		    "setCategory" : function() { $('#courseCategory').val(this.category); }
		};
		
		var SelectedInstructorList = new Array();
		
		var SelectedInstructor = {
		    "personID" : "",
		    "userName" : "",
		    "firstName" : "",
		    "lastName" : ""
		};
		
		<core:if test="${courseRecord.feeOption=='Non-fee' || courseRecord.feeOption=='Facility-fee'}">
		var availableCourses = [
			<core:forEach  var="course" items="${courseCodeList}" varStatus="rowCount">
				<core:choose>
					<core:when test="${rowCount.last}">
				{ "id": "<core:out value="${course.key}"/>", "value": "<core:out value="${course.value}"/>", "label": "<core:out value="${course.value}"/>" }
					</core:when>
					<core:otherwise>
				{ "id": "<core:out value="${course.key}"/>", "value": "<core:out value="${course.value}"/>", "label": "<core:out value="${course.value}"/>" },
					</core:otherwise>
				</core:choose>
			</core:forEach>
		];
		
		
		var availableAquaticsCourses = [
			<core:forEach  var="course" items="${aquaticsCourseCodeList}" varStatus="rowCount">
				<core:choose>
					<core:when test="${rowCount.last}">
				{ "id": "<core:out value="${course.key}"/>", "value": "<core:out value="${course.value}"/>", "label": "<core:out value="${course.value}"/>" }
					</core:when>
					<core:otherwise>
				{ "id": "<core:out value="${course.key}"/>", "value": "<core:out value="${course.value}"/>", "label": "<core:out value="${course.value}"/>" },
					</core:otherwise>
				</core:choose>
			</core:forEach>
		];
		</core:if>
		
		var defaultCourseList = [{"id": "0", "label": "Input search text to find a course or input % to get the full list."}];
   	 	
  	 	function Delete(row_id) {
  	 	  	 var row_data = oTable.fnGetData(row_id);
		    $('#courseRecordSheet').text("Delete Instructor "+ row_data.firstName + " " +  row_data.lastName);
		    $('#deleteDialog').dialog({
				autoOpen: true,
 				width: 330,
 				modal: true,
 				buttons: {
  					"OK": function () {
  						//console.log("row_id:"+row_id);
  						$(this).dialog("close");
 						var url = '/instructor/instructor/instructorRoster.html?action=Delete&type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>';
			
						$.post(url, { id: row_id},function(data) {
							//console.log("aaData :", data.aaData);
							oTable.fnDraw();
						}, "json");
  					}, 
  					"Cancel": function() { 
   						$(this).dialog("close"); 
  					} 
				}
			});
  	 	}

  	 	function Add() {
  	 		if($('#organizationID').val() == ""){
  	 			$('#addInstructorError').text("Please select an organization before adding instructors.");
  	 			$('#addInstructorError').show();
  	 			//$('html, body').animate({scrollTop:0}, 'slow');
  	 			$("html, body").animate({scrollTop: $('#addInstructorError').offset().top }, 2000);
  	 		} else {
  	 			$('#addInstructorError').text("");
  	 			$('#addInstructorError').hide();
  	 			$('#addError').text("");
				$('#addError').hide();
				clearAddInstructor('search');
			    $('#addDialog').dialog({
					autoOpen: true,
	 				width: 650,
	 				height: 550,
	 				modal: true,
	 				buttons: {
	  					"OK": function () {
	  						if( SelectedInstructorList.length == 0){
	  							$('#addError').text("Please Selected an instructor from the search results below. ");		
		    					$('#addError').show();
		    					clearAddInstructor();
	  						}
	  							
	  							
	  						if( $('#addError').text() == ""){
		  						$(this).dialog("close");
		 						var url = '/instructor/instructor/instructorRoster.html?action=Create&type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>';
								jsonInstructorList = JSON.stringify(SelectedInstructorList);
								$.post(url, { selectedInstructors: jsonInstructorList } ,function(data) {
									//console.log("aaData :", data.aaData);
									oTable.fnDraw();
								}, "json");
							}
	  					}, 
	  					"Cancel": function() { 
	   						$(this).dialog("close"); 
	  					} 
					},
					open: function(event, ui) { $('#searchBtn').focus(); }
				});
			}
  	 	}	
  	 	
  	 	function zipCodeCheck(){
			//console.log("Zip: " + $("#zipCode").val());
			var dateRegEx = /^[0-9]+$/
			var postalcode = $('#zipCode').val();

			if (postalcode != '' && 
				((postalcode.length) == 5) && 
				(postalcode.match(dateRegEx) != null)){
				getAddressList($("#zipCode").val()).then(autoCompleteAddress);						
			} else {
				//console.log("Error encountered with postal code");
			}  	 	
  	 	}

		function autoCompleteAddress(List) {
			//console.log("Address List: " + List.addressList);
			var state;
		    for (var i = 0; i < List.addressList.length; i++) {
		    	//console.log("Address List Iterate: " + List.addressList[i].key + " " + List.addressList[i].value);
		    	switch(i)
				{
				case 0:
				  $('#city').val(List.addressList[i].value);
				  break;
				case 1:
				  state = List.addressList[i].value;
				  $('#state').val(state.toUpperCase());
				  break;
				case 2:
				  County.county = List.addressList[i].value;
				  var $select = $("#county");
		    	  $select.empty();
		          var $opt = createOption(List.addressList[i].key, List.addressList[i].value);
		          $select.append($opt);				  
				  break;
				default:
				  break;
				}
		    }
		    getCountyList(state.toUpperCase()).then(populateCountyList);
		}
		
		function getAddressList(zipCode) {
		    var url = "/instructor/list/addressList.html";
		    return $.ajax(url, {
		        data: {
		            type: "json",
		            state: "",
		            zip: zipCode
		        }
		    });
		}
		  	 	
		function createOption(val, html) {
		    return $("<option></option>").html(html).val(val);
		}
		
		function populateCountyList(List) {
			//console.log("County List: " + List.addressList);
		    var $select = $("#county");
		    $select.empty();
		    for (var i = 0; i < List.addressList.length; i++) {
		    	//console.log("County List Iterate: " + List.addressList[i]);
		        var $opt = createOption(List.addressList[i].key, List.addressList[i].value);
		        $select.append($opt);
		    }
		    County.setCounty();
		}
		
		function getCountyList(stateCode) {
		    var url = "/instructor/list/addressList.html";
		    return $.ajax(url, {
		        data: {
		            type: "json",
		            state: stateCode,
		            zip: ""
		        },
		        error: function(xhr, exception) {
					if (xhr.status === 0) {
					    console.log('Not connect. Verify Network.');
					} else if (xhr.status == 404) {
					    console.log('Requested page not found. [404]');
					} else if (xhr.status == 500) {
					    console.log('Internal Server Error [500].');
					} else if (exception === 'parsererror') {
					    console.log('Requested JSON parse failed.');
					} else if (exception === 'timeout') {
					    console.log('Time out error.');
					} else if (exception === 'abort') {
					    console.log('Ajax request aborted.');
					} else {
					    console.log('Uncaught Error.\n' + xhr.responseText);
					}
					window.location = '/instructor/authentication/login.html';
				}
		    });
		}
		
		function clearAddInstructor(action) {
		
			SelectedInstructorList = new Array();
 			$('#addDialog').find('input[name="userName"]').val('');
 			$('#addDialog').find('input[name="firstName"]').val('');
 			$('#addDialog').find('input[name="lastName"]').val('');
 			$('#addDialog').find('input[name="id"]').val('');
 			
 			if(action == 'fresh' || action == 'search')
 				$('#addDialog').find('#instructorResults' ).empty();
 				
 			if(action == 'search'){
	 			$('#addDialog').find('input[name="searchUserName"]').val('');
	 			$('#addDialog').find('input[name="searchFirstName"]').val('');
	 			$('#addDialog').find('input[name="searchLastName"]').val('');
	 		}	
		}
		
		function centerModal() {
		    var top, left;
		
		    top = Math.max($(window).height() - $('#overlay').outerHeight(), 0) / 2;
		    left = Math.max($(window).width() - $('#overlay').outerWidth(), 0) / 2;
		
		    $('#overlay').css({
		        top:top + $(window).scrollTop(), 
		        left:left + $(window).scrollLeft()
		    });
		};
 
		<core:if test="${courseRecord.feeOption=='Non-fee'}">
 		function aquaticCourses() {
		    if($('#aquaticCourseDisplay1').is(':checked')) {
		        $( '#courseName' ).autocomplete({ source: availableAquaticsCourses, autoFocus: true, scroll: true, select: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); $( '#courseID' ).val(ui.item.id);  return false;}, keyPressed : function(event, ui){ event.preventDefault(); $(this).val(ui.item.label); $( '#courseID' ).val(ui.item.id); return false;  }, minLength: 0 }).focus(function(){ $(this).autocomplete("search", ""); });
				//$('input[name="more"]').show();
		    } else {
		       	$( '#courseName' ).autocomplete({ source: availableCourses, autoFocus: true, scroll: true, select: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); $( '#courseID' ).val(ui.item.id);  return false;}, keyPressed : function(event, ui){ event.preventDefault(); $(this).val(ui.item.label); $( '#courseID' ).val(ui.item.id); return false;  }, minLength: 0 }).focus(function(){ $(this).autocomplete("search", ""); });
				//$('input[name="more"]').hide();
		    }		
 		}
 		</core:if>
 		
 		function studentCountDisplay(){
		    if($('#studentDetails1').is(':checked')) {
		        $('#studentCounts').show();
		    } else {
		        $('#studentCounts').hide();
		    }
 		}
 		
 		function setCountyValue(){
 			var courseRecordCounty = '${courseRecord.county}';
 			//console.log("Course Record County " + courseRecordCounty);
 			County.county = courseRecordCounty;
 			County.setCounty();
 		}
 		
 		function skipStudentsIndicator(courseID){
 			if (courseID != "" && $('#courseName').val() != "") {
				isRequired = isStudentsRequired(courseID);
				
				// TODO: Fix this, it is reversed
				if(isRequired == 'true')
					$('#skipStudents').show();
				else
					$('#skipStudents').hide();		
 			}
 		}
 		
 		function isStudentsRequired(courseID){
 			for (var key in courseStudentsRequired){
				if(courseStudentsRequired[key].id == courseID){
					return courseStudentsRequired[key].studentRequired
				}
			}
 			
 			return 'true';
 		}
 		
 		function setCourseCodeList(list){
			$.each( list.courseStudentsRequired, function( i, item ) {
       			courseStudentsRequired[i] = item;
    		});
			
			skipStudentsIndicator($('#courseID').val());
 		}
 		
 		function getCourseCodeList(courseCategory, courseName) {
		    var url = "/instructor/coursecategory/courseCategoryList.html";
		    return $.ajax(url, {
		        data: {
		            type: "json",
		            courseCategory: courseCategory,
		            courseName: courseName,
		            feeOption: '<core:out value="${courseRecord.feeOption}"/>'
		        },
		        error: function(xhr, exception) {
					if (xhr.status === 0) {
					    console.log('Not connect. Verify Network.');
					} else if (xhr.status == 404) {
					    console.log('Requested page not found. [404]');
					} else if (xhr.status == 500) {
					    console.log('Internal Server Error [500].');
					} else if (exception === 'parsererror') {
					    console.log('Requested JSON parse failed.');
					} else if (exception === 'timeout') {
					    console.log('Time out error.');
					} else if (exception === 'abort') {
					    console.log('Ajax request aborted.');
					} else {
					    console.log('Uncaught Error.\n' + xhr.responseText);
					}
					window.location = '/instructor/authentication/login.html';
				}
		    });
		}
	 		
  		$(document).ready(function() {
  		
  			$( document ).tooltip();
  			
  			$("#ethnicGroup").tooltip({
		      items: "[title]",
		      content: function() {
		        if ( $( this ).is( "[title]" ) ) {
		          return " Racial and ethnic minority populations are defined as:<br />&nbsp;&nbsp;&nbsp;&#8226;  Asian American<br />&nbsp;&nbsp;&nbsp;&#8226;  Black or African American<br />&nbsp;&nbsp;&nbsp;&#8226;  Hispanic or Latino<br />&nbsp;&nbsp;&nbsp;&#8226;  Native Hawaiian and Other Pacific Islander<br />&nbsp;&nbsp;&nbsp;&#8226;  American Indian and Alaska Native";
		        }
		      }
		    });
		    
  			<core:if test="${courseRecord.feeOption=='Non-fee'}">
		    aquaticCourses();
		    </core:if>
		    
		    <core:if test="${courseRecord.feeOption=='Facility-fee'}">
		    $( '#courseName' ).autocomplete({ source: availableCourses, autoFocus: true, scroll: true, select: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); $( '#courseID' ).val(ui.item.id);  return false;}, keyPressed : function(event, ui){ event.preventDefault(); $(this).val(ui.item.label); $( '#courseID' ).val(ui.item.id); return false;  }, minLength: 0 }).focus(function(){ $(this).autocomplete("search", ""); });
            </core:if>
		    
		    <core:if test="${courseRecord.feeOption=='Fee' && precreate =='Yes'}" >
		    paymentTypeDisplay($('#payment\\.paymentTypeID').val());
		  	$('#payment\\.paymentTypeID').change(function() { paymentTypeDisplay($('#payment\\.paymentTypeID').val()); });
		  	</core:if>
		  	
		  	<core:if test="${courseRecord.feeOption=='Fee' && precreate =='Yes'}" >
		  	if ($('#organizationID').val() != "") {
		  		getPurchaseOrderList($('#organizationID').val(), "0.0").then(updatePurchaseOrders);
		  	}
			</core:if>
		    
		    <core:if test="${courseRecord.feeOption=='Fee' && courseRecord.action=='Edit'}">
		    	getCountyList($("#state").val()).then(populateCountyList).then(setCountyValue);
		    </core:if>
		    
		    /* commented this out because the keyup trigger is defined elsewhere */
 			//$.fn.keyPressed = function(fn) {  
			//    return this.each(function() {  
			//        $(this).bind('keyPressed', fn);
			//        $(this).keyup(function(e){
			//              $(this).trigger("keyPressed");
			//        })
			//    });  
			// }; 

					    			
  			/*
  		    $('#targetPopulation').click(function() {
  		    	if( $('#targetPopulationQuestions').is(":visible")){
  		    		$('#targetPopulationImage').removeClass('ui-icon-circle-triangle-n');
  		    		$('#targetPopulationImage').addClass('ui-icon-circle-triangle-s');
  		    		$('#targetPopulationQuestions').hide();
  		    	} else {
  		    		$('#targetPopulationImage').removeClass('ui-icon-circle-triangle-s');
		        	$('#targetPopulationImage').addClass('ui-icon-circle-triangle-n');
		        	$('#targetPopulationQuestions').show();
		        }
		        
		    });
		    
		    $('#military').click(function() {
  		    	if( $('#militaryQuestions').is(":visible")){
  		    		$('#militaryImage').removeClass('ui-icon-circle-triangle-n');
  		    		$('#militaryImage').addClass('ui-icon-circle-triangle-s');
  		    		$('#militaryQuestions').hide();
  		    	} else {
  		    		$('#militaryImage').removeClass('ui-icon-circle-triangle-s');
		        	$('#militaryImage').addClass('ui-icon-circle-triangle-n');
		        	$('#militaryQuestions').show();
		        }
		        
		    });
 			*/
 			
 			$('#addDialog').hide();
 			$('#warningDialog').hide();
 			$('#POWarningDialog').hide();
 			

 			//$('#targetPopulationQuestions').hide();
 			//$('#militaryQuestions').hide();
 			
 			$('#addInstructorIndicators').hide();
 			
 			//$("#endDate").datepicker({ beforeShowDay: $.datepicker.noWeekends });
 			
 			<core:if test="${courseRecord.feeOption=='Non-fee' || (courseRecord.feeOption=='Fee' && precreate=='No') || courseRecord.feeOption=='Facility-fee'}">
 			$("#endDate").datepicker({maxDate: '0'});
 			</core:if>
 			
 			<core:if test="${courseRecord.feeOption=='Fee' && precreate=='Yes'}">
 			$("#endDate").datepicker();
 			</core:if>
 			
 			$("#showCalendar").click(function() {
 				 $('#endDate').datepicker('show');
 			});
 			
 			$('input[name="submit"]').click(function() { 
 				centerModal();
				el = document.getElementById("overlay");
				el.style.visibility = (el.style.visibility == "visible") ? "hidden" : "visible";
 			});
 			
 			$("#state").change(function() {
 				getCountyList($("#state").val()).then(populateCountyList);
 			});
 			
 			$("#zipCode").focusout(function() { zipCodeCheck(); });
 			
 			$.fn.pressEnter = function(fn) {  
			    return this.each(function() {  
			        $(this).bind('enterPress', fn);
			        $(this).keyup(function(e){
			            if(e.keyCode == 13)
			            {
			              $(this).trigger("enterPress");
			            }
			        })
			    });  
			 }; 

			$('#zipCode').pressEnter(function(){ zipCodeCheck(); });
 			
 			<core:if test="${courseRecord.feeOption=='Non-fee' || (courseRecord.feeOption=='Fee' && precreate=='No')}">
 			$("#endDate").change(function() {
 				var dateRegEx = /^(0[1-9]|1[012]|[1-9])[- /.](0[1-9]|[12][0-9]|3[01]|[1-9])[- /.](19|20)\d\d$/
 			    var now = new Date();
 				var endDate = new Date($("#endDate").val());
 				
 				$('#dateError').text("");
 				$('#dateError').hide();
 
 				if($("#endDate").val().match(dateRegEx) == null){
 					$('#dateError').text("Course Ending Date must have a format of MM/DD/YYYY");
 					$('#dateError').show();
 				} else {
	 				//endDate.setDate(endDate.getDate()+1);
	 				if(!(endDate <= now)){
	 					$('#dateError').text("Course Ending Date must be earlier than today or today's date");	
	 					$('#dateError').show();
	 				}
	 			}
	 			
	 			
 			});
 			</core:if>
 			
			<core:if test="${courseRecord.feeOption=='Fee'}">
				studentCountDisplay();
				
				if ($('#courseName').val() == "") {
					// hide skip students by default
					$('#skipStudents').hide();
				} else {
					getCourseCodeList($('#courseCategory').val(), $('#courseName').val()).then(setCourseCodeList);
				}

				$('#courseName').autocomplete({
				      source: function( request, response ) {
				        $.ajax({
				          url: "/instructor/coursecategory/courseCategoryList.html",
				          dataType: "json",
				          data: {
				        	type: "json",
					        courseCategory: $('#courseCategory').val(),
					        courseName: request.term,
					        feeOption: '<core:out value="${courseRecord.feeOption}"/>'
				          },
				          success: function( data ) {
				        	setCourseCodeList(data);
				        	if (!data.availableCourses.length)
				        		$('#courseResultText').text('No records found');
				        	else
				        		$('#courseResultText').text('');
				            response( $.map( data.availableCourses, function( item ) {
				              return {
				                label: item.label,
				                value: item.label,
				                id: item.id
				              }
				            }));
				          }
				        });
				      },
				      minLength: 1,
				      select: function( event, ui ) {
				    	$('#courseID').val(ui.item.id);
				    	<core:if test="${courseRecord.feeOption=='Fee'}">
						if (ui.item.value == "")
							$('#skipStudents').hide();
						else
							skipStudentsIndicator($('#courseID').val());
						</core:if>
				      },
				      open: function() {
				        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
				      },
				      close: function() {
				        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
				      }
				    });
				
			</core:if>
			
			// test non fee with dynamic autocomplete
			<core:if test="${courseRecord.feeOption=='Non-fee'}">
            $('#courseName').autocomplete({
                source: function( request, response ) {
                  $.ajax({
                    url: "/instructor/coursecategory/nonFeeCourseCategoryList.html",
                    dataType: "json",
                    data: {
                      type: "json",
                      courseCategory: $('#courseCategory').val(),
                      courseName: request.term,
                      feeOption: '<core:out value="${courseRecord.feeOption}"/>'
                    },
                    success: function( data ) {
                      //setCourseCodeList(data);
                      if (!data.availableCourses.length)
                          $('#courseResultText').text('No records found');
                      else
                          $('#courseResultText').text('');
                      response( $.map( data.availableCourses, function( item ) {
                        return {
                          label: item.label,
                          value: item.label,
                          id: item.id
                        }
                      }));
                    }
                  });
                },
                minLength: 0,
                select: function( event, ui ) {
                  $('#courseID').val(ui.item.id);
                  <core:if test="${courseRecord.feeOption=='Fee'}">
                  if (ui.item.value == "")
                      $('#skipStudents').hide();
                  else
                      skipStudentsIndicator($('#courseID').val());
                  </core:if>
                },
                open: function() {
                  $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
                },
                close: function() {
                  $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
                }
              });
			</core:if>
			
			// disable Enter key on course name autocomplete field (fee and non-fee)
			$("#courseName").keypress(function(event){
				var keycode = (event.keyCode ? event.keyCode : event.which);
				if (keycode == '13') {
					event.preventDefault();
					event.stopPropagation();    
				}
			});
			
			<core:if test="${courseRecord.feeOption=='Fee' && precreate=='No'}">
				$( '#courseName' ).focusout(function() { skipStudentsIndicator($( '#courseID' ).val()) });
	    	</core:if>
	    					 	
  			$('#studentDetails1').click(function() {
  				studentCountDisplay();
			});
			
			$('#aquaticCourseDisplay1').click(function() {
				aquaticCourses();
			});
			 
			$('input[name="more"]').click(function() {
				//console.log("Create More Course Record Sheets");
				$('#action').val('More');
			});
			
			$.extend( $.fn.dataTable.defaults, {
		        "bJQueryUI": true,
		        "bFilter": false,
		        "bPaginate": false, 
		        "bLengthChange": false,
        		"bSort": false,
				"bAutoWidth": false,
				"aoColumns": [
				               { "mData": "id","sName": "id", "sTitle": "ID","sWidth": null, "bVisible": false }, 
                               { "mData": "userName","sName": "userName", "sTitle": "User Name","sWidth":  null, "bVisible": false }, 
                               { "mData": "firstName", "sName": "firstName", "sTitle": "First Name","sWidth":  null },  
                               { "mData": "lastName","sName": "lastName", "sTitle": "Last Name","sWidth":  null },  
                               { "mData": "email","sName": "email", "sTitle": "Email","sWidth":  null, "bVisible": false }, 
                               { "mData": "phoneNumber","sName": "phoneNumber", "Phone": "ID","sWidth":  null, "bVisible": false },
                               { "mData": "postalCode","sName": "postalCode", "sTitle": "Postal Code","sWidth":  null, "bVisible": false },
                               { "mData": "action","sName": "action", "sTitle": "Action","sWidth": "8%" } 
                           ]                           
    		} );

			<core:if test="${courseRecord.feeOption=='Fee'}">
			oTable = $('#searchResults').dataTable({ 
				"oLanguage": {
					"sEmptyTable": "Add instructor(s) to the course record.",
					"sInfo": "Showing _START_ to _END_  of _END_ "
				},
			    "bProcessing": true,
        		"bServerSide": true,
        		"sAjaxSource": '/instructor/instructor/instructorRoster.html?type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>&organizationID='
			});
			</core:if>
			<core:if test="${courseRecord.feeOption=='Non-fee'}">
			oTable = $('#searchResults').dataTable({ 
				"oLanguage": {
					"sEmptyTable": "Add an instructor to the course record entry.",
					"sInfo": "Showing _START_ to _END_  of _END_ "
				},
			    "bProcessing": true,
        		"bServerSide": true,
        		"sAjaxSource": '/instructor/instructor/instructorRoster.html?type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>&organizationID='
			});
			</core:if>
			
			$('#courseCategory').focus(function() {
				CourseCategory.category = $( this ).val();
			}).change(function() {
				$('#courseName').val("");
				$('#courseID').val("");
				<core:if test="${courseRecord.feeOption=='Fee'}">
				if (CourseCategory.getCategory() != $( this ).val()){
					setCourseCodeList([]);
				}
				</core:if>
			} );
			
			$('#organizationID').focus(function() {
        		Organization.organizationID = $( this ).val();
    		}).change(function() {
    			<core:if test="${courseRecord.feeOption=='Fee' && precreate =='Yes'}" >
    			getPurchaseOrderList($(this).val(), "0.0").then(updatePurchaseOrders);
    			</core:if>
    			//console.log("Current OrgID " + $( this ).val() + " Old Value: "+ Organization.getOrganization());
    			//console.log("Instructor Table Size: " + oTable.fnGetData().length);
    			<core:if test="${courseRecord.feeOption!='Facility-fee'}">
    			if (Organization.getOrganization() != $( this ).val() && oTable.fnGetData().length > 0) {
					//Modal window change confirmation
					//console.log("Warning Organization changed remove instrutor list");
				    $('#warningDialog').dialog({
						autoOpen: true,
		 				width: 330,
		 				modal: true,
		 				buttons: {
		  					"Yes": function () {
								$(this).dialog("close");
		 						var url = '/instructor/instructor/instructorRoster.html?action=Delete&type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>';
								$.post(url, { id: -1},function(data) {
									//console.log("aaData :", data.aaData);
									oTable.fnDraw();
								}, "json");
		  					}, 
		  					"No": function() { 
		  						$( '#organizationID' ).val(Organization.getOrganization());
		   						$(this).dialog("close"); 
		  					} 
						}
					});
				}
    			</core:if>
 			});
			
			//$('.course-title').tooltip({ position: { my: "right+15 center-50", at: "right center" } });
			
		} );
	</script>
	
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<!--[if lt IE 9]>
		<script>
		document.createElement('header');
		document.createElement('nav');
		document.createElement('section');
		document.createElement('article');
		document.createElement('aside');
		document.createElement('footer');
		document.createElement('hgroup');
		</script>
	<![endif]-->
	
<!-- Lucky: Google Analytics Code Start -->	
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-869735-35', 'redcross.org');
  ga('send', 'pageview');
</script>	
<!-- Lucky: Google Analytics Code End -->
	
</head>
<body>
<div id="hdr" class="cert-hdr">
	<header class="page-header" role="banner">
		<a href="//www.redcross.org" class="branding" target="_blank">
			<img class="logo" border=0 src="../images/redcross-logo.png" alt="American Red Cross">
		</a>
		<div style="position:relative; top:25px; float:right; right:25px;"> 
			<p class="light-grey-out"> Welcome <span class="grey-out"><core:out value="${username}" /></span>; Click here to <a href="<core:url value="/authentication/logout.html"/>" >logout.</a></p>
		</div>
	</header>
</div>
<div class="left-rail content-wrap default-layout">
	<div class="content student-page">
		<aside class="sidebar" role="complementary">
		    <nav class="sidebar-inner">
		        <ul class="nav-list">
		         <core:if test="${courseRecord.feeOption=='Fee'}" >
		         	<li class="nav-item">
		                <a class="nav-link" href="<core:url value="/search/courseRecordSearch.html"/>">
		                <span>Search existing course records</span>
		                </a>
		            </li>
		            <li class="nav-item" title="Selecting this link will open a new tab. You will be required to login to the Learning Center.">
		                <a class="nav-link" onclick="SearchClassParticipants()" href="#">
		                <span>Search class participants</span>
		                </a>
		            </li>
		            <core:if test="${precreate=='No'}" >
		          	<li class="nav-item current">
		                <a class="nav-link" href="<core:url value="/course/courseRecordSheet.html"/>">
		                <span>Create new course record</span>
		                </a>
		            </li>
		            <li class="nav-item">
		                <a class="nav-link" href="<core:url value="/course/preCreateCourseRecordSheet.html"/>">
		                <span>Schedule class and set up payment</span>
		                </a>
		            </li>
		            </core:if>
		            <core:if test="${precreate=='Yes'}" >
		          	<li class="nav-item">
		                <a class="nav-link" href="<core:url value="/course/courseRecordSheet.html"/>">
		                <span>Create new course record</span>
		                </a>
		            </li>
		            <li class="nav-item current">
		                <a class="nav-link" href="<core:url value="/course/preCreateCourseRecordSheet.html"/>">
		                <span>Schedule class and set up payment</span>
		                </a>
		            </li>
		            </core:if>
		         </core:if>
		         <core:if test="${courseRecord.feeOption=='Non-fee'}" >
		          	<li class="nav-item current">
		                <a class="nav-link" href="<core:url value="/course/courseRecordSheet.html"/>">
		                <span>Create new course record</span>
		                </a>
		            </li>
		            <li class="nav-item">
		                <a class="nav-link" href="<core:url value="/search/courseRecordSearch.html"/>">
		                <span>Search existing course records</span>
		                </a>
		            </li>
		         </core:if>
		         <core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                    <li class="nav-item current">
                        <a class="nav-link" href="<core:url value="/course/courseRecordSheet.html"/>">
                        <span>Create new course record: LTS Facility Fee</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<core:url value="/search/courseRecordSearch.html"/>">
                        <span>Search existing course records: LTS Facility Fee</span>
                        </a>
                    </li>
                 </core:if>
		        </ul>
		        <div class="sidebar-module-wrap"></div>
		    </nav>
		</aside>
		
		<form:form method="POST" name="courseRecord" commandName="courseRecord" >
		
		<section class="primary-content">
			<header class="primary-header">
				<core:if test="${courseRecord.feeOption=='Non-fee'}" >
				<h1>Create new course record: Courses without student fees</h1>
				</core:if>
				<core:if test="${courseRecord.feeOption=='Fee'}" >
				
				<core:if test="${courseRecord.action=='Create'}" >
				<h1>New Course Record</h1>
				</core:if>
				<core:if test="${courseRecord.action=='Edit'}" >
				<h1>Edit Course Record</h1>
				</core:if>
				
				</core:if>
				<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                <h1>Create new course record: LTS Facility Fee</h1>
                </core:if>
			</header>
			
			<!--  BREADCRUMBS -->
			<core:if test="${courseRecord.feeOption=='Fee'}" >
			<div class="field-wrap">
				<core:if test="${precreate=='Yes'}" >
				<u>Course and Payment</u> < Confirmation
				</core:if>
				<core:if test="${precreate=='No'}" >
				<u>Course</u> < Students < Review < Payment < Confirmation
				</core:if>
			</div>
			</core:if>
			<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
            <div class="field-wrap">
                <u>Course</u> < Payment < Confirmation
            </div>
            </core:if>

			<core:if test="${courseRecord.feeOption=='Non-fee'}" >
			<div style="color: #6D6E70;">
				<p>Thank you for taking the time to complete your course record.  Your timely and complete course record reporting enables the American Red Cross to:</p>
				<div style="position: relative;left: 40px;">
					<ul class="bullet-list">
						<li>Measure the effectiveness of our training</li>
						<li>Conduct informed resource planning</li>
						<li>Seek grants and corporate sponsorships to help our partners increase the reach of our training to target populations</li>
					</ul> 
				</div>
			</div>
			</core:if>
			<div class="field-wrap">
				<form:hidden path="sabaMessage" />
				<form:errors path="sabaMessage" cssClass="msgError" />
			</div>
			</section>
			<section class="primary-content ">
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>
							<div class="field-wrap min-fifty-percent" style="margin-right: 35px;">
								<label for="" class="grey-out no-asterisk" style="width: auto; padding-right: 20px;">Contact:</label>
								<div class="field light-grey-out">
									<core:choose>
										<core:when test="${courseRecord.sheetNumber == null}">
											<span><core:out value="${username}" /></span>
										</core:when>
										<core:otherwise>
											<span><core:out value="${courseRecord.contactUserName}" /></span>
										</core:otherwise>
									</core:choose>
								</div>
							</div>
							<core:if test="${courseRecord.sheetNumber != null}">
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk" style="width: auto; padding-right: 20px;">Course Record Sheet No.:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.sheetNumber}" /></span>
								</div>
							</div>
							</core:if>
						</fieldset>
					</div>
				</div>
				<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                <header class="form-header">
                    <h1>Program Information</h1>
                </header>
                </core:if>
                <core:if test="${courseRecord.feeOption!='Facility-fee'}" >
				<header class="form-header">
					<h1>Course Information <!-- <a  href="javascript:void(0)" title="Select the organization associated with this course record. You are only able to select organizations that are associated with your instructor record. If the desired organization does not appear in the drop down menu, contact your local unit. The end date entered will appear on the student certificates. If the course you are entering does not require student names you will see an option &quot;skip student details&quot;. If you want to skip entering student names, you can check the box."><span class="ui-icon ui-icon-info" style="display:inline-block"></span></a> --></h1>
				</header>
				</core:if>
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>
							<div class="field-wrap">
								<span id="addInstructorError" class="msgError" style="display: none;"></span>
							</div>
							
							<div class="field-wrap">
								<c:choose>
								    <c:when test="${courseRecord.feeOption=='Fee' && ((courseRecord.action == 'Create') || (courseRecord.action == 'Edit' && canChangeOrganization))}">
								    	<label for="organizationName" title="Select the organization for whom the course was offered. Organizations which you are associated with will appear in the list.">Organization:</label>
								    </c:when>
								    <c:otherwise>
								    	<label for="organizationName">Organization:</label>
								    </c:otherwise>
								</c:choose>
								<core:if test="${(courseRecord.action == 'Create') || (courseRecord.action == 'Edit' && canChangeOrganization) }">
								<div class="field">
									<form:select  items="${organizationList}" path="organizationID" itemValue="key" itemLabel="value" /> 
								</div>
								</core:if>
								<core:if test="${canChangeOrganization!=null && !canChangeOrganization}">
								<div class="field">
									<span><core:out value="${courseRecord.organizationName}" /></span>
									<form:hidden path="organizationID" />
								</div>
 								</core:if>
							</div>
							<div class="field-wrap">
								<form:errors path="organizationID" cssClass="msgError" />
							</div>
							<core:if test="${courseRecord.feeOption=='Non-fee' || courseRecord.feeOption=='Fee'}" >
							<div class="field-wrap">
								<label class="optional" for="courseCategory" title="Use the drop down to narrow the category of the course.">Course Category: <span class="optional-text">(optional)</span></label>
								<div class="field">
									<form:select items="${courseCategoryList}" path="courseCategory" itemValue="key" itemLabel="value" />
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="courseCategory" cssClass="msgError" />
							</div>
							</core:if>

							<div class="field-wrap">
								<core:if test="${courseRecord.feeOption=='Fee'}" >
									<label for="course" title="Input text to get a list of matching courses, or input % to retrieve the full list.">Course Name:</label>
								</core:if>
								<core:if test="${courseRecord.feeOption=='Non-fee'}" >
									<label for="course">Course:</label>
								</core:if>
								<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                                    <label for="course">LTS Program Category:</label>
                                </core:if>
								<div class="field">
									<!-- <form:select items="${courseCodeList}" path="courseID" itemValue="key" itemLabel="value" /> -->
									<form:input  path="courseName" cssClass="input-large"  />								
									<form:hidden  path="courseID"  />  
									<core:if test="${courseRecord.feeOption=='Non-fee'}" >
									<!--<form:checkbox  path="aquaticCourseDisplay" /><label class="no-asterisk" for="aquaticCourseDisplay">Aquatics-only courses</label>-->
									</core:if>
								</div>
								<div id="courseResultText" style="margin-left:10px; float:left; font-weight:bold; color:#AD5E5C;"></div>
								<!--
								<a id="showCourseSearch" title="Click to configure course filter" href="javascript:void(0)"><span class="ui-icon ui-icon-gear" style="display:inline-block"></a>
								-->
							</div>
							<div class="field-wrap">
								<form:errors path="courseID" cssClass="msgError" />
							</div>

                            <core:if test="${courseRecord.feeOption=='Facility-fee'}">
                            <form:hidden  path="endDate" />  
                            </core:if>
                            <core:if test="${courseRecord.feeOption!='Facility-fee'}">
							<div class="field-wrap">
								<core:if test="${courseRecord.feeOption=='Fee' && precreate=='No'}" >
									<label for="endDate" title="Enter the end date of the course. May not  be a future date.">Course Ending Date:</label>
								</core:if>
								<core:if test="${courseRecord.feeOption=='Fee' && precreate=='Yes'}" >
									<label for="endDate" title="Enter the end date of the course. Please do not create course records for classes more than three weeks in the future.">Course Ending Date:</label>
								</core:if>
								<core:if test="${courseRecord.feeOption=='Non-fee'}" >
									<label for="endDate">Course Ending Date:</label>
								</core:if>
								<div class="field">
									<form:input path="endDate" cssClass="input-medium" /> 
								</div>
								<a id="showCalendar" title="Click to view calendar" href="javascript:void(0)"><span class="ui-icon ui-icon-calendar" style="display:inline-block"></span></a>
							</div>
							<div class="field-wrap">
								<form:errors path="endDate" cssClass="msgError" />
								<span id="dateError" class="msgError" style="display: none;"></span>
							</div>
							</core:if>
							
							<core:if test="${courseRecord.feeOption=='Fee' && precreate=='No'}" >
							<div id="skipStudents">
							<div class="field-wrap">
								<label for="studentDetails" class="optional">Skip Student Details: <span class="optional-text">(optional)</span></label>
								<div class="field">
									<form:checkbox path="studentDetails" />
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="studentDetails" cssClass="msgError" />
							</div>	
							</div>
							</core:if>

							<core:if test="${precreate=='No' && courseRecord.feeOption!='Facility-fee'}">
							
							<div class="field-wrap">
								<core:if test="${courseRecord.feeOption=='Fee'}" >
									<label for="totalStudents" title="Enter the number of students trained.">Total Students:</label>
								</core:if>
								<core:if test="${courseRecord.feeOption=='Non-fee'}" >
									<label for="totalStudents">Total Students:</label>
								</core:if>
								<div class="field">
									<form:input path="totalStudents" cssClass="input-tiny" />
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="totalStudents" cssClass="msgError" />
							</div>
							
							</core:if>
							
							<core:if test="${courseRecord.feeOption!='Facility-fee'}">
							<div id="studentCounts" >
								<div class="field-wrap">
									<label for="totalSuccessful">Total Successful:</label>
									<div class="field">
										<form:input path="totalSuccessful" cssClass="input-tiny" />
									</div>
								</div>
								<div class="field-wrap">
									<form:errors path="totalSuccessful" cssClass="msgError" />
								</div>	  
								<div class="field-wrap">
									<label for="totalUnsuccessful">Total Unsuccessful:</label>
									<div class="field">
										<form:input path="totalUnsuccessful" cssClass="input-tiny" />
									</div>
								</div>
								<div class="field-wrap">
									<form:errors path="totalUnsuccessful" cssClass="msgError"/>
								</div>	
								<div class="field-wrap">
									<label for="totalNotEvaluated">Total Not Evaluated:</label>
									<div class="field">
										<form:input path="totalNotEvaluated" cssClass="input-tiny" />
									</div>
								</div>
								<div class="field-wrap">
									<form:errors path="totalNotEvaluated"  cssClass="msgError"/>
								</div>	
							</div>
							</core:if>
							
						</fieldset>
					</div>
				</div>
		</section>
		<section class="primary-content">
		        <core:if test="${courseRecord.feeOption=='Facility-fee'}" >
				<header class="form-header" title="Enter the contact person's name and mailing address for the promotional kit. Please do not use a PO box.">
					<h1>Shipping Address for Promotional Kit</h1>
				</header>
				</core:if>
				<core:if test="${courseRecord.feeOption=='Non-fee' || courseRecord.feeOption=='Fee'}" >
                <header class="form-header" title="Enter the information for the facility where the course was taught.">
                    <h1>Training Site Address<!-- <a  href="javascript:void(0)" title="This is the address of the facility where the course was conducted."><span class="ui-icon ui-icon-info" style="display:inline-block"></span></a> --></h1>
                </header>
                </core:if>
				<div class="simple-form tabular-form">
					<div class="fieldset-group" style="float: left; width: 50%">
						<fieldset>
							<div class="field-wrap">
							    <core:if test="${courseRecord.feeOption=='Facility-fee'}" >
								<label for="trainingCenterName" style="width: 160px;">Name (to the attention of):</label>
								</core:if>
								<core:if test="${courseRecord.feeOption=='Non-fee' || courseRecord.feeOption=='Fee'}" >
                                <label for="trainingCenterName" style="width: 160px;">Training Site Name:</label>
                                </core:if>
								<div class="field">
									<form:input path="trainingCenterName" cssClass="input-medium-large" />
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="trainingCenterName" cssClass="msgError" />
							</div>
							<div class="field-wrap">
								<label for="streetAddress" style="width: 160px;">Street Address:</label>
								<div class="field">
									<form:input path="streetAddress" cssClass="input-medium-large" />
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="streetAddress" cssClass="msgError" />
							</div>
							<div class="field-wrap">
								<label for="zipCode" style="width: 160px;">Postal Code:</label>
								<div class="field">
									<form:input path="zipCode" cssClass="input-tiny" />
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="zipCode" cssClass="msgError" />
							</div>
						</fieldset>
					</div>
					<div class="fieldset-group" style="float: left; width: 50%">
						<fieldset>
							<div class="field-wrap">
								<label for="city" style="width: 110px;">City:</label>
								<div class="field">
									<form:input path="city" cssClass="input-medium-large" />
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="city" cssClass="msgError" />
							</div>
							<div class="field-wrap">
								<label for="state" style="width: 110px;">State:</label>
								<div class="field">				
									<form:select  items="${stateList}" path="state" itemValue="key" itemLabel="value" cssClass="input-medium-large" /> 
								</div>
							</div>
							<div class="field-wrap">
								<form:errors path="state" cssClass="msgError" />
							</div>
							<div class="field-wrap">
								<label for="county" class="optional" style="width: 110px;">County: <span class="optional-text">(optional)</span></label>
								<div class="field">
									<form:select  items="${countyList}" path="county" cssClass="input-medium-large" />
								</div>
							</div>
						</fieldset>
					</div>
				</div>
		</section>							
		<core:if test="${courseRecord.feeOption=='Non-fee'}" >
		
		<section class="primary-content">
			<header class="form-header">
				<h1>Other Information <!-- <a  href="javascript:void(0)" title="Indicate which options below apply to you."><span class="ui-icon ui-icon-info" style="display:inline-block"></span></a> --></h1>
			</header>
			<div class="simple-form tabular-form">
				<div class="fieldset-group">
					<fieldset>
						<div id="targetPopulation" class="field-wrap">
							 <label style="font-weight: bold;text-decoration:underline;width: 300px;" class="optional"><!-- <span id="targetPopulationImage" class="ui-icon ui-icon-circle-triangle-s" style="display:inline-block"></span>&nbsp; --> Target Populations</label>
							<% if(isBrowserIE8OrLess(request.getHeader("User-Agent"))) { %>
							<table><tr><td>
								<p />
								<p style="color: #6D6E70;"> Please select as many of the following boxes that can be applied to the students in your course. <span class="optional-text">(Optional)</span> </p>
							</td></tr></table>							
							<% } else { %>
							<div class="field">
								<p />
								<p style="color: #6D6E70;"> Please select as many of the following boxes that can be applied to the students in your course. <span class="optional-text">(Optional)</span> </p>
							</div>
							<% } %>
						</div>
						<div id="targetPopulationQuestions" >
							<div title="12 and under" class="field-wrap">
								
								<div class="field">
									<form:checkbox path="statistics.youth" cssClass="youth" />
								</div>
								<label for="youth" class="optional"><b>Children </b></label>
								
							</div>
							<div title="13 to 61 years old" class="field-wrap">
								
								<div class="field">
									<form:checkbox path="statistics.adults" cssClass="adults" />
								</div>
								<label for="adults" class="optional"><b>Adults </b></label>
								
							</div>
							<div title="62 years and older" class="field-wrap">
								
								<div class="field">
									<form:checkbox path="statistics.seniors" cssClass="seniors" />
								</div>
								<label for="seniors" class="optional"><b>Seniors </b></label>
								
							</div>
							<div title="Earning $45,000 or less per household annually" class="field-wrap">
								
								<div class="field">
									<form:checkbox path="statistics.lowIncome" cssClass="lowIncome" />
								</div>
								<label for="lowIncome" class="optional"><b>Low Income </b></label>
								
							</div>
							<div title="Open country and settlements with fewer than 2,500 residents" class="field-wrap">
								<div class="field">
									<form:checkbox path="statistics.rural" cssClass="rural" />
								</div>
								<label for="rural" class="optional"><b>Rural </b></label>
								
							</div>
							<div id="ethnicGroup" title="" class="field-wrap">
								<div class="field">
									<form:checkbox path="statistics.minority" cssClass="minority" />
								</div>
								<label for="minority"  class="optional"><b>Minority </b></label>
							</div>	
							<div title=" Includes active duty, veterans and their families" class="field-wrap">
								<div class="field">
									<form:checkbox path="statistics.military" cssClass="military" />
								</div>
								<label for="military"  class="optional"><b>Military </b></label>
							</div>						
							<div title="Individuals who have physical, sensory, mental health, and cognitive and/or intellectual disabilities affecting their ability to function independently without assistance" class="field-wrap">
								<div class="field">
									<form:checkbox path="statistics.functionalNeeds" cssClass="functionalNeeds" />
								</div>
								<label for="functionalNeeds"  class="optional"><b>Functional Needs </b></label>
							</div>
						</div>
					</fielset>
				</div>
			</div>
			<div class="simple-form tabular-form">
				<div class="fieldset-group">
					<fieldset>
						<div id="targetPopulation" class="field-wrap">
							 <label style="font-weight: bold;text-decoration:underline;width: 300px;" class="optional"><!-- <span id="targetPopulationImage" class="ui-icon ui-icon-circle-triangle-s" style="display:inline-block"></span>&nbsp; --> Training Site</label>
							<% if(isBrowserIE8OrLess(request.getHeader("User-Agent"))) { %>
							<table><tr><td>
								<p />
								<p style="color: #6D6E70;"> Please select as many of the following boxes that can be applied to your training center.</p>
							</td></tr></table>							
							<% } else { %>							
							<div class="field">
								<p />
								<p style="color: #6D6E70;"> Please select as many of the following boxes that can be applied to your training center.</p>
							</div>
							<% } %>
						</div>
						<div id="targetPopulationQuestions" >
							<div title=" To submit course records for Learn-to-Swim, facilities must be registered in the annual Learn-to-Swim Program. To get started, go to redcross.org/LTSenroll" >
								<div class="field-wrap">
									<label style="margin: 0;" for="learnToSwim" class="optional"><b><a href="http://redcross.org/LTSenroll" target="_blank">Learn-to-Swim Participating Facility: </a></b></label>
								</div>	
								<div class="field-wrap">
									<div class="field">
										<table><tr><td><form:radiobutton path="statistics.learnToSwim" value="Yes" /> Yes</td><td><form:radiobutton path="statistics.learnToSwim" value="No" /> No</td><td><form:radiobutton path="statistics.learnToSwim" value="Unknown" /> Unknown</td></tr></table>
									</div>
								</div>
							</div>
							<div title=" Ready Rating is a FREE service that helps businesses, schools and organizations become prepared for disasters and other emergencies.  Want to learn more?  Please visit readyrating.org" >
								<div class="field-wrap">
									<label style="margin: 0;" for="ratingMember" class="optional"><b><a href="http://readyrating.org" target="_blank">Ready Rating member: </a></b></label>
								</div>	
								<div class="field-wrap">
									<div class="field">
										<table><tr><td><form:radiobutton path="statistics.ratingMember" value="Yes" /> Yes</td><td><form:radiobutton path="statistics.ratingMember" value="No" /> No</td><td><form:radiobutton path="statistics.ratingMember" value="Unknown" /> Unknown</td></tr></table>
									</div>
								</div>
							</div>
							<div title=" AmeriCorps is a national network of programs that engages more than 70,000 Americans each year in intensive service to meet critical needs in communities throughout the nation.  To learn more, please visit americorps.gov" >
								<div class="field-wrap">
									<label style="margin: 0;" for="ameriCorps" class="optional"><a href="http://americorps.gov" target="_blank"><b>AmeriCorps member: </a></b></label>
								</div>	
								<div class="field-wrap">
									<div class="field">
										<table><tr><td><form:radiobutton path="statistics.ameriCorps" value="Yes" /> Yes</td><td><form:radiobutton path="statistics.ameriCorps" value="No" /> No</td><td><form:radiobutton path="statistics.ameriCorps" value="Unknown" /> Unknown</td></tr></table>
									</div>																								
								</div>
							</div>
						</div>
					</fieldset>
				</div>			
			</div>
		</section>
		</core:if>
		
		<core:if test="${courseRecord.feeOption!='Facility-fee'}" >
		<section class="primary-content">
				<header class="form-header">
					<h1>Instructors:* <!-- <a  href="javascript:void(0)" title="Click the &quot;Add Instructor&quot; link, You can add instructors associated with the Organization selected above."><span class="ui-icon ui-icon-info" style="display:inline-block"></span></a> --></h1>
				</header>										
				<div class="simple-form tabular-form">
					<div class="fieldset-group">	
						<fieldset>
						<div class="field-wrap">
								<form:errors path="errorMessage" cssClass="msgError" />
						</div>	
						<% if(isBrowserIE8OrLess(request.getHeader("User-Agent"))) { %>
						<table>
								<tr>
									<td width="5px"><a href="javascript:Add();" id="instructor" name="create" title="Click to add the instructor who taught the course. You can add multiple instructors."><span class="ui-icon ui-icon-plusthick"></span></a></td><td><a href="javascript:Add();" id="instructor" name="create" title="Click to add the instructor who taught the course. You can add multiple instructors.">Add Instructor</a> </td>
								</tr>
						</table>						
						<% } else { %>			
						<table>
							<tfoot>
								<tr>
									<td><a href="javascript:Add();" id="instructor" name="create" title="Click to add the instructor who taught the course. You can add multiple instructors."><span class="ui-icon ui-icon-plusthick" style="display:inline-block"></span> Add Instructor</a> </td>
								</tr>
							</tfoot>
						</table>
						<% } %>
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="searchResults">
						<thead>
							<tr>
								<th>ID</th>
								<th>Username</th>
								<th>First Name</th>
								<th>Last Name</th>
								<th>Email</th>
								<th>Phone</th>
								<th>Postal Code</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
						<tbody>
						</tbody>
						</table>
						</fieldset>
					</div>
				</div>
		</section>
		</core:if>
		
		<core:if test="${courseRecord.feeOption=='Fee' && precreate =='Yes'}" >
		<section class="primary-content">
				<header class="form-header">
					<h1>Payment Information</h1>
				</header>
				<div class="cert-form tabular-form">
					<div class="fieldset-group">
						<fieldset>
						
							<div class="field-wrap">
								<label for="paymentType">Payment Type: </label>
								<div class="field">
									<form:select items="${paymentTypeList}" path="payment.paymentTypeID" itemValue="key" itemLabel="value" />
								</div>
							</div>
							<div class="field-wrap">
								<span id="paymentType_msg"></span>
								<form:errors path="payment.paymentTypeID" cssClass="msgError" />
							</div>
						</fieldset>
						
						<div id="purchaseOrderFields" >
						
						<fieldset>
							<input type="hidden" id="totalPrice" name="totalPrice" value="0.0">
							<div class="field-wrap">
							     <label for="purchaseOrder">Purchase Order:</label>
							     <div class="field">
							     	<form:select items="${purchaseOrderList}" path="payment.purchaseOrderID" itemValue="key" itemLabel="value" />
							     </div>
							</div>
							<div class="field-wrap">
								<span id="purchaseOrder_msg"></span>
								<form:errors path="payment.purchaseOrderID" cssClass="msgError" />
							</div>
						</fieldset>
							
						</div>
	
					</div>
				</div>
		</section>
		</core:if>
		
		<core:if test="${courseRecord.feeOption=='Fee' && precreate=='No'}" >
		<section class="primary-content">
			<header class="form-header">
				<h1>Comments: </h1>
			</header>	
			<fieldset>
				<div class="field-wrap">
					<div class="field">
						<form:textarea path="comments" rows="5" cols="50" />
					</div>
				</div>
			</fieldset>
		</section>
		</core:if>
		
		<section class="primary-content">
				<core:if test="${courseRecord.feeOption=='Fee' && precreate=='No'}" >
				<footer class="form-action clearfix" title="Click next to save your entry and move to the next step. Select close to close this record, without saving.">
				</core:if>
				<core:if test="${courseRecord.feeOption=='Fee' && precreate=='Yes'}" >
				<footer class="form-action clearfix" title="Click create to save your entry.">
				</core:if>
				<core:if test="${courseRecord.feeOption=='Non-fee' || courseRecord.feeOption=='Facility-fee'}" >
				<footer class="form-action clearfix">
				</core:if>

					<core:if test="${courseRecord.feeOption=='Fee' && precreate=='No'}" >
					<div class="action">
						<div class="bw">
							<input type="submit" class="button" name="submit" value="Next">	
						</div>
					</div>
					<div class="action plain-button alt-button">
						<div class="bw">
							<input id="close" type="button" class="button" name="close" value="Close">	
						</div>
					</div>
					</core:if>
					<core:if test="${courseRecord.feeOption=='Fee' && precreate=='Yes'}" >
					<div class="action">
						<div class="bw">
							<input type="submit" class="button" name="submit" value="Create">	
						</div>
					</div>
					</core:if>
					<core:if test="${courseRecord.feeOption=='Non-fee'}" >
					<div class="action plain-button alt-button">
						<div class="bw">
							<input type="submit" class="button" name="more" value="Add Another Course">	
						</div>
					</div>
					<div class="action">
						<div class="bw">
							<input type="submit" class="button" name="submit" value="Submit">	
						</div>
					</div>
					<div class="action plain-button alt-button">
						<div class="bw">
							<input id="close" type="button" class="button" name="close" value="Close">	
						</div>
					</div>
					</core:if>
					
					<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                    <div class="action">
                        <div class="bw">
                            <input type="submit" class="button" name="submit" value="Next / Save">
                        </div>
                    </div>
                    <div class="action plain-button alt-button">
                        <div class="bw">
                            <input id="close" type="button" class="button" name="close" value="Close">  
                        </div>
                    </div>
                    </core:if>

				</footer>
		</section>
		<form:hidden  path="action"  />
		</form:form>
	</div>
</div>
<div id="ftr">
    <footer class="contentinfo" role="contentinfo">
        <section class="social-tab">
            <nav>
                <ul class="social-list-gray clearfix">
                    <li class="contact-us"><a href="http://www.redcross.org/contact-us"><span>Contact Us</span></a></li>
                    <li class="facebook"><a href="http://www.facebook.com/redcross"><span>Facebook</span></a></li>
                    <li class="twitter"><a href="http://www.twitter.com/redcross"><span>Twitter</span></a></li>
                    <li class="flicker"><a href="http://www.flickr.com/photos/americanredcross"><span>Flickr</span></a></li>
                    <li class="youtube"><a href="http://www.youtube.com/user/AmRedCross"><span>Youtube</span></a></li>
                </ul>
            </nav>
        </section>
        <div class="footer-content clearfix">
            <section class="taxonomy">
                <nav class="taxonomy-nav">
                    <h3 class="taxonomy-title"><a href="http://www.redcross.org/about-us">Who We Are</a></h3>
						<ul class="bullet-list">
							<li><a href="http://www.redcross.org/about-us/mission" data-ga-action="Content-Mission, Vision, and Fundamental Principles|Page_link">
							Mission, Vision, and Fundamental Principles</a></li>
							<li><a href="http://www.redcross.org/about-us/history" data-ga-action="Content-Our History|Page_link">Our History</a></li>
							<li><a href="http://www.redcross.org/about-us/governance" data-ga-action="Content-Governance|Page_link">Governance</a></li>
							<li><a href="http://www.redcross.org/about-us/publications" data-ga-action="Content-Publications|Page_link">Publications</a></li>
							<li><a href="http://www.redcross.org/about-us/careers" data-ga-action="Content-Career Opportunities|Page_link">Career Opportunities</a></li>
							<li><a href="http://www.redcross.org/about-us/media" data-ga-action="Content-Media Resources|Page_link">Media Resources</a></li>
						</ul>
                </nav>
                <nav class="taxonomy-nav">
                    <h3 class="taxonomy-title"><a href="http://www.redcross.org/what-we-do">What We Do</a></h3>
						<ul class="bullet-list">
							<li><a href="http://www.redcross.org/what-we-do/disaster-relief" data-ga-action="Content-Disaster Relief|Page_link">Disaster Relief</a></li>
							<li><a href="http://www.redcross.org/what-we-do/support-military-families" data-ga-action="Content-Supporting America's Military Families |Page_link">Supporting America's Military Families</a>
							</li>
							<li><a href="http://www.redcross.org/what-we-do/training-education" data-ga-action="Content-Health and Safety Training &amp; Education|Page_link">Health and Safety Training &amp; Education
							</a></li>
							<li><a href="http://www.redcross.org/what-we-do/blood-donation" data-ga-action="Content-Lifesaving Blood |Page_link"> Lifesaving Blood</a></li>
							<li><a href="http://www.redcross.org/what-we-do/international-services" data-ga-action="Content-International Services |Page_link">
							International Services</a></li>
						</ul>
                </nav>
                <nav class="taxonomy-nav">
                    <h3 class="taxonomy-title"><a href="http://www.redcross.org/prepare">Plan &amp; Prepare</a></h3>
						<ul class="bullet-list">
							<li><a href="http://www.redcross.org/prepare/location/home-family" data-ga-action="Content-Prepare Your Home and Family|Page_link">Prepare Your Home and Family</a>
							</li>
							<li><a href="http://www.redcross.org/prepare/location/school" data-ga-action="Content-Prepare Your School|Page_link">Prepare Your School</a>
							</li>
							<li><a href="http://www.redcross.org/prepare/location/workplace" data-ga-action="Content-Prepare Your Workplace|Page_link">Prepare Your Workplace</a>
							</li>
							<li><a href="http://www.redcross.org/prepare/disaster" data-ga-action="Content-Types of Emergency|Page_link">Types of Emergency</a>
							</li>
							<li><a href="http://www.redcross.org/prepare/disaster-safety-library" data-ga-action="Content-Tools and Resources|Page_link">Tools and Resources</a></li>
						</ul>
                </nav>
                <nav class="taxonomy-nav">
                    <h3 class="taxonomy-title"><a href="http://www.redcross.org/supporters">Our Supporters</a></h3>
						 <ul class="bullet-list">
							<li><a href="http://www.redcross.org/supporters/corporate-foundations" data-ga-action="Content-Corporate and Foundation|Page_link">Corporate and Foundation</a>
							</li>
							<li><a href="http://www.redcross.org/supporters/community-partners" data-ga-action="Content-Community Partners|Page_link">Community Partners</a>
							</li>
							<li><a href="http://www.redcross.org/supporters/individuals" data-ga-action="Content-Individual Major Donors|Page_link">Individual Major Donors</a>
							</li>
							<li><a href="http://www.redcross.org/supporters/celebrities" data-ga-action="Content-National Celebrity Cabinet|Page_link">
							National Celebrity Cabinet</a>
							</li>
						</ul>
                </nav>
            </section>
			<div class="promo-box-area clearfix">
                <div class="box-image promo-box clearfix">
                    <img class="promo-image" src="../images/home/footer-promo.png" alt="Join the Movement" />
						<ul class="bullet-list">
							<li>
							<!-- This is to remove the extra spaces in between -->
							<a href="http://www.redcross.org/support/volunteer" target="" class="">Volunteer</a>
							</li>
							<li>
							<!-- This is to remove the extra spaces in between -->
							<a href="http://www.redcross.org/support/get-involved/school-clubs" target="" class="">Involve Your School</a>
							</li>
							<li>
							<a href="http://www.redcrossblood.org/hosting-blood-drive" target="" class="">Host a Blood Drive</a>
							</li>
							<li>
							<!-- This is to remove the extra spaces in between -->
							<a href="http://www.redcross.org/support/donating-fundraising/fundraising-licensing/workplace-giving" target="" class="">Workplace Giving</a>
							</li>
							<li>
							<!-- This is to remove the extra spaces in between -->
							<a href="http://www.redcross.org/support/donating-fundraising/fundraising-licensing" target="" class="">Fundraise</a>
							</li>
						</ul>
                    <b class="bl"></b>
                    <b class="br"></b>
                </div>
            </div>
        </div><!-- END footer-content -->
        <section class="legal">
            <div class="legal-inner clearfix">
                <nav>
                    <p class="copyright">&copy; Copyright 2013 The American Red Cross</p>
					<ul>
					<li class="nav-item"><a class="nav-link" href="http://www.redcross.org/privacy-policy" data-ga-action="Content-Privacy Policy|Page_link">Privacy Policy</a></li>
					<li class="nav-item"><a class="nav-link" href="http://www.redcross.org/terms-of-use" data-ga-action="Content-Terms and Conditions|Page_link">Terms and Conditions</a></li>
					<li class="nav-item"><a class="nav-link" href="http://www.redcross.org/connect-with-us" data-ga-action="Content-Connect With Us|Page_link">Connect With Us</a></li>
					<li class="nav-item"><a class="nav-link" href="http://www.redcross.org/help-faq" data-ga-action="Content-FAQ|Page_link">FAQ</a></li>
					<li class="nav-item"><a class="nav-link" href="http://www.redcross.org/contact-us" data-ga-action="Content-Contact Us|Page_link">Contact Us</a></li></ul>
                </nav>
            </div>
        </section><!-- END legal -->
    </footer>
</div><!-- END ftr -->

<div id="overlay"><div><h4 class="grey-out">Processing ...</h4></div></div>

<div id="POWarningDialog" class="window" title="No Purchase Orders Found">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>No suitable purchase orders found for this organization.</p>
	</td>
	</tr>
	</table>	
</div>

<div id="deleteDialog" class="window" title="Delete Instructor Confirmation">
	<table align="center" width="85%">
	<tr>
	<td>
		<p id="courseRecordSheet"></p>
	</td>
	</tr>
	</table>	
</div>

<div id="warningDialog" class="window" title="Remove Instructor(s) Confirmation">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>If you change your organization your selected instructor(s) will be deleted.</p>
	</td>
	</tr>
	</table>	
</div>

<div id="addDialog" class="window">

 	<script type="text/javascript" charset="utf-8">

		var availableInstructorList = new Array();
		
		function populateAvailableInstructorlist(List){
			clearAddInstructor('fresh');
			availableInstructorList = List.availableInstructors;
			//console.log("availableInstructorList: " + availableInstructorList);
			for (var i = 0; i < availableInstructorList.length; i++) {
				//console.log("availableInstructorList Iterate: " +availableInstructorList[i].userName);
				$( '#instructorResults' ).append('<tr><td><input class="assignInstructor" id="' + availableInstructorList[i].id + '" name="assignInstructor" type="checkbox" value="' + availableInstructorList[i].id + '" />' + '</td><td title="Click the box next to the instructor you want to select then click \'OK\' to add them to the course record.">' + availableInstructorList[i].userName + '</td><td title="Click the box next to the instructor you want to select then click \'OK\' to add them to the course record.">' + availableInstructorList[i].firstName + '</td><td title="Click the box next to the instructor you want to select then click \'OK\' to add them to the course record.">' + availableInstructorList[i].lastName + '</td></tr>');
		    }
		    
		    if(availableInstructorList.length == 0){
		    	$( '#instructorResults' ).append('<tr><td align="center" colspan="4"><div style="width:250px;height:20px;padding:15px;text-align:center; background-color: #fffffff; border:2px solid #808080;" ><p class="light-grey-out">No search results found</p></div></td></tr>');
		    }
		    
		    $('.assignInstructor').click(function(){
		    
		    	$('#addError').text("");
				$('#addError').hide();
				
			    var boxes = $('.assignInstructor:checkbox');
			    
			    SelectedInstructorList = new Array();
			    
	            if(boxes.length > 0) {
		             
		             for(var b = 0; b < boxes.length; b++){   	
	                	if(boxes[b].checked == true){
	                		for(var i =0; i < availableInstructorList.length; i++){
			                	if( availableInstructorList[i].id == boxes[b].value){
				                	if (typeof Object.create !== 'function') {
									    Object.create = function (o) {
									        function F() {}
									        F.prototype = o;
									        return new F();
									    };
									}
			                		choosenInstructor = Object.create(SelectedInstructor);
			                	    choosenInstructor.personID = availableInstructorList[i].id;
									choosenInstructor.userName = availableInstructorList[i].userName;
									choosenInstructor.firstName = availableInstructorList[i].firstName;
									choosenInstructor.lastName = availableInstructorList[i].lastName;
									SelectedInstructorList.push(choosenInstructor);
								}
							}
	                	}
	                }
	            }
			});
		} 	
 	
		function getAvailableInstructorList(organizationID, username, firstname, lastname) {
		
			var url = '/instructor/instructor/instructorRoster.html?type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>&organizationID=' + organizationID;
			
			return $.ajax(url, {
				data: { 
					firstName: firstname,
					lastName: lastname,
					userName: username
				},
		        error: function(xhr, exception) {
					if (xhr.status === 0) {
					    console.log('Not connect. Verify Network.');
					} else if (xhr.status == 404) {
					    console.log('Requested page not found. [404]');
					} else if (xhr.status == 500) {
					    console.log('Internal Server Error [500].');
					} else if (exception === 'parsererror') {
					    console.log('Requested JSON parse failed.');
					} else if (exception === 'timeout') {
					    console.log('Time out error.');
					} else if (exception === 'abort') {
					    console.log('Ajax request aborted.');
					} else {
					    console.log('Uncaught Error.\n' + xhr.responseText);
					}
					window.location = '/instructor/authentication/login.html';
				}
			 });
		}
		
		function searchInstructor() { 
			  	$('#addError').text("");
				$('#addError').hide();
				clearAddInstructor('fresh');
				var username = $('#addDialog').find('input[name="searchUserName"]').val();
				var firstname = $('#addDialog').find('input[name="searchFirstName"]').val();
				var lastname = $('#addDialog').find('input[name="searchLastName"]').val();
				
				
				if($.trim( username ) == '' && $.trim( firstname ) == '' && $.trim( lastname ) == ''){
				  	$('#addError').text("Please enter a value below to search for instructors");
					$('#addError').show();				
				} else {
					$( '#instructorResults' ).append('<tr><td align="center" colspan="4"><div style="width:250px;height:20px;padding:15px;text-align:center; background-color: #fffffff; border:2px solid #808080;" ><p class="light-grey-out">Processing ...</p></div></td></tr>');
					getAvailableInstructorList($('#organizationID').val(), username, firstname, lastname).then(populateAvailableInstructorlist);
				}
		}
 	
  		$(document).ready(function() {
  		
  			$('#addDialog').pressEnter(function(){ searchInstructor(); });
  			
  			$('input[name="search"]').focus();
			$('input[name="search"]').click(function(){ searchInstructor(); });
			
			$('input[name="clear"]').click(function() { 
			  	$('#addError').text("");
				$('#addError').hide();
				clearAddInstructor('search');
			});
			
		});
		
	</script>
	
	<form method="post">
		<section class="primary-content" style="width: 90%;margin-left: 15px;">
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>							
							<legend>
									<p class="light-grey-out">Search for instructor below:</p>
							</legend>
							
							<div class="field-wrap">
								<span id="addError" class="msgError" style="display: none;"></span>
							</div>

							<div class="field-wrap">
								<label class="no-asterisk" for="firstName" title="Enter the first few letters of the instructor's information.  Only instructors associated with the organization selected on the previous page will appear.  If you want to see all the instructors for the organization, enter the percent sign (%) in one of the fields and select search.">First Name</label>
								<div class="field">
									<input type="text" name="searchFirstName" id="searchFirstName" cssClass="input-xxlarge" /> 
								</div>
							</div>
							<div class="field-wrap">
								<label class="no-asterisk" for="lastName" title="Enter the first few letters of the instructor's information.  Only instructors associated with the organization selected on the previous page will appear.  If you want to see all the instructors for the organization, enter the percent sign (%) in one of the fields and select search.">Last Name</label>
								<div class="field">
									<input type="text" name="searchLastName" id="searchLastName" cssClass="input-xxlarge" />
								</div>
							</div>

							<div id="addInstructorIndicators">
								<div class="field-wrap">
									<label for="id">ID</label>
									<div class="field">
										<input type="text" name="id" id="id" />
									</div>
								</div>
								<div class="field-wrap">
									<label class="no-asterisk" for="userName">Username</label>
									<div class="field ">
										<input type="text" name="userName" id="userName" cssClass="input-xxlarge" /> 
									</div>
								</div>
								<div class="field-wrap">
									<label class="no-asterisk" for="firstName">First Name</label>
									<div class="field">
										<input type="text" name="firstName" id="firstName" cssClass="input-xxlarge" /> 
									</div>
								</div>
								<div class="field-wrap">
									<label class="no-asterisk" for="lastName">Last Name</label>
									<div class="field">
										<input type="text" name="lastName" id="lastName" cssClass="input-xxlarge" />
									</div>
								</div>
								<div class="field-wrap">
									<label class="no-asterisk" for="userName">Username</label>
									<div class="field ">
										<input type="text" name="searchUserName" id="searchUserName" cssClass="input-xxlarge" /> 
									</div>
								</div>
							</div>
						</fieldset>
					</div>
				</div>
				<footer class="form-action clearfix">
					<div class="action">
						<div class="bw">
							<input name="search" id="searchBtn" type="button" class="button" value="Search">	
						</div>
					</div>
					<div class="action plain-button alt-button">
						<div class="bw">
							<input name="clear" type="button" class="button" value="Clear">	
						</div>
					</div>
				</footer>
		</section>
		<section class="primary-content" style="width: 90%;margin-left: 15px;">
				<header class="form-header">
					<h1>Instructor Search Results:</h1>
				</header>										
				<div class="standard-table course-table catalog-list-table">
					<div class="fieldset-group">	
						<fieldset>					
						<table cellpadding="0" cellspacing="0" border="0" id="instructorSearchResults">
						<thead>
							<tr>
								<th></th>
								<th>Username</th>
								<th>First Name</th>
								<th>Last Name</th>
							</tr>
						</thead>
						<tbody id="instructorResults">
						</tbody>
						</table>
						</fieldset>
					</div>
				</div>
		</section>
	</form>
</div>

</body>
</html>
