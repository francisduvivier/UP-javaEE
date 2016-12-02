package system.util;

import java.util.HashMap;

public class Clique {

	private HashMap<Object, HashMap<Object, Integer>> cliqueHashMap;
	/**
	 * Maakt een nieuwe clique aan. Er moet 1 node opgegeven zijn, men kan dus geen lege clique maken.
	 * @param firstNode
	 */
	public Clique(Object firstNode){
		cliqueHashMap = new HashMap<Object, HashMap<Object,Integer>>();
		cliqueHashMap.put(firstNode, new HashMap<Object, Integer>());
	}
	
	/**
	 * Geeft het gewicht van de zijde tussen twee knopen.
	 * @param obj1 & node2 de twee knopen waarover het gaat.
	 * @return
	 * 		Het gewicht van de zijde tussen node1 en node2
	 */
	public int getEdgeWeight(Object node1, Object node2) {
		return cliqueHashMap.get(node1).get(node2);
	}
	/**
	 * Voegt een node toe aan de clique
	 * @param toBeAddedNode De node die men zou willen toevoegen
	 * @param weightMap de gewichten van de edges tussen de huidige nodes en de nieuwe node
	 */
	public void addObject(Object toBeAddedNode, HashMap<Object, Integer> weightMap){
		canBeAdded(toBeAddedNode, weightMap);
		//We beginnen een nieuwe loop zodat we niet me een inconsistente hashmap zitten indien 1 van de objecten van de meegegeven hashmap onjuist is.
		for(Object obj:weightMap.keySet()){
			cliqueHashMap.get(obj).put(toBeAddedNode, weightMap.get(obj));
		}
		cliqueHashMap.put(toBeAddedNode, weightMap);
	}

	/**
	 * Kijkt of de argumenten die meegegeven zijn correct zijn om en nieuwe Node aan de clique toe te voegen.
	 * @param toBeAddedNode De node die men zou willen toevoegen
	 * @param weightMap de gewichten van de edges tussen de huidige nodes en de nieuwe node
	 */
	private void canBeAdded(Object toBeAddedNode,
			HashMap<Object, Integer> weightMap) {
		if(cliqueHashMap.containsKey(toBeAddedNode))
			throw new IllegalArgumentException("het object dat je wil toevoegen zit al in de clique");
		if (weightMap.size()!=cliqueHashMap.size())
			throw new IllegalArgumentException("Je hebt niet het juiste aantal gewichten meegegeven");
		for(Object obj:weightMap.keySet()){
			if(!cliqueHashMap.containsKey(obj))
				throw new IllegalArgumentException("minstens 1 van de objecten die je meegegeven hebt is geen deel van de clique");
		}
	}

	
	
}