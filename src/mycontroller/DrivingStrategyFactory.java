package mycontroller;

import controller.CarController;
import swen30006.driving.Simulation.StrategyMode;

public class DrivingStrategyFactory {
	
	private static DrivingStrategyFactory drvingStrategyFactory = new DrivingStrategyFactory();

	private DrivingStrategyFactory() {
		
	}
	
	public static DrivingStrategyFactory getInstance() {
		return drvingStrategyFactory;
	}
	
	public DrivingStrategy getDrivingStrategy(StrategyMode toConserve, CarController controller) {
		switch(toConserve) {
		case FUEL:
			return new FuelConserveStrategy(controller);
		case HEALTH:
			return new HealthConserveStrategy(controller);
		default:
			return null;
		}
	}
}
