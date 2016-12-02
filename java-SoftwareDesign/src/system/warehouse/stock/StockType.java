package system.warehouse.stock;

public enum StockType {
	MEAL(false),
	PLASTER(false),
	MEDICATION_ITEM(true);
	
	private final boolean isComposite;
	
	private StockType(boolean isComposite) {
		this.isComposite = isComposite;
	}
	
	public final boolean isComposite() {
		return this.isComposite;
	}
}
