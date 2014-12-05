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
	</style> 
  	
  	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/jquery.dataTables.min.js"></script>
  	<script src="../js/jquery.jeditable.mini.js"></script>
  	<script src="../js/instructor.js"></script>
  	
	<script type="text/javascript" charset="utf-8">
	
		var oTable;
		var giRedraw = false;
		
		var $ = jQuery.noConflict(true);
		
		$(function( $ ) {
			
	  	});
		
 		
 		function getOfferingList() {
		    var url = "/instructor/offering/getOfferingList.html?type=json";
		    return $.ajax(url, {
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

	  	
		$(document).ready(function( $ ) {
		
			$( document ).tooltip();

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
			<p class="light-grey-out"> Welcome <span class="grey-out"><core:out value="${user.username}" /></span>; Click here to <a href="<core:url value="/authentication/login.html?feeOption=Scheduling"/>" >logout.</a></p>
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
		            <li class="nav-item current">
		                <a class="nav-link" href="<core:url value="/offering/scheduledOffering.html"/>">
		                <span>Create new ILT/Blended Offering</span>
		                </a>
		            </li>
		            <li class="nav-item">
		                <a class="nav-link" href="<core:url value="/offering/onlineOffering.html"/>">
		                <span>Create new Online Offering</span>
		                </a>
		            </li>
		        </ul>
		        <div class="sidebar-module-wrap"></div>
		    </nav>
		</aside>
		
		<section class="primary-content">
			<header class="primary-header">
				<h1>To be constructed..</h1>
			</header>
			<div style="color: #6D6E70;">
				<p>This page is under construction ...</p>
			</div>
		</section>
	</div>
</div>
</body>
</html>
