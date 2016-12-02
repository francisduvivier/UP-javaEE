package system.machines;

/**
 * Deze klasse stelt een locatie in het ziekenhuis voor.
 * 
 * @invar room >= 0
 * 
 * @author SWOP Team 10
 */
public class Location {
	/**
	 * De verdieping van de locatie
	 */
	private int floor;
	/**
	 * De kamer van de locatie
	 */
	private int room;
	
	/**
	 * Constructor voor  een locatie-object
	 * 
	 * @param floor
	 *        Een verdieping van het ziekenhuis
	 * @param room
	 *        Een kamer (op een bepaalde verdieping) van het ziekenhuis
	 * @throws IllegalArgumentException
	 *         Als de opgegeven kamer een negatief getal is
	 */
	public Location(int floor, int room) throws IllegalArgumentException {
		this.floor = floor; // assume negative floors are possible
		if (room < 0) // assume negative rooms aren't possible
			throw new IllegalArgumentException("Roomnumber is, but cannot be, lower than zero.");
		this.room = room;
	}
	
	/**
	 * Getter voor de verdieping van de locatie
	 * 
	 * @return floor
	 *         De verdieping van de locatie
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * Getter voor de kamer van de locatie
	 * 
	 * @return room
	 *         De kamer van de locatie
	 */
	public int getRoom() {
		return room;
	}
}
