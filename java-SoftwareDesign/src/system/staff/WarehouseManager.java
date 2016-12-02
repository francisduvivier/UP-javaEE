package system.staff;

/**
 * Een object van deze klasse stelt een Warehouse Manager voor.
 * Een Warehouse Manager kan voorraden ontladen en plaatsen in het warenhuis,
 * hij kan bovendien producten die vervallen zijn verwijderen.
 * Deze klasse is een subklasse van StaffMember.
 */

import system.repositories.StaffType;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een magazijnbeheerder voor
 * 
 * @author SWOP Team 10
 */
public class WarehouseManager extends StaffMember {

	/**
	 * Constructor voor WarehouseManager
	 * @param name
	 */
	public WarehouseManager(String name) {
		super(name, StaffType.WAREHOUSE_MANAGER);
	}

	/**
	 * Een toString voor WarehouseManager.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Warehouse Manager: " + super.toString();
	}
}
