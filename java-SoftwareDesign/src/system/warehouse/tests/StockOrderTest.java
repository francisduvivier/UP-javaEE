package system.warehouse.tests;

import java.util.SortedSet;

import system.campus.Campus;
import system.campus.Hospital;
import system.controllers.SessionController;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.warehouse.StockOrder;
import system.warehouse.Warehouse;
import system.warehouse.stock.StockList;

public abstract class StockOrderTest {
	protected Warehouse warehouse;
	protected Hospital hospital;
	protected SessionController sessionController;
	
	
	
	public void testInit() {
		sessionController = new SessionController();
		hospital = (Hospital)sessionController.getBigHospital();
		sessionController.setCurrentCampus((Campus)sessionController.getCampuses().get(0));
		warehouse = new Warehouse(new StockList(sessionController.getCurrentCampus()));
	}

	/**
	 * @param amountArrived
	 * @param orders
	 */
	protected int doArrivals(SortedSet<StockOrder> orders) {
		int amountArrived=0; 
		for (StockOrder order:orders) {
			order.getArrivalEvent().execute();
			amountArrived+=order.getAmount();
		}
		return amountArrived;
	}
	
	/**
	 * @param amountArrived
	 * @param orders
	 */
	protected void processArrivals(SortedSet<StockOrder> orders, TimeStamp expirationDate) {
		for (StockOrder order:orders) {
			order.processArrival(expirationDate);
		}
	}
	/**
	 * Geeft een tijdstip in de toekomst terug
	 * @param nbOfDays
	 * 		The number of days more that should be added to the current time
	 * @return
	 * 		A timestamp of a day in the distant future
	 */
	public TimeStamp getFutureTime(double nbOfDays){
		TimeStamp now = hospital.getHospitalTime().getTime();
		return TimeStamp.addedToTimeStamp(TimeDuration.days((int)(nbOfDays*100)/100), now);
	}
}
