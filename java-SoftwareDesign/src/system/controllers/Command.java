package system.controllers;

/**
 * Deze interface typeert een commando. Op elk commando moeten de methodes
 * execute() en undo() kunnen uitgevoerd worden. In de huidige versie van het
 * programma zijn er enkel commando's voor operaties die door een dokter
 * ongedaan gemaakt moeten kunnen worden.
 */
interface Command {
	
	/**
	 * Voert een commando uit.
	 */
	public void execute();

	/**
	 * Maakt een eerder uitgevoerd commando ongedaan. De methode execute() moet
	 * dus opgeroepen zijn geweest alvorens dat deze methode opgeroepen mag
	 * worden.
	 */
	public void undo();
	
	/**
	 * Voert een commando opnieuw uit.
	 */
	public void redo();
}
