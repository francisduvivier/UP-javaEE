package system.util;

/**
 *	Dit is een predicaat interface dat kan gecheckt worden en als dusdanig kan
 *	meegegeven worden als object. Het predicaat kan getypeerd aan meerdere dingen
 *  worden meegegeven, die het kunnen be•nvloeden en als interface aan degene
 *  die het moet checken. Hierdoor vormt er zich geen noodzakelijke koppeling
 *  tussen het object dat het predicaat moet checken en de objecten die het
 *  be•nvloeden.
 */
public interface Condition {
	/**
	 * Kijkt na of de conditie voldaan is
	 * 
	 * @param
	 * 			eventuele extra informatie die kan meegegeven worden
	 * @return
	 * 			waar als de conditie voldaan is
	 */
	public boolean check(Object arg);
}
