package telran.cars.mongo.repo;

import org.springframework.data.jpa.repository.JpaRepository;


import telran.cars.entities.DriverJpa;


public interface DriversRepository extends JpaRepository<DriverJpa, Long>{

}
