package client;

import annotations.SystemAPI;

/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public interface IStockOrder {
	/**
	 * Getter voor het aantal bestelde items
	 * 
	 * @return amount
	 *         Het aantal bestelde items als int
	 */
	@SystemAPI
	int getAmount();
}
