package ristorante.serv.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ristorante.display.ServerOrdersUI;
import ristorante.entity.Order.OrderStatus;
import ristorante.serv.OrderService;
import ristorante.serv.ServerService;

@Service
public class ServerServiceImpl extends AbstractDisplayService implements ServerService {

	
	@Autowired
	public ServerServiceImpl(OrderService orderService, SimpMessagingTemplate messaging) {
		super(orderService, messaging);
	}

	@SendTo("/topic/server/initial")
	@Override
	public void initialServerDisplay() {

		ServerOrdersUI ordersUI = new ServerOrdersUI(getUIOrdersForStatus(OrderStatus.READY),
				getUIOrdersForStatus(OrderStatus.SERVED),
				getRecentUIOrdersForStatus(OrderStatus.BILLED, Duration.ofMinutes(5)));

		messaging.convertAndSend("/topic/server/initial", ordersUI);
	}

	@SendTo("/topic/orders/billedrefresh")
	@Override
	public void updateBilledOrders() {

		messaging.convertAndSend("/topic/orders/billedrefresh", updateOrdersId(OrderStatus.BILLED, Duration.ofMinutes(5)));
	}
}
