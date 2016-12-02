package system.results;

/**
 * Deze enumklasse houdt de verschillende soorten van resultaat types bij. 
 * Er is een constante voor elke concrete PatientOperation.
 * 
 *  @author SWOP Team 10
 */
public enum ResultType {
	// Treatments
	MEDICATION_RESULT, 
	CAST_RESULT, 
	SURGERY_RESULT,

	// Medical Tests
	BLOODANALYSIS_RESULT, 
	ULTRASOUND_SCAN_RESULT, 
	XRAY_SCAN_RESULT;
}
