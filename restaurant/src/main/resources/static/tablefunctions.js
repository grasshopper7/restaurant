
function initialize() {
	const sock = new SockJS('/restaurant-websocket');
	let stomp = Stomp.over(sock);
	stomp.connect({}, function(frame) {
		console.log('Connected');
		stomp.subscribe("/topic/order/status", handleChangeStateOrder);
	});
	document.getElementById("loadmsg").style.display = "none";
}

function handleChangeStateOrder(incoming) {
	const details = JSON.parse(incoming.body);
	console.log('Received Table State: ', details);	
	
	//In case of billed and cancelled orders
	const tableNum = details.table;
	const tableNumElem = document.getElementById("tablestatus"+tableNum);
	const displayedState = tableNumElem.textContent;
	let orderState = details.order.status.toUpperCase();
	
	if(orderState !== displayedState) {
		if(orderState === "BILLED" || orderState === "CANCELLED")
			orderState = "VACANT";
		tableNumElem.textContent = orderState;
		tableNumElem.scrollIntoView(false);
	}
}

function opentable(tableNum) {
	let highTables = document.getElementsByClassName("hightablestatus");
	for (let i=0; i<highTables.length; i++) {
		highTables[i].className = "tablestatus";
	}
	window.parent.document.getElementById("menuDetails").src = "/tables/"+tableNum;
}