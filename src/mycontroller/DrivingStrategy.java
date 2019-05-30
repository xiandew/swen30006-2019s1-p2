package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial.Direction;

public abstract class DrivingStrategy {
	protected CarController controller;
	
	private Coordinate[] directionDeltas = new Coordinate[] { new Coordinate(0, 1), new Coordinate(1, 0),
			new Coordinate(0, -1), new Coordinate(-1, 0) };
	private Direction[] directions = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH,
			Direction.WEST };

	
	public DrivingStrategy(CarController controller) {
		this.controller = controller;
	}
	
	
	public Coordinate directionDelta(Direction d) {
		return directionDeltas[Arrays.asList(directions).indexOf(d)];
	}

	public Direction direction(Coordinate directionDelta) {
		return directions[Arrays.asList(directionDeltas).indexOf(directionDelta)];
	}

	public Coordinate explore(Coordinate curr, HashMap<Coordinate, MapTile> unviewedTiles) {
		// the farthest road tile that the car can reach in the current view
		Coordinate nearest = null;
		// the distance to the farthest road tile
		float nearestDistance = 0;
		
		for (Coordinate coor : unviewedTiles.keySet()) {
			MapTile tile = unviewedTiles.get(coor);
			if (tile.isType(MapTile.Type.WALL)) {
				continue;
			}
			float d = (float) Math.sqrt(Math.pow(coor.x - curr.x, 2) + Math.pow(coor.y - curr.y, 2));
			if (nearest == null || d < nearestDistance) {
				nearestDistance = d;
				nearest = coor;
			}
		}
		
		return getNextPurposiveMove(curr, nearest, World.getMap());
	}
	
	public Coordinate getNextPurposiveMove(Coordinate start, HashMap<Coordinate, MapTile> map) {
		return getNextPurposiveMove(start, null, map);
	}

	public Coordinate getNextPurposiveMove(Coordinate start, Coordinate goal, HashMap<Coordinate, MapTile> map) {

		HashMap<Coordinate, Coordinate> cameFrom = new HashMap<>();
		LinkedList<Coordinate> nextToVisit = new LinkedList<>();
		ArrayList<Coordinate> explored = new ArrayList<>();

		nextToVisit.add(start);

		while (!nextToVisit.isEmpty()) {
			Coordinate currPosition = nextToVisit.remove();
			MapTile currTile = map.get(currPosition);

			if (currTile == null || currTile.isType(MapTile.Type.EMPTY) || explored.contains(currPosition)) {
				continue;
			}

			if (currTile.isType(MapTile.Type.WALL)) {
				explored.add(currPosition);
				continue;
			}

			if (goalTest(currTile, currPosition, goal)) {
				// backtrackPath(cur);
				Coordinate nextPosition = currPosition;

				System.out.println("dest: " + currPosition.toString());
				while (!currPosition.equals(start)) {
					nextPosition = currPosition;
					currPosition = cameFrom.get(currPosition);
				}

				return nextPosition;
			}

			for (Coordinate dd : directionDeltas) {
				Coordinate coordinate = new Coordinate(currPosition.x + dd.x, currPosition.y + dd.y);

				if (explored.contains(coordinate)) {
					continue;
				}
				cameFrom.put(coordinate, currPosition);
				nextToVisit.add(coordinate);

			}
			explored.add(currPosition);
		}
		
		return null;
	}
	
	public boolean goalTest(MapTile currTile, Coordinate currPosition, Coordinate goal) {
		if (goal != null && currPosition.equals(goal)) {
			return true;
		}
		
		return utilityTest(currTile);
	}

	public abstract boolean utilityTest(MapTile currTile);
}
