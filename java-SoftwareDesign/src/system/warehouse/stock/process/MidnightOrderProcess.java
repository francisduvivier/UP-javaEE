package system.warehouse.stock.process;

import system.time.TimeStamp;
import system.warehouse.stock.MealStock;
import system.warehouse.stock.Stock;
import system.warehouse.stock.StockType;

public class MidnightOrderProcess implements StockProcess {
	private final TimeStamp timeStamp;
	
	public MidnightOrderProcess(TimeStamp timeStamp) { 
		this.timeStamp = timeStamp;
	}
	
	@Override
	public void process(Stock stock) {
		if (stock.getType() != StockType.MEAL)
			return;

		MealStock mealStock = (MealStock) stock;
		
		mealStock.makeMidnightOrder(this.timeStamp);
	}
	
}
