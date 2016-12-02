package system.scheduling;

import system.campus.Hospital;

/**
 * Deze klasse is de scheduler voor events met normale prioriteit.
 */

public class NormalScheduler extends MainScheduler {
	
	/**
	 * De constructor voor NormalScheduler.
	 * 
	 * @param hospital
	 * 		   Het ziekenhuis waar de scheduler van toepassing is
	 */
	public NormalScheduler(Hospital hospital) {
		super(hospital, Priority.NORMAL);
		setSuccessor(new UrgentScheduler(hospital));
	}
}
