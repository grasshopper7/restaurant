package ristorante.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import ristorante.serv.OrderService;
import ristorante.serv.impl.OrderStatusChanges.OrderStatusData;

@Controller
public class OrderController {

	private final OrderService orderService;
	

	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@MessageMapping("/changeOrderState")
	public void getUIOrderForStatusChange(OrderStatusData data) {

		orderService.changeOrderStatus(orderService.findOrderById(data.getOrderId()),
				data.getState(), data.getTargetState());
	}
}