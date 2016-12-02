package system.util;

/**
 * Het is de bedoeling dat je een FilterValue kan meegeven aan een Filter.
 * De methode om te aanvaarden moet zelf overschreven worden.
 * 
 * Bv.
 * 		let filter = new Filter<Integer>(new Integer[] {1, 2, 3, 4});
 * 		let filterValue = new FilterValue<Integer>(){
 * 								public boolean accept(Integer t) {
 * 									return t > 2;
 * 								}
 * 							};
 * 		filter.getFiltered().equals(new Integer[] {3,4})
 * 
 * @author 	SWOP Team 10 
 * @param 	<T>
 * 			Het type van element waarvoor dit predikaat moet gelden.
 */
public interface FilterValue<T> {
	public boolean accept(T item);
}
