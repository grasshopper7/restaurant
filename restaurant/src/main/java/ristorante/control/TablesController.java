package ristorante.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ristorante.entity.Dish;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;
import ristorante.entity.Tables;
import ristorante.serv.OrderService;
import ristorante.serv.TableService;

@Controller
@RequestMapping("/tables")
public class TablesController {

	private final TableService tableService;
	
	private final OrderService orderService;

	
	@Autowired
	public TablesController(TableService tableService, OrderService orderService) {
		this.tableService = tableService;
		this.orderService = orderService;
	}

	@GetMapping
	public String tables(Model model) {

		model.addAttribute("tables", tableService.getAllTables());
		return "tables";
	}

	@GetMapping("/{table}")
	public String showTableDishes(Model model, @PathVariable("table") Tables table) {

		Order order = new Order();
		if (table.getOrder() != null)
			order = table.getOrder();
		
		showNoTableDishes(model, order);

		model.addAttribute("table", table.getId());
		return "order";
	}

	@GetMapping("/notable/{order}")
	public String showNoTableDishes(Model model, @PathVariable("order") Order order) {

		if (order == null)
			return "redirect:/home";

		if (order.getOrderLines() != null) 
			model.addAllAttributes(orderService.getDishQuantityUIMap(order));
		
		model.addAllAttributes(Dish.getDishesForType());
		model.addAttribute("order", order);
		model.addAttribute("table", "");
		setButtonDisplayOptions(order, model);
		return "order";
	}

	@PostMapping("/{table}")
	public String saveOrder(@RequestParam Map<String, String> params, @PathVariable("table") Tables table,
			RedirectAttributes model) {

		if (table == null)
			return "redirect:/home";

		Order order = table.getOrder();
		if (order != null) {
			OrderStatus status = order.getStatus();
			if (!status.toString().equalsIgnoreCase(params.get("order-status"))
					|| (!order.getModificationTime().toString().equals(params.get("modTime")))
					|| !(status == OrderStatus.ORDERED || status == OrderStatus.PREPARING)) {
				model.addFlashAttribute("operation", "STATEERROR");
				return "redirect:/tables/" + table.getId();
			}
		}

		List<OrderLine> lines = new ArrayList<>();
		params.forEach((k, v) -> {
			if (v.length() > 0) {
				Optional<Dish> optDish = Dish.getDishFromId(k);
				if (optDish.isPresent())
					lines.add(new OrderLine(0, optDish.get(), Integer.parseInt(v)));
			}
		});

		if (lines.isEmpty())
			return "redirect:/tables/" + table.getId();

		if (order == null) 
			orderService.createNewOrder(table, lines);
		else				
			orderService.updateOrder(order, lines);

		if (params.get("order-status").isEmpty())
			model.addFlashAttribute("operation", "CREATED");
		else
			model.addFlashAttribute("operation", "UPDATED");

		return "redirect:/tables/" + table.getId();
	}

	private void setButtonDisplayOptions(Order order, Model model) {
		String style = "margin: 1em;visibility: ";
		model.addAttribute("create", style + "hidden");
		model.addAttribute("modify", style + "hidden");
		model.addAttribute("cancel", style + "hidden");

		if (order.getStatus() == null) {
			model.addAttribute("create", style + "visible");
		} else if (order.getStatus() == OrderStatus.ORDERED || order.getStatus() == OrderStatus.PREPARING) {
			model.addAttribute("modify", style + "visible");
			model.addAttribute("cancel", style + "visible");
		} else {
			model.addAttribute("allowed", "disabled");
		}
	}
}