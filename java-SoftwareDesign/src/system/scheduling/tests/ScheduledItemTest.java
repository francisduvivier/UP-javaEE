package system.scheduling.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import system.medicaltests.BloodAnalysis;
import system.patients.Patient;
import system.scheduling.Schedule;
import system.scheduling.ScheduledItem;
import system.time.TimePeriod;
import system.time.TimeStamp;

public class ScheduledItemTest {
	@Test
	public void testAddSuffix() {
		TimeStamp 	begin = new TimeStamp(2012,10,4,10,10),
					end = new TimeStamp(2012,10,4,10,30),
					begin2 = new TimeStamp(2012,10,4,10,30),
					end2 = new TimeStamp(2012,10,4,10,40);
		TimePeriod	period = new TimePeriod(begin,end),
					period2 = new TimePeriod(begin2,end2);
		BloodAnalysis 	test = new BloodAnalysis(new Patient("test"),"test",5);
		Schedule schedule = new Schedule();
		schedule.addItem(period, test, null);
		ScheduledItem<?> item = schedule.getSchedule().first();
		assertEquals(item,item);
		schedule.addItem(period2, test, null);
		ScheduledItem<?> item2 = schedule.getSchedule().first();
		assertEquals(item,item2);
	}
}
