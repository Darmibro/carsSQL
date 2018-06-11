package telran.cars.entities;

import java.util.List;

import javax.persistence.*;

import telran.cars.dto.Car;
import telran.cars.dto.State;

@Entity
@Table(name = "cars")
public class CarJpa {
	@Id
	String regNumber;
	String color;
	@Enumerated(EnumType.STRING)
	State state;
	boolean inUse;
	boolean flRemoved;
	@ManyToOne
	ModelJpa model;
	@OneToMany(mappedBy = "car", cascade=CascadeType.REMOVE)
	List<RecordJpa> records;

	
	public CarJpa() {
	}

	public CarJpa(String regNumber, String color, State state, boolean inUse, boolean flRemoved, ModelJpa model) {
		this.regNumber = regNumber;
		this.color = color;
		this.state = state;
		this.inUse = inUse;
		this.flRemoved = flRemoved;
		this.model = model;
	}
	
	public Car getCar() {
		  return new Car(regNumber, color, model.modelName);
		 }

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public boolean isFlRemoved() {
		return flRemoved;
	}

	public void setFlRemoved(boolean flRemoved) {
		this.flRemoved = flRemoved;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public String getColor() {
		return color;
	}

	public ModelJpa getModel() {
		return model;
	}
	
	public List<RecordJpa> getRecords() {
		return records;
	}


}
