package system.scheduling;


public interface Undoable extends ScheduleEvent {
	/** 
	 * Methode die een actie die op een patient moet uitgevoerd worden annuleert.
	 */
	public void cancel();
	
	/**
	 * Methode die een actie die op een patient moest uitgevoerd worden maar geannuleerd
	 * werd, weer plant.
	 */
	public void redo();	
}
