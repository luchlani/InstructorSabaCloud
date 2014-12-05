<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<%@ page import="com.arc.instructor.controller.OfferingController" %>
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
  		.dataTables_filter {
		     display: none;
		}
		
		.row_selected td {
  			background-color:#B0BED9;
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
		
		function filterTable(elem)
		{
			console.log("filterTable for " + elem + " value=" + $(elem).val());
			if(elem == "#startDateAfter")
			{
				$('#searchResults').dataTable().fnFilter("", null, null, null);
			}
			else
			{
				$('#searchResults').dataTable().fnFilter($(elem).val(), null, null, null);
			} 
		}
		
		$.fn.dataTableExt.afnFiltering.push(function( oSettings, aData, iDataIndex ) {
	        // "date-range" is the id for my input
			var startDate = $('#startDateAfter').val();
	        if(startDate == null || startDate == '')
	        {
	        	return true;
	        }
	 
	        //here is the column where my dates are.
	        var date = aData[4];
	        if(date == null || date == '')
	        {
	        	return true;
	        	
	        }

			// Convert date from 04/28/2014 to 20140428 format.
	        dateMin = startDate.substring(6,10) + startDate.substring(0,2)  + startDate.substring(3,5);
	        date = date.substring(6,10) + date.substring(0,2)  + date.substring(3,5);
	        	 
	        // check offering date is greater
	        if ( dateMin <= date)
			{
	            return true;
	        }
	        else
			{
	            return false;
	        }
    	});
		
		function setAutoComplete(elem, list)
		{
			$(elem).autocomplete(
					{ 
						source: list, 
						autoFocus: true, 
						scroll: true, 
						select: function(event, ui) 
						{ 
							event.preventDefault(); 
							$(this).val(ui.item.label); 
							filterTable(elem); 
							return false;
						},  
						keyup: function(event, ui) 
						{ 
							event.preventDefault(); 
							$(this).val(ui.item.label); 
							filterTable(elem); 
							return false; 
						}, 
						minLength: 0  
					});
		}
		
		function setFocusHandlers(elem)
		{
			//$(elem).keyup(function(){console.log('Keyup for' + elem + ' value=' + $(elem).val());filterTable(elem);});
			//$(elem).mouseup(function(){console.log('Mouseup for' + elem + ' value=' + $(elem).val());filterTable(elem);});
			//$(elem).focusout(function(){console.log('Foucusout for' + elem + ' value=' + $(elem).val());filterTable(elem);});
			
			$(elem).click(function() 
					{ 
						console.log('Click for' + elem + ' value=' + $(elem).val());
						$("#course").val(""); 
						$("#offeringno").val("");
						$("#deltype").val("");
						$("#startDateAfter").val("");
						filterTable(elem);//Show full table
						if(elem == "#startDateAfter")
						{
							$(this).datepicker();
						}
						else
						{
							$(this).autocomplete("search", "");
						} 
					});
		}
		
		function setFilterAutoComplete(list)
		{
			setAutoComplete("#course", list.courseList);
			setAutoComplete("#offeringno", list.offeringNoList);
			setAutoComplete("#deltype", list.delTypeList);
		}
 		
 		function getOfferingList() 
		{
		    return $.ajax("/instructor/offering/getOfferingList.html?type=json");
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

	  	function editOffering(id, isOnline)
		{
			if(isOnline == 'true')
			{
				document.location.href = "/instructor/offering/onlineOffering.html?action=GetDetail&offeringId=" + id;
			}
			else
			{
				document.location.href = "/instructor/offering/scheduledOffering.html?action=GetDetail&offeringId=" + id;
			}
	  	}
	  	
	  	function viewRoster(id)
		{
			document.location.href = "/instructor/offering/roster.html?offeringId=" + id;
	  	}
	  	
	  	function printCertificates(id){
		    document.location.href = "/instructor/offering/certificates.html?offeringId=" + id;
	  	}
	  	
		$(document).ready(function( $ ) 
		{
			$( document ).tooltip();
			$('#viewDialog').hide();
			
			oTable = $('#searchResults').dataTable({ 
				"bJQueryUI": true,
				"sPaginationType": "full_numbers",
			    "bProcessing": true,
        		"bServerSide": false,
				"aoColumns": [
					{ "mData": "offeringId", "sType": "string", "sTitle": "ID", "sWidth": "10px"},
					{ "mData": "courseName", "sTitle": "Course", "sWidth": "200px"},
					{ "mData": "delType", "sTitle": "Delivery Type", "sWidth": null},
					{ "mData": "organizationName", "sTitle": "Organization", "sWidth": null},
					{ "mData": "startDate", "sTitle": "Start Date", "sWidth": null},
					{ "mData": "status", "sTitle": "Class Status", "sWidth": "10px"},
					{ "mData": "action", "sTitle": "Actions", "sWidth": null, "bSortable": false}
				],
				"sAjaxSource": '/instructor/offering/doOfferingSearch.html?type=json'
			});
			//"oLanguage": {"sEmptyTable": "Create a new Offering."	},
			
			//Set Auto Complete Handlers
			getOfferingList().then(setFilterAutoComplete);
			
			//Start Date search field
			$("#startDateAfter").datepicker();
			$("#startDateAfter").on("change", function(){
				filterTable("#startDateAfter");
				return false;
			});
			
			//Set focus out event handlers to show the full table
			setFocusHandlers("#course");
			setFocusHandlers("#offeringno");
			setFocusHandlers("#deltype");
			setFocusHandlers("#startDateAfter");
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
		            <%= OfferingController.printMenuItems(request, "searchOffering" ) %>
		        </ul>
		        <div class="sidebar-module-wrap"></div>
		    </nav>
		</aside>
		
		<section class="primary-content">
			<header class="primary-header">
				<h1>Search Existing Classes</h1>
			</header>
			<div style="color: #6D6E70;">
				<p>To find a specific class, enter the information below:</p>
			</div>
			
			Filter by:
			<form:form method="POST" action="/instructor/offering/scheduledOffering.html" commandName="offeringSearch">
				<div class="simple-form tabular-form">
					<div class="fieldset-group">
								<div class="standard-table course-table catalog-list-table">
									<fieldset>
									<table class="">
										<tr>
										<tbody>
											<td id="filter_global" class="">
												<div class="field-wrap">
													<label for="course" title="Enter course name to find course and then select." style="width:auto;" class="no-asterisk">Course</label>
													<div class="field">
														<input type="text" name="course" id="course" class="input-large">
													</div>
												</div>
											</td>
											<td id="filter_global" class="">
												<div class="field-wrap">
													<label for="" title="Blended Learning or Instructor-Led" style="width:auto;" class="no-asterisk">Delivery Type</label>
													<div class="field">
														<input type="text" name="deltype" id="deltype" style="width:160px;">
													</div>
												</div>
											</td>
											<td id="filter_global" class="">
												<div class="field-wrap">
													<label for="" style="width:auto;" class="no-asterisk">Offering ID</label>
													<div class="field">
														<input type="text" name="offeringno" id="offeringno" style="width:160px;">
													</div>
												</div>
											</td>
											<td id="filter_global" class="">
												<div class="field-wrap">
													<label for="" style="width:auto;" class="no-asterisk">Class Starts After</label>
													<div class="field">
														<input type="text" name="startDateAfter" id="startDateAfter" style="width:160px;">
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
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="searchResults">
					<thead>
						<tr>
							<th>Column 1</th>
							<th>Column 2</th>
							<th>Column 3</th>
							<th>Column 4</th>
							<th>Column 5</th>
							<th>Column 6</th>
							<th>Column 7</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</form:form>
		</section>
	</div>
</div>
<%@ include file="footer.jsp" %>
</body>
</html>
