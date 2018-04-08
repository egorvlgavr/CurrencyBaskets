// Call the dataTables jQuery plugin
var EDIT_ACCOUNT_ROW_NUMBER = 6;
$(document).ready(function() {
  $('#dataTable').DataTable({
    columnDefs: [ { orderable: false, targets: [EDIT_ACCOUNT_ROW_NUMBER] } ]
  });
});
