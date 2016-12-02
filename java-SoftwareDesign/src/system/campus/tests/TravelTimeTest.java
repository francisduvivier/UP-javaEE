package system.campus.tests;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.campus.CampusId;
import system.campus.CampusTravelTimes;
import system.campus.Hospital;
import system.time.TimeDuration;
import system.util.Clique;

public class TravelTimeTest {
	Hospital hospital;
	CampusTravelTimes travel;
	
	@Before
	public void testInit() {
		hospital = new Hospital();
		travel = hospital.getCampusTravelTimes();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorInvalid() {
		travel = new CampusTravelTimes(null, null);
	}
	
	@Test
	public void testCalculateTravelTime() {
		CampusId campusId1 = new CampusId(hospital.getCampuses().get(0)),
				campusId2 = new CampusId(hospital.getCampuses().get(1));
		
		Clique campusIdClique= new Clique(campusId1);
		HashMap<Object, Integer> weightHashmap=new HashMap<Object, Integer>();
		weightHashmap.put(campusId1, (int)TimeDuration.minutes(15).getMilliseconds());
		campusIdClique.addObject(campusId2, weightHashmap);
		List<HashMap<Object,Integer>> weightHashMaps=new ArrayList<HashMap<Object,Integer>>();
		weightHashMaps.add(weightHashmap);
		travel = new CampusTravelTimes(hospital.getCampuses(),weightHashMaps);
		
		assertEquals(TimeDuration.minutes(15), travel.calculateTravelTime(campusId1, campusId2));
	}

}
