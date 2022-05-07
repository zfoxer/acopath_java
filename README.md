# AcoPath for Java: Ant Colony Optimization algorithm for the shortest path problem

<pre>
Copyright (C) 2021-2022 by Constantine Kyriakopoulos, zfox@users.sourceforge.net
Version: 1.0.1
License: GNU GPL Version 2
</pre>


## About the project

The shortest path problem is solved by many methods including heuristics that offer lower complexity in expense of accuracy. There are many use-cases where the lower accuracy is acceptable in return of lower consumption of computing resources or the ability to adapt to a constantly changing operating environment.

The basic idea of the Ant System (AS) [1, 2] is that virtual ants are exploited for finding paths with a specific property, e.g., short distance between physical nodes, in the same way nature guides physical ants. A special chemical substance is being deposited upon their path which raises the probability for other ants to follow it during subsequent traversals. When this substance concentrates in high levels on a path, all subsequent ants have higher probability to follow it and increment it as well. Evaporation takes place on paths that are less traversed. Usually, the path with the highest pheromone concentration is the shortest path. The AS emulates this nature's behaviour with satisfying results solving computational problems. When the number of virtual ants and iterations are high enough, the right paths are usually found under polynomial complexity.

This is a heuristic method, i.e., optimal results are not always feasible. According to topology's resources like the node and edge numbers, the proper numbers of iterations and virtual ants must be used. Large numbers lead to paths with higher probability of being optimal but more computational resources are consumed.


## Usage

Execute 'java -jar acopath.jar [src node] [dest node] [topology file]' where [src node] is the source node number, [dest node] is the destination node number, and [topology file] is the file of the network topology representation in JSON format.

As a client programmer, create a new instance of AntSystem in your code passing as argument a Map<Pair<Integer, Integer>, Long> instance, representing directional connections between nodes and the distacne in betweeen. Next, execute the method path(src, dest) where src is the source node and dest the destination to reach. This returns the valid path which ants converge to.

Utilises [JSON Simple](https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar) [[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)] for topology input parsing.

Developed with Oracle Java 17.0.2.


## Related work

<pre>
[1] Dorigo, M., Birattari, M. and Stutzle, T., 2006. Ant colony optimization. IEEE computational intelligence magazine, 1(4), pp. 28-39.
[2] Dorigo, M. and Stützle, T., 2019. Ant colony optimization: overview and recent advances. In Handbook of metaheuristics, pp. 311-351. Springer, Cham.
</pre>


## Changelog

<pre>
1.0.1    2022-05-08    Minor fixes.
1.0      2022-05-07    Initial public release.
</pre>
