package ristorante.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findOrdersByStatusIsIn(Set<OrderStatus> status);
	
	@EntityGraph(value = "getOrderLines", type = EntityGraphType.LOAD)
	List<Order> findOrdersByStatus(OrderStatus status);
		
	List<Order> findOrdersByStatusIsInAndModificationTimeGreaterThanEqual(Set<OrderStatus> status, LocalDateTime ldt);
}
