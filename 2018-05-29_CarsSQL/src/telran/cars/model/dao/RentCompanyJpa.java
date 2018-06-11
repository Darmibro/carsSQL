package telran.cars.model.dao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.cars.dto.*;
import telran.cars.entities.*;
import telran.cars.mongo.repo.*;


@Service
public class RentCompanyJpa extends AbstractRentCompany{ //implements IRentCompany {
	protected RentCompanyData companyData=new RentCompanyData(); 

	@Autowired
	CarRepository cars;
	@Autowired
	DriversRepository drivers;
	@Autowired
	ModelRepository models;
	@Autowired
	RentRecordsRepositor records;

	@Override
	@Transactional
	public CarsReturnCode addModel(Model model) {
		if (models.existsById(model.getModelName())) {
			return CarsReturnCode.CAR_EXISTS;
		}
		models.save(new ModelJpa(model.getModelName(), model.getGasTank(), 
				model.getCompany(), model.getCountry(), model.getPriceDay()));
		return CarsReturnCode.OK;
	}

	@Override
	@Transactional
	public CarsReturnCode addCar(Car car) {
		if (models.existsById(car.getModelName()) == false) {
			return CarsReturnCode.NO_MODEL;
		}
		if (cars.existsById(car.getRegNumber())) {
			return CarsReturnCode.CAR_EXISTS;
		}
		ModelJpa model=models.findById(car.getModelName()).get();
		cars.save(new CarJpa(car.getRegNumber(), car.getColor(), car.getState(), 
				car.isInUse(), car.isFlRemoved(), model));
		return CarsReturnCode.OK;
	}

	@Override
	@Transactional
	public CarsReturnCode addDriver(Driver driver) {
		if (drivers.existsById(driver.getLicenseId())) {
			return CarsReturnCode.DRIVER_EXISTS;
		}
		drivers.save(new DriverJpa(driver.getLicenseId(), driver.getName(), driver.getBirthYear(), 
				driver.getPhone()));
		return CarsReturnCode.OK;
	}

	@Override
	public Model getModel(String modelName) {
		ModelJpa modelJpa=models.findById(modelName).get();
		return modelJpa==null?null:getModel(modelJpa);
	}

	private Model getModel(ModelJpa modelJpa) {
		return new Model(modelJpa.getModelName(), modelJpa.getGasTank(), modelJpa.getCompany()
				, modelJpa.getCountry(),modelJpa.getPriceDay());
	}

	@Override
	public Car getCar(String carNumber) {
		CarJpa carJpa = cars.findById(carNumber).get();
		
		return carJpa != null ? getCarDto(carJpa): null;
	}

	private Car getCarDto(CarJpa carJpa) {
		return new Car(carJpa.getRegNumber(), carJpa.getColor(), carJpa.getModel().getModelName());
	}

	@Override
	public Driver getDriver(long licenseId) {
		DriverJpa driverJpa = drivers.findById(licenseId).get();
		return driverJpa != null ? new Driver(driverJpa.getLicenseId(), 
				driverJpa.getName(), driverJpa.getBirthYear(), driverJpa.getPhone()) : null;
	}

	@Override
	@Transactional
	public CarsReturnCode rentCar(String regNumber, long licenseId, LocalDate rentDate, int rentDays) {
		CarJpa carJpa = cars.findById(regNumber).get();
		if (carJpa == null || carJpa.isFlRemoved())
			return CarsReturnCode.NO_CAR;
		if (carJpa.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		DriverJpa driverJpa=drivers.findById(licenseId).get();
		if (driverJpa == null)
			return CarsReturnCode.NO_DRIVER;
		
		RecordJpa recordJpa = new RecordJpa(driverJpa, carJpa,rentDate, rentDays);
		records.save(recordJpa);
		carJpa.setInUse(true);
		return CarsReturnCode.OK;
	}

	@Override
	@Transactional
	public CarsReturnCode returnCar(String carNumber, long licenseId, LocalDate returnDate, int gasTankPercent,
			int damages) {
		
		RecordJpa recordJpa=records.findByCarRegNumberAndReturnDateNull(carNumber);
		if (recordJpa==null)
			return CarsReturnCode.CAR_NOT_RENTED;
		if (returnDate.isBefore(recordJpa.getRentDate()))
			return CarsReturnCode.RETURN_DATE_WRONG;
		CarJpa carJpa = cars.findById(carNumber).get();
		updateRecordJpa(returnDate, gasTankPercent, damages, recordJpa, carJpa);

		
		recordJpa.setDamages(damages);
		recordJpa.setGasTankPercent(gasTankPercent);
		recordJpa.setReturnDate(returnDate);
		setCost(recordJpa, carJpa);
		
		return CarsReturnCode.OK;
	}
	
	private void updateRecordJpa(LocalDate returnDate, int gasTankPercent, int damages, RecordJpa recordJpa,
			CarJpa carJpa) {
		recordJpa.setDamages(damages);
		recordJpa.setGasTankPercent(gasTankPercent);
		recordJpa.setReturnDate(returnDate);
		
		
	}

	private void setCost(RecordJpa recordJpa, CarJpa carJpa) {
		long period = ChronoUnit.DAYS.between(recordJpa.getRentDate(), recordJpa.getReturnDate());
		float costPeriod = 0;
		ModelJpa modelJpa = models.findById(carJpa.getModel().getModelName()).orElse(null);
		float costGas = 0;
		costPeriod = getCostPeriod(recordJpa, period, modelJpa);
		costGas = getCostGas(recordJpa, modelJpa);
		recordJpa.setCost(costPeriod + costGas);

	}

	private float getCostGas(RecordJpa rentRecordCrud, ModelJpa modelCrud) {
		float costGas;
		int gasTank = modelCrud.getGasTank();
		float litersCost = (float) (100 - rentRecordCrud.getGasTankPercent()) * gasTank / 100;
		costGas = litersCost * companyData.getGasPrice();
		return costGas;
	}

	private float getCostPeriod(RecordJpa rentRecordCrud, long period, ModelJpa modelCrud) {
		float costPeriod;
		long delta = period - rentRecordCrud.getRentDays();
		float additionalPeriodCost = 0;

		if (modelCrud == null)
			throw new IllegalArgumentException("Car contains wrong model");
		int pricePerDay = modelCrud.getPriceDay();
		int rentDays = rentRecordCrud.getRentDays();
		if (delta > 0) {
			additionalPeriodCost = getAdditionalPeriodCost(pricePerDay, delta);
		}
		costPeriod = rentDays * pricePerDay + additionalPeriodCost;
		return costPeriod;
	}

	private float getAdditionalPeriodCost(int pricePerDay, long delta) {
		float fineCostPerDay = pricePerDay * companyData.getFinePercent() / 100;
		return (pricePerDay + fineCostPerDay) * delta;
	}

	@Override
	@Transactional
	public CarsReturnCode removeCar(String carNumber) {
		CarJpa carJpa = cars.findById(carNumber).get();
		if (carJpa == null)
			return CarsReturnCode.NO_CAR;
		if (carJpa.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		carJpa.setFlRemoved(true);
		return CarsReturnCode.OK;
	}
	
	@Override
	@Transactional
	public List<Car> clear(LocalDate currentDate, int days) {
		LocalDate returnedDateDelete = currentDate.minusDays(days);
		List<RecordJpa> recordDelete = getRecordsForDelete(returnedDateDelete);
		List<CarJpa> carsForDalete=getCarsJpaForDelete(recordDelete);
		List<Car> result=carsForDalete.stream().map(x->x.getCar()).collect(Collectors.toList());
		carsForDalete.forEach(cars::delete);
	return result;
	}

	private List<CarJpa> getCarsJpaForDelete(List<RecordJpa> recordDelete) {
		return recordDelete.stream().map(x->x.getCar()).collect(Collectors.toList());
	}

	private List<RecordJpa> getRecordsForDelete(LocalDate returnedDateDelete) {
		return records.findByCarFlRemovedTrueAndReturnDateBefore(returnedDateDelete);
	}

	@Override
	public List<Driver> getCarDrivers(String carNumber) {
		CarJpa carJpa=cars.findById(carNumber).get();
		if (carJpa==null) {
			return new ArrayList<>();
		}
		List<Driver> result=carJpa.getRecords().stream().map(r->r.getDriver().getDriverDto()).distinct().collect(Collectors.toList());
		return result;
	}

	@Override
	public List<Car> getDriverCars(long licenseId) {
		return cars.findAll().stream().map(CarJpa::getCar).collect(Collectors.toList());
	}

	@Override
	public Stream<Car> getAllCars() {
		return cars.findAll().stream().map(CarJpa::getCar);
	}

	@Override
	public Stream<Driver> getAllDrivers() {
		return drivers.findAll().stream().map(DriverJpa::getDriverDto);
	}

	@Override
	public Stream<RentRecord> getAllRecords() {
		return records.findAll().stream().map(x->x.getRentRecord());
	}

	@Override
	public List<String> getAllModels() {

		return models.findAll().stream().map(ModelJpa::getModelName).collect(Collectors.toList());
	}

	@Override
	public void save() {

	}

	@Override
	public void setCompanyData(RentCompanyData companyData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterable<String> getMostPopularsModel(int fromYear, int toYear) {
		long maxCount=records.getMaxCountModels(fromYear, toYear);
	//	List<String> res=records.getModelNamesPopular(fromYear, toYear, maxCount);
		return null;
	}

	
}
