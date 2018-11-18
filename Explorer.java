import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class Explorer implements IRobotController {
    // the robot in the maze
    private IRobot robot;
    // a flag to indicate whether we are looking for a path
    private boolean active = false;
    // a value (in ms) indicating how long we should wait
    // between moves
    private int delay;

    public int direction = 0;

    // generate an array containing 4 relative directions
    private int[] relativeDirection = {IRobot.AHEAD, IRobot.RIGHT, IRobot.BEHIND, IRobot.LEFT};

    // returns a number indicating how many non-wall exits there
    // are surrounding the robot's current position
    public int nonwallExits() {
        // TODO: implement
        int results=0;
        for(int i=0;i<4;i++){
            if(robot.look(relativeDirection[i])!=IRobot.WALL){
                results++;
            }
        }
        return results;
    }

    // this method is called when the "start" button is clicked
    // in the user interface
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

            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
        }
    }

    // this method creates a new array that expand original array by 1 element
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

    private int[] explorerDirection = new int[0];
    private int[] noWalls = new int[0];

    public int deadEnd(){
        for(int i=0;i<relativeDirection.length;i++) {
            if (robot.look(relativeDirection[i]) != IRobot.WALL) {
                direction = relativeDirection[i];
            }
        }
        return direction;
    }

    public int corridor(){
        for(int i=0;i<relativeDirection.length;i++) {
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

    public int junction(){
        for(int i=0;i<relativeDirection.length;i++) {
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

    public int crossRoad(){
        for(int i=0;i<relativeDirection.length;i++) {
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


    // this method returns a description of this controller
    public String getDescription() {
       return "A controller which explores the maze in a structured way";
    }

    // sets the delay
    public void setDelay(int millis) {
       delay = millis;
    }

    // gets the current delay
    public int getDelay() {
       return delay;
    }

    // stops the controller
    public void reset() {
       active = false;
    }

    // sets the reference to the robot
    public void setRobot(IRobot robot) {
       this.robot = robot;
    }

}

