
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
        } catch (Exception e)
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
            Set<Integer> neighs = availNeighbours(srcTemp, edge2phero);
            int neighbour = pickUpNeighbour(neighs, edge2phero);
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

    private Integer pickUpNeighbour(Set<Integer> neighs, double[][] edge2phero)
    {
        return null;
    }

    private void init()
    {
        //  TODO: Use parsed value
        nodeCount = 0;


        return;
    }

    private Set<Integer> availNeighbours(int node, double[][] edge2phero)
    {
        Set<Integer> neighbours = new TreeSet<Integer>();

        for (int i = 0; i < edge2phero[node - 1].length; ++i)
        {
            if (edge2phero[node - 1][i] >= 0)
                if (i + 1 != node)
                    neighbours.add(i + 1);
        }

        return neighbours;
    }

    private double prob(int i, int j, double[][] edge2phero) throws IllegalArgumentException
    {
        double num = Math.pow(phero(i, j, edge2phero), A)
                * Math.pow(heuInfo(i, j, edge2phero), B);

        double denum = 0;
        Set<Integer> neighs = availNeighbours(i, edge2phero);
        if (neighs.size() == 0)
            throw new IllegalArgumentException("prob(..): No neighbours");

        for (int neigh : neighs)
            denum += Math.pow(phero(i, neigh, edge2phero), A)
                    * Math.pow(heuInfo(i, neigh, edge2phero), B);

        return num / denum;
    }

    private double heuInfo(int i, int j, double[][] edge2phero)
    {
        //  TODO: Correlate with assets and resources, not pheromone

        return 1 / edge2phero[i][j];
    }

    private double phero(int i, int j, double[][] edge2phero)
    {
        //  TODO: Remove - Useless

        return edge2phero[i][j];
    }

    private double tourLength(Vector<Integer> path)
    {
        //  TODO: Calculate Lk

        return (double) path.size();
    }

    private void updateTrails(Map<Vector<Integer>, Double> trails)
    {
        //  TODO

        return;
    }

    Vector<Integer> bestPath()
    {
        //  TODO: Choose the return path

        return null;
    }
}
