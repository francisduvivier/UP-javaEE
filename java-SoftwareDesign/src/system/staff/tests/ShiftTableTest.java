package system.staff.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.campus.CampusId;
import system.campus.Hospital;
import system.staff.Shift;
import system.staff.ShiftTable;
import system.time.TimePeriod;
import system.time.TimeStamp;

public class ShiftTableTest {

	private ShiftTable table1, table2;
	private CampusId campus1, campus2;
	private Shift shift1, shift2, shift3, shift4;
	private TimePeriod period1, period2, period3, period4;
	
	@Before
	public void testInit() {
		Hospital hospital = new Hospital();
		
		TimeStamp 	time1 = new TimeStamp(2012,10,10,9,0),
					time2 = new TimeStamp(2012,10,10,12,0),
					
					time3 = new TimeStamp(2012,10,11,13,0),
					time4 = new TimeStamp(2012,10,11,18,0),
					
					time5 = new TimeStamp(2012,10,10,10,0),
					time6 = new TimeStamp(2012,10,10,13,0),
					
					time7 = new TimeStamp(2012,10,11,14,0),
					time8 = new TimeStamp(2012,10,11,19,0);
		
		period1 = new TimePeriod(time1, time2);
		period2 = new TimePeriod(time3, time4);
		period3 = new TimePeriod(time5, time6);
		period4 = new TimePeriod(time7, time8);
		
		shift1 = new Shift(period1);
		shift2 = new Shift(period2);
		shift3 = new Shift(period3);
		shift4 = new Shift(period4);
		
		campus1 = new CampusId(hospital.getCampuses().get(0));
		campus2 = new CampusId(hospital.getCampuses().get(1));
		
		table1 = new ShiftTable();
		table2 = new ShiftTable();
	}
	
	@Test
	public void testEquals() {
		table1.add(campus2, shift1);
		table1.add(campus1, shift2);
		table2.add(campus2, shift3);
		assertTrue(table1.equals(table1));
		assertTrue(table1.equals(table1.copyShiftTable()));
		assertFalse(table1.equals(table2));
		assertFalse(table1.equals(new Object()));
	}
	
	@Test
	public void testAdd() {
		table1.add(campus2, shift1);
		table1.add(campus1, shift2);
		table2.add(campus2, shift3);
		table2.add(null, shift4);
	}
	
	@Test
	public void testGetShiftList() {
		assertEquals(table1.getShiftList(campus1),null);
		table1.add(campus2, shift1);
		table1.add(campus1, shift2);
		assertEquals(table1.getShiftList(campus1).size(),1);
		assertEquals(table1.getShiftList(campus2).size(),1);
		assertEquals(table1.getShiftList(campus1).get(0),shift2);
		assertEquals(table1.getShiftList(campus2).get(0),shift1);
		table1.add(campus1, shift3);
		assertEquals(table1.getShiftList(campus1).size(),2);
		assertEquals(table1.getShiftList(campus1).get(1),shift3);
	}
	
	@Test
	public void testIsEmpty() {
		assertTrue(table1.isEmpty());
		table1.add(campus1, shift1);
		assertFalse(table1.isEmpty());
	}
	
	@Test
	public void testIntersect() {
		table1.add(campus2, shift1);
		
		assertEquals(table1.intersect(null),table1);
		
		table2.add(campus2, shift3);
		table1.add(campus1, shift2);
		
		TimeStamp 	eTime1 = new TimeStamp(2012,10,10,10,0),
					eTime2 = new TimeStamp(2012,10,10,12,0);
		Shift		eShift1 = new Shift(new TimePeriod(eTime1,eTime2));
		ShiftTable	eTable = new ShiftTable();
		
		eTable.add(campus2, eShift1);
		
		assertEquals(table1.intersect(table2),eTable);
	}
	
	@Test
	public void testIsWorking() {
		assertFalse(table1.isWorking(period1,campus1));
		table1.add(campus2, shift1);
		assertFalse(table1.isWorking(period1,campus1));
		assertFalse(table1.isWorking(period2,campus2));
		assertTrue(table1.isWorking(period1,campus2));
	}
	
	@Test
	public void testFirstAvailable() {
		TimeStamp	eTime1 = new TimeStamp(2012,10,10,13,0),
					eTime2 = new TimeStamp(2012,10,10,16,0);
		TimePeriod	ePeriod = new TimePeriod(eTime1,eTime2);
		table1.add(campus2, shift2);
		assertEquals(table1.firstAvailable(period1, campus1),null);
		assertEquals(table1.firstAvailable(period1, campus2),ePeriod);
		table1.add(campus2, shift1);
		assertEquals(table1.firstAvailable(period1, campus2),period1);
		table1.add(campus1,shift1);
		table1.add(campus1, shift2);
		assertEquals(table1.firstAvailable(period1, campus1),period1);
	}
	
	@Test
	public void testToString() {
		assertEquals("No shifts",table1.toString());
		table1.add(campus1, shift1);
		assertEquals(campus1.toString()+"\n "+shift1.toString(),table1.toString());
		
	}
}
