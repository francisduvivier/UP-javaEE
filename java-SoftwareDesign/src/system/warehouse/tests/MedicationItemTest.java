package system.warehouse.tests;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import system.time.TimeStamp;
import system.warehouse.MedicationItem;
import system.warehouse.MedicationItemType;


public class MedicationItemTest {	
	@Test
	public void testMedicationItemConstructor() {
		TimeStamp timeStamp = new TimeStamp(2012, 0, 1, 11, 14);
		MedicationItem testMedicationItem = new MedicationItem(timeStamp, MedicationItemType.ASPIRIN);
		TimeStamp expirationDate = testMedicationItem.getExpirationDate();
		assertEquals(2012, expirationDate.get(Calendar.YEAR));
		assertEquals(0, expirationDate.get(Calendar.MONTH));
		assertEquals(1, expirationDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, expirationDate.get(Calendar.HOUR));
		assertEquals(14, expirationDate.get(Calendar.MINUTE));
		assertEquals(MedicationItemType.ASPIRIN, testMedicationItem.getType());
	}
}
