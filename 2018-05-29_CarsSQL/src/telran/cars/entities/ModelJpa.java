package telran.cars.entities;

import java.util.List;

import javax.persistence.*;

@Entity(name="carmodels")
public class ModelJpa {
@Id
String modelName; 
int gasTank;
String 
company;
String country;
int priceDay;
@OneToMany(mappedBy="model")
List<CarJpa> cars;
public ModelJpa() {
}

public ModelJpa(String modelName, int gasTank, String company, String country, int priceDay) {
	this.modelName = modelName;
	this.gasTank = gasTank;
	this.company = company;
	this.country = country;
	this.priceDay = priceDay;
}

public int getPriceDay() {
	return priceDay;
}

public void setPriceDay(int priceDay) {
	this.priceDay = priceDay;
}

public String getModelName() {
	return modelName;
}

public int getGasTank() {
	return gasTank;
}

public String getCompany() {
	return company;
}

public String getCountry() {
	return country;
}

public List<CarJpa> getCars() {
	return cars;
}


}
