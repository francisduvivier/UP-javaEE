package system.warehouse.stock.condition;


import java.util.List;

import system.util.Condition;
import system.warehouse.CompositeType;
import system.warehouse.stock.MedicationItemStock;
import system.warehouse.stock.Stock;
import system.warehouse.stock.StockType;

/**
 * Kijkt na of een bepaalde medication item in voldoende mate aanwezig is in de 
 * MedicationItemStock 
 */
public class HasMedicationItemsCondition implements Condition {
	private final List<? extends CompositeType> items ;
	
	public <T extends CompositeType> HasMedicationItemsCondition(List<T> items) {
		this.items = items;
	}
	
	@Override
	public boolean check(Object arg) {
		Stock stock ;
		if (arg instanceof Stock)
			stock = (Stock) arg;
		else 
			return false;
		
		if (stock.getType() != StockType.MEDICATION_ITEM)
			return false;
		
		MedicationItemStock medicationItemStock = (MedicationItemStock) stock;
		
		return medicationItemStock.hasMedicationItems(items);
	}
	
}
