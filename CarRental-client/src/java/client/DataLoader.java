package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import session.ManagerSessionRemote;

public class DataLoader {

//    private static Map<String, CarRentalCompany> rentals;



    public void loadRental(String crcName, String datafile, ManagerSessionRemote ms) throws IOException {
       ms.addCarRentalCompany(crcName);
        Logger.getLogger(DataLoader.class.getName()).log(Level.INFO, "loading {0} from file {1}", new Object[]{crcName, datafile});

     BufferedReader in = new BufferedReader(new InputStreamReader(DataLoader.class.getClassLoader().getResourceAsStream(datafile)));
//        while next line exists
        while (in.ready()) {
//            read line
            String line = in.readLine();
//            if comment: skip
            if (line.startsWith("#")) {
                continue;
            }
            //tokenize on ,
            StringTokenizer csvReader = new StringTokenizer(line, ",");
            //create new car type from first 5 fields
           int carTypeId= ms.addCarType(crcName,csvReader.nextToken(),
                    Integer.parseInt(csvReader.nextToken()),
                    Float.parseFloat(csvReader.nextToken()),
                    Double.parseDouble(csvReader.nextToken()),
                    Boolean.parseBoolean(csvReader.nextToken()));
            
            //create N new cars with given type, where N is the 5th field
            for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                ms.addCar(crcName,carTypeId);
            }
        }
            
    }

     
}