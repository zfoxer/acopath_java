/*
 * AcoPath for Java: Shortest path calculation using Ant Colony Optimization
 * Copyright (C) 2021-2022 by Constantine Kyriakopoulos
 * zfox@users.sourceforge.net
 * @version 1.0.1
 *
 * @section LICENSE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package acopath_java;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class AntSystem
{
    /**
     * The default number of ants to unleash in each iteration.
     */
    public static final int ANTS = 300;

    /**
     * Total default number of iterations for ant unleashing.
     */
    public static final int ITERATIONS = 100;

    /**
     * Initial pheromone quantity.
     */
    public static final int PHERO_QNT = 100;

    /**
     * Provides importance to its base parameter.
     */
    public static final double A = 1;

    /**
     * Provides importance to its base parameter.
     */
    public static final double B = 5;

    /**
     * Percentage of pheromone evaporation.
     */
    public static final double EVAPORATE_PER = 0.5;

    /**
     * Denotes that no neighbour exists for this node.
     */
    public static final int NO_NEIGHBOUR = -1;

    /**
     * Denote that no pheromone exists for this edge.
     */
    public static final int NO_PHEROMONE = -1;

    /**
     * Set containing topology's nodes.
     */
    private Set<Integer> nodes = new TreeSet<>();

    /**
     * Mapping each edge to its distance.
     */
    private Map<Pair<Integer, Integer>, Long> edge2distance = new HashMap<>();

    /**
     * The number of ants to unleash in each iteration.
     */
    private int ants = 0;

    /**
     * Total number of iterations for ant unleashing.
     */
    private int iterations = 0;

    /**
     * Creates a new Ant System from a container of edges mapped to their distances.
     * @param edge2distance Topology representation.
     */
    public AntSystem(Map<Pair<Integer, Integer>, Long> edge2distance, int ants, int iterations)
    {
        this.ants = ants;
        this.iterations = iterations;
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

    /**
     * Initialises the Ant System.
     */
    private void init()
    {
        if(ants <= 0)
            ants = ANTS;
        if(iterations <= 0)
            iterations = ITERATIONS;

        Set<Pair<Integer, Integer>> keys = edge2distance.keySet();
        for(Pair<Integer, Integer> edge : keys)
        {
            nodes.add(edge.getLeft());
            nodes.add(edge.getRight());
        }
    }

    /**
     * Creates a container representing the topology with initial pheromone quantity on its edges.
     * @return double[][] A two dimensional array with the amount of pheromone each edge carries initially.
     */
    private double[][] createPheroTopo()
    {
        double[][] edge2phero = new double[nodes.size()][nodes.size()];
        for(int i = 0; i < nodes.size(); ++i)
            for(int j = 0; j < nodes.size(); ++j)
                edge2phero[i][j] = NO_PHEROMONE;

        Set<Pair<Integer, Integer>> keys = edge2distance.keySet();
        for(Pair<Integer, Integer> edge : keys)
        {
            edge2phero[edge.getRight()][edge.getLeft()] = PHERO_QNT;
            edge2phero[edge.getLeft()][edge.getRight()] = PHERO_QNT;
        }

        return edge2phero;
    }

    /**
     * Returns the path consisting of a sequence of nodes, starting from 'src' and ending to 'dest'.
     * @param src Path starting node.
     * @param dest Path ending node.
     * @return Vector<Integer> A node sequence comprising the path.
     */
    public Vector<Integer> path(int src, int dest)
    {
        Map<Vector<Integer>, Integer> pathCount = new HashMap<>();
        double[][] edge2phero = createPheroTopo();

        int i = 0;
        while(i++ < iterations)
        {
            int ant = 0;
            while(ant++ < ants)
            {
                Vector<Integer> tour = unleashAnt(src, dest, edge2phero);
                if(tour.size() > 1)
                    if(pathCount.containsKey(tour))
                    {
                        Integer currentValue = pathCount.get(tour);
                        pathCount.replace(tour, currentValue, currentValue + 1);
                    }
                    else
                        pathCount.put(tour, 1);
            }
            updateTrails(pathCount, edge2phero);
        }

        return convergedPath(pathCount);
    }

    /**
     * Unleashes an ant from the 'src' node, hoping it will reach the 'dest' node.
     * @param src The node to unleash the ant from.
     * @param dest The node for the ant to reach.
     * @param edge2phero Topology's edges with the amount of pheromone for each.
     * @return Vector<Integer> The path traversal for this ant.
     */
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

    /**
     * Picks up the next neighbour to continue the traversal.
     * @param node The current node.
     * @param edge2phero The amount of pheromone each edge carries currently.
     * @return Integer The next neighbour.
     */
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

    /**
     * Checks node's available neighbours.
     * @param node The current node to check its available neighbours.
     * @param edge2phero The amount of pheromone each edge carries currently.
     * @return Vector<Integer> Node's neighbours collected.
     */
    private Vector<Integer> availNeighbours(int node, double[][] edge2phero)
    {
        Vector<Integer> neighbours = new Vector<>();

        for(int i = 0; i < edge2phero[node].length; ++i)
            if(edge2phero[node][i] >= 0 && i != node)
                neighbours.add(i);

        return neighbours;
    }

    /**
     * Produces a probability number to pick up node 'j' from node 'i'.
     * @param i Starting edge node.
     * @param j Ending edge node.
     * @param edge2phero The amount of pheromone each edge carries currently.
     * @return double The probability.
     * @throws IllegalArgumentException In case no available neighbours exist.
     */
    private double prob(int i, int j, double[][] edge2phero) throws IllegalArgumentException
    {
        double num = Math.pow(edge2phero[i][j], A) * Math.pow(heuInfo(i, j), B);
        double denum = 0;
        Vector<Integer> neighs = availNeighbours(i, edge2phero);
        if(neighs.size() == 0)
            throw new IllegalArgumentException("prob(..): No neighbours");

        for(int neigh : neighs)
            denum += Math.pow(edge2phero[i][neigh], A) * Math.pow(heuInfo(i, neigh), B);

        return num / denum;
    }

    /**
     * Produces the heuristic value from node 'i' from node 'j'.
     * @param i Starting edge node.
     * @param j Ending edge node.
     * @return double The heuristic info.
     */
    private double heuInfo(int i, int j)
    {
        //  Should not be in the return statement
        double distance = edge2distance.get(new Pair<Integer, Integer>(i, j));

        return 1 / distance;
    }

    /**
     * Calculates the sum of path's edge distances.
     * @param path The node sequence comprising the path.
     * @return The total path distance.
     */
    private double tourLength(Vector<Integer> path)
    {
        if(path.size() <= 1)
            return 0;

        //  The Lk value: Edge weight sum
        Iterator it = path.iterator();
        double pathSum = 0;
        int strNode = (int)it.next();
        while(it.hasNext())
        {
            int endNode = (int)it.next();
            pathSum += edge2distance.get(new Pair<Integer, Integer>(strNode, endNode));
            strNode = endNode;
        }

        return pathSum;
    }

    /**
     * Updates traversed paths pheromone levels.
     * @param evalPaths The traversed paths to update their pheromone levels.
     * @param edge2phero The amount of pheromone each edge carries currently.
     */
    private void updateTrails(Map<Vector<Integer>, Integer> evalPaths, double[][] edge2phero)
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
                edge2phero[end][str] += PHERO_QNT / tourLength(path);
                str = end;
            }
        }
    }

    /**
     * Returns the path with the higher occurrence.
     * @param pathCount A map of paths to occurrence numbers for each.
     * @return Vector<Integer> The chosen path.
     */
    Vector<Integer> convergedPath(Map<Vector<Integer>, Integer> pathCount)
    {
        //  Choose the return path
        Vector<Integer> retpath = new Vector<>();
        Integer cnt = Integer.MIN_VALUE;

        Set<Vector<Integer>> keys = pathCount.keySet();
        for(Vector<Integer> path : keys)
            if(pathCount.get(path) > cnt)
            {
                cnt = pathCount.get(path);
                retpath = path;
            }

        return retpath;
    }
}

/**
 * Typical pair record.
 * @param lhs The left item.
 * @param rhs The right item.
 * @param <L> The type of the left item.
 * @param <R> The type of the right item.
 */
record Pair<L, R>(L lhs, R rhs)
{
    /**
     * Asserts for the null case.
     * @param lhs The left hand side.
     * @param rhs The right hand side.
     */
    Pair
    {
        assert lhs != null;
        assert rhs != null;
    }

    /**
     * Returns the left item.
     * @return The left hand side.
     */
    public L getLeft()
    {
        return lhs;
    }

    /**
     * Returns the right item.
     * @return The right hand side item.
     */
    public R getRight()
    {
        return rhs;
    }

    /**
     * Hashcode method.
     * @return int The hash code.
     */
    @Override
    public int hashCode()
    {
        return lhs.hashCode() ^ rhs.hashCode();
    }

    /**
     * Checks for equality.
     * @param obj The reference object with which to compare.
     * @return true If equals.
     */
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Pair))
            return false;

        Pair pairObj = (Pair)obj;
        return this.lhs.equals(pairObj.getLeft()) && this.rhs.equals(pairObj.getRight());
    }
}
