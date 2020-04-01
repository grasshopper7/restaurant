package ristorante.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ristorante.serv.MenuService;

@Controller
@RequestMapping("/menu")
public class MenuController {

	private final MenuService menuService;

	@Autowired
	public MenuController(MenuService menuService) {
		this.menuService = menuService;
	}

	@GetMapping
	public String showMenu(Model model) {
		
		model.addAllAttributes(menuService.displayMenu());		
		return "menu";
	}
}
