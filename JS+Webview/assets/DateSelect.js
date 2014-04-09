 function DateSelect(eleId) {
            var dateChoose = document.getElementById(eleId);
            javascript:window.myjs.chooseDate(dateChoose.value,dateChoose.id);
        }
        
  function setDate(dateStr,eleId){
    var dateChoose = document.getElementById(eleId);
    dateChoose.setAttribute('value',dateStr);
  }