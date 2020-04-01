package ristorante.pages;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;
import ristorante.entity.Tables;

@Data
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class OrderUI {

	private Order initialOrderData;
	
	public OrderUI() {
		initialOrderData = new Order(0L);
		initialOrderData.setOrderLines(new ArrayList<>());
	}
	
	public void setOrderId(long orderid) {
		Order newOrder = new Order(orderid);
		if(initialOrderData.getOrderLines().size() > 0)
			newOrder.setOrderLines(initialOrderData.getOrderLines());
		if(initialOrderData.getStatus() != null)
			newOrder.setStatus(initialOrderData.getStatus());
		if(initialOrderData.getTable() != null)
			newOrder.setTable(initialOrderData.getTable());
		initialOrderData = newOrder;
	}
	
	public long getOrderId() {
		return initialOrderData.getId();
	}
	
	public List<OrderLine> getOrderLines() {
		return initialOrderData.getOrderLines();
	}
	
	public void setOrderLines(List<OrderLine> lines) {
		initialOrderData.setOrderLines(lines);
	}
	
	public long getTableNo() {
		return initialOrderData.getTable().getId();
	}
	
	public void setTableNo(String tableNo) {
		initialOrderData.setTable(new Tables(Long.parseLong(tableNo)));		
	}
	
	public OrderStatus getStatus() {
		return initialOrderData.getStatus();
	}
	
	public void setStatus(String status) {
		initialOrderData.setStatus(OrderStatus.valueOf(status));		
	}
}
