package ristorante.serv;

import java.util.List;
import java.util.Map;

import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;
import ristorante.entity.Tables;

public interface OrderService {

	List<Order> searchOrder(String[] selectedStatuses, String selectedTime);
	
	Map<String, Integer> getDishQuantityUIMap(Order order);
	
	Order createNewOrder(Tables table, List<OrderLine> lines);
	
	Order updateOrder(Order order, List<OrderLine> updatedLines);
	
	Order changeOrderStatus(Order order, OrderStatus current, OrderStatus target);
	
	Order findOrderById(long id);
	
	List<Order> findOrdersByStatus(OrderStatus status);
}