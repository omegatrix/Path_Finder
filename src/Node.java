
import java.util.ArrayList;

/**
 *
 * @author User Arnold Anthonypillai W1519172
 */
public class Node {
    private int XPos = 0;
    private int YPos = 0;
    private double gValue = 0;
    private double hValue = 0;
    private double fValue = 0;
    private ArrayList<Node> adjacentNodes = new ArrayList<>();
    private Node parentNode = null;

    public Node(int X, int Y) {
        this.XPos = X;
        this.YPos = Y;
    }

    public int getXPos() {
        return XPos;
    }

    public void setXPos(int XPos) {
        this.XPos = XPos;
    }

    public int getYPos() {
        return YPos;
    }

    public void setYPos(int YPos) {
        this.YPos = YPos;
    }

    public double getGValue() {
        return gValue;
    }

    public void setGValue(double gValue) {
        this.gValue = gValue;
    }

    public double getHValue() {
        return hValue;
    }

    public void setHValue(double hValue) {
        this.hValue = hValue;
    }

    public double getFValue() {
        return fValue;
    }

    public void setFValue(double fValue) {
        this.fValue = fValue;
    }

    public ArrayList<Node> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(ArrayList<Node> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }
}
