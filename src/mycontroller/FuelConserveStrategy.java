package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;
import world.WorldSpatial.RelativeDirection;

public class FuelConserveStrategy implements IDrivingStrategy {
	private HashMap<Coordinate, MapTile> currentView;
	private MapTile.Type[] hazards = new MapTile.Type[] {MapTile.Type.WALL};
	
	public FuelConserveStrategy() {
		
	}

	@Override
	public void explore(CarController controller) {
		MyAutoController myAutoController = ((MyAutoController)controller);
		
		currentView = myAutoController.getView();
		myAutoController.viewedTiles.putAll(currentView);
		
		// Need speed to turn and progress toward the exit
		if (myAutoController.getSpeed() == 0) {
			// Tough luck if there's a wall in the way
			myAutoController.applyForwardAcceleration();
		}
		
		if (myAutoController.checkMoreSpace(myAutoController.getOrientation())) {
			if (!myAutoController.checkHazard(hazards, currentView)) {
				return;
			}
		}
		
		if (myAutoController.checkMoreSpace(RelativeDirection.RIGHT)) {
			// turn right
			if (!myAutoController.checkHazard(RelativeDirection.RIGHT, hazards, currentView)) {
				myAutoController.turnRight();
				return;
			}
		}
		
		if (myAutoController.checkMoreSpace(RelativeDirection.LEFT)) {
			// turn left
			if (!myAutoController.checkHazard(RelativeDirection.LEFT, hazards, currentView)) {
				myAutoController.turnLeft();
				return;
			}
		}
		
		// back the car
		myAutoController.viewedTiles.clear();
		myAutoController.applyBrake();
		myAutoController.applyReverseAcceleration();
	}
	
	@Override
	public void pathing(CarController controller, String target) {
		MyAutoController myAutoController = ((MyAutoController)controller);
		HashMap<Coordinate, MapTile> viewedTiles = myAutoController.viewedTiles;
		HashMap<Coordinate, Coordinate> cameFrom = new HashMap<>();
		LinkedList<Coordinate> nextToVisit = new LinkedList<>();
		ArrayList<Coordinate> explored = new ArrayList<>();
		String[] startXY = myAutoController.getPosition().split(",");
	    Coordinate start = new Coordinate(Integer.parseInt(startXY[0]), Integer.parseInt(startXY[1]));
	    nextToVisit.add(start);
	 
	    while (!nextToVisit.isEmpty()) {
	        Coordinate cur = nextToVisit.remove();
	        MapTile curTile = viewedTiles.get(cur);
	 
	        if (curTile.isType(MapTile.Type.EMPTY) || explored.contains(cur)) {
	            continue;
	        }
	 
	        if (curTile.isType(MapTile.Type.WALL)) {
	        	explored.add(cur);
	            continue;
	        }
	 
	        if ((target.equals("exit") && curTile.isType(MapTile.Type.FINISH)) || 
	        	(target.equals("parcel") && ((TrapTile) curTile).getTrap().equals("parcel"))) {
	            // backtrackPath(cur);
	        	while (!cur.equals(start)) {
	        		myAutoController.tempPath.add(cur);
	        		cur = cameFrom.get(cur);
	        	}
	        	Collections.reverse(myAutoController.tempPath);
	            return;
	        }
	 
	        for (Coordinate dd : myAutoController.directionDeltas) {
	            Coordinate coordinate = new Coordinate(cur.x + dd.x, cur.y + dd.y);
	            cameFrom.put(coordinate, cur);
	            nextToVisit.add(coordinate);
	            explored.add(cur);
	        }
	    }
	}
	
	private void followPath(CarController controller) {
		MyAutoController myAutoController = ((MyAutoController)controller);
		Direction orientation = myAutoController.getOrientation();
		Coordinate next = ((MyAutoController)myAutoController).tempPath.remove(0);
		String[] currXY = myAutoController.getPosition().split(",");
	    Coordinate curr = new Coordinate(Integer.parseInt(currXY[0]), Integer.parseInt(currXY[1]));
	    Coordinate directionDelta = new Coordinate(next.x - curr.x, next.y - next.x);
	    Direction turnDirection = myAutoController.directions[Arrays.asList(myAutoController.directionDeltas).indexOf(directionDelta)];
	    float rotateAngle = WorldSpatial.rotation(turnDirection) - WorldSpatial.rotation(orientation);
	    if (rotateAngle == -90) {
	    	myAutoController.turnLeft();
	    } else if (rotateAngle == 90) {
	    	myAutoController.turnRight();
	    } else if (Math.abs(rotateAngle) == 180) {
	    	myAutoController.applyBrake();
	    	myAutoController.applyReverseAcceleration();
	    }
	}
	
	@Override
	public void update(CarController controller) {
		MyAutoController myAutoController = ((MyAutoController)controller);
		currentView = myAutoController.getView();
		myAutoController.viewedTiles.putAll(currentView);
		if (myAutoController.numParcelsFound() < myAutoController.numParcels()) {
			if (!myAutoController.viewedParcels()) {
				explore(myAutoController);
			} else {
				pathing(myAutoController, "parcel");
			}
		} else {
			pathing(myAutoController, "exit");
		}
		
		if (myAutoController.tempPath.size() == 0) {
			explore(myAutoController);
		} else {
			followPath(myAutoController);
		}
	}
}
