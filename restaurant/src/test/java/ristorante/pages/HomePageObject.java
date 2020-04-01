package ristorante.pages;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class HomePageObject extends BasePageObject<HomePageObject> {

	@FindBy(id = "kitchen")
	@CacheLookup
	private WebElement kitchenLink;

	@FindBy(id = "server")
	@CacheLookup
	private WebElement serverLink;

	public HomePageObject(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
	
	public void switchToKitchen() {
		
		driver.switchTo().defaultContent();
		String homeHandle = driver.getWindowHandle();
		kitchenLink.click();
		waitDriver.until(ExpectedConditions.numberOfWindowsToBe(2));
		
		Set<String> handles = driver.getWindowHandles();
		handles.remove(homeHandle);
		driver.switchTo().window(handles.iterator().next());
	}

	@Override
	protected void load() {
		driver.get("http://localhost:8080/");
	}

	@Override
	protected void isLoaded() throws Error {
		try {
			waitDriver.until(ExpectedConditions.and(ExpectedConditions.visibilityOf(kitchenLink),
					ExpectedConditions.visibilityOf(serverLink)));
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
