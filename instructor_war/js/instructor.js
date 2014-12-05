	  $(function() {
		$('#close').click(function(){
			$(location).attr('href','/instructor/search/courseRecordSearch.html');
		});
		
		$('#print').click(function(){
			//$('#primary-content').printPage(); 
			window.print();
			return false; 
		});
		
	  });
	  
	  
	  function getCookie(name) {
	      if (document.cookie.length>0) {
	        cookie_start=document.cookie.indexOf(name + "=");
	        if (cookie_start != -1) {
	        	cookie_start=cookie_start + name.length+1;
	        	cookie_end=document.cookie.indexOf(";",cookie_start);
	        	if (cookie_end == -1) 
	        		cookie_end=document.cookie.length;
	        	return unescape(document.cookie.substring(cookie_start,cookie_end));
	        }
	      }
	      
	      return "";
	  }
	   
	  function deleteCookie(name) {
	      document.cookie = escape(name) +'=; expires=Thu, 01-Jan-70 00:00:01 GMT;';
	  }

	  function hasCookie(name) {
		  var found = false;
		  
		  var cookieValue = getCookie(name);
		  
		  if(cookieValue){
			  found = true;
		  }
		  
		  return found;
	  }


	  function createCookie(name, value, days) {
		  var expires = "";
		  
	      if (days) {
	          var date = new Date();
	          date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
	          expires = "; expires=" + date.toGMTString();
	      }  
	    	 
	      document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
	  }	  
	  
	  
	  function surveyDialog() {
        $('#surveyDialog').dialog({
            autoOpen: true,
            position: ["20%",],
            modal: true,
            width: 500,
            height: 'auto',
            resizable: false,
            buttons: {
				"OK": function () {
					$(this).dialog("close");
					//createCookie("american_redcross_survey", true, 10);
					//window.location.href = 'https://www.surveymonkey.com/s/TTLGS2S';
					window.open('https://www.surveymonkey.com/s/TTLGS2S', '_blank');
				}, 
            	"No, thanks": function(){
            		//createCookie("american_redcross_survey", true, 5);
                	$(this).dialog('close');
                    
                }
            }
        });
     }
	  
	function displaySurvey() {
		//if(!hasCookie("american_redcross_survey")){
			surveyDialog();
		//} 
	}
	
	function SearchClassParticipants() {
		var URLAddress = window.location.protocol.toString() + "//" + window.location.host.toString() + "/Saba/Web/Main/goto/SearchClassParticipants";
		window.open(URLAddress);
	}
	  


	  
	
	  
