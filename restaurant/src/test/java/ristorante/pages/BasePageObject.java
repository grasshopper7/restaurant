package ristorante.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class BasePageObject<T extends BasePageObject<T>> extends LoadableComponent<T> {

	@Autowired
	protected WebDriver driver;

	@Autowired
	protected WebDriverWait waitDriver;

}
