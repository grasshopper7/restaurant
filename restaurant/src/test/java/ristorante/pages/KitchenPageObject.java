package ristorante.pages;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ristorante.entity.Dish;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;
import ristorante.entity.Tables;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class KitchenPageObject extends BasePageObject<KitchenPageObject> {

	@FindBy(id = "loadmsg")
	private WebElement loadmsg;
	
	@Autowired
	private HomePageObject homePO;
	
	
	public KitchenPageObject(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
	
	public void switchToKitchenPage() {
		homePO.switchToKitchen();
	}

	public Order getOrderDetails(long orderid, String status) {

		WebElement orderDetails = waitDriver.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@id='" + status + "']/div[@data-orderid='" + orderid + "']")));
		Order order = new Order(orderid);

		order.setStatus(OrderStatus.valueOf(status.toUpperCase()));

		List<WebElement> tableDetails = orderDetails.findElements(By.xpath(".//span[@id='tableid']"));
		if(tableDetails.size() == 1)
			order.setTable(new Tables(tableDetails.get(0).getText()));
		else
			order.setTable(new Tables(0));

		List<WebElement> dishes = orderDetails.findElements(By.xpath(".//div[@class='dishes']/div"));
		List<OrderLine> lines = new ArrayList<>();

		dishes.forEach(d -> {
			String dishName = d.findElement(By.xpath("./span[@class='dishname']")).getText();
			int dishQty = Integer.parseUnsignedInt(d.findElement(By.xpath("./span[@class='dishqty']")).getText());
			lines.add(new OrderLine(0, Dish.getDishFromName(dishName), dishQty));
		});

		order.setOrderLines(lines);
		return order;
	}

	@Override
	protected void load() {
	}

	@Override
	protected void isLoaded() throws Error {
		try {
			waitDriver.until(ExpectedConditions.invisibilityOf(loadmsg));
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
