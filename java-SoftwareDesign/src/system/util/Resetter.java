package system.util;
/**
 * 
 * Deze klasse roept de reset methode aan bij een gegeven Resetable. Een nieuwe
 * instansie van deze klasse kan meegegeven aan een bepaalde klasse, deze kan 
 * dan de resetable veranderen naar wens zonder de klasse die de Resetter maakt
 * weet over welk soort resetable het gaat.
 *
 */
public final class Resetter {
	/**
	 * De klasse die gereset kan worden
	 */
	private Resetable resetable;
	
	/**
	 * Deze methode verzet het resetbare object dat actief is binnen dit object
	 * 
	 * @param resetable
	 * 			De meegegeven resetbare instansie
	 */
	public final void setResetable(Resetable resetable) {
		this.resetable = resetable;
	}
	
	/**
	 * Deze methode reset het huidige resetbare object
	 * 
	 * @throws NullPointerException
	 * 			Als er nog geen resetbaar object is ingesteld
	 */
	public final void reset() throws NullPointerException {
		if (this.resetable != null)
			this.resetable.reset();
		else 
			throw new NullPointerException();
	}
}
