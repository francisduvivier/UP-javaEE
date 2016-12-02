package system.results.treatmentresults;

import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse is een subklasse van TreatmentResult
 * en stelt het resultaat van een medicijnenkuur voor.
 * 
 * @author SWOP Team 10
 */
public class MedicationResult extends TreatmentResult{
	/**
	 * Variabele die aangeeft of de patient een abnormale reactie op de
	 * medicijnenkuur heeft gehad
	 */
	private boolean abnormalReaction;
	
	/**
	 * Constructor van MedicationResult
	 * 
	 * @param report
	 *        De tekst in het resultatenrapport
	 * @param abnormalReaction
	 *        Indicatie van of de patient een abnormale reactie op de medicijnenkuur heeft gehad
	 */
	public MedicationResult(String report, boolean abnormalReaction) {
		super(report,ResultType.MEDICATION_RESULT);
		
		this.abnormalReaction = abnormalReaction;
	}
	
	/**
	 * Methode die aangeeft of de patient een abnormale reactie heeft gehad
	 * 
	 * @return true
	 *         Als de patient een abnormaal reageerde op de medicijnenkuur
	 *         false
	 *         Als de patient normaal reageerde op de medicijnenkuur
	 */
	private boolean hasAbnormalReaction() {
		return abnormalReaction;
	}
	
	/**
	 * Een tostring voor MedicationResult.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return super.toString() +"Medication: "+ hasAbnormalReaction();
	}

	/**
	 * Een methode om de gegevens van een medication resultaat om te zetten naar een String.
	 */
	@Override
	public String getDetails() {
		String details = "Report: " + super.getReport() + "\n" + "Abnormal reaction: " + hasAbnormalReaction();
		return details;
	}
}
