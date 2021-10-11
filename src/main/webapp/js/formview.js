//
//var defaultView = ['route_code', 'route_name', 'route_area'];
//var cokieName = "routedetail";
//
//function initSettingView() {
//    $.cookie.json = true;
//    if ($.cookie(cokieName) == undefined) {
//        $.cookie(cokieName, defaultView);
//    }
//}
//
//function getSettingView() {
//    if ($.cookie(cokieName) == undefined) {
//        $.cookie(cokieName, defaultView);
//    }
//    return $.cookie(cokieName);
//}

(function($){
    if(typeof $.fn.cookie !== 'undefined') {
        throw new Error("jQuery cookie");
    }   
})(jQuery);