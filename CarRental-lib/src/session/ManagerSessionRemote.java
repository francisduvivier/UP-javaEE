package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public int getNumberOfReservationsBy(String renter);

    public void addCarRentalCompany(String name);

    public void addCar(String crcName, int carTypeId);

    public int addCarType(String crcName ,String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed);

    public List<String> getAllCarRentalCompanies();

    public String getMostPopularCarRentalCompany();

    public CarType getMostPopularCarTypeIn(String carRentalCompanyName);
}