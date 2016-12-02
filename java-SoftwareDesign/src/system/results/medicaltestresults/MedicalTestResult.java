package system.results.medicaltestresults;

import system.results.Result;
import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse stelt het resultaat van een medische test voor.
 * 
 * @author SWOP Team 10
 */
public abstract class MedicalTestResult implements Result{
	/**
	 * Variabele die het type resultaat voorstelt
	 */
	private final ResultType type;
	
	/**
	 * Constructor van MedicalTestResult
	 * 
	 * @param type
	 *        Het type resultaat
	 */
	public MedicalTestResult(ResultType type) {
		this.type = type;
	}
	
	/**
	 * Een toString voor MedicalTestResult.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Medical test result: ";
	}
	
	/**
	 * Een methode om het resulttype op te vragen.
	 */
	@Override
	public ResultType getResultType() {
		return this.type;
	}
}

