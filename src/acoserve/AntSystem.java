
package acoserve;

import java.util.Vector;
import java.util.Map;
import java.util.Map.Entry;
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
    private int nodeCount;
    private Map<Integer, Integer> edges = new TreeMap<>();

    /*class Edge implements java.lang.Comparable<Edge>
    {
        Edge(int a, int b)
        {
            this.a = a;
            this.b = b;
        }

        public boolean equals(Edge edge)
        {
            if(this == edge)
                return true;

            return (a == edge.a && b == edge.b) || (a == edge.b && b == edge.a);
        }

        private int a = 0;
        private int b = 0;
        private double cost = 1;

        @Override
        public int compareTo(Edge rhs)
        {
            if(this.a < rhs.a)
                return -1;

            if(this.a > rhs.a)
                return 1;

            if(this.b < rhs.b)
                return -1;

            if(this.b > rhs.b)
                return 1;

            return 0;
        }
    }*/

    public AntSystem()
    {
        try
        {
            init();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public Vector<Integer> path(int src, int dest)
    {
        Map<Vector<Integer>, Double> trails = new TreeMap<>();
        double[][] edge2phero = new double[nodeCount][nodeCount];
        // TODO: init array

        int i = 0;
        while(i++ < ITERATIONS)
        {
            trails.clear();
            int ant = 1;
            while(ant++ <= ANTS)
            {
                Vector<Integer> tour = unleashAnt(src, dest);
                double length = tourLength(tour);
                trails.put(tour, length);
            }
            updateTrails(trails);
        }

        return bestPath();
    }

    private Vector<Integer> unleashAnt(int src, int dest)
    {
        int srcTemp = src;
        int destTemp = dest;

        while(destTemp != dest)
        {

        }

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

        for(int i = 0; i < edge2phero[node - 1].length; ++i)
        {
            if(edge2phero[node - 1][i] >= 0)
                if(i + 1 != node)
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
        if(neighs.size() == 0)
            throw new IllegalArgumentException("prob(..): No neighbours");

        for(int neigh : neighs)
            denum += Math.pow(phero(i, neigh, edge2phero), A)
				* Math.pow(heuInfo(i, neigh, edge2phero), B);

        return num / denum;
    }

    private double heuInfo(int i, int j, double[][] edge2phero)
    {
        //  Correlate with assets and resources
        /*Edge edge = new Edge(i, j);
        if(edge2phero.containsKey(edge))
            for(Entry<Edge, Double> entry : edge2phero.entrySet())
                if(entry.getKey().equals(edge))
                {
                    edge = entry.getKey();
                    break;
                }*/

        return 1 / edge2phero[i][j];
    }

    private double phero(int i, int j, double[][] edge2phero)
    {
        return edge2phero[i][j];
    }

    private double tourLength(Vector<Integer> path)
    {
        return (double)path.size();
    }

    private void updateTrails(Map<Vector<Integer>, Double> trails)
    {
        return;
    }

    Vector<Integer> bestPath()
    {
        return null;
    }
}
