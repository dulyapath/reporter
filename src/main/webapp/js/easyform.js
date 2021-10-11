var log = console.log.bind();

$('.es-table .es-sort, .es-table .es-sort-asc, .es-table .es-sort-desc').on('click', function (){
    var currentObj = $(this);
   var currentAction = ""; 
   if(currentObj.hasClass('es-sort')){
       currentAction = "asc";
   }else if(currentObj.hasClass('es-sort-asc')){
       currentAction = "desc";
   }else if(currentObj.hasClass('es-sort-desc')){
       currentAction = "asc";
   }
   
   currentObj.closest('tr').find('[class*="es-sort"]').removeClass('es-sort es-sort-asc es-sort-desc').addClass('es-sort');
   
  currentObj.removeClass('es-sort').addClass('es-sort-'+currentAction);
});