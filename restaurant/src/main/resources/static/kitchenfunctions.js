let stomp = null;

function initialize() {
	const sock = new SockJS('/restaurant-websocket');
	stomp = Stomp.over(sock);
	stomp.connect({}, function(frame) {
		console.log('Connected');
		stomp.subscribe("/topic/kitchen/initial", initialOrders);
		stomp.subscribe("/topic/order/update", handleUpdatedOrder);
		stomp.subscribe("/topic/order/status", handleChangeStateOrder);
		stomp.subscribe("/topic/orders/readyrefresh", handleReadyOrders);
		stomp.subscribe("/topic/orders/cancelledrefresh", handleCancelledOrders);

		stomp.send("/app/kitchen/initial", {}, {});
	});
	retrieveReadyOrders();
	retrieveCancelledOrders();
}

function removeLoadMsg(){
	document.getElementById("loadmsg").style.display = "none";
}

function retrieveReadyOrders() {
    let readyOrders = setInterval(function(){
    	stomp.send("/app/orders/readyrefresh", {}, {});
    },30000);
}

function retrieveCancelledOrders() {
    let cancelledOrders = setInterval(function(){
    	stomp.send("/app/orders/cancelledrefresh", {}, {});
    },30000);
}

function updateStatus(elem) {
	stomp.send("/app/changeOrderState", {}, JSON.stringify({
		'orderId' : elem.getAttribute("data-orderid"),
		'state' : elem.getAttribute("data-initial").toUpperCase(),
		'targetState' : elem.getAttribute("data-final").toUpperCase()
	}));
}

function initialOrders(incoming) {
	const orders = JSON.parse(incoming.body);
	console.log('Received: ', orders);
	createTable(orders.orderedOrders, document.getElementById("ordered"));
	createTable(orders.preparingOrders, document.getElementById("preparing"));
	createTable(orders.readyOrders, document.getElementById("ready"));
	createTable(orders.cancelledOrders, document.getElementById("cancelled"));
	removeLoadMsg();
}

function handleChangeStateOrder(incoming) {
	const details = JSON.parse(incoming.body);
	console.log('Received : ', details);
	
	const order = details.order;	
	const orderId = order.id;
	const status = order.status;
	const initState = details.initialState.toUpperCase();
	
	const orderedContainer = document.getElementById("ordered");
	const preparingContainer = document.getElementById("preparing");
	const readyContainer = document.getElementById("ready");
	const cancelledContainer = document.getElementById("cancelled");
	
	if(status === "ORDERED") {
		createOrderElement(order, orderedContainer);
	}
	else if(status === "PREPARING") {
		let removeOrderDiv = orderedContainer.querySelector("#" + "ORD_" + orderId);
		orderedContainer.removeChild(removeOrderDiv);

		preparingContainer.prepend(removeOrderDiv);
		let statusButton = removeOrderDiv.querySelector("#" + "BUT_" + orderId);
		statusButtonText(statusButton, "Ready To Serve", "preparing", "ready");
	} 
	else if(status === "READY") {
		let remOrderDiv = preparingContainer.querySelector("#" + "ORD_" + orderId);
		preparingContainer.removeChild(remOrderDiv);
		
		readyContainer.prepend(remOrderDiv);
		const statusButtonDiv = remOrderDiv.querySelector("#" + "BUT_" + order.id).parentElement;
		remOrderDiv.removeChild(statusButtonDiv);
	} 
	else if(status === "SERVED") {
		let remOrderDiv = readyContainer.querySelector("#" + "ORD_" + orderId);
		readyContainer.removeChild(remOrderDiv);
	} 
	else if(initState === "PREPARING" && status === "BILLED") {
		let remOrderDiv = preparingContainer.querySelector("#" + "ORD_" + orderId);
		preparingContainer.removeChild(remOrderDiv);
	} 
	else if(initState === "ORDERED" && status === "CANCELLED") {
		let removeOrderDiv = orderedContainer.querySelector("#" + "ORD_" + orderId);
		orderedContainer.removeChild(removeOrderDiv);
		
		const statusButton = removeOrderDiv.querySelector("#" + "BUT_" + order.id);
		removeOrderDiv.removeChild(statusButton.parentElement);
		
		const orderTableDiv = removeOrderDiv.querySelector("#" + "OT_" + order.id);
		const tableNoSpan = orderTableDiv.querySelector("#tableid");

		orderTableDiv.removeChild(tableNoSpan.previousElementSibling);
		orderTableDiv.removeChild(tableNoSpan);	
		
		cancelledContainer.prepend(removeOrderDiv);
	}
}

function handleCancelledOrders(incoming) {
	const orders = JSON.parse(incoming.body);
	console.log('Received: ', orders);
	refreshOrders(orders, document.getElementById("cancelled"));
}

function handleReadyOrders(incoming) {
	const orders = JSON.parse(incoming.body);
	console.log('Received: ', orders);
	refreshOrders(orders, document.getElementById("ready"));
}

function refreshOrders(recentOrders, container) {
	const presentOrders = container.querySelectorAll(".order");
	
	for(let i=0; i < presentOrders.length; i++) {		
		if(recentOrders.includes(presentOrders[i].id) === false)
			container.removeChild(presentOrders[i]);
	}
}

function createTable(data, mainContainer) {	
	for (i = 0; i < data.length; i++) {
		createOrderElement(data[i], mainContainer);
	}
}

function createOrderElement(order, mainContainer) {
	
	const existingOrderDiv = mainContainer.querySelector("#" + "ORD_" + order.id);	
	if(existingOrderDiv != null)
		return false;
	
	const orderDiv = document.createElement('div');
	//mainContainer.appendChild(orderDiv);
	mainContainer.prepend(orderDiv);
	orderDiv.id = "ORD_" + order.id;
	orderDiv.className = "order";
	orderDiv.setAttribute("data-orderid",order.id);
	
	if(order.status === "ORDERED" || order.status === "PREPARING")
		orderDiv.appendChild(changeStatusElement(order, mainContainer));
	
	orderDiv.appendChild(orderAndTableTextElement(order));
	
	orderDiv.appendChild(orderLinesElement(order));
}

function changeStatusElement(order, mainContainer) {
	
	const statusChangeDiv = document.createElement('div');
	const statusButton = document.createElement('button');
	statusButton.id = "BUT_" + order.id;
	statusButton.className = "padded";
	
	if(mainContainer.id === "ordered") {	
		statusButtonText(statusButton, "Start Preparing", "ordered", "preparing");
	} else if(mainContainer.id === "preparing") {
		statusButtonText(statusButton, "Ready To Serve", "preparing", "ready");
	}
	
	statusButton.setAttribute("data-orderid",order.id);
	statusButton.setAttribute("data-tableid",order.table.id);
	statusButton.onclick = function(){updateStatus(this)};
	statusChangeDiv.appendChild(statusButton);
	
	return statusChangeDiv;
}

function statusButtonText(statusButton, text, initial, final) {
	statusButton.textContent = text;
	statusButton.setAttribute("data-initial", initial);
	statusButton.setAttribute("data-final", final);
}

function orderAndTableTextElement(order) {
	const orderAndTableIdDiv = document.createElement('div');
	orderAndTableIdDiv.id = "OT_" + order.id ;
	orderAndTableIdDiv.className = "padded";
	
	const orderTextSpan = document.createElement('span');
	orderTextSpan.textContent = "Order No - ";
	orderAndTableIdDiv.appendChild(orderTextSpan);
	const orderIdSpan = document.createElement('span');
	orderIdSpan.id = "orderid";
	orderIdSpan.textContent = order.id;
	orderAndTableIdDiv.appendChild(orderIdSpan);
	
	if(order.status === "CANCELLED")
		return orderAndTableIdDiv;
	
	const tableTextSpan = document.createElement('span');
	tableTextSpan.textContent = " Table No - ";
	orderAndTableIdDiv.appendChild(tableTextSpan);
	const tableIdSpan = document.createElement('span');
	tableIdSpan.id = "tableid";
	tableIdSpan.textContent = order.table.id;
	orderAndTableIdDiv.appendChild(tableIdSpan);
	
	return orderAndTableIdDiv;
}

function createOrderLines(orderLinesDiv, line, dishNameId) {
	
	const lineDiv = document.createElement('div');
	lineDiv.className = "padded";
	lineDiv.id = dishNameId;
	orderLinesDiv.appendChild(lineDiv);
	
	const dishName = document.createElement('span');
	dishName.className = "dishname";
	dishName.id = dishNameId+"_NAME";
	dishName.textContent = line.dish.name;
	lineDiv.appendChild(dishName);
	
	let dishQty = document.createElement('span');
	dishQty.className = "dishqty";
	dishQty.id = dishNameId+"_QTY";
	dishQty.textContent = line.qty;
	lineDiv.appendChild(dishQty);
}

function orderLinesElement(order) {
	const orderLinesDiv = document.createElement('div');
	orderLinesDiv.id = "OTL_" + order.id;
	orderLinesDiv.className = "dishes";
	
	for (let j = 0; j < order.orderLines.length; j++) {
		const line = order.orderLines[j];
		const dishNameId = line.dish.name.replace(/\W+/g,'_');
		
		createOrderLines(orderLinesDiv, line, dishNameId);
	}
	return orderLinesDiv;
}

function handleUpdatedOrder(incoming) {
	const details = JSON.parse(incoming.body);
	console.log('Received State: ', details);
	
	const order = details.order;
	const orderId = order.id;
	const status = order.status;
	const orderLines = order.orderLines;
	let mainContainer = null;
	
	if(status === "ORDERED") {
		mainContainer = document.getElementById("ordered");
	} else if(status === "PREPARING") {
		mainContainer = document.getElementById("preparing");
	}
	
	const orderLinesDiv = mainContainer.querySelector("#" + "OTL_" + orderId);
	let currentDishNameIds = [];
	for (let j = 0; j < orderLines.length; j++) {
		const line = orderLines[j];
		let dishNameId = line.dish.name.replace(/\W+/g,'_');
		
		currentDishNameIds.push(dishNameId);

		let lineDiv = orderLinesDiv.querySelector("#"  +dishNameId);
		let dishName = "";
		let dishQty = "";
		if(lineDiv === null) {			
			createOrderLines(orderLinesDiv, line, dishNameId);
		} else {
			dishName = lineDiv.querySelector("#" + dishNameId + "_NAME");
			dishName.textContent = line.dish.name;
			dishQty = lineDiv.querySelector("#" + dishNameId + "_QTY");
			dishQty.textContent = line.qty;	
		}		
	}
	
	//Remove old orderlines from order
	const maybeOldLines = orderLinesDiv.children;
	for (let k=0; k < maybeOldLines.length; k++) {
		let dishNameId = maybeOldLines[k].id;
		if(currentDishNameIds.includes(dishNameId) === false) {
			orderLinesDiv.removeChild(maybeOldLines[k]);
			k--;
		} 
	}
	
	let orderDiv = mainContainer.querySelector("#" + "ORD_" + orderId);
	mainContainer.removeChild(orderDiv);	
	mainContainer.prepend(orderDiv);
}