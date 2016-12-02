package system.util;

/**
 * Interface die een alarm voorstelt in het systeem.
 */
public interface Alarm {
	public void notifyAlarm(Object arg);
	public void falseAlarm();
}
