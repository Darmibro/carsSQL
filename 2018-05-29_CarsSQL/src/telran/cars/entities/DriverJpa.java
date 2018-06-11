package telran.cars.entities;

import java.util.List;

import javax.persistence.*;

import telran.cars.dto.Driver;

@Entity()
@Table(name="drivers")
public class DriverJpa {
	@Id
	long licenseId;
	String name;
	int birthYear;
	String phone;
	@OneToMany(mappedBy = "driver")
	List<RecordJpa> records;

	public DriverJpa() {
	}

	public DriverJpa(long licenseId, String name, int birthYear, String phone) {
		this.licenseId = licenseId;
		this.name = name;
		this.birthYear = birthYear;
		this.phone = phone;
	}

	public Driver getDriverDto() {
		return new Driver(licenseId, name, birthYear, phone);
	}

	public long getLicenseId() {
		return licenseId;
	}

	public String getName() {
		return name;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public String getPhone() {
		return phone;
	}

	public List<RecordJpa> getRecords() {
		return records;
	}

}
