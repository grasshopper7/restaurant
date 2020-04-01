package ristorante.pages;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ristorante.entity.Order.OrderStatus;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class SearchOrdersPageObject extends BasePageObject<SearchOrdersPageObject> {
	
	@FindBy(id = "search")
	private WebElement searchButton;
	
	@FindBy(name = "checkAllState")
	private WebElement statusAllCheck;
	
	@FindBy(id = "loadmsg")
	private WebElement loadmsg;
	
	public SearchOrdersPageObject (WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
	
	public void switchToSearchPage() {
		driver.switchTo().defaultContent();
		waitDriver.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("searchOrder"));
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

	public void searchOrders(OrderStatus[] statuses, String time) {

		if (!statusAllCheck.isSelected())
			statusAllCheck.click();

		waitDriver.until(ExpectedConditions.elementToBeSelected(statusAllCheck));
		statusAllCheck.click();

		Arrays.stream(statuses).forEach(stat -> {
			WebElement statusCheck = driver
					.findElement(By.cssSelector("input[name='checkedState'][value='" + stat.toString() + "']"));
			statusCheck.click();
			waitDriver.until(ExpectedConditions.elementToBeSelected(statusCheck));
		});

		WebElement timeRadio = driver.findElement(By.cssSelector("input[name='selectedTime'][value='" + time + "']"));
		timeRadio.click();
		waitDriver.until(ExpectedConditions.elementToBeSelected(timeRadio));

		searchButton.click();

		waitDriver.until(ExpectedConditions.invisibilityOf(loadmsg));
		waitDriver.until(ExpectedConditions.or(
				ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("ul[id='results'] > li > div > a")),
				ExpectedConditions.visibilityOfElementLocated(By.id("noorders"))));
	}

	public boolean orderPresentInSearch(long tableNo, long orderNo, OrderStatus status) {

		String line = "";
		if (status == OrderStatus.BILLED || status == OrderStatus.CANCELLED)
			line = String.format("Order No - %s - Status - %s", orderNo, status.toString());
		else
			line = String.format("Order No - %s - Status - %s - Table No - %s", orderNo, status.toString(), tableNo);

		List<WebElement> reqdOrder = driver.findElements(By.xpath("//ul[@id='results']/li/div/a[.='" + line + "']"));
		if (reqdOrder.isEmpty())
			return false;

		return true;
	}
}
