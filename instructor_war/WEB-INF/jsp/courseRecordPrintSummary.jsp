<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
	<title>American Red Cross | Course Records</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="shortcut icon" href="../images/favicon.ico">
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/jquery-ui-1.10.3.custom.css" />
	
	<style type="text/css">
	     @media print {
	        .hideWhilePrinting { display:none; }
	         
		    @page PrintSection {
				size:8.27in 11.69in; 
				margin:.5in .5in .5in .5in; 
				mso-header-margin:.5in; 
				mso-footer-margin:.5in; 
				mso-paper-source:0;
			}
			
			.PrintSection {
				page:PrintSection;
			} 
			
			.pageBreakAfter { display: block; page-break-after:always; page-break-inside: avoid;}
			
			.pageBreakBefore {  display: block; page-break-before:always; page-break-inside: avoid;}

		
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

	</style> 

 
	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/instructor.js"></script>
  	
  	<script type="text/javascript" charset="utf-8">
  	
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
<body class="print">
<div id="hdr" class="print-hdr">
	<header class="page-header" role="banner">
		<a href="//www.redcross.org" class="branding" target="_blank">
			<img class="logo" border=0 src="../images/redcross-logo.png" alt="American Red Cross">
		</a>
	</header>
</div>
<div class="content-wrap">
	<div class="content student-page"> 
		<section class="primary-content" style="width: 868px;">
				<header class="primary-header">
				<core:if test="${courseRecord.feeOption=='Non-fee'}" >
				<h1 style="font-size: 30px;line-height: 35px;padding-bottom: 20px;">Course Record Summary</h1>
				</core:if>
				<core:if test="${courseRecord.feeOption=='Fee'}" >
				<h1 style="font-size: 30px;line-height: 35px;padding-bottom: 20px;">Course Record Summary</h1>
				</core:if>
				</header>
			<form method="post">
			
			<core:if test="${courseRecord.feeOption=='Non-fee'}" >
			<core:forEach  var="courseRecord" items="${courseRecordList.courseRecordList}" varStatus="rowCount">
				<core:choose>
					<core:when test="${rowCount.last}">
						<div>
					</core:when>
					<core:otherwise>
						<div class="pageBreakAfter">
					</core:otherwise>
				</core:choose>
				<div class="corners PrintSection">
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>
							<div class="field-wrap min-fifty-percent" style="margin-right: 35px;">
								<label for="" class="grey-out no-asterisk" style="width: auto; padding-right: 20px;">Contact:</label>
								<div class="field light-grey-out">
									<span><core:out value="${username}" /></span>
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
						<fieldset>
							<div class="field-wrap">
								<div class="hr" />
							</div>
						</fieldset>
						<fieldset>							
							<div class="field-wrap">
								<c:set var="now" value="<%=new java.util.Date()%>" />
								<label for="" class="grey-out no-asterisk">Date Submitted:</label>
								<div class="field  light-grey-out">
									<span><fmt:formatDate pattern="MM/dd/yyyy" value="${now}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Organization:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.organizationName}" /></span>
								</div>
							</div>
							<div class="field-wrap">
                                <label for="" class="grey-out no-asterisk">Course:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.courseName}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Course Ending Date:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.endDate}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Total Students:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.totalStudents}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Training Center Name:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.trainingCenterName}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Street Address:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.streetAddress}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="city" class="grey-out no-asterisk">City:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.city}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">State:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.state}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Postal Code:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.zipCode}" /></span>
								</div>
							</div>
						</fieldset>
					</div>
					
					<div class="fieldset-group">
						<fieldset>
					
						<header class="simple-header">
							<h2 class="grey-out">Course Record Instructor(s)</h2>
						</header>
						<div class="standard-table course-table catalog-list-table">
							<table class="">
							   <thead>
							      <tr>
							         <th class="" scope="col"><span class="col-header">First Name</span></th>
							         <th class="" scope="col"><span class="col-header">Last Name</span></th>
							      </tr>
							   </thead>
							   <tbody>
								<core:forEach items="${courseRecord.instructors}" var="instructor">    
								  <tr class="light-grey-out">
								    <td><core:out value="${instructor.firstName}" /></td>
								    <td><core:out value="${instructor.lastName}" /></td>
								  </tr>
								</core:forEach>
							   </tbody>
							</table>
						</div>
					
						</fieldset>
					</div>
				</div>
			</div>
			</core:forEach>
			</core:if>
			
			<core:if test="${courseRecord.feeOption=='Fee' || courseRecord.feeOption=='Facility-fee'}" >
				<div class="corners PrintSection">
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
						<fieldset>
							<div class="field-wrap">
								<div class="hr" />
							</div>
						</fieldset>
						<fieldset>							
							<div class="field-wrap">
								<c:set var="now" value="<%=new java.util.Date()%>" />
								<label for="" class="grey-out no-asterisk">Date Submitted:</label>
								<div class="field  light-grey-out">
									<span><fmt:formatDate pattern="MM/dd/yyyy" value="${now}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Organization:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.organizationName}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                                <label for="" class="grey-out no-asterisk">LTS Program Category:</label>
                                </core:if>
                                <core:if test="${courseRecord.feeOption!='Facility-fee'}" >
                                <label for="" class="grey-out no-asterisk">Course:</label>
                                </core:if>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.courseName}" /></span>
								</div>
							</div>
							<core:if test="${courseRecord.feeOption!='Facility-fee'}" >
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Course Ending Date:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.endDate}" /></span>
								</div>
							</div>
							</core:if>
							<core:if test="${courseRecord.feeOption!='Facility-fee'}" >
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Total Students:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.totalStudents}" /></span>
								</div>
							</div>
							</core:if>
							<core:if test="${courseRecord.feeOption=='Fee'}" >
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Training Center Name:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.trainingCenterName}" /></span>
								</div>
							</div>
							</core:if>
							<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                            <div class="field-wrap">
                                <label for="" class="grey-out no-asterisk">Mailing Address Name:</label>
                                <div class="field light-grey-out">
                                    <span><core:out value="${courseRecord.trainingCenterName}" /></span>
                                </div>
                            </div>
                            </core:if>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Street Address:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.streetAddress}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="city" class="grey-out no-asterisk">City:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.city}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">State:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.state}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Postal Code:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.zipCode}" /></span>
								</div>
							</div>
							<core:if test="${courseRecord.feeOption!='Facility-fee'}" >
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Comments:</label>
								<div class="field light-grey-out">
									<span style="white-space: pre-line"><core:out value="${courseRecord.comments}" /></span>
								</div>
							</div>
							</core:if>
						</fieldset>
					</div>
					
					<core:if test="${courseRecord.feeOption!='Facility-fee'}" >
					<div class="fieldset-group">
						<fieldset>
					
						<header class="simple-header">
							<h2 class="grey-out">Course Record Instructor(s)</h2>
						</header>
						<div class="standard-table course-table catalog-list-table">
							<table class="">
							   <thead>
							      <tr>
							         <th class="" scope="col"><span class="col-header">First Name</span></th>
							         <th class="" scope="col"><span class="col-header">Last Name</span></th>
							      </tr>
							   </thead>
							   <tbody>
								<core:forEach items="${courseRecord.instructors}" var="instructor">    
								  <tr class="light-grey-out">
								    <td><core:out value="${instructor.firstName}" /></td>
								    <td><core:out value="${instructor.lastName}" /></td>
								  </tr>
								</core:forEach>
							   </tbody>
							</table>
						</div>
					
						</fieldset>
					</div>

					<core:if test="${!courseRecord.studentDetails}" >
					<div class="fieldset-group">
						<fieldset>
					
						<header class="simple-header">
							<h2 class="grey-out">Course Record Student(s)</h2>
						</header>
						<div class="standard-table course-table catalog-list-table">
							<table class="">
							   <thead>
							      <tr>
							         <th class="" scope="col"><span class="col-header">First Name</span></th>
							         <th class="" scope="col"><span class="col-header">Last Name</span></th>
							         <th class="" scope="col"><span class="col-header">Email</span></th>
							         <th class="" scope="col"><span class="col-header">Phone #</span></th>
							         <th class="" scope="col"><span class="col-header">Components</span></th>
							      </tr>
							   </thead>
							   <tbody>
								<core:forEach items="${courseRecord.students}" var="student">    
								  <tr class="light-grey-out">
								    <td><core:out value="${student.firstName}" /></td>
								    <td><core:out value="${student.lastName}" /></td>
								    <td><core:out value="${student.email}" /></td>
								    <td><core:out value="${student.phoneNumber}" /></td>
   							        <td><table>
									   <core:forEach items="${student.courseComponents}" var="courseComponent2">
							         	<tr><td style="border-style:none;margin:0px;padding:0px;"><core:out value="${courseComponent2.courseComponentLabel}" />: <core:out value="${courseComponent2.courseComponentValue}" /></td></tr>
							    	   </core:forEach>    
									</table></td>
								  </tr>
								</core:forEach>
							   </tbody>
							</table>
						</div>
					
						</fieldset>
					</div>
					</core:if>
					
					</core:if>
					
				</div>
			</div>

				<div class="corners PrintSection">
				
					<div class="simple-form tabular-form">
						<div class="fieldset-group">
							<fieldset>				
							<header class="form-header">
								<h1 class="grey-out">Payment Information</h1>
							</header>
							<div class="simple-form tabular-form">
								<div class="fieldset-group">
									<fieldset>
									<div class="field-wrap">
										<label for="" class="no-asterisk grey-out">Payment Type</label>
										<div class="field">
											<span class="light-grey-out">${courseRecord.payment.paymentType}</span>
										</div>
									</div>
									<core:if test="${courseRecord.payment.paymentTypeID=='CC'}" >
									<div class="field-wrap">
										<label for="" class="no-asterisk grey-out">Credit Card #</label>
										<div class="field">
											<span class="light-grey-out" >${courseRecord.payment.paymentReference}</span>
										</div>
									</div>
									</core:if>
									<core:if test="${courseRecord.payment.paymentTypeID=='PO'}" >
									<div class="field-wrap">
										<label for="" class="no-asterisk grey-out">Purchase Order #</label>
										<div class="field">
											<span class="light-grey-out" >${courseRecord.payment.paymentReference}</span>
										</div>
									</div>
									</core:if>
									<core:if test="${courseRecord.payment.paymentTypeID!='CC' && courseRecord.payment.paymentTypeID!='PO'}" >
									<div class="field-wrap">
										<label for="" class="no-asterisk grey-out">Reference #</label>
										<div class="field">
											<span class="light-grey-out" >${courseRecord.payment.paymentReference}</span>
										</div>
									</div>
									</core:if>
									<div class="field-wrap">
										<label for="" class="no-asterisk grey-out">Amount</label>
										<div class="field">
											<span class="light-grey-out">${courseRecord.payment.totalPrice}</span>
										</div>
									</div>
									<div class="field-wrap">
										<label for="" class="no-asterisk grey-out">Payment Status</label>
										<div class="field">
											<core:choose>
        										<core:when test="${courseRecord.payment.paymentTypeID==null || courseRecord.payment.paymentTypeID=='' || courseRecord.payment.paymentTypeID=='PL'}">
													<span class="light-grey-out">Not Paid</span>
        										</core:when>
        										<core:otherwise>
													<span class="light-grey-out">Completed</span>
        										</core:otherwise>
    										</core:choose>
										</div>
									</div>
									</fieldset>
									</div>
								</div>
								</fieldset>
							</div>
						</div>
				
				</div>
				<core:if test="${courseRecord.payment.paymentTypeID=='CC' || courseRecord.payment.paymentTypeID=='PO'}" >
				<div class="corners PrintSection">
				
					<div class="simple-form tabular-form">
						<div class="fieldset-group">
							<fieldset>					
								<header class="form-header">
									<h1 class="grey-out">Approval Information</h1>
								</header>
								<div class="simple-form tabular-form">
									<div class="fieldset-group">
										<fieldset>
											<div class="field-wrap">
												<label for="" class="no-asterisk grey-out">Offering No.:</label>
												<div class="field">
													<span class="light-grey-out">${courseRecord.offeringNumber}</span>
												</div>
											</div>
											<div class="field-wrap">
												<label for="" class="no-asterisk grey-out">Order No.:</label>
												<div class="field">
													<span class="light-grey-out">${courseRecord.orderNumber}</span>
												</div>
											</div>
											<div class="field-wrap">
												<label for="" class="no-asterisk grey-out">Approved By:</label>
												<div class="field">
													<span class="light-grey-out">${courseRecord.approverFirstName} ${courseRecord.approverLastName}</span>
												</div>
											</div>
											<div class="field-wrap">
												<label for="" class="no-asterisk grey-out">Approved Date:</label>
												<div class="field">
													<span class="light-grey-out">${courseRecord.approvedDate}</span>
												</div>
											</div>
											<div class="field-wrap">
												<label for="" class="no-asterisk grey-out">Approval Comments:</label>
												<div class="field">
													<span class="light-grey-out">${courseRecord.approverComments}</span>
												</div>
											</div>
										</fieldset>
									</div>
								</div>
						
								</fieldset>
							</div>
						</div>						
					</div>
					</core:if>
				</core:if>

				<footer class="form-action clearfix">
					<div class="hideWhilePrinting">
						<div class="action plain-button  alt-button">
							<div class="bw">
								<input id="print" type="button" class="button" name="print" value="Print">	
							</div>
						</div>
						<div class="action plain-button">
							<div class="bw">
								<input id="close" type="button" class="button" name="close" value="Close" onClick="window.close();">	
							</div>
						</div>
					</div>
				</footer>			
			</form>
		</section>
	</div>
</div>
</body>
</html>