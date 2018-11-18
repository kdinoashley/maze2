import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class Explorer implements IRobotController {
	private IRobot robot;
	private boolean active = false;
	private int delay;

	public int direction = 0;

	private int[] relativeDirection = {IRobot.RIGHT, IRobot.AHEAD, IRobot.LEFT, IRobot.BEHIND};
	private final int commandoThreshold = 3;
	// who are we?
	// commando!
	// what do we do?
	// not going back!
	// how?
	// set a threshold!
	// what's the mechanism behind that???
	// call me ``好哥哥'' and I'll tell you.

	public int nonwallExits() {
		int count= 0;
		for (int i = 0; i < 4; i++){
			if (robot.look(relativeDirection[i]) != IRobot.WALL){
				count++;
			}
		}
		return count;
	}

	public void start() {
		this.active = true;

		while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
			int exits = nonwallExits();
			switch(exits){
				case 1:
					direction = deadEnd();
				case 2:
					direction = corridor();
				case 3:
					direction = junction();
				case 4:
					direction = crossRoad();
			}

			robot.face(direction);
			robot.advance();

			if (delay > 0)
				robot.sleep(delay);
		}
	}

	private int[] extendArray(int[] originalArray, int specificDirection) {
		int[] result = new int[originalArray.length + 1];
		for (int i = 0; i < originalArray.length; i++) {
			result[i] = originalArray[i];
		}
		result[originalArray.length] = specificDirection;
		return result;
	}

	public int deadEnd(){
		for (int i = 0; i < relativeDirection.length; i++) {
			if (robot.look(relativeDirection[i]) != IRobot.WALL) {
				return relativeDirection[i];
			}
		}
	}

	public int corridor(){
		for (int i = 0; i < commandoThreshold; i++) {
			if (robot.look(relativeDirection[i]) != IRobot.WALL) {
				return relativeDirection[i];
			}
		}
	}

	private int uniformOfJunctionAndCrossRoad() {
		int[] noWalls = new int[0];
		int[] explorerDirection = new int[0];
		for (int i = 0; i < relativeDirection.length; i++) {
			if (robot.look(relativeDirection[i]) != IRobot.WALL) {
				noWalls = extendArray(noWalls, relativeDirection[i]);
				if (robot.look(relativeDirection[i]) == IRobot.PASSAGE) {
					explorerDirection = extendArray(explorerDirection, relativeDirection[i]);
				}
			}
		}

		if(explorerDirection.length == 0){
			explorerDirection = noWalls;
		}

		int index = (int)(Math.random() * explorerDirection.length);
		return explorerDirection[index];
	}

	// make them happy
	public int junction() {
		return uniformOfJunctionAndCrossRoad();
	}

	// ditto
	public int crossRoad() {
		return uniformOfJunctionAndCrossRoad();
	}

	public String getDescription() {
		return "A controller which explores the maze in a structured way";
	}

	public void setDelay(int millis) {
		delay = millis;
	}

	public int getDelay() {
		return delay;
	}

	public void reset() {
		active = false;
	}

	public void setRobot(IRobot robot) {
		this.robot = robot;
	}

}

