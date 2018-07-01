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

  var accountRate = $('#accountRate' + id).text();
  $('#accountModalRate').text(accountRate);
  var rateFloat = parseFloat(accountRate);
  if (isNaN(rateFloat) || rateFloat == 1.0) {
    $('#accountModalRateInput').hide();
  }

  // show modal
  $('#accountModal').modal('show');
}

function showRateModal(id) {
    // build modal form with proper data
    $('#rateModalId').text(id);
    $('#rateModalHiddenId').val(id);
    var currency = $('#rateCurrency' + id).text();
    $('#rateModalCurrency').text(currency);
    var rate = $('#rateRate' + id).text();
    $('#rateModalRate').val(rate);
    // show modal
    $('#rateModal').modal('show');
}

function modalUpdateBaseAmount() {
    var accountAmount = parseFloat($('#accountModalAmount').val());
    var rate = parseFloat($('#accountModalRate').text());
    if(!isNaN(accountAmount) && !isNaN(rate)) {
       $('#accountModalAmountBase').text(Math.round(accountAmount * rate * 100) / 100);
    }
}