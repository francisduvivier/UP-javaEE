package system.scheduling.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.CampusId;
import system.campus.Hospital;
import system.exceptions.SchedulingException;
import system.medicaltests.BloodAnalysis;
import system.medicaltests.MedicalTest;
import system.medicaltests.UltrasoundScan;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.scheduling.Schedule;
import system.scheduling.ScheduledItem;
import system.scheduling.ShiftSchedule;
import system.scheduling.Undoable;
import system.staff.Doctor;
import system.staff.ShiftTable;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.treatments.Medication;
import system.treatments.Treatment;
import system.warehouse.MedicationItemType;


public class ScheduleTest {
	private Schedule schedule, schedule2;
	private ShiftSchedule schedule3;
	private MedicalTest mTest;
	private Treatment tTest;
	private Campus campus;
	
	@Before
	public void initTest() {
		this.schedule = new Schedule();
		this.schedule2 = new Schedule();
				
		this.schedule3 = new ShiftSchedule(new ShiftTable());
		
		campus = new Campus(new Hospital());
		
		Patient testPatient = new Patient("Bob");
		Doctor testDoctor = new Doctor("Alice");
		
		Diagnosis testDiagnosis = new Diagnosis(testDoctor,testPatient,"Test");
		
		List<MedicationItemType> medicationItemTypes = new ArrayList<MedicationItemType>();
		medicationItemTypes.add(MedicationItemType.MISC);
		
		this.tTest = new Medication(testDiagnosis,"Test",true, medicationItemTypes);
		this.mTest = new BloodAnalysis(testPatient, "Test", 2);
		@SuppressWarnings("unused")
		MedicalTest mTest2 = new UltrasoundScan(testPatient,"Test",true, true);

	}
	
	@Test(expected=SchedulingException.class)
	public void testWrongAdd() throws SchedulingException {
		TimeStamp 	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,8,0),
					end2 = new TimeStamp(2011,10,8,8,30);
		
		TimePeriod time = new TimePeriod(begin, end);
		TimePeriod time2 = new TimePeriod(begin2, end2);
		
		this.schedule.addItem(time, mTest, campus);
		this.schedule.addItem(time2, tTest, campus);
	}
	
	@Test(expected=SchedulingException.class)
	public void testWrongAdd2() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,8,25),
					end2 = new TimeStamp(2011,10,8,9,0);
		
		TimePeriod time = new TimePeriod(begin, end);
		TimePeriod time2 = new TimePeriod(begin2, end2);
		
		this.schedule.addItem(time, mTest, campus);
		this.schedule.addItem(time2, tTest, campus);
	}
	
	@Test(expected=SchedulingException.class)
	public void testWrongAdd3() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,7,55),
					end2 = new TimeStamp(2011,10,8,8,1);
		
		TimePeriod time = new TimePeriod(begin, end);
		TimePeriod time2 = new TimePeriod(begin2, end2);
		
		this.schedule.addItem(time,mTest, campus);
		this.schedule.addItem(time2,tTest, campus);
	}
	
	@Test
	public void testRightAdd() {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,8,30),
					end2 = new TimeStamp(2011,10,8,9,0);
		
		TimePeriod time = new TimePeriod(begin, end);
		TimePeriod time2 = new TimePeriod(begin2, end2);
		
		try {
			this.schedule.addItem(time,mTest, campus);
			this.schedule.addItem(time2,tTest, campus); }
		catch (SchedulingException e) {
				fail("Scheduling went wrong");
			}
	}
	
	@Test
	public void testRightAdd2() {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,9,30),
					end2 = new TimeStamp(2011,10,8,10,0);
		
		TimePeriod time = new TimePeriod(begin, end);
		TimePeriod time2 = new TimePeriod(begin2, end2);
		
		try {
			this.schedule.addItem(time,mTest, campus);
			this.schedule.addItem(time2,tTest, campus); }
		catch (SchedulingException e) {
				fail("Scheduling went wrong");
			}
	}
	
	@Test
	public void testRemove() {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,9,30),
					end2 = new TimeStamp(2011,10,8,10,0);
		
		TimePeriod time = new TimePeriod(begin, end);
		TimePeriod time2 = new TimePeriod(begin2, end2);
		
		try {
			this.schedule.addItem(time,mTest, campus);
			this.schedule.addItem(time2,tTest, campus); 
			this.schedule.removeItem(mTest);
			assertEquals(this.schedule.getSchedule().size(),1);
			this.schedule.removeItem(tTest);
			assertTrue(this.schedule.getSchedule().isEmpty());
		}
		catch (SchedulingException e) {
				fail("Scheduling went wrong");
			}
	}
	
	
	@Test
	public void testFirstAvailable() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,8,45),
					end2 = new TimeStamp(2011,10,8,9,0),
					
					aBegin = new TimeStamp(2011,10,8,8,0),
					aEnd = new TimeStamp(2011,10,8,8,15),
					
					eBegin = new TimeStamp(2011,10,8,8,30),
					eEnd = new TimeStamp(2011,10,8,8,45);
		
		TimePeriod 	time = new TimePeriod(begin, end),
					time2 = new TimePeriod(begin2, end2),
					
					aTime = new TimePeriod(aBegin,aEnd),
					
					eTime = new TimePeriod(eBegin,eEnd);
		
		this.schedule.addItem(time,mTest, campus);
		this.schedule.addItem(time2,mTest, campus);
		
		TimePeriod firstAvailable = this.schedule.firstAvailable(aTime,
				new CampusId(campus),begin);
		
		assertEquals(firstAvailable,null);
		firstAvailable = this.schedule.firstAvailable(aTime,
				new CampusId(campus),TimeStamp.END_OF_DAYS);
		
		assertTrue(firstAvailable.equals(eTime));	
		
		firstAvailable = this.schedule.firstAvailable(eTime, 
				new CampusId(campus), TimeStamp.END_OF_DAYS);
		
		assertEquals(firstAvailable,eTime);
		
		assertTrue(schedule.getOverlapping().isEmpty());
	}
	
	
	@Test
	public void testMerge() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,30),
					begin2 = new TimeStamp(2011,10,8,8,45),
					end2 = new TimeStamp(2011,10,8,9,0),
					begin3 = new TimeStamp(2011,10,8,9,30),
					end3 = new TimeStamp(2011,10,8,10,0),
					
					begin4 = new TimeStamp(2011,10,8,8,30),
					end4 = new TimeStamp(2011,10,8,8,40),
					begin5 = new TimeStamp(2011,10,8,9,15),
					end5 = new TimeStamp(2011,10,8,9,50),
					begin6 = new TimeStamp(2011,10,8,10,0),
					end6 = new TimeStamp(2011,10,8,10,30),
					
					eBegin = new TimeStamp(2011,10,8,8,0),
					eEnd = new TimeStamp(2011,10,8,8,40),
					eBegin2 = new TimeStamp(2011,10,8,8,45),
					eEnd2 = new TimeStamp(2011,10,8,9,0),
					eBegin3 = new TimeStamp(2011,10,8,9,15),
					eEnd3 = new TimeStamp(2011,10,8,10,30);

		TimePeriod 	time = new TimePeriod(begin, end),
		 			time2 = new TimePeriod(begin2, end2),
		 			time3 = new TimePeriod(begin3, end3),
		
		 			time4 = new TimePeriod(begin4, end4),
		 			time5 = new TimePeriod(begin5, end5),
		 			time6 = new TimePeriod(begin6, end6),
		 			
		 			eTime = new TimePeriod(eBegin, eEnd),
		 			eTime2 = new TimePeriod(eBegin2, eEnd2),
		 			eTime3 = new TimePeriod(eBegin3, eEnd3);

		this.schedule = new ShiftSchedule(new ShiftTable());
		
		this.schedule.addItem(time,mTest, campus);
		this.schedule.addItem(time2,mTest, campus);
		this.schedule.addItem(time3,mTest, campus);	
		
		this.schedule2.addItem(time4,mTest, campus);
		this.schedule2.addItem(time5,mTest, campus);
		this.schedule2.addItem(time6,mTest, campus);
		
		Schedule mergedSchedule = this.schedule.merge(schedule2);
		Schedule expectedSchedule = new Schedule();
		
		expectedSchedule.addItem(eTime,mTest, campus);
		expectedSchedule.addItem(eTime2,mTest, campus);
		expectedSchedule.addItem(eTime3,mTest, campus);
		
		ScheduledItem<?> 	first = mergedSchedule.getSchedule().first(),
							last = mergedSchedule.getSchedule().last(),
							eFirst = expectedSchedule.getSchedule().first(),
							eLast = expectedSchedule.getSchedule().last();
		
		assertTrue(first.timeEquals(eFirst));
		assertTrue(last.timeEquals(eLast));
		
		SortedSet<ScheduledItem<?>> 	headSet = mergedSchedule.getSchedule().tailSet(first),
										eHeadSet = expectedSchedule.getSchedule().tailSet(eFirst);
		
		assertTrue(headSet.first().timeEquals(eHeadSet.first()));
	}
	
	@Test
	public void testMerge2() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,15),
					begin2 = new TimeStamp(2011,10,8,10,0),
					end2 = new TimeStamp(2011,10,8,10,30),
					
					begin3 = new TimeStamp(2011,10,8,8,15),
					end3 = new TimeStamp(2011,10,8,9,0),
					
					begin4 = new TimeStamp(2011,10,8,8,5),
					end4 = new TimeStamp(2011,10,8,8,30),
					begin5 = new TimeStamp(2011,10,8,8,50),
					end5 = new TimeStamp(2011,10,8,9,15),
					begin6 = new TimeStamp(2011,10,8,9,50),
					end6 = new TimeStamp(2011,10,8,10,25),
					
					eBegin = new TimeStamp(2011,10,8,8,0),
					eEnd = new TimeStamp(2011,10,8,9,15),
					eBegin2 = new TimeStamp(2011,10,8,9,50),
					eEnd2 = new TimeStamp(2011,10,8,10,30);

		TimePeriod 	time = new TimePeriod(begin, end),
		 			time2 = new TimePeriod(begin2, end2),
		 			time3 = new TimePeriod(begin3, end3),
		
		 			time4 = new TimePeriod(begin4, end4),
		 			time5 = new TimePeriod(begin5, end5),
		 			time6 = new TimePeriod(begin6, end6),
		 			
		 			eTime = new TimePeriod(eBegin, eEnd),
		 			eTime2 = new TimePeriod(eBegin2, eEnd2);
	
		
		this.schedule.addItem(time,mTest, campus);
		this.schedule.addItem(time2,mTest, campus);
		
		assertTrue(schedule.toString().contains(time.toString()));
		
		this.schedule2.addItem(time3,mTest, campus);	
		
		this.schedule3.addItem(time4,mTest, campus);
		this.schedule3.addItem(time5,mTest, campus);
		this.schedule3.addItem(time6,mTest, campus);
		
		Schedule mergedSchedule = this.schedule.merge(schedule2).merge(schedule3);
		Schedule expectedSchedule = new Schedule();
		
		expectedSchedule.addItem(eTime,mTest, campus);
		expectedSchedule.addItem(eTime2,mTest, campus);
		
		assertTrue(mergedSchedule.getSchedule().first().timeEquals(expectedSchedule.getSchedule().first()));
		assertTrue(mergedSchedule.getSchedule().last().timeEquals(expectedSchedule.getSchedule().last()));
	}
	
	@Test
	public void testMerge3() throws SchedulingException {
		Random rand = new Random();
		
		for (int i = 1, n = 0; i < 500; i++) 
			if (rand.nextDouble()>0.95) {
				this.schedule.addItem(new TimePeriod(new TimeStamp(2011,10,8,8,n), new TimeStamp(2011,10,8,8,i)), mTest, campus);
				n=i;
			}
		
		for (int i = 1, n = 0; i < 500; i++) 
			if (rand.nextDouble()>0.95) {
				this.schedule2.addItem(new TimePeriod(new TimeStamp(2011,10,8,8,n), new TimeStamp(2011,10,8,8,i)), mTest, campus);
				n=i;
			}
					
		Schedule mergedSchedule = this.schedule.merge(schedule2);

		assertEquals(mergedSchedule.getSchedule().size(),1);
	}
	
	
	@Test
	public void testGetScheduleByClass() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,15),
					begin2 = new TimeStamp(2011,10,8,8,15),
					end2 = new TimeStamp(2011,10,8,9,00);
		
		TimePeriod 	time = new TimePeriod(begin,end),
					time2 = new TimePeriod(begin2,end2);
		
		this.schedule.addItem(time, mTest, campus);
		this.schedule.addItem(time2, tTest, campus);
		
		SortedSet<ScheduledItem<MedicalTest>> testScheduleByClass = this.schedule.getScheduleByClass(MedicalTest.class);
		
		assertTrue(testScheduleByClass.size() == 1);
		assertTrue(testScheduleByClass.first().getScheduleEvent() == mTest);
		
	}
	
	@Test
	public void testGetScheduleByClass2() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,15),
					begin2 = new TimeStamp(2011,10,8,8,15),
					end2 = new TimeStamp(2011,10,8,9,00);
		
		TimePeriod 	time = new TimePeriod(begin,end),
					time2 = new TimePeriod(begin2,end2);
		
		this.schedule.addItem(time, mTest, campus);
		this.schedule.addItem(time2, mTest, campus);
		
		SortedSet<ScheduledItem<MedicalTest>> testScheduleByClass = this.schedule.getScheduleByClass(MedicalTest.class);
		
		assertTrue(testScheduleByClass.size() == 2);
		assertTrue(testScheduleByClass.first().getScheduleEvent() == mTest);
	}
	
	@Test
	public void testGetScheduleByClass3() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,15),
					begin2 = new TimeStamp(2011,10,8,8,15),
					end2 = new TimeStamp(2011,10,8,9,00);
		
		TimePeriod 	time = new TimePeriod(begin,end),
					time2 = new TimePeriod(begin2,end2);
		
		this.schedule.addItem(time, mTest, campus);
		this.schedule.addItem(time2, tTest, campus);
		
		SortedSet<ScheduledItem<Treatment>> testScheduleByClass = this.schedule.getScheduleByClass(Treatment.class);
		
		assertTrue(testScheduleByClass.size() == 1);
		assertTrue(testScheduleByClass.first().getScheduleEvent() == tTest);
	}
	
	@Test
	public void testGetScheduleByClass4() throws SchedulingException {
		TimeStamp	begin = new TimeStamp(2011,10,8,8,0),
					end = new TimeStamp(2011,10,8,8,15),
					begin2 = new TimeStamp(2011,10,8,8,15),
					end2 = new TimeStamp(2011,10,8,9,00);
		
		TimePeriod 	time = new TimePeriod(begin,end),
					time2 = new TimePeriod(begin2,end2);
		
		this.schedule.addItem(time, mTest, campus);
		this.schedule.addItem(time2, tTest, campus);
		
		SortedSet<ScheduledItem<Undoable>> testScheduleByClass = this.schedule.getScheduleByClass(Undoable.class);
		
		assertTrue(testScheduleByClass.size() == 2);
		assertTrue(testScheduleByClass.first().getScheduleEvent() == mTest);
		assertTrue(testScheduleByClass.last().getScheduleEvent() == tTest);
	}

	@Test
	public void testIsOverlapping() throws SchedulingException {
		Random rand = new Random();
		
		for (int i = 1, n = 0; i < 500; i++) 
			if (rand.nextDouble()>0.95) {
				this.schedule.addItem(new TimePeriod(new TimeStamp(2011,10,8,8,n), new TimeStamp(2011,10,8,8,i)), mTest, campus);
				i+=rand.nextInt(100);
				n=i;
			}
		
		for (int i = 1, n = 0; i < 500; i++) 
			if (rand.nextDouble()>0.95) {
				this.schedule2.addItem(new TimePeriod(new TimeStamp(2011,10,8,8,n), new TimeStamp(2011,10,8,8,i)), mTest, campus);
				i+=rand.nextInt(100);
				n=i;
			}
		
		Schedule mergedSchedule = this.schedule.merge(schedule2);
		
		assertFalse(mergedSchedule.isOverlapping());
	}
}
