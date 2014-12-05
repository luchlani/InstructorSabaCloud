	var nopayment = false;

	function paymentTypeDisplay(paymentTypeID){
		if (paymentTypeID == "PO") {
			$('#creditCardFields').hide();
			$('#purchaseOrderFields').show();
			
			// show warning if no purchase orders
	  		if ($('#payment\\.purchaseOrderID option').size() == 1) {
	  			$('#POWarningDialog').dialog({
					autoOpen: true,
	 				width: 330,
	 				modal: true,
	 				buttons: {
	  					"Close": function() { 
	   						$(this).dialog("close"); 
	  					} 
					}
				});
	  		}
		} else if (paymentTypeID == "CC") {
			$('#purchaseOrderFields').hide();
			$('#creditCardFields').show();
		} else {
			$('#purchaseOrderFields').hide();
			$('#creditCardFields').hide();
		}
	}
  	
  	function processCoupon() {
  		if ($('#couponButton').val() == "Apply") {
  			applyCoupon().then(processCouponResponse);
  		} else if ($('#couponButton').val() == "Remove") {
  			removeCoupon().then(processCouponResponse);
  		}
	}
  	
  	function applyCoupon() {
  		var url = "/instructor/coupon/processCoupon.html";
	    var coupon_code = $("#payment\\.promotionalCode").val();
	    var org_id = $("#organizationID").val();
	    var crs_id = $("#sheetNumberID").val();
	    return $.ajax(url, {
	        data: {
	            action: "apply",
	            orgID: org_id,
	            crsID: crs_id,
	            code: coupon_code
	        }
	    });
  	}
  	
  	function removeCoupon() {
  		var url = "/instructor/coupon/processCoupon.html";
  		var coupon_code = $("#payment\\.promotionalCode").val();
  		var org_id = $("#organizationID").val();
	    var crs_id = $("#sheetNumberID").val();
	    return $.ajax(url, {
	        data: {
	            action: "remove",
	            orgID: org_id,
	            crsID: crs_id,
	            code: coupon_code
	        }
	    });
  	}
  	
  	function processCouponResponse(jsonObj) {
  		var msg = jsonObj.coupon_msg;
  		
  		// Reset messages
  		$('#promoCode_msgNotify').text("");
  		$('#promoCode_msgNotify').attr("class", "");
  		$('#promoCode_msg').text("");
  		$('#promoCode_msg').attr("class", "");
  		
  		//console.log("coupon_msg: "+msg);
  		if (msg == "Coupon successfully applied.") {
  			$('#payment\\.promotionalCode').attr("disabled", true);
  			$('#couponButton').val("Remove");
  			$('#promoCode_msgNotify').text(msg);
  			$('#promoCode_msgNotify').attr("class", "msgNotify");
  			$('#promoCode_msgNotify').show();
  			//console.log("amount per student: "+jsonObj.amt_per_student);
  			calcPrice(parseFloat(jsonObj.amt_per_student));
  		} else if (msg == "Coupon successfully removed.") {
  			$('#payment\\.promotionalCode').removeAttr("disabled");
  			$('#payment\\.promotionalCode').val("");
  			$('#couponButton').val("Apply");
  			$('#promoCode_msgNotify').text(msg);
  			$('#promoCode_msgNotify').attr("class", "msgNotify");
  			$('#promoCode_msgNotify').show();
  			//console.log("amount per student: "+jsonObj.amt_per_student);
  			calcPrice(parseFloat(jsonObj.amt_per_student));
  		} else if (msg == "A coupon is already applied on this Course Record. Please remove this coupon before applying another one.") {
  			$('#payment\\.promotionalCode').val("");
  			$('#payment\\.promotionalCode').attr("disabled", true);
  			$('#couponButton').val("Remove");
  			$('#promoCode_msg').text(msg);
  			$('#promoCode_msg').attr("class", "msgError");
  			$('#promoCode_msg').show();
  		} else {
  			$('#promoCode_msg').text(msg);
  			$('#promoCode_msg').attr("class", "msgError");
  			$('#promoCode_msg').show();
  		}
  		
  		// Delete options
  		$select = $("#payment\\.purchaseOrderID");
  		$select.empty();
  		
  		// Rebuild list
  		var poList = jsonObj.po_list;
  		for (var i = 0; i < poList.length; i++) {
	    	//console.log("PO List Iterate: " + poList[i].key + " " + poList[i].value + " " + poList[i].label);
	    	$select.append("<option value=\""+poList[i].key+"\">"+poList[i].value+"</option>");
  		}
  		
  		// Update CC Transaction Signature
  		//console.log("update cc transaction signature");
  		//$('#orderPage_signaturePublic').val(jsonObj.orderPage_signaturePublic);
  		//$('#orderPage_timestamp').val(jsonObj.orderPage_timestamp);
  		
  	}
  	
  	function getPurchaseOrderList(org_id, amount) {
  		var url = "/instructor/purchaseorder/getPurchaseOrderList.html";
	    return $.ajax(url, {
	        data: {
	            orgID: org_id,
	            amt: amount
	        }
	    });
	}
  	
  	function updatePurchaseOrders(jsonObj) {
  		// Delete options
  		$select = $("#payment\\.purchaseOrderID");
  		$select.empty();
  		
  		// Rebuild list
  		var poList = jsonObj.po_list;
  		for (var i = 0; i < poList.length; i++) {
	    	//console.log("PO List Iterate: " + poList[i].key + " " + poList[i].value + " " + poList[i].label);
	    	$select.append("<option value=\""+poList[i].key+"\">"+poList[i].value+"</option>");
  		}
  	}
  	
  	function addPurchaseOrder() {
  		var url = "/instructor/purchaseorder/processPurchaseOrder.html";
	    var purchase_order_id = $("#payment\\.purchaseOrderID").val();
	    var crs_id = $("#sheetNumberID").val();
	    return $.ajax(url, {
	        data: {
	            crsID: crs_id,
	            poID: purchase_order_id
	        }
	    });
	}
  	
  	function processPOResponse(msg) {
  		//console.log("PO msg: " + msg);
  		if (msg == "Success") {
  			// return to payment controller and show print screen
  			$('#courseRecord').submit();
  		} else {
  			document.getElementById("overlay").style.visibility = "hidden";
  			displayErrorMsg($('#purchaseOrder_msg'), msg);
  		}
  	}
  	
  	function insertCCTrack() {
  		var url = "/instructor/creditcard/insertCCTrack.html";
  		var crs_id = $("#sheetNumberID").val();
	    return $.ajax(url, {
	        async: false,
			data: {
	            crsID: crs_id
	        }
	    });
	}
  	
  	function processCCResponse(msg) {
  		//console.log("CC msg: " + msg);
  		if (msg == "Success") {
  			// send credit card form to cybersource
  			$('#processPayment').submit();
  		} else {
  			document.getElementById("overlay").style.visibility = "hidden";
  			displayErrorMsg($('#paymentType_msg'), msg);
  		}
  	}
  	
  	function paymentPendingNotification() {
  		var url = "/instructor/paylater/paymentPendingNotification.html";
  		var crs_id = $("#sheetNumberID").val();
	    return $.ajax(url, {
	        data: {
	            crsID: crs_id
	        }
	    });
  	}
  	
  	function processPLResponse(msg) {
  		//console.log("PL msg: " + msg);
  		if (msg == "Success") {
  			// pay later saved
  			$('#courseRecord').submit();
  		} else {
  			document.getElementById("overlay").style.visibility = "hidden";
  			displayErrorMsg($('#paymentType_msg'), msg);
  		}
  	}
  	
  	function approveNoCostNoPaymentCR() {
  		var url = "/instructor/nopayment/approveNoCostNoPaymentCR.html";
  		var crs_id = $("#sheetNumberID").val();
	    return $.ajax(url, {
	        data: {
	            crsID: crs_id
	        }
	    });
  	}
  	
  	function processNoPayment(msg) {
  		//console.log("PL msg: " + msg);
  		if (msg == "Success") {
  			// no payment processed
  			$('#courseRecord').submit();
  		} else {
  			document.getElementById("overlay").style.visibility = "hidden";
  			displayErrorMsg($('#paymentType_msg'), msg);
  		}
  	}
  	
  	function calcPrice(amt) {
  		var total_students = $("#totalStudents").val();
		var students = total_students;
  		var total = amt * students;
  		total = Math.round(total * 100.0) / 100.0;
  		total = total.toFixed(2);
  		$('#amtPerStudent').text(amt.toFixed(2));
  		$('#totalPrice').text(total);
  		$('#payment\\.totalPrice').val(total);
  		// also update amount in credit card form
  		$('#amount').val(total);

  		if (total == 0.0) {
  			$('#payment-section').hide();
  			$('#purchaseOrderFields').hide();
			$('#creditCardFields').hide();
  			$('#payment\\.paymentTypeID').val("");
  			nopayment = true;
  		} else {
  			$('#payment-section').show();
  			nopayment = false;
  		}
  	}
  	
  	function cardType(card_num) {
  		visa_regex = /^4[0-9]{12}(?:[0-9]{3})?$/;
  		mastercard_regex = /^5[1-5][0-9]{14}$/;
  		american_express_regex = /^3[47][0-9]{13}$/;
  		discover_regex = /^6(?:011|5[0-9]{2})[0-9]{12}$/;
  		
  		var card_type = "-1";
  		if (visa_regex.test(card_num)) {
  			card_type = "001";
  		} else if (mastercard_regex.test(card_num)) {
  			card_type = "002";
  		} else if (american_express_regex.test(card_num)) {
  			card_type = "003";
  		} else if (discover_regex.test(card_num)) {
  			card_type = "004";
  		}
  		return card_type;
  	}
  	
  	function highlightCard() {
  		$('#visa-icon').attr("class", "inactive");
  		$('#mastercard-icon').attr("class", "inactive");
  		$('#american-express-icon').attr("class", "inactive");
  		$('#discover-icon').attr("class", "inactive");

  		var card_type = cardType($('#card_accountNumber').val());
 
  		if (card_type == "001") {
  			$('#visa-icon').attr("class", "active");
  			$('#card_cardType').val("001");
  		} else if (card_type == "002") {
  			$('#mastercard-icon').attr("class", "active");
  			$('#card_cardType').val("002");
  		} else if (card_type == "003") {
  			$('#american-express-icon').attr("class", "active");
  			$('#card_cardType').val("003");
  		} else if (card_type == "004") {
  			$('#discover-icon').attr("class", "active");
  			$('#card_cardType').val("004");
  		}
  	}
  	
  	function validateEmailFormat(email)
	{
		if(email=='') return false;
		
		var valid = true;
		//No @ sign
		if(email.indexOf('@') == -1) 
		{
			return false;
		}
		
		//Multiple @ signs
		if(email.lastIndexOf('@') != email.indexOf('@'))
		{
			return false;
		}
		
		var user = email.substr(0,email.indexOf('@'));
		var domain = email.substr(email.indexOf('@')+1);
		if(domain.indexOf('.') == -1)
		{
			return false;
		}
		return true;			
	}
	
	function validateExpiryDate(month, year){
		if(month=='' || year == '') return false;
		
		var today = new Date();
		var currMonth = today.getMonth()+1;
		var currYear = today.getFullYear();
		
		//If Future Year, Exp date is good.
		if(year > currYear) return true;
		
		//Past year, exp Date is bad.
		if(year < currYear) return false;
		
		//Reached here, means year is current. If month is past, exp date is bad.
		if(month < currMonth) return false;
		
		//Month is current or future. 
		return true;
	}
  	
  	
  	function processPayment() {
  		//console.log("process payment");
  		
  		centerModal();
		document.getElementById("overlay").style.visibility = "visible";
  		
		if (nopayment) {
			approveNoCostNoPaymentCR().then(processNoPayment);
			return;
		}
		
		var paymentTypeID = $('#payment\\.paymentTypeID').val();
  		if (paymentTypeID == "PO") {
  			//console.log("add purchase order");
  			addPurchaseOrder().then(processPOResponse);
  		} else if (paymentTypeID == "CC") {
			document.processPayment.card_expiry_date.value = document.processPayment.expMonth.value+'-'+document.processPayment.expYear.value;
			var signatureFields = ["access_key","profile_id","signed_date_time","signed_field_names","unsigned_field_names","transaction_uuid","locale","transaction_type","amount","bill_to_address_city","bill_to_address_country","bill_to_address_line1","bill_to_address_line2","bill_to_address_postal_code","bill_to_address_state","bill_to_email","bill_to_forename","bill_to_surname","reference_number","currency","ignore_avs","ignore_cvn","merchant_defined_data1","merchant_defined_data2","merchant_defined_data3","merchant_defined_data4","payment_method","tax_amount"];
			

			var dataToPost = {};
			dataToPost.merchantId = document.processPayment.merchantID.value; 
			for(i=0; i < document.processPayment.elements.length; i++)
			{
				var input = document.processPayment.elements[i];
				if($.inArray(input.name, signatureFields) > -1)
				{
					var name = input.name;
					var value = input.value;
					dataToPost[name] = value;
				}
			}
			
			var url = "/instructor/creditcard/generateSignatureAndInsertCCTrack.html";
			return $.ajax(url, {
				async: false, 
				type: "POST", 
				data: dataToPost,
				dataType: "json",
				success: function(data){
					if(data.signed_date_time != '' && data.signature != '')
					{
						document.processPayment.signed_date_time.value = data.signed_date_time;
						document.processPayment.signature.value = data.signature;
						//insertCCTrack();
						processCCResponse("Success");
					}
					else
					{
						document.getElementById("overlay").style.visibility = "hidden";
						alert("There was an error generating the signature. Please try again later");
						return false;
					}
			   },
			   error: function(request,status,errorThrown) {
					document.getElementById("overlay").style.visibility = "hidden";
					alert("Error while signature generation - "+request.status+":"+errorThrown);
					return false;
			   }
			});
  		} else if (paymentTypeID == "PL") {
   			//console.log("process pay later");
   			paymentPendingNotification().then(processPLResponse);
   		}
  	}
