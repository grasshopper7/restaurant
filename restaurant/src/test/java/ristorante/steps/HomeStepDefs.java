package ristorante.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.java.en.Given;
import ristorante.Config;
import ristorante.pages.HomePageObject;

//@DirtiesContext
@SpringBootTest (classes = { Config.class })
public class HomeStepDefs {

	@Autowired
	private HomePageObject homePO;
			
	@Given("User navigates to home page")
	public void userNavigatesToHomePage() {		
		
		homePO.get();
	}
	
}
