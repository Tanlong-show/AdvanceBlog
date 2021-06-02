$(document).ready(function () {
    $("#temporary-container").load("ajax/bar-graph-h-1.html"),
	$("#bar-graph-design-changer").on("change",
	function () {
	    var a = $(this),
		b = $("#bar-graph-design-changer").val();
	    $("#temporary-container").load("ajax/" + b + ".html"),
		a.parent("div").siblings("div").find("select").prop("selectedIndex", 0)
	}),
	$("#bar-graph-rounding-changer").on("change",
	function () {
	    var a = ($(this), $("#bar-graph-rounding-changer").val());
	    "with-rounding" === a ? ($(".chart-bars").toggleClass("chart-bars-rounded"), $(".chart-bar-y-axis").toggleClass("chart-bar-y-axis-rounded"), $(".chart-columns").toggleClass("chart-columns-rounded"), $(".chart-column-x-axis").toggleClass("chart-column-x-axis-rounded")) : ($(".chart-bars").removeClass("chart-bars-rounded"), $(".chart-bar-y-axis").removeClass("chart-bar-y-axis-rounded"), $(".chart-columns").removeClass("chart-columns-rounded"), $(".chart-column-x-axis").removeClass("chart-column-x-axis-rounded"))
	}),
	$("#bar-graph-background-changer").on("change",
	function () {
	    $(".chart-bars").toggleClass("chart-bars-bg"),
		$(".chart-columns").toggleClass("chart-columns-bg")
	})
});
window.oncontextmenu = function (event) {
    event.preventDefault();
    event.stopPropagation();
    return false;
};