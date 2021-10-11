(function () {
    $(function () {
        $(".smbox-icon").on("click", function () {
            var box = $(this);
            box.prev(".smbox-input").focus();

            if (box.hasClass("fa-close")) {
                box.prev(".smbox-input").val("");
                box.removeClass("fa-close").addClass("fa-search");
            }
        });

        $('.smbox-input').on("input", function () {
            if ($(this).val() == "") {
                $(this).next(".smbox-icon").removeClass("fa-close").addClass("fa-search");
            } else {
                $(this).next(".smbox-icon").removeClass("fa-search").addClass("fa-close");
            }
        });
    });
})(jQuery);