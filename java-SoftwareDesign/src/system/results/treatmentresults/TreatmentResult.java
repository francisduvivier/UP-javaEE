package system.results.treatmentresults;

import system.results.Result;
import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse stelt het resultaat van een behandeling voor.
 * 
 * @invar report != null && report != ""
 * 
 * @author SWOP Team 10
 */
public abstract class TreatmentResult implements Result {
	/**
	 * Variabele die de tekst in het resultatenrapport voorstelt
	 */
	private final String report;
	/**
	 * Variabele die het type resultaat voorstelt
	 */
	private final ResultType type;
	
	/**
	 * Constructor van TreatmentResult
	 * 
	 * @param report
	 *        De tekst in het resultatenrapport
	 * @param type
	 *        Het type resultaat
	 * @pre	report != null && report != ""
	 * 		De report moet verschillende van null en de lege string zijn
	 * @post getReport() == report
	 * @post getType() == type
	 * @throws NullPointerException
	 *         Als het resultatenrapport leeg is
	 */
	public TreatmentResult(String report, ResultType type) throws NullPointerException {
		if (report == null || report.equals(""))
			throw new NullPointerException("Rapport is null.");
		this.report = report;
		this.type = type;
	}

	/**
	 * Getter voor de inhoud van het resultatenrapport
	 * 
	 * @return report
	 *         De tekst in het resultatenrapport
	 */
	public String getReport() {
		return report;
	}
	
	/**
	 * Een toString methode voor TreatmentResult.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Treatment result: "+ report + ", ";
	}
	
	/**
	 * Een methode om het resultaat type van de treatment result op te vragen.
	 */
	@Override
	public ResultType getResultType() {
		return this.type;
	}
}
