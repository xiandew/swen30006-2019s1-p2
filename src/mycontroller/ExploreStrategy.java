package mycontroller;

import java.util.HashMap;

import swen30006.driving.Simulation;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public class ExploreStrategy implements IDrivingStrategy {

	private HashMap<Coordinate, MapTile> visitedTiles;
	
	public ExploreStrategy(Simulation.StrategyMode mode) {
		visitedTiles = new HashMap<>();
	}

	@Override
	public void update(Car car, MyAutoController controller) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = car.getView();
		
		// Need speed to turn and progress toward the exit
		if (car.getSpeed() < controller.CAR_MAX_SPEED) {
			// Tough luck if there's a wall in the way
			car.applyForwardAcceleration();
		}
		
		if (controller.isFollowingWall()) {
			// If wall no longer on left, turn left
			if(!controller.checkFollowingWall(controller.getOrientation(), currentView)) {
				car.turnLeft();
			} else {
				// If wall on left and wall straight ahead, turn right
				if(controller.checkWallAhead(controller.getOrientation(), currentView)) {
					car.turnRight();
				}
			}
		} else {
			// Start wall-following (with wall on left) as soon as we see a wall straight ahead
			if(controller.checkWallAhead(controller.getOrientation(),currentView)) {
				car.turnRight();
				controller.setFollowingWall(true);
			}
		}
		
	}
}
