<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@ page import="com.arc.instructor.controller.OfferingController" %>
<%
response.setHeader("Cache-Control","no-cache no-store"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0); 
%>
<!DOCTYPE html>
<html>
<head>
	<title>American Red Cross | Scheduling Interface</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
	<meta http-equiv="EXPIRES" content="0">

	<link rel="shortcut icon" href="../images/favicon.ico">
	<link rel="stylesheet" href="../css/main.css">
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
	</style> 
	
	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/instructor.js"></script>	
  	
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			
			$( document ).tooltip();
			
			$("#close").click(function(){
				$("#offeringAction").val("Close");
				document.offeringDetail.submit();
			});
			
			$("#edit").click(function(){
				<core:choose>
					<core:when test="${offeringDetail.online}">
						document.location.href = "/instructor/offering/onlineOffering.html?action=GetDetail&offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
					</core:when>
					<core:otherwise>
						document.location.href = "/instructor/offering/scheduledOffering.html?action=GetDetail&offeringId=" + "<core:out value='${offeringDetail.sabaOfferingId}'/>";
					</core:otherwise>
				</core:choose>	
			});
	  	});

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
		
		<section class="primary-content" role="main">
			<header class="primary-header">
				<h1>Confirmation</h1>
			</header>
			<form:form method="POST" name="offeringDetail" commandName="offeringDetail">
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<div class="field-wrap">
							<form:hidden path="sabaMessage" />
							<form:errors path="sabaMessage" cssClass="msgError" />
						</div>	
						<fieldset>
							<legend>
								<c:set var="blendedText" value="${offeringDetail.blended=='true' ? ' and complete the online session' : ''}"/>
								<p style="font-size: 16px">
									Thank you for scheduling a class. Send the registration link below to class participants so they can register <core:out value="${blendedText}"/>.
								</p>
								<br/><br/>
								<div class="field-wrap">
									<label class="grey-out no-asterisk" for="deeplink">Offering No:</label>
									<div class="field">
										<span id="deeplink"><core:out value="${offeringDetail.offeringNo}" /></span>
									</div>
								</div>
								<div class="field-wrap">
									<label class="grey-out no-asterisk" for="deeplink">Student Registration Link:</label>
									<div class="field">
										<span id="deeplink"><core:out value="${offeringDetail.deeplink}" /></span>
									</div>
								</div>
								
								
								<!--<p>Offering No: <core:out value="${offeringDetail.offeringNo}" /></p>
								<p>Student Registration Link: <core:out value="${offeringDetail.deeplink}" /></p>-->
							</legend>
						</fieldset>
					</div>
				</div>
				<footer class="form-action clearfix">
					<div class="action plain-button alt-button" title="Make edits to the Class information">
						<div class="bw">
							<input id="edit" type="button" class="button" name="" value="Edit Class">	
						</div>
					</div>
					<div class="action plain-button alt-button" title="Exit to return to main screen.">
						<div class="bw">
							<input id="close" type="button" class="button" name="close" value="Exit">	
						</div>
					</div>
				</footer>
				<form:hidden path="offeringAction"/>
			</form:form>
		</section>
	</div>
</div>
<%@ include file="footer.jsp" %>
</body>
</html>