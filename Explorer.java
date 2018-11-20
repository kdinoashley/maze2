import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class Explorer implements IRobotController {
    private IRobot robot;
    private boolean active = false;
    private int delay;
    // Data store for junctions
    private RobotData robotData;
    // 1 = explore, 0 = backtrack
    private int exploreMode;
    // initialise the variable direction
    public int direction = 0;
    // creat an array containing 4 relative direction
    private int[] relativeDirection = {IRobot.RIGHT, IRobot.AHEAD, IRobot.LEFT, IRobot.BEHIND};

    // this method is called when start is pressed
    public void start() {
        this.active = true;
        // On the first move of the first run of a new maze
        if (this.robot.getRuns() == 0) {
            // initialise the data store
            this.robotData = new RobotData();
        }

        while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
            // determine which mode should be used and call corresponding methods
            determineMode();
            if (exploreMode == 1) {
                exploreControl();
            }
            else {
                backtracking();
            }
        }

        if (delay > 0)
            robot.sleep(delay);

    }

    // this method determines which mode should be used
    public void determineMode () {
        // exploreMode is 1 if robot is in dead end or corridor or junction/crossrod with passage direction around
        if ((nonwallExits() < 3) | (nonwallExits() > 2 && passageExits() > 0)) {
            exploreMode = 1;
        } else {
            // exploreMode is 0 if robot is in junction/crossroad with no passage around
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

        // store the result of nonwallExits method
        int exits = nonwallExits();
        // call corresponding methods depending on the number of nonwallExits
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

        // face the direction and move forward
        robot.face(direction);
        robot.advance();

    }

    // this method is called when exploreMode is 0
    public void backtracking() {
        // search the direction robot arrived from in certain junctions/crossroads
        // exit the junction the opposite way to which it first entered the junction
        robot.face(convertDirection(reverseDirection(this.robotData.searchJunction(robot.getLocationX(), robot.getLocationY()))));
        robot.advance();
    }

    // this method calculates how many non-wall exits are there
    public int nonwallExits() {
        int count= 0;
        for (int i = 0; i < 4; i++){
            // look at each direction and increase count by 1 if it's not wall
            if (robot.look(relativeDirection[i]) != IRobot.WALL){
                count++;
            }
        }
        return count;
    }

    // similarly, this mathod calculates how many been-before exits are there
    public int beenbeforeExits() {
        int count= 0;
        for (int i = 0; i < 4; i++){
            if (robot.look(relativeDirection[i]) == IRobot.BEENBEFORE){
                count++;
            }
        }
        return count;
    }

    // similarly, this method calculates how many passage exits are there
    public int passageExits() {
        int count= 0;
        for (int i = 0; i < 4; i++){
            if (robot.look(relativeDirection[i]) == IRobot.PASSAGE){
                count++;
            }
        }
        return count;
    }

    // this method returns the opposite of a given direction
    // 1000 NORTH, 1001 EAST, 1002 SOUTH, 1003 WEST
    public int reverseDirection(int x) {
        switch (x){
            case 1000:
                return 1002;
            case 1001:
                return 1003;
            case 1002:
                return 1000;
            case 1003:
                return 1001;
        }
        return -1;
    }

    // this method returns which relative direction robot shoudl turn to reach a given absolute direction
    public int convertDirection(int x) {
        // the difference of the corresponding values of the directions of Heading and aim is how many 90 degrees robot should turn
        // +4 ensures it is always positive, remainder is same
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
    private int[] extendArray(int[] originalArray, int specificDirection) {
        // create a new array that has one more element than the original one
        int[] result = new int[originalArray.length + 1];
        // the first few elements of new array are the same as the original one's
        for (int i = 0; i < originalArray.length; i++) {
            result[i] = originalArray[i];
        }
        // add specificDirection to the last one
        result[originalArray.length] = specificDirection;
        return result;
    }

    // when robot is in dead end, look for the direction that doesn't have wall (only 1)
    public int deadEnd(){
        for (int i = 0; i < 4; i++) {
            if (robot.look(relativeDirection[i]) != IRobot.WALL) {
                return relativeDirection[i];
            }
        }
        return-1;
    }

    // when robot is in corridor (corner), look for the direction (except behind) that doesn't have wall (only 1)
    public int corridor(){
        for (int i = 0; i < 3; i++) {
            if (robot.look(relativeDirection[i]) != IRobot.WALL) {
                return relativeDirection[i];
            }
        }
        return -1;
    }

    // junctions and crossroads are basically the same
    private int uniformForJunctionAndCrossRoad() {
        int[] explorerDirection = new int[0];
        // check if four directions meet the requirements below, if one does, add it in to the array
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
