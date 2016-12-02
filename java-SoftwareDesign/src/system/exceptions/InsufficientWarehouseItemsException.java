package system.exceptions;

import annotations.SystemAPI;

/**
 * Voor het geven van een foutmelding wanneer er onvoldoende stock is om een
 * ScheduleEvent te plannen.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public class InsufficientWarehouseItemsException extends RuntimeException {
	private static final long serialVersionUID = -4813716587805480098L;

	@SystemAPI
	public InsufficientWarehouseItemsException() {
	}

	@SystemAPI
	public InsufficientWarehouseItemsException(String message) {
		super(message);
	}
}
