package system.warehouse.stock;

import java.util.ArrayList;
import java.util.List;

import system.campus.Campus;
import system.util.Condition;
import system.warehouse.stock.process.StockProcess;

public class StockList {
	/**
	 * De voorraden die in het magazijn worden bewaard 
	 */
	private final List<Stock> stocks;
	
	public StockList(Campus campus) {
		this.stocks = new ArrayList<Stock>();
		this.stocks.add(new PlasterStock(campus.getBigHospital().getHospitalTime()));
		this.stocks.add(new MedicationItemStock(campus.getBigHospital().getHospitalTime()));
		this.stocks.add(new MealStock(campus));
	}
	
	/**
	 * Getter voor alle voorraden die in het magazijn worden bewaard
	 * 
	 * @return stocks
	 *         Alle voorraden in het magazijn
	 */
	public List<Stock> getStocks() {
		return this.stocks;
	}
	
	/**
	 * 
	 * @param type
	 * 			een type stock (moet een gewone stock zijn, geen samengestelde)
	 * @return
	 * 			de gewone stock van bovenstaand type
	 */
	public DefaultStock getDefaultStock(StockType type) {	
		if (type.isComposite()) 
			return null;
		for (Stock stock : stocks) {
			if (stock.getType() == type)
				return (DefaultStock) stock;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param type	
	 * 			een type samengestelde stock
	 * @return
	 * 			De samengestelde stock van bovenstaand type
	 */
	public CompositeStock getCompositeStock(StockType type) {
		if (!type.isComposite())
			return null;
		for (Stock stock : stocks) {
			if (stock.getType() == type)
				return (CompositeStock) stock;
		}
		
		return null;
	}
	
	/**
	 * Voert meegegeven process uit op de juiste stock(s)
	 * 
	 * @param stockProcess
	 * 			het process dat moet uitgevoerd worden
	 */
	public void processOnStock(StockProcess stockProcess) {
		for (Stock stock : this.stocks) {
			stockProcess.process(stock);
		}
	}
	
	/**
	 * Checkt de meegegeven conditie op de juiste stock(s)
	 * 
	 * @param stockCondition
	 * 			de conditie moet uitgevoerd worden
	 * @return
	 * 			waar als de meegegeven conditie waar is
	 */
	public boolean conditionIsTrue(Condition stockCondition) {
		for (Stock stock : this.stocks) {
			if (stockCondition.check(stock))
				return true;
		}
		return false;
	}
}
