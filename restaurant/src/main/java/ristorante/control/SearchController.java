package ristorante.control;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ristorante.display.OrderSearchDisplay;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.serv.OrderService;

@Controller
@RequestMapping("/search")
public class SearchController {

	private final OrderService orderService;

	@Autowired
	public SearchController(OrderService searchService) {
		this.orderService = searchService;
	}

	@GetMapping
	public String initialSearch(Model model) {

		search(new OrderSearchDisplay(new String[] { OrderStatus.BILLED.name(), OrderStatus.CANCELLED.name() },
				new String[] { "15" }),model);		
		return "ordersearch";
	}

	@PostMapping
	public String search(OrderSearchDisplay display, Model model) {

		List<Order> orders = orderService.searchOrder(display.getCheckedState(), display.getSelectedTime()[0]);
				
		model.addAttribute("orders", orders);
		setSearchModel(model, display.getCheckedState(), display.getSelectedTime(), display.getCheckAllState());
		return "ordersearch";
	}

	private void setSearchModel(Model model, String[] checkedState, String[] selectedTime, String checkAllState) {
		model.addAttribute("states", OrderStatus.values());
		model.addAttribute("times", Arrays.asList("5", "10", "15", "30", "45", "60"));
		model.addAttribute("all", "ALL");

		model.addAttribute("display", new OrderSearchDisplay(checkedState, selectedTime, checkAllState));
	}
}
