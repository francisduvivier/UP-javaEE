package system.staff;

import java.util.Calendar;

import system.time.TimeDuration;
import system.time.TimePeriod;
import system.time.TimeStamp;
import annotations.SystemAPI;
/**
 * 
 * @author Swop Team 10
 *
 */
public class Shift {
	private final TimePeriod timePeriod;
	
	/**
	 * Initialisatie van een Shift object
	 * 
	 * @param timePeriod
	 * 			De tijdsperiode waartussen de shift plaatsvindt
	 */
	public Shift(TimePeriod timePeriod) {
		if (!isValidShift(timePeriod)) 
			throw new IllegalArgumentException("een shift mag maar ��n dag bestrijken");
		
		this.timePeriod = timePeriod;
	}
	
	/**
	 * Methode om te controleren of de staffmember werkt binnen de gegeven tijdsperiode
	 * 
	 * @param timePeriod
	 * 			Tijdsperiode waarvan gecontroleerd moet worden of er binnen gewerkt wordt
	 * 
	 * @return True als gewerkt wordt binnen gegeven tijdsperiode
	 * 			| result == getTimePeriod().encapsulates(getTimePeriod().shiftTimePeriod(timePeriod))
	 */
	public boolean isWorking(TimePeriod timePeriod) {
		return getTimePeriod().encapsulates(getTimePeriod().shiftTimePeriod(timePeriod));
	}
	
	/**
	 * Getter voor de tijdsperiode van de shift
	 * 
	 * @return de tijdsperiode van de shift
	 */
	private TimePeriod getTimePeriod() {
		return this.timePeriod;
	}
	
	/**
	 * Methode om te controleren of de shift wel op een geldig moment valt
	 * 
	 * @param timePeriod
	 * 			Tijdsperiode waarvan gecontroleerd wordt of hij wel geldig is
	 * @return True als het een geldig moment is
	 */
	private boolean isValidShift(TimePeriod timePeriod) {
		return timePeriod.getDuration().compareTo(TimeDuration.days(1))<=0 && 
			(timePeriod.getBegin().get(Calendar.DAY_OF_MONTH) == timePeriod.getEnd().get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * Deze methode geeft de doorsnede van de twee shifts
	 * 
	 * @param otherShift
	 * 			De shift waarmee verbonden moet worden
	 * @return e nieuw bekomen shift waar beide shifts verbonden zijn
	 */
	public Shift intersect(Shift otherShift) {
		TimePeriod intersect = this.getTimePeriod().intersect(this.getTimePeriod().
				shiftTimePeriod(otherShift.getTimePeriod()));
		
		if (intersect == null)
			return null;
		else 
			return new Shift(intersect);
	}
	
	/**
	 * Methode om de volgende beschikbare tijdsperiode te vinden a.d.h.v. gegeven tijdsperiode
	 * 
	 * @param timePeriod
	 * 			Tijdsperiode waarvoor een volgende beschikbare tijdsperiode gevonden moet worden
	 * @return Volgende beschikbare tijdsperiode
	 */
	public TimePeriod nextAvailable(TimePeriod timePeriod) {
		if (timePeriod.longerThan(getTimePeriod()))
			return null;
		
		if (isWorking(timePeriod))
			return timePeriod;
		
		TimePeriod shifted = getTimePeriod().shiftTimePeriod(timePeriod);
		
		if (shifted.getBegin().before(getTimePeriod().getBegin())) {
			TimeStamp shiftTo = timePeriod.shiftTimePeriod(getTimePeriod()).getBegin();
			
			return TimePeriod.shiftBegin(timePeriod, shiftTo);
		} else {
			TimeStamp shiftTo = TimeStamp.addedToTimeStamp(TimeDuration.days(1), 
					timePeriod.shiftTimePeriod(getTimePeriod()).getBegin());
			
			return TimePeriod.shiftBegin(timePeriod, shiftTo);
		}
	}
	
	/**
	 * Twee shifts zijn gelijk als hun tijdsperiodes gelijk zijn
	 * 
	 * @param o
	 * 			shift waarmee vergeleken moet worden
	 * @return True als de shifts gelijk zijn
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) 
			return true;
		if (!(o instanceof Shift))
			return false;
		
		Shift s = (Shift) o ;
		
		return getTimePeriod().
				shiftTimePeriod(s.getTimePeriod()).equals(getTimePeriod());
	}
	
	@Override
	@SystemAPI
	public String toString() {
		return "Shift - " + timePeriod.getBegin().formatDate("HH:mm") + " to "+
							timePeriod.getEnd().formatDate("HH:mm");
	}
}
