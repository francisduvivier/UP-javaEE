package system.scheduling.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;

import system.time.TimeStamp;


public class TimeStampTest {

	private TimeStamp timeStamp1, timeStamp2;
	
	@Test
	public void testTimeStampConstructor() {
		timeStamp1 = new TimeStamp(2011, 2, 5, 7, 4);
		assertEquals(timeStamp1.get(Calendar.YEAR), 2011);
		assertEquals(timeStamp1.get(Calendar.MONTH), 2);
		assertEquals(timeStamp1.get(Calendar.DAY_OF_MONTH), 5);
		assertEquals(timeStamp1.get(Calendar.HOUR), 7);
		assertEquals(timeStamp1.get(Calendar.MINUTE), 4);
	}
	
	@Test
	public void testCopyTimeStamp() {
		timeStamp1 = new TimeStamp(2011, 2, 5, 7, 4);
		timeStamp2 = timeStamp1;
		assertEquals(timeStamp2.get(Calendar.YEAR), 2011);
		assertEquals(timeStamp2.get(Calendar.MONTH), 2);
		assertEquals(timeStamp2.get(Calendar.DAY_OF_MONTH), 5);
		assertEquals(timeStamp2.get(Calendar.HOUR), 7);
		assertEquals(timeStamp2.get(Calendar.MINUTE), 4);
	}
	
	@Test
	public void testParseStringToTimeStamp() throws ParseException {
		String testParseString = "01/12/2010 10:00";
		TimeStamp time = new TimeStamp(2010,11,1,10,00);
		
		assertEquals(time, TimeStamp.parseStringToTimeStamp(testParseString));
	}
	
	@Test
	public void testParseDateToTimeStamp() throws ParseException {
		String testParseString = "01/12/2010";
		TimeStamp time = new TimeStamp(2010,11,1,0,0);
		
		assertEquals(time, TimeStamp.parseDateToTimeStamp(testParseString));
	}
	
	@Test
	public void testAfter() throws ParseException {
		TimeStamp 	time = new TimeStamp(2010,11,1,0,0),
					time2 = new TimeStamp(2010,11,1,0,1);
		
		assertTrue(time2.after(time));
	}
	
	@Test
	public void testEquals() {
		TimeStamp 	time = new TimeStamp(2010,11,1,0,0);
		Object o = time;
		
		assertTrue(time.equals(o));
		assertFalse(time.equals(new Object()));
	}
	
	@Test 
	public void testGetDay() {
		TimeStamp	time1 = new TimeStamp(2012,11,3,23,59),
					time2 = new TimeStamp(2012,11,4,0,0);
		
		long	day1 = time1.getDay(),
				day2 = time2.getDay();
		
		assertEquals(day2-day1, 1);
	}
	
	@Test
	public void testCalcForwardToReturns(){
		TimeStamp	time1 = new TimeStamp(2012,11,3,23,59),
					goodFirstMidnigt1 = new TimeStamp(2012,11,4,23,59),
					goodFirstMealArrival= new TimeStamp(2012,11,5,6,0),
					goodFirstBreakFast= new TimeStamp(2012,11,4,9,0);
		assertEquals(goodFirstMidnigt1, time1.calcForwardTo(23, 59));
		assertEquals(goodFirstMealArrival, time1.calcForwardTo(6,0).calcForwardTo(6, 0));
		assertEquals(goodFirstBreakFast, time1.calcForwardTo(9, 0));
		TimeStamp	time2 = new TimeStamp(2012,11,3,10,58),
				goodFirstMidnigt2 = new TimeStamp(2012,11,3,23,59);
		assertEquals(goodFirstMidnigt2, time2.calcForwardTo(23, 59));
		assertEquals(goodFirstMealArrival, time2.calcForwardTo(6,0).calcForwardTo(6, 0));
		assertEquals(goodFirstBreakFast, time2.calcForwardTo(9, 0));
		}
	
	@Test
	public void testCalcForwardToExceptions(){
		TimeStamp	time1 = new TimeStamp(2012,11,3,23,59);
		for(int hour:new Integer[]{-1,24,3,15,0,23})
		for(int minute:new Integer[]{-1,60,5,48,0,60})
		try {
			time1.calcForwardTo(hour, minute);
			if(hour>23||hour<0||minute>59||minute<0)
				fail();
		} catch (IllegalArgumentException e) {
			if(hour<=23&&hour>=0&&minute<=59&&minute>=0)
				fail();
		}

	}
}
