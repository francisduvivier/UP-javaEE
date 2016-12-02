package system.warehouse.stock.process;

import system.warehouse.stock.MealStock;
import system.warehouse.stock.Stock;
import system.warehouse.stock.StockType;

public class PatientsEatProcess implements StockProcess {

	@Override
	public void process(Stock stock) {
		if (stock.getType() != StockType.MEAL)
			return;

		MealStock mealStock = (MealStock) stock;
		
		mealStock.patientsEat();
	}
	
}
