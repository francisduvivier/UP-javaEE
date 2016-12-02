package system.scheduling;

import system.results.ResultType;

/**
 * Enumerator om het type event voor te stellen.
 */

public enum EventType {
	// TREATMENT
	SURGERY(ResultType.SURGERY_RESULT),
	CAST(ResultType.CAST_RESULT),
	MEDICATION(ResultType.MEDICATION_RESULT),
	
	// MEDICAL TEST
	BLOOD_ANALYSIS(ResultType.BLOODANALYSIS_RESULT),
	ULTRASOUND_SCAN(ResultType.ULTRASOUND_SCAN_RESULT),
	XRAY_SCAN(ResultType.XRAY_SCAN_RESULT),
	
	// APPOINTMENT
	APPOINTMENT;
	
	private final ResultType resultType;
	
	private EventType(ResultType resultType) {
		this.resultType = resultType;
	}
	
	private EventType() {
		this.resultType = null;
	}
	
	public ResultType getResultType() {
		return this.resultType;
	}
}
