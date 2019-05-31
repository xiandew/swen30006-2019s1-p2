package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
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
	
	/**
	 * explore the map and return the coordinate of the nearest unviewed road tile
	 * @param curr current position of the car
	 * @param unviewedTiles
	 * @return
	 */
	public Coordinate explore(Coordinate curr, HashMap<Coordinate, MapTile> unviewedTiles) {
		HashMap<Coordinate, Float> distanceDict = new HashMap<>();
		
		for (Coordinate coor : unviewedTiles.keySet()) {
			MapTile tile = unviewedTiles.get(coor);
			if (tile.isType(MapTile.Type.WALL) || tile.isType(MapTile.Type.EMPTY)) {
				continue;
			}
			// float d = (float) Math.sqrt(Math.pow(coor.x - curr.x, 2) + Math.pow(coor.y - curr.y, 2));
			float d = Math.abs(coor.x - curr.x) + Math.abs(coor.y - curr.y);
			distanceDict.put(coor, d);
		}
		
		ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) distanceDict.entrySet().stream().sorted(Entry.comparingByValue()).map(e -> e.getKey()).collect(Collectors.toList());
		
		
		// get all coordinates which give the same shortest distance 
		Coordinate dest = null;
		for (Coordinate coor : coordinates) {
			if (getNextMove(curr, coor, World.getMap()) != null) {
				dest = coor;
				break;
			}
		}
		final float shortestDist = distanceDict.get(dest);
		ArrayList<Coordinate> tiedDests = (ArrayList<Coordinate>) coordinates.stream()
				.filter(coor -> shortestDist == distanceDict.get(coor))
				.collect(Collectors.toList());
		
		// sort the tied destinations by the number of unviewed tiles that they
		// can give us when reach there
		tiedDests.sort((a, b) -> getNumUnviewedTiles(b, unviewedTiles) - getNumUnviewedTiles(a, unviewedTiles));
		Coordinate moveTo = null;
		for (Coordinate coor : tiedDests) {
			moveTo = getNextMove(curr, coor, World.getMap());
			if (moveTo != null) {
				break;
			}
		}
		
		return moveTo;
	}
	
	private int getNumUnviewedTiles(Coordinate coor, HashMap<Coordinate, MapTile> unviewedTiles) {
		int n = 0;
		for (int x = coor.x - Car.VIEW_SQUARE; x < coor.x + Car.VIEW_SQUARE; x ++) {
			for (int y = coor.y - Car.VIEW_SQUARE; y < coor.y + Car.VIEW_SQUARE; y++) {
				Coordinate newCoor = new Coordinate(x, y);
				if (unviewedTiles.containsKey(newCoor) && !unviewedTiles.get(newCoor).isType(MapTile.Type.WALL)) {
					n++;
				}
			}
		}
		return n;
	}
	
	public Coordinate getNextMove(Coordinate start, HashMap<Coordinate, MapTile> map) {
		return getNextMove(start, new ArrayList<>(), map);
	}
	
	public Coordinate getNextMove(Coordinate start, Coordinate goal, HashMap<Coordinate, MapTile> map) {
		return getNextMove(start, new ArrayList<>(Arrays.asList(new Coordinate[] { goal })), map);
	}

	/**
	 *  breadth firsts search for a shortest path to a goal and return the first
	 *  coordinate of the path
	 * @param start current location of the car
	 * @param goal can be either a coordinate, parcels or exit
	 * @param map the map to be searched
	 * @return
	 */
	public Coordinate getNextMove(Coordinate start, ArrayList<Coordinate> goals, HashMap<Coordinate, MapTile> map) {

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

			if (goalTest(currTile, currPosition, goals)) {
				// backtrackPath(cur);
				Coordinate nextPosition = currPosition;

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
	
	/**
	 * test if current location reaches the goal
	 * @param currTile
	 * @param currPosition
	 * @param goal
	 * @return
	 */
	public boolean goalTest(MapTile currTile, Coordinate currPosition, ArrayList<Coordinate> goals) {
		
		// for explore purpose
		if (goals != null && goals.contains(currPosition)) {
			return true;
		}
		
		return utilityTest(currTile);
	}

	public abstract boolean utilityTest(MapTile currTile);
}
