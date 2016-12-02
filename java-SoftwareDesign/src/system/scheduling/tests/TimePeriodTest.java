package system.scheduling.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import system.time.TimePeriod;
import system.time.TimeStamp;


/**
 * Unit Test TimePeriod.java
 *
 */

public class TimePeriodTest {	
	@Test
	public void testTimePeriodConstructor() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 0, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		@SuppressWarnings("unused")
		TimePeriod timePeriod = new TimePeriod(timeStamp1, timeStamp2);
	}
	
	@Test
	public void testShiftBegin() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 0, 1, 1, 0);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 1, 0);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		TimeStamp timeStamp3 = new TimeStamp(2012, 0, 2, 1, 0);
		
		TimeStamp 	eTime1 = new TimeStamp(2012,0,2,1,0),
					eTime2 = new TimeStamp(2012,5,2,1,0);
		
		TimePeriod eTimePeriod = new TimePeriod(eTime1,eTime2);
		
		TimePeriod shiftedTimePeriod = TimePeriod.shiftBegin(timePeriod1, timeStamp3);
		
		assertEquals(shiftedTimePeriod, eTimePeriod);
	}
	
	@Test
	public void testIncapsulates() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		TimeStamp timeStamp3 = new TimeStamp(2012, 2, 1, 11, 14);
		TimeStamp timeStamp4 = new TimeStamp(2012, 4, 1, 11, 14);
		TimePeriod timePeriod2 = new TimePeriod(timeStamp3, timeStamp4);
		assertTrue(timePeriod1.encapsulates(timePeriod2));
	}
	
	@Test
	public void testInvalid1Incapsulates() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		TimeStamp timeStamp3 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp4 = new TimeStamp(2012, 6, 1, 11, 14);
		TimePeriod timePeriod2 = new TimePeriod(timeStamp3, timeStamp4);
		timePeriod1.encapsulates(timePeriod2);
		assertFalse(timePeriod1.encapsulates(timePeriod2));
	}
	
	@Test
	public void testInvalid2Incapsulates() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		TimeStamp timeStamp3 = new TimeStamp(2012, 0, 1, 11, 14);
		TimeStamp timeStamp4 = new TimeStamp(2012, 4, 1, 11, 14);
		TimePeriod timePeriod2 = new TimePeriod(timeStamp3, timeStamp4);
		timePeriod1.encapsulates(timePeriod2);
		assertFalse(timePeriod1.encapsulates(timePeriod2));
	}
	
	@Test
	public void testEquals() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		assertFalse(timePeriod1.equals(new Object()));
	}
	
	@Test
	public void testEquals2() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		TimeStamp timeStamp3 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp4 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod2 = new TimePeriod(timeStamp3, timeStamp4);

		assertEquals(timePeriod1, timePeriod2);
	}
	
	@Test
	public void testEquals3() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 6, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		TimeStamp timeStamp3 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp4 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod2 = new TimePeriod(timeStamp3, timeStamp4);

		assertFalse(timePeriod1.equals(timePeriod2));
	}
	
	@Test
	public void testEquals4() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 2, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		TimeStamp timeStamp3 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp4 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod2 = new TimePeriod(timeStamp3, timeStamp4);

		assertFalse(timePeriod1.equals(timePeriod2));
	}
	
	@Test
	public void testToString() {
		TimeStamp timeStamp1 = new TimeStamp(2012, 1, 1, 11, 14);
		TimeStamp timeStamp2 = new TimeStamp(2012, 5, 1, 11, 14);
		TimePeriod timePeriod1 = new TimePeriod(timeStamp1, timeStamp2);
		assertNotSame(timePeriod1.toString().length(), 0);
	}
}
