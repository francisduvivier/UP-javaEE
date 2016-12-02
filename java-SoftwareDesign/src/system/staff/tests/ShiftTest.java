package system.staff.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.staff.Shift;
import system.time.TimePeriod;
import system.time.TimeStamp;

/**
 * Unit Test Shift.java
 *
 */

public class ShiftTest {
	private Shift shift1, shift2, shift3, shift4;
	private TimePeriod period1, period2, period3, period4;
	@Before
	public void testInit() {
		TimeStamp 	
				time1 = new TimeStamp(2012,10,10,13,0),
				time2 = new TimeStamp(2012,10,10,13,40),
				
				time3 = new TimeStamp(2012,10,11,13,0),
				time4 = new TimeStamp(2012,10,11,18,0),
				
				time5 = new TimeStamp(2012,10,10,10,0),
				time6 = new TimeStamp(2012,10,10,13,0),
				
				time7 = new TimeStamp(2012,10,12,14,0),
				time8 = new TimeStamp(2012,10,12,19,0);
	
		period1 = new TimePeriod(time1, time2);
		period2 = new TimePeriod(time3, time4);
		period3 = new TimePeriod(time5, time6);
		period4 = new TimePeriod(time7, time8);
		
		shift1 = new Shift(period1);
		shift2 = new Shift(period2);
		shift3 = new Shift(period3);
		shift4 = new Shift(period4);	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor() {
		new Shift(new TimePeriod( 
				new TimeStamp(1,1,1,1,1),
				new TimeStamp(1,1,2,2,2)));
		new Shift(new TimePeriod(
				new TimeStamp(1,1,1,23,59),
				new TimeStamp(1,1,2,0,1)));
	}

	@Test
	public void testIsWorkingTrue() {
		assertTrue(shift1.isWorking(period1));
		assertFalse(shift1.isWorking(period2));
	}
	
	@Test
	public void testIsWorkingFalse() {
		assertFalse(shift1.isWorking(period2));
		assertFalse(shift4.isWorking(period3));
	}
	
	@Test
	public void testEquals() {
		assertEquals(shift1,new Shift(period1));
		assertFalse(shift1.equals(new Object()));
	}
	
	@Test
	public void testIntersect() {
		TimeStamp
			eTime = new TimeStamp(1,1,1,13,0),
			eTime2 = new TimeStamp(1,1,1,13,40);
		
		TimePeriod ePeriod = new TimePeriod(eTime,eTime2);
		
		assertEquals(shift1.intersect(shift2),new Shift(ePeriod));
	}
	
	@Test
	public void testIntersect2() {
		assertEquals(shift1.intersect(shift3),null);
	}
	
	@Test
	public void testIntersectFalse() {
		TimeStamp
			eTime = new TimeStamp(1,1,1,13,0),
			eTime2 = new TimeStamp(1,1,1,13,41);
		
		TimePeriod ePeriod = new TimePeriod(eTime,eTime2);
		
		assertFalse(shift1.intersect(shift2).equals(new Shift(ePeriod)));
	}
	
	@Test
	public void testNextAvailable() {
		assertEquals(shift1.nextAvailable(period1),period1);
		assertEquals(shift2.nextAvailable(period1),period1);
		assertEquals(shift1.nextAvailable(period2),null);
		
		TimeStamp
			eTime = new TimeStamp(2012,10,11,10,0),
			eTime2 = new TimeStamp(2012,10,11,10,40);
	
		TimePeriod ePeriod = new TimePeriod(eTime,eTime2);
		assertEquals(shift3.nextAvailable(period1),ePeriod);
		
		eTime = new TimeStamp(2012,10,10,14,0);
		eTime2 = new TimeStamp(2012,10,10,14,40);
		ePeriod = new TimePeriod(eTime,eTime2);
		assertEquals(shift4.nextAvailable(period1),ePeriod);
	}
	
	@Test
	public void testToString() {
		Shift shift = new Shift(new TimePeriod(new TimeStamp(2010,5,5,5,5), new TimeStamp(2010,5,5,23,59)));
		assertEquals(shift.toString(), "Shift - " + (new TimeStamp(2010,5,5,5,5)).formatDate("HH:mm") + " to "+
							(new TimeStamp(2010,5,5,23,59)).formatDate("HH:mm"));
	}
	
}
