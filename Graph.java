//Charles Jackson
import java.util.*;
public class Graph<T>{
    private LinkedList<Vertex<T>> vertexes;                                     //a list of all the Vertexes
    private int NumOfWeights;                                                   //the number of different costs to travel an edge
    private int size;                                                           //how many cities there are
    private int c;                                                              //for WeightedEdge Comparable function. sets what is being compared

    public Graph(int NoW){
        vertexes =  new LinkedList<>();
        if(NoW < 0) throw new IllegalArgumentException("Cannot have a negative number of weights.");
        //else
        NumOfWeights = NoW;
        size=0;
        c=0;
    }

    private class Vertex<T>{                                                    //this is the starting vertex
        private T data;                                                         //the name of the vertex, or what is significat about it
        LinkedList<WeightedEdge<T>> connections;                                //stores all the paths from the starting vertex

        public Vertex(T d){
            data=d;
            connections= new LinkedList<>();
        }
        public void addConnection(T t){
            Vertex<T> daOne =null;
            for(Vertex v: vertexes)
                if(v.data.equals(t)) {
                    daOne = v;
                    break;
                }
            if(daOne==null ) {
                daOne=new Graph.Vertex(t);
                vertexes.add((Graph.Vertex)daOne);
                ++size;
            }
            connections.add(new WeightedEdge<T>(this, daOne,new Number[]{1}));
        }
        public void addWeightedConnection(T t, Number[] w){
            Vertex<T> daOne =null;
            for(Vertex v: vertexes)
                if(v.data.equals(t)) {
                    daOne = v;
                    break;
                }
            if(daOne==null ) {
                daOne=new Graph.Vertex(t);
                vertexes.add((Graph.Vertex)daOne);
                ++size;
            }
            connections.add(new WeightedEdge<T>(this, daOne,w));
        }

        public T getData() {
            return data;
        }

        /**
         * @param c the number indicating what kind of weight
         * @return the neighbor that is cheapest to get to according to the cth weight
         */
        private WeightedEdge<T> getCheapestNeighbor(int c){
            WeightedEdge<T> min=connections.getFirst();
            for (WeightedEdge w : connections)
                if(w.weights[c].doubleValue()<min.weights[c].doubleValue())
                    min=w;
            return min;
        }
    }

    private class WeightedEdge<T> implements Comparable<WeightedEdge<T>>{
        private Vertex<T> from;
        private Vertex<T> to;
        private Number[] weights;                                               //stores the different costs from getting from start to to

        public WeightedEdge(Vertex<T> f, Vertex<T> t, Number[] w){
            if(w.length != NumOfWeights) throw new IllegalArgumentException("Number of weights number be equal to the number of weights.");
            if(f==null || t== null) throw new IllegalArgumentException("edge must connect two vertexes");
            weights= new Number[w.length];
            for(int i = 0; i < w.length; ++i) {                                 //copy the weights
                if(w[i].doubleValue()<0) throw new IllegalArgumentException("Negatives weights not allowed.");
                weights[i] = w[i];
            }
            from=f;
            to=t;
        }
        public Number getWeight(int c){
            return weights[c];
        }

        public Vertex<T> getTo() {
            return to;
        }

        @Override
        public int compareTo(WeightedEdge<T> o) {
            return (int) (weights[c].doubleValue()-o.weights[c].doubleValue());
        }
    }
    public void addVertex(T d){
        for(Vertex<T> v:vertexes) {
            if(v.data.equals(d)) return;
        }
        vertexes.add(new Vertex<>(d));
        ++size;
    }
    public void addEdge(T s, T e){
        for (Vertex v: vertexes)
            if(v.data.equals(s))
                    v.addConnection(e);
    }
    public void addWeightedEdge(T s, T e, Number[] w){
        if(w.length != NumOfWeights) throw new IllegalArgumentException("Number of weights number be equal to the number of weights.");
        for (Vertex v: vertexes)
            if(v.data.equals(s))
                    v.addWeightedConnection(e, w);
    }

    /**
     * @param s the starting vertex
     * @param e the ending vertex
     * @param c the type of weight to use
     * @return an array of containing the path (index 0), and an array of Numbers containing the different costs for that path (index 1)
     */
    public Object[] Dijkstra(T s, T e, int c){
        this.c=c;
        Vertex start = null;
        for (Vertex v: vertexes)
            if(v.data.equals(s)) {
                start = v;
                break;
            }
        if(start == null) throw new IllegalArgumentException("Start vertex does not exist.");
        HashMap<Vertex<T>, Vertex<T>> paths = new HashMap<>();                  //<child, parent>
        Number[] smallestWeight=new Number[size];                               //stores the smallest cost for each vertex
        PriorityQueue<WeightedEdge<T>> q = new PriorityQueue<>();
        boolean[] known =new boolean[size];                                     //stores if any vertex has been known
        int i=vertexes.indexOf(start);
        known[i]=true;                                                          //marks the first vertex as known
        paths.put(start, null);                                                 //there is no path from one to itself
        smallestWeight[i]=0;                                                    //the path from start to start is zero
        for(Object edge: start.connections)                                     //add all the edges from start
            q.add((WeightedEdge)edge);
        while(!q.isEmpty()){                                                    //while there are more edges
            WeightedEdge<T> shortEdge=q.remove();                               //get the shortest edge
            int i_e=vertexes.indexOf(shortEdge.to);                             //get the index of closest vertex
            int i_s=vertexes.indexOf(shortEdge.from);                           //get index of "current" vertex
            if(known[i_e])continue;                                             //ignore stale edges
            if(smallestWeight[i_e]==null ||                                     //if this path is shorter than the previous shortest path
               smallestWeight[i_e].doubleValue() > smallestWeight[i_s].doubleValue() + shortEdge.weights[c].doubleValue()){
               smallestWeight[i_e] = smallestWeight[i_s].doubleValue() + shortEdge.weights[c].doubleValue();//update the shortest cost
               paths.put(shortEdge.to, shortEdge.from);                         //update the shortest path
            }
            known[i_s]=true;                                                    //this current path to this vertex is the shortest one
            for(WeightedEdge<T> edge: shortEdge.to.connections)                 //add all the adjacent edges
                q.add(edge);
        }
        LinkedList<T> path = new LinkedList<>();                                //store the final shortest path
        Number[] costs= new Number[NumOfWeights];                               //stores the total cost for the shortest path
        for(i=0; i<NumOfWeights; ++i)
            costs[i]=0;
        Vertex<T> p=null;
        for (Vertex v: vertexes)
            if(v.data.equals(e)) {
                p = v;
                break;
            }
        if(p==null)throw new IllegalArgumentException("End vertex is does not exist.");
        while(p!=null){
            path.addFirst(p.data);
            Vertex<T> prev=paths.get(p);
            if(prev==null && !p.data.equals(s))throw new IllegalArgumentException("End vertex is not reachable.");
            if(prev == null) break;
            for(WeightedEdge<T> w: prev.connections){
                if(w.getTo().data.equals(p.data))
                    for(i=0; i <NumOfWeights; ++i)
                        costs[i] = costs[i].doubleValue() + w.weights[i].doubleValue();
            }
            p=prev;
        }
        return new Object[]{path, costs};
    }
}