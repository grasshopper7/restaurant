package ristorante.serv;

import java.util.List;
import java.util.Map;

import ristorante.entity.Dish;

public interface MenuService {

	Map<String, List<Dish>> displayMenu();
}
