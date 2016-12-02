package system.warehouse.stock.process;

import system.time.TimeStamp;
import system.warehouse.stock.MealStock;
import system.warehouse.stock.Stock;
import system.warehouse.stock.StockType;

public class RemoveExpiringMealsProcess implements StockProcess {
	private final TimeStamp timeStamp;
	
	public RemoveExpiringMealsProcess(TimeStamp timeStamp) { 
		this.timeStamp = timeStamp;
	}
	
	@Override
	public void process(Stock stock) {
		if (stock.getType() != StockType.MEAL)
			return;

		MealStock mealStock = (MealStock) stock;

		mealStock.removeExpiringMeals(this.timeStamp);
	}
}
