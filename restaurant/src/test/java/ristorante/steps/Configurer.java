package ristorante.steps;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterByTypeTransformer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableCellByTypeTransformer;
import io.cucumber.datatable.TableCellTransformer;
import io.cucumber.datatable.TableEntryByTypeTransformer;
import io.cucumber.datatable.TableEntryTransformer;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import ristorante.entity.Dish;
import ristorante.entity.Order.OrderStatus;
import ristorante.entity.OrderLine;

public class Configurer implements TypeRegistryConfigurer {

	@Override
	public void configureTypeRegistry(TypeRegistry registry) {

		JacksonTableTransformer jacksonTableTransformer = new JacksonTableTransformer();
		registry.setDefaultParameterTransformer(jacksonTableTransformer);
		registry.setDefaultDataTableEntryTransformer(jacksonTableTransformer);
		registry.setDefaultDataTableCellTransformer(jacksonTableTransformer);
		
		registry.defineParameterType(new ParameterType<>("status", ".*?", OrderStatus.class, 
				(String s) -> OrderStatus.valueOf(s.toUpperCase())));

		registry.defineDataTableType(new DataTableType(OrderLine.class, new TableEntryTransformer<OrderLine>() {
			@Override
			public OrderLine transform(Map<String, String> entry) {
				return new OrderLine(0, Dish.getDishFromName(entry.get("dish")), Integer.parseInt(entry.get("qty")));
			}
		}));
		
		registry.defineDataTableType(new DataTableType(Dish.class, new TableCellTransformer<Dish>() {
			@Override
			public Dish transform(String cell) throws Throwable {
				return Dish.getDishFromName(cell);
			}
		}));
	}

	@Override
	public Locale locale() {
		return Locale.ENGLISH;
	}

	private static final class JacksonTableTransformer
			implements ParameterByTypeTransformer, TableEntryByTypeTransformer, TableCellByTypeTransformer {

		private final ObjectMapper objectMapper = new ObjectMapper();

		@Override
		public Object transform(String s, Type type) {
			return objectMapper.convertValue(s, objectMapper.constructType(type));
		}

		@Override
		public <T> T transform(Map<String, String> entry, Class<T> type, TableCellByTypeTransformer cellTransformer) {
			return objectMapper.convertValue(entry, type);
		}

		@Override
		public <T> T transform(String value, Class<T> cellType) {
			return objectMapper.convertValue(value, cellType);
		}
	}
}
