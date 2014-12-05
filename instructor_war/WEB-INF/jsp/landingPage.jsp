<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
	<title>American Red Cross | Course Records</title>
	<link rel="shortcut icon" href="../images/favicon.ico">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/base.min.css">
	<link rel="stylesheet" href="../css/jquery-ui-1.10.3.custom.css" />
	
	
	<style type="text/css">
		
		tbody td, tbody th {
		  border-color: #ffffff;
		  border-style: solid;
		  border-width: 0px;
		}
		
		.content.student-page {
    		min-height: 500px;
		}
		
	</style>
	
	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>

	
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
	</header>
</div>
<div class="left-rail content-wrap">
	<div class="content student-page" style="min-height: 400px;">
		<!--
		<aside class="sidebar" role="complementary">
		    <nav class="sidebar-inner">
		    </nav>
		</aside>
		-->
		<div style="position: relative; top: 50px; width:800px; margin-left:auto; margin-right:auto;">
		<!--
		<header class="primary-header">
			<h1>Submit Course Records</h1>
		</header>
		-->
		<div style="color: #6D6E70;">
			<!--
			<p>Please enter your course information here. Once you have entered this information, your students are eligible to obtain proof of certification, including digital certificates.</p>
			-->
		</div>
		<table align="center" style="width: 100%">
			<tr>
				<td style="width:375px">
				<div style="width:275px; margin-left:auto; margin-right:auto;">
				<section class="image-topic-box">
					<div class="box-image-wrap clearfix">
						<a href="<core:url value="/authentication/login.html?feeOption=Fee"/>"><img class="box-image"  src="../images/CPR-Class.jpg"  alt="Red Cross Fee based course"/></a>		
					</div>
					<header class="box-header">
						<h1 class="arrow-item first">
							<a href="<core:url value="/authentication/login.html?feeOption=Fee"/>" class="arrow">Enter or edit records for courses with fees.</a>                	
						</h1>
					</header>
				</section>
				<table width="100%">
				<tr>
					<td>
					<div style="color: #6D6E70;">
						<div style="position: relative;left: 25px;">
							<span>Examples of these courses include:</span>
							<ul class="bullet-list" style="padding: 10px;">
								<li>CPR/AED and First Aid</li>
								<li>Lifeguarding</li>
								<li>Babysitter's Training</li>
							</ul> 
						</div>
					</div>
					</td>
				</tr>
				</table>
				</div>
				</td>
				
				<td style="width:375px">
				<div style="width:275px; margin-left:auto; margin-right:auto;">
				<section class="image-topic-box">
					<div class="box-image-wrap clearfix">
						<a href="<core:url value="/authentication/login.html?feeOption=Non-fee"/>"><img class="box-image" src="../images/Swim-Class.jpg"  alt="Red Cross Non-fee based course"/></a>		
					</div>
					<header class="box-header">
						<h1 class="arrow-item first">
				                <a href="<core:url value="/authentication/login.html?feeOption=Non-fee"/>"  class="arrow">Enter records for courses without fees.</a>
						</h1>					
					</header>
				</section>
				<table width="100%">
				<tr>
					<td>
					<div style="color: #6D6E70;">
						<div style="position: relative;left: 25px;">
							<span>Examples of these courses include:</span>
							<ul class="bullet-list" style="padding: 10px;">
								<li>Preparedness Presentations</li>
								<li>Learn-To-Swim</li>
							</ul> 
						</div>
					</div>
					</td>
				</tr>
				</table>
				</div>
				</td>
				
				<td style="width:375px">
                <div style="width:295px; margin-left:auto; margin-right:auto;">
                <section class="image-topic-box">
                    <div class="box-image-wrap clearfix">
                        <a href="<core:url value="/authentication/login.html?feeOption=Facility-fee"/>"><img class="box-image" src="../images/Facility-Fee.jpg"  alt="LTS Facility Fee"/></a>        
                    </div>
                    <header class="box-header">
                        <h1 class="arrow-item first">
                                <a href="<core:url value="/authentication/login.html?feeOption=Facility-fee"/>"  class="arrow">Enroll for 2014 and pay your Learn-to-Swim facility fee.</a>
                        </h1>
                    </header>
                </section>
                <table width="100%">
                <tr>
                    <td>
                    <div style="color: #6D6E70;">
                        <div style="position: relative;left: 25px;">
                            <span>Please use this option if you:</span>
                            <ul class="bullet-list" style="padding: 10px;">
                                <li>Were enrolled for 2013</li>
                                <li>Are completing the process for 2014</li>
                                <li style="margin-top:25px;">If you're starting the enrollment process for the first time, <a href="<core:url value="http://www.redcross.org/LTSEnroll"/>">get started now</a></li>
                            </ul> 
                        </div>
                    </div>
                    </td>
                </tr>
                </table>
                </div>
                </td>
			</tr>
		</table>
		</div>
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
</body>
</html>