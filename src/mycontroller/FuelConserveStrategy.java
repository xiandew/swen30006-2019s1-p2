package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.RelativeDirection;

public class FuelConserveStrategy implements IDrivingStrategy {
	private HashMap<Coordinate, MapTile> currentView;

	public FuelConserveStrategy() {
		
	}

	@Override
	public void explore(MyAutoController controller) {
		currentView = controller.getView();
		controller.viewedTiles.putAll(currentView);
		
		// Need speed to turn and progress toward the exit
		if (controller.getSpeed() < controller.CAR_MAX_SPEED) {
			// Tough luck if there's a wall in the way
			controller.applyForwardAcceleration();
		}
		
		if (controller.isHazardAside) {
			// If wall no longer on left, turn left
			if(!controller.checkFollowingWall(controller.getOrientation(), currentView)) {
				controller.turnLeft();
			} else {
				// If wall on left and wall straight ahead, turn right
				if(controller.checkAhead(MapTile.Type.WALL, controller.getOrientation(), currentView)) {
					if (controller.checkMoreSpace(RelativeDirection.RIGHT)) {
						controller.turnRight();
					} else if (controller.checkMoreSpace(RelativeDirection.LEFT)) {
						controller.turnLeft();
					}
				}
			}
		} else {
			// Start wall-following (with wall on left) as soon as we see a wall straight ahead
			if(controller.checkAhead(MapTile.Type.WALL, controller.getOrientation(),currentView)) {
				controller.turnRight();
				controller.isHazardAside = true;
			}
		}
	}
	
	@Override
	public void pathing(MyAutoController controller) {
		
	}
	
	@Override
	public void update(MyAutoController controller) {
		currentView = controller.getView();
		controller.viewedTiles.putAll(currentView);
		if (!viewedParcels()) {
			explore(controller);
		} else {
			pathing(controller);
		}
	}

	private boolean viewedParcels() {
		
		return false;
	}
}
