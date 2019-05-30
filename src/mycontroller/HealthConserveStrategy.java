package mycontroller;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;

public class HealthConserveStrategy extends DrivingStrategy {
	public static final float LOW_HEALTH = 50;

	public HealthConserveStrategy(CarController controller) {
		super(controller);
	}

	public boolean utilityTest(MapTile currTile) {
		boolean hasEnoughParcels = controller.numParcelsFound() >= controller.numParcels();

		String trap = currTile.isType(MapTile.Type.TRAP) ? ((TrapTile) currTile).getTrap() : "";

		// when the search path reaches our goal
		if (( hasEnoughParcels && currTile.isType(MapTile.Type.FINISH)) ||
			(!hasEnoughParcels && trap.equals("parcel"))) {
			return true;
		}

		// when the search path reaches the red buffer and we need it
		if (controller.getHealth() <= LOW_HEALTH && (trap.equals("water") || trap.equals("health"))) {
			return true;
		}

		return false;
	}
}
