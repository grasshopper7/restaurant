package ristorante.serv.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ristorante.display.KitchenOrdersUI;
import ristorante.entity.Order.OrderStatus;
import ristorante.serv.KitchenService;
import ristorante.serv.OrderService;

@Service
public class KitchenServiceImpl extends AbstractDisplayService implements KitchenService {

	@Autowired
	public KitchenServiceImpl(OrderService orderService,  SimpMessagingTemplate messaging) {
		super(orderService, messaging);
	}

	@SendTo("/topic/kitchen/initial")
	@Override
	public void initialKitchenDisplay() {
		
		KitchenOrdersUI ordersUI = new KitchenOrdersUI(getUIOrdersForStatus(OrderStatus.ORDERED),
				getUIOrdersForStatus(OrderStatus.PREPARING),
				getRecentUIOrdersForStatus(OrderStatus.READY, Duration.ofMinutes(5)),
				getRecentUIOrdersForStatus(OrderStatus.CANCELLED, Duration.ofMinutes(5)));
		
		messaging.convertAndSend("/topic/kitchen/initial", ordersUI);
	}	

	@SendTo("/topic/orders/readyrefresh")
	@Override
	public void updateReadyOrders() {

		messaging.convertAndSend("/topic/orders/readyrefresh", updateOrdersId(OrderStatus.READY, Duration.ofMinutes(5)));
	}

	@SendTo("/topic/orders/cancelledrefresh")
	@Override
	public void updateCancelledOrders() {

		messaging.convertAndSend("/topic/orders/cancelledrefresh", updateOrdersId(OrderStatus.CANCELLED, Duration.ofMinutes(5)));
	}
}
