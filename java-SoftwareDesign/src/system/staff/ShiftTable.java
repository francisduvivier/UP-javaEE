package system.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import system.campus.CampusId;
import system.time.TimePeriod;
import system.time.TimeStamp;
import annotations.SystemAPI;

public class ShiftTable {
	private final Map<CampusId,List<Shift>> shiftTable;
	
	/**
	 * Initialisatie van een ShiftTable object
	 */
	public ShiftTable() {
		this.shiftTable = new HashMap<CampusId,List<Shift>>();
	}
	
	/**
	 * Methode om een shift naargelang campusId toe te voegen aan de shiftTable
	 * 
	 * @param campus
	 * 			Gegeven campusId
	 * @param shift
	 * 			Toe te voegen shift
	 */
	public void add(CampusId campus, Shift shift) {
		if (shiftTable.containsKey(campus)) 
			shiftTable.get(campus).add(shift);
		else {
			List<Shift> shiftList = new ArrayList<Shift>();
			shiftList.add(shift);
			shiftTable.put(campus, shiftList);
		}
	}
	
	/**
	 * Getter voor de lijst van shifts naargelang campusId
	 * 
	 * @param campus
	 * 			CampusId waarvan we de shift willen
	 * @return Lijst van shifts van gegeven CampusId
	 */
	public List<Shift> getShiftList(CampusId campus) {
		if (!containsCampus(campus))
			return null;
		else 
			return Collections.unmodifiableList(shiftTable.get(campus));
	}
	
	/**
	 * Methode om te controleren of de shiftTable de campusId bevat
	 * 
	 * @param campus
	 * 			CampusId van de campus waarvan we willen weten of deze in de shiftTable zit
	 * @return True als de shiftTable de campus bevat
	 * 			| result == (getShiftTable().containsKey(campus))
	 */
	private boolean containsCampus(CampusId campus) {
		return shiftTable.containsKey(campus);
	}
	
	/**
	 * Methode om de doorsnede van twee shiftTables te bekomen
	 * 
	 * @param otherTable
	 * 			De shiftTable waar verbonden met verbonden moet worden
	 * @return De doorsnede van de twee shiftTables
	 */
	public ShiftTable intersect(ShiftTable otherTable) {
		if (otherTable == null) {
			return this.copyShiftTable();
		}
		
		ShiftTable returnTable = new ShiftTable();
		Map<CampusId,List<Shift>> shiftTable = new HashMap<CampusId,List<Shift>>();
		
		shiftTable = returnTable.shiftTable;
		
		for (CampusId campus : this.shiftTable.keySet()) {
			if (otherTable.containsCampus(campus))
				shiftTable.put(campus,mergeLists(this.getShiftList(campus),
						otherTable.getShiftList(campus)));	
		}
		
		return returnTable;
	}
	
	/**
	 * Methode om te controleren of de shiftTable leeg is
	 * 
	 * @return True als de shiftTable leeg is
	 *		   False als de shiftTable niet leeg is
	 */
	public boolean isEmpty() {
		for (CampusId campus : this.shiftTable.keySet()) 
			if (!this.shiftTable.get(campus).isEmpty())
				return false;
		return true;
	}
	
	/**
	 * Methode om twee lijsten van shifts samen te smelten
	 * 
	 * @param list1
	 * 		  Eerste shiftlijst
	 * @param list2
	 * 	  	  Tweede shiftlijst
	 * @return Samengesmolten shiftlijst
	 */
	private static List<Shift> mergeLists(List<Shift> list1, List<Shift> list2) {
		List<Shift> returnList = new ArrayList<Shift>();
		
		for (Shift shift1 : list1)
			for (Shift shift2 : list2) {
				Shift mergedShift = shift1.intersect(shift2);
				if (mergedShift != null) 
					returnList.add(mergedShift);
			}
		
		return returnList;
	}
	
	/**
	 * Een toString voor ShiftTable.
	 */
	@Override
	@SystemAPI
	public String toString() {
		if (isEmpty()) 
			return "No shifts";
		
		String returnString = "";
		for (CampusId campus : this.shiftTable.keySet()) 
			for (Shift shift : this.shiftTable.get(campus))
				returnString += "\n"+campus.toString()+"\n "+shift.toString();

		returnString = returnString.replaceFirst("\n","");
		
		return returnString;
	}
	
	/**
	 * Methode om te controleren of gewerkt wordt in een bepaalde tijdsperiode in een bepaalde campus
	 * 
	 * @param timePeriod
	 * 			Tijdsperiode waarvan gekeken moet worden of er binnen gewerkt wordt
	 * @param campus
	 * 			CampusId behorende bij de Campus
	 * @return True als er gewerkt wordt binnen gegeven tijdsperiode
	 * 			| False als er niet gewerkt wordt binnen gegeven tijdsperiode
	 */
	public boolean isWorking(TimePeriod timePeriod, CampusId campus) {
		List<Shift> shiftList = null;
		
		for (CampusId campusId : this.shiftTable.keySet()) {
			if (campus.equals(campusId)) 
				shiftList = this.shiftTable.get(campusId);
		}
		
		if (shiftList != null)
			for (Shift shift : shiftList)
				if (shift.isWorking(timePeriod))
					return true;
		
		return false;
	}
	
	/**
	 * Methode om te controleren of gewerkt wordt op een bepaald moment in een bepaalde campus
	 * 
	 * @param timeStamp
	 * 			Moment waarvan gekeken moet worden of er gewerkt wordt
	 * @param campus
	 * 			CampusId behorende bij de Campus
	 * @return True als er gewerkt wordt op het gegeven moment
	 * 			| False als er niet gewerkt wordt op het gegeven moment
	 */
	public boolean isWorking(TimeStamp timeStamp, CampusId campus) {
		return this.isWorking(new TimePeriod(timeStamp,timeStamp),campus);
	}
	
	/**
	 * Methode om een shift table te kopieëren
	 * 
	 * @return De gekopieerde shiftTable
	 */
	public ShiftTable copyShiftTable() {
		ShiftTable returnTable = new ShiftTable();
		
		Map<CampusId,List<Shift>> shiftTable = returnTable.shiftTable;
		
		for (CampusId campus : this.shiftTable.keySet()) {
			shiftTable.put(campus, new ArrayList<Shift>());
			for (Shift shift : this.shiftTable.get(campus))
				shiftTable.get(campus).add(shift);
		}
		
		return returnTable;
	}
	
	/**
	 * Methode om de eerste beschikbare tijdsperiode te vinden in een shiftTable met
	 * gegeven tijdsperiode en van gegeven campus
	 * 
	 * @param timePeriod
	 * 			Tijdsperiode waarna de eerste beschikbare tijdsperiode gevonden moet worden
	 * @param campus
	 * 			CampusId behorende bij de campus
	 * @return eerst gevonden tijdsperiode
	 */
	public TimePeriod firstAvailable(TimePeriod timePeriod, CampusId campus) {
		TimePeriod chosenOne = null;
		
		if (getShiftList(campus) == null) 
			return null;
		
		for (Shift shift : getShiftList(campus)) {
			if (chosenOne == null) {
				chosenOne = shift.nextAvailable(timePeriod);
			} else {
				TimePeriod runnerUp = shift.nextAvailable(timePeriod);
				
				if (runnerUp.getBegin().before(chosenOne.getBegin())) 
					chosenOne = runnerUp;
			}
		}
		
		return chosenOne;
	}
	
	/**
	 * Methode om de eerste beschikbare moment te vinden in een shiftTable met
	 * gegeven moment en van gegeven campus
	 * 
	 * @param timeStamp
	 * 			Moment waarna de eerste beschikbare moment gevonden moet worden
	 * @param campus
	 * 			CampusId behorende bij de campus
	 * @return eerst gevonden moment
	 */
	public TimeStamp firstAvailable(TimeStamp timeStamp, CampusId campus) {
		TimePeriod chosenOne = null;
		
		if (getShiftList(campus) == null) 
			return null;
		
		for (Shift shift : getShiftList(campus)) {
			if (chosenOne == null) {
				chosenOne = shift.nextAvailable(new TimePeriod(timeStamp,timeStamp));
			} else {
				TimePeriod runnerUp = shift.nextAvailable(new TimePeriod(timeStamp,timeStamp));
				
				if (runnerUp.getBegin().before(chosenOne.getBegin())) 
					chosenOne = runnerUp;
			}
		}
		
		return chosenOne.getBegin();
	}
	
	public boolean equals(Object o) {
		if (this == o) 
			return true;
		
		if (!(o instanceof ShiftTable))
			return false;
		
		ShiftTable t = (ShiftTable) o;
		
		for (CampusId campus : t.shiftTable.keySet()) 
			if (!t.getShiftList(campus).equals(getShiftList(campus)))
				return false;
		
		return true;
	}
}
