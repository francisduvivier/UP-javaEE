package system.scheduling.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.CampusId;
import system.campus.Hospital;
import system.exceptions.InsufficientWarehouseItemsException;
import system.machines.Identifier;
import system.medicaltests.BloodAnalysis;
import system.medicaltests.XRayScan;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.repositories.MachineType;
import system.repositories.StaffType;
import system.scheduling.MainScheduler;
import system.scheduling.NormalScheduler;
import system.scheduling.Priority;
import system.staff.Doctor;
import system.staff.Shift;
import system.time.TimePeriod;
import system.time.TimeStamp;
import system.treatments.Cast;
import system.warehouse.Plaster;
import system.warehouse.stock.StockType;
import testsuite.TestWithControllers;


public class SchedulerTest extends TestWithControllers {
	private Diagnosis diagnosis, diagnosis2;
	
	@Before
	public void testInit() {
		super.testInit();
		
		this.diagnosis = new Diagnosis((Doctor) testC1Doctor1,testC1Patient1,"Syndrome of Foo");
		this.diagnosis2 = new Diagnosis((Doctor) testC2Doctor1,testC1Patient1,"Bar-asitis");
	}
	
	@Test
	public void testScheduleInsufficiantItems() {	
		do {
			Campus campus1 = (Campus) this.sessionController.getCampuses().get(0);
			Campus campus2 = (Campus) this.sessionController.getCampuses().get(1);
			campus1.getWarehouse().getStockList().getDefaultStock(StockType.PLASTER).removeWarehouseItem();
			campus2.getWarehouse().getStockList().getDefaultStock(StockType.PLASTER).removeWarehouseItem();
		} while (this.sessionController.getCurrentCampus().getWarehouse().getStockList().getDefaultStock(StockType.PLASTER).getStockSize()>0);
		
		Cast 	cast = new Cast(diagnosis,"Foobar bone",1000),
				cast2 = new Cast(diagnosis2,"Bar bone",1000);
		cast.store();
		cast2.store();
		TimeStamp timeStamp = new TimeStamp(20000,50,9,9,0);
		try {
			this.sessionController.getBigHospital().getScheduler().schedule(cast, timeStamp);
			fail("Test Failed");
		} catch (InsufficientWarehouseItemsException e) {}
		
		assertEquals(cast.getScheduledPeriod(),null);
		assertEquals(cast2.getScheduledPeriod(),null);

		this.sessionController.getCurrentCampus().getWarehouse().getStockList().getDefaultStock(StockType.PLASTER).addWarehouseItem(new Plaster());
		
		this.sessionController.getBigHospital().getScheduler().update(this.sessionController.getCurrentCampus().getWarehouse().getStockList().getDefaultStock(StockType.PLASTER), null);
		
		assertEquals(cast.getScheduledPeriod().getBegin(),timeStamp);
		assertEquals(cast2.getScheduledPeriod(),null);
	}
	
	@Test
	public void testSchedule() {
		Cast 	cast = new Cast(diagnosis,"Foo bone",1000),
				cast2 = new Cast(diagnosis2,"Bar bone",1000);
		cast.store();
		cast2.store();
		TimeStamp timeStamp = new TimeStamp(20000,50,10,20,0);
		TimeStamp eTime = new TimeStamp(20000,50,11,9,0);

		
		this.sessionController.getBigHospital().getScheduler().schedule(cast, timeStamp);
		this.sessionController.getBigHospital().getScheduler().schedule(cast2, timeStamp);
		
		assertTrue(cast.getScheduledPeriod().getEnd().before(cast2.getScheduledPeriod().getEnd()));
		assertEquals(cast.getScheduledPeriod().getBegin(), eTime);
	}

	@Test
	public void testSchedule2() {
		Cast 	cast = new Cast(diagnosis,"Foo bone",1000),
				cast2 = new Cast(diagnosis2,"Bar bone",1000),
				cast3 = new Cast(diagnosis2,"Foobar Bone",2000);
		cast.store();
		cast2.store();
		cast3.store();
		TimeStamp timeStamp = new TimeStamp(20000,50,10,20,0);
		TimeStamp 	eTime = new TimeStamp(20000,50,11,9,0),
					eTime2 = new TimeStamp(20000,50,11,11,0);

		
		this.sessionController.getBigHospital().getScheduler().schedule(cast, timeStamp);
		this.sessionController.getBigHospital().getScheduler().schedule(cast2, timeStamp);
		
		assertTrue(cast.getScheduledPeriod().getEnd().before(cast2.getScheduledPeriod().getEnd()));
		assertEquals(cast.getScheduledPeriod().getBegin(), eTime);
		assertEquals(cast2.getScheduledPeriod().getBegin(), eTime2);
		
		timeStamp = new TimeStamp(20000,51,10,9,0);
		this.sessionController.getBigHospital().getScheduler().schedule(cast3, timeStamp);
		assertEquals(cast3.getScheduledPeriod().getBegin(), timeStamp);
		assertEquals(cast3.getScheduledPeriod().getEnd(),
				TimeStamp.addedToTimeStamp(cast3.getDuration(), timeStamp));

	}
	
	@Test
	public void testAll() {
		// Check for no errors.
		Hospital hospital = new Hospital();
		Campus 
			campus = hospital.getCampuses().get(0),
			campus2 = hospital.getCampuses().get(1);
		
		campus.getPatientRepository().addPatient(new Patient("Francis"));
		campus.getPatientRepository().addPatient(new Patient("Vincent"));
		
		Patient 
			vincent = campus.getPatientRepository().getRegisteredPatients().get(1),
			francis = campus.getPatientRepository().getRegisteredPatients().get(0);
		
		campus.getMachineRepository().addMachine(new Identifier(123), 0, 3, MachineType.BLOOD_ANALYZER);
		campus2.getMachineRepository().addMachine(new Identifier(321), 2, 1, MachineType.XRAY_SCANNER);
		
		campus.getStaffRepository().addStaffMember(StaffType.NURSE,"Betsy");
		campus2.getStaffRepository().addStaffMember(StaffType.NURSE,"Betina");
		
		campus.getStaffRepository().getStaffMembers(StaffType.NURSE).get(0).addShift(new CampusId(campus), 
				new Shift(new TimePeriod(new TimeStamp(1,1,1,9,0),new TimeStamp(1,1,1,12,0))));
		campus2.getStaffRepository().getStaffMembers(StaffType.NURSE).get(0).addShift(new CampusId(campus2), 
				new Shift(new TimePeriod(new TimeStamp(1,1,1,9,0),new TimeStamp(1,1,1,12,0))));
		
		Random rand = new Random();
		MainScheduler scheduler = new NormalScheduler(hospital);
		Patient patient;
		double chance;
		int n = 1;
		for (int i = 0; i < 50; i++) {
			chance = rand.nextDouble();
			patient = (rand.nextDouble()>0.50?vincent:francis);
			BloodAnalysis test = new BloodAnalysis(patient
					, (chance>0.3&&chance<0.6?Priority.URGENT:(chance<=0.3?Priority.NORMAL:Priority.NO_PRIORITY)), patient.toString()+" "+n, 1+rand.nextInt(10));
			patient = (rand.nextDouble()>0.50?vincent:francis);
			n++;			 
			chance = rand.nextDouble();

			XRayScan test2 = new XRayScan(	patient,
											(chance>0.3&&chance<0.6?Priority.URGENT:(chance<=0.3?Priority.NORMAL:Priority.NO_PRIORITY)),
											patient.toString()+" "+n, 1+rand.nextInt(10), 
											1+rand.nextInt(10));
			n++;
			//BloodAnalysis test = new BloodAnalysis(testPatient, (chance>0.3&&chance<0.5?Priority.URGENT:Priority.NORMAL), "test"+i, 1+rand.nextInt(10));
			scheduler.schedule(test, new TimeStamp(2012,10,12,10,20));
			scheduler.schedule(test2, new TimeStamp(2012,10,12,10,20));
		}
	}

}
