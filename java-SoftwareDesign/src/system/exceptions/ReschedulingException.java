package system.exceptions;

import annotations.SystemAPI;

/**
 * Voor het geven van een foutmelding wanneer een gebeurtenis opnieuw gepland
 * moet worden.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public class ReschedulingException extends RuntimeException {
	private static final long serialVersionUID = -2778533121706820208L;

	@SystemAPI
	public ReschedulingException(String message) {
		super(message);
	}

}
