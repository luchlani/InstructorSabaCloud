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
				document.location.href = "/instructor/offering/offeringSearch.html";
			});
			
			$("#back").click(function(){
				history.back();
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
				<h1>Error Encountered</h1>
			</header>
			<div class="simple-form tabular-form">
				<div class="fieldset-group">
					<fieldset>
						<legend>
							<p><core:out value="${errLabel}"/></p>
							<p style="color:red;font-weight:bold;"><core:out value="${errMsg}"/></p>
							<br/>
							<br/>
							<br/>
							<p>Click Back and try again or contact 1-800-REDCROSS for assistance.</p>
						</legend>
					</fieldset>
				</div>
			</div>
			<footer class="form-action clearfix">
				<div class="action plain-button alt-button">
					<div class="bw">
						<input id="back" type="button" class="button" name="" value="Back">	
					</div>
				</div>
				<div class="action plain-button alt-button" title="Exit to return to main screen.">
					<div class="bw">
						<input id="close" type="button" class="button" name="close" value="Exit">	
					</div>
				</div>
			</footer>
		</section>
	</div>
</div>
<%@ include file="footer.jsp" %>
</body>
</html>