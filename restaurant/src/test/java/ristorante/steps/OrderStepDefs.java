package ristorante.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ristorante.entity.Order;
import ristorante.entity.OrderLine;
import ristorante.pages.OrderPageObject;
import ristorante.pages.OrderUI;

public class OrderStepDefs {

	@Autowired
	private OrderPageObject orderPO;

	@Autowired
	private OrderUI orderUI;

	@When("User creates new order by selecting dishes")
	public void userCreatesNewOrderBySelectingDishes(List<OrderLine> lines) {
		orderPO.switchToOrderPage();
		for (int i = 0; i < lines.size(); i++) {
			// Complex ways of adding quantities for first 2 dishes
			orderPO.setDishName(lines.get(i).getDish().getName());
			int qty = lines.get(i).getQty();
			if (i == 0) {
				orderPO.addQuantityToDish(3);
				orderPO.subtractQuantityFromDish(3);
			} else if (i == 1) {
				orderPO.addQuantityToDish(5);
				orderPO.subtractQuantityFromDish(3);
				orderPO.clearDishQuantity();
			}
			orderPO.addQuantityToDish(qty);
		}
		long orderid = orderPO.saveOrder();

		if (orderid != 0)
			orderUI.setOrderId(orderid);

		orderUI.setStatus("ORDERED");
		orderUI.setOrderLines(lines);
	}

	@When("User creates new order without selecting dish")
	public void userCreatesNewOrderWithoutSelectingDish() {
		orderPO.switchToOrderPage();
		orderPO.saveOrder();
	}

	@Then("Created/Updated/Promoted order details should be displayed")
	public void orderDetailsShouldBeUpdatedDisplayed() {

		Order expectedOrder = orderUI.getInitialOrderData();
		Order actualOrder = orderPO.getOrderDetails();
		// System.out.println("expectedOrder - " + expectedOrder);
		// System.out.println("actualOrder - " + actualOrder);

		assertThat(actualOrder).isEqualToIgnoringGivenFields(expectedOrder, "id", "orderLines").extracting("orderLines")
				.asList().usingElementComparatorIgnoringFields("id")
				.containsOnlyElementsOf(expectedOrder.getOrderLines());
	}

	@Then("Order (details )should not be created/updated")
	public void orderShouldNotBeCreated() {

		orderDetailsShouldBeUpdatedDisplayed();
	}

	@Then("Alert is displayed with order creation message")
	public void alertIsDisplayedWithOrderCreationMessage() {
		assertThat(orderPO.getAlertMessage()).isEqualTo("Order is created.");
	}

	@Then("Alert is displayed with order creation warning")
	public void alertIsDisplayedWithIllegalOperationWarning() {
		assertThat(orderPO.getAlertMessage()).isEqualTo("Order needs atleast one dish.");
	}
}
