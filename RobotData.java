import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class RobotData {
    // Maximum number of junctions likely to occur in a given maze
    private static int maxJunctions = 10000;
    // Number of junctions stored so far
    private static int junctionCounter;
    // X-coordinates of the junctions
    private int[] juncX;
    // Y-coordinates of the junctions
    private int[] juncY;
    // Headings the robot first arrived from
    private int[] arrived;

    public RobotData(){
        this.maxJunctions = 10000;
        this.junctionCounter = 0;
        this.juncX = new int[this.maxJunctions];
        this.juncY = new int[this.maxJunctions];
        this.arrived = new int[this.maxJunctions];
    }

    public void recordJunction(int x, int y, int heading){
        this.juncX[this.junctionCounter] = x;
        this.juncY[this.junctionCounter] = y;
        this.arrived[this.junctionCounter] = heading;
        this.junctionCounter++;
    }

    public void printJunction(){
        System.out.println("Junction " + this.junctionCounter + " (x = " + this.juncX[this.junctionCounter] + " y = " + this.juncY[this.junctionCounter] + ")  heading " + this.arrived[this.junctionCounter]);
    }

    public void resetJunctionCounter() {
        this.junctionCounter = 0;
    }



}
