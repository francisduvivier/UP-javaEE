package system.epidemic;

import system.patients.Diagnosis;
/**
 *	Klasse om een identificatie aan ziektes te geven.
 */
public class DiseaseID {
	private final String identifier;
	
	/**
	 * Maakt een nieuwe id aan
	 * 
	 * @param diagnosis
	 * 			De diagnose aan de hand van dewelke een identificatie kan gegeven
	 * 			worden
	 */
	DiseaseID(Diagnosis diagnosis) {
		this.identifier = diagnosis.getDescription();
	}
	
	/**
	 * De identificator van deze id. Aan de hand van deze kan vergeleken worden.
	 * 
	 * @return	De identificator van de ziekte.
	 */
	private String getIdentifier() {
		return this.identifier;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof DiseaseID))
			return false;
		
		DiseaseID id = (DiseaseID) o;
		
		return id.getIdentifier().equals(this.getIdentifier());
	}
	
	public int hashCode() {
		return this.getIdentifier().hashCode();
	}
}
