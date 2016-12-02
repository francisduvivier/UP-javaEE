package system.controllers;

import client.IHospital;
import system.campus.Campus;
import system.exceptions.IllegalAccessException;
import annotations.SystemAPI;

@SystemAPI
public abstract class StaffController {
	/**
	 * @invar sessionController != null
	 */
	protected final SessionController sessionController;
		
	/**
	 * Constructor voor StaffController
	 * 
	 * @param 	sessionController
	 * 			De session controller (om te zien wie het systeem gebruikt)
	 * 
	 * @throws	NullPointerException
	 * 			Als de session controller null is
	 */
	protected StaffController(SessionController sessionController){
		if (sessionController == null)
			throw new NullPointerException(
					"De opgegeven sessioncontroller is null");
		this.sessionController=sessionController;
		}

	/** 
	 * @return	Geeft de campus terug waar de admin nu zit.
	 * @throws  IllegalAccessException
	 * 			Wanneer er geen geldige toegang kan verleend worden.
	 */
	@SystemAPI
	public Campus getCurrentCampus() throws IllegalAccessException {
		if (!this.hasValidAccess())
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		return this.sessionController.getCurrentCampus();
	}
	
	/** 
	 * @return	Geeft de hospital terug.
	 * @throws  IllegalAccessException
	 * 			Wanneer er geen geldige toegang kan verleend worden.
	 */
	@SystemAPI
	public IHospital getBigHospital() throws IllegalAccessException {
		if (!this.hasValidAccess())
			throw new IllegalAccessException("Er kon geen toegang tot deze actie verleend worden.");
		
		return this.sessionController.getBigHospital();
	}
	/**
	 * Methode om te verzekeren dat de ingelogde gebruiker wel de juiste is.
	 * 
	 * @return 	true als de user access in de sessie controller nog klopt.
	 * 			|	result = sessionController.hasValidAccess(StaffType....)
	 */
	protected abstract boolean hasValidAccess();
}
