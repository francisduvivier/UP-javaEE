package system.controllers;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import client.IStockOrder;

import system.exceptions.IllegalAccessException;
import system.repositories.StaffType;
import system.time.TimeStamp;
import system.warehouse.MedicationItemType;
import system.warehouse.StockOrder;
import system.warehouse.Warehouse;
import system.warehouse.stock.StockType;
import annotations.SystemAPI;

/**
 * @Invar de sessionController is niet null.
 */
@SystemAPI
public class WarehouseManagerController extends StaffController{

	/**
	 * constructor voor een WareHouseController
	 * 
	 * @param 	sessionController
	 *         	De sessionController van de sessie waarin de WarehouseController
	 *          ook gebruikt wordt.
	 */
	@SystemAPI
	public WarehouseManagerController(SessionController sessionController) {
		super(sessionController);
		}

	/**
	 * Methode om te verzekeren dat de ingelogde gebruiker
	 * een warehouse manager is.
	 * 
	 * @return 	true als de user access in de sessie controller nog klopt.
	 * 			|	result = sessionController.hasValidAccess(StaffType.WAREHOUSE_MANAGER)
	 */
	@Override
	protected boolean hasValidAccess() {
		return sessionController.hasValidAccess(StaffType.WAREHOUSE_MANAGER);
	}
	
	/**
	 * 
	 * @return 	Gesorteerde set met daarin de gearriveerde meal orders
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public SortedSet<IStockOrder> getArrivedMealOrders() throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(this.getWarehouse().getStockList().getDefaultStock(StockType.MEAL).getArrivedOrders());
		
		return Collections.unmodifiableSortedSet(orders);
	}

	/**
	 * Deze methode returnt een List met de laatste X niet-ontvangen
	 * Meal-bestellingen. Een WarehouseItem is niet ontvangen wanneer zijn
	 * attribuut "arrived" op "false" staat. We beginnen achteraan in de
	 * ArrayList "getStock" om de laatste bestellingen te hebben.
	 * 
	 * @param amount
	 * @return de laatste X niet-ontvangen Meal-bestellingen
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 * @throws	IllegalARgumentException
	 * 			Als het aantal opgevraagde mealorders lager is dan 1
	 */
	@SystemAPI
	public SortedSet<IStockOrder> getLatestMealOrders(int amount)
			throws IllegalAccessException, IllegalArgumentException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		if (amount <= 0)
			throw new IllegalArgumentException(
					"Amount has to be greater than 0");
		
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(this.getWarehouse().getStockList().getDefaultStock(StockType.MEAL).getLatestOrders(amount));
		
		return Collections.unmodifiableSortedSet(orders);
	}
	
	/**
	 * 
	 * @return Gesorteerde set met daarin alle gearriveerde medicatie-items van de stock.
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public SortedSet<IStockOrder> getAllArrivedMedicationItemOrders() throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(this.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getAllArrivedOrders());
		
		return Collections.unmodifiableSortedSet(orders);
	}
	
	/**
	 * Een methode om een gesorteerde set met gearriveerde medicatie items van gegeven type uit de stock
	 * op te vragen.
	 * 
	 * @param	type
	 * 			Medicatie item waar we een set van willen
	 * @return	Een gesorteerde set van stock orders van gewenst type
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public SortedSet<IStockOrder> getArrivedMedicationItemOrders(MedicationItemType type) throws IllegalAccessException {
		this.hasValidAccess();
		
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(this.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getArrivedOrdersByType(type));
		
		return Collections.unmodifiableSortedSet(orders);
	}

	/**
	 * Analoog aan getLatestMealOrders
	 * 
	 * @param amount
	 * @return de laatste X niet-ontvangen Medication-bestellingen
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 * @throws	IllegalArgumentException
	 * 			Als het aantal opgevraagde orders lager of gelijk aan nul is
	 */
	@SystemAPI
	public SortedSet<IStockOrder> getLatestMedicationItemOrders(
			MedicationItemType type, int amount) throws IllegalAccessException,
			IllegalArgumentException {
		this.hasValidAccess();

		if (amount <= 0)
			throw new IllegalArgumentException(
					"Amount has to be greater than 0");
		
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(this.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getLatestOrders(amount, type));
		
		return Collections.unmodifiableSortedSet(orders);
	}
	
	/**
	 * 
	 * @return Gesorteerde set met daarin de gearriveerde plaster orders
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public SortedSet<IStockOrder> getArrivedPlasterOrders() throws IllegalAccessException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(this.getWarehouse().getStockList().getDefaultStock(StockType.PLASTER).getArrivedOrders());
		
		return Collections.unmodifiableSortedSet(orders);
	}

	/**
	 * Analoog aan getLatestMealOrders
	 * 
	 * @param amount
	 * @return de laatste X niet-ontvangen Plaster-bestellingen
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 * @throws	IllegalArgumentException
	 * 			Als het aantal opgevraagde orders lager of gelijk aan nul is
	 */
	@SystemAPI
	public SortedSet<IStockOrder> getLatestPlasterOrders(int amount)
			throws IllegalAccessException, IllegalArgumentException {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		if (amount <= 0)
			throw new IllegalArgumentException(
					"Amount has to be greater than 0");
		
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(this.getWarehouse().getStockList().getDefaultStock(StockType.PLASTER).getLatestOrders(amount));
		
		return Collections.unmodifiableSortedSet(orders);
	}
	
	/**
	 * Methode om een order in het correcte magazijn te plaatsen en te
	 * verwijderen uit de order repository.
	 * 
	 * @param 	selectedOrder
	 * 			Order tot betrekking
	 * @param 	expirationDate
	 * 			Vervaldatum van de order
	 * 
	 * @throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	@SystemAPI
	public void processArrivedOrder(IStockOrder selectedOrder, TimeStamp expirationDate){
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		StockOrder order = (StockOrder) selectedOrder;
		order.processArrival(expirationDate);
		getWarehouse().removeOrder(order);
	}
	
	/**
	 * Geeft de hoeveelheid voedsel in de voedselvoorraad.
	 * 
	 * @return
	 * 			De hoeveelheid voedsel.
	 * 
	 * @throws	NullPointerException
	 * 			Als er geen voedselvoorraad is
	 */
	@SystemAPI
	public int getNbMealInStock() {
		return getCurrentCampus().getWarehouse().getStockList().getDefaultStock(StockType.MEAL).getStockSize();
	}
	
	/**
	 * @return de OrderRepository van de Hospital
	 * 
	 * throws	IllegalAccessException
	 * 			Als iemand zonder toegang de methode uitvoert
	 */
	private Warehouse getWarehouse() {
		if (!this.hasValidAccess()) 
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");

		return sessionController.getCurrentCampus().getWarehouse();
	}
}
