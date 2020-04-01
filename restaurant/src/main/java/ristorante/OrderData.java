package ristorante;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import ristorante.entity.Order;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;
import ristorante.entity.Tables;

public class OrderData {

	public static List<Order> parseOrderJSONFile(String profile, String jsonDataFileName) {

		try {
			BufferedReader reader = null;			
			Path path = Paths.get(jsonDataFileName);
			
			if(path.isAbsolute())
				reader = Files.newBufferedReader(path);
			else {
				ClassLoader cl = OrderData.class.getClassLoader();
				InputStream stream = cl
						.getResourceAsStream(OrderData.class.getPackage().getName() + "/" + jsonDataFileName);
				if(stream != null)
					reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			}			
			
			if(reader == null)
				throw new RuntimeException("JSON data file path format is not correct. For default and test profile "
						+ "the path is relative to restorante package. For other profiles it is an absolute path.");

			ObjectMapper objectMapper = new ObjectMapper();
			JsonOrderData testOrder = objectMapper.readValue(reader, JsonOrderData.class);

			return JsonOrderData.createEntityOrders(testOrder);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Data
	private static class JsonOrderData {

		private InputDishData singleDefaultDish;
		private List<InputDishData> multipleDefaultDishes;
		private List<Integer> orderedStatusSingleDishTables;
		private List<Integer> preparingStatusSingleDishTables;
		private List<Integer> readyStatusSingleDishTables;
		private List<Integer> servedStatusSingleDishTables;
		private List<Integer> orderedStatusMutipleDishesTables;
		private List<Integer> preparingStatusMutipleDishesTables;
		private List<Integer> readyStatusMutipleDishesTables;
		private List<Integer> servedStatusMutipleDishesTables;
		private int billedStatusSingleDishCount;
		private int billedStatusMultipleDishesCount;
		private int cancelledStatusSingleDishCount;
		private int cancelledStatusMultipleDishesCount;
		private List<InputOrderData> individualTables;
		private List<InputSearchOrderData> searchOrders;
		

		public static List<ristorante.entity.Order> createEntityOrders(JsonOrderData jsonOrder) {

			List<ristorante.entity.Order> orders = new ArrayList<>();
			List<InputDishData> singleDefaultDishList = Collections.singletonList(jsonOrder.getSingleDefaultDish());

			if (jsonOrder.getSingleDefaultDish() != null) {
				orders.addAll(createEntityOrder(OrderStatus.ORDERED, jsonOrder.getOrderedStatusSingleDishTables(),
						singleDefaultDishList));
				orders.addAll(createEntityOrder(OrderStatus.PREPARING, jsonOrder.getPreparingStatusSingleDishTables(),
						singleDefaultDishList));
				orders.addAll(createEntityOrder(OrderStatus.READY, jsonOrder.getReadyStatusSingleDishTables(),
						singleDefaultDishList));
				orders.addAll(createEntityOrder(OrderStatus.SERVED, jsonOrder.getServedStatusSingleDishTables(),
						singleDefaultDishList));
				orders.addAll(createEntityOrderWOTable(OrderStatus.BILLED, jsonOrder.getBilledStatusSingleDishCount(),
						singleDefaultDishList));
				orders.addAll(createEntityOrderWOTable(OrderStatus.CANCELLED,
						jsonOrder.getCancelledStatusSingleDishCount(), singleDefaultDishList));
			}

			if (jsonOrder.getMultipleDefaultDishes() != null) {
				orders.addAll(createEntityOrder(OrderStatus.ORDERED, jsonOrder.getOrderedStatusMutipleDishesTables(),
						jsonOrder.getMultipleDefaultDishes()));
				orders.addAll(createEntityOrder(OrderStatus.PREPARING,
						jsonOrder.getPreparingStatusMutipleDishesTables(), jsonOrder.getMultipleDefaultDishes()));
				orders.addAll(createEntityOrder(OrderStatus.READY, jsonOrder.getReadyStatusMutipleDishesTables(),
						jsonOrder.getMultipleDefaultDishes()));
				orders.addAll(createEntityOrder(OrderStatus.SERVED, jsonOrder.getServedStatusMutipleDishesTables(),
						jsonOrder.getMultipleDefaultDishes()));
				orders.addAll(createEntityOrderWOTable(OrderStatus.BILLED,
						jsonOrder.getBilledStatusMultipleDishesCount(), jsonOrder.getMultipleDefaultDishes()));
				orders.addAll(createEntityOrderWOTable(OrderStatus.CANCELLED,
						jsonOrder.getCancelledStatusMultipleDishesCount(), jsonOrder.getMultipleDefaultDishes()));
			}

			if (jsonOrder.getIndividualTables() != null) {
				jsonOrder.getIndividualTables().forEach(o -> {
					List<OrderLine> lines = new ArrayList<>();

					o.getDishes().forEach(d -> {
						ristorante.entity.Dish dish = ristorante.entity.Dish.getDishFromName(d.getName());
						lines.add(new OrderLine(0, dish, d.getQty()));
					});
					orders.add(createOrder(o.getStatus(), o.getTable(), LocalDateTime.now(), lines));
				});
			}
					
			if(jsonOrder.getSearchOrders() != null) {
				jsonOrder.getSearchOrders().forEach(o -> {
					LocalDateTime modTime = LocalDateTime.now().minusMinutes(o.getDelaymins());	
					orders.add(createOrder(o.getStatus(), o.getTable(), modTime, InputDishData.createEntityOrderLines(singleDefaultDishList)));
				});
			}

			return orders;
		}

		public static List<ristorante.entity.Order> createEntityOrder(OrderStatus status, List<Integer> tables,
				List<InputDishData> dishData) {

			List<ristorante.entity.Order> orders = new ArrayList<>();
			if (tables != null) {
				tables.forEach(t -> {
					orders.add(createOrder(status, new Tables(t), LocalDateTime.now(), InputDishData.createEntityOrderLines(dishData)));
				});
			}
			return orders;
		}

		public static List<ristorante.entity.Order> createEntityOrderWOTable(OrderStatus status, int count,
				List<InputDishData> dishData) {

			List<ristorante.entity.Order> orders = new ArrayList<>();
			int i = 1;

			while (i <= count) {
				orders.add(createOrder(status, null, LocalDateTime.now(), InputDishData.createEntityOrderLines(dishData)));
				i++;
			}
			return orders;
		}
		
		private static ristorante.entity.Order createOrder(OrderStatus status, Tables table, LocalDateTime dateTime, List<OrderLine> dishes) {
			
			ristorante.entity.Order order = new ristorante.entity.Order(0);
			order.setStatus(status);
			if(table!=null)
				order.setTable(table);
			order.setModificationTime(dateTime);
			order.setOrderLines(dishes);
			return order;
		}
	}

	@Data
	private static class InputOrderData {

		private OrderStatus status;
		private Tables table;
		private List<InputDishData> dishes;
	}

	@Data
	private static class InputDishData {

		private String name;
		private int qty;

		public static ristorante.entity.OrderLine createEntityOrderLine(InputDishData dishData) {

			ristorante.entity.Dish dish = ristorante.entity.Dish.getDishFromName(dishData.getName());
			return new OrderLine(0, dish, dishData.getQty());
		}

		public static List<ristorante.entity.OrderLine> createEntityOrderLines(List<InputDishData> dishData) {

			List<OrderLine> lines = new ArrayList<>();
			dishData.forEach(d -> {
				lines.add(createEntityOrderLine(d));
			});
			return lines;
		}
	}
	
	@Data
	private static class InputSearchOrderData {

		private OrderStatus status;
		private Tables table;
		private int delaymins;
	}
}
