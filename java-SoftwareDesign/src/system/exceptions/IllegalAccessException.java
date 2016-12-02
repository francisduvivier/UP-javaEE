package system.exceptions;

import annotations.SystemAPI;

/**
 * Voor het geven van een foutmelding wanneer de gebruiker niet ingelogd is of
 * niet de juiste bevoegdheid heeft.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public class IllegalAccessException extends RuntimeException {
	private static final long serialVersionUID = -6797573856175589146L;

	@SystemAPI
	public IllegalAccessException(String message) {
		super(message);
	}
}
