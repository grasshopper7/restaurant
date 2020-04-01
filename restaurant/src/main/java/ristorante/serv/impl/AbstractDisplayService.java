package ristorante.serv.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.Tables;
import ristorante.serv.OrderService;

public abstract class AbstractDisplayService {

	protected final OrderService orderService;
	
	protected final SimpMessagingTemplate messaging;

	@Autowired
	public AbstractDisplayService (OrderService orderService, SimpMessagingTemplate messaging) {
		this.orderService = orderService;
		this.messaging = messaging; 
	}
	
	protected List<Order> getUIOrdersForStatus(OrderStatus state) {

		List<Order> statusOrders = orderService.findOrdersByStatus(state);
		return processOrderUI(statusOrders);
	}
	
	protected List<Order> getRecentUIOrdersForStatus(OrderStatus state, Duration duration) {

		List<Order> statusOrders = orderService.findOrdersByStatus(state);
		List<Order> statusRecentOrders = statusOrders.stream()
				.filter(o -> o.getModificationTime().isAfter(LocalDateTime.now().minus(duration)))
				.collect(Collectors.toList());
		return processOrderUI(statusRecentOrders);
	}
	
	protected List<String> updateOrdersId(OrderStatus state, Duration duration) {

		List<Order> statusOrders = orderService.findOrdersByStatus(state);
		List<String> recentOrders = statusOrders.stream()
				.filter(o -> o.getModificationTime().isAfter(LocalDateTime.now().minus(duration)))
				.map(o -> "ORD_" + o.getId()).collect(Collectors.toList());
		return recentOrders;
	}	

	public static List<Order> processOrderUI(List<Order> orders) {

		orders.sort(Comparator.comparing((Order o) -> o.getModificationTime()));		
		return orders.stream().map(o -> processOrderUI(o)).collect(Collectors.toList());
	}

	public static Order processOrderUI(Order order) {

		Order uiOrder = new Order(order.getId());
		uiOrder.setStatus(order.getStatus());
		uiOrder.setOrderLines(order.getOrderLines());
		
		// To avoid recursion stack overflow between Table & Order
		if (order.getTable() != null)
			uiOrder.setTable(new Tables(order.getTable().getId()));
		// To reduce data
		uiOrder.setModificationTime(null);
		return uiOrder;
	}	
}
