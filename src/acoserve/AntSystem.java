
package acoserve;

import java.util.Vector;
import java.util.Map;
import java.util.Map.Entry;
//import java.util.Set;
//import java.util.TreeSet;
import java.util.TreeMap;

public class AntSystem
{
    public static final int ANTS = 30;
    public static final int ITERATIONS = 10;
    public static final int PHERO_QNT = 100;
    public static final double A = 1;
    public static final double B = 5;
    public static final double EVAPORATE_PER = 0.5;


    class Edge
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
        private double cost = 0;
    }

    public AntSystem()
    {
    }

    public Vector<Integer> path(int src, int dest)
    {
        Map<Vector<Integer>, Double> trails = new TreeMap<>();
        Map<Edge, Double> edge2phero = new TreeMap<>();
        int i = 0;
        while(i++ < ITERATIONS)
        {
            trails.clear();
            int ant = 1;
            while(ant++ <= ANTS)
            {
                Vector<Integer> tour = goAnt(src, dest);
                double length = tourLength(tour);
                trails.put(tour, length);
            }
            updateTrails(trails);
        }

        return bestPath();
    }

    private Vector<Integer> goAnt(int src, int dest)
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


        return;
    }

    private double prob(int i, int j)
    {
        double retProb = 0;

        return retProb;
    }

    private double heuInfo(int i, int j, Map<Edge, Double> edge2phero)
    {
        //  Correlate with assets and resources
        Edge edge = new Edge(i, j);
        if(edge2phero.containsKey(edge))
            for(Entry<Edge, Double> entry : edge2phero.entrySet())
                if(entry.getValue().equals(edge))
                {
                    edge = entry.getKey();
                    break;
                }

        return edge.cost;
    }

    private double pheroDiffSum(int i, int j, Map<Edge, Double> edge2phero)
    {
        if(edge2phero.containsKey(new Edge(i, j)))
            return edge2phero.get(new Edge(i, j)).doubleValue();

        return 0;
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
