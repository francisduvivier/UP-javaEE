package system.results.medicaltestresults;

import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een resultaat van een X-ray scan voor.
 * 
 * @invar abnormalities != null && abnormalities != ""
 * @invar numberOfImagestaken > 0
 * 
 * @author SWOP Team 10
 */
public class XRayScanResult extends MedicalTestResult {
	/**
	 * Variabele die de abnormaalheden in de X-ray foto's bijhoudt 
	 */
	private String abnormalities;
	/**
	 * Het aantal genomen X-ray foto's
	 */
	private int numberOfImagesTaken;
	
	/**
	 * Constructor voor XRayScanResult
	 * 
	 * @param abnormalities	
	 *        De abnormaalheden in de X-ray foto's
	 * @param numberOfImagesTaken 
	 *        Het aantal genomen X-ray foto's
	 * @pre	abnormalities != null && abnormalities != ""
	 * 		De abnormaliteiten moeten verschillend zijn van null en de lege string
	 * @post getAbnormalities() == abnormalities
	 * @throws NullPointerException
	 *         Als er geen uitleg bij de rapportsectie abnormaalheden staat
	 */
	public XRayScanResult(String abnormalities, int numberOfImagesTaken) throws NullPointerException {
		super(ResultType.XRAY_SCAN_RESULT);
		
		setNumberOfImagesTaken(numberOfImagesTaken);
		if (abnormalities == "" || abnormalities == null)
			throw new NullPointerException("Abnormalities is null.");
		this.abnormalities = abnormalities;
	}
	
	/**
	 * Een toString voor XRayScanResult
	 */
	@Override
	@SystemAPI
	public String toString() {
		return super.toString()+"XRay scan: " + abnormalities;
	}
	
	/**
	 * Setter voor het aantal genomen X-ray foto's
	 * 
	 * @param numberOfImagesTaken
	 *        Het aantal genomen X-ray foto's
	 * @pre numberOfImagestaken > 0
	 * 		Aantal genomen afbeeldingen moet groter zijn dan nul
	 * @post getNumberOfImagesTaken() == numberOfImagesTaken
	 * @throws IllegalArgumentException
	 *         Het aantal foto's moet groter zijn dan 0
	 */
	private void setNumberOfImagesTaken(int numberOfImagesTaken) throws IllegalArgumentException{
		if(numberOfImagesTaken > 0) this.numberOfImagesTaken = numberOfImagesTaken;
		else throw new IllegalArgumentException();
	}
	
	/**
	 * Getter voor het aantal genomen X-ray foto's
	 * 
	 * @return numberOfImagesTaken
	 *         Het aantal genomen X-ray foto's
	 */
	private int getNumberOfImagesTaken() {
		return numberOfImagesTaken;
	}

	/**
	 * Getter voor de abnormaalheden opgemerkt tijdens de X-ray scan
	 * 
	 * @return abnormalities
	 *         De abnormaalheden opgemerkt in de X-ray foto's
	 */
	private String getAbnormalities() {
		return abnormalities;
	}

	/**
	 * Een methode om de gegevens van een x-ray scan resultaat om te zetten naar een String.
	 * 
	 * @return details
	 * 		   De gegevens
	 */
	@Override
	public String getDetails() { //TODO nog toe te voegen
		String details = "Abnormalities: " + getAbnormalities() + "\n" + "Number of images taken: " + getNumberOfImagesTaken();
		return details;
	}
	
	/**
	 * Een methode om twee x-ray scanresultaten te vergelijken.
	 * Ze zijn gelijk als hun abnormaliteiten, aantal genomen afbeeldingen en resultaattype overeenkomen.
	 * 
	 * @return true
	 * 		   Als hun abnormaliteiten, aantal genomen afbeeldingen en resultaattype overeenkomen
	 * @return false
	 * 		   Als hun abnormaliteiten, aantal genomen afbeeldingen en resultaattype niet overeenkomen
	 */
	@Override
	public boolean equals(Object o) {
		if (this==o)
			return true;
		
		if (!(o instanceof XRayScanResult))
				return false;
		
		XRayScanResult result = (XRayScanResult) o;
		return 	(this.getAbnormalities().equals(result.getAbnormalities()) &&
				this.getNumberOfImagesTaken() == result.getNumberOfImagesTaken() &&
				this.getResultType() == result.getResultType());
	}
}
