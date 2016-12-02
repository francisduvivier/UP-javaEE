package system.repositories.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import system.machines.Identifier;
import system.machines.Machine;
import system.repositories.MachineRepository;
import system.repositories.MachineType;


public class MachineRepositoryTest {

	private MachineRepository machineRepository;
	private Machine testMachine1, testMachine2, testMachine3;
	
	@Before
	public void testInit() {
		machineRepository = new MachineRepository();
		machineRepository.addMachine(new Identifier(5), 0, 1, MachineType.BLOOD_ANALYZER);
		machineRepository.addMachine(new Identifier(6), 0, 1, MachineType.SURGICAL_EQUIPMENT);
		machineRepository.addMachine(new Identifier(7), 0, 1, MachineType.XRAY_SCANNER);
		testMachine1 = machineRepository.getMachines().get(0);
		testMachine2 = machineRepository.getMachines().get(1);
		testMachine3 = machineRepository.getMachines().get(2);
	}
	
	@Test
	public void testGetResources() {
		assertEquals(machineRepository.getResources(MachineType.BLOOD_ANALYZER).get(0), testMachine1);
		assertEquals(machineRepository.getResources(MachineType.SURGICAL_EQUIPMENT).get(0), testMachine2);
		assertEquals(machineRepository.getResources(MachineType.XRAY_SCANNER).get(0), testMachine3);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidGetResources() {
		machineRepository.getResources(null);
	}
}
