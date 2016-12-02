package system.scheduling;

import java.util.List;

import system.campus.Hospital;
import system.time.TimeStamp;

/**
 * Deze klasse is de scheduler voor events zonder prioriteit.
 */

public class NoPriorityScheduler extends MainScheduler {

	/**
	 * De constructor voor NoPriorityScheduler.
	 * 
	 * @param hospital
	 * 		  Het ziekenhuis waar de scheduler van toepassing is
	 */
	NoPriorityScheduler(Hospital hospital) {
		super(hospital, Priority.NO_PRIORITY);
	}

	/**
	 * Een methode om het gefilterd schedule terug te krijgen.
	 * 
	 * @return this.getHandlingSchedule()
	 */
	@Override
	protected Schedule filtered() {
		return this.getHandlingSchedule();
	}

	@Override
	protected void handleReschedules(List<ScheduleResource> combination, TimeStamp begin) {
		//DO NOTHING
	}

}
