package system.warehouse;

import system.warehouse.stock.Stock;
import system.warehouse.stock.StockList;

/**
 * Een klasse die een warenhuis voorstelt
 * 
 * @author SWOP Team 10
 */
public class Warehouse {
	private final StockList stockList;
	
	public Warehouse(StockList stockList) {		
		this.stockList = stockList;
	}
	
	/**
	 * Een methode om een order te verwijderen
	 * 
	 * @param order
	 *        Het te verwijderen order
	 */
	public void removeOrder(StockOrder order) {
		for (Stock stock : this.stockList.getStocks()) {
			stock.removeStockOrder(order);
		}
	}
	
	/**
	 * Een methode om de stocklijst op te vragen
	 * 
	 * @return stockList
	 * 			Lijst die de stock van het warenhuis bevat
	 */
	public StockList getStockList() {
		return this.stockList;
	}
}