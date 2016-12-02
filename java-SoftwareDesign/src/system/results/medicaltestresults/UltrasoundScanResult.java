package system.results.medicaltestresults;

import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een resultaat van een ultrasound scan voor.
 * 
 * @invar scanInfo != null && scanInfo != ""
 * 
 * @author SWOP Team 10
 */
public class UltrasoundScanResult extends MedicalTestResult {
	/**
	 * Variabele die informatie over de ultrasound scan bijhoudt
	 */
	private String scanInfo;
	/**
	 * Variabele die de aard van de gescande massa voorstelt
	 */
	private ScanMatter scanMatter;

	/**
	 * Constructor UltrasoundScanResult
	 * 
	 * @param scanInfo	
	 *        Informatie over de ultrasound scan
	 * @param scanMatter
	 *        De aard van de gescande massa
	 * @pre scanInfo != null && scanInfo != null
	 *		De scaninfo moet verschillend zijn van null en de lege string
	 * @pre scanMatter != null
	 * 		De scanmaterie moet verschillend zijn van null
	 * @post getScanInfo() == scanInfo
	 * @post getScanMatter() == scanMatter
	 * @throws NullPointerException
	 *         Als de waarde van een van beide parameters niet ingevuld is
	 */
	public UltrasoundScanResult(String scanInfo, ScanMatter scanMatter) throws NullPointerException {
		super(ResultType.ULTRASOUND_SCAN_RESULT);

		if (scanInfo == "" || scanInfo == null)
			throw new NullPointerException("Scan info is null.");
		this.scanInfo=scanInfo;
		if (scanMatter == null)
			throw new NullPointerException("Scan matter is null.");
		this.scanMatter = scanMatter;
	}
	
	/**
	 * Een toString van UltrasoundScanResult
	 */
	@Override
	@SystemAPI
	public String toString() {
		return super.toString() +"Ultrasoundscan Result: "+ scanInfo;
	}

	/**
	 * Getter voor informatie over de ultrasound scan
	 * 
	 * @return scanInfo
	 *         Informatie over ultrasound scan
	 */
	private String getScanInfo() {
		return scanInfo;
	}
	
	/**
	 * Getter voor de aard van de gescande massa
	 * 
	 * @return scanMatter
	 *         De aard van de gescande massa
	 */
	private ScanMatter getScanMatter() {
		return scanMatter;
	}

	/**
	 * Een methode om de gegevens van een ultrasonisch scanresultaat om te zetten naar een String.
	 * 
	 * @return details
	 * 		   De gegevens
	 */
	@Override
	public String getDetails() { //TODO nog toe te voegen
		String details = "Scan info: " + getScanInfo() + "\n" + "Scan matter: " + getScanMatter();
		return details;
	}
	
	/**
	 * Een methode om twee ultrasonische scanresultaten te vergelijken.
	 * Ze zijn gelijk als hun scaninfo, resultaattype en scanmaterie overeenkomen.
	 * 
	 * @return true
	 * 		   Als hun scaninfo, resultaattype en scanmaterie overeenkomen
	 * @return false
	 * 		   Als hun scaninfo, resultaattype en scanmaterie niet overeenkomen
	 */
	@Override
	public boolean equals(Object o) {
		if (this==o) 
			return true;
		
		if (!(o instanceof UltrasoundScanResult))
			return false;
		
		UltrasoundScanResult result = (UltrasoundScanResult) o;
		return (this.getResultType() == result.getResultType() &&
				this.getScanInfo().equals(result.scanInfo) &&
				this.getScanMatter() == result.getScanMatter());
	}
}