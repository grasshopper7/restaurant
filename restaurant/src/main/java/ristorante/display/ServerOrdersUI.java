package ristorante.display;

import java.util.List;

import lombok.Data;
import ristorante.entity.Order;

@Data
public class ServerOrdersUI {
		
	private final List<Order> readyOrders;

	private final List<Order> servedOrders;
	
	private final List<Order> billedOrders;
}
