package system.time;

import java.util.Observable;

/**
 * Deze klasse stelt de tijd voor.
 *
 */

public class Time extends Observable {
	private TimeStamp now;
	
	/**
	 * Constructor van Time.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 */
	public Time(int year, int month, int day, int hour, int minute) {
		this.now = new TimeStamp(year,month,day,hour,minute);
		setChanged();
		notifyObservers(this.now);
	}
	
	/**
	 * Een methode om de tijd op te vragen.
	 * 
	 * @return now
	 * 		   De tijd
	 */
	public TimeStamp getTime() {
		return this.now;
	}
	
	/**
	 * Een methode om de tijd in te stellen.
	 * 
	 * @param toTimeStamp
	 * 		  De gewenste tijd
	 */
	public void setTime(TimeStamp toTimeStamp) {
		this.now = toTimeStamp;
		setChanged();
		notifyObservers(this.now);
	}
}
