package system.scheduling;

import system.campus.Hospital;

/**
 * Deze klasse is de scheduler voor events met dringende prioriteit.
 */

public class UrgentScheduler extends MainScheduler {

	/**
	 * De constructor van UrgentScheduler.
	 * 
	 * @param hospital
	 * 		   Het ziekenhuis waar de scheduler van toepassing is
	 */
	UrgentScheduler(Hospital hospital) {
		super(hospital, Priority.URGENT);
		setSuccessor(new NoPriorityScheduler(hospital));
	}
}
