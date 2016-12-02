package system.medicaltests;

import system.patients.Patient;
import system.repositories.MachineType;
import system.scheduling.EventType;
import system.scheduling.Priority;
import system.time.TimeDuration;
import annotations.SystemAPI;


/**
 * Deze klasse stelt een X-ray scan voor.
 * 
 * @invar bodyPart != null && bodyPart != ""
 * @invar numberOfImagesNeeded > 0
 * @invar zoomlevel > 0
 * 
 * @author SWOP Team 10
 */
public class XRayScan extends MedicalTest{
	/**
	 * Een constante die de duur van de X-ray scan bijhoudt
	 */
	private static final TimeDuration DURATION=TimeDuration.minutes(15);
	/**
	 * Het lichaamsdeel waarvan een X-ray foto genomen moet worden
	 */
	private String bodyPart;
	/**
	 * Het aantal X-ray foto's dat genomen moet worden
	 */
	private int numberOfImagesNeeded;
	/**
	 * De zoomlevel waarbij de X-ray scan genomen moet worden
	 */
	private int zoomlevel;

	/**
	 * Constructor voor XRayScan
	 * 
	 * @param patient
	 *        De patient waarop de X-ray scan moet worden uitgevoerd
	 * @param bodyPart	
	 *        Het lichaamsdeel waarvan een X-ray foto genomen moet worden
	 * @param numberOfImagesNeeded	
	 *        Het aantal X-ray foto's dat genomen moet worden
	 * @param zoomlevel	
	 *        De zoomlevel waarbij de X-ray scan genomen moet worden
	 *        
	 * @pre	bodyPart != null && bodyPart != ""
	 * 		Lichaamsdeel moet verschillend zijn van nul of de lege string
	 * @post getBodyPart() == bodyPart
	 * @pre numberOfImagesNeeded > 0
	 * 		Aantal afbeeldingen nodig moet groter zijn dan nul
	 * @post getNumberOfImagesNeeded() == numberOfImagesNeeded
	 * @pre zoomlevel > 0
	 * 		Zoomlevel moet groter zijn dan nul
	 * @post getZoomlevel() == zoomlevel
	 *        
	 * @throws NullPointerException
	 *         Als het lichaamsdeel waarvan een X-ray foto genomen moet worden, niet opgegegeven is
	 * @throws IllegalArgumentException
	 *         Als het aantal te nemen X-ray foto's niet groter is dan 0
	 *         Als het zoomlevel waarbij de X-ray scan genomen moet worden, niet groter is dan 0
	 */
	public XRayScan(Patient patient, String bodyPart, int numberOfImagesNeeded, int zoomlevel) throws NullPointerException, IllegalArgumentException {
		super(patient, DURATION,EventType.XRAY_SCAN, Priority.NORMAL);
		neededResources.add(MachineType.XRAY_SCANNER);
		if (bodyPart == "" || bodyPart == null)
			throw new NullPointerException("Body part is null.");
		this.bodyPart = bodyPart;
		if (numberOfImagesNeeded <= 0)
			throw new IllegalArgumentException("Aantal nodige afbeeldingen is null.");
		this.numberOfImagesNeeded = numberOfImagesNeeded;
		if (zoomlevel <= 0)
			throw new IllegalArgumentException("Zoomlevel is null.");
		this.zoomlevel = zoomlevel;
	}
	
	/**
	 * Constructor voor XRayScan met prioriteit
	 * 
	 * @param patient
	 *        De patient waarop de X-ray scan moet worden uitgevoerd
	 * @param priority
	 * 		  De prioriteit van de X-Ray scan
	 * @param bodyPart	
	 *        Het lichaamsdeel waarvan een X-ray foto genomen moet worden
	 * @param numberOfImagesNeeded	
	 *        Het aantal X-ray foto's dat genomen moet worden
	 * @param zoomlevel	
	 *        De zoomlevel waarbij de X-ray scan genomen moet worden
	 * @throws NullPointerException
	 *         Als het lichaamsdeel waarvan een X-ray foto genomen moet worden, niet opgegegeven is
	 * @throws IllegalArgumentException
	 *         Als het aantal te nemen X-ray foto's niet groter is dan 0
	 *         Als het zoomlevel waarbij de X-ray scan genomen moet worden, niet groter is dan 0
	 */
	public XRayScan(Patient patient,  Priority priority, String bodyPart,
			int numberOfImagesNeeded, int zoomlevel) throws NullPointerException, IllegalArgumentException {
		super(patient, DURATION,EventType.XRAY_SCAN, priority);
		neededResources.add(MachineType.XRAY_SCANNER);
		if (bodyPart == "" || bodyPart == null)
			throw new NullPointerException("Body part is null.");
		this.bodyPart = bodyPart;
		if (numberOfImagesNeeded <= 0)
			throw new IllegalArgumentException("Aantal nodige afbeeldingen is null.");
		this.numberOfImagesNeeded = numberOfImagesNeeded;
		if (zoomlevel <= 0)
			throw new IllegalArgumentException("Zoomlevel is null.");
		if (priority == null)
			throw new NullPointerException("Priority is null.");
		this.zoomlevel = zoomlevel;
	}
	
	@Override
	@SystemAPI
	public String toString() {
		return "Priority: "+ this.getPriority().toString()+
		" - Body Part:" + bodyPart + " ~ Number of Images needed: " + numberOfImagesNeeded + " ~ Zoom Level: " + zoomlevel;
	}
}
