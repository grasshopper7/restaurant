package ristorante.pages;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class TableListPageObject extends BasePageObject<TableListPageObject> {

	@FindBy(id = "refresh")
	private WebElement refreshTablesLink;

	@FindBy(xpath = "//span[@class='tablestatus'][.='VACANT']/preceding-sibling::span[@class='tablenum']")
	private WebElement availableVacantTableLink;

	public TableListPageObject(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
	
	public void switchToTablePage() {
		driver.switchTo().defaultContent();
		waitDriver.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("tables"));
	}

	public String getAvailableVacantTable() {
		
		driver.switchTo().defaultContent();
		waitDriver.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("tables"));
		waitDriver.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//span[@class='tablestatus'][.='VACANT']/preceding-sibling::span[@class='tablenum']")));
		return availableVacantTableLink.getText();
	}

	public void selectTableOrder(String tableNum) {
		
		driver.switchTo().defaultContent();
		waitDriver.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("tables"));
		waitDriver.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//span[@class='tablenum'][.='" + tableNum + "']")))
				.click();
	}

	public String getTableStatus(long tableNum) {

		WebElement status = waitDriver.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//span[@class='tablenum'][.='Table" + tableNum + "']/following-sibling::span")));
		return status.getText();
	}

	@Override
	protected void load() {
	}

	@Override
	protected void isLoaded() throws Error {
		try {
			waitDriver.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadmsg")));
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
