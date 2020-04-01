package ristorante.display;

import lombok.Data;
import ristorante.entity.Order;

@Data
public class OrderUIInfo {

	private final long table;
	
	private final String initialState;
	
	//private final String modifyTime;
	
	private final Order order;
	
	private String message;
	
}
