package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

public abstract class DrivingStrategy {
	private Coordinate[] directionDeltas = new Coordinate[] { new Coordinate(0, 1), new Coordinate(1, 0),
			new Coordinate(0, -1), new Coordinate(-1, 0) };
	private Direction[] directions = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH,
			Direction.WEST };

	public Coordinate directionDelta(Direction d) {
		return directionDeltas[Arrays.asList(directions).indexOf(d)];
	}

	public Direction direction(Coordinate directionDelta) {
		return directions[Arrays.asList(directionDeltas).indexOf(directionDelta)];
	}

	public Coordinate explore() {
		return null;

//		// Need speed to turn and progress toward the exit
//		if (getSpeed() == 0) {
//			// Tough luck if there's a wall in the way
//			applyForwardAcceleration();
//		}
//		
//		if (checkMoreSpace(getOrientation())) {
//			if (!checkHazard(hazards, currentView)) {
//				return;
//			}
//		}
//		
//		if (checkMoreSpace(RelativeDirection.RIGHT)) {
//			// turn right
//			if (!checkHazard(RelativeDirection.RIGHT, hazards, currentView)) {
//				turnRight();
//				return;
//			}
//		}
//		
//		if (myAutoController.checkMoreSpace(RelativeDirection.LEFT)) {
//			// turn left
//			if (!myAutoController.checkHazard(RelativeDirection.LEFT, hazards, currentView)) {
//				myAutoController.turnLeft();
//				return;
//			}
//		}
//		
//		// back the car
//		myAutoController.viewedTiles.clear();
//		myAutoController.applyBrake();
//		myAutoController.applyReverseAcceleration();
	}

	public Coordinate getNextMove(Coordinate start, HashMap<Coordinate, MapTile> viewedTiles) {

		HashMap<Coordinate, Coordinate> cameFrom = new HashMap<>();
		LinkedList<Coordinate> nextToVisit = new LinkedList<>();
		ArrayList<Coordinate> explored = new ArrayList<>();

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

			if (utilityTest(curTile)) {
				// backtrackPath(cur);
				Coordinate next = cur;

				System.out.println("dest: " + cur.toString());
				while (!cur.equals(start)) {
					next = cur;
					cur = cameFrom.get(cur);
				}

				return next;
			}

			for (Coordinate dd : directionDeltas) {
				Coordinate coordinate = new Coordinate(cur.x + dd.x, cur.y + dd.y);

				if (explored.contains(coordinate)) {
					continue;
				}
				cameFrom.put(coordinate, cur);
				nextToVisit.add(coordinate);

			}
			explored.add(cur);
		}
		return null;
	}

	public abstract boolean utilityTest(MapTile curTile);
}
