package ristorante.serv.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ristorante.display.OrderUIInfo;
import ristorante.entity.Dish;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;
import ristorante.entity.Tables;
import ristorante.repo.OrderRepository;
import ristorante.serv.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepo;

	protected final SimpMessagingTemplate messaging;

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepo, SimpMessagingTemplate messaging) {
		this.orderRepo = orderRepo;
		this.messaging = messaging;
	}

	@Override
	public List<Order> searchOrder(String[] selectedStatuses, String selectedTime) {

		Set<OrderStatus> status = Arrays.stream(selectedStatuses).map(d -> OrderStatus.valueOf(d))
				.collect(Collectors.toSet());
		LocalDateTime time = LocalDateTime.now().minusMinutes(Long.parseLong(selectedTime));
		return orderRepo.findOrdersByStatusIsInAndModificationTimeGreaterThanEqual(status, time);
	}

	@Override
	public Map<String, Integer> getDishQuantityUIMap(Order order) {

		return order.getOrderLines().stream()
				.collect(Collectors.toMap(ol -> ol.getDish().getId() + "QTY", ol -> ol.getQty()));
	}

	@Override
	public Order createNewOrder(Tables table, List<OrderLine> lines) {

		Order order = new Order();
		order.setTable(table);
		order.setOrderLines(lines);
		order.setStatus(OrderStatus.ORDERED);
		order.setModificationTime(LocalDateTime.now());
		order = orderRepo.save(order);

		messaging.convertAndSend("/topic/order/status", new OrderUIInfo(order.getTable().getId(), "",
				/* order.getModificationTime().toString(), */ AbstractDisplayService.processOrderUI(order)));
		return order;
	}

	@Override
	public Order updateOrder(Order order, List<OrderLine> updatedLines) {

		List<OrderLine> existLines = order.getOrderLines();

		// Delete removed dishes from existing order
		List<Dish> updatedDishes = updatedLines.stream().map(OrderLine::getDish).collect(Collectors.toList());
		existLines.removeIf(ol -> !updatedDishes.contains(ol.getDish()));

		updatedLines.forEach(ol -> {
			Optional<OrderLine> line = existLines.stream().filter(l -> l.getDish().equals(ol.getDish())).findAny();
			if (!line.isPresent()) {
				// Add new dishes to existing order
				existLines.add(ol);
			} else if (line.isPresent() && line.get().getQty() != ol.getQty()) {
				// Update quantity of existing dish
				line.get().setQty(ol.getQty());
			}
		});
		order.setModificationTime(LocalDateTime.now());
		order = orderRepo.save(order);

		messaging.convertAndSend("/topic/order/update",
				new OrderUIInfo(order.getTable().getId(), order.getStatus().toString(),
						/* order.getModificationTime().toString(), */ AbstractDisplayService.processOrderUI(order)));
		return order;
	}

	@Override
	public Order changeOrderStatus(Order order, OrderStatus current, OrderStatus target) {

		if (order.getStatus() == current && OrderStatusChanges.isChangeAllowedToState(current, target)) {
			long tableid = order.getTable().getId();
			if (target == OrderStatus.BILLED || target == OrderStatus.CANCELLED)
				order.setTable(null);

			order.setStatus(target);
			Order savedOrder = orderRepo.save(order);

			// table num is required in case of billed and cancelled orders
			messaging.convertAndSend("/topic/order/status", new OrderUIInfo(tableid, current.toString(),
					/* order.getModificationTime().toString(), */ AbstractDisplayService.processOrderUI(savedOrder)));

			return savedOrder;
		} else {
			throw new RuntimeException("State change from " + current + " to " + target + " is not allowed.");
		}
	}

	@Override
	public Order findOrderById(long id) {

		Optional<Order> order = orderRepo.findById(id);
		return order.orElseThrow(() -> new RuntimeException("Order does not exist with id " + id));
	}

	@Override
	public List<Order> findOrdersByStatus(OrderStatus status) {

		return orderRepo.findOrdersByStatus(status);
	}
}
