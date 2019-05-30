package mycontroller;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;

public class FuelConserveStrategy extends DrivingStrategy {
	private CarController controller;

	public FuelConserveStrategy(CarController controller) {
		this.controller = controller;
	}

	public boolean utilityTest(MapTile curTile) {
		boolean hasEnoughParcels = controller.numParcelsFound() >= controller.numParcels();
		return
				( hasEnoughParcels && curTile.isType(MapTile.Type.FINISH)) ||
				(!hasEnoughParcels && curTile.isType(MapTile.Type.TRAP)
						&& ((TrapTile) curTile).getTrap().equals("parcel"));
	}

//	@Override
//	public void update(CarController controller) {
//		MyAutoController myAutoController = ((MyAutoController)controller);
//		currentView = myAutoController.getView();
//		myAutoController.viewedTiles.putAll(currentView);
//		if (myAutoController.numParcelsFound() < myAutoController.numParcels()) {
//			if (!myAutoController.viewedParcels()) {
//				explore(myAutoController);
//			} else {
//				pathing(myAutoController, "parcel");
//			}
//		} else {
//			pathing(myAutoController, "exit");
//		}
//		
//		if (myAutoController.tempPath.size() == 0) {
//			explore(myAutoController);
//		} else {
//			
//		}
//	}
}
