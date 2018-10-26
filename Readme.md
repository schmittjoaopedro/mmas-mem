# A MAX-MIN Ant System with Short-term Memory Applied to the Dynamic and Asymmetric Traveling Salesman Problem

It's proposed here a Max-Min Ant System with Memory algorithm, named as MMAS_MEM. That is a version of the well-know Max-Min Ant System (MMAS) proposed by Stutzle (1997) with memory usage, where memory usage is inspired by Mavrovouniotis (2013). 

Besides that, in this study, we are applying the algorithm over dynamic and asymmetric traveling salesman problem with moving vehicle. The problem dynamics is inspired by traffic factors used in Mavrovouniotis (2013).

This study was presented in the 7th Brazilian Conference on Intelligent Systems (BRACIS 2018). For further information, the paper is available at (#link) and the presentation video is available below:

<a href="http://www.youtube.com/watch?feature=player_embedded&v=znOOfW4WXyE" target="_blank">
    <img alt="MMAS_MEM for ADTSP in Java" src="https://img.youtube.com/vi/znOOfW4WXyE/0.jpg" style="width:100%;height:100%;" />
</a>

## Quick reference

This study is motivated by the growth in logistic complexity caused by cities evolution. Based on this, from the logistic literature, the route planning systems are used to support product delivery for clients and are the main focus of this study. Besides that, in the real world, we have different sources of dynamism that can influence the good working of the routing planning systems. In this study, we are interested in tackle how traffic factors (caused by traffic jams) and moving vehicle can influence the routing planning systems.

The main problem approached by this algorithm is basically: how to execute vehicle routing in city environments considering traffic dynamism, cyclic periods and moving vehicle.

To approach this problem a study about memory usage in the Max-Min Ant System was conducted too. Besides that, to validate the proposed algorithm, a comparison was tackled with the canonical version of MMAS (Stutzle, 1997) and with the EIACO (Mavrovouniotis, 2013) in benchmarks instances from TSPLIB and with real instances generated from the Joinville, Brazil.

## Software organization

The following items describe a macro view of the system:

* The folder "maps" have files related to the TSP test instances and the real instance use in this study.
* Folder "notes" have R scripts used to generate the plots.
* Folder "output" have the obtained results.
* Folder "src" have the Java source code.
* Folder "tsp" have some other tsp instances.
* Program files describes different instances used to make the tests.
* The "Run.java" file is the start point to init the algorithm.

## References

Stutzle, T., & Hoos, H. (1997). MAX MIN Ant System and Local Search for the Traveling Salesman Problem. Ieee International Conference on Evolutionary Computation (Icec’97), 309–314. https://doi.org/10.1109/ICEC.1997.592327

Mavrovouniotis, M., & Yang, S. (2013). Ant colony optimization with immigrants schemes for the dynamic travelling salesman problem with traffic factors. Applied Soft Computing Journal. https://doi.org/10.1016/j.asoc.2013.05.022