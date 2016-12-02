package system.treatments;

import java.util.ArrayList;
import java.util.List;

import system.campus.Campus;
import system.campus.CampusId;
import system.exceptions.IllegalOperationException;
import system.exceptions.ReschedulingException;
import system.exceptions.ResultMismatchException;
import system.exceptions.SchedulingException;
import system.patients.Diagnosis;
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
 * Deze klasse is de superklasse van alle soorten behandelingen. Omdat deze de
 * Schedulable interface implementeert kan ze met de Scheduler gepland worden.
 * 
 * @invar duration != null
 * @invar diagnosis != null
 * @invar priority != null
 * 
 * @author SWOP Team 10
 */
public abstract class Treatment implements PatientOperation,
		Undoable {
	/**
	 * Variabele die de geplande tijdsperiode waarbinnen de behandeling
	 * plaatsvindt, voorstelt.
	 */
	private TimePeriod scheduledPeriod;
	/**
	 * Variabele die de duur van de behandeling voorstelt.
	 */
	protected final TimeDuration duration;
	/**
	 * Variabele die de diagnosis op basis waarvan de behandeling wordt gegeven,
	 * voorstelt.
	 */
	private final Diagnosis diagnosis;

	/**
	 * Deze drie variabelen zijn noodzakelijk voor het Scheduling systeem.
	 * 
	 * Wanneer een instantie van Treatment gemaakt wordt, wordt er een dummy
	 * instansie van Nurse meegegeven aan de neededResources list. Deze vertelt
	 * de Scheduler dat er steeds een Nurse nodig is voor een Treatment.
	 */
	private final EventType type;
	protected final List<ResourceType> neededResources;
	protected final List<ScheduleResource> specificResources;

	/**
	 * Een behandeling kan gezien worden als twee events: een start en een stop
	 * event. Deze zijn geimplementeerd als de inner klassen TreatmentStart en
	 * TreatmentStop.
	 */
	private TreatmentStart start;
	private TreatmentStop stop;
	/**
	 * De toestand waarin een behandeling zich bevindt
	 */
	private TreatmentState state;
	/**
	 * Variabele die het resultaat van de behandeling voorstelt
	 */
	private Result result;
	
	/**
	 * De prioriteit waarmee deze test moet uitgevoerd worden.
	 */
	private final Priority priority;
	
	/**
	 * Initialisatie van de behandeling
	 * 
	 * @param diagnosis
	 *            De diagnose op basis waarvan de behandeling wordt gegeven
	 * @param duration
	 *            De duur van de behandeling
	 * @param type
	 *            Een object van de enum EventType
	 */
	protected Treatment(Diagnosis diagnosis, TimeDuration duration, EventType type, Priority priority) {
		neededResources = new ArrayList<ResourceType>();
		neededResources.add(StaffType.NURSE);
		specificResources = new ArrayList<ScheduleResource>();
		
		if(priority == null)
			throw new NullPointerException("Priority is null.");
		this.priority = priority;

		this.duration = duration;
		if (diagnosis == null)
			throw new NullPointerException("Diagnosis is null.");
		this.diagnosis = diagnosis;
		specificResources.add(diagnosis.getPatient());
		this.state = new TreatmentCreated();
		this.type = type;
	}

	/**
	 * Methode die aangeeft of er voor de behandeling voldoende gips en/of
	 * medicijnen in het magazijn liggen.
	 * 
	 * @return true Als er voldoende gips en/of medicijnen in het magazijn zijn
	 *         false Als er onvoldoende gips en/of medicijnen in het magazijn
	 *         zijn
	 */
	@Override
	public abstract boolean canBeScheduled(Warehouse warehouse);

	/**
	 * Een methode om de tijdsperiode waarover de treatment gepland is op te vragen.
	 * 
	 * @return scheduledPeriod
	 * 		   De tijdsperiode waarover de treatment gepland is
	 */
	@Override
	public TimePeriod getScheduledPeriod() {
		return scheduledPeriod;
	}

	/**
	 * Methode om de periode waarover behandeling moet gepland worden, in te
	 * stellen.
	 * 
	 * @param scheduledPeriod
	 *            De periode waarover de behandling gepland wordt
	 * @param usesResources
	 *            De patient, de machines en het personeel dat nodig is voor de
	 *            behandeling
	 */
	@Override
	public void schedule(TimePeriod scheduledPeriod,
			List<ScheduleResource> usesResources, Campus campus) throws SchedulingException {
		this.getState().schedule(scheduledPeriod, usesResources, campus);
	}

	/**
	 * Methode die ervoor zorgt dat de behandeling opgeslagen wordt. Dit
	 * betekent niet dat de behandeling gepland is.
	 */
	public void store() {
		this.getState().store();
	}

	/**
	 * Methode om de (geplande) behandeling uit de planning te verwijderen
	 */
	@Override
	public void cancel() throws SchedulingException {
		this.getState().cancel();
	}

	/**
	 * Methode om de periode waarover behandeling gepland moet worden in te
	 * stellen.
	 */
	@Override
	public void redo() throws SchedulingException {
		this.getState().redo();
	}

	/**
	 * Getter voor de duratie van een behandeling
	 * 
	 * @return duration
	 * 		   De duur van de treatment
	 */
	@Override
	public TimeDuration getDuration() {
		return duration;
	}

	/**
	 * Lijst van de nodige resources
	 * 
	 * @return neededResources
	 * 		   Lijst van de nodige resources
	 */
	@Override
	public List<ResourceType> neededResources() {
		return this.neededResources;
	}

	/**
	 * Getter voor de patient behorende bij de diagnose waarvoor de treatment is opgesteld
	 * 
	 * @return diagnosis.getPatient()
	 * 		   De patiennt die de treatment krijgt
	 */
	@Override
	public Patient getPatient() {
		return diagnosis.getPatient();
	}

	/**
	 * Methode om de nodige resources terug te geven.
	 * 
	 * @return resources patient, machines en personeel
	 */
	@Override
	public List<ScheduleResource> neededSpecificResources() {
		return this.specificResources;
	}

	/**
	 * Getter voor het resultaat van de treatment
	 * 
	 * @return result
	 * 		   Het resultaat van de treatment
	 */
	@Override
	public Result getResult() {
		return result;
	}

	/**
	 * Setter voor het resultaat van de treatment
	 * 
	 * @param result
	 * 			In te stellen resultaat
	 */
	@Override
	public void setResult(Result result) {
		this.getState().setResult(result);
	}

	/**
	 * Getter voor de diagnose op basis waarvan de behandeling wordt gegeven
	 * 
	 * @return diagnosis
	 *         De diagnose op basis waarvan de behandeling wordt gegeven
	 */
	public Diagnosis getDiagnosis() {
		return diagnosis;
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
	 * Methode om te controleren of de treatment een resultaat nodig heeft.
	 * 
	 * @return true
	 *         Als de toestand van de treatment een resultaat nodig heeft
	 * @return false
	 * 		   Als de toestand van de treatment geen resultaat nodig heeft
	 */
	@Override
	public boolean needsResult() {
		return this.getState().needsResult();
	}

	/**
	 * Methode om te kijken of de treatment het einde van zijn levensloop heeft bereikt.
	 * 
	 * @return true
	 *         Als de toestand van de treatment afgelopen is
	 * @return false
	 * 		   Als de toestand van de treatment niet afgelopen is
	 */
	@Override
	public boolean isFinished() {
		return this.getState().isFinished();
	}
	
	/**
	 * Getter voor het start event van de behandeling
	 * 
	 * @return Event
	 *         Het startevent
	 */
	@Override
	public Event getStart() {
		return start;
	}

	/**
	 * Getter voor het stop event van de behandeling
	 * 
	 * @return Event
	 * 		   Het stopevent
	 */
	@Override
	public Event getStop() {
		return stop;
	}

	/**
	 * Getter voor de huidige toestand van de behandeling
	 * 
	 * @return state
	 *         De huidige toestand van de behandeling
	 */
	private TreatmentState getState() {
		return this.state;
	}

	/**
	 * Setter voor de toestand van de behandeling
	 * 
	 * @param state
	 *        De nieuwe toestand van de behandeling
	 */
	private void setState(TreatmentState state) {
		this.state = state;
	}
	
	/**
	 * Getter voor de prioriteit van de treatment
	 * 
	 * @return priority
	 * 		   De prioriteit van de treatment
	 */
	@Override
	public Priority getPriority() {
		return this.priority;
	}
	
	private CampusId handlingCampus;
	
	/**
	 * Een methode om de campus behorende bij de treatment in te stellen.
	 * 
	 * @param campus
	 * 		  De bijbehorende campus
	 */
	@Override
	public void setHandlingCampus(CampusId campus) {
		this.handlingCampus = campus;
	}
	
	/**
	 * Een methode om de campus horende bij de treatment op te vragen.
	 * 
	 * @return handlingCampus
	 * 		   De campus horende bij de treatment
	 */
	@Override
	public CampusId getHandlingCampus() {
		return this.handlingCampus;
	}

	/**
	 * Inner klasse die het start event van de behandeling voorstelt
	 */
	private class TreatmentStart extends Event {
		public TreatmentStart(TimeStamp executionTime) {
			super(executionTime);
		}

		/**
		 * Een methode die de treatment van de toestand start uitvoert.
		 */
		@Override
		public void execute() {
			Treatment.this.getState().start();
		}
	}

	/**
	 * Inner klasse die het stop event van de behandeling voorstelt
	 */
	private class TreatmentStop extends Event {
		public TreatmentStop(TimeStamp executionTime) {
			super(executionTime);
		}

		/**
		 * Een methode die de treatment van de toestand Stop uitvoert.
		 */
		@Override
		public void execute() {
			Treatment.this.getState().stop();
		}
	}

	/**
	 * Inner klasse die de toestand van de behandeling voorstelt.
	 * Er wordt hier gebruik gemaakt van het State patroon
	 */
	private abstract class TreatmentState {
		/**
		 * Methode om de behandeling te plannen
		 * 
		 * @param scheduledPeriod
		 *        De periode waarover de behandeling gepland wordt
		 * @param usesResources
		 *        De resources die nodig zijn om de behandeling te plannen
		 * @param campus FIXME
		 * 		  Campus behorende bij de treatment state
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 * @throws SchedulingException
		 *         Als er iets misloopt met het plannen van de behandeling
		 */
		public abstract void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException, SchedulingException;

		/**
		 * Methode om de behandeling opnieuw te plannen
		 * 
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 * @throws SchedulingException
		 *         Als er iets misloopt met het plannen van de behandeling
		 */
		public abstract void redo() throws IllegalOperationException,
				SchedulingException;

		/**
		 * Methode om aan te geven dat de behandeling begonnen is.
		 * 
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void start() throws IllegalOperationException;

		/**
		 * Methode om aan te geven dat de behandeling gedaan is.
		 * 
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void stop() throws IllegalOperationException;

		/**
		 * Methode om het plannen van de behandeling ongedaan te maken
		 * 
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void cancel() throws IllegalOperationException;

		/**
		 * Methode om de behandeling op te slaan
		 * 
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void store() throws IllegalOperationException;

		/**
		 * Methode om het resultaat van de behandeling in te stellen
		 * 
		 * @param result
		 *        Het resultaat van de behandeling
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 */
		public abstract void setResult(Result result)
				throws IllegalOperationException;

		/**
		 * Methode om aan te geven of de behandeling een resultaat nodig heeft
		 * 
		 * @return true
		 *         Als de behandeling gedaan is
		 *         false
		 *         Als de behandeling nog niet gedaan is
		 */
		public boolean needsResult() {
			return false;
		}
		
		/**
		 * Methode om aan te geven of de behandeling finished is, met resultaat.
		 * 
		 * @return true
		 * 		   Als de behandeling gedaan is met resultaat
		 * 		   false
		 * 		   Als de behandeling niet gedaan is of geen resultaat heeft
		 */
		public boolean isFinished() {
			return false;
		}
	}

	/**
	 * Inner klasse die de Created toestand voorstelt.
	 */
	private class TreatmentCreated extends TreatmentState {
		
		/**
		 * Methode om de behandeling te plannen
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus) {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de created state te starten.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de created state te stoppen.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de created state te annuleren.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om een treatment in de created state te herdoen.
		 */
		@Override
		public void redo() throws IllegalOperationException,
				SchedulingException {
			store();
		}

		/**
		 * Een methode om een nieuwe opgeslagen treatment te zetten als toestand en de huidige treatment aan de lijst
		 * van treatments voor de diagnose toe te voegen.
		 */
		@Override
		public void store() throws IllegalOperationException {
			Treatment.this.diagnosis.addTreatment(Treatment.this);
			setState(new TreatmentStored());
		}

		/**
		 * Een methode om het resultaat in te stellen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}

	/**
	 * Inner klasse die de Stored toestand voorstelt.
	 */
	private class TreatmentStored extends TreatmentState {

		/**
		 * Een methode om een treatment van de toestand stored te plannen.
		 * 
		 * @param scheduledPeriod
		 *        De periode waarover de behandeling gepland wordt
		 * @param usesResources
		 *        De resources die nodig zijn om de behandeling te plannen
		 * @param campus
		 * 		  Campus behorende bij de treatment state
		 * @throws IllegalOperationException
		 *         Als de behandeling zich in een toestand bevindt 
		 *         waarbij deze actie niet mag gebeuren
		 * @throws SchedulingException
		 *         Als er iets misloopt met het plannen van de behandeling
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException, SchedulingException {
			Treatment.this.scheduledPeriod = scheduledPeriod;

			for (ScheduleResource resource : usesResources)
				resource.getSchedule().addItem(scheduledPeriod, Treatment.this, campus);

			start = new TreatmentStart(scheduledPeriod.getBegin());
			stop = new TreatmentStop(scheduledPeriod.getEnd());
			Treatment.this.setState(new TreatmentPlanned(usesResources,
					scheduledPeriod,campus));
		}

		/**
		 * Een methode om de treatment van toestand stored te starten.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand stored te stoppen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();

		}

		/**
		 * Een methode om de treatment van toestand stored te annuleren.
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			Treatment.this.diagnosis.removeTreatment(Treatment.this);
			setState(new TreatmentCreated());
		}

		/**
		 * Een methode om de treatment van toestand stored te herdoen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void redo() throws IllegalOperationException,
				SchedulingException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand stored op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void store() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand stored zijn resultaat op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}
	
	/**
	 * Inner klasse die de Planned toestand voorstelt.
	 */
	private class TreatmentPlanned extends TreatmentState {
		private final List<ScheduleResource> usesResources;
		private final TimePeriod scheduledPeriod;
		private final Campus campus;

		/**
		 * Constructor van TreatmentPlanned
		 * 
		 * @param scheduledPeriod
		 *        De periode waarover de behandeling gepland wordt
		 * @param usesResources
		 *        De resources die nodig zijn om de behandeling te plannen
		 * @param campus
		 * 		  Campus behorende bij de treatment state
		 */
		private TreatmentPlanned(List<ScheduleResource> usesResources,
				TimePeriod scheduledPeriod,Campus campus) {
			this.scheduledPeriod = scheduledPeriod;
			this.usesResources = usesResources;
			this.campus = campus;
		}

		/**
		 * Een methode om de treatment van toestand planned te plannen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand planned te starten.
		 */
		@Override
		public void start() throws IllegalOperationException {
			Treatment.this.setState(new TreatmentInProgress());
		}

		/**
		 * Methode om de planned state te stoppen.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();

		}

		/**
		 * Methode om de planned state te annuleren.
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			for (ScheduleResource resource : this.usesResources)
				resource.getSchedule().removeItem(Treatment.this);
			Treatment.this.diagnosis.removeTreatment(Treatment.this);
			Treatment.this.scheduledPeriod = null;

			Treatment.this.setState(new TreatmentCancelled(this.usesResources,
					this.scheduledPeriod,this.campus));
		}

		/**
		 * Een methode om de treatment van toestand planned te herdoen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void redo() throws IllegalOperationException,
				SchedulingException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand planned op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void store() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand planned zijn resultaat op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}
	
	/**
	 * Inner klasse die de Cancelled toestand voorstelt.
	 */
	private class TreatmentCancelled extends TreatmentState {
		private final List<ScheduleResource> usesResources;
		private final TimePeriod scheduledPeriod;
		private final Campus campus;

		/**
		 * Constructor van TreatmentCancelled.
		 * 
		 * @param usesResources
		 * 		  De gebruikte resources
		 * @param scheduledPeriod
		 * 	      De tijdsperiode waarover het gepland moet worden
		 * @param campus
		 * 		  De bijbehorende campus
		 */
		private TreatmentCancelled(List<ScheduleResource> usesResources,
				TimePeriod scheduledPeriod,Campus campus) {
			this.scheduledPeriod = scheduledPeriod;
			this.usesResources = usesResources;
			this.campus = campus;
		}

		/**
		 * Een methode om de treatment van toestand cancelled te plannen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand cancelled te starten.
		 */
		@Override
		public void start() {
			Treatment.this.setState(new TreatmentInProgress());
		}

		/**
		 * Methode om de cancelled state te stoppen.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();

		}

		/**
		 * Methode om de cancelled state te annuleren.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand cancelled te herdoen.
		 */
		@Override
		public void redo() throws IllegalOperationException,
				SchedulingException {
			try {
				for (ScheduleResource resource : this.usesResources)
					resource.getSchedule().addItem(this.scheduledPeriod,
							Treatment.this, this.campus);
				Treatment.this.diagnosis.addTreatment(Treatment.this);
				Treatment.this.scheduledPeriod = this.scheduledPeriod;
				Treatment.this.setState(new TreatmentPlanned(
						this.usesResources, this.scheduledPeriod,this.campus));
			} catch (SchedulingException e) {
				for (ScheduleResource resource : this.usesResources)
					resource.getSchedule().removeItem(Treatment.this);

				Treatment.this.scheduledPeriod = null;

				Treatment.this.diagnosis.addTreatment(Treatment.this);
				Treatment.this.setState(new TreatmentStored());
				throw new ReschedulingException(Treatment.this.toString()
						+ " had to be rescheduled.");
			}
		}

		/**
		 * Een methode om de treatment van toestand cancelled op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void store() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand cancelled zijn resultaat op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}

	/**
	 * Inner klasse die de InProgress toestand voorstelt.
	 */
	private class TreatmentInProgress extends TreatmentState {
		
		/**
		 * Een methode om de treatment van toestand in progress te plannen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand in progress te starten.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de in progress state te stoppen.
		 */
		@Override
		public void stop() throws IllegalOperationException {
			Treatment.this.setState(new TreatmentNeedsResult());
		}

		/**
		 * Methode om de in progress state te annuleren.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand in progress te herdoen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void redo() throws IllegalOperationException,
				SchedulingException {
			throw new IllegalOperationException();

		}

		/**
		 * Een methode om de treatment van toestand in progress op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void store() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand in progress zijn resultaat op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
	}

	/**
	 * Inner klasse die de NeedsResult toestand voorstelt.
	 */
	private class TreatmentNeedsResult extends TreatmentState {
		
		/**
		 * Een methode om de treatment van toestand needs result te plannen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}
		
		/**
		 * Een methode om de treatment van toestand needs result te starten.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de needs result state te stoppen.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de needs result state te annuleren.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand needs result te herdoen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void redo() throws IllegalOperationException,
				SchedulingException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand needs result op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void store() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand needs result zijn resultaat op te slaan.
		 * @pre result != null
		 * @pre treatment.this.type.getResultType != result.getResultType()
		 * @post treatment.this.result = result
		 * @throw NullPointerException
		 * 		  Wanneer de result null is
		 * @throw ResultMismatchException
		 * 		  Wanneer het resultaattype van het type van de toestand verschilt van het resultaat zijn resultaattype
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException,
				NullPointerException, ResultMismatchException {
			if (result == null)
				throw new NullPointerException("Result is null.");
			if (Treatment.this.type.getResultType() != result.getResultType())
				throw new ResultMismatchException(
						"Event en result komen niet overeen.");

			Treatment.this.result = result;

			Treatment.this.setState(new TreatmentFinished());
		}

		/**
		 * Een methode om te kijken of de treatment een resultaat nodig heeft.
		 * 
		 * @return true
		 */
		@Override
		public boolean needsResult() {
			return true;
		}

	}

	/**
	 * Inner klasse die de Finished toestand voorstelt.
	 */
	private class TreatmentFinished extends TreatmentState {
		
		/**
		 * Een methode om de treatment van toestand finished te plannen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void schedule(TimePeriod scheduledPeriod,
				List<ScheduleResource> usesResources, Campus campus)
				throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand finished te starten.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void start() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de finished state te stoppen.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void stop() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Methode om de finished state te annuleren.
		 * 
		 * @throws IllegalOperationException
		 *         Het oproepen van deze functie mag niet voorvallen
		 */
		@Override
		public void cancel() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand finished te herdoen.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void redo() throws IllegalOperationException,
				SchedulingException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand finished op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void store() throws IllegalOperationException {
			throw new IllegalOperationException();
		}

		/**
		 * Een methode om de treatment van toestand finished zijn resultaat op te slaan.
		 * 
		 * @throws IllegalOperationException
		 * 		   Deze methode mag niet opgeroepen worden
		 */
		@Override
		public void setResult(Result result) throws IllegalOperationException {
			throw new IllegalOperationException();
		}
		
		/**
		 * Een methode om te controleren of de treatment afgelopen is. 
		 * 
		 * @return true
		 * 		   Deze voorwaarde is altijd waar
		 */
		@Override
		public boolean isFinished() {
			return true;
		}
	}
}
