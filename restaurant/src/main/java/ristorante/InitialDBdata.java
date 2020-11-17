package ristorante;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ristorante.entity.Dish;
import ristorante.entity.Order;
import ristorante.entity.Tables;
import ristorante.repo.DishRepository;
import ristorante.repo.OrderRepository;
import ristorante.repo.TablesRepository;


@Configuration
public class InitialDBdata {

	@Value("${table.count}")
	private int tableCount;

	@Value("${spring.profiles.active:}")
	private String profileName;
	
	@Value("${data.path}")
	private String dataPath;

	@Bean
	public CommandLineRunner dataLoader(DishRepository dishRepo, TablesRepository tableRepo,
			OrderRepository orderRepo) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {

				List<Order> orders = new ArrayList<>();
				Dish.getAllDishes().forEach(d -> dishRepo.save(d));

				for (int i = 1; i <= tableCount; i++)
					tableRepo.save(new Tables(i));
				
				if(!dataPath.isEmpty()) {
					orders = OrderData.parseOrderJSONFile(profileName, dataPath);
					orders.stream().forEach(o -> orderRepo.save(o));
				}
			}
		};
	}
}
	