package system.exceptions;

import annotations.SystemAPI;

/**
 * Voor het geven van een foutmelding wanneer meerdere geplande gebeurtenissen
 * elkaar overlappen.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public class SchedulingException extends RuntimeException {
	private static final long serialVersionUID = 4058086811902003070L;

	@SystemAPI
	public SchedulingException(String message) {
		super(message);
	}
}
