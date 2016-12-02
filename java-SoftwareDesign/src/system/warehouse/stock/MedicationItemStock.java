package system.warehouse.stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import system.patients.ObserverAction;
import system.time.Time;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.util.BoundedQueue;
import system.util.Pair;
import system.warehouse.CompositeType;
import system.warehouse.MedicationItem;
import system.warehouse.MedicationItemType;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;

/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public class MedicationItemStock extends CompositeStock {
	/**
	 * Constante die de maximum capaciteit aan medicijnen van het magazijn voorstelt
	 */
	public static final int MAX_CAPACITY_MEDICATION_ITEMS = 10;
	/**
	 * De medicijnen die opgeslagen zijn in het ziekenhuis
	 */
	private Map<MedicationItemType, BoundedQueue<MedicationItem>> medicationItems;
	/**
	 * De geplaatste maar nog niet ontvangen orders voor medicijnen
	 */
	private Map<MedicationItemType, SortedSet<StockOrder>> medicationItemOrders;
	
	/**
	 * Initialisatie van de voorraad aan medicijnen
	 * 
	 * @param campus
	 *        De campus waartoe het magazijn waarin de voorraad wordt bewaard, behoort
	 */
	public MedicationItemStock(Time hospitalTime) {
		super(hospitalTime,StockType.MEDICATION_ITEM);
		
		createMedicationItemStacks();
		createMedicationItemOrderSets();
		
		this.fillStock();
	}

	/**
	 * Methode om de Map medicationItems aan te maken en op te vullen
	 */
	private void createMedicationItemStacks() {
		this.medicationItems = new EnumMap<MedicationItemType, BoundedQueue<MedicationItem>>(
				MedicationItemType.class);

		this.medicationItems.put(MedicationItemType.ASPIRIN,
				new BoundedQueue<MedicationItem>(MAX_CAPACITY_MEDICATION_ITEMS));
		this.medicationItems.put(MedicationItemType.VITAMINS, 
				new BoundedQueue<MedicationItem>(MAX_CAPACITY_MEDICATION_ITEMS));
		this.medicationItems.put(MedicationItemType.ACTIVATED_CARBON, 
				new BoundedQueue<MedicationItem>(MAX_CAPACITY_MEDICATION_ITEMS));
		this.medicationItems.put(MedicationItemType.SLEEPING_TABLETS,
				new BoundedQueue<MedicationItem>(MAX_CAPACITY_MEDICATION_ITEMS));
		this.medicationItems.put(MedicationItemType.MISC, 
				new BoundedQueue<MedicationItem>(MAX_CAPACITY_MEDICATION_ITEMS));
	}
	
	/**
	 * Methode om de Map medicationItemOrders aan te maken en op te vullen
	 */
	private void createMedicationItemOrderSets() {
		this.medicationItemOrders = new EnumMap<MedicationItemType, SortedSet<StockOrder>>(MedicationItemType.class);

		this.medicationItemOrders.put(MedicationItemType.ASPIRIN,
				new TreeSet<StockOrder>());
		this.medicationItemOrders.put(MedicationItemType.VITAMINS,
				new TreeSet<StockOrder>());
		this.medicationItemOrders.put(MedicationItemType.ACTIVATED_CARBON,
				new TreeSet<StockOrder>());
		this.medicationItemOrders.put(MedicationItemType.SLEEPING_TABLETS,
				new TreeSet<StockOrder>());
		this.medicationItemOrders.put(MedicationItemType.MISC,
				new TreeSet<StockOrder>());
	}

	/**
	 * Methode om een medicatie item toe te voegen aan de map van medicatie items.
	 */
	@Override
	public void addWarehouseItem(WarehouseItem item) {
		MedicationItem medicationItem = (MedicationItem) item;
		MedicationItemType type = medicationItem.getType();
		medicationItems.get(type)
				.enqueue(medicationItem);
	}
	

	/**
	 * Een methode om een medicijn uit de voorraad te halen
	 * 
	 * @pre aantal medicatie items in warenhuis > 0
	 * 		| medicationItems > 0
	 * 
	 * @return item
	 *         als er nog medicijnen van het opgevraagde type zijn
	 *         null
	 *         als er geen medicijnen van het opgevraagde type meer zijn
	 */
	@Override
	public WarehouseItem removeWarehouseItem(CompositeType type) {
		BoundedQueue<MedicationItem> items = medicationItems.get(type);

		if (!items.isEmpty()) {
			WarehouseItem item = items.dequeue();

			int nbInStock = items.size();
			if (nbInStock < MAX_CAPACITY_MEDICATION_ITEMS / 2) {
				int amount = MAX_CAPACITY_MEDICATION_ITEMS - nbInStock;
				amount -= getNbItemsAlreadyOrdered(type);

				if (amount > 0) {
					List<WarehouseItem> warehouseItems = new ArrayList<WarehouseItem>();
					TimeStamp expirationDate = TimeStamp.addedToTimeStamp(TimeDuration.days(14), getNow());
					
					while (amount-- > 0) {
						MedicationItem medicationItem = new MedicationItem(expirationDate, type);
						warehouseItems.add(medicationItem);
					}
					
					makeOrder(type, warehouseItems);
				}
			}
			
			return item;
		} else {
			return null;
		}
	}
	/**
	 * Doet alle nodige dingen voor het bestellen van MedicationItems
	 * @param items de items die besteld moeten worden
	 * @param type het type van medicationItems dat besteld moet worden
	 */
	private void makeOrder(CompositeType type,
			List<WarehouseItem> warehouseItems) {
		StockOrder order = new StockOrder(this, getNow(), warehouseItems);
		medicationItemOrders.get(type).add(order);
		setChanged();
		notifyObservers(new Pair<StockOrder, ObserverAction>(order, ObserverAction.ADD));
	}

	/**
	 * Een methode om de stock te vullen met medicatie items.
	 */
	@Override
	public void fillStock() {
		TimeStamp expirationDate = TimeStamp.addedToTimeStamp(TimeDuration.days(2*365), getNow());	

		for(MedicationItemType type : MedicationItemType.values()){
			for (int i = 0; i < MAX_CAPACITY_MEDICATION_ITEMS; i++) {
				medicationItems.get(type).enqueue(new MedicationItem(expirationDate, type));
			}
		}
	}
	

	/**
	 * Getter voor het aantal medicijnen van een bepaald type in voorraad
	 * 
	 * @param type
	 *        Het type medicijn
	 * @return nbMedicationItems
	 *         Het aantal medicijnen van een bepaald type
	 */
	@Override
	public int getStockSize(CompositeType type) {
		return medicationItems.get(type).size();
	}

	/**
	 * Een methode om een medicatie item order van de stock te verwijderen.
	 */
	@Override
	public void removeStockOrder(StockOrder order) {
		for (SortedSet<StockOrder> stack : medicationItemOrders.values()) {
			stack.remove(order);
		}
	}
	
	/**
	 * Getter voor alle orders voor medicijnen van een bepaald type
	 * 
	 * @param type
	 *        Het type medicijn waarvan de orders worden gevraagd
	 * @return alle orders voor medicijnen van het gevraagde type
	 */
	@Override
	public SortedSet<StockOrder> getOrders(CompositeType type) {
		return Collections.unmodifiableSortedSet(medicationItemOrders.get(type));
	}

	/**
	 * Getter voor de orders van medicijnen die gearriveerd moeten zijn 
	 * 
	 * @return orders
	 *         De orders van medicijnen die gearriveerd moeten zijn
	 */
	@Override
	public SortedSet<StockOrder> getAllArrivedOrders() {
		SortedSet<StockOrder> orders = new TreeSet<StockOrder>();

		for (MedicationItemType type : MedicationItemType.values())
			for (StockOrder order : getArrivedOrdersByType(type))
				orders.add(order);

		return orders;
	}

	/**
	 * De primitieve operatie van de ConcreteClass van het Template Method patroon
	 * 
	 * @param before
	 *        De vorige huidige tijd
	 */
	@Override
	protected void updateStockFromTime(TimeStamp before) {
		removeExpiredMedicationItems();
	}
	
	/**
	 * Een methode om de vervallen medicijnen uit het magazijn te verwijderen
	 */
	private void removeExpiredMedicationItems() {
		for (MedicationItemType type : MedicationItemType.values()){
			removeExpiredMedicationItems(medicationItems.get(type));
		}
	}
	
	/**
	 * Hulpmethode van removeExpiredMedicationItems() die controleert of alle medicijnen van een opgegeven collectie niet vervallen zijn
	 * 
	 * @param items
	 *        De collectie medicijnen waarvan gecontroleerd moet worden of alle medicijnen niet vervallen zijn
	 *         
	 */
	private void removeExpiredMedicationItems(BoundedQueue<MedicationItem> items) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getExpirationDate().before(getNow())) {
				items.remove(items.get(i--));
			}
		}
	}
	
	/**
	 * Een methode om te medicationItemsbepalen of er nog medicijnen zijn van een bepaald type
	 * 
	 * @param medicationItemTypes
	 *        De types medicijnen waarvoor er gecontroleerd dient te worden of er nog medicijnen zijn
	 * @return true 
	 *         Als er nog medicijnen van de opgegeven types zijn
	 *         false
	 *         Als er geen medicijnen van de opgegeven types meer zijn
	 */
	public <T extends CompositeType> boolean hasMedicationItems(List<T> types) {
		for (T type : types) 
			if (this.getStockSize(type) <= 0)
				return false;
		
		return true;
	}

	@Override
	public void cancelAllOrders() {
		for (SortedSet<StockOrder> orders : medicationItemOrders.values()) {
			for (StockOrder order : orders) {
				setChanged();
				notifyObservers(new Pair<StockOrder, ObserverAction>(order, ObserverAction.REMOVE));
			}
			orders.clear();
		}
	}
}