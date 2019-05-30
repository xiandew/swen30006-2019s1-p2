package mycontroller;

import java.util.HashMap;

import controller.CarController;
import swen30006.driving.Simulation;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;
import world.WorldSpatial.Direction;
import world.WorldSpatial.RelativeDirection;

public class MyAutoController extends CarController {

	private HashMap<Coordinate, MapTile> viewedTiles;
	
	private HashMap<Coordinate, MapTile> unviewedTiles;

	// current coordinate
	private Coordinate currentCoor;

	// current view
	private HashMap<Coordinate, MapTile> currentView;

	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;

	// How many minimum units the wall or lava is away from the player.
	private int wallSensitivity = 1;

	private DrivingStrategy drivingStrategy;

	public MyAutoController(Car car) {
		super(car);
		viewedTiles = new HashMap<>();
		unviewedTiles = new HashMap<>(World.getMap());
		drivingStrategy = DrivingStrategyFactory.getInstance().getDrivingStrategy(Simulation.toConserve(), this);
	}

	@Override
	public void update() {
		currentCoor = new Coordinate(getPosition());
		currentView = getView();
		viewedTiles.putAll(currentView);
		for (Coordinate coor : currentView.keySet()) {
			if (unviewedTiles.containsKey(coor)) {
				unviewedTiles.remove(coor);
			}
		}

		if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
			applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}

		// try to move to a goal, which can be either health, parcel or exit
		Coordinate moveTo = drivingStrategy.getNextPurposiveMove(currentCoor, viewedTiles);

		if (moveTo == null) {
			moveTo = drivingStrategy.explore(currentCoor, unviewedTiles);
		}
		
		if (!moveTo.equals(currentCoor)) {
			direct(moveTo);
		}
	}

	private void direct(Coordinate moveTo) {

		Coordinate directionDelta = new Coordinate(moveTo.x - currentCoor.x, moveTo.y - currentCoor.y);
		Direction turnDirection = drivingStrategy.direction(directionDelta);

		Direction orientation = getOrientation();

		float rotateAngle = WorldSpatial.rotation(turnDirection) - WorldSpatial.rotation(orientation);

		if ((rotateAngle + 360) % 360 == 90) {
			turnLeft();
		} else if ((rotateAngle - 360) % 360 == -90) {
			turnRight();
		} else if (Math.abs(rotateAngle) == 180) {
			if (!checkWall(RelativeDirection.RIGHT)) {
				turnRight();
			} else if (!checkWall(RelativeDirection.LEFT)) {
				turnLeft();
			} else {
				applyBrake();
				applyReverseAcceleration();
			}
		}
	}

	/**
	 * Check if there is a wall in the given direction
	 * 
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	public boolean checkWall(Direction d) {
		Coordinate dd = drivingStrategy.directionDelta(d);
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentCoor.x + i * dd.x, currentCoor.y + i * dd.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the wall is on the given side
	 * 
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	public boolean checkWall(RelativeDirection r) {
		Direction d = WorldSpatial.changeDirection(getOrientation(), r);
		return checkWall(d);
	}
}
