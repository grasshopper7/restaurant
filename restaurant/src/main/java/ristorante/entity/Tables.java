package ristorante.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access=AccessLevel.PUBLIC, force=true)
@Entity
public class Tables {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private final long id;
	
	@OneToOne(mappedBy = "table")
	private Order order;
	
	public Tables(String table) {
		this(Integer.parseInt(table));
	}
}
