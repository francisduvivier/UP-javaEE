package system.util;
/**
 * Deze klasse kan gebruikt worden om een collectie van een zeker type te filteren
 * op een filtervalue.
 * 
 * @author SWOP Team 10 
 * 
 * @param 	<T> 
 * 			het type elementen van de collectie die moet gefilterd worden.
 */
import java.util.ArrayList;
import java.util.Collection;

public class Filter<T> {
	private Collection<T> filterOn;
	
	/** 
	 * Maakt een nieuwe filter
	 * @param 	filterOn
	 * 			De collectie die moet gefilterd worden
	 */
	public Filter(Collection<T> filterOn) {
		this.filterOn = filterOn;
	}
	
	/**
	 * Geeft de gefilterde versie (volgens meegegeven predikaat) van de collectie.
	 * @param 	byFilter
	 * 			De filterValue die het predikaat voorschrijft.
	 * @return	Gefilterde versie volgens byFilter van de collectie
	 */
	public Collection<T> getFiltered(FilterValue<T> byFilter) {
		Collection<T> copy = new ArrayList<T>();
		
		for (T item : filterOn) 
			if (byFilter.accept(item))
				copy.add(item);
		
		return copy;
	}
}
