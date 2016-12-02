package system.util;

import java.util.Iterator;
import java.util.Stack;

import annotations.SystemAPI;

/**
 * Deze klasse stelt een stack met een maximumcapaciteit voor.
 * 
 * De maximum capaciteit van deze stack is afhankelijk van de maximale
 * hoeveelheid plasters, medication items, enzovoort.
 * 
 * Wanneer de max. capaciteit bereikt wordt, wordt het oudste element
 * (element met index 0) verwijderd.
 * 
 * @author SWOP Team 10
 */
public class BoundedStack<T> implements Iterable<T> {
	/**
	 * De Stack
	 * 
	 * @invar stack != null
	 */
	private Stack<T> stack;
	/**
	 * De maximumcapaciteit van de stack
	 */
	private final int maxSize;
	
	/**
	 * Initialisatie van de stack met zijn maximumcapaciteit
	 * 
	 * @param maxSize
	 *        De maximumcapaciteit van de stack
	 */
	public BoundedStack(int maxSize) {
		this.stack = new Stack<T>();
		this.maxSize = maxSize;
	}
	
	/**
	 * Methode om een element op de stack te pushen
	 */
	public void push(T element) {
		if (this.size() == this.maxSize) {
			stack.removeElementAt(0);
		}
		
		stack.push(element);
	}
	
	/**
	 * Methode om het bovenste element van de stack te popen
	 * 
	 * @return element
	 *         Het bovenste element van de stack
	 */
	public T pop() {
		return this.stack.pop();
	}
	
	/**
	 * Methode om een element te verwijderen aan de hand van zijn positie in de stack
	 * 
	 * @param index
	 *        De positie van het te verwijderen element
	 */
	public void remove(int index) {
		this.stack.removeElementAt(index);
	}
	
	/**
	 * Methode om een element te verwijderen
	 * 
	 * @param element
	 *        Het te verwijderen element
	 */
	public void remove(T element) {
		this.stack.removeElement(element);
	}
	
	/**
	 * Methode het element op de opgegeven positie teruggeeft zonder het te verwijderen uit de stack
	 * 
	 * @param index
	 *        De positie van het terug te geven element
	 * @return element
	 *         Het terug te geven element
	 */
	@SystemAPI
	public T get(int index) {
		return this.stack.get(index);
	}
	
	/**
	 * Methode die aangeeft of de stack leeg is
	 * 
	 * @return true
	 *        Als de stack leeg is
	 *        false
	 *        Als de stack niet leeg is
	 */
	public boolean isEmpty() {
		return this.stack.isEmpty();
	}
	
	/**
	 * Methode die het aantal elementen in de stack teruggeeft
	 * 
	 * @return size
	 *        Het aantal elementen in de stack
	 */
	@SystemAPI
	public int size() {
		return this.stack.size();
	}
	
	/**
	 * Methode die een kopie van de stack maakt en teruggeeft.
	 * Op deze manier kan geen element verkeerdelijk uit de orginele stack verwijderd worden.
	 * 
	 * @param boundedStack
	 *        De orginele stack
	 * @return returnBoundedStack
	 *         De kopie van de orginele stack
	 */
	public BoundedStack<T> copy() {
		BoundedStack<T> returnBoundedStack = new BoundedStack<T>(this.size());
		
		for (int i = 0; i < this.size(); i++) {
			returnBoundedStack.push(this.get(i));
		}
		
		return returnBoundedStack;
	}

	@Override
	public Iterator<T> iterator() {
		return this.stack.iterator();
	}
}
