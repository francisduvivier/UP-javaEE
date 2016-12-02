package system.scheduling;

import java.util.SortedSet;

import system.campus.CampusId;
import system.staff.ShiftTable;
import system.time.TimePeriod;
import system.time.TimeStamp;

public class ShiftSchedule extends Schedule {
	
	/**
	 * Een variabele die in een HashMap de shifts bijhoudt
	 */
	private final ShiftTable shiftTable;
	
	/**
	 * Een constructor van ShiftSchedule.
	 * 
	 * @param shiftTable
	 * 		  De shifts
	 */
	public ShiftSchedule(ShiftTable shiftTable) {
		super();
		this.shiftTable = shiftTable;
	}
	
	/**
	 * Een constructor van ShiftSchedule
	 * 
	 * @param shiftTable
	 * 		  De shifts
	 * @param schedule
	 * 		  Gesorteerde set van geplande schedule items
	 */
	public ShiftSchedule(ShiftTable shiftTable, SortedSet<ScheduledItem<?>> schedule) {
		super(schedule);
		this.shiftTable = shiftTable;
	}

	/**
	 * Doet hetzelfde als firstAvailable bij Schedule maar deze methode houdt
	 * rekening met de werkuren van de ScheduleResource objecten.
	 */
	@Override
	public TimePeriod firstAvailable(TimePeriod timePeriod, CampusId campus, 
			TimeStamp breakOf) {
		TimePeriod returnTimePeriod = timePeriod;
		if (!this.getShiftTable().isWorking(returnTimePeriod,campus))
			returnTimePeriod = getShiftTable().firstAvailable(returnTimePeriod, campus);
		if (returnTimePeriod == null || returnTimePeriod.getEnd().compareTo(breakOf) >= 0) 
			return null; 

		
		for (ScheduledItem<?> item : this.getSchedule()) {
			if (returnTimePeriod.getBegin().compareTo(item.getTimePeriod().getEnd()) >= 0)
				continue;
			if (returnTimePeriod.getEnd().compareTo(breakOf) >= 0) {
				returnTimePeriod = null;
				break;
			}
			
			if (!this.getShiftTable().isWorking(returnTimePeriod,campus))
				returnTimePeriod = getShiftTable().firstAvailable(returnTimePeriod, campus);
			
			if(returnTimePeriod.interferes(item.getTimePeriod()))
				returnTimePeriod = TimePeriod.shiftBegin(returnTimePeriod, item.getTimePeriod().getEnd());
		}
		
		if (returnTimePeriod == null) return null;
		if (!this.getShiftTable().isWorking(returnTimePeriod,campus))
			returnTimePeriod = getShiftTable().firstAvailable(returnTimePeriod, campus);
		if (returnTimePeriod.getEnd().compareTo(breakOf) >= 0) 
			return null;
		
		return returnTimePeriod;
	}
	
	/**
	 * @return true
	 */
	@Override
	public final boolean isShiftSchedule() {
		return true;
	}
	
	/**
	 * @return De werkuren van deze schedule
	 */
	@Override
	public ShiftTable getShiftTable() {
		return this.shiftTable;
	}
}
