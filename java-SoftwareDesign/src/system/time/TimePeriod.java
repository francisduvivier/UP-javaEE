package system.time;

import java.util.Calendar;

import annotations.SystemAPI;

/**
 * 
 * Alle periodes die gescheduled worden, worden bijgehouden als TimePeriod.
 * Deze klasse bestaat uit 2 tijdsmomenten. Het begin en het einde.
 *
 */
@SystemAPI
public final class TimePeriod implements Comparable<TimePeriod> {
	private final TimeStamp begin, end;
	
	/**
	 * Constructor van TimePeriod.
	 * 
	 * @param begin
	 * 		  Het begin van de tijdsperiode
	 * @param end
	 * 		  Het einde van de tijdsperiode.
	 */
	@SystemAPI
	public TimePeriod(TimeStamp begin, TimeStamp end) {
		this.begin = begin;
		this.end = end;
	}
	
	/**
	 * Een methode om een tijdsperiode de krijgen tussen een tijdstip en een tijdsperiode.
	 * 
	 * @param timePeriod
	 * 		  De tijdsperiode waaraan geconcateneerd moet worrden
	 * @param shiftTo
	 * 		  Het te beginnen tijdstip
	 * @return tijdsperiode van tijdstip tot tijdsperiode
	 */
	public static TimePeriod shiftBegin(TimePeriod timePeriod, TimeStamp shiftTo) {
	TimeDuration diff = timePeriod.getDuration();
		TimeStamp 	begin = shiftTo,
					end = TimeStamp.addedToTimeStamp(diff, begin);
		return new TimePeriod(begin, end);
	}
	
	/**
	 * Een methode om het begin van een tijdsperiode op te vragen.
	 * 
	 * @return the begin
	 */
	@SystemAPI
	public TimeStamp getBegin() {
		return begin;
	}

	/**
	 * Een methode om het einde van een tijdsperiode om te vragen.
	 * 
	 * @return the end
	 */
	@SystemAPI
	public TimeStamp getEnd() {
		return end;
	}
	
	/**
	 * Een methode om te controleren of een tijdsperiode overlapt met een andere tijdsperiode.
	 * 
	 * @param otherTimePeriod De mogelijks overlappende tijdsperiode
	 * @return	true als de tijdsperiode overlapt met deze
	 */
	public boolean interferes(TimePeriod otherTimePeriod) { // TODO condities
		if (otherTimePeriod.getBegin().compareTo(this.getBegin()) <= 0 &&
				otherTimePeriod.getEnd().compareTo(this.getBegin()) > 0)
			return true;
		if (otherTimePeriod.getBegin().compareTo(this.getBegin()) >= 0 &&
				otherTimePeriod.getBegin().compareTo(this.getEnd()) < 0)
			return true;
		return false;
	}
	
	/**
	 * Een methode om te controleren of een tijdsperiode een andere tijdsperiode geheel overlapt.
	 * 
	 * @param otherTimePeriod	De mogelijks omhullende tijdsperiode
	 * @return true als deze tijdsperiode de gegeven omhult
	 */
	public boolean encapsulates(TimePeriod otherTimePeriod) { //TODO verwijderen
		if (otherTimePeriod.getBegin().compareTo(this.getBegin()) >= 0 &&
				otherTimePeriod.getEnd().compareTo(this.getEnd()) <= 0)
			return true;
		return false;
	}
	
	/**
	 * Een methode die de intersectie tussen twee tijdsperiodes geeft als tijdsperiode.
	 * 
	 * @param otherTimePeriod
	 * 		  De tijdsperiode waarmee de intersectie berekend wordt.
	 * @return De intersectie als tijdsperiode
	 */
	public TimePeriod intersect(TimePeriod otherTimePeriod) {		
		if (!this.interferes(otherTimePeriod))
			return null;
		
		TimeStamp 	begin = (this.getBegin().after(otherTimePeriod.getBegin()) ?
				this.getBegin():
				otherTimePeriod.getBegin()),
					end = (this.getEnd().before(otherTimePeriod.getEnd()) ?
				this.getEnd():
				otherTimePeriod.getEnd());
		
		
		return new TimePeriod(begin,end);
	}
	
	/**
	 * Een methode om te controleren of het einde van de ene tijdsperiode het begin is van de andere tijdsperiode.
	 * 
	 * @param otherTimePeriod
	 * 		  De tijdsperiode waarmee vergeleken wordt
	 * @return true
	 * 		   Als de begin- en eindtijdstippen overeenkomen
	 * @return false
	 * 		   Als de begin- en eindtijdstippen niet overeenkomen
	 */
	public boolean connects(TimePeriod otherTimePeriod) {
		return this.getEnd().equals(otherTimePeriod.getBegin());
	}
	
	/**
	 * Een methode om een tijdsperiode aan te maken tussen de dag (en uur)
	 * van het begin- en eindtijdstip van gegeven tijdsperiode.
	 * 
	 * @param timePeriod
	 * 		  Tijdsperiode waarvoor we een tijdsperiode willen.
	 * @return tijdsperiode voor gegeven tijdsperiode
	 */
	public TimePeriod shiftTimePeriod(TimePeriod timePeriod) {
		TimeStamp 
			begin = new TimeStamp(getField(Calendar.YEAR),getField(Calendar.MONTH),
				getField(Calendar.DAY_OF_MONTH), timePeriod.getBegin().get(Calendar.HOUR_OF_DAY),
				timePeriod.getBegin().get(Calendar.MINUTE)),
			end = new TimeStamp(getField(Calendar.YEAR),getField(Calendar.MONTH),
				getField(Calendar.DAY_OF_MONTH), timePeriod.getEnd().get(Calendar.HOUR_OF_DAY),
				timePeriod.getEnd().get(Calendar.MINUTE));
		
		return new TimePeriod(begin,end);
	}
	public TimeDuration getDuration(){
		return this.getEnd().timeDifference(this.getBegin());
	}
	/**
	 * Een methode om te controleren of een tijdsperiode langer duurt dan een andere tijdsperiode.
	 * 
	 * @param timePeriod
	 * 		  De tijdsperiode waar met vergelijken moet worden
	 * @return true
	 * 		   Als de tijdsperiode langer duurt dan gegeven tijdsperiode
	 * @return false
	 * 		   Als de tijdsperiode korter duurt dan gegeven tijdsperiode
	 */
	public boolean longerThan(TimePeriod otherPeriod) {
		return this.getDuration().compareTo(otherPeriod.getDuration())>0;
	}
	
	/**
	 * Een hulpmethode voor tijd om de waarde van gegeven soort tijd (dag, maand, jaar, ...) te krijgen
	 * 
	 * @param field
	 * 		  De soort tijd, voorgesteld door een integer
	 * @return de waarde van de soort tijd van het begintijdstip van de tijdsperiode
	 */
	private int getField(int field) {
		return this.getBegin().get(field);
	}
	
	/**
	 * Een toString voor TimePeriod.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return begin.toString() + " - " + end.toString() ;
	}

	/**
	 * Een methode om twee tijdsperiodes te vergelijken
	 * 
	 * @return true
	 * 		   Als de tijdsperiode eerder begint dan gegeven tijdsperiode
	 * @return false
	 * 		   Als de tijdsperiode later begint dan gegeven tijdsperiode
	 */
	@Override
	public int compareTo(TimePeriod otherTimePeriod) {
		return this.getBegin().compareTo(otherTimePeriod.getBegin());
	}
	
	/**
	 * Methode om te kijken of de tijdsperiode gelijk is aan gegeven tijdsperiode.
	 * 
	 * @param o
	 * 		  Gegeven tijdsperiode
	 * @return true
	 * 		   Als het begin- en eindtijdstip van de twee tijdsperiodes overeenkomen
	 * @return false
	 * 		   Als het begin- en eindtijdstip van de twee tijdsperiodes niet overeenkomen
	 */
	@Override
	@SystemAPI
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TimePeriod))
			return false;
		TimePeriod otherTime = (TimePeriod) o;
		
		return (this.getBegin().equals(otherTime.getBegin()) && 
				this.getEnd().equals(otherTime.getEnd()));
	}
	
	public boolean contains(TimeStamp timeStamp) {
		return (this.getBegin().compareTo(timeStamp) <= 0 && this.getEnd().compareTo(timeStamp)>= 0) ;
	}
}
