package system.campus;

import java.util.HashMap;
import java.util.List;

import system.time.TimeDuration;
import system.util.Clique;

/**
 * Deze klasse dient voor het leveren van de tijd die het in beslag neemt op
 * van de ene naar de andere campus te gaan. Deze classe werkt met een een Complete graaf, 
 * hiervoor gebruikt het de klasse Clique uit de Util package.
 * 
 * @author Swop Team 10
 */
public class CampusTravelTimes {
	
	/**
	 * Variabele om een clique van de campusid bij te houden
	 */
	private final Clique campusIdClique;
	
	/**
	 * Initialisatie van een TravelTime object
	 * 
	 * @param campuIdClique
	 * 			De clique waarbinnen de travel time moet opgesteld worden
	 * 
	 * @pre campuses != null
	 * 		De lijst van meegegeven campussen mag niet null (leeg) zijn.
	 */
	public CampusTravelTimes(List<Campus> campuses, List<HashMap<Object, Integer>> weightHashmaps ){
		if(campuses==null)
			throw new IllegalArgumentException("De meegegeven campus-lijst mag niet null zijn.");
		this.campusIdClique= new Clique(new CampusId(campuses.get(0)));
		this.campusIdClique.addObject(new CampusId(campuses.get(1)), weightHashmaps.get(0) );
			}
	
	/**
	 * Methode om de reistijd tussen 2 campussen te berekenen
	 * 
	 * @param one
	 * 			CampusId van de eerste campus
	 * @param two
	 * 			CampusId van de tweede campus
	 * @return de reistijd tussen 2 campussen in booggewicht
	 */
	public TimeDuration calculateTravelTime(CampusId one, CampusId two) {
		return TimeDuration.milliseconds(campusIdClique.getEdgeWeight(one, two));
	}
}
