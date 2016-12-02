package testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * De JUnit Test Suite simuleert 4 scenario's:
 * 	- scenario1_patient simuleert vele patient-scenario's
 *  - scenario2_scheduling simuleert verschillende schedulings (afspraken)
 *  - scenario3_warehouse simuleert typische toepassing m.b.t. het warenhuis
 *  - scenario4_campusswitching simuleert het wisselen van campussen
 * 
 * @author Groep 10
 *
 */

@RunWith(Suite.class)
@SuiteClasses({
	Scenario1_patient.class, Scenario2_scheduling.class,
	Scenario3_warehouse.class, Scenario4_campusswitching.class
	})
public class TestSuite {
	

}
