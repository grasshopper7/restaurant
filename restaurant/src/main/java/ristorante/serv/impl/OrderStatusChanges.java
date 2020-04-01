package ristorante.serv.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import ristorante.entity.Order.OrderStatus;

public class OrderStatusChanges {

	public static final Map<OrderStatus, List<OrderStatus>> allowedStatusChanges = new HashMap<>();
	
	static {
		allowedStatusChanges.put(OrderStatus.ORDERED, Arrays.asList(OrderStatus.PREPARING, OrderStatus.CANCELLED));
		allowedStatusChanges.put(OrderStatus.PREPARING, Arrays.asList(OrderStatus.READY, OrderStatus.BILLED));
		allowedStatusChanges.put(OrderStatus.READY, Arrays.asList(OrderStatus.SERVED, OrderStatus.BILLED));
		allowedStatusChanges.put(OrderStatus.SERVED, Arrays.asList(OrderStatus.BILLED));
	}
	
	public static boolean isChangeAllowedToState(OrderStatus fromState, OrderStatus tostate) {
		
		return allowedStatusChanges.get(fromState).contains(tostate);
	}
	
	@Data
	public static class OrderStatusData {
		
		private final long orderId;
		private final OrderStatus state;
		private final OrderStatus targetState;
	}
}
