package ristorante.steps;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.pages.KitchenPageObject;
import ristorante.pages.OrderUI;

public class KitchenStepDefs {

	@Autowired
	private KitchenPageObject kitchenPO;

	@Autowired
	private OrderUI orderUI;

	@Then("Order should be available in {orderStatus} status in kitchen")
	public void orderShouldBeAvailableInStatusInKitchenPage(OrderStatus status) {

		kitchenPO.switchToKitchenPage();
		Order expectedOrder = orderUI.getInitialOrderData();
		// System.out.println("kitchen expectedOrder - "+expectedOrder);
		Order actualOrder = kitchenPO.getOrderDetails(orderUI.getOrderId(), status.toString().toLowerCase());
		// System.out.println("kitchen actualOrder - "+actualOrder);
		assertThat(actualOrder).isEqualToIgnoringGivenFields(expectedOrder, "id", "orderLines").extracting("orderLines")
				.asList().usingElementComparatorIgnoringFields("id")
				.containsOnlyElementsOf(expectedOrder.getOrderLines());
	}
}
