package system.medicaltests;

import system.patients.Patient;
import system.repositories.MachineType;
import system.scheduling.EventType;
import system.scheduling.Priority;
import system.time.TimeDuration;
import annotations.SystemAPI;


/**
 * Deze klasse stelt een bloedanalyse voor.
 * 
 * @invar focus != null && focus != ""
 * 
 * @author SWOP Team 10
 */
public class BloodAnalysis extends MedicalTest {
	/**
	 * Een constante die de duur van de bloedanalyse bijhoudt
	 */
	private static final TimeDuration DURATION = TimeDuration.minutes(45);
	/**
	 * De focus van de bloedanalyse
	 */
	private String focus;
	/**
	 * Het aantal bloedanalyses dat moet worden uitgevoerd
	 */
	private int numberOfAnalyses;
	
	/** 
	 * Constructor voor BloodAnalysis
	 * 
	 * @param patient
	 *        De patient waarop de bloedanalyse moet worden uitgevoerd
	 * @param focus	
	 *        De focus van de bloedanalyse
	 * @param numberOfAnalyses	
	 *        Het aantal bloedanalyses
	 * @pre focus != null && focus != ""
	 * 		De focus moet verschillend zijn van null en de lege string
	 * @post getFocus() == focus
	 * @throws NullPointerException
	 *         Als de focus van de bloedanalyse niet opgegeven is
	 */
	public BloodAnalysis(Patient patient, String focus, int numberOfAnalyses) throws NullPointerException {
		super(patient, DURATION, EventType.BLOOD_ANALYSIS, Priority.NORMAL);
		neededResources.add(MachineType.BLOOD_ANALYZER);
		if (focus == "" || focus == null)
			throw new NullPointerException("Focus is null.");
		this.focus = focus;
		setNumberOfAnalyses(numberOfAnalyses);
	}
	
	/** 
	 * Constructor voor BloodAnalysis met prioriteit
	 * 
	 * @param patient
	 *        De patient waarop de bloedanalyse moet worden uitgevoerd
	 * @param priority
	 * 		  De prioriteit van de blood analysis
	 * @param focus	
	 *        De focus van de bloedanalyse
	 * @param numberOfAnalyses	
	 *        Het aantal bloedanalyses
	 * @pre focus != null && focus != ""
	 * 		De focus moet verschillend zijn van null en de lege string
	 * @post getFocus() == focus
	 * @pre priotiteit != null
	 * 		De priotiteit mag niet null zijn
	 * @post GetPriority() == prioriteit
	 * @throws NullPointerException
	 *         Als de focus of prioriteit van de bloedanalyse niet opgegeven is
	 */
	public BloodAnalysis(Patient patient, Priority priority, String focus, int numberOfAnalyses) throws NullPointerException {
		super(patient, DURATION, EventType.BLOOD_ANALYSIS, priority);
		neededResources.add(MachineType.BLOOD_ANALYZER);
		if (focus == "" || focus == null)
			throw new NullPointerException("Focus is null.");
		if (priority == null)
			throw new NullPointerException("Priority is null.");
		this.focus = focus;
		setNumberOfAnalyses(numberOfAnalyses);
	}

	/**
	 * Setter voor het aantal bloedanalyses
	 * 
	 * @param numberOfAnalyses 
	 *        Het nieuwe aantal bloedanalyses
	 * @pre	numberOfAnalyses > 0
	 * 		Moet minstens 1 analyse gebeurd zijn
	 * @post getNumberOfAnalyses() == numberOfAnalyses
	 * @throws IllegalArgumentException
	 * 	       Het aantal analyses moet groter zijn dan 0
	 */
	private void setNumberOfAnalyses(int numberOfAnalyses) throws IllegalArgumentException{
		if(numberOfAnalyses > 0) this.numberOfAnalyses = numberOfAnalyses;
		else throw new IllegalArgumentException();
	}

	/**
	 * Een toString voor BloodAnalysis.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Priority: "+this.getPriority().toString()+
			" - Focus: " + focus + " ~ Number of Analyses: " + numberOfAnalyses;
	}

	/**
	 * Getter voor de focus van de bloedanalyse
	 * 
	 * @return focus
	 *         De focus van de bloedanalyse
	 */
	public String getFocus() {
		return focus;
	}
}
