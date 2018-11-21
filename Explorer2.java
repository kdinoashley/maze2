import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class Explorer2 implements IRobotController {
    private IRobot robot;
    private boolean active = false;
    private int delay;
    // Data store for junctions
    private RobotData robotData;
    // 1 = explore, 0 = backtrack
    private int exploreMode;
    // initialise the variable direction
    public int direction = 0;
    // create an array containing 4 relative direction
    private int[] relativeDirection = {IRobot.RIGHT, IRobot.AHEAD, IRobot.LEFT, IRobot.BEHIND};

    // this method is called when start is pressed
    public void start() {
        this.active = true;
        // On the first move of the first run of a new maze
        if (this.robot.getRuns() == 0) {
            // initialise the data store
            this.robotData = new RobotData();
        }

        // determine which mode should be used and call corresponding methods
        while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
            determineMode();
            if (exploreMode == 1) {
                exploreControl();
            }
            else {
                backtracking();
                this.robotData.deleteJunction();
            }

            robot.advance();
        }

        if (delay > 0)
            robot.sleep(delay);

    }

    // this method determines which mode should be used
    // exploreMode is 1 if robot is in dead end or corridor or junction/crossrod with passage direction around
    // exploreMode is 0 if robot is in junction/crossroad with no passage around
    public void determineMode () {
        if ((nonwallExits() < 3) | (nonwallExits() > 2 && passageExits() > 0)) {
            exploreMode = 1;
        } else {
            exploreMode = 0;
        }
    }

    // this method is called when exploreMode is 1
    public void exploreControl() {
        // record and print junction/crossroad data when a new junction/crossroad is encountered
        if (beenbeforeExits() == 1 && nonwallExits() >2 ) {
            this.robotData.recordJunction(robot.getLocationX(), robot.getLocationY(), robot.getHeading());
            this.robotData.printJunction();
        }

        // call corresponding methods depending on the number of nonwallExits
        int exits = nonwallExits();
        switch (exits) {
            case 1:
                direction = deadEnd();
                break;
            case 2:
                direction = corridor();
                break;
            case 3:
                direction = junction();
                break;
            case 4:
                direction = crossRoad();
                break;
        }

        robot.face(direction);

    }

    // this method is called when exploreMode is 0
    // search the direction robot arrived from in encountered junctions/crossroads
    // face the opposite way to which it first entered the junction
    public void backtracking() {
        robot.face(convertDirection(reverseDirection(this.robotData.searchJunction(robot.getLocationX(), robot.getLocationY()))));
    }

    // this method calculates the number of exits of a certain type
    public int countExits (int type){
        int count= 0;
        for (int i = 0; i < 4; i++){
            if (robot.look(relativeDirection[i]) == type){
                count++;
            }
        }
        return count;
    }

    public int beenbeforeExits() {
        return countExits(IRobot.BEENBEFORE);
    }

    public int passageExits() {
        return countExits(IRobot.PASSAGE);
    }

    // the number of non-wall exits is the sum of been-before exits and passgae exits
    public int nonwallExits() {
        return beenbeforeExits() + passageExits();
    }

    // this method returns the opposite of a given direction
    public int reverseDirection(int x) {
        switch (x){
            case IRobot.NORTH:
                return IRobot.SOUTH;
            case IRobot.EAST:
                return IRobot.WEST;
            case IRobot.SOUTH:
                return IRobot.NORTH;
            case IRobot.WEST:
                return IRobot.EAST;
        }
        return -1;
    }

    // this method returns which relative direction robot should turn to reach a given absolute direction
    // the difference of the corresponding values of the directions of Heading and aim is how many 90 degrees robot should turn
    // +4 ensures it is always positive, remainder is same
    public int convertDirection(int x) {
        int y = (robot.getHeading() - x + 4 )%4;
        switch (y) {
            case 0:
                return IRobot.AHEAD;
            case 1:
                return IRobot.LEFT;
            case 2:
                return IRobot.BEHIND;
            case 3:
                return IRobot.RIGHT;
        }
        return -1;
    }

    // this method creates a new array that expands original array by 1 element
    // the first few elements of new array are the same as the original one's, add specificDirection to the last one
    private int[] extendArray(int[] originalArray, int specificDirection) {
        int[] result = new int[originalArray.length + 1];
        for (int i = 0; i < originalArray.length; i++) {
            result[i] = originalArray[i];
        }
        result[originalArray.length] = specificDirection;
        return result;
    }

    // check which direction doesn't have wall
    // deadends and corridors are basically the same (except that we can't face behind in corridor situation)
    public int uniformForDeadendAndCorridor(int threshold) {
        for (int i = 0; i < threshold; i++) {
            if (robot.look(relativeDirection[i]) != IRobot.WALL) {
                return relativeDirection[i];
            }
        }
        return -1;
    }

    public int deadEnd(){
        return uniformForDeadendAndCorridor(4);
    }

    public int corridor(){
       return uniformForDeadendAndCorridor(3);
    }

    // junctions and crossroads are basically the same
    // check if each direction has a passage exit, if one does, add the direction to the array
    private int uniformForJunctionAndCrossRoad() {
        int[] explorerDirection = new int[0];
        for (int i = 0; i < 4; i++) {
            if (robot.look(relativeDirection[i]) == IRobot.PASSAGE) {
                explorerDirection = extendArray(explorerDirection, relativeDirection[i]);
            }
        }

        int index = (int)(Math.random() * explorerDirection.length);
        return explorerDirection[index];
    }


    public int junction() {
        return uniformForJunctionAndCrossRoad();
    }


    public int crossRoad() {
        return uniformForJunctionAndCrossRoad();
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
        this.robotData.resetJunctionCounter();
        exploreMode = 1;
    }

    public void setRobot(IRobot robot) {
        this.robot = robot;
    }

}