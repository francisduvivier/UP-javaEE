package system.scheduling;

import system.campus.CampusId;
import system.time.TimeDuration;
import system.time.TimePeriod;
import system.time.TimeStamp;
	
	/**
	 * 
	 * De klasse ScheduledItem zorgt voor een connectie tussen een TimePeriod
	 * en ScheduleEvent. Ze is generisch gemaakt zodat er voor een er specifieke
	 * methodes kunnen uitgevoerd worden op de ScheduleEvent als er bijvoorbeeld
	 * met Treatments gewerkt wordt. Van deze eigenschap wordt gebruik gemaakt
	 * in getScheduleByClass(Class<T>). ScheduledItem is uniek op zijn ScheduleEvent
	 * m.a.w. 
	 * 		|	let item1 = ScheduledItem
	 * 		|	let item2 = ScheduledItem
	 * 		| 	item1.equals(item2) <=> item1.scheduleEvent.equals(item2.scheduleEvent) 
	 * Dit met de uitzondering wanneer ScheduleEvent null is.
	 * 
	 * @param 	<T>
	 * 			De klasse van de ScheduleEvent. Moet een subklasse zijn van ScheduleEvent.
	 */
	public class ScheduledItem<T extends ScheduleEvent> implements Comparable<ScheduledItem<?>> {
		private TimePeriod timePeriod;
		private final T scheduleEvent;
		private final CampusId campus;
		
		/**
		 * Maak een ScheduledItem aan. Dit kan enkel gebeuren in Schedule.
		 * 
		 * @param 	timePeriod
		 * 			De tijdsperiode wanneer het item gepland is.
		 * @param 	scheduleEvent
		 * 			De event die gepland is. Instantie van de interface ScheduleEvent.
		 */
		ScheduledItem(TimePeriod timePeriod, T scheduleEvent, CampusId campus) {
			this.timePeriod = timePeriod;
			this.scheduleEvent = scheduleEvent;
			this.campus = campus;
		}
			
		/**
		 * Maak een ScheduledItem aan. Dit kan enkel gebeuren in Schedule.
		 * 
		 * DIT MAG ENKEL GEBRUIKT WORDEN VOOR HET MERGEN VAN SCHEDULES.
		 * 
		 * @param 	timePeriod
		 * 			De tijdsperiode wanneer het item gepland is.
		 */
		ScheduledItem(TimePeriod timePeriod) {
			this.timePeriod = timePeriod;
			this.scheduleEvent = null;
			this.campus = null;
		}
		
		/**
		 * 
		 * @return	De scheduleEvent van deze ScheduledItem.
		 */
		public T getScheduleEvent() {
			return this.scheduleEvent;
		}
		
		/**
		 * @return 	De tijdsperiode van deze ScheduledItem
		 */
		TimePeriod getTimePeriod() {
			return this.timePeriod;
		}
		
		CampusId getCampus() {
			return this.campus;
		}
		//TODO Francis beslissen om long door TimeDuration te vervangen
		void addPrefix(TimeDuration prefixDuration) {
			TimeStamp	newBegin = TimeStamp.addedToTimeStamp(prefixDuration.multiply(-1), 
							getTimePeriod().getBegin()),
						newEnd = getTimePeriod().getEnd();			
		
			this.timePeriod = new TimePeriod(newBegin,newEnd);
		}
		//TODO Francis beslissen om long door TimeDuration te vervangen
		void addSuffix(TimeDuration suffixDuration) {
			TimeStamp 	newBegin = getTimePeriod().getBegin(),	
						newEnd = TimeStamp.addedToTimeStamp(suffixDuration, 
							getTimePeriod().getEnd());
			
			this.timePeriod = new TimePeriod(newBegin,newEnd);
		}
		
		ScheduledItem<T> copy() {
			return new ScheduledItem<T>(getTimePeriod(),
										getScheduleEvent(),
										getCampus());
		}
		
		/**
		 * De compare is ongelijk wanneer de 2 timePeriods niet gelijk zijn.
		 * Wanneer de 2 timePeriods gelijk zijn worden de scheduleEvents vergeleken.
		 * De compare moet gelijk zijn wanneer de scheduleEvents hetzelfde zijn.
		 * Als deze niet gelijk zijn, moeten we toch een zeker onderscheid maken,
		 * om de rangorde van de TreeSet consistent te houden en geen Scheduled-
		 * Items af te sluiten. Hiervoor worden de unieke HashCodes van het object
		 * vergeleken. Er mag immers niet gezegd worden dat twee ScheduledItems
		 * gelijk zijn als de TimePeriods overeenkomen.
		 *	
		 */
		@Override
		public int compareTo(ScheduledItem<?> o) {
			int compare = timePeriod.compareTo(o.timePeriod);
			
			if (compare != 0) 
				return compare;
			
			if (this.scheduleEvent != null && o.scheduleEvent != null && 
					o.scheduleEvent.equals(this.scheduleEvent))
				return 0;

			return (System.identityHashCode(o) - System.identityHashCode(this));
		}
		
		public boolean timeEquals(ScheduledItem<?> item) {
			return this.timePeriod.equals(item.timePeriod);
		}
		
		/**
		 * ScheduledItems zijn gelijk wanneer de onderliggende scheduleEvents 
		 * niet null zijn en gelijk. 
		 */
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			  
			if (!(o instanceof ScheduledItem<?>))
				return false;
			
			ScheduledItem<?> item = (ScheduledItem<?>) o;
			
			if (this.scheduleEvent != null && item.scheduleEvent != null && 
					item.scheduleEvent.equals(this.scheduleEvent))		
				return true;
			
			return false;
		}
	}