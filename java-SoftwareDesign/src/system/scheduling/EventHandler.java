package system.scheduling;

import java.util.Observable;
import java.util.Observer;

import system.campus.Campus;
import system.campus.Hospital;
import system.patients.ObserverAction;
import system.time.Time;
import system.time.TimeStamp;
import system.util.Pair;
import system.warehouse.StockOrder;
import system.warehouse.Warehouse;
import system.warehouse.dailyevents.MealOrderEvent;
import system.warehouse.dailyevents.MealRemovalEvent;
import system.warehouse.stock.Stock;


public class EventHandler {
	private EventQueue queue;

	/**
	 * De constructor van EventHandler.
	 * 
	 * @param hospital
	 * 		  Het betreffende ziekenhuis waar de events plaatsvinden
	 */
	public EventHandler(Hospital hospital) {
		Time time= hospital.getHospitalTime();
		MainScheduler scheduler=hospital.getScheduler();
		
		this.queue = new EventQueue(time.getTime());
		
		scheduler.addObserver(new SchedulerObserver());
		for(Campus campus:hospital.getCampuses()){
			for (Stock stock : campus.getWarehouse().getStockList().getStocks()) {
				stock.addObserver(new StockObserver());
			}
			AddDailyWarehouseEvents(campus.getWarehouse(), time.getTime());
		}
		time.addObserver(new TimeObserver());
	}
	/**
	 * Deze methode voegt de events toe die dagelijks zijn. Dit zijn de mealOrderEvents en de MealRemovalevents.
	 * Er is dagelijks 1 mealOrderEvent en 3 mealRemovalEvents. De removalEvents treden op wanneer patienten eten.
	 * 
	 * @param warehouse
	 * 		  Het warenhuis waaraan events moeten worden toegevoegd. 
	 * @param now
	 * 		  Het tijdstip van de events
	 */
	private void AddDailyWarehouseEvents(Warehouse warehouse, TimeStamp now) {
		for(int i=9;i<=19;i+=5){
		Event patientsEatEvent=new MealRemovalEvent(now.calcForwardTo(i, 0), queue, warehouse);
		queue.addEvent(patientsEatEvent);
		}
		Event mealOrderEvent=new MealOrderEvent(now.calcForwardTo(23,59), queue, warehouse);
		queue.addEvent(mealOrderEvent);
	}

	/**
	 * Een methode om de queue van events te updaten met een event van de schedule events
	 * 
	 * @param arg
	 * 		  Een schedule event
	 * @throws ClassCastException
	 * 		   Als het gegeven object niet naar een ScheduleEvent kan worden omgezet
	 */
	private void updateFromScheduler(Object arg) throws ClassCastException {
		ScheduleEvent scheduleEvent = (ScheduleEvent) arg;
		Event start = scheduleEvent.getStart();
		Event stop = scheduleEvent.getStop();
		
		if (start == null || stop == null)
			return;
		
		if (scheduleEvent.getScheduledPeriod() == null) {
				queue.removeEvent(start);
				queue.removeEvent(stop);
		} else {
			queue.addEvent(start);
			queue.addEvent(stop);
		}
	}
	
	/**
	 * Een methode om de queue te updaten met een order zijn event.
	 * 
	 * @param arg
	 * 		  Een stock order
	 * @throws ClassCastException
	 * 		   Als het gegeven object niet naar een StockOrder kan worden omgezet
	 */
	private void updateFromWarehouse(Object arg) throws ClassCastException {
		@SuppressWarnings("unchecked")
		Pair<StockOrder, ObserverAction> pair = (Pair<StockOrder, ObserverAction>) arg;
		
		StockOrder order = pair.getFirst();
		Event arrivalEvent = order.getArrivalEvent();
		
		if (pair.getSecond() == ObserverAction.ADD) {
			queue.addEvent(arrivalEvent);
		} else {
			queue.removeEvent(arrivalEvent);
		}
	}
	
	/**
	 * Een methode om de uitvoeringstermijn te zetten tot gegeven tijdstip
	 * 
	 * @param arg
	 * 		  Tijdstip tot wanneer de queue mag lopen
	 * @throws ClassCastException
	 * 		   Als het gegeven object niet naar een TimeStamp kan worden omgezet
	 */
	private void updateFromTime(Object arg) throws ClassCastException {
		TimeStamp now = (TimeStamp) arg;
		this.queue.executeUntil(now);
	}
	
	/**
	 * SchedulerObserver is een implementatie van Observer.
	 */
	private class SchedulerObserver implements Observer {
		
		/**
		 * Een methode om te updaten van de scheduler.
		 * 
		 * @param o
		 * @param arg
		 * 		  Object waaruit geupdate wordt (schedule event)
		 */
		@Override
		public void update(Observable o, Object arg) {
			updateFromScheduler(arg);
		}
	}
	
	/**
	 * StockObserver is een implementatie van Observer.
	 */
	private class StockObserver implements Observer {
		
		/**
		 * Een methode om te updaten van het warenhuis.
		 * 
		 * @param o
		 * @param arg
		 * 		  Object waar met geupdate wordt (stock order)
		 */
		@Override
		public void update(Observable o, Object arg) {
			updateFromWarehouse(arg);
		}
	}
	
	/**
	 * TimeObserver is een implementatie van Observer
	 *
	 */
	private class TimeObserver implements Observer {
		
		/**
		 * Een methode om te updaten van de tijd.
		 * 
		 * @param o
		 * @param arg
		 * 		  Object waar met geupdate wordt (timestamp)
		 */
		@Override
		public void update(Observable o, Object arg) {
			updateFromTime(arg);
		}
	}
}
