package system.time;

import annotations.SystemAPI;
/**
 * Deze klasse stelt een hoeveelheid van tijd voor.
 * Deze klasse is immutable.
 * @author Swop Team 10
 *
 */
@SystemAPI
public class TimeDuration implements Comparable<TimeDuration> {

	private final long milliseconds;
	
	private static long 	MILLISECOND = 1,
	SECOND = MILLISECOND*1000,
	MINUTE = SECOND*60,
	HOUR = MINUTE*60,
	DAY = HOUR*24;
	
	@SystemAPI
	private TimeDuration(long milliseconds) {
		this.milliseconds = milliseconds;
	}
	
	@SystemAPI
	public static TimeDuration milliseconds(long amount){
		return new TimeDuration(MILLISECOND*amount);
	}
	
	@SystemAPI
	public static TimeDuration seconds(long amount){
		return new TimeDuration(SECOND*amount);
	}
	
	@SystemAPI
	public static TimeDuration minutes(long amount){
		return new TimeDuration(MINUTE*amount);
	}
	
	@SystemAPI
	public static TimeDuration hours(long amount){
		return new TimeDuration(HOUR*amount);
	}
	
	@SystemAPI
	public static TimeDuration days(long amount){
		return new TimeDuration(DAY*amount);
	}
	
	@SystemAPI
	public TimeDuration addDuration(TimeDuration otherDuration){
		return new TimeDuration(this.milliseconds+otherDuration.milliseconds);
	}
	
	@SystemAPI
	public static TimeDuration addDurations(TimeDuration duration1, TimeDuration duration2){
		return new TimeDuration( duration1.milliseconds+duration2.milliseconds);
	}
	
	@SystemAPI
	public double getMilliseconds(){
		return ((double)this.milliseconds/MILLISECOND);
	}
	
	@SystemAPI
	public double getSeconds(){
		return ((double)this.milliseconds/SECOND);
	}
	
	@SystemAPI
	public double getMinutes(){
		return ((double)this.milliseconds/MINUTE);
	}
	
	@SystemAPI
	public double getHours(){
		return ((double)this.milliseconds/HOUR);
	}
	
	@SystemAPI
	public double getDay(){
		return ((double)this.milliseconds/DAY);
	}

	@Override
	@SystemAPI
	public int compareTo(TimeDuration otherDuration) {
		if (this.milliseconds == otherDuration.milliseconds)
			return 0;
		else if (this.milliseconds > otherDuration.milliseconds)
			return 1;
		else
			return -1;
	}
	
	@Override
	public int hashCode() {
		return (int) milliseconds;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.getClass()!=obj.getClass()){
			return false;
		}
		else{
			if(this.compareTo((TimeDuration) obj)==0){
				return true;
			}
			else return false;
		}
	}

	@SystemAPI
	public TimeDuration multiply(int multiplicator) {
		return new TimeDuration(multiplicator*this.milliseconds);
	}
}
