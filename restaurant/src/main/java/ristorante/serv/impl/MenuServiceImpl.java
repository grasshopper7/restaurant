package ristorante.serv.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ristorante.entity.Dish;
import ristorante.entity.Dish.DishType;
import ristorante.repo.DishRepository;
import ristorante.serv.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

	private final DishRepository dishRepo;

	@Autowired
	public MenuServiceImpl(DishRepository dishRepo) {
		this.dishRepo = dishRepo;
	}
	
	@Override
	public Map<String, List<Dish>> displayMenu() {
	
		Map<String, List<Dish>> typeDishes = new HashMap<>();
		List<Dish> dishes = new ArrayList<>();
		
		dishRepo.findAll().forEach(i -> dishes.add(i));

		DishType[] types = Dish.DishType.values();
		for (DishType type : types)
			typeDishes.put(type.toString(), filterByType(dishes, type));
		
		return typeDishes;
	}
	
	private List<Dish> filterByType(List<Dish> dishes, DishType type) {
		return dishes.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
	}
}
