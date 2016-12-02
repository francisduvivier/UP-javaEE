package system.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import annotations.SystemAPI;
/**
 * Deze klasse stelt een zeker moment in de tijd voor. De klasse is
 * immutable.
 * 
 * @author swop team 10
 *
 */
@SystemAPI
public final class TimeStamp implements Comparable<TimeStamp> {
	private final GregorianCalendar timeStamp;
	private static final long MILLISECS_PER_MINUTE = 60 * 1000;
	private static final long MILLISECS_PER_HOUR = 60 * MILLISECS_PER_MINUTE;
	private static final long MILLISECS_PER_DAY = 24 * MILLISECS_PER_HOUR;
	
	@SystemAPI
	public final static TimeStamp END_OF_DAYS;
	
	static {
		END_OF_DAYS = new TimeStamp();
	}
	
	/**
	 * Maakt een timestamp tot op de seconde nauwkeurig.
	 * @param 	year
	 * 			Jaar van deze timestamp
	 * @param 	month
	 * 			Maand van deze timestamp
	 * @param 	day_of_month
	 * 			Dag van deze timestamp
	 * @param 	hour
	 * 			Uur van deze timestamp
	 * @param 	minute
	 * 			Minuut van timestamp
	 * @param 	second
	 * 			Second van timestampw
	 */
	@SystemAPI
	public TimeStamp(int year, int month, int day_of_month, int hour,
			int minute, int second) {
		this.timeStamp = new GregorianCalendar(year, month, day_of_month, hour,
				minute, second);
	}
	
	/**
	 * Private constructor voor TimeStamp die een datum zeer ver in de toekomst initialiseert.
	 */
	private TimeStamp() {
		timeStamp = new GregorianCalendar();
		timeStamp.setTime(new Date(Long.MAX_VALUE));
	}


	/**
	 * Maakt een timestamp tot op de minuut nauwkeurig.
	 * @param 	year
	 * 			Jaar van deze timestamp
	 * @param 	month
	 * 			Maand van deze timestamp
	 * @param 	day_of_month
	 * 			Dag van deze timestamp
	 * @param 	hour
	 * 			Uur van deze timestamp
	 * @param 	minute
	 * 			Minuut van timestamp
	 * @param 	second
	 * 			Second van timestampw
	 */
	@SystemAPI
	public TimeStamp(int year, int month, int day_of_month, int hour, int minute) {
		this.timeStamp = new GregorianCalendar(year, month, day_of_month, hour,
				minute, 0);
	}

	/**
	 * Kopieer gegeven timestamp in een nieuw, gelijk object. Een Timestamp is
	 * immutable van uit het standpunt van andere klasse, maar kan intern wel
	 * zichzelf veranderen, deze methode is nuttig voor de addedTotimestamp
	 * methode.
	 * 
	 * @param otherTimeStamp
	 *            Gegeven tijdsmoment
	 * @return | result == timeStamp -> timeStamp.equals(otherTimeStamp)
	 */
	private static TimeStamp copyTimeStamp(TimeStamp otherTimeStamp) {
		int year = otherTimeStamp.get(Calendar.YEAR), month = otherTimeStamp
				.get(Calendar.MONTH), day_of_month = otherTimeStamp
				.get(Calendar.DAY_OF_MONTH), hour = otherTimeStamp
				.get(Calendar.HOUR_OF_DAY), minute = otherTimeStamp
				.get(Calendar.MINUTE), second = otherTimeStamp
				.get(Calendar.SECOND);
		return new TimeStamp(year, month, day_of_month, hour, minute, second);
	}

	/**
	 * Geeft een nieuwe timestamp, die millis toevoegt aan de gegeven timestamp.
	 * @param 	millis
	 * 			toe te voegen millis. (Note: werk met TimeConvert)
	 * @param 	timeStamp
	 * 			Gegeven timestamp
	 * @return 	Timestamp die millis milliseconden verschilt van de gegeven timeStamp
	 */
	@SystemAPI
	public static TimeStamp addedToTimeStamp(TimeDuration timeDuration, TimeStamp timeStamp) {
		TimeStamp addedTimeStamp = copyTimeStamp(timeStamp);
		addedTimeStamp.timeStamp.add(Calendar.SECOND, (int)timeDuration.getSeconds());
		return addedTimeStamp;
	}
	
	/**
	 * Parset de gegeven string naar een timeStamp
	 * @param 	string
	 * 			gegeven string van de vorm 'dd/MM/yyyy HH:mm'
	 * @return	new TimeSTamp(yyyy,MM,dd,HH,mm)
	 * @throws 	ParseException
	 * 			Als de string niet juist geparsed is.
	 */
	@SystemAPI
	public static TimeStamp parseStringToTimeStamp(String string) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = sdf.parse(string);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		return new TimeStamp(year, month, day_of_month, hour, minute);
	}

	/**
	 * Een methode om een string als datum te formateren
	 * @param format
	 * 		  Het formaat waarin we de string willen formateren
	 * @return Het tijdstip geformateerd als string
	 */
	@SystemAPI
	public String formatDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		return sdf.format(this.timeStamp.getTime());
	}
	
	/**
	 * Een methode om een datum (string) om te zetten naar een tijdstip.
	 * 
	 * @param date
	 * 		  De om te zetten datum
	 * @return omgezette datum als tijdstip
	 * @throws ParseException
	 * 		   Wanneer de gegeven datum niet omgezet kan worden.
	 */
	@SystemAPI
	public static TimeStamp parseDateToTimeStamp(String date) throws ParseException  {
		return parseStringToTimeStamp(date+ " 00:00");
	}
	
	/**
	 * @param 	field
	 * 			Tijdseenheid (Note: kijk Calendar)
	 * @return	de waarde van de gegeven tijdseenheid in deze timestamp.
	 */
	@SystemAPI
	public int get(int field) {
		return this.timeStamp.get(field);
	}

	/**
	 * 
	 * @param 	otherTime
	 * 			te vergelijken timestamp	
	 * @return	true als deze timestamp voor otherTime valt
	 */
	@SystemAPI
	public boolean before(TimeStamp otherTime) {
		return this.timeStamp.before(otherTime.timeStamp);
	}

	/**
	 * 
	 * @param 	otherTime
	 * 			te vergelijken timestamp	
	 * @return	true als deze timestamp na otherTime valt
	 */
	@SystemAPI
	public boolean after(TimeStamp otherTime) { 
		return this.timeStamp.after(otherTime.timeStamp);
	}

	/**
	 * 
	 * @param 	otherTime
	 * @return	verschil in millis met otherTime
	 */
	
	@SystemAPI
	public TimeDuration timeDifference(TimeStamp otherTime) {
		long amountInMillis=Math.abs(this.timeStamp.getTimeInMillis()
				- otherTime.timeStamp.getTimeInMillis());
		return TimeDuration.milliseconds(amountInMillis);
	}

	/**
	 * @return	De dag na epoch
	 */
	
	public long getDay() {
		long timeEpoch = this.timeStamp.getTimeInMillis() + MILLISECS_PER_HOUR;
		double days = (double)timeEpoch/MILLISECS_PER_DAY;
		long day = (long) Math.floor(days);
		
		return day;
	}

	/**
	 * Een methode om twee tijdstippen te vergelijken.
	 * 
	 * @param otherTime
	 * 		  Het tijdstip waar met vergeleken moet worden
	 * @return -1
	 * 		   Als het tijdstip voor gegeven tijdstip is
	 * @return 0
	 * 		   Als het tijdstip samenvalt met gegeven tijdstip
	 * @return 1
	 * 		   Als het tijdstip na gegeven tijdstip is
	 */
	@Override
	public int compareTo(TimeStamp otherTime) {
		return this.timeStamp.compareTo(otherTime.timeStamp);
	}
	
	/**
	 * Een methode om te controleren of 2 tijdstippen gelijk zijn.
	 * 
	 * @param otherTime
	 * 		  Het tijdstip waar met vergeleken moet worden.
	 * @return true
	 * 		   Als het tijdstip op hetzelfde moment is als gegeven tijdstip
	 * @return false
	 * 		   Als het tijdstip niet op hetzelfde moment is als gegeven tijdstip
	 */
	public boolean equals(TimeStamp otherTime) {
		return this.timeStamp.equals(otherTime.timeStamp);
	}

	/**
	 * Een methode om te controleren of het tijdstip gelijk is aan een object.
	 * 
	 * @param o
	 * 		  Het gegeven object
	 * @result true
	 * 		   Als het om hetzelfde object gaat
	 * @result true
	 * 		   Als de tijdstippen op exact hetzelfde moment vallen
	 * @result false
	 * 		   Als de tijdstippen niet samen vallen
	 */
	@Override
	@SystemAPI
	public boolean equals(Object o) {
		if (this == o) 
			return true;
		if (!(o instanceof TimeStamp))
			return false;
		TimeStamp otherTime = (TimeStamp) o;
		
		return this.timeStamp.equals(otherTime.timeStamp);
	}
	
	/**
	 * Een toString voor TimeStamp.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return this.timeStamp.getTime().toString();
	}
	
	/**
	 * Deze methode geeft het eerstvolgende tijdstip met een bepaald uur en
	 * minuut terug. Je kan dit zien als een klok die verder gedraaid wordt tot
	 * minuut en uur op de opgegeven nummers staan.
	 * 
	 * @return een nieuwe TimeStamp met dezelfde dag of de volgende dag, maar met het
	 *         opgegven uur.
	 *
	 * @param hour Het uur voor de nieuwe TimeStamp
	 * @param minute de minuten voor de nieuwe TimeStamp
	 */
	@SystemAPI
	public TimeStamp calcForwardTo(int hour, int minute) {
		if(hour>23||hour<0||minute>59||minute<0)
			throw new IllegalArgumentException("je moet een valide tijstip opgeven");
		int year = this.get(Calendar.YEAR);
		int month = this.get(Calendar.MONTH);
		int day = this.get(Calendar.DAY_OF_MONTH);
		TimeStamp firstGivenTime=new TimeStamp(year, month, day, hour, minute);
		if (!this.before(firstGivenTime))
			return addedToTimeStamp(TimeDuration.days(1), firstGivenTime);
		return firstGivenTime;
	}
}
