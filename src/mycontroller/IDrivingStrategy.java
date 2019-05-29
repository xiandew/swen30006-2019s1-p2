package mycontroller;

public interface IDrivingStrategy {
	// explore the map before anything is found
	public void explore(MyAutoController controller);
	// find a desired path to a target
	public void pathing(MyAutoController controller);
	public void update(MyAutoController controller);
}
