package system.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import client.ISchedule;

import system.campus.Campus;
import system.campus.CampusId;
import system.campus.CampusTravelTimes;
import system.exceptions.SchedulingException;
import system.staff.ShiftTable;
import system.time.TimePeriod;
import system.time.TimeStamp;
import annotations.SystemAPI;

/**
 * 
 * Deze klasse bevat een gesorteerde set met alle geplande scheduleditems. Er
 * zijn 2 belangrijke acties mogelijk, merge(Schedule) en firstAvailable().
 *
 */

public class Schedule implements ISchedule {
	/**
	 * Dit mag een set zijn omdat we niet willen dat er twee scheduleditems
	 * zijn in de lijst met dezelfde scheduleEvent. Deze is immers uniek.
	 * Kijk ook naar de inner klasse ScheduledItem.
	 */
	private SortedSet<ScheduledItem<?>> schedule;
	
	private boolean priorityMode;
	
	/**
	 * 	Maakt een nieuw schedule.
	 */
	public Schedule() {
		this.schedule = new TreeSet<ScheduledItem<?>>();
		this.priorityMode = false;
	}
	
	/**
	 * Voor de interne werking van merge.
	 * @param schedule
	 */
	public Schedule(SortedSet<ScheduledItem<?>> schedule) {
		this.schedule = schedule;
		this.priorityMode = false;
	}
	
	/**
	 * Kijkt na of de gegeven tijdsperiode vrij is in de agenda.
	 * 
	 * @param 	withTimePeriod
	 * 			Na te kijken tijdsperiode.
	 * @return	True als tijdsperiode overlapt met ��n van de tijdsperiodes
	 * 			van de scheduleditems van de gesorteerde set.
	 */
	private boolean interferesWith(TimePeriod withTimePeriod) {
		for (ScheduledItem<?> item : schedule) 
			if (item.getTimePeriod().interferes(withTimePeriod))
				return true;
		return false;
	}

	/**
	 * Een methode om priority mode aan of uit te zetten.
	 * 
	 * @param priorityMode
	 * 		  De in te stellen priority mode
	 */
	public void setPriorityMode(boolean priorityMode) {
		this.priorityMode = priorityMode;
	}
	
	/**
	 * Een methode om de priority mode op te vragen.
	 * 
	 * @return priorityMode
	 * 		   De prioriteitsmode
	 */
	private boolean getPriorityMode() {
		return this.priorityMode;
	}
	
	/**
	 * 
	 * @param 	<T>
	 * 			De klasse van de scheduledItem. Door het generisch zijn van
	 * 			deze methode blijft de functionaliteit van dit item behouden.
	 * @param 	timePeriod
	 * 			De te plannen periode.
	 * @param 	scheduleEvent
	 *			De te plannen event.
	 * @throws	SchedulingException
	 * 			Als de tijdsperiode interrefered met de planning.
	 * 			|	(this.interferresWith(timePeriod))
	 *
	 */
	public <T extends ScheduleEvent> void addItem(TimePeriod timePeriod, T scheduleEvent,
			Campus where) 
			throws SchedulingException {
		if (timePeriod == null) 
			throw new NullPointerException();
		if (this.interferesWith(timePeriod) && !this.getPriorityMode())
			throw new SchedulingException("This schedule interferes with the given timeperiod");
		this.schedule.add(new ScheduledItem<T>(timePeriod,scheduleEvent,
				new CampusId(where)));
	}
	
	/**
	 * 	Methode voor het verwijderen van een ScheduledItem uit de Schedule.
	 * 			
	 * @param 	scheduleEvent
	 * 			Te verwijderen event.
	 */
	public void removeItem(ScheduleEvent scheduleEvent) {
		List<ScheduledItem<?>> removeItems = new ArrayList<ScheduledItem<?>>();
		for (ScheduledItem<?> item : this.getSchedule())
			if (item.getScheduleEvent().equals(scheduleEvent))
				removeItems.add(item);
		// Dit is om ConcurrentModificationException te vermijden. Er kan geen
		// lijst worden ge�tereerd en tegelijk aangepast. Dus wordt er eerst
		// een lijst gemaakt met te verwijderen items.
		for (ScheduledItem<?> item : removeItems)
			this.schedule.remove(item);
	}
	
	/**
	 * Een methode om een aangepast schema op te halen wanneer we moeten
	 * reizen naar een andere campus.
	 * 
	 * @param travelTime
	 * 		  Reistijd naar de andere campus
	 * @param to
	 * 		  Campus(id) van bestemmeling
	 * @param begin
	 * 		  Het begintijdstip van de aangepaste schedule
	 * @return
	 */
	public Schedule adjusted(CampusTravelTimes travelTime, CampusId to,
			TimeStamp begin) {
		SortedSet<ScheduledItem<?>>	adjustedSet = new TreeSet<ScheduledItem<?>>();
				
		for (ScheduledItem<?> immutableItem : this.schedule) {
			if (immutableItem.getTimePeriod().getBegin().before(begin))
				continue;
			
			ScheduledItem<?> item = immutableItem.copy();
			adjustedSet.add(item);
			
			if (!item.getCampus().equals(to)) {
				item.addPrefix(travelTime.calculateTravelTime(to,item.getCampus()));
				item.addSuffix(travelTime.calculateTravelTime(item.getCampus(), to));	
			}
		
		}
		
		if (this.isShiftSchedule())
			return new ShiftSchedule(this.getShiftTable(),adjustedSet);
		else 
			return new Schedule(adjustedSet);
	}
	
	/**
	 * Het samenplakken van 2 agenda's. 
	 * 
	 * @param 	otherSchedule
	 * 			De agenda waarmee deze agenda moet samengeplakt worden.
	 * @return	Een agenda, waarin ScheduledItems zitten met scheduleEvent null,
	 * 			en tijdsperiodes die overeenkomen met de unie van de tijdsperiodes
	 * 			in deze schedule en otherSchedule.
	 */
	public Schedule merge(Schedule otherSchedule) { //TODO te lang
		SortedSet<ScheduledItem<?>> 	mergedSet = new TreeSet<ScheduledItem<?>>(this.schedule),
										correctedSet = new TreeSet<ScheduledItem<?>>();
		
		// Eerst gewoon alles samenzetten
		mergedSet.addAll(otherSchedule.schedule);
		
		ScheduledItem<?> previousItem = null;
		
		// Voor alle elementen in de volledig samengeplakte set, gaan we verbeteren. Aan
		// de hand van de verbetering, vinden we een nieuwe, 'corrected' set.
		for (ScheduledItem<?> item : mergedSet) {
			// We maken gebruik van het feit dat we de ScheduledItems in volgorde
			// afgaan. Dit wil zeggen, ScheduledItems die het eerst gepland zijn,
			// komen eerst aan bod.
			if (previousItem == null) {
				TimeStamp 	begin = item.getTimePeriod().getBegin(),
							end = item.getTimePeriod().getEnd();
				previousItem = new ScheduledItem<ScheduleEvent>(new TimePeriod(begin,end));
			} else if (previousItem.getTimePeriod().interferes(item.getTimePeriod()) ||
						previousItem.getTimePeriod().connects(item.getTimePeriod())) {
				if (item.getTimePeriod().getEnd().compareTo(previousItem.getTimePeriod().getEnd()) >= 0) {
					// Wanneer deze timeperiod een verlenging is van de vorige, wordt de vorige
					// uitgebreid. Dit wil zeggen dat het einde verzet wordt naar het einde van
					// deze timePeriod.
					TimeStamp 	begin = previousItem.getTimePeriod().getBegin(),
							 	end = item.getTimePeriod().getEnd();
					previousItem = new ScheduledItem<ScheduleEvent>(new TimePeriod(begin,end));
				}
			} else {
				// Als deze item niet overlapt met het vorige item, en dus ook de
				// vorige item niet aanlengt, kunnen we het vorige item als een afgebakende
				// tijdsperiode zien, en wordt deze toegevoegd aan de verbeterde set.
				correctedSet.add(previousItem);
				previousItem = item;
			}
		}
		
		// De laatste item moet nog worden toegevoegd.
		if (previousItem != null)
			correctedSet.add(previousItem);
		
		// Een nieuwe schedule maken aan de hand van de verbeterde set.
		Schedule returnSchedule;
		
		if (this.isShiftSchedule())
			returnSchedule = 
				new ShiftSchedule(this.getShiftTable().intersect(otherSchedule.getShiftTable()),
						correctedSet);
		else if (otherSchedule.isShiftSchedule())
			returnSchedule = 
				new ShiftSchedule(otherSchedule.getShiftTable().copyShiftTable(),
						correctedSet);
		else 
			returnSchedule = new Schedule(correctedSet);
		
		// Deze code zou nooit waar mogen zijn. Volgens bovenstaand algoritme kan
		// de methode isOverlapping nooit waar zijn.
		if (returnSchedule.isOverlapping())
			throw new SchedulingException("The generated schedule is overlapping");
		
		return returnSchedule;
	}
	
	/**
	 * Geeft de eerst mogelijk, beschikbare tijsperiode, na het begin van de gegeven
	 * tijdsperiode, met dezelfde duur als de gegeven tijdsperiode.
	 * 
	 * @param 	timePeriod
	 * 			De gegeven tijdsperiode
	 * @return	De eerst mogelijke, beschikbare tijdsperiode, na het begin van de gegeven
	 * 			tijdsperiode, met dezelfde duur.
	 */
	public TimePeriod firstAvailable(TimePeriod timePeriod, CampusId campus,
			TimeStamp breakOf) {
		TimePeriod returnTimePeriod = timePeriod;
		
		if (returnTimePeriod.getEnd().compareTo(breakOf) >= 0) 
			return null;
		
		for (ScheduledItem<?> item : this.schedule) {
			if (returnTimePeriod.getBegin().compareTo(item.getTimePeriod().getEnd()) >= 0)
				continue;
			if (returnTimePeriod.getEnd().compareTo(breakOf) >= 0) {
				returnTimePeriod = null;
				break;
			}
			
			if(returnTimePeriod.interferes(item.getTimePeriod()))
				returnTimePeriod = TimePeriod.shiftBegin(returnTimePeriod, item.getTimePeriod().getEnd());
		}
		
		return returnTimePeriod;
	}
	
	/**
	 * 
	 * @return	De set van ScheduledItems.
	 */
	public SortedSet<ScheduledItem<?>> getSchedule() {
		return Collections.unmodifiableSortedSet(this.schedule);
	}
	
	/**
	 * 
	 * @param 	<T>
	 * 			De klasse van de scheduledEvents in de scheduledItems in de set.
	 * @param 	t
	 * 			De klasse van scheduleEvent in de scheduleItems
	 * @return	Een lijst met ScheduledItems waarvan de ScheduleEvent van de klasse
	 * 			<T> is.
	 */
	public <T extends ScheduleEvent> SortedSet<ScheduledItem<T>> getScheduleByClass(Class<T> t) {
		SortedSet<ScheduledItem<T>> result = new TreeSet<ScheduledItem<T>>();
		
		for (ScheduledItem<?> item : this.schedule) {
			ScheduleEvent itemE = item.getScheduleEvent();
			
			if (t.isAssignableFrom(itemE.getClass())) {
				// Deze cast is veilig door de bovenstaande check. We weten zeker
				// dat itemE een subklasse is van t, of dezelfde klasse als t.
				result.add(	new ScheduledItem<T>(
									item.getTimePeriod(),
									t.cast(itemE),
									item.getCampus()));
			}
		}		
		
		return result;
	}
	
	/**
	 * Checkt op een consistente planning.
	 * @return	true als de planning consistent is.
	 */
	public boolean isOverlapping() {
		for (ScheduledItem<?> item : this.schedule) 
			for (ScheduledItem<?> checkItem : this.schedule) 
				if (item.getTimePeriod() != checkItem.getTimePeriod())
					if (item.getTimePeriod().interferes(checkItem.getTimePeriod()))
						return true;
		return false;
	}
	
	/**
	 * Een methode om een lijst van overlappende schedule events op te vragen.
	 * 
	 * @return overlapping
	 * 		   Lijst van overlappende schedule events
	 */
	public List<ScheduleEvent> getOverlapping() {
		List<ScheduleEvent> overlapping = new ArrayList<ScheduleEvent>();
		
		for (ScheduledItem<?> item : this.schedule) 
			for (ScheduledItem<?> checkItem : this.schedule) 
				if (item.getTimePeriod() != checkItem.getTimePeriod())
					if (item.getTimePeriod().interferes(checkItem.getTimePeriod()))
						overlapping.add(item.getScheduleEvent());
		
		return overlapping;
	}
	
	/**
	 * Een toString voor Schedule.
	 */
	@Override
	@SystemAPI
	public String toString() {
		String returnString = "";
		for (ScheduledItem<?> item : this.schedule) {
			returnString += item.getTimePeriod().toString()+" - "+ item.getScheduleEvent().toString()+"\n";
		}
		return returnString;
	}
	
	/**
	 * Een methode om een schedule om te zetten naar een array van strings.
	 * 
	 * @return de array van strings
	 */
	public String[] scheduleToStringToArray() {
		return toString().split("\n");
	}
	
	/**
	 * True als deze Schedule een ShiftSchedule is
	 * @return	false
	 */
	public boolean isShiftSchedule() {
		return false;
	}
	
	/**
	 * De werkuren van deze planning
	 * @return null
	 */
	public ShiftTable getShiftTable() {
		return null;
	}
}