package system.scheduling;

import annotations.SystemAPI;

/**
 * Een enumerator voor de prioriteit van een event.
 * Deze kan prioriteit kan dringend of normaal zijn, of geen prioriteit.
 */

@SystemAPI
public enum Priority {
	@SystemAPI
	URGENT(1),
	@SystemAPI
	NORMAL(2),
	@SystemAPI
	NO_PRIORITY(0);
	
	private int value;
	
	private Priority(int value) {
		this.value = value;
	}
	
	private int getValue() {
		return this.value;
	}
	
	public int compareValue(Priority priority) {
		return priority.getValue() - this.getValue();
	}
}
