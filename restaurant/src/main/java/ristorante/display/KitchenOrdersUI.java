package ristorante.display;

import java.util.List;

import lombok.Data;
import ristorante.entity.Order;

@Data
public class KitchenOrdersUI {
		
	private final List<Order> orderedOrders;

	private final List<Order> preparingOrders;
	
	private final List<Order> readyOrders;
	
	private final List<Order> cancelledOrders;
}
