package mycontroller;

import swen30006.driving.Simulation.StrategyMode;

public class DrivingStrategyFactory {
	
	private static DrivingStrategyFactory drvingStrategyFactory = new DrivingStrategyFactory();

	private DrivingStrategyFactory() {
		
	}
	
	public static DrivingStrategyFactory getInstance() {
		return drvingStrategyFactory;
	}
	
	public IDrivingStrategy getDrivingStrategy(StrategyMode toConserve) {
		switch(toConserve) {
		case FUEL:
			return new FuelConserveStrategy();
		case HEALTH:
			return new HealthConserveStrategy();
		default:
			return null;
		}
	}
}
