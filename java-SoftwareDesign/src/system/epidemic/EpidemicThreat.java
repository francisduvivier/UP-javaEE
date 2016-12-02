package system.epidemic;

public class EpidemicThreat {
	
	/**
	 * Het maximaal bedreigingsniveau is 20. De campus begeeft zich in gevaar wanneer het bedreigingsniveau
	 * hoger dan, of hieraan gelijk is.
	 */
	private static int MAXIMUM_THREAT = 20;
	
	
	/**
	 * Er zijn drie soorten bedreigingniveau's.
	 */
	private int level;
	
	/**
	 * De constructor voor EpedemicThreat.
	 * 
	 * @param level
	 * 		  De graad van de bedreiging
	 */
	public EpidemicThreat(int level) {
		this.level = level;
	}
	
	/**
	 * Een methode om het bedreigingsniveau op te hogen.
	 * 
	 * @param 	otherThreat
	 * 			Epidemische bedreiging waar met opgehoogd wordt
	 */
	public void addThreat(EpidemicThreat otherThreat) {
		this.level += otherThreat.level;
	}
	
	/**
	 * Een methode om het bedreigingsniveau te verlagen
	 * 
	 * @param 	otherThreat
	 * 			Epidemische bedreiging waar met verlaagd wordt
	 */
	public void removeThreat(EpidemicThreat otherThreat) {
		this.level -= otherThreat.level;
	}
	
	/**
	 * Een methode om te controleren of de campus in gevaar is.
	 * 
	 * @return 	true
	 * 			Als het huidige bedreigingsniveau groter of gelijk is aan het maximaal toegelaten bedreigingsniveau
	 * 			false
	 * 			Als het huidige bedreigingsniveau kleiner is dan het maximaal toegelaten bedreigingsniveau
	 */
	public boolean isAtRisk() {
		return level >= MAXIMUM_THREAT;
	}
	
	/**
	 * Een methode om een object van deze klasse te kopieëren.
	 * 
	 * @return gekopieerd object
	 */
	public EpidemicThreat copy() {
		return new EpidemicThreat(this.level);
		
	}
}
