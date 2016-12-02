package system.scheduling.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.scheduling.Combinations;


public class CombinationsTest {
	Combinations<Integer> combinations;
	
	@Before
	public void testInit() {
		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		listOfLists.add(list1);
		listOfLists.add(list2);
		listOfLists.add(list3);
		list1.add(1);
		list1.add(2);
		list2.add(50);
		list3.add(300);
		list3.add(100);
		list3.add(500);
		combinations = new Combinations<Integer>(listOfLists);
	}
	
	@Test
	public void testCombinations1() {
		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		List<Integer> list4 = new ArrayList<Integer>();
		List<Integer> list5 = new ArrayList<Integer>();
		List<Integer> list6 = new ArrayList<Integer>();
		listOfLists.add(list1);
		listOfLists.add(list2);
		listOfLists.add(list3);
		listOfLists.add(list4);
		listOfLists.add(list5);
		listOfLists.add(list6);
		list1.add(1);
		list1.add(50);
		list1.add(300);
		list2.add(2);
		list2.add(50);
		list2.add(300);
		list3.add(1);
		list3.add(50);
		list3.add(100);
		list4.add(2);
		list4.add(50);
		list4.add(100);
		list5.add(1);
		list5.add(50);
		list5.add(500);
		list6.add(2);
		list6.add(50);
		list6.add(500);
		
		assertTrue(equalListOfLists(listOfLists, this.combinations.getCombinations()));
	}
	
	private static <T> boolean equalListOfLists(List<List<T>> listOfLists, List<List<T>> listOfLists2) {
		List<List<T>> copyListOfLists = new ArrayList<List<T>>();
		
		for (List<T> list : listOfLists2)
			copyListOfLists.add(list);
		
		for (List<T> list : listOfLists) 
			for (List<T> list2 : listOfLists2) 
				if (equalList(list, list2))
					if (!copyListOfLists.remove(list2))
						return false;
		
		return (copyListOfLists.isEmpty());
	}
	
	private static <T> boolean equalList (List<T> list, List<T> list2) {
		List<T> copyList = new ArrayList<T>();
		
		for (T elem : list2) 
			copyList.add(elem);
		
		for (T elem : list) 
			if (!copyList.remove(elem))
				return false;
		
		return (copyList.isEmpty());
	}
}
