
package acoserve;

import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

public class AntSystem
{
    public static final int ANTS = 30;
    public static final int ITERATIONS = 10;
    public static final int PHERO_QNT = 100;
    public static final double A = 1;
    public static final double B = 5;
    public static final double EVAPORATE_PER = 0.5;
    public static final int NO_NEIGHBOUR = -1;
    private int nodeCount;
    private Map<Integer, Integer> edges = new TreeMap<>();

    public AntSystem()
    {
        try
        {
            init();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private double[][] createPheroTopo()
    {
        double[][] edge2phero = new double[nodeCount][nodeCount];
        for (int i = 0; i < nodeCount; ++i)
            for (int j = 0; j < nodeCount; ++j)
                edge2phero[i][j] = -1;

        Set<Integer> strNodes = edges.keySet();
        for (Integer strNode : strNodes)
        {
            Integer endNode = edges.get(strNode);
            edge2phero[strNode - 1][endNode - 1] = PHERO_QNT;
        }

        return edge2phero;
    }

    public Vector<Integer> path(int src, int dest)
    {
        Map<Vector<Integer>, Double> evalPaths = new TreeMap<>();
        double[][] edge2phero = createPheroTopo();

        int i = 0;
        while (i++ < ITERATIONS)
        {
            evalPaths.clear();
            int ant = 1;
            while (ant++ <= ANTS)
            {
                Vector<Integer> tour = unleashAnt(src, dest, edge2phero);
                double length = tourLength(tour);
                evalPaths.put(tour, length);
            }
            updateTrails(evalPaths);
        }

        return bestPath();
    }

    private Vector<Integer> unleashAnt(int src, int dest, double[][] edge2phero)
    {
        Vector<Integer> trace = new Vector<>();
        int srcTemp = src;
        final int destTemp = dest;

        while (srcTemp != destTemp)
        {
            int neighbour = pickUpNeighbour(srcTemp, edge2phero);
            if (neighbour == NO_NEIGHBOUR)
                break;  //  Dead end
            if (!trace.contains(neighbour))
                trace.add(neighbour);
            else
                break;  //  Cycle

            srcTemp = neighbour;
        }

        return trace.firstElement() == src && trace.lastElement() == dest ? trace : new Vector<Integer>();
    }

    private Integer pickUpNeighbour(int src, double[][] edge2phero)
    {
        Vector<Integer> neighs = availNeighbours(src, edge2phero);
        double probs[] = new double[neighs.size()];
        int index = 0;
        // Produce a transition probability to each one
        for(int neigh : neighs)
            probs[index++] = prob(src, neigh, edge2phero);

        double value = Math.random();
        // Sort probabilities in range [0, 1] and use a uniform distro to
        // pick up an index domain
        index = 0; double sum = 0;
        for(; index < neighs.size(); ++index)
        {
            sum += probs[index];
            if(value <= sum)
                break;
        }

        return neighs.size() > 0 ? neighs.elementAt(index) : NO_NEIGHBOUR;
    }

    private void init()
    {
        //  TODO: Use parsed values
        nodeCount = 0;


        return;
    }

    private Vector<Integer> availNeighbours(int node, double[][] edge2phero)
    {
        Vector<Integer> neighbours = new Vector<Integer>();

        for (int i = 0; i < edge2phero[node - 1].length; ++i)
            if (edge2phero[node - 1][i] >= 0)
                if (i + 1 != node)
                    neighbours.add(i + 1);

        return neighbours;
    }

    private double prob(int i, int j, double[][] edge2phero) throws IllegalArgumentException
    {
        double num = Math.pow(edge2phero[i - 1][j - 1], A)
                * Math.pow(heuInfo(i, j, edge2phero), B);

        double denum = 0;
        Vector<Integer> neighs = availNeighbours(i, edge2phero);
        if (neighs.size() == 0)
            throw new IllegalArgumentException("prob(..): No neighbours");

        for (int neigh : neighs)
            denum += Math.pow(edge2phero[i - 1][neigh - 1], A)
                    * Math.pow(heuInfo(i, neigh, edge2phero), B);

        return num / denum;
    }

    private double heuInfo(int i, int j, double[][] edge2phero)
    {
        //  TODO: Correlate with assets and resources, not pheromone

        return 1 / edge2phero[i][j];
    }

    private double tourLength(Vector<Integer> path)
    {
        //  TODO: Calculate Lk

        return (double) path.size();
    }

    private void updateTrails(Map<Vector<Integer>, Double> trails)
    {
        //  TODO: A lot

        return;
    }

    Vector<Integer> bestPath()
    {
        //  TODO: Choose the return path

        return null;
    }
}
