package system.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import client.ICampus;
import client.IHospital;
import client.IStaffMember;

import system.campus.Campus;
import system.campus.Hospital;
import system.exceptions.IllegalOperationException;
import system.exceptions.ReschedulingException;
import system.repositories.StaffType;
import system.scheduling.ScheduleResource;
import system.staff.StaffMember;
import system.time.TimeStamp;
import system.util.BoundedStack;
import annotations.SystemAPI;

/**
 * Deze klasse is een controller voor een userInterface Sessie. Hier wordt het
 * inloggen en uitloggen van gebruikers verwerkt. In de huidige versie van het
 * programma mag er dus maar 1 Sessioncontroller aangemaakt worden in de
 * userinterface.
 * 
 * @Invar de Hospital is niet null
 */
@SystemAPI
public class SessionController {
	
	/**
	 * Variabele waar de huidige gebruiker van het systeem
	 * in wordt bijgehouden.
	 */
	private StaffMember currentUser;
	
	/**
	 * Variabele waar het hospital in wordt bijhehouden.
	 */
	private final Hospital hospital;
	
	/**
	 * Variabele waar de huidige campus wordt bijgehouden,
	 * d.w.z. waar het systeem actief is.
	 */
	private Campus currentCampus;
	private final CommandInvoker invoker;
	
	/**
	 * Constructor voor SessionController.
	 * 
	 */
	@SystemAPI
	public SessionController() {
		this.currentUser = null;
		this.hospital = new Hospital();
		this.invoker = new CommandInvoker();
	}
	
	/**
	 * Methode om te controleren te controleren dat de actor van een controller ingelogd is als gebruiker.
	 * 
	 * @param 	staffType
	 * 			Het soort staff member (Nurse, Doctor, Hospital Administrator, ...).
	 * @return 	True als er toegang mag verleend worden aan de staff member.
	 * 			False als er geen toegang mag verleend worden aan de staff member.
	 * 			|	result == !(currentUser.getResourceType() == null || currentUser.getResourceType() != staffType)
	 */
	@SystemAPI
	public boolean hasValidAccess(StaffType staffType) {
		return !(currentUser == null || currentUser.getResourceType() != staffType);
	}
	
	/**
	 * Een methode om de hospital administrator op te vragen.
	 * 
	 * @return de hospital administrator
	 */
	@SystemAPI
	public IStaffMember getHospitalAdministrator() {
		return getHospital().getStaffRepository().getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0);
	}
	
	/**
	 * @return
	 * 		Het object dat gebruikt moet worden voor het oproepen van commando's.
	 */
	CommandInvoker getCommandInvoker() {
		return this.invoker;
		
	}
	
	/**
	 * @return	currentUser
	 * 			De ingelogde gebruiker
	 */
	@SystemAPI
	public IStaffMember getCurrentUser() {
		return currentUser;
	}

	/**
	 * @return 
	 * 		de lijst van alle Staff die tot de Hospital behoort
	 */
	private List<StaffMember> getStaffOf(Campus campus) {
		return campus.getStaffRepository().getStaff();
	}
	
	/**
	 * Aanmelden van gebruiker
	 * 
	 * @param staffMember
	 * 		de StaffMember die ingelogd moet worden
	 * @invar stafmember !=null && campus!=null && this.getStaff().contains(staffMember)
	 * 
	 * @throws	NullPointerException
	 * 			Als de staff member null is
	 * 
	 * @throws 	IllegalOperationException
	 * 			Als de staff member niet in het systeem voorkomt
	 */
	@SystemAPI
	public void login(IStaffMember staffMember, ICampus campus) {
		if (staffMember == null)
			throw new NullPointerException("Staff member is null.");
		if (campus == null)
			campus = getCampusOf(staffMember);
		if (campus == null)
			campus = getCurrentCampus();
		if (!this.getStaffOf((Campus) campus).contains(staffMember) && !isHospitalStaff(staffMember))
			throw new IllegalOperationException(
					"Staff member doesn't appear in the staff repository.");
		
		currentUser = (StaffMember) staffMember;
		this.invoker.initStaffMember();
		setCurrentCampus((Campus) campus);
	}

	/**
	 * In bepaalde controllers wordt een session controller meegegeven samen met
	 * een hospital. Er moet kunnen voor gezorgd worden dat de hospital van die
	 * controller gelijk is aan de hospital van de session controller.
	 */
	@SystemAPI
	public IHospital getBigHospital() {
		return this.hospital;
	}
	
	private Hospital getHospital() {
		return this.hospital;
	}

	/**
	 * Afmelden van gebruiker
	 */
	@SystemAPI
	public void logOut() {
		currentUser = null;
	} 
	
	/**
	 * Geeft de campus terug waar de ingelogde persoon momenteel zit.
	 * @return de campus waar de ingelogde persoon zit.
	 */
	@SystemAPI
	public Campus getCurrentCampus() {
		return currentCampus;
	}
	/**
	 * Stelt in waar de ingelogde persoon zit,
	 * @param currentCampus
	 * 			De campus waar de ingelogde persoon zit
	 */
	@SystemAPI
	public void setCurrentCampus(Campus currentCampus) {
		this.currentCampus = currentCampus;
	}
	/**
	 * Een klasse voor het aanmaken van een object dat commando's kan oproepen.
	 */
	class CommandInvoker {
		private static final int MAX_PREVIOUS_COMMANDS = 20;
		private static final int MAX_NEXT_COMMANDS = 5;
		
		private CommandMap commandMap;
		
		/**
		 * Constructor van CommandInvoker.
		 */
		private CommandInvoker() {
			commandMap = new CommandMap(new HashMap<StaffMember,BoundedStack<Command>>(), new HashMap<StaffMember,BoundedStack<Command>>());
		}
		
		/**
		 * @return De ingelogde gebruiker
		 */
		private StaffMember getActiveUser() {
			return currentUser;
		}
	
		/**
		 * Methode om commando's te krijgen van ingelogde gebruiker die
		 * te undo'en zijn.
		 * 
		 * @return Stack met undoable commando's
		 */
		public BoundedStack<Command> getPreviousCommands() {
				return commandMap.getPreviousCommands().get(this.getActiveUser()).copy();
		}
	
		/**
		 * Methode om commando's te krijgen van ingelogde gebruiker
		 * die te redo'en zijn.
		 * 
		 * @return Stack met redoable commando's
		 */
		public BoundedStack<Command> getNextCommands() {
			return commandMap.getNextCommands().get(this.getActiveUser()).copy();
		}
		
		/**
		 * Methode om de stack van undoable commando's bewerkbaar
		 * te maken.
		 * Enkel te gebruiken in undoCommand(index).
		 * 
		 * @return Modifiable stack van undoable commando's
		 */
		private BoundedStack<Command> getEditablePreviousCommands() {
			return commandMap.getPreviousCommands().get(this.getActiveUser());
		}
	
		/**
		 * Methode om de stack van redoable commando's
		 * bewerkbaar te maken.
		 * Enkel te gebruiken in redoCommand(index).
		 * 
		 * @return Modifiable stack van redoable commando's
		 */
		private BoundedStack<Command> getEditableNextCommands() {
			return commandMap.getNextCommands().get(this.getActiveUser());
		}
		
		/**
		 * Methode om de undoable en redoable commando-stacks
		 * voor de ingelogde gebruiker te initialiseren.
		 */
		private void initStaffMember() {
			if (!this.commandMap.getPreviousCommands().containsKey(this.getActiveUser()))
				this.commandMap.getPreviousCommands().put(this.getActiveUser(), new BoundedStack<Command>(MAX_PREVIOUS_COMMANDS));
			if (!this.commandMap.getNextCommands().containsKey(this.getActiveUser()))
				this.commandMap.getNextCommands().put(this.getActiveUser(), new BoundedStack<Command>(MAX_NEXT_COMMANDS));
		}
			
		/**
		 * Methode om een bepaald commando uit te voeren.
		 * 
		 * @param 	command
		 * 			Uit te voeren commando
		 */
		public void executeCommand(Command command) {
			this.initStaffMember();
			
			command.execute();
			this.getEditablePreviousCommands().push(command);
		}
	
		/**
		 * Methode om een commando ongedaan te maken.
		 * 
		 * @param	index
		 * 			Indexnummber van commando in stack om ongedaan te maken
		 */
		public void undoCommand(int index) {
			this.initStaffMember();
			
			Command command = this.getEditablePreviousCommands().get(index);
			command.undo();
			this.getEditableNextCommands().push(command);
			this.getEditablePreviousCommands().remove(index);
		}
	
		/**
		 * Methode om ongedaan gemaakte commando te herdoen.
		 * 
		 * @param	index
		 * 			Indexnummber van commando in stack om te herdoen
		 * 
		 * @throws 		RescheduleDiagnosisTreatmentException
		 * 				Als er treatments zijn die gerescheduled zijn
		 */
		public void redoCommand(int index) 
				throws ReschedulingException{
			this.initStaffMember();

			Command command = this.getEditableNextCommands().get(index);
			this.getEditablePreviousCommands().push(command);
			this.getEditableNextCommands().remove(command);
			// Redo moet laatst gebeuren, want we willen dat de actie uit de lijst
			// met redoable acties verdwijnt, ookal worden er exceptions gegooid.
			command.redo();
		}
		
	}
	
	/**
	 * Geeft alle staff van een hospital, dus ook ook die in de campussen van de
	 * hospital
	 * 
	 * @return alle staffmembers
	 */
	@SystemAPI
	public List<StaffMember> getAllStaff() {
		List<StaffMember> allStaf = new ArrayList<StaffMember>();
		allStaf.addAll(getHospital().getStaffRepository().getStaff());
		for(Campus campus:hospital.getCampuses()){
			allStaf.addAll(campus.getStaffRepository().getStaff());
		}
		return allStaf;
	}
	
	/**
	 * Bepaalt of een staffmember rechtstreeks voor de hospital  werkt of niet 
	 * @param staffMember
	 * @return getBigHospital().getStaffRepository().getStaff().contains(staffMember)
	 */
	@SystemAPI
	public boolean isHospitalStaff(IStaffMember staffMember){
		return getHospital().getStaffRepository().getStaff().contains(staffMember);
	}

	/**
	 * Geeft de campus terug van de opgegeven staffmember. Als de staffmember
	 * rechtstreeks voor de campus werkt, dan geeft deze methode null.
	 * 
	 * @param staffMember
	 * @return campus waar staffmember werkt
	 */
	@SystemAPI
	public Campus getCampusOf(IStaffMember staffMember) {
		for (Campus campus: getHospital().getCampuses())
			if(campus.getStaffRepository().getStaff().contains(staffMember))
				return campus;
		return null;
	}
	/**
	 * Geeft een onaanpasbare lijst van de campussen in de hospital
	 * @return een lijst van alle campussen
	 */
	@SystemAPI
	public List<ICampus> getCampuses() {
		List<ICampus> campuses = new ArrayList<ICampus>();
		
		for (Campus campus : hospital.getCampuses()) 
			campuses.add(campus);
		
		return campuses;
	}
	/**
	 * Geeft een lijst van staffmembers het gewilde stafftype van een campus terug.
	 * @param campus
	 * @param staffType
	 * @return een lijst van StaffMember's van het gewilde StaffType
	 * 
	 * @throws 	IllegalArgumentException
	 * 			stafftypes komen niet overeen
	 */
	@SuppressWarnings("unchecked")
	@SystemAPI
	public <T extends IStaffMember> List<T> getCampusStaffByType(ICampus campus, StaffType staffType){
		List<T> staffMemberList=new ArrayList<T>();
		for(ScheduleResource staffMember:((Campus) campus).getResources(staffType)){
			if(staffMember.getResourceType()!=staffType)
				throw new IllegalArgumentException("het staff-type dat je hebt meegegeven is niet overeenkomstig met het generisce return-type.");
			staffMemberList.add((T) staffMember);
			}
		return staffMemberList;
	}

	/**
	 * Een methode om het huidige tijdstip op te vragen
	 * dat gehandteerd wordt doorheen het systeem.
	 * 
	 * @return	Het tijdstip in het ziekenhuis
	 */
	@SystemAPI
	public TimeStamp getTime() {
		return getHospital().getHospitalTime().getTime();
	}
}
