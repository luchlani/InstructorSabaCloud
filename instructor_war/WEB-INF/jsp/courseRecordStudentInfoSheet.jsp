<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<%
response.setHeader("Cache-Control","no-cache no-store"); 
response.setHeader("Cache-Control", "must-revalidate");
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0); 
%>
<!DOCTYPE html>
<html>
<head>
	<title>American Red Cross | Course Records</title>
	
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
		
		select[name="value"] { width: 90px; }
		
		.ui-autocomplete { position: absolute; height: 150px; font-weight: normal; font-style: normal; font-size: 11px; overflow-y: scroll; overflow-x: hidden;}

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
  	<script src="../js/jquery.jeditable.mini.js"></script>
  	<script src="../js/instructor.js"></script>
  	<script src="../js/json2.js"></script>
  	
  	<script type="text/javascript" charset="utf-8">
  		var invalid_name_present = false;

  		// global boolean to keep track of whether there is an invalid email that needs to be fixed
  		var invalid_email_present = false;
  		var invalid_emails_present = new Array();
  		
  		// global boolean to keep track of whether there is an invalid user that needs to be fixed
  		var invalid_user_present = false;
  		var invalid_users_present = new Array();
  	 	
  	 	var oTable;

  	 	var select_next = false;
  	 	
  	 	var studentRowCount = {
  	  	 	count : 0
  	  	}
  	 	
  	 	var studentColumnCount = {
  	 		count : 0
  	 	}
  	 	
  	 	function nextElementSibling(el) {
  	 	    if (el.nextElementSibling) return el.nextElementSibling;
  	 	    do { el = el.nextSibling } while (el && el.nodeType !== 1);
  	 	    return el;
  	 	}
  	 	
  	 	function loadStudentTable(data){
  	 		
  	 		email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

	       	$('#studentTableColumns').empty();
	    	studentColumnCount.count = 0;
	    	for (c in data.aoColumns){
	    	  //console.log("Table Column Name " + data.aoColumns[c].sTitle);
			  $('#studentTableColumns').append('<th>' + data.aoColumns[c].sTitle + '</th>');
			  studentColumnCount.count = studentColumnCount.count + 1;
			}
			
			//console.log("Column Count [" + studentColumnCount.count + "]");
			oTable = $('#searchResults').dataTable({
	        	"oLanguage": {
					"sEmptyTable": "Add student(s) to the course record entry.",
					"sInfo": "Showing _START_ to _END_  of _END_ "
				},
				"bDestroy": true,
				"bProcessing": true,
				"aaSorting": data.aaSorting,
	        	"aoColumns": data.aoColumns,
	        	"aaData": data.aaData
	        });
	        
	        $('#searchResults tbody td').change( function () {
	        	var aPos = oTable.fnGetPosition( this );
				//var aData = oTable.fnGetData( aPos[0] );
				var row_id =  aPos[0];
				var column_id =  aPos[2];
				var value = "";
				var obj = this;
				
				if (column_id >= 8) {  // column >= 8 is a select box, not input text
					$(this).closest('td').find("select").each(function() {
						obj = this;
	        			value = $(this).find(":selected").text();
	    			});
				} else {
					$(this).closest('td').find("input").each(function() {
						obj = this;
						value = this.value;
	    			});
				}
				
				if(value != 'undefined' && value != null){
					if (column_id == 2 || column_id == 3 || column_id == 4) {
						// validate student
						var first = oTable.fnGetNodes()[row_id].children[0].firstChild.value;
						var last = oTable.fnGetNodes()[row_id].children[1].firstChild.value;
						var email = oTable.fnGetNodes()[row_id].children[2].firstChild.value;
						if (first == '' || last == '' || email == '') {  // clear error if any of first, last or email was removed 
							setInvalidUserError(row_id, '');
						} else if (email_regex.test(email)) {
							ValidateStudent(first, last, email, obj, row_id);
						}
					} else if (column_id >= 8) {
						Edit(row_id, column_id, value);
					}
				}
	        
	        });
	        
		  	$('#searchResults tbody td').focusout( function () {
				var aPos = oTable.fnGetPosition( this );
				//var aData = oTable.fnGetData( aPos[0] );
				var row_id =  aPos[0];
				var column_id =  aPos[2];
				var value = "";
				var obj = this;
				
				if (column_id >= 8) {  // column >= 8 is changed on change event, ignore on focusout event
					return;
				}
				
				$(this).closest('td').find("input").each(function() {
					obj = this;
					value = this.value;
    			});
				
				if(value != 'undefined' && value != null) {
					// validate email before sending to server
					if (column_id == 4) {
						if (value == '' || email_regex.test(value)) {  // clear error
							invalid_emails_present[row_id] = false;
							if(invalid_users_present[row_id] == ''){
								obj.style.color='black';
							}
						} else {
							invalid_emails_present[row_id] = true;
							obj.style.color='red';
						}
					}
					Edit(row_id, column_id, value);
				}
		  	});
		  	
		  	$('#searchResults tbody td').on('keydown', function(e) {
		  		// TAB KEY HANDLER
		  	    if (e.which == 9) {
		  	    	var aPos = oTable.fnGetPosition( this );
					var row_id =  aPos[0];
					var column_id =  aPos[2];
					var value = "";
					var obj = this;
					
					$(this).closest('td').find("input").each(function() {
						obj = this;
						value = this.value;
	    			});
					
					var row_count = $('#studentTableData tr').length;

		  			if (column_id == 7) {
		  				e.preventDefault();
		  				
		  				if ((row_id+1) == row_count) {
		  					if(value != "" && value != 'undefined' && value != null) {  // save the value before adding new row or else it is not displayed 
			  					Edit(row_id, column_id, value);
			  				}

			  				select_next = true;
			  				Add();
		  				} else {
		  					// get next row, first column
		  					var parentEl = this.parentElement;
		  					var tmp = nextElementSibling(parentEl).children[0].firstChild;
		  					tmp.focus();
		  				}

		  			}
		  	    }
		  	});
		  	
		  	if (select_next == true) {
		  		var row_count = oTable.fnGetNodes().length;
		  		var first_col = oTable.fnGetNodes(row_count-1).children[0].firstChild;
		  		first_col.select();
		  		first_col.focus();
		  		select_next = false;
		  	}
		  	
		  	// fix to set the text color on invalid fields after data table is rebuilt
		  	var row_count = oTable.fnGetNodes().length;
		  	for (var i=0; i<row_count; i++) 
			{
  	 			if (invalid_emails_present[i] == true) 
				{
  	 				oTable.fnGetNodes()[i].children[2].firstChild.style.color = 'red';
  	 			}
  	 			if (invalid_users_present[i] != null && invalid_users_present[i] != '' && invalid_users_present[i] != 'undefined') 
				{
  	 				setInvalidUserError(i, invalid_users_present[i])
  	 			}
  	 		}
		  	
  	 	}
  	 	
  	 	function loadUpdatedTable(data) {
  	 		//console.log("Updated Data: "+data);
  	 	}
  	 	  	 	
  	 	function Delete(row_id) {
			var first_name = oTable.fnGetNodes(parseInt(row_id)).children[0].firstChild.value;
			var last_name = oTable.fnGetNodes(parseInt(row_id)).children[1].firstChild.value;
			//console.log("Delete " + first_name + " " + last_name);
		    $('#courseRecordSheet').text("Delete "+first_name+" "+last_name+"?");

		    $('#deleteDialog').dialog({
				autoOpen: true,
 				width: 330,
 				modal: true,
 				buttons: {
  					"OK": function () {
  						$(this).dialog("close");
 						var url = '/instructor/student/studentRoster.html?action=Delete&type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>';
						$.post(	url, 
								{ id: row_id}, 
								function ( data ) {
									invalid_emails_present.splice(row_id,1);
									invalid_users_present.splice(row_id,1);
									loadStudentTable(data); 
									}, 
								"json");
						
  					}, 
  					"Cancel": function() { 
   						$(this).dialog("close"); 
  					} 
				}
			});
  	 	}
  	 	
  	 	function Add() {
 			var url = '/instructor/student/studentRoster.html?action=Create&type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>';
			$.post(url, {},function ( data ) {	loadStudentTable(data); }, "json");  	
  	 	}
  	 	
  	 	function Edit(row_id, column_id, cell_value) {
 			var url = '/instructor/student/studentRoster.html?action=Edit&type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>';
			$.post(url, { id: row_id, column: column_id, value: cell_value}, function ( data ) { loadUpdatedTable(data); }, "json");
		}
  	 	
  	 	function validateNames() {
  	 		invalid_name_present = false;
  	 		var row_count = oTable.fnGetNodes().length;
  	 		for (var i=0; i<row_count; i++) {
  	 			var first = oTable.fnGetNodes()[i].children[0].firstChild.value;
  	 			var last = oTable.fnGetNodes()[i].children[1].firstChild.value;
  	 			if ((first==''&&last!='')||(first!=''&&last=='')) {
					invalid_name_present = true;
  	 			}
  	 		}
  	 	}
  	 	
  	 	function ValidateStudent(first, last, email, obj, row) {
  	 		
 			var url = '/instructor/uniquestudent/validateStudent.html?type=json';
			$.post(url, { first: first, last: last, email: email}, function ( data ) {
				if (data.found == 'false' && data.results > 0) {
					var msg = getDuplicateMsg( data.first_name, data.last_name, email);
					setInvalidUserError(row, msg);
					issueUserWarning(data.first_name, data.last_name, email);
				} else {
					setInvalidUserError(row, '');
				}
			}
			, "json");
		}
		
		function setInvalidUserError(row, msg)
		{
			if(msg == null || msg == '')
			{
				invalid_users_present[row] = '';
				oTable.fnGetNodes()[row].children[0].firstChild.style.color = 'black';
				oTable.fnGetNodes()[row].children[1].firstChild.style.color = 'black';
				oTable.fnGetNodes()[row].children[2].firstChild.style.color = 'black';
				oTable.fnGetNodes()[row].children[0].firstChild.removeAttribute('title');
				oTable.fnGetNodes()[row].children[1].firstChild.removeAttribute('title');
				oTable.fnGetNodes()[row].children[2].firstChild.removeAttribute('title');
			}
			else
			{
				invalid_users_present[row] = msg;
				oTable.fnGetNodes()[row].children[0].firstChild.style.color = 'red';
				oTable.fnGetNodes()[row].children[1].firstChild.style.color = 'red';
				oTable.fnGetNodes()[row].children[2].firstChild.style.color = 'red';
				oTable.fnGetNodes()[row].children[0].firstChild.title = msg;
				oTable.fnGetNodes()[row].children[1].firstChild.title = msg;
				oTable.fnGetNodes()[row].children[2].firstChild.title = msg;
			}
		}
		
		function getDuplicateMsg(saba_first, saba_last, username)
		{
		 	var dupMsg = 'Another user found with username ' + username + '. ';
			dupMsg += 'However, name entered does not match the name in the system (' + saba_first + ' ' + saba_last + '). ';
			dupMsg += 'Please correct the name entered or choose another email.';
			return dupMsg;
		}
  	 	
  	 	function validateEmails() {
  	 		invalid_email_present = false;
  	 		var row_count = oTable.fnGetNodes().length;
  	 		for (var i=0; i<row_count; i++) {
  	 			if (invalid_emails_present[i] == true) {
					invalid_email_present = true;
  	 			}
  	 		}
  	 		
  	 		if(invalid_email_present == false)
  	 		{
				email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
				for (var i = 0; i < row_count; i++) 
				{
					var email = oTable.fnGetNodes()[i].children[2].firstChild.value;
					if(email == '' || email_regex.test(email))
					{
						continue;
					}
					else
					{
						invalid_email_present[i] = true;
						invalid_email_present = true;
						oTable.fnGetNodes()[i].children[2].firstChild.style.color = 'red';
					}	
				}
  		 	}
  		 	
  		}
  	 	
  	 	function validateUsers() 
		{
  	 		invalid_user_present = false;
  	 		var row_count = oTable.fnGetNodes().length;
  	 		for (var i=0; i<row_count; i++) 
			{
  	 			if (invalid_users_present[i] != null && invalid_users_present[i] != '' && invalid_users_present[i] != 'undefined') 
				{
					invalid_user_present = true;
  	 			}
  	 		}
  	 		
			if(invalid_user_present == false)
  	 		{
				var url = '/instructor/uniquestudent/validateStudent.html?type=json';
				for (var i = 0; (invalid_user_present == false && i < row_count); i++) 
				{
					var first = oTable.fnGetNodes()[i].children[0].firstChild.value;
					var last = oTable.fnGetNodes()[i].children[1].firstChild.value;
					var email = oTable.fnGetNodes()[i].children[2].firstChild.value;
					if(first == '' || last == '' || email == '')
					{
						continue;
					}
					else
					{
						$.ajax(	{
									type: "POST",
									url: url,
									data: { first: first, last: last, email: email}, 
									async: false, 
									dataType: "json",
									success: 
										function ( data ) 
										{
											if (data.found == 'false' && data.results > 0) 
											{
												var msg = getDuplicateMsg(data.first_name, data.last_name, email);
												setInvalidUserError(i, msg);
												invalid_user_present = true;
											} 
										}
									}
							);
					}
				}
			}
  	 	}
  	 	
  	 	
  	 	function hasDuplicateEmails() {
  	 		var row_count = oTable.fnGetNodes().length;
  	 		for (var i=0; i<row_count; i++) {
  	 			var instances = 0;
  	 			var email = oTable.fnGetNodes()[i].children[2].firstChild.value.toLowerCase();
  	 			if (email != '') {
	  	 			for (var j=0; j<row_count; j++) {
	  	 				var anotherEmail = oTable.fnGetNodes()[j].children[2].firstChild.value.toLowerCase();
						if (email == anotherEmail) {
	  	 					instances++;
	  	 					if (instances > 1) {
	  	 						oTable.fnGetNodes()[i].children[2].firstChild.style.color = 'red';
	  	 						oTable.fnGetNodes()[j].children[2].firstChild.style.color = 'red';
	  	 						return true;
	  	 					}
	  	 				}
	  	 			}
  	 			}
  	 		}
  	 		return false;
  	 	}
  	 	
		function centerModal() {
		    var top, left;
		
		    top = Math.max($(window).height() - $('#overlay').outerHeight(), 0) / 2;
		    left = Math.max($(window).width() - $('#overlay').outerWidth(), 0) / 2;
		
		    $('#overlay').css({
		        top:top + $(window).scrollTop(), 
		        left:left + $(window).scrollLeft()
		    });
		};
		
		function issueCertificatesWarning() {
		    $('#warningDialog').dialog({
				autoOpen: true,
				width: 400,
				modal: true,
				buttons: {
					"OK": function () {
						$(this).dialog("close");
						centerModal();
						el = document.getElementById("overlay");
						el.style.visibility = (el.style.visibility == "visible") ? "hidden" : "visible";
						document.forms["courseRecord"].submit();
					}, 
					"Cancel": function() { 
						$(this).dialog("close");
						$(location).attr('href','/instructor/search/courseRecordSearch.html'); 
					} 
				}
			});		
		}
		
		function goPayment() {
			document.forms["courseRecord"].submit();
		}
		
		function issueNameWarning() {
		    $('#nameWarningDialog').dialog({
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
		
		function issueEmailWarning() {
		    $('#emailWarningDialog').dialog({
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
		
		function issueDupEmailWarning() {
		    $('#dupWarningDialog').dialog({
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
		
		function issueUserWarning(first, last, email) {
			$('#sabaUserInfo').text(getDuplicateMsg(first, last, email));
		    $('#userWarningDialog').dialog({
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
		
		function issueUsersWarning() {
		    $('#usersWarningDialog').dialog({
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
		
  		$(document).ready(function() {
  		
  			$.ajaxSetup({ cache: false });
  			
  			$('#warningDialog').hide();
  			
  			$('#addDialog').hide();
  		 	$( document ).tooltip();	
  		 		
			
 			$.fn.keyPressed = function(fn) {  
			    return this.each(function() {  
			        $(this).bind('keyPressed', fn);
			        $(this).keyup(function(e){
			              $(this).trigger("keyPressed");
			        })
			    });  
			 }; 
			 
			$('input[name="next"]').click(function() 
			{ 
				if (hasDuplicateEmails() == true) 
				{
					issueDupEmailWarning();
					return;
				} 
				
				validateEmails();
				if (invalid_email_present == true) 
				{
					issueEmailWarning();
					return;
				}
				
				validateUsers();
				if (invalid_user_present == true) 
				{
					issueUsersWarning();
					return;
				} 
				
				validateNames();
				if (invalid_name_present == true) 
				{
					issueNameWarning();
				} 
				else 
				{
					centerModal();
					el = document.getElementById("overlay");
					el.style.visibility = (el.style.visibility == "visible") ? "hidden" : "visible";
					document.forms["courseRecord"].submit();	
				}
 			});
 			
 			
 			$('input[name="back"]').click(function() { 
 				if('${courseRecord.action}' == 'Summary'){
 					$( '#action' ).val('Restart');
 				} else if ('${courseRecord.action}' == 'Payment'){
					$( '#action' ).val('Edit');
				}
				
				document.forms["courseRecord"].submit();
 			});
			
			$.extend( $.fn.dataTable.defaults, {				
				"bJQueryUI": true,
		        "bFilter": false,
		        "bPaginate": false, 
		        "bLengthChange": false,
        		"bSort": false,
				"bAutoWidth": false                          
    		} );
    		

			oTable = $('#searchResults').dataTable({ 
				"oLanguage": {
					"sEmptyTable": "Add student(s) to the course record entry.",
					"sInfo": "Showing _START_ to _END_  of _END_ "
				},
				"bProcessing": true,
        		"bServerSide": true,
        		"bRetrieve": true,
        		"bDestroy": true,
        		"sAjaxSource": "/instructor/student/studentRoster.html?type=json&sheetNumber=<core:out value="${courseRecord.sheetNumber}"/>",	
				"fnServerData": function ( sSource, aoData, fnCallback ) {
					$.ajax( {
					    "url": sSource,
					    "success": function ( data ) {
							loadStudentTable(data);
					    },
					    "dataType": "json"
					});
				}
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
		<section id="primary-content" class="primary-content">
			<header class="primary-header">
			<core:if test="${courseRecord.action=='Summary'}" >
				<h1>Students</h1>
			</core:if>
			<core:if test="${courseRecord.action=='Payment'}" >
				<h1>Review</h1>
			</core:if>
			</header>
			
			<core:if test="${courseRecord.action=='Summary'}" >
				<div class="field-wrap">
				Course < <u>Students</u> < Review < Payment < Confirmation
				</div>
			</core:if>
			<core:if test="${courseRecord.action=='Payment'}" >
				<div class="field-wrap">
				Course < Students < <u>Review</u> < Payment < Confirmation
				</div>
			</core:if>
			
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
								<label for="" class="grey-out no-asterisk">Course:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.courseName}" /></span>
								</div>
							</div>
							<div class="field-wrap">
								<label for="" class="grey-out no-asterisk">Offering End Date:</label>
								<div class="field light-grey-out">
									<span><core:out value="${courseRecord.endDate}" /></span>
								</div>
							</div>
						</fieldset>
					</div>
				</div>
		</section>
		<form:form method="POST" commandName="courseRecord">
		<section class="primary-content">
				<core:if test="${courseRecord.action=='Summary'}" >
				<header class="form-header" title="Enter the students who attended the class.  Certificates will print exactly as you enter the names (case sensitive).  Email is required for students to receive a digital certificate and their ability to print hard-copy certificates.">
				</core:if>
				<core:if test="${courseRecord.action=='Payment'}" >
				<header class="form-header">
				</core:if>
					<h1>List of Students <!-- <a  href="javascript:void(0)" title="Students assigned to course entry."><span class="ui-icon ui-icon-info" style="display:inline-block"></span></a>  --> </h1>
				</header>
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
						<fieldset>	
						<div class="field-wrap">
								<form:errors path="errorMessage" cssClass="msgError" />
						</div>								
						<core:if test="${courseRecord.action=='Summary'}" >
						<% if(isBrowserIE8OrLess(request.getHeader("User-Agent"))) { %>
						<table>
								<tr>
									<td width="5px"><a href="javascript:Add();" id="addStudent" name="create" title="Click 'Add Student' to add rows for additional students."><span class="ui-icon ui-icon-plusthick"></span></a></td><td><a href="javascript:Add();" id="addStudent" name="create">Add Student</a> </td>
								</tr>
						</table>						
						<% } else { %>			
						<table>
							<tfoot>
								<tr>
									<td title="Click 'Add Student' to add rows for additional students."><a href="javascript:Add();" id="addStudent" name="create"><span class="ui-icon ui-icon-plusthick" style="display:inline-block"></span> Add Student</a> </td>
								</tr>
							</tfoot>
						</table>
						<% } %>						
						</core:if>
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="searchResults">
							<thead>
								<tr id="studentTableColumns">
									<th></th>
								</tr>
							</thead>
							<tbody id="studentTableData">
								<tr><td></td></tr>  					
							</tbody>
						</table>
					</fieldset>
				</div>
			</div>
		</section>
		<section class="primary-content">
			<core:if test="${courseRecord.action=='Payment'}" >
				<legend style="font-weight: bold; color: #AD5E5C;" style="width:775px;">
				<p>Select 'Confirm' if you have completed the student information on each student, including a unique email address for each student. Note that valid unique emails are required to receive digital certificate links. Next you will be directed to the payment page to finalize your payment of the training fees based on the number of students submitted here. After completing payment you will receive the digital certificate links for your students. You will be able to print your wallet cards if desired.</p>      

				<p>Select 'Close' if you do not want to complete this course record now.  You may return to your course record to make any needed changes or additions at a later time.</p>
				</legend>
			</core:if>
		
				<footer class="form-action clearfix">
					<core:if test="${courseRecord.action=='Summary'}" >
						<div class="action">
							<div class="bw">
								<input type="button" class="button" name="next" value="Next/Save">	
							</div>
						</div>
					</core:if>
					<core:if test="${courseRecord.action=='Payment'}" >
						<div class="action">
							<div class="bw">
								<core:if test="${courseRecord.certificatesIssued==false}" >
								<!--<input type="button" class="button" name="confirm" onClick="issueCertificatesWarning()" value="Confirm">-->
								<input type="button" class="button" name="confirm" onClick="goPayment()" value="Confirm">
								</core:if>
								<core:if test="${courseRecord.certificatesIssued==true}" >
								<input type="button" class="button" name="payment" onClick="goPayment()" value="Next">
								</core:if>
							</div>
						</div>
					</core:if>
					<div class="action plain-button alt-button">
						<div class="bw">
							<input id="back" type="button" class="button" name="back" value="Back">	
						</div>
					</div>
					<div class="action plain-button alt-button">
						<div class="bw">
							<input id="close" type="button" class="button" name="close" value="Close">	
						</div>
					</div>
				</footer>
		</section>
		<form:hidden  path="action"  /> 
		</form:form>
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
<div id="deleteDialog" class="window" title="Delete Student Confirmation">
	<legend>
		<p id="courseRecordSheet"></p>
	</legend>	
</div>
<div id="overlay"><div><h4 class="grey-out">Processing ...</h4></div></div>
<div id="warningDialog" class="window" title="Issue Student Certificates Confirmation">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>You are about to issue student(s) certificates. If you click "OK" you no longer be able to modify the student(s) details for this course record entry. Select "Cancel" to close and save course record entry. </p>
	</td>
	</tr>
	</table>	
</div>
<div id="nameWarningDialog" class="window" title="Missing Name Input">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>Invalid name entry in form.  Please make sure all names have both a first and last name entered.</p>
	</td>
	</tr>
	</table>	
</div>
<div id="emailWarningDialog" class="window" title="Invalid Email">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>Invalid email in form.  Please correct the email in red to proceed.</p>
	</td>
	</tr>
	</table>	
</div>
<div id="userWarningDialog" class="window" title="Invalid User Information">
	<table align="center" width="85%">
	<tr>
	<td>
		<p><span id="sabaUserInfo"></span></p>
	</td>
	</tr>
	</table>	
</div>
<div id="usersWarningDialog" class="window" title="Invalid User Information">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>Invalid user information in form.  Please correct the items in red to proceed.</p>
	</td>
	</tr>
	</table>	
</div>
<div id="dupWarningDialog" class="window" title="Duplicate Emails">
	<table align="center" width="85%">
	<tr>
	<td>
		<p>Duplicate emails found in form.  Please correct the items in red to proceed.</p>
	</td>
	</tr>
	</table>	
</div>
</body>
</html>