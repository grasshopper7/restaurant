package ristorante.steps;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ristorante.pages.OrderUI;
import ristorante.pages.TableListPageObject;

public class TableStepDefs {

	@Autowired
	private TableListPageObject tablePO;

	@Autowired
	private OrderUI orderUI;

	@When("User selects vacant table")
	public void userSelectsTable() {
		String tableNo = tablePO.getAvailableVacantTable();
		tablePO.selectTableOrder(tableNo);

		orderUI.setTableNo(tableNo.substring(5));
	}

	@Then("Order status in table list should be {word}")
	public void orderStatusInTableListShouldBeUpdated(String status) {
		tablePO.switchToTablePage();
		assertThat(tablePO.getTableStatus(orderUI.getTableNo())).isEqualToIgnoringCase(status.toString().toUpperCase());
	}

	@Then("Table should be vacant in table list")
	public void tableShouldBeVacantInTableList() {
		tablePO.switchToTablePage();
		assertThat(tablePO.getTableStatus(orderUI.getTableNo())).isEqualToIgnoringCase("VACANT");
	}
}
