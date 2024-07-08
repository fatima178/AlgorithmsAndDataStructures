import java.io.*;

// Class to represent an edge in the graph
class Edge {
    public int start, end, weight;

    // default constructor
    public Edge() {
        this.start = 0;
        this.end = 0;
        this.weight = 0;
    }

    public Edge(int start, int end, int weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    // display the edge
    public void show() {
        System.out.print("Edge " + toChar(start) + " - " + weight + " - " + toChar(end) + "\n");
    }

    // method to convert vertex into char for pretty printing
    private char toChar(int vertex) {
        return (char) (vertex + 64); // assuming vertices are represented as integers starting from 1
    }
}

// class representing a heap data struct
class Heap {
    private int[] heapArray;
    int heapSize, maxSize;
    Edge[] edgeArray;

    // constructor for heap
    public Heap(int _maxSize, Edge[] _edgeArray) {
        int i;
        maxSize = heapSize = _maxSize;
        heapArray = new int[heapSize + 1];
        edgeArray = _edgeArray;

        // filling the heap array with indices of edgeArray[] array
        for (i = 0; i <= heapSize; ++i)
            heapArray[i] = i;

        // Convert heapArray[] into a heap from the bottom up
        for (i = heapSize / 2; i > 0; --i)
            siftDown(i);
    }

    // perform sift down operation
    private void siftDown(int k) {
        int e, j;

        e = heapArray[k];
        while (k <= heapSize / 2) {
            j = 2 * k;

            if (j < heapSize && edgeArray[heapArray[j]].weight > edgeArray[heapArray[j + 1]].weight) {
                ++j;
            }

            if (edgeArray[e].weight <= edgeArray[heapArray[j]].weight) {
                break;
            }
            heapArray[k] = heapArray[j];
            k = j;
        }
        heapArray[k] = e;
    }

    // method to remove an element from the heap
    public int remove() {
        heapArray[0] = heapArray[1];
        heapArray[1] = heapArray[heapSize--];
        siftDown(1);
        return heapArray[0];
    }
}

// class representing union-find sets for Kruskal's algorithm
class UnionFindSets {
    private int[] parent;
    private int size;

    // constructor for UnionFindSets
    public UnionFindSets(int numVertices) {
        size = numVertices;
        parent = new int[numVertices + 1];

        // Initialising each vertex as a separate set
        for (int i = 1; i <= size; ++i) {
            parent[i] = i;
        }
    }

    // finding the set to which a vertex belongs
    public int findSet(int vertex) {
        if (vertex != parent[vertex]) {
            
            vertex = findSet(parent[vertex]); // recursively find the parent of the set
        }
        return vertex;
    }

    // perform union operation
    public void union(int set1, int set2) {
        parent[findSet(set1)] = findSet(set2); // make one set a child of the other
    }

    public void showTrees() {
        int i;
        for (i = 1; i <= size; ++i)
            System.out.print(toChar(i) + "->" + toChar(parent[i]) + "  ");
        System.out.print("\n");
    }

    // display the sets formed by the union-find sets
    public void showSets() {
        int vertex, root;
        int[] shown = new int[size + 1];
        for (vertex = 1; vertex <= size; ++vertex) {
            root = findSet(vertex);
            if (shown[root] != 1) {
                showSet(root);
                shown[root] = 1;
            }
        }
        System.out.print("\n");
    }

    // helper method to display a single set
    private void showSet(int root) {
        int vertex;
        System.out.print("Set: {");
        for (vertex = 1; vertex <= size; ++vertex)
            if (findSet(vertex) == root)
                System.out.print(toChar(vertex) + " ");
        System.out.print("}  ");
    }

    // convert vertex into char for pretty printing
    private char toChar(int vertex) {
        return (char) (vertex + 64); // assuming vertices are represented as integers starting from 1
    }
}

// class representing the graph and doing Kruskal's algorithm
class Graph {
    private int numVertices, numEdges;
    private Edge[] edges;
    private Edge[] mst;
    private int totalWeight; // store the total weight of the MST

    // constructor for the graph
    public Graph(String graphFile) throws IOException {
        int start, end;
        int weight, edgeIndex;

        // Read the graph from a text file
        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +";  // multiple whitespace as delimiter
        String line = reader.readLine();
        String[] parts = line.split(splits);

        // extract number of vertices and edges from the first line
        numVertices = Integer.parseInt(parts[0]);
        numEdges = Integer.parseInt(parts[1]);

        // edge array
        edges = new Edge[numEdges + 1];

        // reading the edges from the file
        for (edgeIndex = 1; edgeIndex <= numEdges; ++edgeIndex) {
            line = reader.readLine();
            parts = line.split(splits);
            start = Integer.parseInt(parts[0]);
            end = Integer.parseInt(parts[1]);
            weight = Integer.parseInt(parts[2]);

            // display the edge being read
            System.out.println("Edge " + toChar(start) + "--(" + weight + ")--" + toChar(end));

            // create an Edge object and add it to the edge array
            edges[edgeIndex] = new Edge(start, end, weight);
        }

        totalWeight = 0; // initialising total weight to 0
    }

    // method to find the minimum spanning tree
    public Edge[] findMST() {
        int edgeIndex, mstIndex = 0;
        Edge currentEdge;
        int uSet, vSet;
        UnionFindSets partition = new UnionFindSets(numVertices);

        // create edge array to store MST - initially it has no edges.
        mst = new Edge[numVertices - 1];

        // priority queue for indices of array of edges
        Heap h = new Heap(numEdges, edges);

        System.out.println("\nSteps of Kruskal:" );

        // create partition of singleton sets for the vertices
        while (mstIndex < numVertices - 1) {
            edgeIndex = h.remove();
            currentEdge = edges[edgeIndex];
            uSet = partition.findSet(currentEdge.start);
            vSet = partition.findSet(currentEdge.end);

            if (uSet != vSet) {
                // accept the edge
                mst[mstIndex++] = currentEdge;
                partition.union(uSet, vSet);
            }

            // print the sets formed after processing each edge
            System.out.println("\n" + toChar(currentEdge.start) + "-" + currentEdge.weight + "-" + toChar(currentEdge.end));
            partition.showSets();
        }

        // calculate total weight of the MST
        for (int e = 0; e < numVertices - 1; ++e) {
            totalWeight += mst[e].weight;
        }

        return mst;
    }

    // convert vertex into char for pretty printing
    private char toChar(int vertex) {
        return (char) (vertex + 64); // assuming vertices are represented as integers starting from 1
    }

    // display the minimum spanning tree
    public void showMST() {
        System.out.print("\nMinimum spanning tree from following edges:\n");
        for (int e = 0; e < numVertices - 1; ++e) {
            mst[e].show();
        }
        System.out.println("\nWeight of MST: " + totalWeight);
    }
}

// test code
class KruskalTrees {
    public static void main(String[] args) throws IOException {
        String fileName = "wGraph1.txt";

        // create a graph object
        Graph graph = new Graph(fileName);

        graph.findMST();

        graph.showMST();
    }
}
