package system.results.treatmentresults;

import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse is een subklasse van TreatmentResult
 * en stelt het resultaat van een gipsverband voor.
 * 
 * @author SWOP Team 10
 */
public class CastResult extends TreatmentResult {
	
	/**
	 * Constructor van CastResult
	 * 
	 * @param report
	 *        De tekst in het resultatenrapport
	 */
	public CastResult(String report) {
		super(report,ResultType.CAST_RESULT);
	}
	
	/**
	 * Een toString voor CastResult.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return super.toString()+"Cast ";
	}
	
	/**
	 * Een methode om de gegevens van een cast resultaat om te zetten naar een String.
	 */
	@Override
	public String getDetails() {
		return super.getReport();
	}
}
