package system.io;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import annotations.SystemAPI;

public class CustomFile {
	
	/**
	 * Methode om een object weg te schrijven naar een  bestand
	 * 
	 * @param file
	 * 			Gewenste bestandsnaam+extensie
	 * @param object
	 * 			Weg te schrijven object
	 */
	@SystemAPI
	public static void writeObject(String file, Object object) {
		try{
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
		  } catch (Exception e){}
	}
	
	/**
	 * Methode om een object in te lezen van een bestand
	 * 
	 * @param file
	 * 			In te lezen bestand
	 * @return Het ingelezen object van het bestand
	 */
	@SystemAPI
	public static Object getObject(String file) {
		Object object = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = ois.readObject();
			ois.close();			
		} catch (Exception e) {}
		
		return object;
	}
	
	/**
	 * Methode om een string weg te schrijven naar een bestand
	 * 
	 * @param file
	 * 			Gewenste bestandsnaam+extensie
	 * @param txt
	 * 			Weg te schrijven string
	 */
	public static void writeTxt(String file, String txt) {
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(txt);
			out.close();
		} catch (Exception e) {}
	}
}
