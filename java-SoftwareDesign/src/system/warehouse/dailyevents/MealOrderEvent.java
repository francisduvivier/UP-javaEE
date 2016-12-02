package system.warehouse.dailyevents;

import system.scheduling.EventQueue;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.warehouse.Warehouse;
import system.warehouse.stock.process.MidnightOrderProcess;

/**
 * Deze klasse stelt een midnachtelijkse eten bestelling voor. Etensbestellingen
 * gebeuren altijd om 23:59
 * 
 * @author SWOP Team 10
 * 
 */
public class MealOrderEvent extends DailyWarehouseEvent {
	/**
	 * @invar het dagelijks tijdstip van de bestelling moet 23:59 zijn
	 *        executionTime.get
	 */
	public MealOrderEvent(TimeStamp executionTime, EventQueue eventQueue,
			Warehouse warehouse) {
		super(executionTime, eventQueue, warehouse);
		if (!isMidNight(executionTime))
			throw new IllegalArgumentException(
					"Het tijdstip van een dagelijkse mealorder moet 23:59 zijn");
	}

	/**
	 * Checkt of de volgende dag de eerste keer is dat de klok terug op 23:59
	 * staat. Dit is alleen het geval als het meegegeven tijdstip op 23:59
	 * staat.
	 * 
	 * @param executionTime het te checken tijdstip
	 * @return of het meegegeven tijdstip 23:59 als uur en minuten heeft.
	 */
	private boolean isMidNight(TimeStamp executionTime) {
		return executionTime.calcForwardTo(23, 59).equals(TimeStamp
				.addedToTimeStamp(TimeDuration.days(1), executionTime));
	}

	/**
	 * Een methode om (dagelijks) nieuwe maaltijden te bestellen.
	 */
	@Override
	protected void executeDailyThing() {
		warehouse.getStockList().processOnStock(new MidnightOrderProcess(getExecutionTime()));
//		warehouse.getStockList().getDefaultStock(StockType.MEAL).makeMidnightOrder(getExecutionTime());
	}

	/**
	 * Een methode om een warenhuis gebeurtenis van de volgende dag te krijgen.
	 * 
	 * @return event
	 * 		   Het warenhuis event van de volgende dag
	 */
	@Override
	protected DailyWarehouseEvent getNextDayEvent() {
		return new MealOrderEvent(getRightNextDayTime(), eventQueue, warehouse);
	}
	
	/**
	 * Een methode om het juiste tijdstip van de volgende dag te krijgen
	 * 
	 * @return timestamp
	 * 		   Tijdstip van de volgende dag
	 */
	@Override
	protected TimeStamp getRightNextDayTime() {
		if (isMidNight(super.getRightNextDayTime()))
			return super.getRightNextDayTime();
		else
			return super.getRightNextDayTime().calcForwardTo(23, 59);
	}

}
