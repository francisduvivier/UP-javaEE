package system.warehouse.dailyevents;

import system.scheduling.Event;
import system.scheduling.EventQueue;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.warehouse.Warehouse;

/**
 * Deze klasse dient als overkoepelende event voor
 * 
 * @author SWOP Team 10
 * 
 */
public abstract class DailyWarehouseEvent extends Event {
	
	/**
	 * De EventQueue waar de dagelijkse events aan toegevoegd moeten worden
	 */
	protected final EventQueue eventQueue;
	
	/**
	 * De warehouse die dagelijkse event manipuleert of gebruikt
	 */
	protected final Warehouse warehouse;
	
	/**
	 * Maakt een nieuwe dagelijkse event aan.
	 * @param executionTime het tijdstip waarop de event uitgevoerd moet worden
	 * @param eventQueue de EventQueue waar de event in moet staan
	 * @param warehouse de warehouse die dagelijkse event manipuleert of gebruikt
	 * @invar geen van de parameters mag null-waarde hebben.
	 */
	public DailyWarehouseEvent(TimeStamp executionTime, EventQueue eventQueue, Warehouse warehouse) {
		super(executionTime);
		if(executionTime==null)
			throw new IllegalArgumentException("ExecutionTime mang niet null zijn");
		if(eventQueue==null)
			throw new IllegalArgumentException("eventQueue mag niet null zijn");
		if(warehouse==null)
			throw new IllegalArgumentException("warehouse mag niet null zijn");
		this.eventQueue = eventQueue;
		this.warehouse = warehouse;
	}

	/**
	 * Methode die het herplannen van de volgende dag en de dagelijkse bezigheden (bv. verorberen maaltijden) uitvoert
	 */
	@Override
	public void execute() {
		rescheduleNextDay();
		executeDailyThing();
	}

	/**
	 * Zorgt ervoor dat een nieuwe event de volgende dag op de eventQueue
	 * geplaatst wordt.
	 */
	protected void rescheduleNextDay() {
		eventQueue.addEvent(getNextDayEvent());
	}

	/**
	 * Moet de event terug geven die de volgende dag op de queue gezet moet
	 * worden.
	 * 
	 * @return hetzelfde soort event als dit event maar dan de volgende dag als
	 *         executionTime
	 */
	protected abstract DailyWarehouseEvent getNextDayEvent();
	/**
	 * Voert de eigenlijke dagelijke actie uit.
	 */
	protected abstract void executeDailyThing();
	
	/**
	 * geeft hetzelfde bijna tijdstip als dit tijdstip met als enige verschil dat de dag verhoogd is met 1
	 * @return een nieuw TimeStamp object met de dag met 1 verhoogd
	 */
	protected TimeStamp getRightNextDayTime() {
		return TimeStamp.addedToTimeStamp(TimeDuration.days(1), getExecutionTime());
	}
	
	@Override
	public String toString() {
		return super.toString() ;
	}
}
