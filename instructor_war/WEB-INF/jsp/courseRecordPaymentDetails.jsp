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
		
		.msgNotify{
		 color:#5EAD5C; 
		 font-size:1.2em;
		 font-family: 'helvetica', Georgia, arial, sans-serif;
		 font-weight:bold; 
		}


		.msgNotify {
		   cursor:pointer;
		   cursor:hand;
		   background: #E7FFE7 url('../images/icon/green-square.png') center no-repeat;
		   background-position: 15px 50%;
		   text-align: left;
		   padding: 2px 2px 2px 50px;
		   border-top: 2px solid #7DE77B;
		   border-bottom: 2px solid #7DE77B;
		   border:2px solid #7DE77B;
		   overflow:auto;
		   font-size:10px;
		   font-family:Verdana;
		}
	</style> 
	
	<script src="../js/jquery-1.9.1.js"></script>
  	<script src="../js/jquery-ui-1.10.3.custom.js"></script>
  	<script src="../js/instructor.js"></script>
  	<script src="../js/payment.js"></script>
  	
  	<script type="text/javascript" charset="utf-8">
  	
  	var formHasErrors = false;
  	
  	function centerModal() {
	    var top, left;
	
	    top = Math.max($(window).height() - $('#overlay').outerHeight(), 0) / 2;
	    left = Math.max($(window).width() - $('#overlay').outerWidth(), 0) / 2;
	
	    $('#overlay').css({
	        top:top + $(window).scrollTop(), 
	        left:left + $(window).scrollLeft()
	    });
	};
  	
  	function resetErrorMsg(obj) {
  		obj.text("");
  		obj.attr("class", "");
  		
  		$('#submit_msg').text("");
  		$('#submit_msg').attr("class", "");
  	}
  	
  	function displayErrorMsg(obj, msg) {
  		obj.text(msg);
  		obj.attr("class", "msgError");
  		formHasErrors = true;
  	}
  	
  	
  	function validateForm() {
  		//console.log("validate form");

  		formHasErrors = false;
  		
  		var promotionalCode = $('#payment\\.promotionalCode').val();
  		if (promotionalCode != "" && $('#couponButton').val() == "Apply") {
  			displayErrorMsg($('#promoCode_msg'), "Coupon has not been applied.  Please click 'Apply' to add the coupon to your course record entry.");
  		}

  		var paymentTypeID = $('#payment\\.paymentTypeID').val();
  		$('#payment\\.paymentType').val($('#payment\\.paymentTypeID option:selected').text());
  		
  		if (paymentTypeID == "" && nopayment == false) {
  			displayErrorMsg($('#paymentType_msg'), "Invalid Payment Type");
  		} else if (paymentTypeID == "PO") {
  			$('#payment\\.purchaseOrder').val($('#payment\\.purchaseOrderID option:selected').text());
  			
  			if ($('#payment\\.purchaseOrderID').val() == "") {
  				displayErrorMsg($('#purchaseOrder_msg'), "Invalid Purchase Order");
  			}
  		} else if (paymentTypeID == "CC") {
  			var card_type = cardType($('#card_accountNumber').val());
  			$('#card_cardType').val(card_type);
  			if ($('#card_accountNumber').val() == "" || card_type == "-1") {
  				displayErrorMsg($('#card_accountNumber_msg'), "Invalid Card Number");
  			}
  			if ( validateExpiryDate($('#card_expirationMonth').val(),$('#card_expirationYear').val())==false ){
  				displayErrorMsg($('#card_expirationDate_msg'), "Invalid Expiration Date");
  			}
  			//console.log("cvNumber length: "+$('#card_cvNumber').val().length);
  			//console.log("cvNumber isNan: "+isNaN($('#card_cvNumber')));
  			if ($('#card_cvNumber').val() == "" || $('#card_cvNumber').val().length < 3 || isNaN($('#card_cvNumber').val())) {
  				displayErrorMsg($('#card_cvNumber_msg'), "Invalid CVV Code");
  			}
  			if ($('#billTo_firstName').val() == "") {
  				displayErrorMsg($('#billTo_firstName_msg'), "Invalid First Name");
  			}
  			if ($('#billTo_lastName').val() == "") {
  				displayErrorMsg($('#billTo_lastName_msg'), "Invalid Last Name");
  			}
  			if ( validateEmailFormat($('#billTo_email').val())==false ){
  				displayErrorMsg($('#billTo_email_msg'), "Invalid Email");
  			}
  			if ($('#billTo_street1').val() == "") {
  				displayErrorMsg($('#billTo_street1_msg'), "Invalid Address");
  			}
  			if ($('#billTo_city').val() == "") {
  				displayErrorMsg($('#billTo_city_msg'), "Invalid City");
  			}
  			if ($('#billTo_state').val() == "") {
  				displayErrorMsg($('#billTo_state_msg'), "Invalid State");
  			}
  			if ($('#billTo_postalCode').val() == "" || $('#billTo_postalCode').val().length != 5) {
  				displayErrorMsg($('#billTo_postalCode_msg'), "Invalid Zip");
  			}
  			if ($('#billTo_country').val() == "") {
  				displayErrorMsg($('#billTo_country_msg'), "Invalid Country");
  			}
  		}
  		if (!$('#agreeLegalInfo').is(':checked')) {
  			displayErrorMsg($('#agreeLegalInfo_msg'), "You must check the agree box before you can sumbit the form");
  		}
  		
  		if (formHasErrors) {
  			displayErrorMsg($('#submit_msg'), "Please correct the form errors above to submit course record payment");
  		} else {
  			processPayment();
  		}
  	}
  	
  	function showRejectedDialog() {
	    var rejectReason = '${courseRecord.payment.creditCardErrMsg}';
		$('#rejectReason').text(rejectReason);
		$('#rejectedDialog').dialog({
			autoOpen: true,
			width: 400,
			modal: true,
			buttons: {
				"Close": function () {
					$(this).dialog("close");
				}
			}
		});		
	}
  	
  	function showErrorDialog() {
	    $('#errorDialog').dialog({
			autoOpen: true,
			width: 400,
			modal: true,
			buttons: {
				"Close": function () {
					$(this).dialog("close");
				}
			}
		});		
	}
  	
  	function allow_num_only(evt) {
  		var theEvent = evt || window.event;
  	  	var key = theEvent.keyCode || theEvent.which;
  	  	// check if backspace, tab, enter, left, right, delete, home, end (do dafault action)
  	  	if (key==8 || key==9 || key==13 || key==35 || key==36 || key==37 || key==39 || key==46)
  	  		return;
  	  	key = String.fromCharCode( key );
  	  	var regex = /[0-9]/;
  	  	if( !regex.test(key) ) {
  	    	theEvent.returnValue = false;
  	    	if(theEvent.preventDefault) theEvent.preventDefault();
  	  	}
  	}
  	
	$(document).ready(function() {
		
		$( document ).tooltip();
		
		$('#POWarningDialog').hide();

		// for some reason, when page initially loads, orderPage_signaturePublic has a newline in the val
  		// so I am removing it below when document is ready
  		// It is critical that the orderPage_signaturePublic is correct when submitting payment, otherwise
  		// it will fail
  		var tmp_signature = $('#orderPage_signaturePublic').val();
  		tmp_signature = tmp_signature.replace(/\n|\r/g, "");
  		$('#orderPage_signaturePublic').val(tmp_signature);
		
		paymentTypeDisplay($('#payment\\.paymentTypeID').val());
	  	$('#payment\\.paymentTypeID').change(function() { paymentTypeDisplay($('#payment\\.paymentTypeID').val()); });
	  	$('#couponButton').click(function() { processCoupon(); });
	  	
	  	if ($('#payment\\.promotionalCode').val() != "") {
	  		$('#payment\\.promotionalCode').attr("disabled", true);
  			$('#couponButton').val("Remove");
	  	}
  		if ($('#payment\\.promotionalCode').val() == "") {
  			calcPrice(parseFloat($('#payment\\.originalPrice').val()));
  		} else {
  			calcPrice(parseFloat($('#payment\\.finalPrice').val()));
  		}
  		
  		// just in case the autocomplete is ignored, clear out cc fields
  		$('#card_accountNumber').val("");
  		$('#card_cvNumber').val("");
  		$('#card_expirationMonth').val("${cur_month}");
  		$('#card_expirationYear').val("${cur_year}");

  		// create change triggers for all form fields
  		$('#payment\\.promotionalCode').change(function () { resetErrorMsg($('#promoCode_msg')); });
  		$('#payment\\.paymentTypeID').change(function () { resetErrorMsg($('#paymentType_msg')); });
  		$('#payment\\.purchaseOrderID').change(function () { resetErrorMsg($('#purchaseOrder_msg')); });
  		$('#card_accountNumber').change(function () { highlightCard(); resetErrorMsg($('#card_accountNumber_msg')); });
  		$('#card_expirationMonth').change(function () { resetErrorMsg($('#card_expirationDate_msg')); });
  		$('#card_expirationYear').change(function () { resetErrorMsg($('#card_expirationDate_msg')); });
  		$('#card_cvNumber').change(function () { resetErrorMsg($('#card_cvNumber_msg')); });
  		$('#billTo_firstName').change(function () { resetErrorMsg($('#billTo_firstName_msg')); });
  		$('#billTo_lastName').change(function () { resetErrorMsg($('#billTo_lastName_msg')); });
  		$('#billTo_email').change(function () { resetErrorMsg($('#billTo_email_msg')); });
  		$('#billTo_street1').change(function () { resetErrorMsg($('#billTo_street1_msg')); });
  		$('#billTo_city').change(function () { resetErrorMsg($('#billTo_city_msg')); });
  		$('#billTo_state').change(function () { resetErrorMsg($('#billTo_state_msg')); });
  		$('#billTo_postalCode').change(function () { resetErrorMsg($('#billTo_postalCode_msg')); });
  		$('#billTo_country').change(function () { resetErrorMsg($('#billTo_country_msg')); });
  		$('#agreeLegalInfo').change(function () { resetErrorMsg($('#agreeLegalInfo_msg')); });
  		
  		$('#submit').click(function () { validateForm(); });
  		
  		$('input[name="back"]').click(function() { 
			$('#action').val('Back');
			document.forms["courseRecord"].submit();
		});
  		
  		//console.log("url: "+window.location.href);
  		//console.log("url prefix: " + window.location.protocol.toString() + "//" + window.location.host.toString());
  		
  		<core:if test="${card_status=='reject'}" >
  		showRejectedDialog();
  		</core:if>
  		<core:if test="${card_status=='error'}" >
  		showErrorDialog();
  		</core:if>
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
		<section class="primary-content">
			<header class="primary-header">
				<h1>Payment
				<core:if test="${card_status=='reject'}" ><font color="red"> Rejected</font></core:if>
				<core:if test="${card_status=='error'}" ><font color="red"> Error</font></core:if>
				</h1>
			</header>
			<div class="field-wrap">
			<core:if test="${courseRecord.feeOption=='Fee'}" >
                Course < Students < Review < <u>Payment</u> < Confirmation
            </core:if>
			<core:if test="${courseRecord.feeOption=='Facility-fee'}" >
				Course < <u>Payment</u> < Confirmation
			</core:if>
			</div>
			<div class="field-wrap">

			</div>
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
						<div class="field-wrap">
						    <core:if test="${courseRecord.feeOption=='Facility-fee'}" >
                            <label for="" class="grey-out no-asterisk">Date:</label>
                            </core:if>
							<core:if test="${courseRecord.feeOption!='Facility-fee'}" >
                            <label for="" class="grey-out no-asterisk">Offering End Date:</label>
                            </core:if>
							<div class="field light-grey-out">
								<span><core:out value="${courseRecord.endDate}" /></span>
							</div>
						</div>
					</fieldset>
				</div>
			</div>
		</section>
		<section class="primary-content">
				<header class="form-header">
					<h1>Payment Information</h1>
				</header>
				<div class="cert-form tabular-form">

				<form:form method="POST" name="courseRecord" commandName="courseRecord" >
					<div class="fieldset-group">
					
						<form:hidden path="payment.originalPrice" />
						<form:hidden path="payment.finalPrice" />
						<form:hidden path="payment.totalPrice" />

						<fieldset>
							
							<legend>
							<div id="payment-info-section">
							The total is calculated automatically. If you have a coupon code, be certain to include it in the Promotional Code section prior to submitting your payment information. If you have questions regarding pricing or coupon codes, please contact the Training Support Center (TSC) at 1.800.RED.CROSS. 

							Select the payment method and enter the required payment reference information. The Purchase Order option requires the organization to be approved for invoicing privileges with the Red Cross. When finished, click 'Submit'.
							</div>
							</legend>
							
							<core:if test="${courseRecord.feeOption!='Facility-fee'}" >
							<div class="field-wrap">
								<label for="" class="no-asterisk">Amount Per Student:</label>
								<div class="field">
									<span id="amtPerStudent"></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="no-asterisk">Number of Students:</label>
								<div class="field">
									<span><core:out value="${courseRecord.totalStudents}" /></span>
								</div>
							</div>
							</core:if>
							<div class="field-wrap">
								<label for="" class="no-asterisk">Total:</label>
								<div class="field">
									<span id="totalPrice"></span>
								</div>
							</div>
							
							<div class="module-inner button-wrap">
								<label for="payment.promotionalCode" class="no-asterisk">Promotional Code: </label>
							    <div class="field" style="margin-right:5px">
							        <form:input path="payment.promotionalCode" />
							    </div>
							    <div class="button-wrap">
							    <div class="action alt-button plain-button" style="margin-right:5px; float:left;">
							        <!--[if lt IE 9 ]>
							        <div class="action-shadow clearfix">
							            <![endif]-->
							            <div class="bw">
							                <input type="button" id="couponButton" class="button" name="couponButton" value="Apply">
							            </div>
							            <!--[if lt IE 9 ]>
							        </div>
							        <![endif]-->
							    </div>
							    <!-- END .action -->
							    <div style="margin-top:3px;">
							    	<span id="promoCode_msgNotify"></span>
								</div>
								</div>
							</div>
							<div class="field-wrap">
							    <span id="promoCode_msg"></span>
							</div>
							
							<div id="payment-section">
							
							<div class="field-wrap">
								<label for="paymentType">Payment Type: </label>
								<div class="field">
									<form:select items="${paymentTypeList}" path="payment.paymentTypeID" itemValue="key" itemLabel="value" />
									<form:hidden path="payment.paymentType" />
								</div>
							</div>
							<div class="field-wrap">
								<span id="paymentType_msg"></span>
								<form:errors path="payment.paymentTypeID" cssClass="msgError" />
							</div>
							<input type="hidden" id="organizationID" name="organizationID" value="${courseRecord.organizationID}">
							<input type="hidden" id="sheetNumberID" name="sheetNumberID" value="${courseRecord.sheetNumberID}">
							<input type="hidden" id="totalStudents" name="totalStudents" value="${courseRecord.totalStudents}">
							
							</div>
						</fieldset>
						

						<div id="purchaseOrderFields" >
						
						<fieldset>
							<div class="field-wrap">
							     <label for="purchaseOrder">Purchase Order:</label>
							     <div class="field">
							        <form:select items="${purchaseOrderList}" path="payment.purchaseOrderID" itemValue="key" itemLabel="value" />
							        <form:hidden path="payment.purchaseOrder" />
							     </div>
							</div>
							<div class="field-wrap">
								<span id="purchaseOrder_msg"></span>
							</div>
						</fieldset>
							
						</div>
						<form:hidden  path="action"  /> 
					</form:form>
							
						<div id="creditCardFields" >
						
						<form id="processPayment" name="processPayment" action="${CybersourceURL}" method="post" class="payment-form" autocomplete="off">
						<fieldset>
							<input type="hidden" name="merchant_defined_data1" value="InstructorPortal"/>
							<input type="hidden" name="merchant_defined_data2" value="processCREPayment"/>
							<input type="hidden" name="merchant_defined_data3" value="${courseRecord.sheetNumberID}"/>
							<input type="hidden" name="merchant_defined_data4" value=""/>
							
							<input type="hidden" name="reference_number" value="CRE${courseRecord.sheetNumber}"/>
							
							<input type="hidden" id="amount" name="amount" value="${totalPrice}"/>
							<input type="hidden" name="currency" value="usd"/>
							<input type="hidden" name="payment_method" value="card"/>
							<input type="hidden" name="tax_amount" value="0.0"/>
							
							<input type="hidden" id="orderPage_signaturePublic" name="signature" value=""/>
							<input type="hidden" name="access_key" value="${access_key}"/>
							<input type="hidden" name="profile_id" value="${profile_id}"/>
							<input type="hidden" name="signed_date_time" value=""/>
							<input type="hidden" name="signed_field_names" value="${signed_field_names}"/>
							<input type="hidden" name="unsigned_field_names" value="${unsigned_field_names}"/>
							<input type="hidden" name="transaction_uuid" value="${transaction_uuid}"/>
							<input type="hidden" name="merchantID" value="${merchant_id}"/>
							<input type="hidden" name="card_expiry_date" value=""/>
							
							<input type="hidden" name="locale" value="en"/>
							<input type="hidden" name="primaryRoles" value="Custom2"/>
							<input type="hidden" name="agree" value="true"/>
							<input type="hidden" name="ignore_cvn" value="false"/>
							<input type="hidden" name="transaction_type" value="authorization"/>
							<input type="hidden" name="ignore_avs" value="true"/>
							
							<div id="cards-field" class="field-wrap first">
							    <label for="card_accountNumber">Card Number: <div class="note credit-cards">
							    	<div class="visa"><span id="visa-icon"></span></div>
							    	<div class="american-express"><span id="american-express-icon"></span></div>
							    	<div class="mastercard"><span id="mastercard-icon"></span></div>
							    	<div class="discover"><span id="discover-icon"></span></div></div>
							    </label>
								<div class="field">
								    <input type="hidden" id="card_cardType" name="card_type" value=""/>
									<input type="text" id="card_accountNumber" name="card_number" class="input-large cardNumber" maxlength="16" autocomplete="off" />
								</div>
							</div>
							<div class="field-wrap">
								<span id="card_accountNumber_msg"></span>
							</div>
							<div class="field-wrap">
							    <label for="card_expirationDate">Expiration Date:</label>
							    <div class="field">
							    	<select id="card_expirationMonth" name="expMonth" class="input-small">
										<c:forEach items="${monthList}" var="month">
            								<option value="<c:out value="${month.key}"/>"><c:out value="${month.value}"/></option>
										</c:forEach>
									</select>
									<select id="card_expirationYear" name="expYear" class="input-small">
										<c:forEach items="${yearList}" var="year">
            								<option value="<c:out value="${year}"/>"><c:out value="${year}"/></option>
										</c:forEach>
									</select>
							    </div>
							</div>
							<div class="field-wrap">
								<span id="card_expirationDate_msg"></span>
							</div>
							<div class="field-wrap security-wrap">
							     <label for="card_cvNumber">Security Code (CVV):<div class="note">Security Code Hint</div></label>
							     <div class="field">
							        <input type="password" id="card_cvNumber" name="card_cvn" class="input-tiny" maxlength="4" autocomplete="off" />
							     </div>
							 </div>
							 <div class="field-wrap">
								<span id="card_cvNumber_msg"></span>
							</div>
						</fieldset>
						<fieldset class="billing-fieldset">
						     <legend><span>Please enter the billing address associated with this card.</span></legend>
						     <div class="field-wrap">
						         <label for="billTo_firstName">First Name:</label>
						         <div class="field">
						             <input type="text" id="billTo_firstName" name="bill_to_forename" class="input-large" autocomplete="off" />
						         </div>
						     </div>
						     <div class="field-wrap">
								<span id="billTo_firstName_msg"></span>
							</div>
						     <div class="field-wrap">
						         <label for="billTo_lastName">Last Name:</label>
						         <div class="field">
						             <input type="text" id="billTo_lastName" name="bill_to_surname" class="input-large" autocomplete="off" />
						         </div>
						     </div>
						     <div class="field-wrap">
								<span id="billTo_lastName_msg"></span>
							</div>
							<div class="field-wrap">
						         <label for="billTo_email">Email:</label>
						         <div class="field">
						             <input type="text" id="billTo_email" name="bill_to_email" class="input-large" autocomplete="off" />
						         </div>
						     </div>
						     <div class="field-wrap">
								<span id="billTo_email_msg"></span>
							</div>
						    <div class="field-wrap address-1-wrap">
						        <label for="billTo_street1">Address Line 1:<small class="note">Street address, P.O. box, company name, c/o</small></label>
						        <div class="field">
						        	<input type="text" id="billTo_street1" name="bill_to_address_line1" class="input-large" autocomplete="off"/>
						        </div>
						    </div>
						    <div class="field-wrap">
								<span id="billTo_street1_msg"></span>
							</div>
						    <div class="field-wrap address-2-wrap">
						        <label for="billTo_street2" class="optional">Address Line 2:
						            <span class="optional-text">(optional)</span>
						            <small class="note">Apartment, suite, unit, building, floor, etc.</small>
						        </label>
						        <div class="field">
						            <input type="text" id="billTo_street2" name="bill_to_address_line2" class="input-large" autocomplete="off" />
						        </div>
						    </div>
						    <div class="field-wrap">
								<span></span>
							</div>
						    <div class="field-wrap">
						        <label for="billTo_city">City:</label>
						        <div class="field">
						            <input type="text" id="billTo_city" name="bill_to_address_city" class="input-large" autocomplete="off" />
						        </div>
						    </div>
						    <div class="field-wrap">
								<span id="billTo_city_msg"></span>
							</div>
						    <div class="field-wrap">
						        <label for="billTo_state">State:</label>
						        <div class="field">
						        	<select id="billTo_state" name="bill_to_address_state" class="input-large">
										<c:forEach items="${stateList}" var="state">
            								<option value="<c:out value="${state.key}"/>"><c:out value="${state.value}"/></option>
										</c:forEach>
									</select>
						        </div>
						    </div>
						    <div class="field-wrap">
								<span id="billTo_state_msg"></span>
							</div>
						    <div class="field-wrap">
						        <label for="billTo_postalCode">Postal Code:</label>
						        <div class="field">
						        	<input type="text" id="billTo_postalCode" name="bill_to_address_postal_code" class="input-tiny" maxlength="5" onkeypress='allow_num_only(event)' autocomplete="off" />
						        </div>
						    </div>
						    <div class="field-wrap">
								<span id="billTo_postalCode_msg"></span>
							</div>
						    <div class="field-wrap">
						    <label for="billTo_country">Country:</label>
						        <div class="field">
						        	<select id="billTo_country" name="bill_to_address_country" class="input-large">
										<option value="US">United States</option>
									</select>
						        </div>
						    </div>
						    <div class="field-wrap">
								<span id="billTo_country_msg"></span>
							</div>
						</fieldset>
						</form>
						</div>

						<form id="agreeForm" name="agreeForm" class="payment-form">
						<fieldset class="">
						    <section class="">
								<header class="form-header">
								<h1>Legal Information</h1>
								</header>
								<div class="sub-content">
									<p class="first">I certify this training session has been conducted in accordance with the requirements and procedures of the American Red Cross.</p>
									<div class="field-wrap checkbox-wrap">
									<div class="field">
										<input type="checkbox" id="agreeLegalInfo" name="agreeLegalInfo" ${agreeChecked} />
									</div>
									<label for="agreeLegalInfo">I Agree</label>
									</div>
									<div class="field-wrap">
										<span id="agreeLegalInfo_msg"></span>
									</div>
						        </div>
						     </section>
						 </fieldset>
						 </form>
					 </div>
				</div>

				<form id="actionForm" name="actionForm" class="payment-form">
				<footer class="form-action clearfix">
					<fieldset>
					<div class="button-wrap">
						<div class="action plain-button">
							<div class="bw">
								<input type="button" class="button" id="submit" name="submit" value="Submit" >	
							</div>
						</div>
						<!--
						<div class="action plain-button alt-button">
							<div class="bw">
								<input id="back" type="button" class="button" name="back" value="Back">	
							</div>
						</div>
						-->
						<div class="action plain-button alt-button">
							<div class="bw">
								<input id="close" type="button" class="button" name="close" value="Close">	
							</div>
						</div>
					</div>
					</fieldset>
					<br />
					<fieldset>
						<div class="field-wrap">
							<span id="submit_msg"></span>
						</div>
					</fieldset>
				</footer>
				</form>

				<core:if test="${courseRecord.feeOption=='Fee'}" >
					<div>
						<p class="first">Select 'Submit' if you have completed the payment information. Next, emails containing links to the digital certificates will be sent to the instructor and students when you click submit and payment is confirmed. You will be able to print your wallet cards if desired.</p>      
						<p class="first">Select 'Close' if you do not want to complete this course record now.  You may return to your course record to make any needed changes or additions at a later time. Note that selecting 'Close' will not process payment and it will not issue the digital certificates to the students.</p>
					</div>
				</core:if>
		</section>
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
		<p>No suitable purchase orders with sufficient funds found for this organization.</p>
	</td>
	</tr>
	</table>	
</div>

<div id="rejectedDialog" class="window" title="Credit Card Rejected">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>Your credit card was rejected.</p>
		<p id="rejectReason"></p>
	</td>
	</tr>
	</table>	
</div>

<div id="errorDialog" class="window" title="Credit Card Processing Error">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>Your credit card was charged but there was an error during processing.  Please contact technical support.</p>
	</td>
	</tr>
	</table>	
</div>

</body>
</html>