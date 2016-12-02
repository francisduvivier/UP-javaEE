package system.medicaltests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import system.campus.Campus;
import system.campus.CampusId;
import system.exceptions.IllegalOperationException;
import system.exceptions.ReschedulingException;
import system.exceptions.ResultMismatchException;
import system.exceptions.SchedulingException;
import system.patients.Patient;
import system.repositories.ResourceType;
import system.repositories.StaffType;
import system.results.PatientOperation;
import system.results.Result;
import system.scheduling.Event;
import system.scheduling.EventType;
import system.scheduling.Priority;
import system.scheduling.ScheduleResource;
import system.scheduling.Undoable;
import system.time.TimeDuration;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.warehouse.Warehouse;


/**
 * Deze klasse is de superklasse van alle soorten medische tests. Omdat deze de
 * Schedulable interface implementeert kan ze met de Scheduler gepland worden.
 * 
 * @invar patient != null
 * 
 * @author SWOP Team 10
 */
public abstract class MedicalTest implements PatientOperation,
		Undoable {
	/**
	 * De duur van de medische test
	 */
	private final TimeDuration duration;
	/**
	 * Het resultaat van de medische test
	 */
	private Result result;
	/**
	 * De patient waarop de medische test moet uitgevoerd worden
	 */
	private final Patient patient;
	/**
	 * De geplande tijdsperiode waarbinnen de medische test plaatsvindt
	 */
	private TimePeriod scheduledPeriod;
	/**
	 * De prioriteit waarmee deze test moet uitgevoerd worden.
	 */
	private final Priority priority;
	
	/**
	 * Deze drie variabelen zijn noodzakelijk voor het Scheduling systeem.
	 * 
	 * Wanneer medicaltest wordt geinitialiseerd, zal een dummy object nurse
	 * aan neededResources worden toegevoegd. Dit zorgt ervoor dat de Scheduler weet dat
	 * er steeds een Nurse nodig is voor een MedicalTest.
	 */
	private final EventType type;
	protected final List<ResourceType> neededResources;
	protected final List<ScheduleResource> specificResources;

	/**
	 * Een medische test kan gezien worden als twee events: een start en een stop
	 * event. Deze zijn geimplementeerd als de inner klassen MedicalTestStart en
	 * MedicalTestStop.
	 */
	private MedicalTestStart start;
	private MedicalTestStop stop;
	/**
	 * De toestand waarin een behandeling zich bevindt
	 */
	private MedicalTestState state;
	
	/**
	 * Initialisatie van de medische test
	 * 
	 * @param patient
	 *        De patient waarop de medische test moet uitgevoerd worden
	 * @param duration
	 *        De duur van de medische test
	 * @param type
	 *        Een object van de enum EventType
	 */
	protected MedicalTest(Patient patient, TimeDuration duration, EventType type, Priority priority) {
		if (patient == null)
			throw new NullPointerException();

		this.type = type;

		this.patient = patient;
		neededResources = new ArrayList<ResourceType>();
		neededResources.add(StaffType.NURSE);

		specificResources = new ArrayList<ScheduleResource>();
		specificResources.add(patient);

		this.duration = duration;

		this.state = new MedicalTestCreated();
		
		this.priority = priority;
	}

	/**
	 * In het geval van een medicaltest moet er niks veranderd worden in de
	 * Warehouse.
	 */
	@Override
	public void updateWarehouse(Warehouse warehouse, boolean inverse) {
		return;
	}

	/**
	 * Een methode om te controleren of een object van de klasse gepland kan worden.
	 * Deze geeft true terug aangezien medische tests gepland kunnen worden.
	 * 
	 * @return true
	 * 		   Een medische test kan gepland worden
	 */
	@Override
	public boolean canBeScheduled(Warehouse warehouse) {
		return true;
	}

	/**
	 * Een methode om de geplande tijdsperiode van de medische test terug te krijgen.
	 * 
	 * @return scheduledPeriod
	 * 		   De geplande tijdsperiode
	 */
	@Override
	public TimePeriod getScheduledPeriod() {
		return scheduledPeriod;
	}
	/**
	 * Deze methode mag eigenlijk enkel opgeroepen worden door de scheduler.
	 * 
	 * @param	scheduledPeriod
	 * 		  	Tijdsperiode om te plannen
	 * @param	usesResources
	 * 			De gebruikte resources
	 * @param	campus
	 * 			De campus waar gepland moet worden
	 */
	@Override
	public void schedule(TimePeriod scheduledPeriod,
			List<ScheduleResource> usesResources, Campus campus) {
		this.getState().schedule(scheduledPeriod, usesResources, campus);
	}

	/**
	 * Methode om de (geplande) medische test uit de planning te verwijderen
	 */
	@Override
	public void cancel() {
		this.getState().cancel();
	}
	
	/**
	 * Methode om de gecancelled medische test terug te plannen
	 */
	@Override
	public void redo() {
		this.getState().redo();
	}
	
	/**
	 * Methode om de medische uit het patientendossier te verwijderen
	 */
	private void unregister() {
		getPatient().getPatientFile().removeMedicalTest(this);		
	}

	/**
	 * Methode om de medische test aan het patientendossier toe te voegen.
	 */
	private void register() {
		getPatient().getPatientFile().addMedicalTest(this);		
	}
	
	/**
	 * Getter voor het resultaat van een medical test
	 * 
	 * @return 	result
	 * 			Het resultaat van de medische test
	 */
	@Override
	public Result getResult() {
		return this.result;
	}

	/**
	 * Setter voor het resultaat van een medical test.
	 * 
	 * @param	result
	 * 			Het in te stellen resultaat
	 */
	@Override
	public void setResult(Result result) {
		this.getState().setResult(result);
	}

	/**
	 * Methode om een lijst met nodige resources op te halen
	 * 
	 * @return	neededSpecificResources
	 * 			Een lijst met de nodige resources
	 */
	@Override
	public List<ScheduleResource> neededSpecificResources() {
		return Collections.unmodifiableList(specificResources);
	}

	/**
	 * Getter voor de duratie van de medical test
	 * 
	 * @return 	duration
	 * 			De tijdsduratie van de test
	 */
	@Override
	public TimeDuration getDuration() {
		return this.duration;
	}

	/**
	 * Methode om de patient waarop de medische test uitgevoerd moet worden, terug te geven
	 * 
	 * @return patient
	 *         De patient waarop de medische test uitgevoerd moet worden
	 */
	@Override
	public Patient getPatient() {
		return this.patient;
	}
	
	/**
	 * Getter voor het type event
	 * 
	 * @return type
	 *         Het type event
	 */
	@Override
	public EventType getEventType() {
		return this.type;
	}

	/**
	 * Deze methode geeft aan scheduler mee welke resources belangrijk zijn voor
	 * deze medicaltest
	 */
	@Override
	public List<ResourceType> neededResources() {
		return Collections.unmodifiableList(neededResources);
	}

	/**
	 * Getter voor het start event van de medische test
	 * 
	 * @return Event
	 */
	@Override
	public Event getStart() {
		return start;
	}

	/**
	 * Getter voor het stop event van de medische behandeling
	 * 
	 * @return Event
	 */
	@Override
	public Event getStop() {
		return stop;
	}

	/**
	 * Getter voor de huidige toestand van de medische test
	 * 
	 * @return state
	 *         De huidige toestand van de medische test
	 */
	private MedicalTestState getState() {
		return state;
	}

	/**
	 * Setter voor de toestand van de medische test
	 * 
	 * @param state
	 *        De nieuwe toestand van de medische test
	 */
	private void setState(MedicalTestState state) {
		this.state = state;
	}

	/**
	 * Getter voor de prioriteit van de medical test
	 * 
	 * @return prioriteit van de medical test
	 */
	@Override
	public Priority getPriority() {
		return this.priority;
	}
	
	/**
	 * Methode om te controleren of de medical test nog een resultaat nodig heeft
	 * 
	 * @return True als er nog een resultaat nodig is
	 */
	@Override
	public boolean needsResult() {
		return (this.getState().needsResult());
	}
	
	/**
	 * Methode om te controleren of de medical test alle stappen heeft doorlopen
	 * 
	 * @return true als de medical test alle stappen doorlopen heeft
	 */
	@Override
	public boolean isFinished() {
		return this.getState().isFinished();
	}
	
	/**
	 * Inner klasse die het start event van de medische test voorstelt
	 */
	private class MedicalTestStart extends Event {
		public MedicalTestStart(TimeStamp executionTime) {
			super(executionTime);
		}

		@Override
		public void execute() {
			MedicalTest.this.getState().start();
		}
	}

	/**
	 * Inner klasse die het stop event van de medische test voorstelt
	 */
	private class MedicalTestStop extends Event {
		public MedicalTestStop(TimeStamp executionTime) {
			super(executionTime);
		}

		@Override
		public void execute() {
			MedicalTest.this.getState().stop();
		}
	}

	/**
	 * Inner klasse die de toestand van de medische test voorstelt.
	 * Er wordt hier gebruik gemaakt van het State patroon
	 */
	private abstract class MedicalTestState {
		/**
		 * Methode om de medische test te plannen
		 * 
		 * @param scheduledPeriod
		 *        De periode waarover de medische test gepland wordt
		 * @param usesResources
		 *        De resources die nodig zijn om de medische test te plannen
		 * @param campus FIXME
		 * @throws IllegalOperationException
		 *         Als de medische test zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 * @throws SchedulingException
		 *         Als er iets misloopt met het plannen van de medische test
		 */
		public abstract void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException, SchedulingException;

		/**
		 * Methode om aan te geven dat de medische test begonnen is.
		 * 
		 * @throws IllegalOperationException
		 *         Als de medische test zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void start() throws IllegalOperationException;

		/**
		 * Methode om aan te geven dat de medische test gedaan is.
		 * 
		 * @throws IllegalOperationException
		 *         Als de medische test zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void stop() throws IllegalOperationException;

		/**
		 * Methode om het plannen van de medische test ongedaan te maken
		 * 
		 * @throws IllegalOperationException
		 *         Als de medische test zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void cancel() throws IllegalOperationException;

		/**
		 * Methode om de medische test opnieuw te plannen
		 * 
		 * @throws IllegalOperationException
		 *         Als de medische test zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 * @throws SchedulingException
		 *         Als er iets misloopt met het plannen van de medische test
		 */
		public abstract void redo() throws IllegalOperationException;

		/**
		 * Methode om het resultaat van de medische test in te stellen
		 * 
		 * @param result
		 *        Het resultaat van de medische test
		 * @throws IllegalOperationException
		 *         Als de medische test zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void setResult(Result result)
				throws IllegalOperationException;

		/**
		 * Methode om aan te geven of de medische test een resultaat nodig heeft
		 * 
		 * @return true
		 *         Als de medische test gedaan is
		 *         false
		 *         Als de medische test nog niet gedaan is
		 */
		public boolean needsResult() {
			return false;
		}
		
		/**
		 * Methode om aan te geven of de behandeling finished is, met resultaat.
		 * 
		 * @return true
		 * 		   Als de medische test gedaan is met resultaat
		 * 		   false
		 * 		   Als de medische test niet gedaan is of geen resultaat heeft
		 */
		public boolean isFinished() {
			return false;
		}
	}

	/**
	 * De Created state van MedicalTest.
	 */
	private class MedicalTestCreated extends MedicalTestState {
		
		/**
		 * Een methode om een medical test in toestand created te plannen.
		 * ENKEL medical tests in deze toestand kunnen gepland worden
		 * 
		 * @param	scheduledPeriod
		 * 			De tijdsperiode waarover gepland moet worden
		 * @param	usesResources
		 * 			De resources die gebruikt worden
		 * @param	campus
		 * 			De campus waar gepland moet worden
		 * 
		 * @throws	SchedulingException
		 * 			Als er niet gepland kan worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws SchedulingException {
			MedicalTest.this.scheduledPeriod = scheduledPeriod;

			for (ScheduleResource resource : usesResources)
				resource.getSchedule().addItem(scheduledPeriod,
						MedicalTest.this,campus);

			MedicalTest.this.start = new MedicalTestStart(scheduledPeriod.getBegin());
			MedicalTest.this.stop = new MedicalTestStop(scheduledPeriod.getEnd());
			MedicalTest.this.register();
			MedicalTest.this.setState(new MedicalTestPlanned(usesResources,
					scheduledPeriod, campus));
			}

		/**
		 * Een methode om een medical test in toestand created te starten.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestart worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand created te stoppen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestopt worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand created te annuleren.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet geannuleerd worden
		 */
		@Override
		public void cancel() {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand created te herdoen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet herdaan worden
		 */
		@Override
		public void redo() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een resultaat in te stellen van medical test in toestand created.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan geen resultaat krijgen
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}
	
	private CampusId handlingCampus;
	
	@Override
	public void setHandlingCampus(CampusId campus) {
		this.handlingCampus = campus;
	}
	
	@Override
	public CampusId getHandlingCampus() {
		return this.handlingCampus;
	}
	
	private class MedicalTestPlanned extends MedicalTestState {
		private final List<ScheduleResource> usesResources;
		private final TimePeriod scheduledPeriod;
		private final Campus campus;

		public MedicalTestPlanned(List<ScheduleResource> usesResources,
				TimePeriod scheduledPeriod, Campus campus) {
			this.usesResources = usesResources;
			this.scheduledPeriod = scheduledPeriod;
			this.campus = campus;
		}

		/**
		 * Een methode om een medical test in toestand plannend te plannen.
		 * 
		 * @param	scheduledPeriod
		 * 			De tijdsperiode waarover gepland moet worden
		 * @param	usesResources
		 * 			De resources die gebruikt worden
		 * @param	campus
		 * 			De campus waar gepland moet worden
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test van deze state kan niet gepland worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand created te starten.
		 */
		@Override
		public void start() throws IllegalOperationException {
			MedicalTest.this.setState(new MedicalTestInProgress());
		}

		/**
		 * Een methode om een medical test in toestand planned te stoppen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestopt worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand planned te annuleren.
		 * Enkel medical tests van deze toestand, nl. planned, kunnen
		 * geannuleerd worden.
		 */
		@Override
		public void cancel() {
			for (ScheduleResource resource : this.usesResources)
				resource.getSchedule().removeItem(MedicalTest.this);
			MedicalTest.this.unregister();	
			MedicalTest.this.scheduledPeriod = null;
			MedicalTest.this.setState(new MedicalTestCancelled(
					this.usesResources, this.scheduledPeriod,this.campus));
		}

		/**
		 * Een methode om een medical test in toestand planned te herdoen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet herdaan worden
		 */
		@Override
		public void redo() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een resultaat in te stellen van medical test in toestand planned.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan geen resultaat krijgen
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}

	private class MedicalTestCancelled extends MedicalTestState {
		private final List<ScheduleResource> usesResources;
		private final TimePeriod scheduledPeriod;
		private final Campus campus;

		public MedicalTestCancelled(List<ScheduleResource> usesResources,
				TimePeriod scheduledPeriod, Campus campus) {
			this.usesResources = usesResources;
			this.scheduledPeriod = scheduledPeriod;
			this.campus = campus;
		}

		/**
		 * Een methode om een medical test in toestand cancelled te plannen.
		 * 
		 * @param	scheduledPeriod
		 * 			De tijdsperiode waarover gepland moet worden
		 * @param	usesResources
		 * 			De resources die gebruikt worden
		 * @param	campus
		 * 			De campus waar gepland moet worden
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test van deze state kan niet gepland worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand cancelled te starten.
		 */
		@Override
		public void start() {
			MedicalTest.this.setState(new MedicalTestInProgress());
		}

		/**
		 * Een methode om een medical test in toestand cancelled te stoppen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestopt worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand cancelled te annuleren.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet geannuleerd worden
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand cancelled te herdoen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet herdaan worden
		 */
		@Override
		public void redo() throws ReschedulingException {
			try {
				for (ScheduleResource resource : this.usesResources)
					resource.getSchedule().addItem(this.scheduledPeriod,
							MedicalTest.this,this.campus);
				MedicalTest.this.register();
				MedicalTest.this.scheduledPeriod = this.scheduledPeriod;
				MedicalTest.this.setState(new MedicalTestPlanned(
						this.usesResources, this.scheduledPeriod,this.campus));
			} catch (SchedulingException e) {
				for (ScheduleResource resource : this.usesResources)
					resource.getSchedule().removeItem(MedicalTest.this);
				
				MedicalTest.this.scheduledPeriod = null;
				
				MedicalTest.this.setState(new MedicalTestCreated());
				throw new ReschedulingException(MedicalTest.this.toString()
						+ " has to be rescheduled.");
			}
		}

		/**
		 * Een methode om een resultaat in te stellen van medical test in toestand cancelled.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan geen resultaat krijgen
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}

	private class MedicalTestInProgress extends MedicalTestState {
		
		/**
		 * Een methode om een medical test in toestand in progress te plannen.
		 * 
		 * @param	scheduledPeriod
		 * 			De tijdsperiode waarover gepland moet worden
		 * @param	usesResources
		 * 			De resources die gebruikt worden
		 * @param	campus
		 * 			De campus waar gepland moet worden
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test van deze state kan niet gepland worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand in progress te starten.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestart worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand in progress te stoppen.
		 * Enkel een medical test in deze toestand kan gestopt worden.
		 */
		@Override
		public void stop() {
			MedicalTest.this.setState(new MedicalTestNeedsResult());
		}

		/**
		 * Een methode om een medical test in toestand in progress te annuleren.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet geannuleerd worden
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand in progress te herdoen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet herdaan worden
		 */
		@Override
		public void redo() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een resultaat in te stellen van medical test in toestand in progress.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan geen resultaat krijgen
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}

	private class MedicalTestNeedsResult extends MedicalTestState {
		
		/**
		 * Een methode om een medical test in toestand needs result te plannen.
		 * 
		 * @param	scheduledPeriod
		 * 			De tijdsperiode waarover gepland moet worden
		 * @param	usesResources
		 * 			De resources die gebruikt worden
		 * @param	campus
		 * 			De campus waar gepland moet worden
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test van deze state kan niet gepland worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand needs result te starten.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestart worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand needs result te stoppen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestopt worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand needs result te annuleren.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet geannuleerd worden
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand in progress te herdoen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet herdaan worden
		 */
		@Override
		public void redo() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een resultaat in te stellen van medical test in toestand planned.
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException,
				NullPointerException, ResultMismatchException {
			if (result == null)
				throw new NullPointerException("Result is null.");
			if (MedicalTest.this.type.getResultType() != result.getResultType())
				throw new ResultMismatchException(
						"Event en result komen niet overeen.");

			MedicalTest.this.result = result;

			MedicalTest.this.setState(new MedicalTestFinished());
		}

		/**
		 * Een medical test in toestand planned heeft een resultaat nodig.
		 * 
		 * @return	true
		 * 			De medical test heeft een resultaat nodig
		 */
		@Override
		public boolean needsResult() {
			return true;
		}
	}

	private class MedicalTestFinished extends MedicalTestState {
		
		/**
		 * Een methode om een medical test in toestand finished te plannen.
		 * 
		 * @param	scheduledPeriod
		 * 			De tijdsperiode waarover gepland moet worden
		 * @param	usesResources
		 * 			De resources die gebruikt worden
		 * @param	campus
		 * 			De campus waar gepland moet worden
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test van deze state kan niet gepland worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand finished te starten.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestart worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand finished te stoppen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet gestopt worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand finished te annuleren.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet geannuleerd worden
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een medical test in toestand finished te herdoen.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan niet herdaan worden
		 */
		@Override
		public void redo() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een resultaat in te stellen van medical test in toestand finished.
		 * 
		 * @throws	IllegalOperationException
		 * 			Een medical test in deze toestand kan geen resultaat krijgen
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
		
		/**
		 * Een methode om te controleren of een medical test afgelopen is.
		 * 
		 * @return	true
		 * 			Medical test in toestand finished geeft true terug
		 */
		@Override
		public boolean isFinished() {
			return true;
		}
	}
}
