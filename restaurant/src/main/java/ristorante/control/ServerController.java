package ristorante.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ristorante.serv.ServerService;

@Controller
@RequestMapping("/server")
public class ServerController {

	private final ServerService serverService;

	@Autowired
	public ServerController(ServerService serverService) {
		this.serverService = serverService;
	}

	@GetMapping
	public String server() {
		return "server";
	}

	@MessageMapping("/server/initial")
	public void initial() {
		serverService.initialServerDisplay();
	}
	
	@MessageMapping("/orders/billedrefresh")
	public void billedorders() {
		serverService.updateBilledOrders();
	}

}