package system.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import system.campus.Campus;
import system.campus.CampusId;
import system.campus.Hospital;
import system.exceptions.InsufficientWarehouseItemsException;
import system.exceptions.ReschedulingException;
import system.exceptions.SchedulingException;
import system.repositories.ResourceType;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.util.Filter;
import system.util.FilterValue;

public abstract class MainScheduler extends Observable implements Observer {
	private final Hospital hospital;
	private MainScheduler successor;	
	private MainScheduler predecessor;
	private Schedule handlingSchedule;
	private final Priority handlingPriority;

	/**
	 * Een methode om een andere MainScheduler als opvolger te zetten.
	 * 
	 * @param successor
	 * 		  De op te volgen main scheduler
	 */
	protected final void setSuccessor(MainScheduler successor) {
		this.successor = successor;
		successor.setPredecessor(this);
	}
	
	/**
	 * Een methode om een andere MainScheduler als voorganger te zetten.
	 * 
	 * @param predecessor
	 * 		  De voor te gane main scheduler
	 */
	private void setPredecessor(MainScheduler predecessor) {
		this.predecessor = predecessor;
	}
	
	/**
	 * Een methode om de opvolger op te vragen.
	 * 
	 * @return successor
	 * 		   De main scheduler die de scheduler opvolgt
	 */
	private MainScheduler getSuccessor() {
		return this.successor;
	}
	
	/**
	 * Een methode om de voorganger op te vragen.
	 * 
	 * @return predecessor
	 * 		   De main scheduler die de scheduler voorgaat
	 */
	private MainScheduler getPredecessor() {
		return this.predecessor;
	}
	
	/**
	 * Een methode om de prioriteit op te vragen.
	 * 
	 * @return handlingPriority
	 * 		   De prioriteit waarmee de scheduler omgaat
	 */
	protected final Priority getHandlingPriority() {
		return this.handlingPriority;
	}
	
	/**
	 * Een methode om een observer toe te voegen.
	 * 
	 * @param observer
	 * 		  Toe te voegen observer
	 */
	@Override
	public void addObserver(Observer observer) {
		super.addObserver(observer);
		if (this.getSuccessor() != null)
			this.getSuccessor().addObserver(observer);
	}
	
	/**
	 * Een methode om een schedule event te plannen met een tijdstip.
	 * 
	 * @param scheduleEvent
	 * 		  Het te schedulen schedule event
	 * @param begin
	 * 		  Het begintijdstip
	 */
	public final void schedule(ScheduleEvent scheduleEvent, TimeStamp begin) {
		if (this.canSchedule(scheduleEvent)) {
			try {
				this.mainSchedule(scheduleEvent, begin);
			}	catch(NullPointerException e) {
				throw new SchedulingException("Something went wrong");
			}
		}
		else if (this.getSuccessor() != null)
			this.getSuccessor().schedule(scheduleEvent, begin);
	}
	
	/**
	 * Een methode om na een overlapping een schedule event te plannen.
	 * 
	 * @param scheduleEvent
	 * 		  Het te schedulen schedule event
	 * @param begin
	 */
	private void scheduleAfterOverlap(ScheduleEvent scheduleEvent, TimeStamp begin) {
		if (this.canSchedule(scheduleEvent)) {
			try {
				this.mainSchedule(scheduleEvent, begin);
			}	catch(NullPointerException e) {
				throw new SchedulingException("Something went wrong");
			}
		}
		else if (this.getPredecessor() != null)
			this.getPredecessor().scheduleAfterOverlap(scheduleEvent, begin);
	}
	
	/**
	 * Een methode om te controleren of een schedule event gepland kan worden.
	 * 
	 * @param scheduleEvent
	 * 		  Het te plannen schedule event
	 * @return true
	 * 		   Als de prioriteit van het schedule event gelijk is aan de prioriteit waarmee de scheduler omgaat
	 * @return false
	 * 		   Als de prioriteit van het schedule event niet gelijk is aan de prioriteit waarmee de scheduler omgaat
	 */
	private final boolean canSchedule(ScheduleEvent scheduleEvent) {
		return (scheduleEvent.getPriority() == this.getHandlingPriority());
	}
	
	/**
	 * Een event die niet gepland kan worden, wordt bijgehouden in een lijst
	 * deze lijst zal telkens afgegaan worden wanneer de scheduler geupdate
	 * wordt door de warehouse.
	 */
	private final Map<ScheduleEvent, TimeStamp> scheduleLater;
	
	/**
	 * Maakt een scheduler voor een bepaalde campus. De scheduler krijgt een
	 * campus mee en is dus niet volledig abstract. Dit is zo gekozen, zodat
	 * de scheduler verandwoordelijk kan zijn voor het nakijken van de warehouse.
	 * Als er in de warehouse niet genoeg items aanwezig zijn voor een event,
	 * zal de Scheduler de event bijhouden, en later plannen.
	 * @param 	hospital
	 * 			Het ziekenhuis waar de scheduler van toepassing is
	 * @param 	handlingPriority
	 * 			De prioriteit waarmee de scheduler omgaat
	 */
	protected MainScheduler(Hospital hospital, Priority handlingPriority) {
		this.hospital = hospital;
		this.scheduleLater = new HashMap<ScheduleEvent, TimeStamp>();
		this.handlingPriority = handlingPriority;
		this.successor = null;
		this.predecessor = null;
	}
	
	/**
	 * Een methode om de schedule in te stellen waar de
	 * main scheduler gebruik van maakt.
	 * 
	 * @param schedule
	 * 		  De te gebruiken schedule
	 */
	protected final void handleSchedule(Schedule schedule) {
		this.handlingSchedule = schedule;
	}
	
	/**
	 * Een methode om de schedule die de main scheduler gebruikt
	 * op te vragen.
	 * 
	 * @return handlingSchedule
	 * 		   Schedule waar de main scheduler gebruik van maakt
	 */
	protected final Schedule getHandlingSchedule() {
		return this.handlingSchedule;
	}
	
	/**
	 * Verwijdert de gegeven operatie van de schedules van de resources,
	 * en voegt warehouseitems die gebruikt zouden worden terug toe.
	 * @param 	operation
	 * 			De operatie die geannuleerd mag worden
	 */
	public final void unschedule(Undoable operation) {
		if (hospital.getCampusById(operation.getHandlingCampus()) != null)
		operation.updateWarehouse(hospital.getCampusById(
				operation.getHandlingCampus()).getWarehouse(), true);
		
		operation.cancel();
		
		setChanged();
		notifyObservers(operation);
	}
	
	/**
	 * Herplant een operatie die geannuleerd is, met dezelfde resources en
	 * timeperiod. Dit gebeurd intern in de PatientOperation.
	 * @param 	operation
	 * 			De operatie die geherpland moet worden
	 * @throws 	InsufficientWarehouseItemsException
	 * 			Als er niet genoeg dingen in de stock zijn voor deze operatie.
	 * 			Wordt in een lijst geplaatst, en uitgevoerd als de warehouse update.
	 *
	 */
	public final void reschedule(Undoable operation) 
			throws InsufficientWarehouseItemsException {
		if (hospital.getCampusById(operation.getHandlingCampus()) != null) {
			if (operation.canBeScheduled(hospital.getCampusById(
				operation.getHandlingCampus()).getWarehouse())) {
			operation.updateWarehouse(hospital.getCampusById(
					operation.getHandlingCampus()).getWarehouse(), false);
			} else {
				scheduleLater.put(operation, hospital.getHospitalTime().getTime());
				throw new InsufficientWarehouseItemsException("Event has to wait, insufficient warehouse items.");
			}
		}
		
		operation.redo();
		
		setChanged();
		notifyObservers(operation);
	}

	/**
	 * Plan een scheduleEvent, ten vroegste vanaf de gegeven TimeStamp.
	 * @param 	scheduleEvent
	 * 			De te plannen actie.
	 * @param 	begin
	 * 			Het vroegste begin-tijdstip.
	 * @throws 	SchedulingException
	 * 			Als er iets misgaat bij het mergen van agenda's.
	 */
	protected final void mainSchedule(ScheduleEvent scheduleEvent, TimeStamp begin) 
			throws SchedulingException { //TODO te lang
		TimeStamp breakOf = TimeStamp.END_OF_DAYS;
		TimePeriod firstAvailable = null;
		Campus chosenCampus = null;
		List<ScheduleResource> firstAvailableCombination = 
			new ArrayList<ScheduleResource>();
		
		for (Campus campus : getPossibleCampuses(scheduleEvent,begin)) {
			TimePeriod thisPeriod = findFirstAvailable(scheduleEvent, begin, campus,
					breakOf, firstAvailableCombination);
			if (thisPeriod != null) {
				firstAvailable = thisPeriod;
				breakOf = firstAvailable.getEnd();
				chosenCampus = campus;
			}
		}

		scheduleEvent.updateWarehouse(chosenCampus.getWarehouse(), false);
		scheduleFirstAvailable(firstAvailableCombination, firstAvailable, scheduleEvent, chosenCampus);
		handleReschedules(firstAvailableCombination, begin);
		
		setChanged();
		notifyObservers(scheduleEvent);
	}
	
	// Als een event niet genoeg warehouseItems vindt, zal deze bijgehouden
	// worden in een lijst, en later gepland worden (bij een update van de
	// warehouse)
	/**
	 * Een methode om de lijst van campussen waar gescheduled kan worden op te vragen.
	 * 
	 * @param scheduleEvent
	 * 		  Het schedule event waarvan gekeken wordt of het gescheduled kan worden
	 * @param begin
	 * 		  Het begintijdstip van de scheduler
	 * @return campuses
	 * 		   Lijst van campussen waar de schedule event gepland kan worden.
	 */
	private List<Campus> getPossibleCampuses(ScheduleEvent scheduleEvent, TimeStamp begin) {
		List<Campus> campuses = new ArrayList<Campus>();
		
		for (Campus campus : this.hospital.getCampuses()) 
			if (scheduleEvent.canBeScheduled(campus.getWarehouse()))
				campuses.add(campus);
		
		if (campuses.isEmpty()) {
			scheduleLater.put(scheduleEvent, begin);
			throw new InsufficientWarehouseItemsException("Event has to wait, insufficient warehouse items.");
		} else 
			return campuses;
	}
	
	/**
	 * Dit zoekt de eerste mogelijke combinatie en tijdsperiode voor een event
	 * op een bepaalde campus.
	 * @param 	scheduleEvent
	 * 			de te plannen event
	 * @param 	begin
	 * 			vanaf wanneer er gepland mag wordne
	 * @param 	campus
	 * 			op welke campus
	 * @param 	breakOf
	 * 			afbreking wanneer op een andere campus een snellere periode
	 * 			is gevonden.
	 * @param 	firstAvailableCombination
	 * 			hierin wordt de eerste combinatie opgeslagen.
	 * @return	null als er geen snellere tijdsperiode gevonde werd
	 */
	private TimePeriod findFirstAvailable(ScheduleEvent scheduleEvent,
			TimeStamp begin, Campus campus, TimeStamp breakOf, 
			List<ScheduleResource> firstAvailableCombination) {
		
		List<List<ScheduleResource>> allCombinations = findAllPossibleCombinations(scheduleEvent, campus);
		if (allCombinations.isEmpty()) return null;
		TimeStamp end = TimeStamp.addedToTimeStamp(scheduleEvent.getDuration(), begin);
		TimePeriod timePeriod = new TimePeriod(begin,end);
		
		TimePeriod firstAvailable = null;
		
		// Alle combinaties worden afgegaan, de agenda's van de resources worden
		// gemerged. Van deze gemergede agenda's, wordt de eerst toegankelijke
		// geselecteerd. De resources van deze combinatie kunnen worden gepland.
		for (List<ScheduleResource> combination : allCombinations) {
			Schedule mergedSchedule = mergeSchedules(combination,begin,campus);
			
			TimePeriod thisFirstAvailable = 
				mergedSchedule.firstAvailable(timePeriod,new CampusId(campus)
												,breakOf);
			
			if (thisFirstAvailable != null && (firstAvailable == null ||
					thisFirstAvailable.compareTo(firstAvailable) < 0)) {
				firstAvailable = thisFirstAvailable;
				firstAvailableCombination.clear();
				for (ScheduleResource resource: combination) 
					firstAvailableCombination.add(resource);
			}
		}
		
		return firstAvailable;
	}

	/**
	 * Een methode die de eerste beschikbare combinatie van resources gaat schedulen
	 * in de eerst beschikbare tijdsperiode voor een schedule event.
	 * 
	 * @param firstAvailableCombination
	 * 		  De combinatie  van schedule events die het eerst beschikbaar was
	 * @param firstAvailable
	 * 		  Eerst beschikbare tijdsperiode
	 * @param scheduleEvent
	 * 		  Schedule event waar gepland moet worden
	 * @param campus
	 * 		  Campus waar de scheduler van toepassing is
	 */
	private void scheduleFirstAvailable(List<ScheduleResource> firstAvailableCombination,
			TimePeriod firstAvailable, ScheduleEvent scheduleEvent, Campus campus) {
		for (ScheduleResource resource : firstAvailableCombination) 
			resource.getSchedule().setPriorityMode(true);
		
		scheduleEvent.setHandlingCampus(new CampusId(campus));
		scheduleEvent.schedule(firstAvailable, firstAvailableCombination, campus);
		
		for (ScheduleResource resource : firstAvailableCombination) 
			resource.getSchedule().setPriorityMode(false);
		
	}
	
	// Alle combinaties van resources in dit campus voor deze event zoeken
	// gebeurd in Combinations.
	/**
	 * Een methode om alle mogelijke combinaties te vinden.
	 * 
	 * @param scheduleEvent
	 * 		  schedule event waar combinaties met gevonden moeten worden
	 * @param campus
	 * 		  De campus waar combinaties moeten gevonden worden
	 * @return allCombinations
	 * 		   De mogelijke combinaties
	 */
	private List<List<ScheduleResource>> findAllPossibleCombinations(
			ScheduleEvent scheduleEvent, Campus campus) {
		List<List<ScheduleResource>> allResources = this.getListOfResources(scheduleEvent, campus);
		List<List<ScheduleResource>> allCombinations = new Combinations<ScheduleResource>(allResources).getCombinations();
		
		return allCombinations;
	}

	/**
	 * Een methode die de lijst van combinaties samenvoegt.
	 * 
	 * @param combination
	 * 		  Lijst van combinaties
	 * @param begin
	 * 		  Begintijdstip van de schedule
	 * @param campus
	 * 		  Campus waar de schedule van toepassing is
	 * @return mergedSchedule
	 * 		   Het schedule van de samengevoegde combinaties
	 */
	private Schedule mergeSchedules(List<ScheduleResource> combination,
			TimeStamp begin, Campus campus) {
		Schedule mergedSchedule = new Schedule();
		
		for (ScheduleResource scheduleResource : combination) {
			handleSchedule(scheduleResource.getSchedule().
					adjusted(	hospital.getCampusTravelTimes(), 
								new CampusId(campus), 
								begin));
			mergedSchedule = mergedSchedule.merge(filtered());
		}
		
		return mergedSchedule;
	}
	
	/**
	 * Geeft een lijst van alle resources, voor deze campus, horend bij de 
	 * gegeven event.
	 * @param 	scheduleEvent
	 * 			De gegeven event
	 * @return	een lijst van resources voor gegeven event in deze campus
	 */
	private List<List<ScheduleResource>> getListOfResources(ScheduleEvent scheduleEvent, Campus campus) {
		List<List<ScheduleResource>> listOfResources = new ArrayList<List<ScheduleResource>>();
		
		for (ResourceType thisType : scheduleEvent.neededResources()) 
			listOfResources.add(campus.getResources(thisType));
		
		for (ScheduleResource thisResource : scheduleEvent.neededSpecificResources()) {
			List<ScheduleResource> resourceList = new ArrayList<ScheduleResource>();
			resourceList.add(thisResource);
			listOfResources.add(resourceList);
		}
		
		return listOfResources;
	}

	/**
	 * Kijkt na of er genoeg warehouse items zijn om bepaalde events uit de lijst
	 * van uitgestelde events te plannen.
	 * 
	 * @param o
	 * @param arg
	 * 		  
	 */
	@Override
	public final void update(Observable o, Object arg) {
		for (ScheduleEvent event : scheduleLater.keySet()) {
			boolean canBeScheduled = false;
			for (Campus campus : this.hospital.getCampuses()) 
				if (event.canBeScheduled(campus.getWarehouse())) {
					canBeScheduled = true;
					break;
				}
			if (canBeScheduled)
					this.mainSchedule(event,scheduleLater.get(event));
		}
	}
	
	/**
	 * Een methode om de filterwaarde van een event op te vragen.
	 * 
	 * @return true
	 * 		   Als de prioriteit van het event groter is dan die van de scheduler
	 * @return false
	 * 		   Als de prioriteit van het event lager is dan die van de scheduler
	 */
	private FilterValue<ScheduleEvent> getEventFilterValue() {
		return new FilterValue<ScheduleEvent>() {
			public boolean accept(ScheduleEvent event) {
				int compare = event.getPriority().compareValue
					(getHandlingPriority());
				return compare < 0;
			}
		};
	}
	
	/**
	 * Een methode om de filterwaarde van een item op te vragen.
	 * 
	 * @return true
	 * 		   Als de prioriteit van het event lager is dan die van de scheduler
	 * @return false
	 * 		   Als de prioriteit van het event hoger is dan die van de scheduler
	 */
	private FilterValue<ScheduledItem<?>> getItemFilterValue() {
		return new FilterValue<ScheduledItem<?>>() {
			public boolean accept(ScheduledItem<?> event) {
				int compare = event.getScheduleEvent().getPriority().compareValue
					(getHandlingPriority());
				return compare >= 0;
			}
		};
	}
	
	/**
	 * Handelt de herplanning van dingen die overschreven zijn correct af.
	 * @param 	combination
	 * 			alle scheduleresources die een corrupte schedule kunnen hebben.
	 * @param 	begin
	 * 			vanaf wanneer er geherpland mag worden.
	 */
	protected void handleReschedules(List<ScheduleResource> combination, TimeStamp begin) {
		for (ScheduleResource resource : combination) {
			Schedule schedule = resource.getSchedule();
		
			if (schedule.isOverlapping()) {
				Filter<ScheduleEvent> filter = new Filter<ScheduleEvent>(schedule.getOverlapping());
				for (ScheduleEvent event : filter.getFiltered(getEventFilterValue())) {
					unschedule((Undoable) event);
					try {
						reschedule((Undoable) event);
					} catch (ReschedulingException e) {
						scheduleAfterOverlap(event,begin);
					}
				}
			}
		}
	}	
	
	/**
	 * Geeft de gefilterde versie van de schedule volgens de prioriteit die deze
	 * scheduler hanteerd.
	 * @return
	 */
	protected Schedule filtered() {		
		Filter<ScheduledItem<?>> filter = new Filter<ScheduledItem<?>>(this.getHandlingSchedule().getSchedule());
		
		SortedSet<ScheduledItem<?>> sorted = new TreeSet<ScheduledItem<?>>(filter.getFiltered(getItemFilterValue()));
		
		if (getHandlingSchedule().isShiftSchedule())
			return new ShiftSchedule(getHandlingSchedule().getShiftTable(),sorted);
		else
			return new Schedule(sorted);
	}
	
}
