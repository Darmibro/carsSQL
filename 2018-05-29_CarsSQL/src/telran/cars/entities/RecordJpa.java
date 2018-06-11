package telran.cars.entities;

import java.time.LocalDate;

import javax.persistence.*;

import telran.cars.dto.RentRecord;

@Entity(name = "carrecords")
public class RecordJpa {
	@Id
	@GeneratedValue
	int id;
	@ManyToOne
	DriverJpa driver;
	@ManyToOne
	CarJpa car;
	LocalDate rentDate;
	LocalDate returnDate;
	int gasTankPercent;
	int rentDays;
	float cost;
	int damages;

	public RecordJpa() {
	}

	public RecordJpa(DriverJpa driver, CarJpa car, LocalDate rentDate, int rentDays) {
		this.driver = driver;
		this.car = car;
		this.rentDate = rentDate;
		this.rentDays = rentDays;
	}
	public RentRecord getRentRecord() {
		return new RentRecord(driver.getLicenseId(), car.regNumber, rentDate, rentDays);
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public int getGasTankPercent() {
		return gasTankPercent;
	}

	public void setGasTankPercent(int gasTankPercent) {
		this.gasTankPercent = gasTankPercent;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public int getDamages() {
		return damages;
	}

	public void setDamages(int damages) {
		this.damages = damages;
	}

	public int getId() {
		return id;
	}

	public DriverJpa getDriver() {
		return driver;
	}

	public CarJpa getCar() {
		return car;
	}

	public LocalDate getRentDate() {
		return rentDate;
	}

	public int getRentDays() {
		return rentDays;
	}
	
}
