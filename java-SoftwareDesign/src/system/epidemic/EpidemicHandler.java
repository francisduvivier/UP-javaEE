package system.epidemic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import system.patients.Diagnosis;
import system.patients.ObserverAction;
import system.time.Time;
import system.time.TimeStamp;
import system.util.Alarm;
import system.util.Pair;
import system.util.Resetable;
import system.util.Resetter;

/**
 * Deze klasse gaat om met de epidemieën.
 *
 */
public class EpidemicHandler {
	private final Map<DiseaseID,Pair<EpidemicThreat,TimeStamp>> diseaseMap;
	private final List<DiseaseID> inEpidemic;
	private final Observer observer;
	private final Alarm alarm;
	private final Time time;
	
	/**
	 * De constructor van EpedemicHandler.
	 * 
	 * @param 	alarm
	 * 			Verwittiger voor de epidemieën
	 * @param 	time
	 * 			De tijd (gevaar kan verstrijken over tijd)
	 */
	public EpidemicHandler(Alarm alarm, Time time, Resetter resetter) {
		this.diseaseMap = new HashMap<DiseaseID,Pair<EpidemicThreat,TimeStamp>>();
		this.inEpidemic = new ArrayList<DiseaseID>();
		this.observer = new DiagnosisObserver();
		this.alarm = alarm;
		this.time = time;
		resetter.setResetable(new DiseaseResetable());
	}
	
	/**
	 * Een methode om bewerkingen (keuze door observer action) te doen met een
	 * ziekte en diens bedreiging.
	 * 
	 * @param 	id
	 * 			ID van de ziekte
	 * @param 	action
	 * 			Uit te voeren actie (add of remove)
	 * @param 	threat
	 * 			Bedreigingsniveau voor epidemie
	 */
	private void count(DiseaseID id, ObserverAction action, EpidemicThreat threat) {		
		if (action == ObserverAction.ADD)
			addCount(id,threat);
		if (action == ObserverAction.REMOVE)
			removeCount(id,threat);
	}
	
	/**
	 * Een methode om al dan niet een ziekte toe te voegen aan de map van ziektes, 
	 * of het bedreigingsniveau te verhogen als hij al in de map staat.
	 * 
	 * @param 	id
	 * 			ID van de ziekte
	 * @param 	threat
	 * 			Bedreigingsniveau van de epidemie
	 */
	private void addCount(DiseaseID id, EpidemicThreat threat) {
		if (diseaseMap.containsKey(id))
			diseaseMap.get(id).getFirst().addThreat(threat);		
		else 
			diseaseMap.put(id, new Pair<EpidemicThreat,TimeStamp>(threat.copy(),time.getTime()));
	}
	
	/**
	 * Een methode om het bedreigingsniveau van een ziekte te verlagen met gegeven bedreigingsniveau.
	 * 
	 * @param id
	 * 		  ID van de ziekte
	 * @param threat
	 * 		  Bedreigingsniveau van de ziekte
	 */
	private void removeCount(DiseaseID id, EpidemicThreat threat) {
		if (diseaseMap.containsKey(id))
			diseaseMap.get(id).getFirst().removeThreat(threat);		
	}
	
	/**
	 * Een methode om de observer op te vragen.
	 * 
	 * @return	Object van de observer klasse
	 */
	public Observer getObserver() {
		return this.observer;
	}
	
	/**
	 * Een methode om acties te bepalen na toevoegen van een nieuwe diagnose.
	 * Het bedreigingsniveau van de ziekte in de diagnose wordt of toegevoegd of verhoogd,
	 * en er wordt gecontroleerd of er een epidemie is voor de ziekte.
	 * 
	 * @param arg
	 * @throws ClassCastException
	 */
	@SuppressWarnings("unchecked")
	private void updateFromDiagnosis(Object arg) throws ClassCastException {
		Pair<Diagnosis,ObserverAction> diagnosisPair = (Pair<Diagnosis,ObserverAction>) arg;
		DiseaseID id = new DiseaseID(diagnosisPair.getFirst());
		
		this.count(id,diagnosisPair.getSecond(),diagnosisPair.getFirst().getThreat());
		this.checkEpidemic(id);
	}
	
	/**
	 * Een methode om te controleren of er een epidemie is voor een ziekte,
	 * en indien dit zo is wordt er gealarmeerd.
	 * 
	 * @param 	id
	 * 			ID van de te controleren ziekte
	 */
	private void checkEpidemic(DiseaseID id) {
		if (!this.diseaseMap.containsKey(id))
			return;
		
		if (this.inEpidemic.contains(id) && 
				!this.diseaseMap.get(id).getFirst().isAtRisk()) {
			this.inEpidemic.remove(id);
			if(this.inEpidemic.isEmpty())
				alarm.falseAlarm();
		}
		
		if (this.diseaseMap.get(id).getFirst().isAtRisk()) {
			alarm.notifyAlarm(this.diseaseMap.get(id).getSecond());
			this.inEpidemic.add(id);
		}
	}
	
	/**
	 * Leegt de diseaseMap en inEpidemic
	 */
	private void resetDiseases() {
		this.diseaseMap.clear();
		this.inEpidemic.clear();
	}
	
	/**
	 * DiagnosisObserver is een klasse die Observer implementeert om op de hoogte te blijven van het gevaar.	 *
	 */
	public class DiagnosisObserver implements Observer {
		private DiagnosisObserver() {}
		
		/**
		 * Een methode om uit te voeren na toevoegen van nieuwe diagnose.
		 * Deze zal een methode van de epidemicHandler oproepen.
		 */
		@Override
		public void update(Observable o, Object arg) {
			EpidemicHandler.this.updateFromDiagnosis(arg);
		}
		
	}
	
	/**
	 * Deze klasse implementeert de Resetable interface en kan doorgegeven worden
	 * aan een resetter om de diseaseMap van de EpidemicHandler te resetten zonder
	 * er onnodige koppeling is tussen de EpidemicHandler en de klasse die deze moet
	 * resetten.
	 */
	private class DiseaseResetable implements Resetable {

		@Override
		public void reset() {
			EpidemicHandler.this.resetDiseases();
		}
		
	}
}
