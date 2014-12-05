<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

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
  	
  	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/jquery.dataTables.min.js"></script>
  	<script src="../js/jquery.jeditable.mini.js"></script>
  	<script src="../js/offering.js"></script>
  	
	<script type="text/javascript" charset="utf-8">
	
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
  	 		
  	 		//Validate Avail From
  	 		var availFrom = $("#availFrom").val();
			if(availFrom == '')
  	 		{
  	 			alertDialog("Please select an Available From");
  	 			return false;
  	 		}
  	 		var isValid = validateDateFormat(availFrom, "Invalid Date Format for Available From. Please correct and try again.");
  	 		if(!isValid)
  	 		{
  	 			return false;
  	 		}
  	 		
  	 		//Validate Disc From and if Avail > Disc
  	 		var discFrom = $("#discFrom").val();
  	 		if(discFrom != '')
  	 		{
  	 			isValid = validateDateFormat(discFrom, "Invalid Date Format for Discontinued From. Please correct and try again.");
	  	 		if(!isValid)
	  	 		{
	  	 			return false;
	  	 		}
	  	 		
	  	 		if(availFrom == discFrom || isFormerAfterLater(availFrom, discFrom))
	  	 		{
	  	 			alertDialog("Available From must be before Discontinued From");
	  	 			return false;
	  	 		}
  	 		}
  	 		
  	 		return true;
  	 	}

		$(document).ready(function( $ ) 
		{
			$( document ).tooltip();
			
			if(isNew)
			{	
				getCourseListForOnline();
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
				
				// disable Enter key on course name autocomplete field (fee and non-fee)
				$("#courseName").keypress(function(event){
					var keycode = (event.keyCode ? event.keyCode : event.which);
					if (keycode == '13') {
						event.preventDefault();
						event.stopPropagation();    
					}
				});
			}
			
			//Start Date Calender widget
			$("#availFrom").datepicker();
			$("#showCalendarAvailFrom").click(function() {
 				 $('#availFrom').datepicker('show');
 			});
 			
 			$("#discFrom").datepicker();
			$("#showCalendarDiscFrom").click(function() {
 				 $('#discFrom').datepicker('show');
 			});
 			
 			$("#orgId").change(function(){
				if(	$("#poId").val() != '' )
				{
						var message = "Changing the organization will remove the purchase order. Do you want to continue?";
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
									$("#poId").val('');
									setPurchaseOrderList($("#orgId").val());
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
				setPurchaseOrderList($(this).val());
				gOrgId = $(this).val();
			});
			
			<core:if test="${offeringDetail.sabaOfferingId!=null}">
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
			
			$("#save").click(function(){
				if(!validateData()) return false;
				centerModal();
				document.offeringDetail.submit();
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
		            <li class="nav-item">
		                <a class="nav-link" href="<core:url value="/offering/offeringSearch.html"/>">
		                <span>Manage Offerings</span>
		                </a>
		            </li>
		            <li class="nav-item">
		                <a class="nav-link" href="<core:url value="/offering/scheduledOffering.html"/>">
		                <span>Create new ILT/Blended Offering</span>
		                </a>
		            </li>
		            <li class="nav-item current">
		                <a class="nav-link" href="<core:url value="/offering/onlineOffering.html"/>">
		                <span>Create new Online Offering</span>
		                </a>
		            </li>
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
							<div class="field light-grey-out">
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
							<div class="field light-grey-out">
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

							<!-- Organization -->
							<div class="field-wrap">
						    	<label for="organizationName">Organization:</label>
								<form:select items="${organizationList}" path="orgId" itemValue="key" itemLabel="value" />
							</div>

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
									<span id="deliveryType">Online</span>
								</div>
							</div> -->
							
							<!-- Available From -->
							<div class="field-wrap">
								<label for="startDate">Available From:</label>
								<div class="field">
									<form:input path="availFrom" cssClass="input-medium" /> 
								</div>
								<a id="showCalendarAvailFrom" title="Click to view calendar" href="javascript:void(0)"><span class="ui-icon ui-icon-calendar" style="display:inline-block"></span></a>
							</div>

							<!-- Discontinued From -->
							<div class="field-wrap">
								<label class="grey-out no-asterisk" for="startDate">Discontinued From:</label>
								<div class="field">
									<form:input path="discFrom" cssClass="input-medium" /> 
								</div>
								<a id="showCalendarDiscFrom" title="Click to view calendar" href="javascript:void(0)"><span class="ui-icon ui-icon-calendar" style="display:inline-block"></span></a>
							</div>
							
							<!-- Price -->
							<div class="field-wrap">
								<label class="grey-out no-asterisk" for="price">Price:</label>
								<div class="field">
									<span id="price">
										<core:if test="${offeringDetail.sabaOfferingId != null || offeringDetail.sabaMessage != null}">
											<core:out value="${offeringDetail.price}"/>
										</core:if>
									</span>
								</div>
							</div>
							
							<!-- Purchase Orders -->
							<div class="field-wrap">
								<label class="optional" for="facility">Purchase Order: </label>
								<div class="field">
									<form:select items="${purchaseOrderList}" path="poId" itemValue="key" itemLabel="value" />
								</div>
							</div>
							
							<!-- Deeplink -->
							<core:if test="${offeringDetail.sabaOfferingId != null}">
								<div class="field-wrap">
									<label class="grey-out no-asterisk" for="deeplink">Registration Deeplink:</label>
									<div class="field">
										<span id="deeplink"><core:out value="${offeringDetail.deeplink}" /></span>
									</div>
								</div>
							</core:if>
						</fieldset>
					</div>
				</div>
		</section>
		
		<!-- Buttons -->
		<section class="primary-content">
				<footer class="form-action clearfix">
					<div class="action">
						<div class="bw">
							<input id="save" type="button" class="button" name="Save" value="Save">	
						</div>
					</div>

					<core:if test="${offeringDetail.sabaOfferingId!=null}">
						<div class="action plain-button alt-button">
							<div class="bw">
								<input id="roster" type="button" class="button" name="roster" value="Roster">	
							</div>
						</div>
						
						<div class="action plain-button alt-button">
							<div class="bw">
								<input id="certificates" type="button" class="button" name="certificates" value="Certificates">	
							</div>
						</div>
					</core:if>

					<div class="action plain-button alt-button">
						<div class="bw">
							<input id="close" type="button" class="button" name="close" value="Close">	
						</div>
					</div>
				</footer>
		</section>
	</div>
	<form:hidden path="offeringAction"/>
	<form:hidden path="sabaOfferingId"/>
	</form:form>
</div>

<%@ include file="footer.jsp" %>
<div id="overlay"><div><h4 class="grey-out">Processing ...</h4></div></div>
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

</body>
</html>
