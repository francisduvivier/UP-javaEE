package system.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Geeft alle mogelijke combinaties in getCombinations() van de lijst van 
 * lijsten die meegegeven is in de constructor.
 * 
 * Voorbeeld: 	let comb = new Combinations({{1,2,3},{50},{200,300}})
 * 				comb.getCombinations() == {{1,50,200},{1,50,300},{2,50,200},{2,50,300},{3,50,200},{3,50,300}}
 *	
 * @param 	<E>
 * 			De klasse van de elementen in de lijsten van de lijst.
 */
public class Combinations<E> {
	private List<List<E>> listOfLists;
	private List<List<E>> combinations;

	/**
	 * De constructor voor Combinations
	 * 
	 * @param listOfLists
	 * 		  Lijsten met combinaties
	 */
	public Combinations(List<List<E>> listOfLists) {
		this.listOfLists = listOfLists;
		this.combinations = this.calculateCombinations();
	}
	
	/**
	 * Een methode om de combinaties op te vragen.
	 * 
	 * @return combinations
	 * 		   De lijst van lijsten van combinaties
	 */
	public List<List<E>> getCombinations() {
		return Collections.unmodifiableList(this.combinations);
	}
	
	/**
	 * Een methode om de combinaties te berekenen.
	 * 
	 * @return Lijst van lijsten van combinaties
	 */
	private List<List<E>> calculateCombinations() {
		return this.calculateCombinations(0);
	}
	
	/**
	 * Een methode om de combinaties te berekenen van gegeven lijst
	 * 
	 * @param i
	 * 		  Indexnummer van de lijst
	 * @return Lijst van lijsten van combinaties
	 */
	private List<List<E>> calculateCombinations(int i) {	 
		if(i == listOfLists.size()) {
			List<List<E>> result = new ArrayList<List<E>>();
			result.add(new ArrayList<E>());
			return result;
		}
	 
		List<List<E>> result = new ArrayList<List<E>>();
		List<List<E>> recursive = calculateCombinations(i+1);
	 
		for(int j = 0; j < listOfLists.get(i).size(); j++) {
			for(int k = 0; k < recursive.size(); k++) {
				List<E> newList = new ArrayList<E>();
				for(E item : recursive.get(k)) 
					newList.add(item);
				newList.add(listOfLists.get(i).get(j));
				result.add(newList);
			}
		}
		return result;
	}
}
