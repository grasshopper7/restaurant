package ristorante.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@Entity
@Table(name = "Dish_Order")
@NamedEntityGraph(name = "getOrderLines", attributeNodes = @NamedAttributeNode("orderLines"))
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final long id;

	@OneToMany(targetEntity = OrderLine.class, orphanRemoval = true, cascade = CascadeType.ALL)
	private List<OrderLine> orderLines;

	private OrderStatus status;

	@OneToOne
	private Tables table;

	private LocalDateTime modificationTime;

	/*
	 * @PrePersist void preInsert() { this.modificationTime = LocalDateTime.now(); }
	 */

	@PreUpdate
	void preUpdate() {
		this.modificationTime = LocalDateTime.now();
	}

	public static enum OrderStatus {
		ORDERED, PREPARING, READY, SERVED, BILLED, CANCELLED;
	}
}
