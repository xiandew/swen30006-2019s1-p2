package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import swen30006.driving.Simulation;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAutoController extends CarController{
	
	// How many minimum units the wall or lava is away from the player.
	private int hazardSensitivity = Car.VIEW_SQUARE;
	
	// This is set to true when the car can see a wall or lava aside.
	protected boolean isHazardAside = false;
	
	// Car Speed to move at
	protected final int CAR_MAX_SPEED = 1;
	
	protected HashMap<Coordinate, MapTile> viewedTiles;
	
	private IDrivingStrategy drivingStrategy;
	
	public MyAutoController(Car car) {
		super(car);
		viewedTiles = new HashMap<>();
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
	public boolean checkAhead(MapTile.Type toCheck, WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch(orientation){
		case EAST:
			return checkEast(toCheck, currentView);
		case NORTH:
			return checkNorth(toCheck, currentView);
		case SOUTH:
			return checkSouth(toCheck, currentView);
		case WEST:
			return checkWest(toCheck, currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
		case EAST:
			return checkNorth(MapTile.Type.WALL, currentView);
		case NORTH:
			return checkWest(MapTile.Type.WALL, currentView);
		case SOUTH:
			return checkEast(MapTile.Type.WALL, currentView);
		case WEST:
			return checkSouth(MapTile.Type.WALL, currentView);
		default:
			return false;
		}	
	}
	
	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(MapTile.Type toCheck, HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= hazardSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(toCheck)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(MapTile.Type toCheck, HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= hazardSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(toCheck)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(MapTile.Type toCheck, HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= hazardSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(toCheck)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(MapTile.Type toCheck, HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= hazardSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(toCheck)){
				return true;
			}
		}
		return false;
	}

	public boolean checkMoreSpace(WorldSpatial.RelativeDirection d) {
		
	}
}
