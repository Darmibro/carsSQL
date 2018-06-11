package telran.cars.mongo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import telran.cars.entities.CarJpa;


public interface CarRepository extends JpaRepository<CarJpa, String>{

}
