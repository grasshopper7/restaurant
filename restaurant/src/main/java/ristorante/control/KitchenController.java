package ristorante.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ristorante.serv.KitchenService;

@Controller
public class KitchenController {
		
	private final KitchenService kitchenService;
	
	
	@Autowired
	public KitchenController(KitchenService kitchenService) {
		this.kitchenService = kitchenService;
	}

	@GetMapping("/kitchen")
	public String kitchen() {
		return "kitchen";
	}
	
	@MessageMapping("/kitchen/initial")
	public void initial() {

		kitchenService.initialKitchenDisplay();
	}

	@MessageMapping("/orders/readyrefresh")
	public void readyorders() {
		kitchenService.updateReadyOrders();
	}
	
	@MessageMapping("/orders/cancelledrefresh")
	public void cancelledorders() {
		kitchenService.updateCancelledOrders();
	}
}
