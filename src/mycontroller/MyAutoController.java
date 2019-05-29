package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import controller.CarController;
import swen30006.driving.Simulation;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;
import world.WorldSpatial.RelativeDirection;

public class MyAutoController extends CarController{
	
	// Car Speed to move at
	protected final int CAR_MAX_SPEED = 1;
	
	// How many minimum units the wall or lava is away from the player.
	protected int hazardSensitivity = 1;
	
	// This is set to true when the car can see a wall or lava aside.
	protected boolean isHazardAside = false;
	
	protected ArrayList<Coordinate> tempPath = new ArrayList<>();
	
	protected HashMap<Coordinate, MapTile> viewedTiles = new HashMap<>();
	
	private IDrivingStrategy drivingStrategy;
	
	
	protected Coordinate[] directionDeltas = new Coordinate[] {
			new Coordinate( 0,  1),
			new Coordinate( 1,  0),
			new Coordinate( 0, -1),
			new Coordinate(-1,  0)
	};
	protected Direction[] directions = new Direction[] {
			Direction.NORTH,
			Direction.EAST,
			Direction.SOUTH,
			Direction.WEST
	};
	
	public MyAutoController(Car car) {
		super(car);
		drivingStrategy = DrivingStrategyFactory.getInstance().getDrivingStrategy(Simulation.toConserve());
	}
	
	@Override
	public void update() {
		drivingStrategy.update(this);
	}
	
	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	public boolean checkHazard(MapTile.Type[] toCheck, HashMap<Coordinate, MapTile> currentView){
		Direction orientation = getOrientation();
		return checkHazard(orientation, toCheck, currentView);
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	public boolean checkHazard(RelativeDirection r, MapTile.Type[] toCheck, HashMap<Coordinate, MapTile> currentView) {
		Direction orientation = WorldSpatial.changeDirection(getOrientation(), r);
		return checkHazard(orientation, toCheck, currentView);
	}
	
	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkOrientation will check up to wallSensitivity amount of tiles to the given orientation
	 */
	public boolean checkHazard(Direction orientation, MapTile.Type[] toCheck, HashMap<Coordinate, MapTile> currentView){
		Coordinate dd = directionDelta(orientation);
		
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= hazardSensitivity; i++){
			MapTile tile =
					currentView.get(new Coordinate(currentPosition.x+i*dd.x, currentPosition.y+i*dd.y));
			for (MapTile.Type t : toCheck) {
				if(tile.isType(t)){
					return true;
				}
			}
		}
		return false;
	}

	// check if there is more space on the relative direction beyond current view point
	public boolean checkMoreSpace(RelativeDirection r) {
		return checkMoreSpace(WorldSpatial.changeDirection(getOrientation(), r));
	}
	
	public boolean checkMoreSpace(Direction d) {
		Coordinate dd = directionDelta(d);
		// North, East
		if (dd.x + dd.y == 1) {
			float[] viewRange = getUpperRange(viewedTiles);
			return
					(dd.x == 0 || normalise(mapWidth() - 1 - viewRange[0]) == dd.x) &&
					(dd.y == 0 || normalise(mapHeight() - 1 - viewRange[1]) == dd.y);
		}
		// South, West
		else {
			float[] viewRange = getLowerRange(viewedTiles);
			return
					(dd.x == 0 || normalise(0 - viewRange[0]) == dd.x) &&
					(dd.y == 0 || normalise(0 - viewRange[1]) == dd.y);
		}
	}
	
	private float[] getLowerRange(HashMap<Coordinate, MapTile> map) {
		float mx = mapWidth(), my = mapHeight();
		for (Coordinate c : map.keySet()) {
			if (!map.get(c).isType(MapTile.Type.EMPTY)) {
				if (c.x < mx) {
					mx = c.x;
				}
				if (c.y < my) {
					my = c.y;
				}
			}
		}
		return new float[] {mx, my};
	}

	private float[] getUpperRange(HashMap<Coordinate, MapTile> map) {
		float mx = 0, my = 0;
		for (Coordinate c : map.keySet()) {
			if (!map.get(c).isType(MapTile.Type.EMPTY)) {
				if (c.x > mx) {
					mx = c.x;
				}
				if (c.y > my) {
					my = c.y;
				}
			}
		}
		return new float[] {mx, my};
	}
	
	private float normalise(float n) {
		return n > 0 ? 1 : n < 0 ? -1 : 0;
	}


	public Coordinate directionDelta(Direction d) {
		return directionDeltas[Arrays.asList(directions).indexOf(d)];
	}
	
	protected boolean viewedParcels() {
//		for (MapTile t : controller.viewedTiles.values()) {
//			if (t.isType(MapTile.Type.TRAP) && ((TrapTile) t).getTrap().equals("parcel")) {
//				return true;
//			}
//		}
		return false;
	}
}
