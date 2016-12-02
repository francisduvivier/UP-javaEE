package system.scheduling.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.scheduling.Event;
import system.scheduling.EventQueue;
import system.time.TimeStamp;


public class EventQueueTest {
	
	private EventQueue eventQueue;
	
	@Before
	public void testInit() {
		TimeStamp testTimeStamp = new TimeStamp(2011, 11, 26, 2, 27);
		this.eventQueue = new EventQueue(testTimeStamp);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testAddEvent() {
		TimeStamp timeStamp = new TimeStamp(2011,11, 20, 2, 0);
		TestEvent testEvent = new TestEvent(timeStamp);

		this.eventQueue.addEvent(testEvent);
	}
	
	@Test 
	public void testAddEvent2() {
		TimeStamp timeStamp = new TimeStamp(2011,11, 28, 2, 0);
		TestEvent testEvent = new TestEvent(timeStamp);

		this.eventQueue.addEvent(testEvent);
	}
	
	@Test
	public void testRemoveEvent() {
		TimeStamp timeStamp = new TimeStamp(2011,11, 28, 2, 0);
		TestEvent testEvent = new TestEvent(timeStamp);
		
		this.eventQueue.addEvent(testEvent);
		
		assertTrue(this.eventQueue.removeEvent(testEvent));
	}
	
	@Test
	public void testExecuteUntill() {
		TimeStamp 	timeStamp = new TimeStamp(2011,11, 28, 2, 0),
					timeStamp2 = new TimeStamp(2011,11,29,1,59),
					timeStamp3 = new TimeStamp(2011,11,29,2,0),
					timeStamp4 = new TimeStamp(2011,11,30,2,0);
		TestEvent testEvent = new TestEvent(timeStamp),
				  testEvent2 = new TestEvent(timeStamp3);
		
		this.eventQueue.addEvent(testEvent);
		this.eventQueue.addEvent(testEvent2);
		
		assertFalse(testEvent.executed);
		assertFalse(testEvent2.executed);
		this.eventQueue.executeUntil(timeStamp2);
		assertTrue(testEvent.executed);
		assertFalse(testEvent2.executed);
		this.eventQueue.executeUntil(timeStamp3);
		assertTrue(testEvent.executed);
		assertTrue(testEvent2.executed);
		this.eventQueue.executeUntil(timeStamp4);
		assertTrue(testEvent.executed);
		assertTrue(testEvent2.executed);
	}
	
	private class TestEvent extends Event {

		private boolean executed;

		private TestEvent(TimeStamp executionTime) {
			super(executionTime);
			this.executed = false;
		}
		
		@Override
		public void execute() {
			executed = true;
		}
		
		
	}
}
