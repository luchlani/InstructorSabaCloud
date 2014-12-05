<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>American Red Cross | Course Records</title>
	<link rel="shortcut icon" href="../images/favicon.ico">
	<link rel="stylesheet" href="../css/main.css" media="all">
	<link rel="stylesheet" href="../css/jquery.dataTables_themeroller.css">
	<link rel="stylesheet" href="../css/jquery.dataTables.css">
  	<link rel="stylesheet" href="../css/jquery-ui-1.10.3.custom.css" />
  	<link rel="stylesheet" href="../css/print-preview.css" type="text/css" media="screen">
  	
  	<style type="text/css">
		.dataTables_filter {
		     display: none;
		}
		
		.row_selected td {
  			background-color:#B0BED9;
		}
		
		#insert-image-button.ui-button {
		   background:red ;
		   border-radius: 0px;
		}
		
		.my-icon-eye {
			background-image: url(../images/eye.png) !important;
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

		tbody td, tbody th {
		  border-color: #ffffff;
		  border-style: solid;
		  border-width: 0px;
		}
		
		@media print {
	        .hideWhilePrinting { display:none; }
	        .ui-dialog-buttonpane { display:none; }
	        
		    @page PrintSection {
				size:8.5in 11in; 
				margin:.5in .5in .5in .5in; 
				mso-header-margin:.5in; 
				mso-footer-margin:.5in; 
				mso-paper-source:0;
			}
			
			.PrintSection {
				page:PrintSection;
			} 
			
			.non-printable { display: none; }
    		.printable { display: block; }
			
	     }
			
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
			
			var availableStatus = [
			<core:forEach var="value" items="${crStatusList}" varStatus="rowCount"> 
				<core:choose>
					<core:when test="${rowCount.last}">
						{ "value": "<core:out value="${value}"/>", "label": "<core:out value="${value}"/>" }
					</core:when>
					<core:otherwise>
						{ "value": "<core:out value="${value}"/>", "label": "<core:out value="${value}"/>" },
					</core:otherwise>
				</core:choose>
			</core:forEach> 
			];
			
			$( "#status" ).autocomplete({ source: availableStatus, autoFocus: true,  select: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); fnFilter_status();  return false; }, keyup: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); fnFilter_status(); return false; }, minLength: 0  }).focus(function(){ $(this).trigger('keydown.autocomplete'); });
	  	});
		
		function setCourseList(list){
			//console.log("list: "+JSON.stringify(list));
			$( "#course" ).autocomplete({ source: list.crCourseCodeList, autoFocus: true, scroll: true, select: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); fnFilter_course();  return false;},  keyup: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); fnFilter_course(); return false; }, minLength: 0  }).focus(function(){ $(this).trigger('keydown.autocomplete'); });
			$( "#sheetno" ).autocomplete({ source: list.crSheetNumberList, autoFocus: true, scroll: true, select: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); fnFilter_sheetno();  return false; },  keyup: function(event, ui) { event.preventDefault(); $(this).val(ui.item.label); fnFilter_sheetno(); return false; }, minLength: 0  }).focus(function(){ $(this).trigger('keydown.autocomplete'); });
 		}
 		
 		function getCourseList(showAll) {
		    var url = "/instructor/courselist/getCourseList.html?type=json&showAll="+(showAll?"true":"false");
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
			WindowObject.document.writeln("<html><head><title>American Red Cross | Course Records</title> <link rel=\"stylesheet\" href=\"../css/main.css\"></head>");
			WindowObject.document.writeln("<body class=\"print\" style=\"display: block;\" >");			
			WindowObject.document.writeln(DocumentContainer.innerHTML);
			WindowObject.document.writeln("</body></html>");		
			WindowObject.document.close();
			WindowObject.focus();
			WindowObject.print();
			WindowObject.close();
		}

	  	function viewRow(number){
			getCourseRecordEntry(number).then(populateViewModalWindow);	  	
	  	}
	  	
	  	function editRow(number){
		    $('#action').val("Edit");
		    $('#sheetNumber').val(number);
		    $('#feeOption').val("Fee")
		    document.forms["courseRecordSearch"].submit();				  	
	  	}
	  	
	  	function copyRow(number){
		    $('#action').val("Copy");
		    $('#sheetNumber').val(number);
		    $('#feeOption').val("Fee")
		    document.forms["courseRecordSearch"].submit();				  	
	  	}
	  	
	  	function deleteRow(number, id){
		    $('#courseRecordSheet').text("Course Record Sheet: " + number);
		    $('#deleteDialog').dialog({
				autoOpen: true,
 				width: 330,
 				modal: true,
 				buttons: {
  					"OK": function () {
						$(this).dialog("close");
						$('#action').val("Delete");
		    			$('#sheetNumber').val(number);
		    			$('#sheetNumberID').val(id);
		    			$('#feeOption').val("Fee")
		    			document.forms["courseRecordSearch"].submit(); 
  					}, 
  					"Cancel": function() { 
   						$(this).dialog("close"); 
  					} 
				}
			});			  	
	  	}
	  	
		function fnGetSelected( oTableLocal )
		{
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		
		function fnFilter_status ()
		{
		    $('#searchResults').dataTable().fnFilter(
		        $("#status").val(),
		        null,
		        null,
		        null
		    );
		}
		function fnFilter_sheetno ()
		{
		    $('#searchResults').dataTable().fnFilter(
		        $("#sheetno").val(),
		        null,
		        null,
		        null
		    );
		}
		function fnFilter_course ()
		{
		    $('#searchResults').dataTable().fnFilter(
		        $("#course").val(),
		        null,
		        null,
		        null
		    );
		}
		
		function populateViewModalWindow(List) {
			//console.log("List " + List);
			//console.log("List.courseRecord " + List.courseRecord);
			$('#username').text(List.courseRecord.username);
			$('#sheetnumber').text(List.courseRecord.sheetNumber);
			$('#organizationName').text(List.courseRecord.organizationName);
			$('#courseName').text(List.courseRecord.courseName);
			$('#createDate').text(List.courseRecord.createDate);
			$('#city').text(List.courseRecord.city);
			$('#endDate').text(List.courseRecord.endDate);
			$('#state').text(List.courseRecord.state);
			$('#trainingCenterName').text(List.courseRecord.trainingCenterName);
			$('#streetAddress').text(List.courseRecord.streetAddress);
			$('#county').text(List.courseRecord.county);
			$('#zipCode').text(List.courseRecord.zipCode);
			$('#totalStudents').text(List.courseRecord.totalStudents);
			
			<core:if test="${feeOption=='Fee' || feeOption=='Facility-fee'}" >
			$('#comments').text(List.courseRecord.comments);
			
			// hide all conditional sections
			$('#studentInfo').hide();
			$('#creditCardInfo').hide();
			$('#purchaseOrderInfo').hide();
			$('#referenceNumberInfo').hide();
			$('#approvalInfo').hide();
			$('#printCerts').hide();
			
			// select the appropriate label for payment reference and display payment info
			$('#paymentType').text(List.courseRecord.paymentType);
			if ($('#paymentType').text() == "Credit Card") {
				$('#cardAccountNumber').text(List.courseRecord.paymentReference);
				$('#creditCardInfo').show();
			} else if ($('#paymentType').text() == "Purchase Order") {
				$('#purchaseOrder').text(List.courseRecord.paymentReference);
				$('#purchaseOrderInfo').show();
			} else {
				$('#referenceNumber').text(List.courseRecord.paymentReference);
				$('#referenceNumberInfo').show();
			}

			var price = parseFloat(List.courseRecord.totalPrice);
			price = price.toFixed(2);
			$('#totalPrice').text(price);
			if (List.courseRecord.status == "Approved") {
				$('#paymentStatus').text("Completed");
				$('#approvalInfo').show();
			} else {
				$('#paymentStatus').text("Not Paid");
			}
			
			$('#offeringNumber').text(List.courseRecord.offeringNumber);
			$('#orderNumber').text(List.courseRecord.orderNumber);
			$('#approverName').text(List.courseRecord.approverName);
			$('#approvedDate').text(List.courseRecord.approvedDate);
			$('#approverComments').text(List.courseRecord.approverComments);
			</core:if>
			
			$('#instructors').empty();
		    for (var i = 0; i < List.courseRecord.instructors.length; i++) {
		       	//console.log("Instructor Name: " + List.courseRecord.instructors[i].firstName + " " + List.courseRecord.instructors[i].lastName);
				$('#instructors').append('<tr class="light-grey-out"><td>' + List.courseRecord.instructors[i].firstName + '</td><td>' + List.courseRecord.instructors[i].lastName  + '</td></tr>');

		    }
		    
		    <core:if test="${feeOption=='Fee'}" >
		    $('#students').empty();		// clear out whatever was loading last time
		    
		    if (List.courseRecord.students.length != 0) {
		    	$('#studentInfo').show();
		    	for (var i = 0; i < List.courseRecord.students.length; i++) {
			       	//console.log("Instructor Name: " + List.courseRecord.instructors[i].firstName + " " + List.courseRecord.instructors[i].lastName);
			       	var courseComponents = "";
					if(List.courseRecord.issuedCertificates == "true" && List.courseRecord.status != "Cancelled")   
					{
						courseComponents += "<table>";
				       	for(var j = 0; j < List.courseRecord.students[i].courseComponents.length; j++) {
				       		courseComponents += "<tr><td style='padding:0px;margin:0px;border:none'>";
				       		courseComponents += List.courseRecord.students[i].courseComponents[j].courseComponentLabel;
				       		courseComponents += ": ";
				       		courseComponents += List.courseRecord.students[i].courseComponents[j].courseComponentValue;
				       		courseComponents += "</td></tr>";
				       	}
				       	courseComponents += "</table>";
				    }
			       	
			       	
			       	
			       	
					$('#students').append('<tr class="light-grey-out"><td>' + List.courseRecord.students[i].firstName + '</td><td>' + List.courseRecord.students[i].lastName  + '</td><td>' + List.courseRecord.students[i].email + '</td><td>' + List.courseRecord.students[i].phoneNumber + '</td><td>' + List.courseRecord.students[i].addlInfo + '</td><td>' + courseComponents + '</td></tr>');
			    }
		    }
		    </core:if>
		   	
		    if (List.courseRecord.showCertificates == "true") {
		    	if (List.courseRecord.students.length != 0) {
			    	$('#printCerts').show();
		    	}
			    $('#viewDialog').dialog({
					autoOpen: true,
	 				width: 1000,
	 				modal: true,
	 				position: {my: "center", at: "center", of: window},
	 				buttons: {
	 					"Print Summary": function() {
 	 						$('#viewDialog').dialog("close");
	 						$('#viewDialog').dialog({autoOpen: true, width: 1000, modal: true, position: {my: "left top", at: "left top", of: document, collision: "none"}});
	 						window.print();
	  					},
						 "Print 8.5 x 11 Certificates": function() { 
	 						var certificatesURL = window.location.protocol.toString() + "//" + window.location.host.toString() + "/Saba/Web/Main/goto/FullCertificate?offeringId=" + List.courseRecord.crsID;
	 				  		
	 						window.open(certificatesURL,'_blank');
	  					},
	  					"Print Wallet Certificates": function() { 
	 						var certificatesURL = window.location.protocol.toString() + "//" + window.location.host.toString() + "/Saba/Web/Main/goto/WalletCertificate?offeringId=" + List.courseRecord.crsID;
	 				  		
	 						window.open(certificatesURL,'_blank');
	  					},
	  					"Close": function() { 
	   						$(this).dialog("close"); 
	  					}
					}
				});		
		    } else {
			    $('#viewDialog').dialog({
					autoOpen: true,
	 				width: 700,
	 				modal: true,
	 				position: {my: "center", at: "center", of: window},
	 				buttons: {
	 					"Print Summary": function() { 
	 						$('#viewDialog').dialog("close");
	 						$('#viewDialog').dialog({autoOpen: true, width: 700, modal: true, position: {my: "left top", at: "left top", of: document, collision: "none"}});
							 window.print();
	  					},
	  					"Close": function() { 
	   						$(this).dialog("close"); 
	  					}
					}
				});		
		    }
		    
		}
		
		function getCourseRecordEntry(sheetnumber) {
		    var url = "/instructor/view/courseRecordEntry.html";
			return $.ajax(url, {
				        data: {
				            sheetNumber: sheetnumber
				        },
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
		
		
		$(document).ready(function( $ ) {
		
			$( document ).tooltip();
			
			$('#viewDialog').hide();
			
			$("#searchResults tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
			});
			
			$.extend( $.fn.dataTable.defaults, {
		        "bJQueryUI": true,
		        "bFilter": true,
		        "bPaginate": true, 
		        "bLengthChange": true,
				"bAutoWidth": false,
				"aaSorting": [],
				"aoColumns": [
				               { "mData": "id","sName": "id", "sTitle": "ID","sWidth": null, "bVisible": false }, 
                               { "mData": "sheetNumber","sName": "sheetNumber", "sTitle": "Course Record","sWidth":  null }, 
                               { "mData": "organizationName", "sName": "organizationName", "sTitle": "Organization Name","sWidth":  null },  
                               { "mData": "organizationID","sName": "organizationID", "sTitle": "Organization ID","sWidth":  null },  
                               { "mData": "courseName","sName": "courseName", "sTitle": "Course","sWidth":  null },
                               { "mData": "endDate","sName": "endDate", "sTitle": "End Date","sWidth":  null  },
                               { "mData": "status","sName": "status", "sTitle": "Status","sWidth":  null },
                               { "mData": "action","sName": "action", "sTitle": "Actions","sWidth": null, "bSortable": false } 
                           ]                           
    		} );
			
			$.fn.dataTableExt.oApi.fnReloadAjax = function ( oSettings, sNewSource, fnCallback, bStandingRedraw )
			{
			    if ( sNewSource !== undefined && sNewSource !== null ) {
			        oSettings.sAjaxSource = sNewSource;
			    }
			 
			    // Server-side processing should just call fnDraw
			    if ( oSettings.oFeatures.bServerSide ) {
			        this.fnDraw();
			        return;
			    }
			 
			    this.oApi._fnProcessingDisplay( oSettings, true );
			    var that = this;
			    var iStart = oSettings._iDisplayStart;
			    var aData = [];
			 
			    this.oApi._fnServerParams( oSettings, aData );
			 
			    oSettings.fnServerData.call( oSettings.oInstance, oSettings.sAjaxSource, aData, function(json) {
			        /* Clear the old information from the table */
			        that.oApi._fnClearTable( oSettings );
			 
			        /* Got the data - add it to the table */
			        var aData =  (oSettings.sAjaxDataProp !== "") ?
			            that.oApi._fnGetObjectDataFn( oSettings.sAjaxDataProp )( json ) : json;
			 
			        for ( var i=0 ; i<aData.length ; i++ )
			        {
			            that.oApi._fnAddData( oSettings, aData[i] );
			        }
			         
			        oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
			 
			        that.fnDraw();
			 
			        if ( bStandingRedraw === true )
			        {
			            oSettings._iDisplayStart = iStart;
			            that.oApi._fnCalculateEnd( oSettings );
			            that.fnDraw( false );
			        }
			 
			        that.oApi._fnProcessingDisplay( oSettings, false );
			 
			        /* Callback user function - for event handlers etc */
			        if ( typeof fnCallback == 'function' && fnCallback !== null )
			        {
			            fnCallback( oSettings );
			        }
			    }, oSettings );
			};


    		oTable = $('#searchResults').dataTable({ 
				"oLanguage": {
					"sEmptyTable": "Add a course record entry."
				},
				"sPaginationType": "full_numbers",
			    "bProcessing": true,
        		"bServerSide": false,
        		"sAjaxSource": '/instructor/existingcourserecord/existingCourseRecordList.html?type=json&showAll=${showAll}'
			});
    		
			
			$("#status").keyup( fnFilter_status );
			$("#sheetno").keyup( fnFilter_sheetno );
			$("#course").keyup( fnFilter_course );

			$("#status").mouseup( fnFilter_status );
			$("#sheetno").mouseup( fnFilter_sheetno );
			$("#course").mouseup( fnFilter_course );
			
			$("#status").focusout(function() { $('#status').val(""); });
			$("#sheetno").focusout(function() { $('#sheetno').val(""); });
			$("#course").focusout(function() { $('#course').val(""); });
			
			$("#status").click(function() { $(this).autocomplete("search", ""); });
			$("#sheetno").click(function() { $(this).autocomplete("search", ""); });
			$("#course").click(function() { $(this).autocomplete("search", ""); });
			
			/*
			$('a[name=edit]').click(function(){
			    var form = $(this), data = form.data('params');
				editRow(data);
			});

			$('a[name=delete]').click(function(){
			    var form = $(this), data = form.data('params');
			    deleteRow(data);
			});
			
			$('a[name=view]').click(function(){
				var form = $(this), data = form.data('params');
				viewRow(data);
			});
			*/
			
			$('#fee').click(function(){
			    $('#action').val("Create");
			    $('#sheetNumber').val("");
			    $('#feeOption').val("Fee");
			    document.forms["courseRecordSearch"].submit();
			    
			});
			
			$('#non-fee').click(function(){
			    $('#action').val("Create");
			    $('#sheetNumber').val("");
			    $('#feeOption').val("Non-fee");
			    document.forms["courseRecordSearch"].submit();
			    
			});
			
			/*
			$('#searchResults tbody tr').click(function () {        
			    var aData = oTable.fnGetData(this); 
			    if (null != aData) 
			    {
			        //console.log("Column 1 " + aData[0]);
			        //console.log("Column 2 " + aData[1]);
			        //console.log("Column 3 " + aData[2]);
			    }
			});
			*/
			
			// replace the search filter with a checkbox to show all records
			$('#searchResults_filter').html("<div title=\"Check this box to view all course records associated with your organization(s).  If left unchecked, you will see only courses that you created or for which you were listed as the instructor.\">Show all course records <input type=\"checkbox\" id=\"allRecords\" ${showAll=='true'?'checked':''}></div>");
			$('#searchResults_filter').show();
			$('#allRecords').click(function(){
				if ($('#allRecords').is(':checked')) {
					//console.log("show all records");
					oTable.fnReloadAjax("/instructor/existingcourserecord/existingCourseRecordList.html?type=json&showAll=true");
					getCourseList(true).then(setCourseList);
				} else {
					//console.log("show my records");
					oTable.fnReloadAjax("/instructor/existingcourserecord/existingCourseRecordList.html?type=json&showAll=false");
					getCourseList(false).then(setCourseList);
				}
			});
			
			<core:choose>
				<core:when test="${showAll != null}">
					getCourseList(${showAll}).then(setCourseList);
				</core:when>
				<core:otherwise>
					getCourseList(false).then(setCourseList);
				</core:otherwise>
			</core:choose>

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
<div class="non-printable">
<div id="hdr" class="cert-hdr">
	<header class="page-header" role="banner">
		<a href="//www.redcross.org" class="branding" target="_blank">
			<img class="logo" border=0 src="../images/redcross-logo.png" alt="American Red Cross">
		</a>
		<div style="position:relative; top:25px; float:right; right:25px;"> 
			<p class="light-grey-out"> Welcome <span class="grey-out"><core:out value="${user.username}" /></span>; Click here to <a href="<core:url value="/authentication/logout.html"/>" >logout.</a></p>
		</div>
	</header>
</div>
<div class="left-rail content-wrap default-layout">
	<div class="content student-page">
		<aside class="sidebar" role="complementary">
		    <nav class="sidebar-inner">
		        <ul class="nav-list">
		        	<core:if test="${feeOption=='Fee'}" >
		            <li class="nav-item current">
		                <a class="nav-link" href="<core:url value="/search/courseRecordSearch.html"/>">
		                <span>Search existing course records</span>
		                </a>
		            </li>
		            <li class="nav-item" title="Selecting this link will open a new tab. You will be required to login to the Learning Center.">
		                <a class="nav-link" onclick="SearchClassParticipants()" href="#">
		                <span>Search class participants</span>
		                </a>
		            </li>
		            <li class="nav-item">
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
		            <core:if test="${feeOption=='Non-fee'}" >
		          	<li class="nav-item">
		                <a class="nav-link" href="<core:url value="/course/courseRecordSheet.html"/>">
		                <span>Create new course record</span>
		                </a>
		            </li>
		            <li class="nav-item current">
		                <a class="nav-link" href="<core:url value="/search/courseRecordSearch.html"/>">
		                <span>Search existing course records</span>
		                </a>
		            </li>
		            </core:if>
		            <core:if test="${feeOption=='Facility-fee'}" >
                    <li class="nav-item">
                        <a class="nav-link" href="<core:url value="/course/courseRecordSheet.html"/>">
                        <span>Create new course record: LTS Facility Fee</span>
                        </a>
                    </li>
                    <li class="nav-item current">
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
			    <core:if test="${feeOption=='Facility-fee'}" >
				<h1>Search Existing Course Records: LTS Facility Fee</h1>
				</core:if>
				<core:if test="${feeOption!='Facility-fee'}" >
                <h1>Search Existing Course Records</h1>
                </core:if>
			</header>
			<div style="color: #6D6E70;">
				<p>To find an existing course record, enter the filter information below.  Use the action icons next to each course record to manage existing course records.</p>
			</div>
			Filter by:
			<form:form method="POST" commandName="courseRecordSearch">
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
								<div class="standard-table course-table catalog-list-table">
									<fieldset>
									<table class="">
										<tr>
										<tbody>
											<td id="filter_global" class="">
												<div class="field-wrap">
													<label for="" class="no-asterisk">Status</label>
													<div class="field">
														<input type="text" name="status" id="status" class="input-large">
													</div>
												</div>
											</td>
											<td id="filter_global" class="">
												<div class="field-wrap">
													<label for="" class="no-asterisk">Course Record</label>
													<div class="field">
														<input type="text" name="sheetno" id="sheetno" class="input-large">
													</div>
												</div>
											</td>
											<td id="filter_global" class="">
												<div class="field-wrap">
													<label for="" class="no-asterisk">Course Name</label>
													<div class="field">
														<input type="text" name="course" id="course" class="input-large">
													</div>
												</div>
											</td>
										</tr>
				   						</tbody>
									</table>
									</fieldset>
								</div>
					</div>
				</div>
				<div class="field-wrap">
					<form:hidden path="sabaMessage" />
					<form:errors path="sabaMessage" cssClass="msgError" />
				</div>
				<core:if test="${showFunctionality}">
				<table class="">
					<tfoot>
						<tr>
							<td><a href="javascript:void(0);" id="fee" name="create"><span class="ui-icon ui-icon-plusthick" style="display:inline-block"></span>Fee Based Course Entry</a> &nbsp;<a href="javascript:void(0);" id="non-fee" name="create"><span class="ui-icon ui-icon-plusthick" style="display:inline-block"></span>Anonymous Non-Fee Course Entry</a></td>
						</tr>
					</tfoot>
				</table>
				</core:if>
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="searchResults">
					<thead>
						<tr>
							<th>ID</th>
							<th>Course Record</th>
							<th>Organization Name</th>
							<th>Organization ID</th>
							<th>Course</th>
							<th>End Date</th>
							<th>Status</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<form:hidden path="action" />
				<form:hidden path="sheetNumber" />
				<form:hidden path="sheetNumberID" />
				<form:hidden path="feeOption" />		
			</form:form>
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

<div id="deleteDialog" class="window" title="Delete Course Record">
	<table align="center" width="85%">
	<tr>
	<td>
	<p id="courseRecordSheet"></p>	
	</td>
	</tr>
	</table>	
</div>
</div>
<div class="printable">
<div id="viewDialog" class="window" title="Viewing Course Record Entry">
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>American Red Cross | Course Records</title>
	<link rel="shortcut icon" href="../images/favicon.ico">
    <script src="../js/jquery.tools.js"></script>
    <script type="text/javascript">
    	var $JQ = jQuery.noConflict(true);
    </script>
    <script src="../js/jquery.print-preview.js" type="text/javascript" charset="utf-8"></script>
    
    
    <script type="text/javascript">
        $(function() {
            $JQ('a.print-preview').printPreview();
        });
    </script>
</head>
<body> 	
	<!-- <div style="position: relative; float: right; padding-bottom: 5px; padding-right: 30px; top: 55px;"><a class="print-preview">Print this page</a></div> -->
	<div id="PrintSection" class="PrintSection printable">
	<div id="summary-hdr" class="print-hdr">
	<header style="height: 96px;margin: 0 auto;padding: 0 12px;" role="banner">
		<a href="//www.redcross.org" class="branding" target="_blank">
			<img class="logo" border=0 src="../images/redcross-logo.png" alt="American Red Cross">
		</a>
	</header>
	</div>
	<div class="corners">
	<div class="simple-form tabular-form">
		<div class="fieldset-group" autofocus>
			<fieldset>
				<legend>
					<!-- 
					<p>The information you entered appears at the top of this page. Enter the student information in the fields provided. Use the drop down menus to select the completion status for each component for each student. Click Review.</p>
					-->
				</legend>
				<div class="field-wrap min-fifty-percent" style="margin-right: 35px;">
					<label for="" class="grey-out no-asterisk" style="width: auto; padding-right: 20px;">Contact:</label>
					<div class="field light-grey-out">
						<span id="username"></span>
					</div>
				</div>
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk" style="width: auto; padding-right: 20px;">Course Record Sheet No.:</label>
					<div class="field light-grey-out">
						<span id="sheetnumber"></span>
					</div>
				</div>
			</fieldset>
			<fieldset>
				<div class="field-wrap">
					<div class="hr"></div>
				</div>
			</fieldset>
			<fieldset>							
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Date Submitted:</label>
					<div class="field  light-grey-out">
						<span id="createDate"></span>
					</div>
				</div>
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Organization:</label>
					<div class="field light-grey-out">
						<span id="organizationName"></span>
					</div>
				</div>
				<div class="field-wrap">
				    <core:if test="${feeOption=='Facility-fee'}" >
					<label for="" class="grey-out no-asterisk">LTS Program Category:</label>
					</core:if>
					<core:if test="${feeOption!='Facility-fee'}" >
                    <label for="" class="grey-out no-asterisk">Course:</label>
                    </core:if>
					<div class="field light-grey-out">
						<span id="courseName"></span>
					</div>
				</div>
				<core:if test="${feeOption!='Facility-fee'}" >
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Course Ending Date:</label>
					<div class="field light-grey-out">
						<span id="endDate"></span>
					</div>
				</div>
				</core:if>
				<core:if test="${feeOption!='Facility-fee'}" >
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Total Students:</label>
					<div class="field light-grey-out">
						<span id="totalStudents"></span>
					</div>
				</div>
				</core:if>
				<core:if test="${feeOption=='Fee'}" >
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Training Center Name:</label>
					<div class="field light-grey-out">
						<span id="trainingCenterName"></span>
					</div>
				</div>
				</core:if>
				<core:if test="${feeOption=='Facility-fee'}" >
                <div class="field-wrap">
                    <label for="" class="grey-out no-asterisk">Mailing Address Name:</label>
                    <div class="field light-grey-out">
                        <span id="trainingCenterName"></span>
                    </div>
                </div>
                </core:if>
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Street Address:</label>
					<div class="field light-grey-out">
						<span id="streetAddress"></span>
					</div>
				</div>
				<div class="field-wrap">
					<label for="city" class="grey-out no-asterisk">City:</label>
					<div class="field light-grey-out">
						<span id="city"></span>
					</div>
				</div>
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">State:</label>
					<div class="field light-grey-out">
						<span id="state"></span>
					</div>
				</div>
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Postal Code:</label>
					<div class="field light-grey-out">
						<span id="zipCode"></span>
					</div>
				</div>
				<core:if test="${feeOption=='Fee'}" >
				<div class="field-wrap">
					<label for="" class="grey-out no-asterisk">Comments:</label>
					<div class="field light-grey-out">
						<span id="comments" style="white-space: pre-line"></span>
					</div>
				</div>
				</core:if>
			</fieldset>
		</div>
		
		<core:if test="${feeOption!='Facility-fee'}" >
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
				   <tbody id="instructors">
				   </tbody>
				</table>
			</div>
		
			</fieldset>
		</div>
		</core:if>
		
		<core:if test="${feeOption=='Fee'}" >
		<div id="studentInfo" class="fieldset-group" style="display: none;">
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
				         <th class="" scope="col"><span class="col-header">Additional Info</span></th>
				         <th class="" scope="col"><span class="col-header">Components</span></th>
				      </tr>
				   </thead>
				   <tbody id="students">
				</table>
			</div>
		
			</fieldset>
		</div>
		</core:if>
		
	</div>
	</div>
	
	<core:if test="${feeOption=='Fee' || feeOption=='Facility-fee'}" >
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
							<div class="field light-grey-out">
								<span id="paymentType"></span>
							</div>
						</div>

						<div class="field-wrap" id="creditCardInfo" style="display: none;">
							<label for="" class="no-asterisk grey-out">Credit Card #</label>
							<div class="field light-grey-out">
								<span id="cardAccountNumber"></span>
							</div>
						</div>


						<div class="field-wrap" id="purchaseOrderInfo" style="display: none;">
							<label for="" class="no-asterisk grey-out">Purchase Order #</label>
							<div class="field light-grey-out">
								<span id="purchaseOrder"></span>
							</div>
						</div>
						
						<div class="field-wrap" id="referenceNumberInfo" style="display: none;">
							<label for="" class="no-asterisk grey-out">Reference #</label>
							<div class="field light-grey-out">
								<span id="referenceNumber"></span>
							</div>
						</div>

						<div class="field-wrap">
							<label for="" class="no-asterisk grey-out">Amount</label>
							<div class="field light-grey-out">
								<span id="totalPrice"></span>
							</div>
						</div>
						<div class="field-wrap">
							<label for="" class="no-asterisk grey-out">Payment Status</label>
							<div class="field light-grey-out">
								<span id="paymentStatus"></span>
							</div>
						</div>
						</fieldset>
					</div>
				</div>
				</fieldset>
			</div>
		</div>	
	</div>
	
	<div id="approvalInfo" class="corners PrintSection" style="display: none;">
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
									<div class="field light-grey-out">
										<span id="offeringNumber"></span>
									</div>
								</div>
								<div class="field-wrap">
									<label for="" class="no-asterisk grey-out">Order No.:</label>
									<div class="field light-grey-out">
										<span id="orderNumber"></span>
									</div>
								</div>
								<div class="field-wrap">
									<label for="" class="no-asterisk grey-out">Approved By:</label>
									<div class="field light-grey-out">
										<span id="approverName"></span>
									</div>
								</div>
								<div class="field-wrap">
									<label for="" class="no-asterisk grey-out">Approved Date:</label>
									<div class="field light-grey-out">
										<span id="approvedDate"></span>
									</div>
								</div>
								<div class="field-wrap">
									<label for="" class="no-asterisk grey-out">Approval Comments:</label>
									<div class="field light-grey-out">
										<span id="approverComments"></span>
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
	
	</div>
</body>
</html>
</div>	
</div>	
</body>
</html>
