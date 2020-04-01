package ristorante;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import io.github.bonigarcia.wdm.WebDriverManager;

@Configuration
@ComponentScan({ "ristorante.pages" })
public class Config {

	@Bean(destroyMethod = "quit")
	@Scope(SCOPE_CUCUMBER_GLUE)
	public WebDriver getDriver() {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("http://localhost:8080/");
		return driver;
	}
	
	@Bean
	@Scope(SCOPE_CUCUMBER_GLUE)
	public WebDriverWait getWaitDriver() {
		return new WebDriverWait(getDriver(), 10, 250);
	}
}
