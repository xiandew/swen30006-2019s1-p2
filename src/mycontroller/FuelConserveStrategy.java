package mycontroller;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;

public class FuelConserveStrategy extends DrivingStrategy {

	public FuelConserveStrategy(CarController controller) {
		super(controller);
	}

	public boolean utilityTest(MapTile currTile) {
		boolean hasEnoughParcels = controller.numParcelsFound() >= controller.numParcels();
		
		String trap = currTile.isType(MapTile.Type.TRAP) ? ((TrapTile) currTile).getTrap() : "";
		
		if (( hasEnoughParcels && currTile.isType(MapTile.Type.FINISH)) ||
			(!hasEnoughParcels && trap.equals("parcel"))) {
			return true;
		}

		return false;
	}
}
