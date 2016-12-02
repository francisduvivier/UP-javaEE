package system.results;

import client.IResult;

/**
 * Deze klasse stelt een resultaat van een PatientOperation voor.
 * 
 * @author SWOP Team 10
 */
public interface Result extends IResult {
	
	/**
	 * Elke Result heeft een textuele presentatie van de details van het
	 * resultaat. Deze methode geeft die details in de vorm van een string.
	 */
	public abstract String getDetails();

	/**
	 * Geeft weer wat voor soort resultaat dit resultaat is. Dit kan
	 * bijvoorbeeld XRAY_SCAN_RESULT of MEDICATION_RESULT zijn.
	 */
	public abstract ResultType getResultType();
	
	@Override
	public abstract boolean equals(Object o);
}
