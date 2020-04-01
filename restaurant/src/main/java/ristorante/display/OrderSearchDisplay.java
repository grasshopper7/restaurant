package ristorante.display;

public class OrderSearchDisplay {

	private String[] checkedState;
	
	private String[] selectedTime;
	
	private String checkAllState;
	
	
	public OrderSearchDisplay() {
	}

	public OrderSearchDisplay(String[] checkedState, String[] selectedTime) {
		this(checkedState, selectedTime, "");
	}
	
	public OrderSearchDisplay(String[] checkedState, String[] selectedTime, String checkAllState ) {
		this.checkedState = checkedState;
		this.selectedTime = selectedTime;
		this.checkAllState = checkAllState;
	}

	public String[] getCheckedState() {
		return checkedState;
	}

	public void setCheckedState(String[] checkedState) {
		this.checkedState = checkedState;
	}

	public String[] getSelectedTime() {
		return selectedTime;
	}

	public void setSelectedTime(String[] selectedTime) {
		this.selectedTime = selectedTime;
	}

	public String getCheckAllState() {
		return checkAllState;
	}

	public void setCheckAllState(String checkAllState) {
		this.checkAllState = checkAllState;
	}
}
