
package acoserve;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Iterator;

public class AntSystem
{
    public static final int ANTS = 300;
    public static final int ITERATIONS = 100;
    public static final int PHERO_QNT = 100;
    public static final double A = 1;
    public static final double B = 5;
    public static final double EVAPORATE_PER = 0.5;
    public static final int NO_NEIGHBOUR = -1;
    public static final int NO_PHEROMONE = -1;
    private Set<Integer> nodes = new TreeSet<>();
    private Map<Pair<Integer, Integer>, Long> edge2distance = new HashMap<>();

    public AntSystem(Map<Pair<Integer, Integer>, Long> edge2distance)
    {
        this.edge2distance = edge2distance;
        try
        {
            init();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void init()
    {
        Set<Pair<Integer, Integer>> keys = edge2distance.keySet();
        for(Pair<Integer, Integer> edge : keys)
        {
            nodes.add(edge.getLeft());
            nodes.add(edge.getRight());
        }
    }

    private double[][] createPheroTopo()
    {
        double[][] edge2phero = new double[nodes.size()][nodes.size()];
        for(int i = 0; i < nodes.size(); ++i)
            for(int j = 0; j < nodes.size(); ++j)
                edge2phero[i][j] = NO_PHEROMONE;

        Set<Pair<Integer, Integer>> keys = edge2distance.keySet();
        for(Pair<Integer, Integer> edge : keys)
            edge2phero[edge.getLeft()][edge.getRight()] = PHERO_QNT;

        return edge2phero;
    }

    public Vector<Integer> path(int src, int dest)
    {
        Set<Vector<Integer>> allPaths = new TreeSet<>();
        Map<Vector<Integer>, Double> evalPaths = new HashMap<>();
        double[][] edge2phero = createPheroTopo();

        int i = 0;
        while(i++ < ITERATIONS)
        {
            evalPaths.clear();
            int ant = 0;
            while(ant++ < ANTS)
            {
                Vector<Integer> tour = unleashAnt(src, dest, edge2phero);
                if(tour.size() > 1)
                    evalPaths.put(tour, tourLength(tour));
            }
            updateTrails(evalPaths, edge2phero);
            allPaths.addAll(evalPaths.keySet());
        }

        return bestPath(allPaths, edge2phero);
    }

    private Vector<Integer> unleashAnt(int src, int dest, double[][] edge2phero)
    {
        Vector<Integer> trace = new Vector<>();
        int node = src;
        trace.add(node);

        while(node != dest)
        {
            int neighbour = pickUpNeighbour(node, edge2phero);
            if(neighbour == NO_NEIGHBOUR)
                break;  //  Dead end
            if(!trace.contains(neighbour))
                trace.add(neighbour);
            else
                break;  //  Cycle

            node = neighbour;
        }

        if(trace.size() <= 1)
            return new Vector<Integer>();

        return trace.firstElement() == src && trace.lastElement() == dest ? trace : new Vector<Integer>();
    }

    private Integer pickUpNeighbour(int node, double[][] edge2phero)
    {
        Vector<Integer> neighs = availNeighbours(node, edge2phero);     //  Unsorted neighbours
        if(neighs.size() == 0)
            return NO_NEIGHBOUR;

        double probs[] = new double[neighs.size()];
        int index = 0;
        // Produce a transition probability to each one
        for(int neigh : neighs)
            probs[index++] = prob(node, neigh, edge2phero);

        double value = Math.random();
        // Sort probabilities in range [0, 1] and use a uniform distro to
        // pick up an index domain
        double sum = 0;
        for(index = 0; index < neighs.size(); ++index)
        {
            sum += probs[index];
            if(value <= sum)
                break;
        }

        return neighs.size() > 0 ? neighs.elementAt(index) : NO_NEIGHBOUR;
    }

    private Vector<Integer> availNeighbours(int node, double[][] edge2phero)
    {
        Vector<Integer> neighbours = new Vector<Integer>();

        for(int i = 0; i < edge2phero[node].length; ++i)
            if(edge2phero[node][i] >= 0)
                if(i != node)
                    neighbours.add(i);

        return neighbours;
    }

    private double prob(int i, int j, double[][] edge2phero) throws IllegalArgumentException
    {
        double num = Math.pow(edge2phero[i][j], A)
                * Math.pow(heuInfo(i, j, edge2phero), B);

        double denum = 0;
        Vector<Integer> neighs = availNeighbours(i, edge2phero);
        if(neighs.size() == 0)
            throw new IllegalArgumentException("prob(..): No neighbours");

        for(int neigh : neighs)
            denum += Math.pow(edge2phero[i][neigh], A)
                    * Math.pow(heuInfo(i, neigh, edge2phero), B);

        return num / denum;
    }

    private double heuInfo(int i, int j, double[][] edge2phero)
    {
        //  Should not be in the return statement
        double distance = edge2distance.get(new Pair<Integer, Integer>(i, j));

        return 1 / distance;
    }

    private double tourLength(Vector<Integer> path)
    {
        //  The Lk value
        return (double)path.size();
    }

    private void updateTrails(Map<Vector<Integer>, Double> evalPaths, double[][] edge2phero)
    {
        // Evaporate existing pheromone levels
        for(int i = 0; i < nodes.size(); ++i)
            for(int j = 0; j < nodes.size(); ++j)
                if(edge2phero[i][j] != NO_PHEROMONE)
                    edge2phero[i][j] *= (1 - EVAPORATE_PER);

        // Increase pheromone level upon correct paths
        Set<Vector<Integer>> onlyTrails = evalPaths.keySet();
        for(Vector<Integer> path : onlyTrails)
        {
            Iterator<Integer> it = path.iterator();
            int str = it.next();
            while(it.hasNext())
            {
                int end = it.next();
                edge2phero[str][end] += PHERO_QNT / tourLength(path);
                str = end;
            }
        }
    }

    Vector<Integer> bestPath(Set<Vector<Integer>> allPaths, double[][] edge2phero)
    {
        /*//  Choose the return path
        Vector<Integer> retpath = new Vector<>();
        double eval = Double.MAX_VALUE;

        Set<Vector<Integer>> keys = evalPaths.keySet();
        for(Vector<Integer> path : keys)
            if(evalPaths.get(path) < eval)
            {
                eval = evalPaths.get(path);
                retpath = path;
            }

        return retpath;*/

        return null;
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
