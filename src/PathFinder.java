
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Arnold Anthonypillai W1519172
 */
public class PathFinder {

    private int gridSize = 0;
    private int startXPosition = 0;
    private int startYPosition = 0;
    private int endXPosition = 0;
    private int endYPosition = 0;
    private double tempG = 0;
    private boolean pathFound = false;
    private boolean manhattanChosen = false;
    private boolean euclideanChosen = false;
    private boolean chebyshevChosen = false;
    private ArrayList<Node> openNodes = null;
    private ArrayList<Node> evaluatedNodes = null;
    private Stopwatch timerFlow = null;
    boolean[][] randomlyGenMatrix = null;
    
    private Scanner in = new Scanner(System.in);

    public PathFinder() {
    }

    /*
    initialise the grid and shows the grid to the user
     */
    public void initGrid(boolean[][] a, boolean which) {
        int N = a.length;
        StdDraw.setXscale(-1, N);;
        StdDraw.setYscale(-1, N);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j] == which) {
                    /*
                    taking the parameter which to determine if this square to be blocked cell or not
                     */
                    StdDraw.square(j, N - i - 1, .5);

                } else {
                    StdDraw.filledSquare(j, N - i - 1, .5);
                }
            }
        }
    }

    /*
    taken the parameters from the user and produces the grid and then points out where the starting node and 
    ending nodes are placed
     */
    public void showGrid(boolean[][] a, boolean which, int x1, int y1, int x2, int y2) {
        int N = a.length;
        StdDraw.setXscale(-1, N);
        StdDraw.setYscale(-1, N);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j] == which) {
                    if ((i == x1 && j == y1) || (i == x2 && j == y2)) { //starting and ending nodes to be circled
                        StdDraw.circle(j, N - i - 1, .5);
                    } else {
                        StdDraw.square(j, N - i - 1, .5);

                    }
                } else {
                    StdDraw.filledSquare(j, N - i - 1, .5);
                }
            }
        }
    }

    public boolean[][] random(int N, double p) {
        /*
        takes the user parameter and produces the grid randomly placing the blocked cells
         */
        boolean[][] a = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                a[i][j] = StdRandom.bernoulli(p);
            }
        }
        return a;
    }

    /*
    this takes the user the usr parameter of the size of the grid and creates the grid
    and also let the user to make a fresh grid allowing them to choose the size recursively
     */
    public void createNewGrid() {
        openNodes = new ArrayList<>();
        Scanner in = new Scanner(System.in);

        System.out.println("\nEnter size of the grid");
        while (!in.hasNext("([2-9]|1[0-9]|2[0-9]|3[0])")) {
            in.next();
            System.err.println("\nPlease enter a number between 2 - 30!");
        }
        int size = in.nextInt();

        randomlyGenMatrix = random(size, 0.7);
        gridSize = randomlyGenMatrix.length - 1;
        StdArrayIO.print(randomlyGenMatrix);
        initGrid(randomlyGenMatrix, true);

        refreshGrid();
    }
    
    /*
    this method is used to recursively called upon if the user wants to use the same grid with the same blocked cells
    in order to analyse the perfomance of the different distance metrics
    */
    public void refreshGrid() {
        
        StdDraw.clear(); //clears the existing grid and redraws the grid
        openNodes = new ArrayList<>();
        initGrid(randomlyGenMatrix, true);
        boolean validInput = false;
        
        System.out.println("\nEnter X coordinate for Starting Node >> ");
        startXPosition = in.nextInt();

        System.out.println("\nEnter Y coordinate for Starting Node >> ");
        startYPosition = in.nextInt();

        System.out.println("\nEnter X coordinate for Ending Node >> ");
        endXPosition = in.nextInt();

        System.out.println("\nEnter Y coordinate for Ending Node >> ");
        endYPosition = in.nextInt();
       
        System.out.println("\nEnter the Distance metrics to be used for the calculations \n 1: Manhattan \n 2: Euclidean \n 3: Chebyshev");
        while (!in.hasNext("[1-3]")) {
            in.next();
            System.err.println("\nplease enter a number between 1 - 3!");
        }
        int metricChoice = in.nextInt();

        switch (metricChoice) {
            case 1:
                manhattanChosen = true;
                break;

            case 2:
                euclideanChosen = true;
                break;

            case 3:
                chebyshevChosen = true;
                break;
        }

        double G = 0;
        double heuristic = calculateHeuristic(startXPosition, startYPosition);
        double fValue = G + heuristic;

        Node startingNode = new Node(startXPosition, startYPosition);
        startingNode.setFValue(fValue);

        openNodes.add(startingNode);

        Node endingNode = new Node(endXPosition, endYPosition);

        showGrid(randomlyGenMatrix, true, startXPosition, startYPosition,
                endXPosition, endYPosition);

        findPath(startingNode, endingNode);

        System.out.println("\n1: Try a different metric on the same grid \n2: Produce a new randomized grid \n3: Terminate Application");
        while (!in.hasNext("[1-3]")) {
            in.next();
            System.err.println("\nPlease enter a valid input!");
        }

        int choice = in.nextInt();

        switch(choice){
            case 1:
                refreshGrid();
            break;
            
            case 2:
                createNewGrid();
            break;
            
            case 3:
                System.exit(0);
            break;
        }
        
    }
    
    public void findPath(Node start, Node end) {
        
        evaluatedNodes = new ArrayList<>();
        timerFlow = new Stopwatch(); //starts the timer
        System.out.println("\nTimer starting now!");

        Node currentNode = null;

        while (!openNodes.isEmpty()) { //iterates through until the list is empty
            currentNode = findLowestFValue(openNodes); // sorts the array and returns the node with the lowest F value

            /*
            current node is the target node so we exit the while loop and trace the path and draw the line
            */
            if (currentNode.getXPos() == end.getXPos() && currentNode.getYPos() == end.getYPos()) {
                determinePath(currentNode);
                System.out.println("\nPath Found!");
                pathFound = true;
                break;
            }

            removeNode(openNodes, currentNode);
            evaluatedNodes.add(currentNode);
            
            /*
            evaluate the valid adjacent cells of this node and sets them as it's adjacent nodes
            */
            currentNode.setAdjacentNodes(evaluateAdjacentNodes(currentNode));

            ArrayList<Node> adjacentNodesOfCurrentNode = currentNode.getAdjacentNodes();

            /*
            for each of the adjacent cells of the current node and we check if it is already evaluated or if it is in open
            nodes or or if it is a new node.
            */
            for (Node adjacentNode : adjacentNodesOfCurrentNode) {
                if (checkExistingNode(evaluatedNodes, adjacentNode) == false) {
                    tempG = 0;
                    
                    /*
                    if the adjacent nodes are not diagonal, then the cost to move one cell is 1
                    */
                    if (adjacentNode.getXPos() == currentNode.getXPos()
                            || adjacentNode.getYPos() == currentNode.getYPos()) {
                        tempG = currentNode.getGValue() + 1;
                    } 
                    
                    else {
                        
                        /*
                        if it is diagonal and if the user has picked the Manhattan metics then the cost to move diagonally
                        will be 2
                        */
                        if (manhattanChosen) {
                            tempG = currentNode.getGValue() + 2;
                        } 
                        
                        /*
                        if it is the Euclidean metrics being used then the cost to move diagonally is changed to 1.4
                        */
                        else if (euclideanChosen) {
                            tempG = currentNode.getGValue() + 1.4;
                        } 
                        
                        /*
                        last but not least the default metrics cost to move would be 1 (Chebyshev)
                        */
                        else if (chebyshevChosen) {
                            tempG = currentNode.getGValue() + 1;
                        }
                    }

                    boolean betterPath = false;

                    if (checkExistingNode(openNodes, adjacentNode) == true) {
                        if (tempG < adjacentNode.getGValue()) {
                            adjacentNode.setGValue(tempG); //recalculates the adjacent node's G value
                            betterPath = true;
                        }

                    } else {
                        adjacentNode.setGValue(tempG);
                        betterPath = true;
                        openNodes.add(adjacentNode);
                    }

                    /*
                    a better path has been discovered hence, recalculate the values and set the current node as it's 
                    parent node
                    */
                    if(betterPath){
                        int X = adjacentNode.getXPos();
                        int Y = adjacentNode.getYPos();
                        adjacentNode.setHValue(calculateHeuristic(X, Y));
                        adjacentNode.setFValue(adjacentNode.getGValue() + adjacentNode.getHValue());
                        adjacentNode.setParentNode(currentNode);
                    }

                }

            }

        }
        if (!pathFound) { // failure to find a path
            manhattanChosen = false;
            euclideanChosen = false;
            chebyshevChosen = false;
            System.out.println("no path!");
        }

    }

    /*
    bubble sort algorithm to get the lowest F value to be considered progressing through the search
    */
    public Node findLowestFValue(ArrayList<Node> listOfNode) {
        if (!(listOfNode.isEmpty())) {

            for (int i = 0; i < listOfNode.size(); i++) {
                for (int j = 1; j < (listOfNode.size() - i); j++) {
                    double tempFValue1 = listOfNode.get(j).getFValue();
                    double tempFvalue2 = listOfNode.get(j - 1).getFValue();

                    if (tempFValue1 < tempFvalue2) {
                        Node temp1 = listOfNode.get(j - 1);
                        Node temp2 = listOfNode.get(j);
                        listOfNode.set(j, temp1);
                        listOfNode.set(j - 1, temp2);
                    }
                }
            }
        }

        return listOfNode.get(0);
    }
    
    /*
    takes a node and checks it's adjacent nodes if they are valid then add to an array list and returns it
    */
    public ArrayList<Node> evaluateAdjacentNodes(Node nodeToEvaluate) {
        ArrayList<Node> adjacentNodes = new ArrayList<>();
        int X = nodeToEvaluate.getXPos();
        int Y = nodeToEvaluate.getYPos();

        if (X < gridSize && randomlyGenMatrix[X + 1][Y] == true) {
            adjacentNodes.add(new Node(X + 1, Y));
        }

        if (X > 0 && randomlyGenMatrix[X - 1][Y] == true) {
            adjacentNodes.add(new Node(X - 1, Y));
        }

        if (Y < gridSize && randomlyGenMatrix[X][Y + 1] == true) {
            adjacentNodes.add(new Node(X, Y + 1));
        }

        if (Y > 0 && randomlyGenMatrix[X][Y - 1] == true) {
            adjacentNodes.add(new Node(X, Y - 1));
        }

        if (X > 0 && Y > 0 && randomlyGenMatrix[X - 1][Y - 1] == true) {
            adjacentNodes.add(new Node(X - 1, Y - 1));
        }

        if (X < gridSize && Y > 0 && randomlyGenMatrix[X + 1][Y - 1] == true) {
            adjacentNodes.add(new Node(X + 1, Y - 1));
        }

        if (X > 0 && Y < gridSize && randomlyGenMatrix[X - 1][Y + 1] == true) {
            adjacentNodes.add(new Node(X - 1, Y + 1));
        }

        if (X < gridSize && Y < gridSize && randomlyGenMatrix[X + 1][Y + 1] == true) {
            adjacentNodes.add(new Node(X + 1, Y + 1));
        }

        return adjacentNodes;
    }

    /*
    calculate heuristics, to determin which is the optimum path
    we use 3 distance metics
    */
    public double calculateHeuristic(int X, int Y) {
        double H = 0;

        //manhattan
        if (manhattanChosen) {
            H = Math.abs(X - endXPosition) + Math.abs(Y - endYPosition);
        } //euclidean
        else if (euclideanChosen) {
            H = (int) Math.sqrt((Math.pow((X - endXPosition), 2)) + (Math.pow((Y - endYPosition), 2)));
        } //chebyshev
        else if (chebyshevChosen) {
            H = Math.max(Math.abs(X - endXPosition), Math.abs(Y - endYPosition));
        }

        return H;
    }

    /*
    this method helps me to check if a node in a list eg: open nodes, evaluated nodes
    */
    public boolean checkExistingNode(ArrayList<Node> list, Node nodeToCheck) {
        for (Node n : list) {
            if (n.getXPos() == nodeToCheck.getXPos() && n.getYPos() == nodeToCheck.getYPos()) {
                return true;
            }
        }

        return false;
    }

    /*
    removes an element from the arraylist, this method actually removes from the end, rather than the front
    */
    public void removeNode(ArrayList<Node> list, Node nodeToRemove) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals(nodeToRemove)) {
                list.remove(i);
            }
        }
    }

    /*
    we have got the ending node as our current node so we can trace it back to the starting node
    */
    public void determinePath(Node leadingNode) {
        ArrayList<Node> path = new ArrayList<>();
        Node pathNode = leadingNode;
        path.add(pathNode);

        while (pathNode.getParentNode() != null) {
            path.add(pathNode.getParentNode());
            pathNode = pathNode.getParentNode();
        }
        drawPath(path);
    }

    /*
    draws the line from cell to cell from the ending node to starting node
    */
    public void drawPath(ArrayList<Node> path) {
       
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.text(startYPosition, gridSize - startXPosition, "Start");
        for (int i = 1; i < path.size(); i++) {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(path.get(i - 1).getYPos(), gridSize - path.get(i - 1).getXPos(),
                    path.get(i).getYPos(), gridSize - path.get(i).getXPos());

        }
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.text(endYPosition, gridSize - endXPosition, "End");
        if (manhattanChosen) {
            System.out.println("Total Cost for Manhattan Distance Metric :: " + path.get(0).getFValue());
        } else if (euclideanChosen) {
            System.out.println("Total Cost for Euclidean Distance Metric :: " + path.get(0).getFValue());
        } else if (chebyshevChosen) {
            System.out.println("Total Cost for Chebyshev Distance Metric :: " + path.get(0).getFValue());
        }

        manhattanChosen = false;
        euclideanChosen = false;
        chebyshevChosen = false;

        StdOut.println("Elapsed time = " + timerFlow.elapsedTime()); //measures the time until now
    }

    public static void main(String[] args) {
        PathFinder pathFinder = new PathFinder();
        pathFinder.createNewGrid();

    }
}
