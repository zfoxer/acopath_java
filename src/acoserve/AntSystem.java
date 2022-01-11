
package acoserve;

import java.util.*;
import java.util.Set;

public class AntSystem
{
    public static final int ANTS = 30;
    public static final int ITERATIONS = 10;
    public static final int PHERO_QNT = 100;
    public static final double A = 1;
    public static final double B = 5;
    public static final double EVAPORATE_PER = 0.5;
    public static final int NO_NEIGHBOUR = -1;
    public static final int NO_PHEROMONE = -1;
    private int nodeCount;
    private Map<Integer, Integer> edges = new TreeMap<>();
    private Map<Pair<Integer, Integer>, Long> edge2distance = new HashMap<>();

    public AntSystem(Map<Pair<Integer, Integer>, Long> edge2distance)
    {
        this.edge2distance = edge2distance;
        try
        {
            init();
        } catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private double[][] createPheroTopo()
    {
        double[][] edge2phero = new double[nodeCount][nodeCount];
        for(int i = 0; i < nodeCount; ++i)
            for(int j = 0; j < nodeCount; ++j)
                edge2phero[i][j] = NO_PHEROMONE;

        Set<Integer> strNodes = edges.keySet();
        for(Integer strNode : strNodes)
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
        while(i++ < ITERATIONS)
        {
            evalPaths.clear();
            int ant = 1;
            while(ant++ <= ANTS)
            {
                Vector<Integer> tour = unleashAnt(src, dest, edge2phero);
                evalPaths.put(tour, tourLength(tour));
            }
            updateTrails(evalPaths, edge2phero);
        }

        return bestPath(evalPaths);
    }

    private Vector<Integer> unleashAnt(int src, int dest, double[][] edge2phero)
    {
        Vector<Integer> trace = new Vector<>();
        int srcTemp = src;
        final int destTemp = dest;

        while(srcTemp != destTemp)
        {
            int neighbour = pickUpNeighbour(srcTemp, edge2phero);
            if(neighbour == NO_NEIGHBOUR)
                break;  //  Dead end
            if(!trace.contains(neighbour))
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
        index = 0;
        double sum = 0;
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
        Set<Integer> nodes = new TreeSet<>();
        Set<Pair<Integer, Integer>> keys = edge2distance.keySet();
        for(Pair<Integer, Integer> edge : keys)
        {
            nodes.add(edge.getLeft());
            nodes.add(edge.getRight());
        }

        nodeCount = nodes.size();
    }

    private Vector<Integer> availNeighbours(int node, double[][] edge2phero)
    {
        Vector<Integer> neighbours = new Vector<Integer>();

        for(int i = 0; i < edge2phero[node - 1].length; ++i)
            if(edge2phero[node - 1][i] >= 0)
                if(i + 1 != node)
                    neighbours.add(i + 1);

        return neighbours;
    }

    private double prob(int i, int j, double[][] edge2phero) throws IllegalArgumentException
    {
        double num = Math.pow(edge2phero[i - 1][j - 1], A)
                * Math.pow(heuInfo(i, j, edge2phero), B);

        double denum = 0;
        Vector<Integer> neighs = availNeighbours(i, edge2phero);
        if(neighs.size() == 0)
            throw new IllegalArgumentException("prob(..): No neighbours");

        for(int neigh : neighs)
            denum += Math.pow(edge2phero[i - 1][neigh - 1], A)
                    * Math.pow(heuInfo(i, neigh, edge2phero), B);

        return num / denum;
    }

    private double heuInfo(int i, int j, double[][] edge2phero)
    {
        return 1 / edge2distance.get(new Pair<Integer, Integer>(i, j));
    }

    private double tourLength(Vector<Integer> path)
    {
        //  The Lk value
        return (double)path.size();
    }

    private void updateTrails(Map<Vector<Integer>, Double> trails, double[][] edge2phero)
    {
        // Evaporate all existing pheromone levels
        for(int i = 0; i < nodeCount; ++i)
            for(int j = 0; j < nodeCount; ++j)
                if(edge2phero[i][j] != NO_PHEROMONE)
                    edge2phero[i][j] *= (1 - EVAPORATE_PER);

        // Increase pheromone level upon correct paths
        Set<Vector<Integer>> onlyTrails = trails.keySet();
        for(Vector<Integer> path : onlyTrails)
        {
            Iterator<Integer> it = path.iterator();
            int str = it.next();
            while(it.hasNext())
            {
                int end = it.next();
                edge2phero[str - 1][end - 1] += PHERO_QNT / tourLength(path);
                str = end;
            }
        }
    }

    Vector<Integer> bestPath(Map<Vector<Integer>, Double> evalPaths)
    {
        //  Choose the return path
        Vector<Integer> retpath = new Vector<>();
        double eval = Double.MAX_VALUE;

        Set<Vector<Integer>> keys = evalPaths.keySet();
        for(Vector<Integer> path : keys)
            if(evalPaths.get(path) < eval)
            {
                eval = evalPaths.get(path);
                retpath = path;
            }

        return retpath;
    }
}

record Pair<L, R>(L lhs, R rhs)
{
    Pair
    {
        assert lhs != null;
        assert rhs != null;
    }

    public L getLeft()
    {
        return lhs;
    }
    public R getRight()
    {
        return rhs;
    }

    @Override
    public int hashCode()
    {
        return lhs.hashCode() ^ rhs.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Pair))
            return false;

        Pair pairObj = (Pair)obj;
        return this.lhs.equals(pairObj.getLeft()) && this.rhs.equals(pairObj.getRight());
    }
}
