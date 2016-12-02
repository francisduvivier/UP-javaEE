package system.campus;

import annotations.SystemAPI;

public class CampusId {
	
	/**
	 * Variabele die de identifier (een uniek en onveranderbaar getal) voorstelt
	 */
	private final long id;
	
	/**
	 * Initialisatie van een CampusId object
	 * 
	 * @param campus
	 * 			De campus waarvoor een ID gegenereerd wordt a.d.h.v. een hashcode
	 */
	public CampusId(Campus campus) {
		this.id = System.identityHashCode(campus);
	}
	
	/**
	 * Methode om te controleren of 2 campussen dezelfde identifier hebben.
	 * 
	 * @param o
	 * 			De campusid waarmee vergeleken wordt
	 * @return true als de identifiers gelijk zijn
	 * 			| false als de identifiers verschillend zijn
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CampusId))
			return false;
		
		CampusId otherId = (CampusId) o;
		
		return otherId.id == this.id;
	}
	
	/**
	 * Getter voor de hashcode van een id
	 */
	@Override
	public int hashCode() {
		return (int)this.id;
	}
	
	/**
	 * ToString methode voor de campusid in de vorm van "CAMPUS ID: id-nummer".
	 * Kan o.a. gebruikt worden om te debuggen of in de user interface.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "CAMPUS ID: "+Long.toString(this.id);
	}
}
