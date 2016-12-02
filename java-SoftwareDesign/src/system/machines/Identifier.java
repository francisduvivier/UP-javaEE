package system.machines;

import annotations.SystemAPI;

/**
 * Deze klasse stelt een id voor.
 * 
 * Op dit moment hebben enkel machines id's nodig en geeft de
 * ziekenhuisadministrator de juiste id bij het aanmaken van een machine op. Het
 * toekennen van id's kan beter gedaan worden door een bijkomende klasse
 * IdGenerator te maken die de id's automatisch genereert, en vervolgens
 * automatisch de juiste id aan een nieuwe machine toe te kennen. De opgave
 * verhindert dit spijtig genoeg.
 * 
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public class Identifier {
	/**
	 * De id
	 */
	private final long ID;
	
	/**
	 * Constructor voor Identifier
	 * 
	 * @param ID
	 *       het identifier nummer
	 */
	@SystemAPI
	public Identifier(long ID) {
		this.ID = ID;
	}
	
	/**
	 * Methode om de identifier met een andere identifier te vergelijken
	 * 
	 * @param other
	 *        De andere identifier
	 * @return true 
	 *         Als ze gelijk zijn
	 *         false
	 *         Als ze verschillend zijn
	 */
	public boolean equals(Identifier other) {
		return (this.ID == other.ID);
	}
	
	/**
	 * Een toString voor Identifier.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return Long.toString(ID);
	}
}
