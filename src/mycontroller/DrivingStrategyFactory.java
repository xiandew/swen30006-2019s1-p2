package mycontroller;

import swen30006.driving.Simulation;

public class DrivingStrategyFactory {
	
	private DrivingStrategyFactory strategyAdapterFac = new DrivingStrategyFactory();

	private DrivingStrategyFactory() {
		
	}
	
	public DrivingStrategyFactory getInstance() {
		return strategyAdapterFac;
	}
	
	public IDrivingStrategy getConserveStrategy(Simulation.StrategyMode mode) {
		return new ConserveStrategy(mode);
	}
	
	public IDrivingStrategy getExploreStrategy(Simulation.StrategyMode mode) {
		return new ExploreStrategy(mode);
	}
}
