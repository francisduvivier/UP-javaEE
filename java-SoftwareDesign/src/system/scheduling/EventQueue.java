package system.scheduling;

import java.util.TreeSet;

import system.time.TimeStamp;

/**
 * Deze klasse is een heel algemene eventqueue klasse. Ze bevat een geordende
 * lijst van events. Er kunnen events toegevoegd en terug verwijderd worden en
 * alle events die voor een bepaald tijdstip plaatvinden kunnen uitgevoerd
 * worden. Er is geen implementatie om rekening te houden met de huidige tijd.
 * 
 * @author Swop team 10
 * 
 */
public class EventQueue {
	private TreeSet<Event> eventQueue;
	private TimeStamp executedUntil;

	/**
	 * De constructor van EventQueue.
	 * 
	 * @param begin
	 * 		  Het tijdstip tot wanneer events kunnen worden uitgevoerd.
	 */
	public EventQueue(TimeStamp begin) {
		this.eventQueue = new TreeSet<Event> ();
		this.executedUntil = begin;
	}

	/**
	 * Een methode om een event toe te voegen aan de queue.
	 * 
	 * @param event
	 * 		  Toe te voegen event
	 * @throws IllegalArgumentException
	 * 		   Als de uitvoeringstijd van het toe te voegen event niet voor de stoptijd van de queue valt
	 */
	public void addEvent(Event event) throws IllegalArgumentException {
		if (!event.getExecutionTime().before(executedUntil))
			eventQueue.add(event);
		else
			throw new IllegalArgumentException("Given event is in the past.");
	}

	/**
	 * Een methode om een event uit de queue te verwijderen.
	 * 
	 * @param event
	 * 		  Het  te verwijderen event
	 * @return true
	 * 		   Als het evenement verwijderd is
	 * @return false
	 * 		   Als het evenement niet verwijderd is
	 */
	public boolean removeEvent(Event event) {
		if (event != null)
			return eventQueue.remove(event);
		else
			return false;
	}

	/**
	 * Een methode om de events op de queue uit te voeren tot een gegeven tijdstip.
	 * 
	 * @param timeStamp
	 * 		  Het tijdstip tot wanneer er uitgevoerd mag worden
	 * @throws IllegalArgumentException
	 */
	public void executeUntil(TimeStamp timeStamp) throws IllegalArgumentException {
		if (this.executedUntil.before(timeStamp)) {
				TimeStamp eventTime = null;
				
				if (!this.eventQueue.isEmpty())
					eventTime = this.eventQueue.first().getExecutionTime();
			
				while (!this.eventQueue.isEmpty() && eventTime.compareTo(timeStamp)<=0) {
					Event firstEvent=this.eventQueue.pollFirst();
					
					this.executedUntil = eventTime;
					firstEvent.execute();
					if (!this.eventQueue.isEmpty())
						eventTime = this.eventQueue.first().getExecutionTime();
				}
				
				this.executedUntil = timeStamp;
		}
	}
}
