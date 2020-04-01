
//ordersearch.html
function menuWithTable(tablenum) {
	resetTablesHighlight();
	window.parent.document.getElementById("menuDetails").src="/tables/"+tablenum;
}

function menuWithOutTable(ordernum) {
	resetTablesHighlight();
	window.parent.document.getElementById("menuDetails").src="/tables/notable/"+ordernum;
}

function checkAllNone(elem) {
	let arrStates = document.getElementsByName("checkedState");
	for (let i = 0; i < arrStates.length; i++) {
		arrStates[i].checked = elem.checked;
	}
}

function validateAllNone(elem) {
	if(elem.checked === false && document.getElementsByName("checkAllState")[0].checked === true) {
		document.getElementsByName("checkAllState")[0].checked = elem.checked;
		return;
	}
	
	let allSame = true;
	let arrStates = document.getElementsByName("checkedState");
	for (let i = 0; i < arrStates.length; i++) {
		if(arrStates[i].checked !== elem.checked)
			allSame = false;
	}
	if(allSame === true)
		document.getElementsByName("checkAllState")[0].checked = elem.checked;
}

function checkStatusSelect() {
	let arrStates = document.getElementsByName("checkedState");
	for (let i = 0; i < arrStates.length; i++) {
		if(arrStates[i].checked === true)
			return true;
	}
	alert("Select atleast one status.");
	return false;
}

function resetTablesHighlight() {
	let highTables = window.parent.document.getElementById("tables").contentWindow.document.getElementsByClassName("hightablestatus");
	for (let i=0; i<highTables.length; i++) {
		highTables[i].className = "tablestatus";
	}
}

function removeLoadMsg(){
	document.getElementById("loadmsg").style.display = "none";
}

//home.html
function loadiframe(iframe) {
	iframe.height = 20 + iframe.contentWindow.document.body.scrollHeight + "px";
	let tables = document.getElementById("tables");
	tables.height = 300 + +iframe.contentWindow.document.body.scrollHeight + "px";
}