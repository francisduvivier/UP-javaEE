package system.controllers;

import java.util.HashMap;

import system.staff.StaffMember;
import system.util.BoundedStack;

/**
 * Deze klasse zet de vorige en volgende commands in een hashmap.
 *
 */

public class CommandMap {

	/**
	 * In deze hashmap zitten commands die al geweest zijn.
	 */
	private HashMap<StaffMember, BoundedStack<Command>> previousCommands;
	
	/**
	 * In deze hashmap zitten commands die redoable zijn.
	 */
	private HashMap<StaffMember, BoundedStack<Command>> nextCommands;
	
	/**
	 * De constructor van CommandMap
	 * 
	 * @param	previousCommands
	 * 			Commands die al uitgevoerd zijn
	 * @param 	nextCommands
	 * 			Commands die redoable zijn
	 * 			
	 */
	public CommandMap(HashMap<StaffMember, BoundedStack<Command>> previousCommands, HashMap<StaffMember, BoundedStack<Command>> nextCommands) {
		this.previousCommands = previousCommands;
		this.nextCommands = nextCommands;
	}
	
	/**
	 * Een methode om de voorgaande commands te krijgen
	 * @return	previousCommands
	 * 			De uitgevoerde commands
	 */
	public HashMap<StaffMember, BoundedStack<Command>> getPreviousCommands() {
			return previousCommands;
	}
	 
	/**
	 * Een methode om de volgdende commands te krijgen
	 * @return	nextCommands
	 * 			De uit te voeren, redoable, commands
	 * @return
	 */
	public HashMap<StaffMember, BoundedStack<Command>> getNextCommands() {
		return nextCommands;
	}
    


   
}
