package system.warehouse.dailyevents;
import system.scheduling.EventQueue;
import system.time.TimeStamp;
import system.warehouse.Warehouse;
import system.warehouse.stock.process.PatientsEatProcess;
import system.warehouse.stock.process.RemoveExpiringMealsProcess;

/**
 * Deze klasse implementeert een dagelijkse event die eten verbruikt/verwijderd. Deze
 * event gebeurt wanneer er gegeten wordt. Bij het serveren van eten wordt er
 * eerst gekeken of er geen vervallen eten is wat dan ook weggedaan wordt.
 * 
 * @author SWOP Team 10
 * 
 */
public class MealRemovalEvent extends DailyWarehouseEvent{

	/**
	 * De constructor van de klasse MealRemovalEvent.
	 * 
	 * @param executionTime
	 * 		  Uitvoeringstijd van het event
	 * @param eventQueue
	 *        Queue om events op te zetten
	 * @param warehouse
	 * 		  Het actieve warenhuis
	 */
	public MealRemovalEvent(TimeStamp executionTime, EventQueue eventQueue,
			Warehouse warehouse) {
		super(executionTime, eventQueue, warehouse);
	}

	/**
	 * Een methode om vervallen maaltijden te verwijderen en de patienten te laten eten.
	 */
	@Override
	protected void executeDailyThing() {
		warehouse.getStockList().processOnStock(new RemoveExpiringMealsProcess(getExecutionTime()));
		warehouse.getStockList().processOnStock(new PatientsEatProcess());
	}

	/**
	 * Een methode om het event van de volgende dag te krijgen.
	 * 
	 * @return dailyWarehouseEvent
	 * 		   Het event met zijn gebeurtenissen van de volgende dag
	 */
	@Override
	protected DailyWarehouseEvent getNextDayEvent() {
		return new MealRemovalEvent(getRightNextDayTime(), eventQueue, warehouse);

	}

}
