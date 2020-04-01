package ristorante.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
public class Dish {

	@Id
	private final String id;

	private final String name;

	private final DishType type;

	private final float price;

	public static enum DishType {
		APPETIZER, PASTA, PIZZA, DESSERT, BEVERAGE
	}

	private static List<Dish> dishes = new ArrayList<>();

	static {
		dishes.add(new Dish("FRIGNOAPP", "Fried Gnocchi", DishType.APPETIZER, 3.75f));
		dishes.add(new Dish("SPIMEAAPP", "Spicy Meatballs", DishType.APPETIZER, 5.25f));
		dishes.add(new Dish("CHIMILAPP", "Chicken Milanese", DishType.APPETIZER, 4.75f));
		dishes.add(new Dish("SPAOLIPAS", "Spaghetti Aglio E Olio", DishType.PASTA, 7.25f));
		dishes.add(new Dish("SAUPAPPAS", "Sausage Pappardelle", DishType.PASTA, 9.75f));
		dishes.add(new Dish("TAGBOLPAS", "Tagliatelle Bolognese", DishType.PASTA, 9.75f));
		dishes.add(new Dish("MARGHEPIZ", "Margherita", DishType.PIZZA, 7.75f));
		dishes.add(new Dish("CHIDIAPIZ", "Chicken Alla Diavola", DishType.PIZZA, 9.75f));
		dishes.add(new Dish("NEOPOLPIZ", "Neapolitan", DishType.PIZZA, 8.25f));
		dishes.add(new Dish("TRAMISDES", "Tiramisu", DishType.DESSERT, 4.75f));
		dishes.add(new Dish("NOCMOUDES", "Nocha Mousse", DishType.DESSERT, 4.25f));
		dishes.add(new Dish("CANNOLDES", "Cannoli", DishType.DESSERT, 4.25f));
		dishes.add(new Dish("ESPRESBEV", "Espresso", DishType.BEVERAGE, 2.75f));
		dishes.add(new Dish("MACCHIBEV", "Macchiato", DishType.BEVERAGE, 2.75f));
		dishes.add(new Dish("CAPPUCBEV", "Cappuccino", DishType.BEVERAGE, 2.75f));
	}

	public static List<Dish> getAllDishes() {
		return new ArrayList<>(dishes);
	}

	public static Dish getDishFromName(String name) {
		return dishes.stream().filter(d -> d.name.equalsIgnoreCase(name)).findAny()
				.orElseThrow(() -> new RuntimeException("Dish does not exist for name - " + name + "."));
	}

	public static Optional<Dish> getDishFromId(String id) {
		return dishes.stream().filter(d -> d.id.equals(id)).findAny();
	}

	public static String getDishIdFromName(String name) {
		return getDishFromName(name).getId();
	}

	public static Map<String, List<Dish>> getDishesForType() {
		List<Dish> dishes = Dish.getAllDishes();
		Map<String, List<Dish>> typeDishesMap = new HashMap<>();

		DishType[] types = Dish.DishType.values();
		for (DishType type : types)
			typeDishesMap.put(type.name(),
					dishes.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList()));
		return typeDishesMap;
	}

}
