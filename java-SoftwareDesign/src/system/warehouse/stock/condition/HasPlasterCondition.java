package system.warehouse.stock.condition;

import system.util.Condition;

import system.warehouse.stock.PlasterStock;
import system.warehouse.stock.Stock;
import system.warehouse.stock.StockType;

/**
 * Kijkt na of de plaasterconditie voldaan is op de plasterstock
 */
public class HasPlasterCondition implements Condition {

	@Override
	public boolean check(Object arg) {
		Stock stock ;
		if (arg instanceof Stock)
			stock = (Stock) arg;
		else 
			return false;
		
		if (stock.getType() != StockType.PLASTER) 
			return false;
		
		PlasterStock plasterStock = (PlasterStock) stock;
		
		return plasterStock.hasPlaster();
	}
	
}
