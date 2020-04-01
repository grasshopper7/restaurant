let stomp = null;

function initialize() {
	const sock = new SockJS('/restaurant-websocket');
	stomp = Stomp.over(sock);
	stomp.connect({}, function(frame) {
		console.log('Connected');
		stomp.subscribe("/topic/server/initial", initialOrders);
		stomp.subscribe("/topic/order/status", handleChangeStateOrder);
		stomp.subscribe("/topic/orders/billedrefresh", handleBilledOrders);

		stomp.send("/app/server/initial", {}, {});
	});
	retrieveBilledOrders();
}

function removeLoadMsg(){
	document.getElementById("loadmsg").style.display = "none";
}

function retrieveBilledOrders() {
    let downloadOrders = setInterval(function(){
    	stomp.send("/app/orders/billedrefresh", {}, {});
    },30000);
}

function updateStatus(elem) {
	stomp.send("/app/changeOrderState", {}, JSON.stringify({
		'orderId' : elem.getAttribute("data-orderid"),
		'state' : elem.getAttribute("data-initial").toUpperCase(),
		'targetState' : elem.getAttribute("data-final").toUpperCase()
	}));
}

function handleChangeStateOrder(incoming) {
	const details = JSON.parse(incoming.body);
	console.log('Received : ', details);
	
	const order = details.order;
	const id = order.id;
	const status = order.status.toUpperCase();
	const initState = details.initialState.toUpperCase();
	
	const readyContainer = document.getElementById("ready");
	const servedContainer = document.getElementById("served");
	const billedContainer = document.getElementById("billed");
	
	if(status === "READY") {
		createOrderElement(order, readyContainer);
	} else if (status === "SERVED") {
		moveOrderElement(order, readyContainer, servedContainer);
	} else if (initState === "PREPARING" && status === "BILLED") {
		createOrderElement(order, billedContainer);
	} else if (initState === "SERVED" && status === "BILLED") {
		moveOrderElement(order, servedContainer, billedContainer);
	}
}

function handleBilledOrders(incoming) {
	const orders = JSON.parse(incoming.body);
	console.log('Received: ', orders);
	refreshBilledOrders(orders);
}

function refreshBilledOrders(recentOrders) {
	const billOrdContainer = document.getElementById("billed");	
	const presentOrders = billOrdContainer.querySelectorAll(".order");
	
	for(let i=0; i < presentOrders.length; i++) {		
		if(recentOrders.includes(presentOrders[i].id) === false)
			billOrdContainer.removeChild(presentOrders[i]);
	}
}

function initialOrders(incoming) {
	const orders = JSON.parse(incoming.body);
	console.log('Received: ', orders);
	createTable(orders.readyOrders, document.getElementById("ready"));
	createTable(orders.servedOrders, document.getElementById("served"));
	createTable(orders.billedOrders, document.getElementById("billed"));
	removeLoadMsg();
}

function moveOrderElement(order, source, target) {
	const removeOrderDiv = source.querySelector("#" + "ORD_" + order.id);
	
	if(removeOrderDiv !== null) {
		source.removeChild(removeOrderDiv);		
		const statusButton = removeOrderDiv.querySelector("#" + "BUT_" + order.id);
		
		if(order.status.toUpperCase() === "SERVED") {			
			statusButton.textContent = "Charge Money";
			statusButton.setAttribute("data-initial", "served");
			statusButton.setAttribute("data-final", "billed");
			
		} else if(order.status.toUpperCase() === "BILLED") {			
			const orderTableDiv = removeOrderDiv.querySelector("#" + "OT_" + order.id);
			const tableNoSpan = orderTableDiv.querySelector("#tableid");

			orderTableDiv.removeChild(tableNoSpan.previousElementSibling);
			orderTableDiv.removeChild(tableNoSpan);			
			removeOrderDiv.removeChild(statusButton.parentElement);
		}
		
		//target.appendChild(removeOrderDiv);
		target.prepend(removeOrderDiv);
	}
}

function removeOrderElement(order, source) {
	const removeOrderDiv = source.querySelector("#" + "ORD_" + order.id);	
	if(removeOrderDiv !== null) {
		source.removeChild(removeOrderDiv);
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
		return;
	
	const orderDiv = document.createElement('div');
	//mainContainer.appendChild(orderDiv);
	mainContainer.prepend(orderDiv);
	orderDiv.id = "ORD_" + order.id;
	orderDiv.className = "order";
	orderDiv.setAttribute("data-orderid",order.id);
	
	if(order.status !== "BILLED")
		orderDiv.appendChild(changeStatusElement(order, mainContainer));
	
	orderDiv.appendChild(orderAndTableTextElement(order));
	
	orderDiv.appendChild(orderLinesElement(order));
}

function changeStatusElement(order, mainContainer) {
		
	const statusChangeDiv = document.createElement('div');
	const statusButton = document.createElement('button');
	statusButton.id = "BUT_" + order.id;
	statusButton.className = "padded";
	
	if(mainContainer.id === "ready") {	
		statusButtonText(statusButton, "Time to Serve", "ready", "served");
	} else if(mainContainer.id === "served") {
		statusButtonText(statusButton, "Charge Money", "served", "billed");
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
	
	if(order.status === "BILLED")
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

function orderLinesElement(order) {
	const orderLinesDiv = document.createElement('div');
	orderLinesDiv.id = "OTL_" + order.id;
	orderLinesDiv.className = "dishes";
	
	for (let j = 0; j < order.orderLines.length; j++) {
		const line = order.orderLines[j];
		const dishNameId = line.dish.name.replace(/\W+/g,'_');
		
		const lineDiv = document.createElement('div');
		lineDiv.className = "padded";
		lineDiv.id = dishNameId;
		orderLinesDiv.appendChild(lineDiv);
		
		dishName = document.createElement('span');
		dishName.className = "dishname";
		dishName.id = dishNameId+"_NAME";
		dishName.textContent = line.dish.name;
		lineDiv.appendChild(dishName);
		
		dishQty = document.createElement('span');
		dishQty.className = "dishqty";
		dishQty.id = dishNameId+"_QTY";
		dishQty.textContent = line.qty;
		lineDiv.appendChild(dishQty);
	}
	
	return orderLinesDiv;
}