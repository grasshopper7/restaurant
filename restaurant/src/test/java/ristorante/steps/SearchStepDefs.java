package ristorante.steps;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import ristorante.entity.Order.OrderStatus;
import ristorante.pages.OrderUI;
import ristorante.pages.SearchOrdersPageObject;

public class SearchStepDefs {

	@Autowired
	private SearchOrdersPageObject searchPO;

	@Autowired
	private OrderUI orderUI;

	@Then("Order should be available in {orderStatus} status in search")
	public void orderShouldBeAvailableInSearch(OrderStatus state) {

		searchPO.switchToSearchPage();

		OrderStatus[] statuses = { state };
		searchPO.searchOrders(statuses, "45");

		boolean present = searchPO.orderPresentInSearch(orderUI.getTableNo(), orderUI.getOrderId(), state);
		// System.out.println("search order - "+present);
		assertThat(present).isEqualTo(true);
	}
}
