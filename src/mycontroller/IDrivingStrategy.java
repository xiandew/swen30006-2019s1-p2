package mycontroller;

import controller.CarController;

public interface IDrivingStrategy {
	// explore the map before anything is found
	public void explore(CarController controller);
	// find a desired path to a target
	public void pathing(CarController controller, String target);
	public void update(CarController facade);
}
