package system.results.medicaltestresults;

import system.results.ResultType;
import annotations.SystemAPI;

/**
 * Deze klasse stelt het resultaat van een bloedanalyse voor.
 * 
 * @invar amountOfBlood > 0
 * @invar whiteCellCount > 1
 * @invar redCellCount > 1
 * @invar plateletCount > 1
 * 
 * @author SWOP Team 10
 */
public class BloodAnalysisResult extends MedicalTestResult {
	/**
	 * Variabele die hoeveel getrokken bloed voorstelt
	 */
	private int amountOfBlood;
	/**
	 * Variabele die het aantal getelde witte bloedcellen voorstelt
	 */
	private int whiteCellCount;
	/**
	 * Variabele die het aantal getelde rode bloedcellen voorstelt
	 */
	private int redCellCount;
	/**
	 * Variabele die het aantal getelde bloedplaatjes voorstelt
	 */
	private int plateletCount;

	/**
	 * Constructor voor BloodAnalysisResult
	 * 
	 * @param amountOfBlood	
	 *        De hoeveelheid getrokken bloed
	 * @param whiteCellCount
	 *        Het aantal getelde witte bloedcellen
	 * @param redCellCount	
	 *        Het aantal getelde rode bloedcellen
	 * @param plateletCount	
	 *        Het aantal getelde bloedplaatjes
	 */
	public BloodAnalysisResult(int amountOfBlood, int whiteCellCount, int redCellCount, int plateletCount) {
		super(ResultType.BLOODANALYSIS_RESULT);
		setAmountOfBlood(amountOfBlood);
		setWhiteCellCount(whiteCellCount);
		setRedCellCount(redCellCount);
		setPlateletCount(plateletCount);
	}
	
	/**
	 * Een toString voor BloodAnalysisResult.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return super.toString() + "Blood Analysis: "+ amountOfBlood + " " + whiteCellCount + " " + redCellCount + " " + plateletCount;
	}

	/**
	 * Getter voor de hoeveelheid getrokken bloed
	 * 
	 * @return amountOfBlood
	 *         De hoeveelheid getrokken bloed
	 */
	private int getAmountOfBlood() {
		return amountOfBlood;
	}

	/**
	 * Setter voor de hoeveelheid getrokken bloed
	 * 
	 * @param amountOfBlood	
	 *        De nieuwe hoeveelheid getrokken bloed
	 * @pre	amountOfBlood > 0
	 * 		Het aantal bloed moet strikt positief zijn
	 * @post	getAmountOfBlood() == amountOfBlood
	 * @throws IllegalArgumentException
	 * 		   De hoeveelheid moet groter zijn dan 0
	 */
	private void setAmountOfBlood(int amountOfBlood) throws IllegalArgumentException{
		if (amountOfBlood > 0)
			this.amountOfBlood = amountOfBlood;
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Getter voor het aantal getelde witte bloedcellen
	 * 
	 * @return whiteCellCount
	 *         Het aantal getelde witte bloedcellen
	 */
	private int getWhiteCellCount(){
		return whiteCellCount;
	}

	/**
	 * Setter voor het aantal getelde witte bloedcellen
	 * 
	 * @param whiteCellCount
	 *        Het nieuwe aantal getelde witte bloedcellen
	 * @pre	whiteCellCount > 1
	 * 		Aantal witte cellen moet groter zijn dan 1
	 * @post getWhiteCellCount() == whiteCellCount
	 * @throws IllegalArgumentException
	 * 			Het aantal moet positief groter zijn dan 0
	 */
	private void setWhiteCellCount(int whiteCellCount) throws IllegalArgumentException{
		if (whiteCellCount > 1)
			this.whiteCellCount = whiteCellCount;
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Getter voor het aantal getelde rode bloedcellen
	 * 
	 * @return redCellCount
	 *         Het aantal getelde rode bloedcellen
	 */
	private int getRedCellCount() {
		return redCellCount;
	}

	/**
	 * Setter voor het aantal getelde rode bloedcellen
	 * 
	 * @param redCellCount
	 *        Het nieuwe aantal getelde rode bloedcellen
	 * @pre	redCellCount > 1
	 * 		Aantal rode cellen moet groter zijn dan 1
	 * @post getRedCellCount() == redCellCount
	 * @throws IllegalArgumentException
	 * 			Het aantal moet groter zijn dan 0
	 */
	private void setRedCellCount(int redCellCount) throws IllegalArgumentException{
		if (redCellCount > 1)
			this.redCellCount = redCellCount;
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Getter voor het aantal getelde bloedplaatjes
	 * 
	 * @return plateletCount
	 *         Het aantal getelde bloedplaatjes
	 */
	private int getPlateletCount() {
		return plateletCount;
	}

	/**
	 * Setter voor het aantal getelde bloedplaatjes
	 * 
	 * @param plateletCount	
	 *        Het nieuwe aantal getelde bloedplaatjes
	 * @pre	plateletCount > 1
	 * 		Aantal platelet cellen moet groter zijn dan 1
	 * @post getWhitePlateletCount() == plateletCellCount
	 * @throws IllegalArgumentException
	 * 			Het aantal moet positief groter zijn dan 0
	 */
	private void setPlateletCount(int plateletCount) throws IllegalArgumentException{
		if (plateletCount > 1)
			this.plateletCount = plateletCount;
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Een methode om de gegevens van een bloedanalyse resultaat om te zetten naar een String.
	 * 
	 * @return details
	 * 		   De gegevens
	 */
	@Override
	public String getDetails() { //TODO nog toe te voegen
		String details = "Amount of blood: " + getAmountOfBlood() + "\n" + "White cell count: " + getWhiteCellCount() 
				+ "\n" + "Red cell count: " + getRedCellCount() + "\n" + "Platelet count: " + getPlateletCount();
		return details;
	}
	
	/**
	 * Een methode om twee bloedanalyse resultaten te vergelijken.
	 * Ze zijn gelijk als hun cel-, bloed- en plateletaantallen overeenkomen.
	 * 
	 * @return true
	 * 		   Als hun cel-, bloed- en plateletaantallen overeenkomen
	 * @return false
	 * 		   Als hun cel-, bloed- en plateletaantallen niet overeenkomen
	 */
	@Override
	public boolean equals(Object o) {
		if (this==o) 
			return true;
		
		if (!(o instanceof BloodAnalysisResult))
			return false;
		
		BloodAnalysisResult result = (BloodAnalysisResult) o;
		return (this.getPlateletCount() == result.getPlateletCount() &&
				this.getAmountOfBlood() == result.getAmountOfBlood() &&
				this.getRedCellCount() == result.getRedCellCount() &&
				this.getWhiteCellCount() == result.getWhiteCellCount());
	}
}
