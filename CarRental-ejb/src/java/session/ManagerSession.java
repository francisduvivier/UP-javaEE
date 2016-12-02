package session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;
import rental.ReservationException;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @Override
    public Set<CarType> getCarTypes(String company) {
        return new HashSet<CarType>(em.createQuery("SELECT crc.carType FROM CarRentalCompany crc "
                + "WHERE crc.name LIKE :company").setParameter("company", company).getResultList());

    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        for (Car c : em.find(CarRentalCompany.class, company).getCars(type)) {
            out.add(c.getId());
        }

        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        return em.find(CarRentalCompany.class, company).getCar(id).getReservations().size();
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
            for (Car c : em.find(CarRentalCompany.class, company).getCars(type)) {
                out.addAll(c.getReservations());
            }

        return out.size();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        Set<Reservation> out = new HashSet<Reservation>();
        for (String crcStr : getAllCarRentalCompanies()) {
            CarRentalCompany crc=em.find(CarRentalCompany.class, crcStr);
            out.addAll(crc.getReservationsBy(renter));
        }
        return out.size();
    }
    @PersistenceContext
    EntityManager em;

    @Override
    public void addCarRentalCompany(String name) {
        CarRentalCompany carRentalCompany = new CarRentalCompany(name);
        em.merge(carRentalCompany);

    }

    @Override
    public void addCar(String crcName, int carTypeId) {
        CarType carType = em.find(CarType.class, carTypeId);
        Car car = new Car(carType);
        CarRentalCompany crc = em.find(CarRentalCompany.class, crcName);
        crc.addCar(car);
//        em.persist(car); not needed here
        em.merge(crc);

    }

    @Override
    public int addCarType(String crcName, String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        
        CarType carType = new CarType(name, nbOfSeats, trunkSpace, rentalPricePerDay, smokingAllowed);
        CarRentalCompany crc= em.find(CarRentalCompany.class, crcName);
        crc.addCarType(carType); 
        em.persist(carType); //Here it is needed
        em.merge(crc);
        return carType.getId();
    }

    @Override
    public List<String> getAllCarRentalCompanies() {
        return new ArrayList<String>(em.createQuery("SELECT crc.name FROM CarRentalCompany crc").getResultList()); 
    }

    
    @Override
    public String getMostPopularCarRentalCompany(){

        String result=(String) em.createQuery("SELECT crc1.name FROM CarRentalCompany crc1 "
                + "WHERE NOT EXISTS "
                + "("
                    + "SELECT crc2 FROM CarRentalCompany crc2 WHERE "
                    + "(SELECT COUNT(res1) FROM Reservation res1 WHERE crc1.name=res1.rentalCompany) < "
                    + "(SELECT COUNT(res2) FROM Reservation res2 WHERE crc2.name=res2.rentalCompany)"
                + ")").getResultList().get(0);   
    return result;
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName) {

        CarType result = (CarType) em.createQuery("SELECT carType1 FROM CarRentalCompany crc, IN (crc.carTypes) carType1 "
                + "WHERE crc.name LIKE :company"
                + " AND NOT EXISTS"
                + "("
                    + "SELECT carType2 FROM CarRentalCompany crc, IN (crc.carTypes) carType2 "
                        + "WHERE crc.name LIKE :company AND "
                            + "(SELECT COUNT(res1) FROM Reservation res1 WHERE res1.carType=carType1.name AND res1.rentalCompany LIKE :company )"
                            + " < "
                            + "(SELECT COUNT(res2) FROM Reservation res2 WHERE res2.carType=carType2.name AND res2.rentalCompany LIKE :company )"
                + ")"
        ).setParameter("company", carRentalCompanyName).getResultList().get(0);

        return result;

    }

}
