package system.campus;

import system.util.Condition;

/**
 *	De CampusCondition klasse implementeerd de Condition interface en kan als
 *	predicaat worden meegegeven. 
 *	Specifiek gaat deze conditie gelinked zijn met de status van de campus en
 *	is het predicaat enkel voldaan indien de status zich niet in lockdown bevindt.
 *	Om het makkelijk te maken heeft de conditie zelf ook een status.
 */
public class CampusCondition implements Condition {
	/**
	 * De status van deze conditie
	 */
	private ConditionState state;
	
	/**
	 * Maak een nieuwe campusconditie
	 */
	CampusCondition() {
		this.setState(new DefaultConditionState());
	}
		
	/**
	 * Verandert de status van deze conditie
	 * 
	 * @param state
	 * 			de status waarnaar er verandert wordt
	 */
	private void setState(ConditionState state) {
		this.state = state;
	}
	
	/**
	 * 
	 * @return
	 * 			de status van deze conditie
	 */
	private ConditionState getState() {
		return this.state;
	}
	
	/**
	 * Waar als deze conditie zich en de open status bevindt
	 */
	@Override
	public boolean check(Object arg) {
		return this.getState().check();
	}
	
	/**
	 * De status laten veranderen van staat naar lockdown
	 */
	void lockDown() {
		this.getState().lockdown();
	}

	/**
	 * De status laten veranderen van staat
	 */
	void open() {
		this.getState().open();
	}
	
	/**
	 * De staat van de conditie	(State pattern)
	 */
	private interface ConditionState {
		boolean check();
		void lockdown();
		void open();
	}
	
	private class DefaultConditionState implements ConditionState {

		@Override
		public boolean check() {
			return true;
		}

		@Override
		public void lockdown() {
			CampusCondition.this.setState(new LockdownConditionState());
		}

		@Override
		public void open() {
		}
	}
	
	private class LockdownConditionState implements ConditionState {
		@Override
		public boolean check() {
			return false;
		}

		@Override
		public void lockdown() {
		}

		@Override
		public void open() {
			CampusCondition.this.setState(new DefaultConditionState());
		}
	}
}
