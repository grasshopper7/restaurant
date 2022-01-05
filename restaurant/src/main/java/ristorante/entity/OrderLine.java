package ristorante.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class OrderLine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final long id;

	@ManyToOne(targetEntity = Dish.class)
	private final Dish dish;

	private int qty;

	public OrderLine(long id, Dish dish, int qty) {
		this.id = id;
		this.dish = dish;
		this.qty = qty;
	}
}
