$(document).ready(function () {
    $.getScript("http://s11.cnzz.com/z_stat.php?id=1254580861&web_id=1254580861");
    $(document).bind("keydown", function (e) {

        if (e.which == 27) {
            if (parent && parent.$.closeIfrm) {
                parent.$.closeIfrm(null, false);
            }
            if (parent && parent.parent && parent.parent.$.closeIfrm) {
                parent.parent.$.closeIfrm(null, false);
            }

            var $pop = $(".recommend-pop");
            if ($pop.length > 0 && $pop.is(":visible")) {
                $(".recommend-close").trigger("click");
            }
        }
    });
    $("input[regex='num']").bind("blur", function () {
        var value = parseInt($(this).val());
        if (isNaN(value)) {
            $(this).val("0");
        } else if (value < 0) {
            $(this).val(0 - value);
        }
    });
    $("input[type=button]").bind("mouseenter", function () {
        $(this).css({ "background-color": "#FFFFFF" });
    }).bind("mouseleave", function () {
        $(this).css({ "background-color": "#F7F7F7" });
    });
    $(".sheet-back").bind("click", function () {
        var search = window.location.search;
        var href = "";
        var questionMarkIndex = document.referrer.indexOf('?');
        if (questionMarkIndex > -1) {
            href = document.referrer.substring(0, questionMarkIndex);
        } else {
            href = document.referrer;
        }
        window.location.href = href + search;
    });
    $(".recommend-pop-btn").bind("mouseenter", function () {
        $(this).addClass("recommend-pop-btn-focus");
    }).bind("mouseleave", function () {
        $(this).removeClass("recommend-pop-btn-focus");
    });
    $(".recommend-pop-cancel").bind("mouseenter", function () {
        $(this).addClass("recommend-pop-cancel-focus");
    }).bind("mouseleave", function () {
        $(this).removeClass("recommend-pop-cancel-focus");
    });
    $("#fax_msg_box").bind("mouseenter",function(){
        var length = $(this).children(".fax_msg").length;
        if(length==0){
            return;
        }
        $(this).stop(true).animate({"height":22*length+2+"px"});
    }).bind("mouseleave",function(){
        $(this).stop(true).animate({"height":"22px"});
    });
});


/*--获取网页传递的参数--*/
function request(paras) {
    var url = location.href;
    var paraString = url.substring(url.indexOf("?") + 1, url.length).split("&");
    var paraObj = {}
    for (i = 0; j = paraString[i]; i++) {
        paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
    }
    var returnValue = paraObj[paras.toLowerCase()];
    if (typeof (returnValue) == "undefined") {
        return "";
    } else {
        return returnValue;
    }
}
/*********************动态载入JS End************************/
var popUpWin;
function PopUpCenterWindow(URLStr, width, height, newWin, scrollbars) {
    var popUpWin = 0;
    if (typeof (newWin) == "undefined") {
        newWin = false;
    }
    if (typeof (scrollbars) == "undefined") {
        scrollbars = 0;
    }
    if (typeof (width) == "undefined") {
        width = 800;
    }
    if (typeof (height) == "undefined") {
        height = 600;
    }
    var left = 0;
    var top = 0;
    if (screen.width >= width) {
        left = Math.floor((screen.width - width) / 2);
    }
    if (screen.height >= height) {
        top = Math.floor((screen.height - height) / 2);
    }
    if (newWin) {
        open(URLStr, '', 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=' + scrollbars + ',resizable=yes,copyhistory=yes,width=' + width + ',height=' + height + ',left=' + left + ', top=' + top + ',screenX=' + left + ',screenY=' + top + '');
        return;
    }

    if (popUpWin) {
        if (!popUpWin.closed) popUpWin.close();
    }
    popUpWin = open(URLStr, 'popUpWin', 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=' + scrollbars + ',resizable=yes,copyhistory=yes,width=' + width + ',height=' + height + ',left=' + left + ', top=' + top + ',screenX=' + left + ',screenY=' + top + '');
    popUpWin.focus();
}
function OpenModalDialog(url, option) {
    option.type = 2;
    if ($.ShowIfrmDailog) {
        $.ShowIfrmDailog(url, option);
    }
}
function CloseModalDialog(callback, dooptioncallback, userstate) {
    if (parent && parent.$.closeIfrm) {
        parent.$.closeIfrm(callback, dooptioncallback, userstate);
    }
}
function OpenModelWindow(url, option) {
    var fun;
    try {
        if ($.ShowIfrmDailog != undefined) {
            fun = $.ShowIfrmDailog;
        }
        else if (parent != null && parent.$ != null && parent.$.ShowIfrmDailog != undefined) {
            fun = parent.$.ShowIfrmDailog
        }
        else  if (parent.parent != null && parent.parent.$ != null && parent.parent.$.ShowIfrmDailog != undefined) {
            fun = parent.parent.$.ShowIfrmDailog
        }
        
    }
    catch (e) {
        fun = $.ShowIfrmDailog;
    }
    fun(url, option);
}
function CloseModelWindow(callback, dooptioncallback, userstate) {
    if (parent) {
        parent.$.closeIfrm(callback, dooptioncallback, userstate);
    }
    else {
        window.close();
    }
}


function StrFormat(temp, dataarry) {
    return temp.replace(/\{([\d]+)\}/g, function (s1, s2) { var s = dataarry[s2]; if (typeof (s) != "undefined") { if (s instanceof (Date)) { return s.getTimezoneOffset() } else { return encodeURIComponent(s) } } else { return "" } });
}
function StrFormatNoEncode(temp, dataarry) {
    return temp.replace(/\{([\d]+)\}/g, function (s1, s2) { var s = dataarry[s2]; if (typeof (s) != "undefined") { if (s instanceof (Date)) { return s.getTimezoneOffset() } else { return (s); } } else { return ""; } });
}
function ajaxError(jqXHR, textStatus, errorThrown) {
    if (jqXHR.status == 200) {
        window.top.window.location.href = "/default";
    } else {
        alert(jqXHR.responseText);
    }
}
function showLoadingMsg(msg, position, isAutoHide, timeout) {
    if (isAutoHide == undefined) {
        isAutoHide = true;
    }
    if (timeout == undefined) {
        timeout = 3000;
    }

    var loadpanel = $("#loadpanel");
    if (loadpanel.length == 0) {
        loadpanel = $("<div id=\"loadpanel\" class=\"loadingpanel\"/>").appendTo("body");
    }
    loadpanel.html("<span>" + msg + "</span>");
    if (!position) {
        var topIsDefault = window.location.href.indexOf('/home/default') > -1;
        var _left = 0;
        if (!topIsDefault) {
            _left = ($(window).width() - (msg.length * 12 + 35 + 30)) / 2;
        } else {
            _left = (($(window).width() - 170) - (msg.length * 12 + 35 + 30)) / 2 + 170;
        }
        position = { left: _left, top: !topIsDefault ? 0 : 48 };
    }
    loadpanel.css(position).show();
    if (isAutoHide) {
        showLoadTipTimerId = setTimeout(hideLoadingMsg, timeout);
    }
}
function hideLoadingMsg() {
    var loadpanel = $("#loadpanel");
    if (loadpanel.length > 0) {
        loadpanel.hide();
    }
}
var showErrorTipTimerId;
var showLoadTipTimerId;
var msg;
function showErrorTip(msg, position, isAutoHide, timeout) {
    if (isAutoHide == undefined) {
        isAutoHide = true;
    }
    if (timeout == undefined) {
        timeout = 3000;
    }
    var errorpanel = $("#errorpanel");
    if (errorpanel.length == 0) {
        errorpanel = $("<div id=\"errorpanel\" style='z-index:9999' class=\"errorpanel\"/>").appendTo("body");
    }
    if (errorpanel.css("display") != "none") {
        errorpanel.find(">dt").append("<dl>" + msg + "</dl>");
        if (showErrorTipTimerId) {
            window.clearTimeout(showErrorTipTimerId);
        }
    } else {
        errorpanel.html("<dt ><dl>" + msg + "</dl></dt>");
        if (!position) {
            var topIsDefault = window.location.href.indexOf('/home/default') > -1;
            var _left = 0;
            if (!topIsDefault) {
                _left = ($(window).width() - (msg.length * 12 + 35 + 30)) / 2;
            } else {
                _left = (($(window).width() - 170) - (msg.length * 12 + 35 + 30)) / 2 + 170;
            }

            position = { left: _left, top: !topIsDefault ? 0 : 48 };
        }
        errorpanel.css(position).fadeIn();
    }
    if (isAutoHide) {
        showErrorTipTimerId = setTimeout(hideErrortip, timeout);
    }

}
function hideErrortip() {
    var errorpanel = $("#errorpanel");
    if (errorpanel.length > 0) {
        errorpanel.hide();
    }
}
function removeParent() {
    $(this).parent().hide();
    return false;
}
function showValidateError(error, element) {
    //var close = $("<a href=\"javascript:void(0)\" class=\"valiclose\">&nbsp;</a>").click(removeParent);  
    var pos = element.position();
    var height = element.height();
    if (pos.left + 155 >= document.documentElement.clientWidth) {
        pos.left = document.documentElement.clientWidth - 156;
    }
    var newpos = { left: pos.left, top: pos.top + height + 2 }
    error.appendTo("#fmEdit").css(newpos);
}
//复制对象
function Clone(obj) {
    var objClone = new Object();
    if (obj.constructor == Object) {
        objClone = new obj.constructor();
    } else {
        objClone = new obj.constructor(obj.valueOf());
    }
    for (var key in obj) {
        if (objClone[key] != obj[key]) {
            if (typeof (obj[key]) == 'object') {
                objClone[key] = Clone(obj[key]);
            } else {
                objClone[key] = obj[key];
            }
        }
    }
    objClone.toString = obj.toString;
    objClone.valueOf = obj.valueOf;
    return objClone;
}
String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "");
}
String.prototype.ltrim = function () {
    return this.replace(/(^\s*)/g, "");
}
String.prototype.rtrim = function () {
    return this.replace(/(\s*$)/g, "");
}
//格式化日期返回String
Date.prototype.Format = function (format) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "H+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "w": "0123456".indexOf(this.getDay()),
        "W": ["日", "一", "二", "三", "四", "五", "六"][this.getDay()],
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format))
            format = format.replace(RegExp.$1,
  RegExp.$1.length == 1 ? o[k] :
    ("00" + o[k]).substr(("" + o[k]).length));
    }
    return format;
};

//仿照VBScript的DateAdd函数
function DateAdd(interval, number, idate) {
    number = parseInt(number);
    var date;
    if (typeof (idate) == "string") {
        date = idate.split(/\D/);
        eval("var date = new Date(" + date.join(",") + ")");
    }

    if (typeof (idate) == "object") {
        date = new Date(idate.toString());
    }
    switch (interval) {
        case "y": date.setFullYear(date.getFullYear() + number); break;
        case "m": date.setMonth(date.getMonth() + number); break;
        case "d": date.setDate(date.getDate() + number); break;
        case "w": date.setDate(date.getDate() + 7 * number); break;
        case "h": date.setHours(date.getHours() + number); break;
        case "n": date.setMinutes(date.getMinutes() + number); break;
        case "s": date.setSeconds(date.getSeconds() + number); break;
        case "l": date.setMilliseconds(date.getMilliseconds() + number); break;
    }
    return date;
}
//获取两个日程的时间间隔,仿照VbScript的Datediff函数
function DateDiff(interval, d1, d2) {
    switch (interval) {
        case "d": //天
        case "w":
            d1 = new Date(d1.getFullYear(), d1.getMonth(), d1.getDate());
            d2 = new Date(d2.getFullYear(), d2.getMonth(), d2.getDate());
            break;  //w
        case "h":
            d1 = new Date(d1.getFullYear(), d1.getMonth(), d1.getDate(), d1.getHours());
            d2 = new Date(d2.getFullYear(), d2.getMonth(), d2.getDate(), d2.getHours());
            break; //h
        case "n":
            d1 = new Date(d1.getFullYear(), d1.getMonth(), d1.getDate(), d1.getHours(), d1.getMinutes());
            d2 = new Date(d2.getFullYear(), d2.getMonth(), d2.getDate(), d2.getHours(), d2.getMinutes());
            break;
        case "s":
            d1 = new Date(d1.getFullYear(), d1.getMonth(), d1.getDate(), d1.getHours(), d1.getMinutes(), d1.getSeconds());
            d2 = new Date(d2.getFullYear(), d2.getMonth(), d2.getDate(), d2.getHours(), d2.getMinutes(), d2.getSeconds());
            break;
    }
    var t1 = d1.getTime(), t2 = d2.getTime();
    var diff = NaN;
    switch (interval) {
        case "y": diff = d2.getFullYear() - d1.getFullYear(); break; //y
        case "m": diff = (d2.getFullYear() - d1.getFullYear()) * 12 + d2.getMonth() - d1.getMonth(); break;    //m
        case "d": diff = Math.floor(t2 / 86400000) - Math.floor(t1 / 86400000); break;
        case "w": diff = Math.floor((t2 + 345600000) / (604800000)) - Math.floor((t1 + 345600000) / (604800000)); break; //w
        case "h": diff = Math.floor(t2 / 3600000) - Math.floor(t1 / 3600000); break; //h
        case "n": diff = Math.floor(t2 / 60000) - Math.floor(t1 / 60000); break; //
        case "s": diff = Math.floor(t2 / 1000) - Math.floor(t1 / 1000); break; //s
        case "l": diff = t2 - t1; break;
    }
    return diff;

}
/*禁用backspace键的后退功能，但是可以删除文本内容*/
//document.onkeydown = check;
//function check(e) {
//    var code;
//    if (!e) var e = window.event;
//    if (e.keyCode) code = e.keyCode;
//    else if (e.which) code = e.which;
//    if (((event.keyCode == 8) &&   //BackSpace 
//         ((event.srcElement.type != "text" &&
//         event.srcElement.type != "textarea" &&
//         event.srcElement.type != "password") ||
//         event.srcElement.readOnly == true)) ||
//        ((event.ctrlKey) && ((event.keyCode == 78) || (event.keyCode == 82))) ||    //CtrlN,CtrlR 
//        (event.keyCode == 116)) {      //F5 
//        event.keyCode = 0;
//        event.returnValue = false;
//    }
//    return true;
//}
function StrFormat(temp, dataarry) {
    return temp.replace(/\{([\d])\}/g, function (s1, s2) { var s = dataarry[s2]; if (typeof (s) != "undefined") { if (s instanceof (Date)) { return encodeURIComponent(s.Format("yyyy-MM-dd HH:mm:ss S")); } else { return encodeURIComponent(s); } } else { return ""; } });
}
function StrFormatNoEncode(temp, dataarry) {
    return temp.replace(/\{([\d])\}/g, function (s1, s2) { var s = dataarry[s2]; if (typeof (s) != "undefined") { if (s instanceof (Date)) { return (s.Format("yyyy-MM-dd HH:mm:ss S")); } else { return (s); } } else { return ""; } });
}


(function ($) {
    if ($.fn.noSelect == undefined) {
        $.fn.noSelect = function (p) { //no select plugin by me :-)
            if (p == null)
                prevent = true;
            else
                prevent = p;

            if (prevent) {

                return this.each(function () {
                    if ($.browser.msie || $.browser.safari) $(this).bind('selectstart', function () { return false; });
                    else if ($.browser.mozilla) {
                        $(this).css('MozUserSelect', 'none');
                        $('body').trigger('focus');
                    }
                    else if ($.browser.opera) $(this).bind('mousedown', function () { return false; });
                    else $(this).attr('unselectable', 'on');
                });

            } else {

                return this.each(function () {
                    if ($.browser.msie || $.browser.safari) $(this).unbind('selectstart');
                    else if ($.browser.mozilla) $(this).css('MozUserSelect', 'inherit');
                    else if ($.browser.opera) $(this).unbind('mousedown');
                    else $(this).removeAttr('unselectable', 'on');
                });

            }

        }; //end noSelect
    }
})(jQuery)

function showMask(){
    var mask = $(".mask");
    
    if(mask.length==0){
        mask = $("<div/>");
        mask.attr("class","mask");
        $(document.body).append(mask);
    }

    var bodyHeight = $(document.body).height();
    var winHeight = $(window).height();

    mask.css({ "width": $(document.body).width(), "height": bodyHeight > winHeight ? bodyHeight : winHeight });
    mask.show();
}
function hideMask(){
    var $mask = $(".mask");
    
    if($mask.length>0){
        $mask.hide();
    }
}
function showLoading() {
    showMask();

    var maskCircle = $(".mask-circle-animation");
    if (maskCircle.length == 0) {
        maskCircle = $("<div/>");
        maskCircle.attr("class", "mask-circle-animation");
        $(document.body).append(maskCircle);
    }

    var bodyHeight = $(document.body).height();
    var winHeight = $(window).height();

    var left = ($(window).width() - maskCircle.width()) / 2;
    var top = ($(window).height() - maskCircle.height()) / 2;

    maskCircle.css({ "left": left, "top": top + $(document).scrollTop() });
    maskCircle.show();
}
function hideLoading() {
    $(".mask-circle-animation").hide();
    $(".mask").hide();
}
function renderDate(calendar, year, month) {
    var rightNow = new Date();
    var rightNowYear = rightNow.getFullYear();
    var rightNowMonth = rightNow.getMonth();
    var rightNowDate = rightNow.getDate();

    var title = calendar.children(".calendar-title");
    var leftCal = calendar.children(".cal-tab").eq(0);
    var rightCal = calendar.children(".cal-tab").eq(1);
    var leftCalTbody = leftCal.children("tbody");
    var rightCalTbody = rightCal.children("tbody");

    var selectedYear = calendar.attr("Year");
    var selectedMonth = calendar.attr("Month");
    var selectedDate = calendar.attr("Date");

    title.children(".calendar-prev-text").text(year + "年" + (month + 1) + "月");
    title.attr("Year", year);
    title.attr("Month", month);

    var firstDayOfMonth = new Date(Date.parse(year + "/" + (month + 1) + "/" + 1));
    var endDay = new Date(firstDayOfMonth.getFullYear(), firstDayOfMonth.getMonth(), firstDayOfMonth.getDate());
    endDay.setMonth(endDay.getMonth() + 1);

    var dtList = leftCalTbody.find("td");
    var dtIndex = firstDayOfMonth.getDay();

    dtList.text("").css({ "background-color": "#FFFFFF", "color": "#666666" }).removeAttr("selected", "selected");
    while (firstDayOfMonth < endDay) {
        var y = firstDayOfMonth.getFullYear();
        var m = firstDayOfMonth.getMonth();
        var d = firstDayOfMonth.getDate();

        var $td = dtList.eq(dtIndex);
        $td.attr("Year", y);
        $td.attr("Month", m);
        $td.attr("Date", d);

        if (rightNowYear == y && rightNowMonth == m && rightNowDate == d) {
            $td.text("今天");
            $td.css("color", "#FF6600");
        } else {
            $td.text(d);
        }

        if (selectedYear == y && selectedMonth == m && selectedDate == d) {
            $td.css("background-color", "#EEEEEE");
            $td.attr("selected", "selected");
        }   

        firstDayOfMonth.setDate(d + 1);
        dtIndex++;
    }

    month += 2;
    if (month == 13) {
        year += 1;
        month = 1;
    }
    title.children(".calendar-next-text").text(year + "年" + month + "月");
    firstDayOfMonth = new Date(Date.parse(year + "/" + month + "/" + 1));
    endDay = new Date(firstDayOfMonth.getFullYear(), firstDayOfMonth.getMonth(), firstDayOfMonth.getDate());
    endDay.setMonth(endDay.getMonth() + 1);

    dtList = rightCalTbody.find("td");
    dtIndex = firstDayOfMonth.getDay();

    dtList.text("").css({ "background-color": "#FFFFFF", "color": "#666666" }).removeAttr("selected", "selected");
    while (firstDayOfMonth < endDay) {
        var y = firstDayOfMonth.getFullYear();
        var m = firstDayOfMonth.getMonth();
        var d = firstDayOfMonth.getDate();

        var $td = dtList.eq(dtIndex);
        $td.attr("Year", y);
        $td.attr("Month", m);
        $td.attr("Date", d);
        if (rightNowYear == y && rightNowMonth == m && rightNowDate == d) {
            $td.text("今天");
            $td.css("color", "#FF6600");
        } else {
            $td.text(d);
        }

        firstDayOfMonth.setDate(d + 1);
        dtIndex++;
    }
}
var isTimePickerInit = false;
function timePickerInit() {
    isTimePickerInit = true;

    var calendar = $(".calendar-pop");
    if (calendar.length == 0) {
        calendar = $("<div/>");
        calendar.attr("class", "calendar-pop");

        var title = $("<div/>");
        title.attr("class", "calendar-title");
        title.append("<div class='calendar-prev'></div>");
        title.append("<div class='calendar-prev-text'></div>");
        title.append("<div class='calendar-next'></div>");
        title.append("<div class='calendar-next-text'></div>");

        calendar.append(title);

        var leftCal = $("<table/>");
        leftCal.attr("class", "cal-tab");
        leftCal.attr("cellpadding", "0");
        leftCal.attr("cellspacing", "0");
        leftCal.attr("border", "0");
        leftCal.append("<thead><tr class='weekRow'><th style='color:#FF6600;'>日</th><th>一</th><th>二</th><th>三</th><th>四</th><th>五</th><th style='color:#FF6600;'>六</th></tr></thead>");
        leftCal.append("<tbody><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr></tbody>");

        calendar.append(leftCal);

        var rightCal = $("<table/>");
        rightCal.attr("class", "cal-tab");
        rightCal.attr("cellpadding", "0");
        rightCal.attr("cellspacing", "0");
        rightCal.attr("border", "0");
        rightCal.css("margin-left", "10px");
        rightCal.css("margin-right", "10px");
        rightCal.append("<thead><tr class='weekRow'><th style='color:#FF6600;'>日</th><th>一</th><th>二</th><th>三</th><th>四</th><th>五</th><th style='color:#FF6600;'>六</th></tr></thead>");
        rightCal.append("<tbody><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr></tbody>");

        calendar.append(rightCal);

        $("body").append(calendar);
    }

    var title = calendar.children(".calendar-title");
    title.children(".calendar-next").bind("click", function (event) {
        var year = parseInt(title.attr("Year"));
        var month = parseInt(title.attr("Month"));

        month += 1;
        if (month == 12) {
            year += 1;
            month = 0;
        }
        month += 1;
        if (month == 12) {
            year += 1;
            month = 0;
        }
        renderDate(calendar, year, month);
        event.stopPropagation();
    });
    title.children(".calendar-prev").bind("click", function (event) {
        var year = parseInt(title.attr("Year"));
        var month = parseInt(title.attr("Month"));
        month -= 1;
        if (month == -1) {
            year -= 1;
            month = 11;
        }
        month -= 1;
        if (month == -1) {
            year -= 1;
            month = 11;
        }
        renderDate(calendar, year, month);
        event.stopPropagation();
    });
    calendar.bind("click", function (event) {
        event.stopPropagation();
    });
    calendar.find("td").unbind("mouseenter").bind("mouseenter", function () {
        if ($(this).text() != "") {
            $(this).css({ "background-color": "#EEEEEE", "cursor": "pointer" });
        }
    }).unbind("mouseleave").bind("mouseleave", function () {
        if ($(this).text() != "" && $(this).attr("selected") != "selected") {
            $(this).css({ "background-color": "#FFFFFF" });
        }
    });
    $(document).bind("click", function () {
        var calendar = $(".calendar-pop");

        if (calendar.length > 0 && calendar.is(":visible")) {
            var $StartTime = $("#StartTime");
            var $EndTime = $("#EndTime");

            if ($StartTime.length > 0 && $EndTime.length > 0) {
                var startdate = new Date(Date.parse($("#StartTime").val().replace(/-/g, "/")));
                var enddate = new Date(Date.parse($("#EndTime").val().replace(/-/g, "/")));
                if (startdate > enddate) {
                    window.top.showErrorTip("结束时间小于开始时间，调整后再查询！");
                } else {
                    var calendar = $(".calendar-pop");
                    if (calendar.length > 0 && calendar.is(":visible")) {
                        calendar.hide();
                    }
                }
            } else {
                calendar.hide();
            }
        }
    });
}
jQuery.fn.timePicker = function (opt) {
    if ($(this).is(":input")) {
        $(this).addClass("calendar-input");
    }
    $(this).bind("click", function (event) {
        $(document).trigger("click");

        var txt = "";
        var $input = $(this);
        if ($input.attr("disabledxf") == "disabled") {
            return;
        }
        if ($(this).is(":input")) {
            txt = $(this).val();
        } else {
            txt = $(this).text();
        }
        var calendar = $(".calendar-pop");

        var top = 0;
        if (!$(this).is(":input") && $(this).height() > 25) {
            top = $(this).offset().top + ($(this).height()/2) + 14;
        } else {
            top = $(this).offset().top + $(this).height() + 4;
        }
        var left = $(this).offset().left;

        var winWidth = $(window).width();
        var overflow = left + 470 - winWidth;
        if (overflow > 0) {
            left -= (overflow + 2);
        }
        calendar.css({ "top": top, "left": left });
        calendar.show();

        var thisDate = null;
        if (txt == "") {
            thisDate = new Date();
        } else {
            thisDate = new Date(Date.parse(txt.replace(/-/g, "/")));
        }

        var year = thisDate.getFullYear();
        var month = thisDate.getMonth();
        var date = thisDate.getDate();

        calendar.attr("Year", year);
        calendar.attr("Month", month);
        calendar.attr("Date", date);

        renderDate(calendar, year, month);
        calendar.find("td").unbind("click").bind("click", function () {
            calendar.find("td:[selected='selected']").removeAttr("selected").css({ "background-color": "#FFFFFF" });

            if ($(this).text() != "") {
                var yyMMdd = "";
                var y = $(this).attr("Year");
                var m = parseInt($(this).attr("Month"));
                var d = $(this).attr("Date");

                yyMMdd = y + "-";
                if (m < 9) {
                    yyMMdd = yyMMdd + "0" + (m + 1) + "-";
                } else {
                    yyMMdd = yyMMdd + (m + 1) + "-";
                }

                if (d < 10) {
                    yyMMdd = yyMMdd + "0" + d;
                } else {
                    yyMMdd = yyMMdd + d;
                }
                if ($input.is(":input")) {
                    $input.val(yyMMdd);
                } else {
                    $input.text(yyMMdd);
                }

                calendar.hide();

                var $StartTime = $("#StartTime");
                var $EndTime = $("#EndTime");
                if ($StartTime.length > 0 && $EndTime.length > 0) {
                    var startdate = new Date(Date.parse($StartTime.val().replace(/-/g, "/")));
                    var enddate = new Date(Date.parse($EndTime.val().replace(/-/g, "/")));
                    if (startdate > enddate) {
                        var id = $input.attr("id");
                        if (id == "StartTime") {
                            $("#EndTime").trigger("click");
                        } else {
                            $("#StartTime").trigger("click");
                        }
                        window.top.showErrorTip("结束时间小于开始时间，调整后再查询！");
                    } else if (opt && opt.onReturn) {
                        opt.onReturn.call(this,$input);
                    }
                } else if (opt && opt.onReturn) {
                    opt.onReturn.call(this, $input);
                }
            }
        })
        event.stopPropagation();
    });

    if (!isTimePickerInit) {
        timePickerInit();
    }
}
jQuery.fn.tourPopOver = function ($html, bodyTop, arrowTop, bodyRight) {
    $(".xf-popover-right", window.top.document).remove();

    return this.each(function () {
        if (window.top == window) {
            return;
        }
        $(this).css("position", "relative");

        $(this).bind("mouseenter", function () {
            var xfpopover = $(".xf-popover-right", window.top.document);

            if (xfpopover.length == 0) {
                xfpopover = $("<div/>");
                xfpopover.attr("class", "xf-popover-right");
                if (bodyTop != undefined) {
                    xfpopover.css("top", bodyTop+5);
                }
                if (bodyRight != undefined) {
                    xfpopover.css("right", bodyRight);
                }

                var xfarrow = $("<div/>");
                xfarrow.attr("class", "xf-arrow-right");
                xfarrow.append("<em></em><span></span>");
                if (arrowTop != undefined) {
                    xfarrow.css("top", arrowTop);
                }

                var xfpopoverbody = $("<div/>");
                xfpopoverbody.attr("class", "xf-popover-right-body");
                xfpopoverbody.append($html);
                xfpopoverbody.append("<div style='clear:both;'/>");

                xfpopover.append(xfarrow);
                xfpopover.append(xfpopoverbody);
                xfpopover.append("<div style='clear:both;'/>");
                xfpopover.bind("mouseenter", function () {
                    $(this).attr("body-selected", "selected");
                }).bind("mouseleave", function () {
                    $(this).removeAttr("body-selected");
                    setTimeout(function () {
                        var xfpopover = $(".xf-popover-right", window.top.document);
                        if (xfpopover.length != 0 &&
                            xfpopover.attr("question-selected") != "selected" &&
                            xfpopover.attr("body-selected") != "selected") {
                            xfpopover.remove();
                        }
                    }, 200);
                });
            }
            xfpopover.attr("question-selected", "selected");
            $("body", window.top.document).append(xfpopover);

            $(".xf-popover-right", window.top.document).show();
        }).bind("mouseleave", function () {
            var xfpopover = $(".xf-popover-right", window.top.document);
            if (xfpopover.length > 0) {
                xfpopover.removeAttr("question-selected");
            }
            setTimeout(function () {
                var xfpopover = $(".xf-popover-right", window.top.document);
                if (xfpopover.length != 0 &&
                    xfpopover.attr("question-selected") != "selected" &&
                    xfpopover.attr("body-selected") != "selected") {
                    xfpopover.remove();
                }
            }, 200);
        });
    });
}
jQuery.fn.tourPopOver2 = function ($html, bodyTop, arrowTop, bodyRight) {
    $(".xf-popover-right", $(this).parent()).remove();
    return this.each(function () {
        if (window.top == window) {
            return;
        }
        $(this).css("position", "relative");
        $(this).parent().css("position", "relative");

        $(this).bind("mouseenter", function () {
            var xfpopover = $(".xf-popover-right", $(this).parent());

            if (xfpopover.length == 0) {
                xfpopover = $("<div/>");
                xfpopover.attr("class", "xf-popover-right");
                if (bodyTop != undefined) {
                    xfpopover.css("top", bodyTop);
                }
                if (bodyRight != undefined) {
                    xfpopover.css("right", bodyRight);
                }

                var xfarrow = $("<div/>");
                xfarrow.attr("class", "xf-arrow-right");
                xfarrow.append("<em></em><span></span>");
                if (arrowTop != undefined) {
                    xfarrow.css("top", arrowTop);
                }

                var xfpopoverbody = $("<div/>");
                xfpopoverbody.attr("class", "xf-popover-right-body");
                xfpopoverbody.append($html);
                xfpopoverbody.append("<div style='clear:both;'/>");

                xfpopover.append(xfarrow);
                xfpopover.append(xfpopoverbody);
                xfpopover.append("<div style='clear:both;'/>");
                xfpopover.bind("mouseenter", function () {
                    $(this).attr("body-selected", "selected");
                }).bind("mouseleave", function () {
                    $(this).removeAttr("body-selected");
                    setTimeout(function () {
                        if (xfpopover.length != 0 &&
                            xfpopover.attr("question-selected") != "selected" &&
                            xfpopover.attr("body-selected") != "selected") {
                            xfpopover.remove();
                        }
                    }, 200);
                });
            }
            xfpopover.attr("question-selected", "selected");
            //$(this).append(xfpopover);
            $(this).parent().append(xfpopover);

            $(".xf-popover-right", $(this).parent()).show();
        }).bind("mouseleave", function () {
            var xfpopover = $(".xf-popover-right", $(this).parent());
            if (xfpopover.length > 0) {
                xfpopover.removeAttr("question-selected");
            }
            setTimeout(function () {
                if (xfpopover.length != 0 &&
                    xfpopover.attr("question-selected") != "selected" &&
                    xfpopover.attr("body-selected") != "selected") {
                    xfpopover.remove();
                }
            }, 200);
        });
    });
}
function getJoinValue($chkList) {
    var ret = "";
    $chkList.each(function () {
        var val = $(this).val();
        if (ret == "") {
            ret = val;
        } else {
            ret += ","+val;
        }
    });

    return ret;
}
jQuery.fn.getNumberValue = function () {
    if ($(this).text() == "") {
        return 0;
    } else {
        return parseFloat($(this).text());
    }
}
jQuery.fn.getAttr2Number = function (param) {
    if ($(this).attr(param) == "") {
        return 0;
    } else {
        return parseFloat($(this).attr(param));
    }
}
function changeURLPar(destiny, par, par_value) {
    var pattern = par + '=([^&]*)';
    var replaceText = par + '=' + par_value;
    if (destiny.match(pattern)) {
        var tmp = '/\\' + par + '=[^&]*/';
        tmp = destiny.replace(eval(tmp), replaceText);
        return (tmp);
    }
    else {
        if (destiny.match('[\?]')) {
            return destiny + '&' + replaceText;
        }
        else {
            return destiny + '?' + replaceText;
        }
    }
    return destiny + '\n' + par + '\n' + par_value;
}

if (!this.JSON) {
    this.JSON = {};
}

(function () {

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf()) ?
                   this.getUTCFullYear() + '-' +
                 f(this.getUTCMonth() + 1) + '-' +
                 f(this.getUTCDate()) + 'T' +
                 f(this.getUTCHours()) + ':' +
                 f(this.getUTCMinutes()) + ':' +
                 f(this.getUTCSeconds()) + 'Z' : null;
        };

        String.prototype.toJSON =
        Number.prototype.toJSON =
        Boolean.prototype.toJSON = function (key) {
            return this.valueOf();
        };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"': '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

        // If the string contains no control characters, no quote characters, and no
        // backslash characters, then we can safely slap some quotes around it.
        // Otherwise we must also replace the offending characters with safe escape
        // sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ?
            '"' + string.replace(escapable, function (a) {
                var c = meta[a];
                return typeof c === 'string' ? c :
                    '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
            }) + '"' :
            '"' + string + '"';
    }


    function str(key, holder) {

        // Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

        // If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

        // If we were called with a replacer function, then call the replacer to
        // obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

        // What happens next depends on the value's type.

        switch (typeof value) {
            case 'string':
                return quote(value);

            case 'number':

                // JSON numbers must be finite. Encode non-finite numbers as null.

                return isFinite(value) ? String(value) : 'null';

            case 'boolean':
            case 'null':

                // If the value is a boolean or null, convert it to a string. Note:
                // typeof null does not produce 'null'. The case is included here in
                // the remote chance that this gets fixed someday.

                return String(value);

                // If the type is 'object', we might be dealing with an object or an array or
                // null.

            case 'object':

                // Due to a specification blunder in ECMAScript, typeof null is 'object',
                // so watch out for that case.

                if (!value) {
                    return 'null';
                }

                // Make an array to hold the partial results of stringifying this object value.

                gap += indent;
                partial = [];

                // Is the value an array?

                if (Object.prototype.toString.apply(value) === '[object Array]') {

                    // The value is an array. Stringify every element. Use null as a placeholder
                    // for non-JSON values.

                    length = value.length;
                    for (i = 0; i < length; i += 1) {
                        partial[i] = str(i, value) || 'null';
                    }

                    // Join all of the elements together, separated with commas, and wrap them in
                    // brackets.

                    v = partial.length === 0 ? '[]' :
                    gap ? '[\n' + gap +
                            partial.join(',\n' + gap) + '\n' +
                                mind + ']' :
                          '[' + partial.join(',') + ']';
                    gap = mind;
                    return v;
                }

                // If the replacer is an array, use it to select the members to be stringified.

                if (rep && typeof rep === 'object') {
                    length = rep.length;
                    for (i = 0; i < length; i += 1) {
                        k = rep[i];
                        if (typeof k === 'string') {
                            v = str(k, value);
                            if (v) {
                                partial.push(quote(k) + (gap ? ': ' : ':') + v);
                            }
                        }
                    }
                } else {

                    // Otherwise, iterate through all of the keys in the object.

                    for (k in value) {
                        if (Object.hasOwnProperty.call(value, k)) {
                            v = str(k, value);
                            if (v) {
                                partial.push(quote(k) + (gap ? ': ' : ':') + v);
                            }
                        }
                    }
                }

                // Join all of the member texts together, separated with commas,
                // and wrap them in braces.

                v = partial.length === 0 ? '{}' :
                gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' +
                        mind + '}' : '{' + partial.join(',') + '}';
                gap = mind;
                return v;
        }
    }

    // If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

            // The stringify method takes a value and an optional replacer, and an optional
            // space parameter, and returns a JSON text. The replacer can be a function
            // that can replace values, or an array of strings that will select the keys.
            // A default replacer method can be provided. Use of the space parameter can
            // produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

            // If the space parameter is a number, make an indent string containing that
            // many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

                // If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

            // If there is a replacer, it must be a function or an array.
            // Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                     typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

            // Make a fake root object containing our value under the key of ''.
            // Return the result of stringifying the value.

            return str('', { '': value });
        };
    }


    // If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

            // The parse method takes a text and an optional reviver function, and returns
            // a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

                // The walk method is used to recursively walk the resulting structure so
                // that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


            // Parsing happens in four stages. In the first stage, we replace certain
            // Unicode characters with escape sequences. JavaScript handles many characters
            // incorrectly, either silently deleting them, or treating them as line endings.

            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

            // In the second stage, we run the text against regular expressions that look
            // for non-JSON patterns. We are especially concerned with '()' and 'new'
            // because they can cause invocation, and '=' because it can cause mutation.
            // But just to be safe, we want to reject all unexpected forms.

            // We split the second stage into 4 regexp operations in order to work around
            // crippling inefficiencies in IE's and Safari's regexp engines. First we
            // replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
            // replace all simple value tokens with ']' characters. Third, we delete all
            // open brackets that follow a colon or comma or that begin the text. Finally,
            // we look to see that the remaining characters are only whitespace or ']' or
            // ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/.
test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

                // In the third stage we use the eval function to compile the text into a
                // JavaScript structure. The '{' operator is subject to a syntactic ambiguity
                // in JavaScript: it can begin a block or an object literal. We wrap the text
                // in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

                // In the optional fourth stage, we recursively walk the new structure, passing
                // each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function' ?
                    walk({ '': j }, '') : j;
            }

            // If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
} ());

function startSyncFaxCount(){
    var pathName = document.location.pathname;
    var biasIndex = pathName.indexOf('/', 1);
    var companyUID = pathName.substr(0, biasIndex);

    var FaxIDList =  "";
    $(".fax_msg").each(function(){
        var FaxID= parseInt($(this).attr("id").replace("fax_",""));
        if(FaxIDList==""){
            FaxIDList=FaxID;
        }else{
            FaxIDList+=","+FaxID;
        }
    });
    $.ajax({
        url: companyUID + "/Home/QueryFaxOnlineCount",
        data: {
            "FaxIDList": FaxIDList
        },
        type: "post",
        dataType: "json",
        success: function (result) {
            if (!result.IsSuccess || result.Data == undefined) {
                return;
            }

            $("#top-notice-fax-count").text(result.Data.length);
            if (result.Data != undefined &&
                result.Data.length > 0) {
                $("#top-notice .top-item-icon").css("background-position", "-82px -197px");

                var maxZIndex = 1;

                $(".fax_msg").each(function () {
                    var zIndex = parseInt($(this).css("z-index"));
                    if (!isNaN(zIndex) && zIndex > maxZIndex) {
                        maxZIndex = zIndex;
                    }
                });

                for (var i = 0; i < result.Data.length; i++) {
                    var faxGroup = result.Data[i];
                    var faxMsg = $("#fax_" + faxGroup.ID);
                    if (faxMsg.length == 0) {
                        faxMsg = $("<div/>");
                        faxMsg.attr("class", "fax_msg");
                        faxMsg.attr("id", "fax_" + faxGroup.ID);
                        faxMsg.attr("result", faxGroup.Result);
                        faxMsg.css("z-index", ++maxZIndex);

                        faxMsg.append(faxGroup.Display);

                        if (faxGroup.Result == 0) {
                            setTimeout(function () {
                                faxMsg.remove();
                            }, 60000);
                        }
                        if (faxGroup.Result != -1) {
                            var del = $("<a/>");
                            del.css({ "float": "right", "margin-right": "5px", "margin-top": "2px" });
                            del.attr("href", "javascript:void(0)")
                            del.append("<img style='border:0;' src='/themes/default/images/icons/delete.png'/>");
                            del.bind("click", function () {
                                $(this).parent().remove();
                                _RefreshFaxMsgIsDisplay();
                            });
                            faxMsg.append(del);
                        }

                        $("#fax_msg_box").prepend(faxMsg);
                    } else {
                        var faxResult = parseInt(faxMsg.attr("result"));
                        if (!isNaN(faxResult) && faxResult != faxGroup.Result) {
                            faxMsg.attr("result", faxGroup.Result);
                            faxMsg.css("z-index", ++maxZIndex);
                            faxMsg.html("");

                            faxMsg.append(faxGroup.Display);
                            $("#fax_msg_box").prepend(faxMsg);

                            if (faxGroup.Result == 0) {
                                setTimeout(function () {
                                    faxMsg.remove();
                                }, 60000);
                            }
                            if (faxGroup.Result != -1) {
                                var del = $("<a/>");
                                del.css({ "float": "right", "margin-right": "5px", "margin-top": "2px" });
                                del.attr("href", "javascript:void(0)")
                                del.append("<img style='border:0;' src='/themes/default/images/icons/delete.png'/>");
                                del.bind("click", function () {
                                    $(this).parent().remove();
                                    _RefreshFaxMsgIsDisplay();
                                });
                                faxMsg.append(del);
                            }
                        }
                    }
                }
                _RefreshFaxMsgIsDisplay();
                setTimeout(startSyncFaxCount, 10000);
            } else {
                $("#top-notice .top-item-icon").css("background-position", "-64px -197px");
            }
        }
    });
}
function _RefreshFaxMsgIsDisplay() {
    if ($(".fax_msg").length > 0) {
        $("#fax_msg_box").show();
    } else {
        $("#fax_msg_box").hide();
    }
}
function reSendFax(a){
    var pathName = document.location.pathname;
    var biasIndex = pathName.indexOf('/', 1);
    var companyUID = pathName.substr(0, biasIndex);

    $.ajax({
        url: companyUID + "/Home/ResendFax",
        data: { 
            "ID":$(a).attr("FaxID")
        },
        type: "post",
        dataType : "json",
        success: function (result) {
            $(a).parent(".fax_msg").remove();
            if(result.IsSuccess){
                startSyncFaxCount();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ajaxError(jqXHR, textStatus, errorThrown);
        }
    });
}
function SendFaxTest(ID){
    var pathName = document.location.pathname;
    var biasIndex = pathName.indexOf('/', 1);
    var companyUID = pathName.substr(0, biasIndex);

    $.ajax({
        url: companyUID + "/Home/SendFaxTest",
        data: { 
            "ID":ID
        },
        type: "post",
        dataType : "json",
        success: function (result) {
            startSyncFaxCount();
        }
    });
}
function getFenTuanCode(i) {
    if (i < 26) {
        return String.fromCharCode(65 + i);
    } else {
        return 'A' + String.fromCharCode(65 + i - 26);
    }
}