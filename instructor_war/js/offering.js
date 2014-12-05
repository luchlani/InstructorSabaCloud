$.fn.dataTableExt.oApi.fnFindCellRowIndexes = function ( oSettings, sSearch, iColumn )
{
    var
        i,iLen, j, jLen,
        aOut = [], aData;
      
    for ( i=0, iLen=oSettings.aoData.length ; i<iLen ; i++ )
    {
        aData = oSettings.aoData[i]._aData;
          
        if ( typeof iColumn == 'undefined' )
        {
            for ( j=0, jLen=aData.length ; j<jLen ; j++ )
            {
                if ( aData[j] == sSearch )
                {
                    aOut.push( i );
                }
            }
        }
        else if ( aData[iColumn] == sSearch )
        {
            aOut.push( i );
        }
    }
      
    return aOut;
};


function alertDialog(message)
{
	$("#alertText").text(message);
	$("#alertDialog").dialog({
		autoOpen: true,
		width: 330,
		modal: true,
		position: {my: "center", at: "center", of: window},
		buttons: { 
			"OK": function(){
				$(this).dialog("close");
				$("#alertText").text(message);
			} 
		} 
	});
}

function centerModal() 
{
    var top, left;

    top = Math.max($(window).height() - $('#overlay').outerHeight(), 0) / 2;
    left = Math.max($(window).width() - $('#overlay').outerWidth(), 0) / 2;

    $('#overlay').css({
        top:top + $(window).scrollTop(), 
        left:left + $(window).scrollLeft()
    });
    
    var el = document.getElementById("overlay");
	el.style.visibility = (el.style.visibility == "visible") ? "hidden" : "visible";
};
		
function validateDateFormat(date, message)
{
	if(!isDateFormatValid(date))
	{
		alertDialog(message);
		return false;
	}
	return true;
}

function isDateFormatValid(date)
{
	var dateFormat=/^\d{2}\/\d{2}\/\d{4}$/ //Basic check for format validity
	if (!dateFormat.test(date))
	{
		return false;
	}
	var month = date.split("/")[0];
	var day = date.split("/")[1];
	var year = date.split("/")[2];
	var dayobj = new Date(year, month-1, day);
	if ( (dayobj.getMonth()+1!=month) || (dayobj.getDate()!=day) || (dayobj.getFullYear()!=year) )
	{
		return false;
	}
	return true;
}

function isFormerAfterLater(formerDate, laterDate)
{
	var laterDateYearFirst = laterDate.substring(6,10) + laterDate.substring(0,2)  + laterDate.substring(3,5);
	var formerDateYearFirst = formerDate.substring(6,10) + formerDate.substring(0,2)  + formerDate.substring(3,5);
	
    // check offering date is greater
    if ( formerDateYearFirst > laterDateYearFirst)
	{
        return true;
    }
    else
	{
        return false;
    }
}

function getCourseListForILT() 
{
	$.ajax("/instructor/offering/getCourseListForILT.html?type=json").then(setCourseList);
}

function getCourseListForBlended(category) 
{
	$.ajax("/instructor/offering/getCourseListForBlended.html?type=json&category=" + category).then(setCourseList);
}

function getCourseListForOnline()
{
	$.ajax("/instructor/offering/getCourseListForOnline.html?type=json").then(setCourseList);
}

function setCourseList(data)
{
	courseList.length=0;
	for(var i=0; i < data.courseList.length ; i++)
	{
		var course = data.courseList[i];
		courseList.push({label:course.value,value:course.value,key:course.key});
	}
}



function setDeliveryTypeList(courseId)
{
	$.ajax({	
			url: "/instructor/offering/getDeliveryTypeList.html?type=json&courseId=" + courseId,
			success: function(data){
						$("#deliveryType").empty();
						$.each(data.deliveryTypeList, function()
									 {
										$("#deliveryType").append($("<option />").val(this.key).text(this.value));
									 }
								);
					}
			});
}

function setPrice(courseId)
{
	$.ajax({	
			url: "/instructor/offering/getPriceForCourse.html?type=json&courseId=" + courseId,
			success: function(data){
				$("#price").text(data.price);
				if($("#couponCode").val()=='')
				{
					$("#couponNote").text('If this differs from your contract price, apply your organization’s coupon code below.');	
				}
			}
	});
}

function setFacilityList(orgId)
{
	$.ajax({	
			url: "/instructor/offering/getFacilityList.html?type=json&orgId=" + orgId,
			success: function(data){
						$("#facilityId").empty();
						$.each(data.facilityList, function()
									 {
										$("#facilityId").append($("<option />").val(this.key).text(this.value));
									 }
								);
					}
			});
}

function setPurchaseOrderList(orgId)
{
	$.ajax({	
			url: "/instructor/offering/getPurchaseOrderList.html?type=json&orgId=" + orgId,
			success: function(data){
						$("#poId").empty();
						$.each(data.purchaseOrderList, function()
									 {
										$("#poId").append($("<option />").val(this.key).text(this.value));
									 }
								);
					}
			});
}
