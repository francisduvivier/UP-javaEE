package system.medicaltests;


import system.patients.Patient;
import system.repositories.MachineType;
import system.scheduling.EventType;
import system.scheduling.Priority;
import system.time.TimeDuration;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een ultrasound scan voor.
 * 
 *  @invar 
 * 
 * @author SWOP Team 10
 */
public class UltrasoundScan extends MedicalTest{
	/**
	 * Een constante die de duur van de ultrasound scan bijhoudt
	 */
	private static final TimeDuration DURATION=TimeDuration.minutes(30);
	/**
	 * De focus van de ultrasound scan
	 */
	private String focus;
	/**
	 * Variabele die aangeeft of de ultrasound scan moet opgenomen worden in de vorm van video
	 */
	private boolean recordVideo;
	/**
	 * Variabele die aangeeft of de ultrasound scan moet opgenomen worden in de vorm van foto's
	 */
	private boolean recordImages;
	
	/**
	 * Constructor voor UltrasoundScan
	 * @param patient
	 *        De patient waarop de ultrasound scan moet worden uitgevoerd
	 * @param focus	
	 *        De focus van de ultrasound scan
	 * @param recordVideo
	 *        true indien de ultrasound scan als video opgenomen moet worden
	 *        false indien de ultrasound scan niet als video opgenomen moet worden
	 * @param recordImages
	 *        true indien de ultrasound scan in de vorm van foto's opgenomen moet worden
	 *        false indien de ultrasound scan in de vorm van foto's opgenomen moet worden
	 * @pre focus != null && focus != ""
	 * 		De focus moet verschillend zijn van null en de lege string
	 * @post getFocus() == focus
	 * @throws NullPointerException
	 *         Als de focus van de ultrasound scan niet opgegeven is
	 */
	public UltrasoundScan(Patient patient, String focus, boolean recordVideo, boolean recordImages) {
		super(patient, DURATION, EventType.ULTRASOUND_SCAN, Priority.NORMAL);
		neededResources.add(MachineType.ULTRASOUND_MACHINE);
		if (focus ==  "" || focus == null)
			throw new NullPointerException("Focus is null.");
		this.focus = focus;
		this.recordVideo = recordVideo;
		this.recordImages = recordImages;
	}
	
	/**
	 * Initialisatie van een UltrasoundScan object met prioriteit
	 * 
	 * @param patient
	 *        De patient waarop de ultrasound scan moet worden uitgevoerd
	 * @param priority
	 *		  De prioriteit van de ultrasound scan
	 * @param focus	
	 *        De focus van de ultrasound scan
	 * @param recordVideo
	 *        true indien de ultrasound scan als video opgenomen moet worden
	 *        false indien de ultrasound scan niet als video opgenomen moet worden
	 * @param recordImages
	 *        true indien de ultrasound scan in de vorm van foto's opgenomen moet worden
	 *        false indien de ultrasound scan in de vorm van foto's opgenomen moet worden
	 *        
	 * @pre focus != null && focus != ""
	 * 		De focus moet verschillend zijn van null en de lege string
	 * @post getFocus() == focus
	 * @pre priotiteit != null
	 * 		De priotiteit mag niet null zijn
	 * @post GetPriority() == prioriteit
	 */
	public UltrasoundScan(Patient patient, Priority priority, String focus, boolean recordVideo, boolean recordImages) {
		super(patient, DURATION, EventType.ULTRASOUND_SCAN, priority);
		neededResources.add(MachineType.ULTRASOUND_MACHINE);
		if (focus ==  "" || focus == null)
			throw new NullPointerException("Focus is null.");
		if (priority == null)
			throw new NullPointerException("Priority is null.");
		this.focus = focus;
		this.recordVideo = recordVideo;
		this.recordImages = recordImages;
	}
	
	/**
	 * Een toString voor UltrasoundScan.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Priority: "+this.getPriority().toString()+
			   " - Focus: " + focus + " - Record Video: " + recordVideo + " - Record Images: " + recordImages;
	}
}