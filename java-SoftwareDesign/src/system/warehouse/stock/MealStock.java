package system.warehouse.stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import system.campus.Campus;
import system.patients.ObserverAction;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.util.Pair;
import system.util.Queue;
import system.warehouse.Meal;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;

/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public class MealStock extends DefaultStock {
	/**
	 * Constante die de maximum capaciteit aan maaltijden van het magazijn voorstelt
	 */
	public static final int MAX_CAPACITY_MEALS = 120;
	/**
	 * De maaltijden die opgeslagen zijn in het ziekenhuis
	 */
	private Queue<Meal> meals;

	/**
	 * De campus waartoe het magazijn waarin de voorraad wordt bewaard, behoort
	 */
	private Campus campus;
	
	/**
	 * Een integer die bijhoudt hoeveel meals er bij de laatste Order besteld zijn.
	 * Door deze waarde te weten weten we hoeveel er tussen een order en zijn aankomst nog gaat aankomen.
	 */
	private int previousOrderAmount;
	/**
	 * Initialisatie van de voorraad aan maaltijden
	 * 
	 * @param campus
	 *        De campus waartoe het magazijn waarin de voorraad wordt bewaard, behoort
	 */
	public MealStock(Campus campus) {
		super(campus.getBigHospital().getHospitalTime(),StockType.MEAL);
		
		this.meals = new Queue<Meal>();
		this.orders = new TreeSet<StockOrder>();
		
		this.campus = campus;
		
		this.fillStock();
		
		this.previousOrderAmount=0;
	}
	/**
	 * er moet niks gedaan worden bij updatefromtime in de mealstock omdat de tijd vooruit gezet word door de events. 
	 */
	@Override
	protected void updateFromTime(Object arg) {
		return;
	}
	
	/**
	 * Methode om een maaltijd aan het warenhuis toe te voegen.
	 * 
	 * @return item
	 *         als er nog maaltijden zijn
	 *         null
	 *         als er geen maaltijden meer zijn
	 * 
	 */
	@Override
	public void addWarehouseItem(WarehouseItem item) {
			meals.enqueue((Meal) item);
	}

	/**
	 * Methode om een maaltijd van het warenhuis te verwijderen.
	 * 
	 * @pre aantal maaltijd in warenhuis > 0
	 * 		| meals > 0
	 * 
	 * @return item
	 * 		   als er nog maaltijden zijn
	 * 		   null
	 * 		   als er geen maaltijden meer zijn
	 */
	@Override
	public WarehouseItem removeWarehouseItem() {
		if (!meals.isEmpty()) {
			return meals.dequeue();
		} else {
			return null;
		}
	}

	/**
	 * Methode om een order van de stock te verwijderen.
	 * 
	 * @item order
	 * 			De te verwijderen order
	 */
	@Override
	public void removeStockOrder(StockOrder order) {
		orders.remove(order);
	}

	/**
	 * Methode om de stock te vullen met maaltijden.
	 * De maaltijden krijgen een vervaltermijn van 14 dagen.
	 */
	@Override
	public void fillStock() {
		TimeStamp expirationDate = TimeStamp.addedToTimeStamp(TimeDuration.days(14), getNow());
		
		for (int i = 0; i < MAX_CAPACITY_MEALS; i++) {
			meals.enqueue(new Meal(expirationDate));
		}	
	}

	/**
	 * Methode om het aantal maaltijd in de stock op te vragen.
	 * 
	 * @return meals.size()
	 * 		   Aantal maaltijden in de stock
	 */
	@Override
	public int getStockSize() {
		return meals.size();
	}

	/**
	 * Methode om een gesorteerde set van orders te krijgen.
	 * 
	 * @return orders
	 *         Gesorteerde set van maaltijd orders
	 */
	@Override
	public SortedSet<StockOrder> getOrders() {
		return Collections.unmodifiableSortedSet(orders);
	}
	
	/**
	 * De primitieve operatie van de ConcreteClass van het Template Method patroon.
	 * 
	 * @param before
	 *        De vorige huidige tijd
	 */
	@Override
	protected void updateStockFromTime(TimeStamp before) {
		removeExpiringMeals(getNow()); 
	}
	
	
	/**
	 * Methode om het aantal maaltijden dat op 1 dag besteld moet worden, te
	 * berekenen. Er wordt ook rekening gehouden met het aantal meals dat nog
	 * aangekomen gaat zijn voor de aankomst van de Order waarvoor we de
	 * hoeveelheid berekenen. Dit doen we door naar het laatst aantal beselde
	 * meals te kijken.
	 * 
	 * @return Het aantal te bestellen maaltijden
	 */
	private int calcDailyOrderAmount() {
		int nbInStock = meals.size();
		int nbStockAtArrival=nbInStock + previousOrderAmount - 3 * getNbPatients();
		int amount = Math.max(
				0,
				Math.min(
				MAX_CAPACITY_MEALS - Math.max(0,nbStockAtArrival)
				// indien we over de bovenstaande hoeveelheid zouden bestellen
				// zou bij aankomst de maximale capaciteit overschreven worden.
						, 15 + getNbPatients() * 2 * 3 - nbInStock - previousOrderAmount));
		return amount;
	}
	
	/**
	 * Getter voor het aantal niet ontslagen patienten op de campus
	 * 
	 * @return nbPatients
	 *         Het aantal niet ontslagen patienten op de campus
	 */
	private int getNbPatients(){
		return campus.getPatientRepository()
		.getNonDischargedPatients().size();
	}
	/**
	 * Deze methode verwijdert alle meals die vervallen voor een gegeven tijdstip.
	 * Deze methode mag alleen maar opgeroepen worden door mealRemovalEvent.
	 * 
	 * @param simulatedNow
	 *        Het tijdstip waartegen de vervaldatum vergeleken moet worden
	 */
	public void removeExpiringMeals(TimeStamp executionTime) {
		this.now=executionTime;
		for (int i = 0; i < meals.size(); i++) {
			if (((Meal) meals.get(i)).
					getExpirationDate().before(executionTime)) {
				meals.remove(meals.get(i--));
			}
		}
	}
	
	/**
	 * Methode die het eetgedrag van de patienten simuleert.
	 * Voor elke patient wordt een maaltijd verwijderd uit het warenhuis.
	 */
	public void patientsEat() {
		for(int i=0;i<getNbPatients();i++)
			removeWarehouseItem();
	}
	/**
	 * Maakt een dagelijkse midnacht bestelling
	 * 
	 * @param executionTime
	 * 			Datum dat de order wordt uitgevoerd
	 */
	public void makeMidnightOrder(TimeStamp executionTime) {
		this.now=executionTime;
		int orderAmount=calcDailyOrderAmount();
		TimeStamp expirationDate = TimeStamp.addedToTimeStamp(TimeDuration.days(14), executionTime);
		List<WarehouseItem> items = new ArrayList<WarehouseItem>();
		for (int i=0; i<orderAmount;i++) {
			Meal meal = new Meal(expirationDate);
			items.add(meal);
		}
		makeOrder(items);
		this.previousOrderAmount=orderAmount;
	}
	
	/**
	 * Een methode om te bepalen of er nog genoeg maaltijden zijn 
	 * om een nieuwe patient in het ziekenhuis op te nemen. Er moet
	 * genoeg voedsel zijn voor 2 dagen, dat zijn 6 maaltijden.
	 * 
	 * @return true 
	 *         Als er nog genoeg eten is
	 *         false
	 *         Als er te weinig eten is
	 */
	public boolean hasEnoughFood() {
		int nbNonDischargedPatients = campus.getPatientRepository().getNonDischargedPatients().size();
		int nbExtraFood = this.getStockSize() - nbNonDischargedPatients*6;
		
		return (nbExtraFood >= 6);
	}
	@Override
	public void cancelAllOrders() {
		for (StockOrder order : orders) {
			setChanged();
			notifyObservers(new Pair<StockOrder, ObserverAction>(order, ObserverAction.REMOVE));
		}
		orders.clear();
	}
}