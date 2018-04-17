// Call the dataTables jQuery plugin
var EDIT_ACCOUNT_ROW_NUMBER = 7;
$(document).ready(function() {
  $('#dataTable').DataTable({
    columnDefs: [ { orderable: false, targets: [EDIT_ACCOUNT_ROW_NUMBER] } ]
  });
});
