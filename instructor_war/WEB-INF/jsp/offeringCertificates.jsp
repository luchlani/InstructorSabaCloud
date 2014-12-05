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
		
		table.dataTable td a{
		    font-size: 90%;
		    font-weight: bold;
		    text-decoration:underline;
		}
		
		table.dataTable td a:hover{
		    color: #000000;
		}
		
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
  	<script src="../js/offering.js"></script>
  	
  	<script type="text/javascript" charset="utf-8">
 	 	var oTable;
 	 	
 	 	var studentArray = new Array();
 	 	
 	 	function showCertificates(deeplink, param)
 	 	{
			var certificatesURL = window.location.protocol.toString() + "//" + window.location.host.toString() + "/Saba/Web/Main/goto/" + deeplink + "?" + param;
			window.open(certificatesURL,deeplink,"location=no,top=0,left=0,height=768,width=1024,scrollbars=yes,resizable=yes");
 	 	}
  	 	
  		$(document).ready(function() {
  		
  			$.ajaxSetup({ cache: false });
  			
  		 	$( document ).tooltip();	
			
			$("#PrintAll8511").click(function(){ 
				showCertificates("FullCertificate", "offeringId=${offeringDetail.sabaOfferingId}");
 			});
 			
			$("#PrintAllWallet").click(function(){ 
				showCertificates("WalletCertificate", "offeringId=${offeringDetail.sabaOfferingId}");	
 			});

 			
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
			
			$("#roster").click(function(){
				document.location.href = "/instructor/offering/roster.html?offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
			});
			
			$("#Close").click(function(){ 
				document.location.href = "/instructor/offering/offeringSearch.html";
			});
 			
 			studentArray = ${javaStudentArray};
 			
			oTable = $("#studentTable").dataTable({ 
				"oLanguage": {
					"sEmptyTable": "No students have completed this course successfully."
				},
				"bPaginate": false,
				"bLengthChange": false,
        		"bSort": false,
			    "bJQueryUI": true,
		        "bFilter": false,
		        "aoColumns": [ 
                               { "mData": "fullname", "sName": "fullname", "sTitle": "Name" },  
							   { "mData": "username", "sName": "username", "sTitle": "Username" }, 
							   { "mData": "fullCertificate", "sName": "fullCertificate", "sTitle": "8.5 x 11 Certificate"},
							   { "mData": "walletCertificate", "sName": "walletCertificate", "sTitle": "Wallet-sized Certificates"}
        					],
				"aaData": studentArray
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
		
		<section id="primary-content" class="primary-content">
			<header class="primary-header">
				<h1>Print Certificates</h1>
			</header>
			
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
							<label for="" class="grey-out no-asterisk">Student Count:</label>
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
		<section class="primary-content">
			<header class="form-header" title="List of students who completed atleast one component successfully in this class.">
				<h1>List of Students  </h1>
			</header>
			<div class="simple-form tabular-form">
				<div class="fieldset-group">
					<fieldset>	
					<table cellpadding="0" cellspacing="0" border="0" class="display" id="studentTable">
						<thead>
							<tr>
								<th>Name</th>
								<th>Username</th>
								<th>8.5 x 11 Certificate</th>
								<th>Wallet Certificate</th>
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
			
				<core:if test="${!empty javaStudentArray}">
					<footer class="form-action clearfix">
						<div class="action">
							<div class="bw">
								<input id="PrintAll8511" type="button" class="button" name="PrintAll8511" value="Print 8.5 x 11 Certificates for All">	
							</div>
						</div>
						<div class="action">
							<div class="bw">
								<input id="PrintAllWallet" type="button" class="button" name="PrintAllWallet" value="Print Wallet Certificates for All">	
							</div>
						</div>
					</footer>
					<br/>
				</core:if>
			
			<footer class="form-action clearfix">
				<div class="action plain-button alt-button">
					<div class="bw">
						<input id="EditOffering" type="button" class="button" name="back" value="Edit Class">	
					</div>
				</div>
				<div class="action plain-button alt-button">
					<div class="bw">
						<input id="roster" type="button" class="button" name="roster" value="Manage Students">	
					</div>
				</div>
				<div class="action plain-button alt-button" title="Exit to return to main screen.">
					<div class="bw">
						<input id="Close" type="button" class="button" name="close" value="Exit">	
					</div>
				</div>
			</footer>
		</section>
		<input name="offeringId" type="hidden" value="<core:out value='${param.offeringId}'/>">
		</form>
	</div>
</div>
<%@ include file="footer.jsp" %>
</body>
</html>