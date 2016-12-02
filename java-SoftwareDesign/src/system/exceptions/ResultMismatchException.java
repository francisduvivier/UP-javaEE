package system.exceptions;

import annotations.SystemAPI;

/**
 * Voor het geven van een foutmelding wanneer de resultaten van meerdere
 * medische tests of behandelingen niet overeenkomen hoewel ze wel overeen
 * zouden moeten komen.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public class ResultMismatchException extends RuntimeException {
	private static final long serialVersionUID = 4904238622643470185L;

	@SystemAPI
	public ResultMismatchException(String message) {
		super(message);
	}
}
