// Callbacks for modal windows
function showAccountModal(id) {
  // build modal form with proper data
  var accountId = $('#accountId' + id).text();
  $('#accountModalId').text(accountId);
  $('#accountModalHiddenId').val(accountId);
  var accountBank = $('#accountBank' + id).text();
  $('#accountModalBank').text(accountBank);
  var accountCurrency = $('#accountCurrency' + id).text();
  $('#accountModalCurrency').text(accountCurrency);
  var accountAmount = $('#accountAmount' + id).text();
  $('#accountModalAmount').val(accountAmount);
  var accountAmountBase = $('#accountAmountBase' + id).text();
  $('#accountModalAmountBase').text(accountAmountBase);

  // show modal
  $('#accountModal').modal('show');
}