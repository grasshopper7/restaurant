package ristorante.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import ristorante.entity.Dish;

public interface DishRepository extends JpaRepository<Dish, String> {

}
