package mycontroller;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;

public class HealthConserveStrategy extends DrivingStrategy {
	public static final float LOW_HEALTH = 100;

	private CarController controller;

	public HealthConserveStrategy(CarController controller) {
		this.controller = controller;
	}

	public boolean utilityTest(MapTile curTile) {
		boolean hasEnoughParcels = controller.numParcelsFound() >= controller.numParcels();

		String trap = curTile.isType(MapTile.Type.TRAP) ? ((TrapTile) curTile).getTrap() : "";

		// when the search path reaches our goal
		if (( hasEnoughParcels && curTile.isType(MapTile.Type.FINISH)) ||
			(!hasEnoughParcels && curTile.isType(MapTile.Type.TRAP) && (trap.equals("parcel")))) {
			return true;
		}

		// when the search path reaches the red buffer and we need it
		if (controller.getHealth() <= LOW_HEALTH && (trap.equals("water") || trap.equals("health"))) {
			return true;
		}

		return false;
	}
}
