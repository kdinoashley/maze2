import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class RobotData {
    private IRobot robot;
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


    public String convertHeading(int heading){
        switch(heading){
            case 1000:
                return "NORTH";
            case 1001:
                return "EAST";
            case 1002:
                return "SOUTH";
            case 1003:
                return "WEST";
        }
        return "0";
    }

    public void printJunction(){
        System.out.println("Junction " + this.junctionCounter + " (x = " + this.juncX[this.junctionCounter-1] + " y = " + this.juncY[this.junctionCounter-1] + ")  heading " + convertHeading(this.arrived[this.junctionCounter-1]));
    }

    public int searchJunction(int x, int y) {
        for(int i = 0; i < this.junctionCounter; i++){
            if (this.juncX[i] == x && this.juncY[i] == y) {
                return this.arrived[i];
            }
        }
        return -1;
    }

    public void resetJunctionCounter() {
        this.junctionCounter = 0;
    }



}
