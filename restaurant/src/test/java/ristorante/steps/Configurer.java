package ristorante.steps;

import java.lang.reflect.Type;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cucumber.java.DataTableType;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import io.cucumber.java.DefaultParameterTransformer;
import io.cucumber.java.ParameterType;
import ristorante.entity.Dish;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;

public class Configurer {

	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	@ParameterType(".*?")
	public OrderStatus orderStatus(String status) {
		return OrderStatus.valueOf(status.toUpperCase());
	}

	@DataTableType
	public OrderLine getOrderLine(Map<String, String> entry) {
		Dish dish = Dish.getDishFromName(entry.get("dish"));
		return new OrderLine(0, dish, Integer.parseInt((entry.get("qty"))));
	}

	@DefaultParameterTransformer
	@DefaultDataTableEntryTransformer
	@DefaultDataTableCellTransformer
	public Object defaultTransformer(Object fromValue, Type toValueType) {
		JavaType javaType = objectMapper.constructType(toValueType);
		return objectMapper.convertValue(fromValue, javaType);
	}
}
