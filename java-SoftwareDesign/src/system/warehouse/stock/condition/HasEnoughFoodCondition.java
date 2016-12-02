package system.warehouse.stock.condition;

import system.util.Condition;
import system.warehouse.stock.MealStock;
import system.warehouse.stock.Stock;
import system.warehouse.stock.StockType;

/**
 * Kijkt na of er genoeg voedsel is in de Mealstock
 */
public class HasEnoughFoodCondition implements Condition {

	@Override
	public boolean check(Object arg) {
		Stock stock ;
		if (arg instanceof Stock)
			stock = (Stock) arg;
		else 
			return false;
		
		if (stock.getType() != StockType.MEAL)
			return false;
		
		MealStock mealStock = (MealStock) stock;
		
		return mealStock.hasEnoughFood();
	}
}
