package system.scheduling.tests;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import system.time.Time;
import system.time.TimeStamp;


/**
 * Unit Test Time.java
 *
 */

public class TimeTest {
	
	private Time time;
	
	@Test
	public void testTimeConstructor() {
		time = new Time(2011,10,8,8,0);
		assertEquals(time.getTime().get(Calendar.YEAR), 2011);
		assertEquals(time.getTime().get(Calendar.MONTH), 10);
		assertEquals(time.getTime().get(Calendar.DAY_OF_MONTH), 8);
		assertEquals(time.getTime().get(Calendar.HOUR), 8);
		assertEquals(time.getTime().get(Calendar.MINUTE), 0);
	}
	
	@Test
	public void testSetTime() {
		time = new Time(2011,10,8,8,0);
		TimeStamp timeStamp = new TimeStamp(2012, 0, 1, 11, 14);
		time.setTime(timeStamp);
		assertEquals(time.getTime().get(Calendar.YEAR), 2012);
		assertEquals(time.getTime().get(Calendar.MONTH), 0);
		assertEquals(time.getTime().get(Calendar.DAY_OF_MONTH), 1);
		assertEquals(time.getTime().get(Calendar.HOUR), 11);
		assertEquals(time.getTime().get(Calendar.MINUTE), 14);
	}
}
