package system.results.treatmentresults;

import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse is een subklasse van TreatmentResult
 * en stelt het resultaat van een operatie voor.
 * 
 * @invar specialAftercare != null
 * 
 * @author SWOP Team 10
 */
public class SurgeryResult extends TreatmentResult {
	private String specialAftercare;
	
	/**
	 * Constructor van SurgeryResult 
	 * 
	 * @param report
	 *        De tekst in het resultatenrapport
	 * @param specialAftercare
	 *        De maatregelen die achter de operatie dienen genomen te worden
	 * @pre specialAftercare != null
	 * 		De afterkuur moet verschillend zijn van null
	 * @post getSpecialAftercare() == specialAftercare
	 * @throws NullPointerException
	 *         Als er geen maatregels voor na de operatie opgegeven zijn
	 */
	public SurgeryResult(String report, String specialAftercare) throws NullPointerException {
		super(report, ResultType.SURGERY_RESULT);
		
		if (specialAftercare == null)
			throw new NullPointerException("Special aftercare is null.");
		this.specialAftercare=specialAftercare;
	}

	/**
	 * Getter voor de maatregelen die achter de operatie moeten genomen worden
	 * 
	 * @return specialAftercare
	 *         De maatregelen voor na de operatie
	 */
	private String getSpecialAftercare() {
		return specialAftercare;
	}
	
	/**
	 * Een toString voor SurgeryResult.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return super.toString() +"Surgery: "+ getSpecialAftercare();
	}

	/**
	 *  Een methode om de gegevens van een surgery resultaat om te zetten naar een String.
	 */
	@Override
	public String getDetails() { //TODO nog toe te voegen
		String details = "Report: " + super.getReport() + "\n" + "Special aftercare: " + getSpecialAftercare();
		return details;
	}
}
