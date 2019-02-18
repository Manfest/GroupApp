function openFormSign() {
  var back = document.getElementById("backgrnd");
  var span = document.getElementsByClassName("exit")[0];
  back.style.display = "block";
  span.onclick = function () {
    back.style.display = "none";
  };

  window.onclick = function (event) {
    if (event.target == back) {
      back.style.display = "none";
    }
  };

};