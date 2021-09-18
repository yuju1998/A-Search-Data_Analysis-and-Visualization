import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    // TODO: figure out how to return output information
    public static void runRobot(Robot rob, Grid grid, SearchAlgo algo) {
        double totalTrajectoryLength = 0;
        int totalCellsProcessed = 0;

        while(rob.getLocation().f1 != rob.getGoal().f1 || rob.getLocation().f2 != rob.getGoal().f2) {
            // find path
            GridWorldInfo result = algo.search(rob.getLocation(), rob.getGoal(), grid, rob.getKnownObstacles()::contains);
            totalTrajectoryLength += result.getTrajectoryLength();
            totalCellsProcessed += result.getNumberOfCellsProcessed();

            if(result.getPath() == null) {
                // no path found, exit with failure
                totalTrajectoryLength = Double.NaN;
                break;
            }

            // attempt to move along that path
            rob.run(result.getPath(), grid);
        }

        // return output...
    }

    public static void runProbabilitySimulation(int xDimension, int yDimension, int numberOfIterations, boolean allowBumps) {
        ArrayList<GridWorldInfo> solutionDensity = new ArrayList<>();
        for (int i = 0; i <= 33; i++){
            for (int j = 0; j<numberOfIterations; j++) {
                Grid grid = new Grid(xDimension, yDimension, i);
                aStarSearchObject aso = new aStarSearchObject(Heuristics::euclideanDistance);
                GridWorldInfo info = aso.aStarSearch(new Tuple<>(0, 0), new Tuple<>(xDimension - 1, yDimension - 1), grid, (new HashSet<GridCell>())::contains);
                info.setProbability(i);
                solutionDensity.add(info);
            }
        }
        String bumps = allowBumps ? "bumps" : "NoBumps";
        printResultsToCsv(xDimension + "x" + yDimension + bumps +"Result.csv", solutionDensity);

    }

    public static void printResultsToCsv(String fileName, List<GridWorldInfo> gridWorldInfo){
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {

            StringBuilder sb = new StringBuilder();
            sb.append("Probability");
            sb.append(',');
            sb.append("Solvable");
            sb.append(',');
            sb.append("TrajectoryLength");
            sb.append(',');
            sb.append("NumberOfCellsProcessed");
            sb.append('\n');
            writer.write(sb.toString());

            for (GridWorldInfo info: gridWorldInfo) {
                sb = new StringBuilder();
                sb.append(info.getProbability());
                sb.append(',');
                sb.append(!Double.isNaN(info.getTrajectoryLength()));
                sb.append(',');
                sb.append(info.getTrajectoryLength());
                sb.append(',');
                sb.append(info.getNumberOfCellsProcessed());
                sb.append('\n');
                writer.write(sb.toString());
            }

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int iterations = args.length > 2 ? Integer.parseInt(args[2]): 1000;
        runProbabilitySimulation(x,y,iterations,true);
        //runProbabilitySimulation(x,y,iterations,false);
    }





}
