package telran.cars.mongo.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.cars.entities.RecordJpa;

public interface RentRecordsRepositor extends JpaRepository<RecordJpa, Integer> {

	RecordJpa findByCarRegNumberAndReturnDateNull(String carNumber);

	List<RecordJpa> findByCarFlRemovedTrueAndReturnDateBefore(LocalDate returnedDateDelet);

	@Query(value = "select count(*) from carrecords join cars  "
			+ "on car_reg_number=reg_number join driver on driver_license_id=license_id"
			+ "where birth_year between :from and :to" + "group by model_model_name"
			+ "order by count(*) desc limit 1", nativeQuery = true)
	long getMaxCountModels(@Param("from") int from, @Param("to") int to);

	/*@Query("select car.model.modelName from RecordJpa where driver.birthYear"
			+ " between :from and :to group by car.model.modelName having count(*)=:count")
	List<String> getModelNamesPopular(@Param("from") int from, @Param("to") int to, @Param("count") long maxCount);*/

}
