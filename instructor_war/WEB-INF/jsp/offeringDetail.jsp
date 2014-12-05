<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@ page import="com.arc.instructor.controller.OfferingController" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>American Red Cross | Scheduling Interface</title>
	<link rel="shortcut icon" href="../images/favicon.ico">
	<link rel="stylesheet" href="../css/main.css" media="all">
	<link rel="stylesheet" href="../css/jquery.dataTables_themeroller.css">
	<link rel="stylesheet" href="../css/jquery.dataTables.css">
  	<link rel="stylesheet" href="../css/jquery-ui-1.10.3.custom.css" />
  	<link rel="stylesheet" href="../css/print-preview.css" type="text/css" media="screen">
  	
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
		
		.simple-form.tabular-form label{
			padding-right: 30px;
			width: 170px;
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
		
		input.input-course {
			width: 450px;	
		}
			

	</style> 
  	
  	<core:set var="isOpen" value="${offeringDetail.sabaOfferingId==null || offeringDetail.status=='100'}"/>
  	<core:set var="skillStDt" value="${offeringDetail.blended=='true' ? 'Skill Session' : 'Class'}"/>
  	
  	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/jquery.dataTables.min.js"></script>
  	<script src="../js/jquery.jeditable.mini.js"></script>
  	<script src="../js/offering.js"></script>
  	
	<script type="text/javascript" charset="utf-8">
	
		var oInstrTable;
		var oSessTable;
		var instrArray = new Array();
		var sessionArray = new Array();
		var courseList = new Array();
		var giRedraw = false;
		var gOrgId = "";
		<core:if test="${offeringDetail.sabaOfferingId != null}">
			gOrgId = "${offeringDetail.orgId}";
		</core:if> 

		
		var isNew = ${offeringDetail.sabaOfferingId == null};
		
		var $ = jQuery.noConflict(true);
		
		function printContent(){
			var DocumentContainer = document.getElementById('PrintSection');
			var WindowObject = window.open("", "PrintWindow","width=800,height=750,top=25,left=50,toolbars=no,scrollbars=yes,status=no,resizable=yes");
			WindowObject.document.writeln("<html><head><title>American Red Cross | Scheduling Interface</title> <link rel=\"stylesheet\" href=\"../css/main.css\"></head>");
			WindowObject.document.writeln("<body class=\"print\" style=\"display: block;\" >");			
			WindowObject.document.writeln(DocumentContainer.innerHTML);
			WindowObject.document.writeln("</body></html>");		
			WindowObject.document.close();
			WindowObject.focus();
			WindowObject.print();
			WindowObject.close();
		}
		
		var SelectedInstructorList = new Array();
		var SelectedInstructor = {"personID" : "", "firstName" : "", "lastName" : ""};

		function refreshSessionArray(action, param)
		{
			var kAction1 = "<a id='delete' href='javascript:deleteSession(\"";
			var kAction2 = "\")' name='delete'><span class='ui-icon ui-icon-trash'></span></a>";
			if(action == 'Add')
			{
				var session = param;
				sessionArray.push({"uniqueId": session.uniqueId, "date": session.date, "startTime": session.startTime, "endTime": session.endTime, "action": kAction1 + session.uniqueId + kAction2});
			}
			else if(action == 'Delete')
			{
				
				var uniqueId = param;
				for(var i=0; i < sessionArray.length ; i++)
				{
					var item = sessionArray[i];
					if(item.uniqueId == uniqueId)
					{
						sessionArray.splice(i, 1);
						break;
					}
				}
			}
			oSessTable.fnClearTable();
			oSessTable.fnAddData(sessionArray);
			
			<core:if test="${offeringDetail.sabaOfferingId!=null}">
				var message = "Click Save once you have made all the session changes. Session changes are not saved until you click Save.";
				$("#sessionChangeNotice").text(message);
				$("#sessionChangeNoticeDiv").show();
			</core:if>
		}
		
		function refreshInstrArray(action, param)
		{
			var kAction1 = "<a id='delete' href='javascript:deleteInstructor(";
			var kAction2 = ")' name='delete'><span class='ui-icon ui-icon-trash'></span></a>";
			if(action == 'Add')
			{
				var instr = param;
				instrArray.push({"personID": instr.personID, "firstName": instr.firstName, "lastName": instr.lastName, "action": kAction1 + "\"" + instr.personID + "\"" + kAction2});
			}
			else if(action == 'Delete')
			{
				var personId = param;
				for(var i=0; i < instrArray.length ; i++)
				{
					var item = instrArray[i];
					if(item.personID == personId)
					{
						instrArray.splice(i, 1);
						break;
					}
				}
			}
			oInstrTable.fnClearTable();
			oInstrTable.fnAddData(instrArray);
		}
		
		function clearAddInstructor(action) 
		{
			SelectedInstructorList = new Array();
 			$('#addInstructorDialog').find('input[name="id"]').val('');
 			
 			if(action == 'fresh' || action == 'search')
 				$('#addInstructorDialog').find('#instructorResults' ).empty();
 				
 			if(action == 'search')
			{
	 			$('#addInstructorDialog').find('input[name="searchFirstName"]').val('');
	 			$('#addInstructorDialog').find('input[name="searchLastName"]').val('');
	 		}	
		}
		
		function clearAddSession()
		{
			$("#startTime1").val("");
			$("#startTime2").val("");
			$("#startTime3").val("");
			
			$("#endTime1").val("");
			$("#endTime2").val("");
			$("#endTime3").val("");
			
			$("#date").val("");
		}
		
		function addSession()
		{
			if($('#startDate').val() == '')
			{
				
				alertDialog("Please select the " + "<core:out value='${skillStDt}'/>" + " Start Date before adding sessions.");
			}
			else
			{
				$('#addSessionDialog').dialog({
					autoOpen: true,
	 				width: 555,
	 				height: 400,
	 				modal: true,
					open: function(event, ui) { 
						clearAddSession();
						$("#addSessionBtn").focus();
						$("#addSessionError").text("");
						$("#addSessionError").hide(); 
						if(sessionArray.length == 0)
						{
							$("#date").val($("#startDate").val());
						}
					}
				});
			}
		}
		
		function addInstructor() 
		{
  	 		if($('#orgId').val() == "")
			{
  	 			$('#addInstructorError').text("Please select an organization before adding instructors.");
  	 			$('#addInstructorError').show();
  	 			$("html, body").animate({scrollTop: $('#addInstructorError').offset().top }, 2000);
  	 		} 
			else 
			{
  	 			$('#addInstructorError').text("");
  	 			$('#addInstructorError').hide();
  	 			$('#addError').text("");
				$('#addError').hide();
  	 			clearAddInstructor('search');
			    $('#addInstructorDialog').dialog({
					autoOpen: true,
	 				width: 650,
	 				height: 550,
	 				modal: true,
	 				buttons: {
	  					"OK": function () {
	  						if( SelectedInstructorList.length == 0)
							{
	  							$('#addError').text("Please select an instructor from the search results below. ");		
		    					$('#addError').show();
		    					clearAddInstructor();
	  						}
	  							
	  							
	  						if( $('#addError').text() == "")
							{
		  						$(this).dialog("close");
								
								<core:choose>
									<core:when test="${offeringDetail.sabaOfferingId != null}">
										var offId = "<core:out value='${offeringDetail.sabaOfferingId}'/>";
										for(var i=0 ; i<SelectedInstructorList.length ; i++)
										{
											var item = SelectedInstructorList[i];
											var addInstrUrl =  "/instructor/offering/addInstructor.html?personId=" + item.personID + "&offeringId=" + offId;
											$.ajax({url: addInstrUrl, async: false}).then(function(data){
												refreshInstrArray("Add", item);
											});
										}
									</core:when>
									<core:otherwise>
										for(var i=0 ; i<SelectedInstructorList.length ; i++)
										{
											var item = SelectedInstructorList[i];
											refreshInstrArray("Add", item);
										}
									</core:otherwise>
								</core:choose>

								
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
  	 	
  	 	
		function deleteSession(row_num)
  	 	{
			var row_data = getSession(row_num);
			
	 		 $('#deleteText').text("Delete Session "+ row_data.date + " " +  row_data.startTime + "-" + row_data.endTime);
	 		 $('#deleteDialog').dialog(
			 {
				autoOpen: true,
				width: 330,
				modal: true,
				buttons: {
					"OK": function (){
						$(this).dialog("close");
						refreshSessionArray("Delete", row_num);
					}, 
					"Cancel": function(){
					  $(this).dialog("close");
					} 
				}
			});
  	 	}
		
		function deleteInstructor(row_num)
  	 	{
			var row_data = getInstructor(row_num);

			$('#deleteText').text("Delete Instructor "+ row_data.firstName + " " +  row_data.lastName);
	 		$('#deleteDialog').dialog({
				autoOpen: true,
				width: 330,
				modal: true,
				buttons: {
					"OK": function (){
						$(this).dialog("close");
						
						<core:choose>
							<core:when test="${offeringDetail.sabaOfferingId != null}">
								var offId = "<core:out value='${offeringDetail.sabaOfferingId}'/>";
								var url =  "/instructor/offering/deleteInstructor.html?personId=" + row_data.personID + "&offeringId=" + offId;
								$.ajax(url).then(function(data){
									refreshInstrArray("Delete", row_data.personID);
								});
							</core:when>
							<core:otherwise>
								refreshInstrArray("Delete", row_data.personID);
							</core:otherwise>
						</core:choose>
					}, 
					"Cancel": function(){
					  $(this).dialog("close");
					}
				} 
			});
  	 	}
  	 	
  	 	function getInstructor(personId)
  	 	{
  	 		if(!isNaN(personId))
  	 		{
  	 			return instrArray[personId];
  	 		}
			   
			for(var i=0 ; i < instrArray.length ; i++)
  	 		{
  	 			var instructor = instrArray[i];
  	 			if(personId == instructor.personID)
  	 			{
  	 				return instructor;
  	 			}
  	 		}
  	 	}
  	 	
  	 	function getSession(sessionId)
  	 	{
			for(var i=0 ; i < sessionArray.length ; i++)
  	 		{
  	 			var session = sessionArray[i];
  	 			if(session.uniqueId == sessionId)
  	 			{
  	 				return session;
  	 			}
  	 		}
  	 	}
  	 	
  	 	function isOffsetByDays(startDate, offsetDays)
  	 	{
  	 		var today = new Date();
  	 		var todayInMillis = new Date(today.getFullYear(), today.getMonth(), today.getDate()).getTime();
			var offsetDaysInMillis = 1000 * 60 * 60 * 24 * offsetDays;
			var targetInMillis = todayInMillis + offsetDaysInMillis;
			var targetDate = new Date(targetInMillis); 
			
  	 		var dd = targetDate.getDate();
  	 		if(dd<10) 
			   dd = '0' + dd;
			
			var mm = targetDate.getMonth()+1;
			if(mm<10)
				mm = '0' + mm;
				
			var yyyy = targetDate.getFullYear();
			var strTarget = mm + '/' + dd + '/' + yyyy;
			
			return (startDate==strTarget) || isFormerAfterLater(startDate, strTarget);
  	 	}
  	 	
  	 	function validateData()
  	 	{
  	 		if($("#orgId").val() == '')
  	 		{
  	 			alertDialog("Please select an Organization.");
  	 			return false;
  	 		}
  	 		
  	 		if(isNew && $("#courseId").val() == '')
  	 		{
  	 			alertDialog("Please select a course.");
  	 			return false;
  	 		}
  	 		
  	 		//Validate Date
			if($("#startDate").val() == '')
  	 		{
  	 			alertDialog("Please select the " + "<core:out value='${skillStDt}'/>" + " start date.");
  	 			return false;
  	 		}
  	 		var startDate =  $("#startDate").val();
			if(!validateDateFormat(startDate, "Invalid Date Format. Please correct and try again."))
			{
				return false;
			}
			
			if(isNew)
			{
				<core:choose>
					<core:when test="${offeringDetail.blended=='true'}">
						if(!isOffsetByDays(startDate, 7))
						{
							alertDialog("Blended Learning class must be set up atleast 7 days in advance.");
							return false; 
						}
					</core:when>
					<core:otherwise>
						if(!isOffsetByDays(startDate, 1))
						{
							alertDialog("Instructor-Led class must be set up atleast 1 day in advance.");
							return false; 
						}
					</core:otherwise>
				</core:choose>
			}
  	 		//Validate Max Count
  	 		if($("#maxCount").val() == '')
  	 		{
  	 			alertDialog("Please select the Max Enrollment Count.");
  	 			return false;
  	 		}
  	 		var maxCountStr = $("#maxCount").val();
  	 		var numberFormat = /^[0-9]*$/; 
  	 		if(!numberFormat.test(maxCountStr))
  	 		{
  	 			alertDialog("Please enter a valid Max Enrollment Count.");
  	 			return false;
  	 		}
  	 		
  	 		var maxCount = parseInt(maxCountStr);
  	 		if(maxCount < 1 || maxCount > 500)
  	 		{
  	 			alertDialog("Please select a Max Enrollment Count between 1 and 500.");
  	 			return false;
  	 		}
  	 		var studCount = parseInt($("#studCount").text());
			if(!isNew && maxCount < studCount)
  	 		{
  	 			alertDialog("Max Enrollment Count cannot be less than Enrollment Count.");
  	 			return false;
  	 		}
  	 		
  	 		if(sessionArray.length == 0)
  	 		{
  	 			alertDialog("Please add at least one session to the offering.");
  	 			return false;
  	 		}
			   
			if(instrArray.length == 0)
  	 		{
  	 			alertDialog("Please select at least one instructor.");
  	 			return false;
  	 		}
  	 	
  	 		return true;
  	 	
  	 	}

		$(document).ready(function( $ ) 
		{
			$( document ).tooltip();
			
			if(isNew)
			{	
				<core:choose>
					<core:when test="${offeringDetail.blended=='true'}">
						getCourseListForBlended("");
					</core:when>
					<core:otherwise>
						getCourseListForILT();
					</core:otherwise>
				</core:choose>
				$('#courseName').autocomplete({
			      source: courseList,
			      minLength: 0,
			      select: function( event, ui ) {
						$('#courseId').val(ui.item.key); 
						setPrice(ui.item.key);
					},
			      open: function() {$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );},
			      close: function() {$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );}
			    });
				
				$('#courseName').focus(function(){ $(this).autocomplete("search", ""); });
				
				$("#courseCategory").change(function(){
					getCourseListForBlended($(this).val());
				});
				
				// disable Enter key on course name autocomplete field (fee and non-fee)
				$("#courseName").keypress(function(event){
					var keycode = (event.keyCode ? event.keyCode : event.which);
					if (keycode == '13') {
						event.preventDefault();
						event.stopPropagation();    
					}
				});
			}
			
			//Start Date and session start date Calender widget
			<core:if test="${isOpen}">
				<core:choose>
					<core:when test="${offeringDetail.sabaOfferingId==null}">
						<core:set var="dtOffset" scope="request" value="${offeringDetail.blended=='true' ? 7 : 1}"/>
						$("#startDate").datepicker({minDate: <core:out value="${dtOffset}"/>});
						$("#date").datepicker( {minDate: <core:out value="${dtOffset}"/>, dateFormat: "mm/dd/yy"} );
					</core:when>
					<core:otherwise>
						$("#startDate").datepicker();
						$("#date").datepicker( {dateFormat: "mm/dd/yy"} );
					</core:otherwise>
				</core:choose>

			//For start date
			$("#showCalendar").click(function() {
 				 $('#startDate').datepicker('show');
 			});
 			
 			//For session start date
			$("#showSessionsCalendar").click(function() {
 				 $('#date').datepicker('show');
 			});
 			
 			$("#orgId").change(function(){
				if(	($("#facilityId").val() != '') || ($("#poId").val() != '') || (instrArray.length > 0) )
				{
						var message = "Changing the organization will remove the facility, purchase order and instructors. Do you want to continue?";
						$("#confirmText").text(message);
						$("#confirmDialog").dialog({
							autoOpen: true,
							width: 330,
							modal: true,
							position: {my: "center", at: "center", of: window},
							buttons: { 
								"OK": function(){
									$(this).dialog("close");
									$("#confirmText").text(message);
									$("#facilityId").val('');
									$("#poId").val('');
									setFacilityList($("#orgId").val());
									setPurchaseOrderList($("#orgId").val());
									<core:choose>
										<core:when test="${offeringDetail.sabaOfferingId==null}">
											instrArray = new Array();
											oInstrTable.fnClearTable();
										</core:when>
										<core:otherwise>
											var offId = "<core:out value='${offeringDetail.sabaOfferingId}'/>";
											var url =  "/instructor/offering/deleteInstructor.html?offeringId=" + offId;
											$.ajax(url).then(function(data){
												instrArray = new Array();
												oInstrTable.fnClearTable();
											});
										</core:otherwise>
									</core:choose>
									
									gOrgId = $("#orgId").val();
								},
								"Cancel": function(){
									$(this).dialog("close");
									$("#orgId").val(gOrgId);
								} 
							} 
						});
						return false;
				}
				setFacilityList($(this).val());
				setPurchaseOrderList($(this).val());
				gOrgId = $(this).val();
			});
 			
			$("#save").click(function(){
				var sessions = "";
				for(var i=0 ; i < sessionArray.length ; i++)
				{
					var offSession = sessionArray[i];
					sessions = sessions + offSession.date + offSession.startTime + offSession.endTime;
				}
				$("#sessionArrayHiddenField").val(sessions);

				var instructors = "";
				for(var i=0 ; i < instrArray.length ; i++)
				{
					var instr = instrArray[i];
					instructors = instructors + instr.personID;
				}
				$("#instrArrayHiddenField").val(instructors);
				if(!validateData()) return false;
				centerModal();
				document.offeringDetail.submit();
			});
			</core:if>
			
			<core:if test="${offeringDetail.sabaOfferingId!=null and offeringDetail.status!='500'}">
				$("#roster").click(function(){
					document.location.href = "/instructor/offering/roster.html?offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
				});
				
				$("#certificates").click(function(){
					document.location.href = "/instructor/offering/certificates.html?offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
				});
				
			</core:if>
			
			
			$("#close").click(function(){
				$("#offeringAction").val("Close");
				document.offeringDetail.submit();
			});
			
			
			<core:if test="${offeringDetail.sabaOfferingId!=null || offeringDetail.sabaMessage!=null}">
				sessionArray = ${javaSessionArray};
				instrArray = ${javaInstructorArray};
			</core:if>

			$("#sessionChangeNoticeDiv").hide();
			$("#addSessionDialog").hide();
			oSessTable = $("#sessionsTable").dataTable({ 
				"oLanguage": {
					"sEmptyTable": "Add session(s) to the offering."
				},
				"bPaginate": false,
				"bLengthChange": false,
        		"bSort": false,
			    "bJQueryUI": true,
		        "bFilter": false,
		        "aoColumns": [ 
                               { "mData": "date", "sName": "date", "sTitle": "Session Date" },  
                               { "mData": "startTime", "sName": "startTime", "sTitle": "Start Time" },
							   { "mData": "endTime", "sName": "endTime", "sTitle": "End Time" }, 
							   { "mData": "action", "sName": "action", "sTitle": "Delete","sWidth": "10%"}   
        					],
				"aaData": sessionArray
			});
			
			$("#addInstructorDialog").hide();
			oInstrTable = $("#searchResults").dataTable({ 
				"oLanguage": {
					"sEmptyTable": "Add instructor(s) to the offering."
				},
				"bPaginate": false,
				"bLengthChange": false,
        		"bSort": false,
			    "bJQueryUI": true,
		        "bFilter": false,
		        "aoColumns": [ 
                               { "mData": "personID", "sName": "personID", "sTitle": "Saba ID", "bVisible": false },
							   { "mData": "firstName", "sName": "firstName", "sTitle": "First Name" },  
                               { "mData": "lastName", "sName": "lastName", "sTitle": "Last Name" },  
                               { "mData": "action", "sName": "action", "sTitle": "Delete","sWidth": "10%" } 
        					],
				"aaData": instrArray
			});

		} );
	</script>

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
			<p class="light-grey-out"> Welcome <span class="grey-out"><core:out value="${username}" /></span>; Click here to <a href="<core:url value="/authentication/login.html?feeOption=Scheduling"/>" >logout.</a></p>
		</div>
	</header>
</div>
<div class="left-rail content-wrap default-layout">
	<div class="content student-page">
		<aside class="sidebar" role="complementary">
		    <nav class="sidebar-inner">
		        <ul class="nav-list">
		            <%= OfferingController.printMenuItems(request, "scheduledOffering") %>
		        </ul>
		        <div class="sidebar-module-wrap"></div>
		    </nav>
		</aside>

		<form:form method="POST" name="offeringDetail" commandName="offeringDetail" >
		
		<section class="primary-content">
			<header class="primary-header">
				<h1><core:out value="${pageTitle}"/></h1>
			</header>
		
			<core:if test="${offeringDetail.sabaMessage != null}">
				<div class="field-wrap">
					<span id="sabaMessage" class="msgError"><core:out value="${offeringDetail.sabaMessage}"/></span>
				</div>
			</core:if>
		</section>
		<!-- Contact -->
		<section class="primary-content ">
			<div class="simple-form tabular-form">
				<div class="fieldset-group">
					<fieldset>
						<div class="field-wrap min-fifty-percent" style="margin-right: 35px;">
							<label for="" class="grey-out no-asterisk" style="width: auto; padding-right: 20px;">Contact:</label>
							<div class="field">
								<core:choose>
									<core:when test="${offeringDetail.sabaOfferingId != null}">
										<span><core:out value="${offeringDetail.contactUsername}" /></span>
									</core:when>
									<core:otherwise>
										<span><core:out value="${username}" /></span>
									</core:otherwise>
								</core:choose>
							</div>
						</div>
						<core:if test="${offeringDetail.sabaOfferingId != null}">
						<div class="field-wrap">
							<label for="" class="grey-out no-asterisk" style="width: auto; padding-right: 20px;">Offering No.:</label>
							<div class="field">
								<span><core:out value="${offeringDetail.offeringNo}" /></span>
							</div>
						</div>
						</core:if>
					</fieldset>
				</div>
			</div>
			
			
			
			<header class="form-header" style="border-bottom: solid 2px;margin-bottom: 5px">
				<h1>Class Information</h1>
			</header>
			<div style="color: #6D6E70;">
				<p><core:out value="${pageText}"/></p>
			</div>
			<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>

							<div class="field-wrap">
								<span id="addInstructorError" class="msgError" style="display: none;"></span>
							</div>
							
							<!-- Organization -->
							<div class="field-wrap">
						    	<label for="organizationName">Organization:</label>
								<core:choose>
									<core:when test="${isOpen}">
										<form:select items="${organizationList}" path="orgId" itemValue="key" itemLabel="value" />
									</core:when>
									<core:otherwise>
										<span id="orgId"><core:out value="${offeringDetail.orgName}" /></span>
									</core:otherwise>
								</core:choose>
							</div>

							<!-- Course Category -->
							<core:if test="${offeringDetail.blended=='true' && offeringDetail.sabaOfferingId==null}">
								<div class="field-wrap">
									<label class="optional" for="courseCategory">Course Category: <span class="optional-text">(optional)</span></label>
									<div class="field">
										<form:select items="${courseCategoryList}" path="courseCategory" itemValue="key" itemLabel="value" />
									</div>
								</div>
							</core:if>
							
							<!-- Course -->
							<div class="field-wrap">
								<label for="course">Course:</label>
								<div class="field">
									<core:choose>
										<core:when test="${offeringDetail.sabaOfferingId!=null}">
											<span id="courseName"><core:out value="${offeringDetail.courseName}" /></span>
										</core:when>
										<core:otherwise>
											<form:input  path="courseName" autocomplete="off" cssClass="input-course"  />
										</core:otherwise>
									</core:choose>								
									<form:hidden  path="courseId"  />  
								</div>
								<div id="courseResultText" style="margin-left:10px; float:left; font-weight:bold; color:#AD5E5C;"></div>
							</div>
							
							<!-- Delivery Type 
							<div class="field-wrap">
								<label for="deliveryType">Delivery Type: </label>
								<div class="field">
									<core:choose>
										<core:when test="${offeringDetail.sabaOfferingId!=null}">
											<span id="deliveryType"><core:out value="${offeringDetail.deliveryTypeName}" /></span>
										</core:when>
										<core:otherwise>
											<form:select items="${deliveryTypeList}" path="deliveryType" itemValue="key" itemLabel="value" />
										</core:otherwise>
									</core:choose>
								</div>
							</div>-->
							
							<!-- Start Date -->
							<div class="field-wrap">
								<core:set var="stDateHover" value="${(offeringDetail.sabaOfferingId==null and offeringDetail.blended=='true') ? 'Blended Learning courses must be set up 7 days in advance of the skills session to ensure students have sufficient time to complete the online session.' : ''}"/>
								<label title="<core:out value='${stDateHover}'/>" for="startDate"><core:out value="${skillStDt}"/> Start Date:</label>	
								<core:choose>
									<core:when test="${isOpen}">
										<div class="field">
											<form:input path="startDate" cssClass="input-medium" /> 
										</div>
										<a id="showCalendar" title="Click to view calendar" href="javascript:void(0)"><span class="ui-icon ui-icon-calendar" style="display:inline-block"></span></a>
									</core:when>
									<core:otherwise>
										<div class="field">
											<span id="startDate"><core:out value="${offeringDetail.startDate}"/></span>
										</div>
									</core:otherwise>
								</core:choose>		
							</div>

							<!-- End Date -->
							<core:if test="${offeringDetail.sabaOfferingId != null}">
								<div class="field-wrap">
									<label class="grey-out no-asterisk" for="endDate">End Date:</label>
									<div class="field">
										<span id="endDate"><core:out value="${offeringDetail.endDate}" /></span>
									</div>
								</div>
							</core:if>
							
							<!-- Price -->
							<core:set var="offeringPrice" value="${offeringDetail.sabaOfferingId!=null || offeringDetail.sabaMessage != null ? offeringDetail.price : ''}"/>
							<core:set var="couponNote" value="${offeringDetail.showCouponNote ? 'If this differs from your contract price, apply your organization’s coupon code below.' : ''}"/>
							<div class="field-wrap">
								<label class="grey-out no-asterisk" for="price">Price per person paid to the American Red Cross:</label>
								<div class="field">
									<span id="price"><core:out value="${offeringPrice}"/></span>
									<div class="optional"><span id="couponNote" class="optional-text">
										<core:out value="${couponNote}"/>
									</span></div>	
								</div>
							</div>
				
							<!-- Discounted Price -->
							<core:if test="${offeringDetail.discountedPrice != null}">
								<div class="field-wrap">
									<label class="grey-out no-asterisk" for="discountedPrice">Discounted Price:</label>
									<div class="field">
										<span id="discountedPrice">
											<core:out value="${offeringDetail.discountedPrice}"/>
										</span>
									</div>
								</div>
							</core:if>
							
							<!-- Coupon Code -->
							<div class="field-wrap">
								<label class="optional" title="Check with your organization's billing contact for applicable coupon codes" for="couponCode">Coupon Code: <span class="optional-text">(case-sensitive)</span></label>
								<div class="field">
									<core:choose>
										<core:when test="${isOpen}">
											<form:input path="couponCode" cssClass="input-large" />
										</core:when>
										<core:otherwise>
											<span id="maxCount"><core:out value="${offeringDetail.couponCode}" /></span>
										</core:otherwise>
									</core:choose>
								</div>
							</div>							
							
							<!-- Max count -->
							<div class="field-wrap">
								<label for="maxCount" title="Maximum number of students you want to allow in the class.">Max Enrollment Count:</label>
								<div class="field">
									<core:choose>
										<core:when test="${isOpen}">
											<form:input path="maxCount" cssClass="input-tiny" />
										</core:when>
										<core:otherwise>
											<span id="maxCount"><core:out value="${offeringDetail.maxCount}" /></span>
										</core:otherwise>
									</core:choose>
								</div>
							</div>
							
							<!-- Stud Count -->
							<core:if test="${offeringDetail.sabaOfferingId != null}">
								<div class="field-wrap">
									<label class="grey-out no-asterisk" for="studCount">Enrollment Count:</label>
									<div class="field">
										<span id="studCount"><core:out value="${offeringDetail.studCount}" /></span>
									</div>
								</div>
							</core:if>
							
							<!-- Facility -->
							<div class="field-wrap">
								<label class="optional" title="Training locations associated with an organization in the system will appear. To add a training location, contact the TSC." for="facility">Training Location: </label>
								<div class="field">
									<core:choose>
										<core:when test="${isOpen}">
											<form:select items="${facilityList}" path="facilityId" itemValue="key" itemLabel="value" />
										</core:when>
										<core:otherwise>
											<span id="facilityId"><core:out value="${offeringDetail.facilityName}" /></span>
										</core:otherwise>
									</core:choose>
								</div>
							</div>
							
							<!-- Purchase Orders -->
							<div class="field-wrap">
								<label class="optional" title="If your company is approved for invoicing, select the appropriate purchase order. Students will not need to enter payment information when they register. If you would like to set up a prepaid purchase order, please call the Training Support Center at 1-800-Red Cross and select Health and Safety Services Option.
If a prepaid purchase order has not been set up, credit card payment must be entered by each student when they register." for="poId">Red Cross Invoice Purchase Order #</label>
								<div class="field">
									<core:choose>
										<core:when test="${isOpen}">
											<form:select items="${purchaseOrderList}" path="poId" itemValue="key" itemLabel="value" />
										</core:when>
										<core:otherwise>
											<span id="poId"><core:out value="${offeringDetail.poNumber}" /></span>
										</core:otherwise>
									</core:choose>
								</div>
							</div>
							
							<!-- Deeplink -->
							<core:if test="${offeringDetail.sabaOfferingId != null}">
								<div class="field-wrap">
									<label class="grey-out no-asterisk" for="deeplink">Student Registration Link:</label>
									<div class="field">
										<span id="deeplink"><core:out value="${offeringDetail.deeplink}" /></span>
									</div>
								</div>
							</core:if>
						</fieldset>
					</div>
				</div>
		</section>
		
		<!-- Sessions -->
		<section class="primary-content">
			<header class="form-header">
				<h1>Sessions:* </h1>
			</header>	
			<div class="simple-form tabular-form">
				<div class="fieldset-group">	
					<fieldset>
					<core:if test="${isOpen}">
						<div id="sessionChangeNoticeDiv" class="field-wrap" style="display: none;">
							<span id="sessionChangeNotice" class="msgError"></span>
						</div>
						<c:set var="sessionTooltip" value="${offeringDetail.blended=='true' ? 'Provide the date and time for the In-Person Skills Session. If your class has multiple skill sessions, please enter each session.' : 'Provide the date and time for the class session. If your class has multiple sessions, please enter each session.'}"/>
						<% if(isBrowserIE8OrLess(request.getHeader("User-Agent"))) { %>
						<table>
								<tr>
									<td width="5px"><a href="javascript:addSession();" title="<core:out value='${sessionTooltip}'/>" name="create"><span class="ui-icon ui-icon-plusthick"></span></a></td>
									<td><a href="javascript:addSession();" id="session" title="<core:out value='${sessionTooltip}'/>" name="create">Add Session</a> </td>
								</tr>
						</table>						
						<% } else { %>			
						<table>
							<tfoot>
								<tr>
									<td><a href="javascript:addSession();" id="session" title="<core:out value='${sessionTooltip}'/>" name="create"><span class="ui-icon ui-icon-plusthick" style="display:inline-block"></span> Add Session</a> </td>
								</tr>
							</tfoot>
						</table>
						<% } %>
					</core:if>
					<table cellpadding="0" cellspacing="0" border="0" class="display" id="sessionsTable">
					<thead>
						<tr>
							<th>Date</th>
							<th>Start Time</th>
							<th>End Time</th>
							<th>Delete</th>
						</tr>
					</thead>
						<tbody>
						</tbody>
					</table>
					</fieldset>
				</div>
			</div>
			<input type="hidden" id="sessionArrayHiddenField" name="sessionArrayHiddenField" value="">
		</section>	
		
		
		<!-- Instructors -->
		<section class="primary-content">
			<header class="form-header">
				<h1>Instructors:* </h1>
			</header>	
			<div class="simple-form tabular-form">
				<div class="fieldset-group">	
					<fieldset>
					<core:if test="${isOpen}">
						<% if(isBrowserIE8OrLess(request.getHeader("User-Agent"))) { %>
						<table>
								<tr>
									<td width="5px"><a href="javascript:addInstructor();" name="create"><span class="ui-icon ui-icon-plusthick"></span></a></td>
									<td><a href="javascript:addInstructor();" id="instructor" name="create">Add Instructor</a> </td>
								</tr>
						</table>						
						<% } else { %>
						<table>
							<tfoot>
								<tr>
									<td><a href="javascript:addInstructor();" id="instructor" name="create"><span class="ui-icon ui-icon-plusthick" style="display:inline-block"></span> Add Instructor</a> </td>
								</tr>
							</tfoot>
						</table>
						<% } %>
					</core:if>
					<table cellpadding="0" cellspacing="0" border="0" class="display" id="searchResults">
					<thead>
						<tr>
							<th>Saba ID</th>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Delete</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
					</table>
					</fieldset>
				</div>
			</div>
			<input type="hidden" id="instrArrayHiddenField" name="instrArrayHiddenField" value="">
		</section>	

		<!-- Buttons -->
		<section class="primary-content">
				<footer class="form-action clearfix">
					<core:if test="${isOpen}">
						<div class="action">
							<div class="bw">
								<input id="save" type="button" class="button" name="Save" value="Save">	
							</div>
						</div>
					</core:if>
					<core:if test="${offeringDetail.sabaOfferingId!=null and offeringDetail.status!='500'}">
						<div class="action plain-button alt-button">
							<div class="bw">
								<input id="roster" type="button" class="button" name="roster" value="Manage Students">	
							</div>
						</div>
						<div class="action plain-button alt-button">
							<div class="bw">
								<input id="certificates" type="button" class="button" name="certificates" value="Print Certificates">	
							</div>
						</div>
					</core:if>
					
					<div class="action plain-button alt-button" title="Exit to return to main screen." >
						<div class="bw">
							<input id="close" type="button" class="button" name="close" value="Exit">	
						</div>
					</div>
				</footer>
				
				<core:if test="${offeringDetail.sabaOfferingId != null and offeringDetail.status=='100'}">
					<br/>
					<div style="color: #6D6E70;">
						<p><core:out value="If you need to cancel a class, please call the TSC."/></p>
					</div>
				</core:if>
		</section>
	</div>
	<form:hidden path="offeringAction"/>
	<form:hidden path="sabaOfferingId"/>
	<form:hidden path="blended"/>
	</form:form>
</div>

<%@ include file="footer.jsp" %>
<div id="overlay"><div><h4 class="grey-out">Processing ...</h4></div></div>
<div id="deleteDialog" class="window" title="Delete Confirmation">
	<table align="center" width="85%">
	<tr>
	<td>
		<p id="deleteText"></p>
	</td>
	</tr>
	</table>	
</div>

<div id="confirmDialog" class="window" title="Attention">
	<table align="center" width="85%">
	<tr>
	<td>
		<p id="confirmText"></p>
	</td>
	</tr>
	</table>	
</div>

<div id="alertDialog" class="window" title="Attention">
	<table align="center" width="85%">
	<tr>
	<td>
		<p id="alertText"></p>
	</td>
	</tr>
	</table>	
</div>

<div id="addInstructorDialog" class="window" title="Add Instructor">

 	<script type="text/javascript" charset="utf-8">

		
		function populateAvailableInstructorlist(availableInstructorList)
		{
			clearAddInstructor('fresh');
			for (var i = 0; i < availableInstructorList.length; i++) 
			{
				$( '#instructorResults' ).append('<tr><td><input class="assignInstructor" id="' + availableInstructorList[i].id + '" name="assignInstructor" type="checkbox" value="' + availableInstructorList[i].id + '" />' + '</td><td title="Click the box next to the instructor you want to select then click \'OK\' to add them to the course record.">' + availableInstructorList[i].userName + '</td><td title="Click the box next to the instructor you want to select then click \'OK\' to add them to the course record.">' + availableInstructorList[i].firstName + '</td><td title="Click the box next to the instructor you want to select then click \'OK\' to add them to the course record.">' + availableInstructorList[i].lastName + '</td></tr>');
		    }
		    
		  if(availableInstructorList.length == 0)
		  {
		    	$( '#instructorResults' ).append('<tr><td align="center" colspan="4"><div style="width:250px;height:20px;padding:15px;text-align:center; background-color: #fffffff; border:2px solid #808080;" ><p>No search results found</p></div></td></tr>');
		  }
		    
		  $('.assignInstructor').click(function()
		  {
		   	$('#addError').text("");
			$('#addError').hide();
	
		    var boxes = $('.assignInstructor:checkbox');
			    
		    SelectedInstructorList = new Array();
			    
		    for(var b = 0; b < boxes.length; b++)
			{   	
            	if(boxes[b].checked == true)
				{
            		for(var i =0; i < availableInstructorList.length; i++)
					{
	                	if( availableInstructorList[i].id == boxes[b].value)
						{
		                	if (typeof Object.create !== 'function') 
							{
							    Object.create = function (o) 
								{
							        function F() {}
							        F.prototype = o;
							        return new F();
							    };
							}
	                		choosenInstructor = Object.create(SelectedInstructor);
	                	    choosenInstructor.personID = availableInstructorList[i].id;
							choosenInstructor.firstName = availableInstructorList[i].firstName;
							choosenInstructor.lastName = availableInstructorList[i].lastName;
							SelectedInstructorList.push(choosenInstructor);
						}
					}
            	}
            }
		});
		} 	
 	
		function getAvailableInstructorList(orgId, firstname, lastname) 
		{
			var url = '/instructor/offering/searchInstructor.html?type=json&offeringId=<core:out value="${offeringDetail.sabaOfferingId}"/>&orgId=' + orgId;
			return $.ajax(url, 
				{
					data: 
					{ 
						firstName: firstname,
						lastName: lastname
					},
			 });
			 
		}
		
		function searchInstructor() 
		{ 
			$('#addError').text("");
			$('#addError').hide();
			clearAddInstructor('fresh');
			var firstname = $('#addInstructorDialog').find('input[name="searchFirstName"]').val();
			var lastname = $('#addInstructorDialog').find('input[name="searchLastName"]').val();
			
			
			//if($.trim( firstname ) == '' && $.trim( lastname ) == '')
			//{
			//  	$('#addError').text("Please enter a value below to search for instructors");
			//	$('#addError').show();				
			//} 
			//else 
			//{
				$( '#instructorResults' ).append('<tr><td align="center" colspan="4"><div style="width:250px;height:20px;padding:15px;text-align:center; background-color: #fffffff; border:2px solid #808080;" ><p class="light-grey-out">Processing ...</p></div></td></tr>');
				getAvailableInstructorList($('#orgId').val(), firstname, lastname).then(populateAvailableInstructorlist);
			//}
		}
		
		function searchAllInstructors()
		{
			$('#addError').text("");
			$('#addError').hide();
			clearAddInstructor('fresh');

			$( '#instructorResults' ).append('<tr><td align="center" colspan="4"><div style="width:250px;height:20px;padding:15px;text-align:center; background-color: #fffffff; border:2px solid #808080;" ><p class="light-grey-out">Processing ...</p></div></td></tr>');
			getAvailableInstructorList($('#orgId').val(), '', '').then(populateAvailableInstructorlist);
		}
 	
  		$(document).ready(function() 
		{
  			//$('#addInstructorDialog').pressEnter(function(){ searchInstructor(); });
  			
  			$('input[name="search"]').focus();
			$('input[name="search"]').click(searchInstructor);
			$('input[name="searchAll"]').click(searchAllInstructors);
			
			$('input[name="clear"]').click(function() 
			{ 
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
									<p>Search for instructor below</p>
							</legend>
							
							<div class="field-wrap">
								<span id="addError" class="msgError" style="display: none;"></span>
							</div>

							<div class="field-wrap">
								<label class="no-asterisk" for="searchFirstName" title="Enter the first few letters of the instructor's information.  Only instructors associated with the organization selected on the previous page will appear.  If you want to see all the instructors for the organization, enter the percent sign (%) in one of the fields and select search.">First Name</label>
								<div class="field">
									<input type="text" name="searchFirstName" id="searchFirstName" cssClass="input-xxlarge" /> 
								</div>
							</div>
							<div class="field-wrap">
								<label class="no-asterisk" for="searchLastName" title="Enter the first few letters of the instructor's information.  Only instructors associated with the organization selected on the previous page will appear.  If you want to see all the instructors for the organization, enter the percent sign (%) in one of the fields and select search.">Last Name</label>
								<div class="field">
									<input type="text" name="searchLastName" id="searchLastName" cssClass="input-xxlarge" />
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
							<input name="searchAll" type="button" class="button" title="Only instructors associated with your organization will display. Call the TSC to add an instructor." value="Show All Instructors">	
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

<c:set var="addSessionWindowTitle" value="${offeringDetail.blended=='true' ? 'Add In-Person Skills Session' : 'Add Class Session'}"/>
<c:set var="addSessionWindowPageText" value="${offeringDetail.blended=='true' ? 'Enter the In-Person Skills session information below.' : 'Enter the class session information below.'}"/>
<c:set var="skill" value="${offeringDetail.blended=='true' ? 'In-Person Skills' : ''}"/>
<div id="addSessionDialog" class="window" title="<core:out value='${addSessionWindowTitle}'/>">
 	<script type="text/javascript" charset="utf-8">
 	
  		function getTime(hours, mins, ampm)
  		{
  			return hours + ":" + mins + " " + ampm;
  		}
  		
  		function get24Hrs(hours, ampm)
  		{
  			var hours24 = parseInt(hours);
  			if(ampm == "AM" && hours24 == 12)
  			{
  				return 0;
  			}
			if(ampm == "PM" && hours24 != 12)
  			{
				  return hours24 + 12;
  			}
  			return hours24;
  		}
  		
  		function get24HrTime(hours, mins, ampm)
  		{
  			var hrs24 = get24Hrs(hours,ampm);
  			if(hrs24 < 10)
  			{
  				return "0" + hrs24 + ":" + mins;
  			}
  			return hrs24 + ":" + mins; 
  		}
		  		
  		function getMinutesSinceMidnight(hours, minutes, ampm)
  		{
  			return (get24Hrs(hours, ampm)*60) + parseInt(minutes); 
  		}
  		
  		function isStartTimeAfterEndTime()
  		{
  			
			var startMinsSinceMidnight = getMinutesSinceMidnight($("#startTime1").val(), $("#startTime2").val(), $("#startTime3").val());
			var endMinsSinceMidnight = getMinutesSinceMidnight($("#endTime1").val(), $("#endTime2").val(), $("#endTime3").val());
			if(startMinsSinceMidnight >= endMinsSinceMidnight)
			{
				return true;
			}
			return false;
  		}
  		
  		function isSessionDateBeforeStDate()
  		{
  			var sessionDate = $("#date").val();
  			var stDate = $("#startDate").val();
  			if(isFormerAfterLater(stDate, sessionDate))
			{
				return true;
			} 
  		}
  		
  		function getSessionUniqueId()
  		{
  			var startTime = get24HrTime($("#startTime1").val(), $("#startTime2").val(), $("#startTime3").val());
  			var endTime = get24HrTime($("#endTime1").val(), $("#endTime2").val(), $("#endTime3").val());
  			var sessDate = $("#date").val();
  			return sessDate + startTime + endTime;
  		}
  		
		  
		$(document).ready(function() 
		{
  			//$('#addInstructorDialog').pressEnter(function(){ searchInstructor(); });
  			$('#addSessionError').hide();
			$("#addSessionBtn").click(function () {
				if( $("#date").val() == '')
				{
					$('#addSessionError').text("Please select the " + "<core:out value='${skill}'/>" + " Session date.");		
					$('#addSessionError').show();
					return;
				}
				
				if(!isDateFormatValid($("#date").val()))
				{
					$('#addSessionError').text("Invalid Date Format. Please correct and try again.");		
					$('#addSessionError').show();
					return;
				}
				
				if( isStartTimeAfterEndTime() )
				{
					$('#addSessionError').text("<core:out value='${skill}'/>" + " Session End Time must be after Session Start time. ");
					$('#addSessionError').show();
					return;
				}
				
				if( isSessionDateBeforeStDate() )
				{
					$('#addSessionError').text("<core:out value='${skill}'/>" + " Session Date cannot be before class start date.");
					$('#addSessionError').show();
					return;
				}
				
				var date =  $("#date").val();
				var startTime = getTime($("#startTime1").val(), $("#startTime2").val(), $("#startTime3").val());
				var endTime = getTime($("#endTime1").val(), $("#endTime2").val(), $("#endTime3").val());
				$("#addSessionDialog").dialog("close");
				var sessionUniqueId = getSessionUniqueId();
				
				refreshSessionArray("Add", {"uniqueId": sessionUniqueId, "date": date, "startTime": startTime, "endTime": endTime});
			});
			
			$("#closeSessionBtn").click(function() 
			{ 
			  	$("#addSessionDialog").dialog("close");
			});
  			
		});
		
	</script>
	
	<form name="addSessionForm" method="post">
		<section class="primary-content" style="width: 90%;margin-left: 15px;">
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>							
							<legend>
									<p><core:out value="${addSessionWindowPageText}"/></p>
							</legend>
							<div class="field-wrap">
								<span id="addSessionError" class="msgError" style="display: none;"></span>
							</div>
							<div class="field-wrap">
								<label class="no-asterisk" style="width: 200px;padding-right: 10px;"><core:out value="${skill}"/> Session Start Time</label>
								<div class="field">
									<select name="startTime1" id="startTime1">
										<option value="12">12</option>
										<option value="01">01</option>
										<option value="02">02</option>
										<option value="03">03</option>
										<option value="04">04</option>
										<option value="05">05</option>
										<option value="06">06</option>
										<option value="07">07</option>
										<option value="08">08</option>
										<option value="09">09</option>
										<option value="10">10</option>
										<option value="11">11</option>
									</select>
									<select name="startTime2" id="startTime2">
										<option value="00">00</option>
										<option value="15">15</option>
										<option value="30">30</option>
										<option value="45">45</option>
									</select>
									<select name="startTime3" id="startTime3" class="input-xtiny"><option value="AM">AM</option><option value="PM">PM</option></select>
								</div>
							</div>
							<div class="field-wrap">
								<label class="no-asterisk" style="width: 200px;padding-right: 10px;"><core:out value="${skill}"/> Session End Time</label>
								<div class="field">
									<select name="endTime1" id="endTime1">
										<option value="12">12</option>
										<option value="01">01</option>
										<option value="02">02</option>
										<option value="03">03</option>
										<option value="04">04</option>
										<option value="05">05</option>
										<option value="06">06</option>
										<option value="07">07</option>
										<option value="08">08</option>
										<option value="09">09</option>
										<option value="10">10</option>
										<option value="11">11</option>
									</select>
									<select name="endTime2" id="endTime2">
										<option value="00">00</option>
										<option value="15">15</option>
										<option value="30">30</option>
										<option value="45">45</option>
									</select>
									<select name="endTime3" id="endTime3"><option value="AM">AM</option><option value="PM">PM</option></select>
								</div>
							</div>
							<div class="field-wrap">
								<label class="no-asterisk" for="date" style="width: 200px;padding-right: 10px;"><core:out value="${skill}"/> Session Date</label>
								<div class="field">
									<input type="text" name="date" id="date" class="input-medium" /> 
									<a id="showSessionsCalendar" title="Click to view calendar" href="javascript:void(0)"><span class="ui-icon ui-icon-calendar" style="display:inline-block"></span></a>
								</div>
							</div>
						</fieldset>
					</div>
				</div>
				<footer class="form-action clearfix">
					<div class="action">
						<div class="bw">
							<input name="addSessionBtn" id="addSessionBtn" type="button" class="button" value="Add Session">	
						</div>
					</div>
					<div class="action plain-button alt-button">
						<div class="bw">
							<input name="closeSessionBtn" id="closeSessionBtn" type="button" class="button" value="Close">	
						</div>
					</div>
				</footer>
		</section>
	</form>
</div>

</body>
</html>
