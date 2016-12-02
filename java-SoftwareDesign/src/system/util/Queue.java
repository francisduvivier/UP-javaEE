package system.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Klasse die een first-in-first-out (FIFO) queue representeert
 * 
 * @author SWOP Team 10
 */
public class Queue<T> implements Iterable<T> {
	/**
	 * Variabele die het aantal elementen in de queue bijhoudt
	 */
	private int size;
	/**
	 * Variabele die het begin van de queue voorstelt
	 */
	private Link in;
	/**
	 * Variabele die het einde van de queue voorstelt
	 */
	private Link out;     
	
	/**
	 * Constructor die een lege queue creeert
	 */
    public Queue() {
    	this.in = null;
        this.out  = null;
        this.size = 0;
    }
    
    /**
     * Methode die aangeeft of de queue leeg is
     * 
     * @return true
     *         Als de queue geen elementen bevat
     *         false
     *         Als de queue 1 of meerdere elementen bevat
     */
    public boolean isEmpty() {
        return this.in == null;
    }
    
    /**
     * Getter voor het aantal elementen in de queue
     * 
     * @return size
     *         Het aantal elementen in de queue
     */
    public int size() {
        return this.size;     
    }
    
    /**
     * Methode die het element dat het minst recent aan de queue is toegevoegd, teruggeeft
     * 
     * @return elem
     *         Het element dat het minst recent aan de queue is toegevoegd
     *         Als de queue minstens 1 element bevat
     *         null
     *         Als de queue leeg is
     */
    public T peek() {
        if (isEmpty()) 
        	return null;
        return this.in.elem;
    }
    
    /**
     * Methode om een element aan de queue toe te voegen
     * 
     * @param elem
     *        Het toe te voegen element
     */
	public void enqueue(T elem) {
		Link lastOut = this.out;
		this.out = new Link();
		this.out.elem = elem;
		this.out.next = null;
		if (this.isEmpty()) this.in = this.out;
		else          
			lastOut.next = this.out;
		this.size++;
	}
	
	/**
	 * Methode die het element dat het minst recent aan de queue is toegevoegd, teruggeeft en verwijderd
	 * 
	 * @return elem
	 *         Het element dat het minst recent aan de queue is toegevoegd
     *         Als de queue minstens 1 element bevat
     *         null
     *         Als de queue leeg is
	 */
	public T dequeue() {
		if (this.isEmpty()) 
			return null;
		T elem = this.in.elem;
		this.in = this.in.next;
		this.size--;
		if (this.isEmpty()) 
			this.out = null;  
		return elem;
	}
    
	/**
	 * Hulpklasse die de link tussen een element en het volgende element modelleert
	 */
	private class Link {
		private T elem;
		private Link next;
	}
	
	/**
	 * Methode om het element op de opgegeven positie in de queue terug te geeft
	 * 
	 * @param i
	 *        De positie van het element
	 * @return elem
	 *         Het gevraagde element
	 *         Als de queue minstens 1 element bevat
	 *         null
	 *         Als de queue leeg is
	 */
	public T get(int i) {
		int n = 0;
		for (T item : this) {
			if (n++ == i)
				return item;
		}
		return null;
	}
	
	/**
	 * Methode die het element op de opgegeven positie in de queue verwijderd
	 * 
	 * @param item
	 *        Het te verwijderen element
	 */
	public void remove(T item) {
		if(in.elem.equals(item)) {
				in = in.next;
				size--;
			return;
		}
		Link go = in;
		Link last = new Link();
		while (go.next != null) {
			if (go.elem.equals(item)) {
				last.next = go.next;
				size--;
				// Garbage cleaner doet de rest.
			}
			last = go;
			go = go.next;
		}
		if (go.elem.equals(item)) {
			this.out = last;
			out.next = null;
			size--;
		}
	}
	
	/**
	 * Methode die een iterator om over de elementen van de queue te itereren teruggeeft
	 * 
	 * @return iterator
	 *         De iterator om over de elementen van de queue te itereren
	 */
	public Iterator<T> iterator()  {
		return new QueueIterator();  
    }
	
	/**
	 * Concrete iteratorimplementatie 
	 */
	private class QueueIterator implements Iterator<T> {
		private Link current = Queue.this.in;

		public boolean hasNext()  { return this.current != null;                     }
		public void remove()      { 
			return;
		}

		public T next() {
			if (!this.hasNext()) throw new NoSuchElementException();
			T item = this.current.elem;
			current = this.current.next; 
			return item;
        }
    }
    
    public String toString() {
        String s = "";
        for (T elem : this)
            s += elem.toString() + " ";
        return s;
    } 
}

