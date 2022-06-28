package ristorante.pages;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ristorante.entity.Dish;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;
import ristorante.entity.Tables;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class OrderPageObject extends BasePageObject<OrderPageObject> {

	@FindBy(id = "createOrder")
	private WebElement createOrder;

	@FindBy(id = "status")
	private WebElement orderStatus;

	@FindBy(id = "ordernum")
	private WebElement orderNumber;

	@FindBy(id = "tablenum")
	private WebElement tableNumber;

	@FindBy(xpath = "//input[@class='dishqty'][@value!='']/..")
	private List<WebElement> selectedDishes;

	private String alertMessage;
	
	private String dishName;
	
	private String dishContainer;
	

	public OrderPageObject(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}

	public String getAlertMessage() {
		return alertMessage;
	}
	
	public void switchToOrderPage() {
		driver.switchTo().defaultContent();
		waitDriver.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("menuDetails"));
	}
	
	public void setDishName(String dish) {
		this.dishName = dish;
		this.dishContainer = "//span[@class='dishName'][.='" + this.dishName + "']/..";
	}

	private void handleAlert(boolean ... acceptOrDismiss) {
		
		Alert alert = waitDriver.until(ExpectedConditions.alertIsPresent());
		alertMessage = alert.getText();
		// Just for displaying alert message.
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(acceptOrDismiss.length==1 && acceptOrDismiss[0]==false)
			alert.dismiss();
		else
			alert.accept();
	}
	
	public long saveOrder() {

		waitDriver.until(ExpectedConditions.visibilityOf(createOrder)).click();
		handleAlert();
		waitDriver.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadmsg")));		
		return orderNumberDisplay() ? Long.parseLong(orderNumber.getText()) : 0L;
	}

	public Order getOrderDetails() {

		Order order = orderNumberDisplay() ? new Order(Integer.parseUnsignedInt(orderNumber.getText())) :
			new Order(0);
		//Order order = new Order(Integer.parseUnsignedInt(orderNumber.getText()));
		if(orderStatusDisplay())
			order.setStatus(OrderStatus.valueOf(orderStatus.getText().toUpperCase()));
		 
		order.setTable(tableNumber.getText().equals("") ? new Tables() : new Tables(tableNumber.getText()));

		List<OrderLine> lines = new ArrayList<>();

		selectedDishes.forEach(d -> {
			String dishName = d.findElement(By.xpath("./span[@class='dishName']")).getText();
			int dishQty = Integer
					.parseUnsignedInt(d.findElement(By.xpath("./input[@class='dishqty']")).getAttribute("value"));
			lines.add(new OrderLine(0, Dish.getDishFromName(dishName), dishQty));
		});
		order.setOrderLines(lines);
		return order;
	}

	private boolean orderStatusDisplay() {
		try {
			orderStatus.isDisplayed();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean orderNumberDisplay() {
		try {
			orderNumber.isDisplayed();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void addQuantityToDish(int qty) {
		WebElement add = waitDriver.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(dishContainer + "/input[@value='+']")));
		modifyCount(add, qty);
	}

	public void subtractQuantityFromDish(int qty) {
		WebElement sub = waitDriver.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(dishContainer + "/input[@value='-']")));
		modifyCount(sub, qty);
	}

	public void clearDishQuantity() {
		WebElement clear = waitDriver.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath(dishContainer + "/input[@value='X']")));
		clear.click();
		// Slow things down for display
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}			
	}
	
	private void modifyCount(WebElement button, int qty) {
		int count = 0;
		while (count < qty) {
			button.click();
			// Slow things down for display
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
		}
	}

	@Override
	protected void load() {
	}

	@Override
	protected void isLoaded() throws Error {
		try {
			waitDriver.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadmsg")));
		} catch (Exception e) {
			throw new Error();
		}
	}	
}
