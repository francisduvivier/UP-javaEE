package system.util;

/**
 * Deze klasse stelt een queue met een maximumcapaciteit voor.
 * 
 * De maximum capaciteit van deze stack is afhankelijk van de maximale
 * hoeveelheid plasters, medication items, enzovoort.
 * 
 * Wanneer de max. capaciteit bereikt wordt, wordt het oudste element
 * (element met index 0) verwijderd.
 * 
 * @author SWOP Team 10
 */
public class BoundedQueue<Item> extends Queue<Item> {
	/**
	 * De maximumcapaciteit van de queue
	 */
	private int max;
	
	/**
	 * Initialisatie van de queue met zijn maximumcapaciteit
	 * 
	 * @param max
	 *        De maximumcapaciteit van de queue
	 */
	public BoundedQueue(int max) {
		super();
		this.max = max;
	}
	
	/**
     * Methode om een element aan de queue toe te voegen
     * 
     * @param elem
     *        Het toe te voegen element
     */
	public void enqueue(Item item) {
		if (this.size() < max) 
			super.enqueue(item);
		else {
			super.dequeue();
			super.enqueue(item);
		}		
	}
}
