let stomp = null;

function initialize() {
	const sock = new SockJS('/restaurant-websocket');
	stomp = Stomp.over(sock);
	stomp.connect({}, function(frame) {
		console.log('Connected from order');
		stomp.subscribe("/topic/order/update", handleOrderUpdates);
		stomp.subscribe("/topic/order/status", handleOrderStatus);
	});
	refreshTables();
	document.getElementById("loadmsg").style.display = "none";
}

function handleOrderUpdates(incoming) {
	const details = JSON.parse(incoming.body);
	console.log('Received : handleOrderChanges ', details);
	console.log('Received : Browser ', document.getElementById("actionBrowser").value);

	const actionBrowser = document.getElementById("actionBrowser").value;
	const status = details.order.status;
	const tableid = details.table.toString();
	const orderid = details.order.id.toString();
	
	console.log();
	
	if(actionBrowser === "true"&&
			(status === document.getElementById("status").textContent) &&
			(tableid === document.getElementById("tablenum").textContent) &&
			(orderid === document.getElementById("ordernum").textContent)) {
		alert("Order is updated.");
		return;
	}

	//ORDERED and PREPARING status only
	//Match status, table and order	
	if(actionBrowser ===  "false" && 
			(status === "ORDERED" || status === "PREPARING") &&
			(status === document.getElementById("status").textContent) &&
			(tableid === document.getElementById("tablenum").textContent) &&
			(orderid === document.getElementById("ordernum").textContent)) {
		document.getElementById("message").style.display = "inline";
		document.getElementById("modifyOrder").disabled = true;
		document.getElementById("cancelOrder").disabled = true;
		const tableNumElem = window.parent.document.getElementById("tables").contentWindow.document.getElementById("tablestatus"+tableid);
		tableNumElem.className = "hightablestatus";
	}

}

function handleOrderStatus(incoming) {
	const details = JSON.parse(incoming.body);
	console.log('Received : handleOrderChanges ', details);
	console.log('Received : Browser ', document.getElementById("actionBrowser").value);
	
	const initState = details.initialState;
	const status = details.order.status;
	const tableid = details.table.toString();
	const orderid = details.order.id.toString();
	const actionBrowser = document.getElementById("actionBrowser").value;
	
	if(actionBrowser === "true" && (details.table.toString() === document.getElementById("tablenum").textContent)) {
		if((initState === "ORDERED" && status === "CANCELLED") || 
			(initState === "PREPARING" && status === "BILLED" && 
				(orderid === document.getElementById("ordernum").textContent))) {
			alert("Order is cancelled.");
			window.parent.document.getElementById("menuDetails").src="/tables/notable/"+details.order.id;
		}
		else if(initState === "" && status === "ORDERED") {
			alert("Order is created.");
		}
		return;
	} 
	
	if (actionBrowser === "false" && (details.table.toString() === document.getElementById("tablenum").textContent)) {
		if((status === "ORDERED" || status === "BILLED" || status === "CANCELLED") || 
			((status === "PREPARING" || status === "READY" || status === "SERVED") &&
				(orderid === document.getElementById("ordernum").textContent))) {
			document.getElementById("message").style.display = "inline";
			document.getElementById("modifyOrder").disabled = true;
			document.getElementById("cancelOrder").disabled = true;
			const tableNumElem = window.parent.document.getElementById("tables").contentWindow.document.getElementById("tablestatus"+tableid);
			tableNumElem.className = "hightablestatus";
		}
	}	
}

function refreshTables(){
	const operation = document.getElementById("operation").value;
	if(operation === "STATEERROR") {
		window.parent.document.getElementById("tables").src="/tables";
		alert("Update failed as order details are stale. Try again!");
		return false;
	}
}

function updateQty(elem, action) {
	let qtyElem = document.getElementById(elem.name); 
	let qty = qtyElem.value;
	let status = document.getElementById("order-status").value;
	let dishName = document.getElementById(elem.name+"_NAME"); 

	if(action === "clear") {
		if(qty !== "") {
			if(status === "PREPARING" && elem.getAttribute("data-qty") !== null) {
				alert("Your order is getting prepared. Already ordered dishes cannot be removed.");
				return;
			}
		}
		qtyElem.style.border = "";
		dishName.style.color = "black";
		qtyElem.value = "";
	  	return
	}

	if(action === "minus") {
		if(qty !== "") {
			if(status === "PREPARING" && elem.getAttribute("data-qty") !== null) {
				if((Number(qty) - 1) < Number(elem.getAttribute("data-qty"))) {
					alert("Your order is getting prepared. Number of already ordered dishes cannot be reduced.");
					return;
				}				
			}
			if(Number(qty)==1) {
				qtyElem.value = "";
				qtyElem.style.border = "";
				dishName.style.color = "black";
				return
			}
			qtyElem.value = Number(qty) - 1;
		}
	}
	
	if(action === "plus") {
		qtyElem.style.border = "2px solid #FF0000";
		dishName.style.color = "red";
		if(qty !== "") {
			qtyElem.value = Number(qty) + 1;
			return
		}
		if(qty === "") {
			qtyElem.value = 1;
		}
	}
}

function checkQty() {
	let qtys = document.querySelectorAll(".dishqty");

	for (i = 0; i < qtys.length; i++) {
		if(qtys[i].value !== "") {
			document.getElementById("actionBrowser").value = "true";
			return true;
		}
	}
	alert("Order needs atleast one dish.")
	return false;
}

function orderCancel(orderid){
	let init = document.getElementById("order-status").value;
	let fin = "CANCELLED";

	if(init === "ORDERED") {
		let reply = confirm("Order will be cancelled. Click OK to confirm cancellation.");
		if(reply === false)
			return false;	
	} else if(init === "PREPARING") {
		let reply = confirm("Order will be billed as already being prepared. Click OK to confirm cancellation.");
		if(reply === false)
			return false;
		fin = "BILLED";
	} else
		return false;
	
	document.getElementById("actionBrowser").value = "true";
	
	stomp.send("/app/changeOrderState", {}, JSON.stringify({
		'orderId' : orderid,
		'state' : init,
		'targetState' : fin
	}));
}

function reloadOrder(tableNum) {
	document.location.href = document.location.href;
	const tableNumElem = window.parent.document.getElementById("tables").contentWindow.document.getElementById("tablestatus"+tableNum);
	tableNumElem.className = "tablestatus";
}