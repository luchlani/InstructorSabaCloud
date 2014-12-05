<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@ page import="com.arc.instructor.controller.OfferingController" %>
<%
response.setHeader("Cache-Control","no-cache no-store"); 
response.setHeader("Cache-Control", "must-revalidate");
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0); 
%>
<!DOCTYPE html>
<html>
<head>
	<title>American Red Cross | Scheduling Interface</title>
	
	<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
	<meta http-equiv="EXPIRES" content="0">
	
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="shortcut icon" href="../images/favicon.ico">
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/jquery.dataTables_themeroller.css">
	<link rel="stylesheet" href="../css/jquery.dataTables.css">
	<link rel="stylesheet" href="../css/jquery-ui-1.10.3.custom.css" />
	
	<style type="text/css">
	
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
		
		.simple-form.tabular-form label{
			padding-right: 30px;
			width: 100px;
		}
		
		.field-wrap{
			zoom: 1;
			margin: 0px 0px 5px;
		}
	
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
		
		table.dataTable td {
		    padding: 3px 0;
		}
		
		select[name="value"] { width: 90px; }
		
		.dataTables_paginate .ui-state-disabled {
     		 display:none;
		}
		
		tbody td, tbody th {
		  border-color: #ffffff;
		  border-style: solid;
		  border-width: 0px;
		}
		
		.input-medium {
		    width: 100%;
		}
		
		.input-tiny {
		    width: 100%;
		}
		
		.error {
			border:1px solid red;
		}
		
	</style>
	
	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/jquery.dataTables.min.js"></script>
  	<script src="../js/jquery.jeditable.mini.js"></script>
  	<script src="../js/offering.js"></script>
  	
  	<script type="text/javascript" charset="utf-8">
 	 	var oTable;
 	 	
 	 	var rosterArray = new Array();
 	 	var columnArray = new Array();
  	 	
  		$(document).ready(function() {
  		
  			$.ajaxSetup({ cache: false });
  			
  		 	$( document ).tooltip();	
			
			<core:if test="${!offeringDetail.online and offeringDetail.status=='100' and offeringDetail.studCount > 0}">
				$("#SaveRoster").click(function(){
					$("#offeringAction").val("SaveRoster");
					centerModal(); 
					document.rosterForm.submit();	
	 			});
 			</core:if>
 			
 			<core:if test="${offeringDetail.canDeliver}">
				$("#MarkDelivered").click(function(){
					var message = "You must mark completion status of all students prior to closing out this class. Once you close a class, you will not be able to make any changes. Do you want to close out this class?";
					$("#confirmText").text(message);
					$("#confirmDialog").dialog({
						autoOpen: true,
						width: 330,
						modal: true,
						position: {my: "center", at: "center", of: window},
						buttons: { 
							"OK": function(){
								$(this).dialog("close");
								$("#offeringAction").val("MarkDelivered");
								centerModal(); 
								document.rosterForm.submit();
							},
							"Cancel": function(){
								$(this).dialog("close");
							} 
						} 
					});
	 			});
 			</core:if>
 			
 			$("#EditOffering").click(function(){ 
				<core:choose>
					<core:when test="${offeringDetail.online}">
						document.location.href = "/instructor/offering/onlineOffering.html?action=GetDetail&offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
					</core:when>
					<core:otherwise>
						document.location.href = "/instructor/offering/scheduledOffering.html?action=GetDetail&offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
					</core:otherwise>
				</core:choose>				
			});
			
			$("#certificates").click(function(){
				document.location.href = "/instructor/offering/certificates.html?offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
			});
			
			$("#Close").click(function(){ 
				document.location.href = "/instructor/offering/offeringSearch.html";
			});
 			
 			rosterArray = ${javaRosterArray};
 			
 			columnArray = ${javaCompHeaderArray};
 			
			for(i=0 ; i < columnArray.length ; i++)
			{
				$("#studentTableColumns").append('<th>' + columnArray[i].sTitle + '</th>')
			}
			
 			
			oTable = $("#rosterTable").dataTable({ 
				"oLanguage": {
					"sEmptyTable": "No students registered in the class."
				},
				"bPaginate": false,
				"bLengthChange": false,
        		"bSort": false,
			    "bJQueryUI": true,
		        "bFilter": false,
		        "aoColumns": columnArray,
				"aaData": rosterArray
			});
			
			<core:if test="${offeringDetail.sabaMessage=='Success'}">
				alertDialog("Completion results saved successfully.");
			</core:if>
		  	
    		

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
		
		<section id="primary-content" class="primary-content">
			<header class="primary-header">
				<h1>Manage Students</h1>
			</header>
			<div style="color: #6D6E70;">
				<p style="margin: 5px;">This page can be used to:
					<ul class="bullet-list" style="padding-left: 20px;padding-bottom: 10px;">
						<core:if test="${offeringDetail.blended=='true'}">
							<li>Monitor online content completion of students</li>
						</core:if>
						<li>Mark course completion</li>
						<li>Close out class</li>
					</ul>
				</p>
			</div>
			<core:if test="${offeringDetail.sabaMessage != null && offeringDetail.sabaMessage!='Success'}">
				<div class="field-wrap">
					<span id="sabaMessage" class="msgError"><core:out value="${offeringDetail.sabaMessage}"/></span>
				</div>
			</core:if>
				<header class="form-header">
					<h1>Class Information</h1>
				</header>
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>
							<div class="field-wrap min-fifty-percent">
								<label for="" class="grey-out no-asterisk">Course:</label>
								<div class="field">
									<span><core:out value="${offeringDetail.courseName}" /></span>
								</div>
							</div>
							
							<div class="field-wrap min-fifty-percent" style="margin-right: 35px;">
								<label for="" class="grey-out no-asterisk">Delivery Type:</label>
								<div class="field">
									<span><core:out value="${offeringDetail.deliveryTypeName}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Offering ID:</label>
								<div class="field">
									<span><core:out value="${offeringDetail.offeringNo}" /></span>
								</div>
							</div>
							<div class="field-wrap min-fifty-percent" style="margin-right: 35px;">
								<label for="" class="grey-out no-asterisk" >Class Dates:</label>
								<div class="field">
									<span><core:out value="${offeringDetail.startDate} - ${offeringDetail.endDate}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk" >Contact:</label>
								<div class="field">
									<span><core:out value="${offeringDetail.contactUsername}" /></span>
								</div>
							</div>
							<div class="field-wrap min-fifty-percent" style="margin-right: 35px;">
								<label for="" class="grey-out no-asterisk">Enrollment Count:</label>
								<div class="field">
									<span><core:out value="${offeringDetail.studCount} of ${offeringDetail.maxCount} students registered" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Organization:</label>
								<div class="field">
									<span><core:out value="${offeringDetail.orgName}" /></span>
								</div>
							</div>
							<core:if test="${offeringDetail.status=='200'}">
								<div class="field-wrap">
									<label for="" class="grey-out no-asterisk">Class Status:</label>
									<div class="field">
										<span>Delivered</span>
									</div>
								</div>
							</core:if>
											
						</fieldset>
					</div>
				</div>
		</section>
		<form name="rosterForm" action="/instructor/offering/roster.html" method="POST">
			<section class="primary-content">
				<header class="form-header" title="List of students registered in the class. Students who cancelled are not listed here.">
					<h1>List of Students  </h1>
				</header>
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>	
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="rosterTable">
							<thead>
								<tr id="studentTableColumns">
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
						</fieldset>
					</div>
				</div>
			</section>
		<section class="primary-content">
				<footer class="form-action clearfix">
					<core:if test="${!offeringDetail.online and offeringDetail.status=='100' and offeringDetail.studCount > 0}">
						<div class="action">
							<div class="bw">
								<input id="SaveRoster" type="button" class="button" name="next" value="Save">	
							</div>
						</div>
					</core:if>
					
					<div class="action plain-button alt-button">
						<div class="bw">
							<input id="EditOffering" type="button" class="button" name="back" value="Edit Class">	
						</div>
					</div>
					<div class="action plain-button alt-button">
						<div class="bw">
							<input id="certificates" type="button" class="button" name="certificates" value="Print Certificates">	
						</div>
					</div>
					
					<core:if test="${offeringDetail.canDeliver}">
						<div class="action plain-button alt-button">
							<div class="bw">
								<input id="MarkDelivered" type="button" class="button" name="deliver" value="Close out Completed Class">	
							</div>
						</div>
					</core:if>

					<div class="action plain-button alt-button" title="Exit to return to main screen.">
						<div class="bw">
							<input id="Close" type="button" class="button" name="close" value="Exit">	
						</div>
					</div>
				</footer>
		</section>
		<input name="offeringId" type="hidden" value="<core:out value='${param.offeringId}'/>">
		<input id="offeringAction" name="offeringAction" type="hidden" value=""/>
		</form>
	</div>
</div>
<%@ include file="footer.jsp" %>
<div id="overlay"><div><h4 class="grey-out">Processing ...</h4></div></div>
<div id="alertDialog" class="window" title="Attention">
	<table align="center" width="85%">
	<tr>
	<td>
		<p id="alertText"></p>
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
</body>
</html>