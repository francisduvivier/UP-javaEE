package system.exceptions;

import annotations.SystemAPI;

/**
 * Voor het geven van een foutmelding wanneer een actie uitgevoerd wordt, die op
 * dat moment in de tijd niet uitgevoerd mag worden.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public class IllegalOperationException extends RuntimeException {
	private static final long serialVersionUID = 1925150862181252309L;

	@SystemAPI
	public IllegalOperationException() {
	}

	@SystemAPI
	public IllegalOperationException(String message) {
		super(message);
	}

}
