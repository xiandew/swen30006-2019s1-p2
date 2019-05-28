package mycontroller;

import world.Car;

public interface IDrivingStrategy {

	public void update(Car car, MyAutoController controller);
}
