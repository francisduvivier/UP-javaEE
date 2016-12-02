package system.scheduling;

import java.util.ArrayList;
import java.util.List;

import system.campus.Campus;
import system.campus.CampusId;
import system.exceptions.IllegalOperationException;
import system.exceptions.SchedulingException;
import system.patients.Patient;
import system.repositories.ResourceType;
import system.staff.Doctor;
import system.time.TimeDuration;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.warehouse.Warehouse;
import annotations.SystemAPI;

/**
 * 
 * Een appointment die gemaakt kan worden. Deze appointment geldt tussen een
 * dokter en een patient.
 * 
 * @invar doctor != null
 * @invar patient != null
 * 
 */

public class Appointment implements ScheduleEvent {
	
	/**
	 * Variabele om de duratie van een afspraak vast te stellen.
	 * 
	 * @invar duratie van een afspraak is 30 minuten
	 */
	private static final TimeDuration DURATION = TimeDuration.minutes(30);

	/**
	 * Variabele om een geschedulde tijdsperiode weer te geven
	 */
	private TimePeriod scheduledPeriod;
	
	/**
	 * Variabele om de dokter bij te houden
	 */
	private final Doctor doctor;
	
	/**
	 * Variabele om het eventtype bij te houden
	 */
	private final EventType type;
	
	private final List<ResourceType> neededResources;
	private final List<ScheduleResource> specificResources;

	/**
	 * Variabele om de start van de afspraak bij te houden
	 */
	private AppointmentStart start;
	
	/**
	 * Variabele om de stop van de afspraak bij te houden
	 */
	private AppointmentStop stop;
	
	/**
	 * Variabele om de toestand van de afspraak bij te houden
	 */
	private AppointmentState state;

	/**
	 * De constructor van Appointment
	 * 
	 * @param patient
	 *            De patient die een afspraak wil
	 * @param doctor
	 *            De dokter die de afspraak behandeldt
	 */
	public Appointment(Patient patient, Doctor doctor)
			throws IllegalArgumentException {
		if (doctor == null || patient == null)
			throw new NullPointerException();

		this.doctor = doctor;
		
		this.type = EventType.APPOINTMENT;

		this.neededResources = new ArrayList<ResourceType>();
		this.specificResources = new ArrayList<ScheduleResource>();

		this.specificResources.add(doctor);
		this.specificResources.add(patient);

		this.state = new AppointmentCreated();		
	}

	/**
	 * Methode die de collectie van nodige resources teruggeeft.
	 * 
	 * @return result
	 * 			De nodige resources
	 */
	@Override
	public List<ResourceType> neededResources() {
		return this.neededResources;
	}

	/**
	 * Methode die de geschedulde tijdsperiode teruggeeft.
	 * 
	 * @return result
	 * 			De geschedulde tijdsperiode
	 */
	@SystemAPI
	@Override
	public TimePeriod getScheduledPeriod() {
		return this.scheduledPeriod;
	}

	/**
	 * Methode die kijkt of het warenhuis gescheduled kan worden.
	 * 
	 * @param warehouse
	 * 			te controleren warenhuis
	 * @return true
	 */
	@Override
	public boolean canBeScheduled(Warehouse warehouse) {
		return true;
	}
	
	/**
	 * Methode die een collectie van nodige resources teruggeeft.
	 * 
	 * @return result
	 * 			Collectie van nodige resources
	 */
	@Override
	public List<ScheduleResource> neededSpecificResources() {
		return this.specificResources;
	}

	/**
	 * Methode om de duratie van een afspraak terug te krijgen
	 * 
	 * @return duration
	 * 			Duur van de afspraak (30 minuten)
	 */
	@Override
	public TimeDuration getDuration() {
		return DURATION;
	}

	/**
	 * ToString methode van de klasse Appointment.
	 * O.a. bruikbaar voor debuggen of de UI.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Appointment with " + doctor.getName() + " at "
				+ scheduledPeriod;
	}
	
	/**
	 * Methode die het eventtype teruggeeft.
	 * 
	 * @return result
	 * 			Het eventtype
	 */
	@Override
	public EventType getEventType() {
		return this.type;
	}

	/**
	 * Methode die het startevent van de afspraak teruggeeft.
	 * 
	 * @return start
	 * 			Startevent van de afspraak
	 */
	@Override
	public Event getStart() {
		return start;
	}

	/**
	 * Methode die het stopevent van de afspraak teruggeeft.
	 * 
	 * @return stop
	 * 		   Stopevent van de afspraak
	 */
	@Override
	public Event getStop() {
		return stop;
	}
	
	/**
	 * Methode die de prioriteit van de afspraak teruggeeft.
	 * 
	 * @return result
	 * 		   Een afspraak heeft geen prioriteit
	 */
	@Override
	public Priority getPriority() {
		return Priority.NO_PRIORITY;
	}
	
	/**
	 * Methode die de afspraaksstate teruggeeft.
	 * 
	 * @return state
	 * 		   De toestand van de afspraak.
	 */
	private AppointmentState getState() {
		return state;
	}

	/**
	 * Methode om de toestand van de afspraak in te stellen.
	 * 
	 * @param state
	 * 		  De gewenste toestand
	 */
	private void setState(AppointmentState state) {
		this.state = state;
	}
	
	/**
	 * Variabele die de campusid van de respectievelijke campus weergeeft.
	 */
	private CampusId handlingCampus;
	
	/**
	 * Methode om de respectievelijke campus in te stellen naar campusid.
	 * 
	 * @param campus
	 * 		  De campusid van handling campus
	 */
	@Override
	public void setHandlingCampus(CampusId campus) {
		this.handlingCampus = campus;
	}
	
	/**
	 * Methode die de campusid van de respectievelijke campus teruggeeft.
	 */
	@Override
	public CampusId getHandlingCampus() {
		return this.handlingCampus;
	}

	/**
	 * AppointmentStart is een inner class die nodig is omwille van overerving uit ScheduleEvent.
	 */
	private class AppointmentStart extends Event {
		
		/**
		 * Constructor van AppointmentStart
		 * 
		 * @param executionTime
		 * 			Tijdsstip van de startuitvoeringstijd van de afspraak
		 */
		private AppointmentStart(TimeStamp executionTime) {
			super(executionTime);
		}

		/**
		 * Methode die de state start.
		 */
		@Override
		public void execute() {
			Appointment.this.getState().start();
		}
	}

	/**
	 * AppointmentStop is een inner class die nodig is omwille van overerving uit ScheduleEvent.	 *
	 */
	private class AppointmentStop extends Event {
		
		/**
		 * Constructor van AppointmentStop
		 * 
		 * @param executionTime
		 * 			Tijdsstip van de stopuitvoeringstijd van de afspraak
		 */
		private AppointmentStop(TimeStamp executionTime) {
			super(executionTime);
		}

		/**
		 * Methode die de state stopt.
		 */
		@Override
		public void execute() {
			Appointment.this.getState().stop();
		}
	}
	
	/**
	 * AppointmentState is een inner class en is nodig voor het State patroon toe te passen.
	 */
	private abstract class AppointmentState {
		public abstract void schedule(TimePeriod scheduledPeriod, List<ScheduleResource> usesResources, Campus campus) 
			throws IllegalOperationException, SchedulingException;
		public abstract void start() throws IllegalOperationException;
		public abstract void stop() throws IllegalOperationException;
	}
	
	/**
	 * AppointmentCreated is een inner class en een mogelijke toestand van AppointmentState.
	 */
	private class AppointmentCreated extends AppointmentState {
		
		/**
		 * Constructor van AppointmentCreated.
		 * 
		 * @param scheduledPeriod
		 * 			Periode waarover de schedule gaat
		 * @param usesResources
		 * 			Collectie van de resources die de schedule gebruikt
		 * @param campus
		 * 			Campus waar de schedule van toepassing is.
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod, List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException, SchedulingException {
			Appointment.this.scheduledPeriod = scheduledPeriod;
			
			for (ScheduleResource resource : usesResources) 
				resource.getSchedule().addItem(scheduledPeriod, Appointment.this, campus);
			
			start = new AppointmentStart(scheduledPeriod.getBegin());
			stop = new AppointmentStop(scheduledPeriod.getEnd());
			
			Appointment.this.setState(new AppointmentPlanned());
			
		}

		/**
		 * Methode voor toestand AppointmentCreated te starten
		 * 
		 * @throws IllegalOperationException
		 * 			Methode mag niet uitgevoerd worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode voor toestand AppointmentCreated te stoppen
		 * 
		 * @throws IllegalOperationException
		 * 			Methode mag niet uitgevoerd worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}
	
	/**
	 * AppointmentPlanned is een inner class en een mogelijke toestand van AppointmentState.
	 */
	private class AppointmentPlanned extends AppointmentState {
		
		/**
		 * Constructor van AppointmentPlanned.
		 * 
		 * @param scheduledPeriod
		 * 			Periode waarover de schedule gaat
		 * @param usesResources
		 * 			Collectie van de resources die de schedule gebruikt
		 * @param campus
		 * 			Campus waar de schedule van toepassing is.
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod, List<ScheduleResource> useResources, Campus campus) 
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode die toestand van AppointmentPlanned start.
		 * Deze stelt als nieuwe toestand een object van AppointmentInProgress in.
		 */
		@Override
		public void start() throws IllegalOperationException {
			Appointment.this.setState(new AppointmentInProgress());
		}

		/**
		 * Methode voor toestand AppointmentPlanned te stoppen.
		 * 
		 * @throws IllegalOperationException
		 * 			Methode mag niet uitgevoerd worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}
	
	/**
	 * AppointmentInProgress is een inner class en een mogelijke toestand van AppointmentState.
	 */
	private class AppointmentInProgress extends AppointmentState {
		
		/**
		 * Constructor van AppointmentInProgress.
		 * 
		 * @param scheduledPeriod
		 * 			Periode waarover de schedule gaat
		 * @param usesResources
		 * 			Collectie van de resources die de schedule gebruikt
		 * @param campus
		 * 			Campus waar de schedule van toepassing is.
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod, List<ScheduleResource> useResources, Campus campus) 
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}
		
		/**
		 * Methode voor toestand AppointmentInProgress te starten
		 * 
		 * @throws IllegalOperationException
		 * 			Methode mag niet uitgevoerd worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();	
		}

		/**
		 * Methode voor toestand AppointmentInProgress te stoppen
		 * 
		 * @throws IllegalOperationException
		 * 			Methode mag niet uitgevoerd worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			Appointment.this.setState(new AppointmentFinished());
		}
	}
	
	/**
	 * AppointmentFinished is een inner class en een mogelijke toestand van AppointmentState.
	 * @author Thijs
	 *
	 */
	private class AppointmentFinished extends AppointmentState {
		
		/**
		 * Constructor van AppointmentFinished.
		 * 
		 * @param scheduledPeriod
		 * 			Periode waarover de schedule gaat
		 * @param usesResources
		 * 			Collectie van de resources die de schedule gebruikt
		 * @param campus
		 * 			Campus waar de schedule van toepassing is.
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod, List<ScheduleResource> useResources, Campus campus) 
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode voor toestand AppointmentFinished te starten
		 * 
		 * @throws IllegalOperationException
		 * 			Methode mag niet uitgevoerd worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode voor toestand AppointmentFinished te stoppen
		 * 
		 * @throws IllegalOperationException
		 * 			Methode mag niet uitgevoerd worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}

	/**
	 * Methode om een tijdsperiode met resources van een campus in de toestand van de afspraak te schedulen.
	 * 
	 * @param scheduledPeriod
	 * 			Periode waarover de schedule gaat
	 * @param usesResources
	 * 			Collectie van de resources die de schedule gebruikt
	 * @param campus
	 * 			Campus waar de schedule van toepassing is
	 */
	@Override
	public void schedule(TimePeriod scheduledPeriod, List<ScheduleResource> usesResources, Campus campus) 
			throws SchedulingException {
		this.getState().schedule(scheduledPeriod, usesResources, campus);	
	}

	/**
	 * Methode om het warenhuis te updaten. (doet niets; nodige override)
	 * 
	 * @param warehouse
	 * 			warenhuis waarmee geupdate moet worden
	 * @param inverse
	 * 			inverse van de boolean
	 */
	@Override
	public void updateWarehouse(Warehouse warehouse, boolean inverse) {
		return;
	}
}
