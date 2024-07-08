import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Heap {
    private int[] a;       // heap array
    private int[] hPos;    // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v
    private int N;         // heap size
   
    public Heap(int maxSize, int[] _dist, int[] _hPos) {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }

    // checking if the heap is empty
    public boolean isEmpty() {
        return N == 0;
    }

    // method to perform sift-up operation in the heap
    public void siftUp(int k) {
        int v = a[k]; // item to be moved up
        a[0] = 0;
        dist[0] = Integer.MIN_VALUE;

        // move up the heap until we find the correct position
        while (dist[v] < dist[a[k / 2]]) {
            a[k] = a[k / 2];
            hPos[a[k]] = k;
            k = k / 2;
        }

        a[k] = v;
        hPos[v] = k;
    }

    public void siftDown(int k) {
        int v, j;
        v = a[k];  
        
        // move down the heap until we find the correct position
        while (k <= N / 2) {
            j = 2 * k;
            if (j < N && dist[a[j]] > dist[a[j + 1]]) {
                j++;
            }
            if (dist[v] <= dist[a[j]]) {
                break;
            }

            a[k] = a[j];
            hPos[a[k]] = k;
            k = j;
        }

        a[k] = v;
        hPos[v] = k;
    }

    public void insert(int x) {
        // inserting an element into the heap
        a[++N] = x;
        siftUp(N);
    }

    public int remove() {   
        // removing an element from the heap
        int v = a[1];
        hPos[v] = 0; // v is no longer in heap
        a[N + 1] = 0;  // put null node into empty spot
        a[1] = a[N--];
        siftDown(1);
        return v;
    }
}

class Graph {
    class Node {
        public int vert; // vertex number
        public int wgt; // weight of the edge
        public Node next; // reference to the next node in the list
    }
    
    private int V, E; // no of vertices and edges
    private Node[] adj; // array of afjacency list
    private Node z; // sentinel node
    private int[] mst; // array to store the minimum spanning tree
    private int[] visited; // tracking the visited vertices
    
    public Graph(String graphFile) throws IOException {
        int u, v; // vertices
        int e, wgt; // edge count and weight
        Node t; // temporary node for constructing the list

        // opening a file and setting up a buffered reader to read from it
        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);
                   
        String splits = " +";  // multiple whitespace as delimiter
        String line = reader.readLine(); // read first line of file  

        String[] parts = line.split(splits); // split the line into parts 
        
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // initialising the visited array
        visited = new int[V + 1];
        for (int i = 0; i <= V; i++) {
            visited[i] = 0;
        }
        
        z = new Node(); 
        z.next = z;
        
        // initiliase the adjacency list array with sentinel nodes
        adj = new Node[V + 1];        
        for (v = 1; v <= V; ++v)
            adj[v] = z;               
        
        // looping through each edge in the file and creating a list
        for (e = 1; e <= E; ++e) {
            line = reader.readLine(); // reads next line of file
            parts = line.split(splits);
            // parse the parts as source, destination and weight
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            // new node t for the destination vertex v
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u];
            adj[u] = t;  

            // new node t for the destination vertex u
            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v];
            adj[v] = t;
        }	       
    }
   
    // method to convert an integer to it's corresponding character
    public char toChar(int u) {
        return (char)(u + 64);
    }
    
    // displaying the adjacency list
    public void display() {
        int v;
        Node n;
        
        // for loop to iterate over each vertex and its adjacency list
        for (v = 1; v <= V; ++v) {
            System.out.print("\nAdj[" + toChar(v) + "] ->" );
            for (n = adj[v]; n != z; n = n.next) 
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");    
        }
        System.out.println("");
    }

    public void MST_Prim(int s) {
        int v, u;
        int wgt, wgt_sum = 0;
        int[] dist, parent, hPos;
        Node t;
        
        // initialising arrays and variables for prim's
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];
        mst = new int[V + 1];
    
        // initialising distance, parent and heap position arrays
        for (v = 0; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = -1;
            hPos[v] = 0;
        }
        dist[s] = 0;
        
        Heap h =  new Heap(V, dist, hPos); // creating a heap
        h.insert(s); // inserting starting vertex
        
        // printing the initial state
        System.out.println("\nStep 1: Vertex " + toChar(s) + " added to MST");
    
        // performing Prim's algorithm
        int step = 1;
        while (!h.isEmpty()) {
            v = h.remove();
            dist[v] = -dist[v];
            wgt_sum += -dist[v];
    
            for (t = adj[v]; t != z; t = t.next) {
                u = t.vert;
                wgt = t.wgt;
    
                // update the distance and parent arrays if a shorter path is found
                if (wgt < dist[u]) {
                    dist[u] = wgt;
                    parent[u] = v;
    
                    // insert or update the vertex in the heap
                    if (hPos[u] == 0) {
                        h.insert(u);
                    } else {
                        h.siftUp(hPos[u]);
                    }
                }
            }
            
            // print the current state
            if (parent[v] != -1) {
                System.out.println("Step " + step + ": Edge " + toChar(parent[v]) + " -> " + toChar(v) + " added to MST");
            }
            step++;
        }
    
        // storing the MST parent array
        mst = parent; 
        // printing total weight of the MST
        System.out.print("\nTotal weight of MST = " + wgt_sum + "\n");      
    }
    
    
    // displaying the MST
    public void showMST() {
        System.out.print("\n\nMinimum Spanning tree parent array is:\n");
        for (int v = 1; v <= V; ++v)
            System.out.println(toChar(v) + " --> " + toChar(mst[v]));
        System.out.println("");
    }


    // Dijikstra's Algorithm
    public void SPT_Dijkstra(int s) {
        int v, u;
        int wgt = 0;
        int[]  dist, parent, hPos;
        Node t;
        boolean[] settled = new boolean[V + 1];

        // initalising arrays and variables
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];

        // initialising distance, parent and heap position arrays
        for (v = 0; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = -1;
            hPos[v] = 0;
        }

        dist[s] = 0;

        // creating a heap and inserting the starting vertex
        Heap h = new Heap(V, dist, hPos);
        h.insert(s);

        while (!h.isEmpty()) {
            v = h.remove();
            settled[v] = true;

            for (t = adj[v]; t != z; t = t.next) {
                u = t.vert;
                if (!settled[u] && dist[v] + t.wgt < dist[u]) {
                    dist[u] = dist[v] + t.wgt;
                    parent[u] = v;
                    if (hPos[u] == 0) {
                        h.insert(u);
                    } else {
                        h.siftUp(hPos[u]);
                    }
                }
            }
        }

        // displaying the SPT in a table
        System.out.println("Vertex\t\tDistance \t\tPathway");
        for (v = 1; v <= V; ++v) {
            if (v != s) {
                System.out.print(toChar(v) + "\t\t" + dist[v] + "\t\t");
                printPath(s, v, parent);
                System.out.println();
            }
        }
    }

    // helper method to print the shortest path from start to vertex
    private void printPath(int start, int vertex, int[] parent) {
        if (vertex == start) {
            System.out.print(toChar(start));
        } else if (parent[vertex] == -1) {
            System.out.print("--");
        } else {
            printPath(start, parent[vertex], parent);
            System.out.print(" --> " + toChar(vertex));
        }
    }

    // Depth first search
    public void DF(int v) {
        visited[v] = 1; // marking current vertex as visited
        System.out.print(toChar(v) + " "); // print current vertex 

        // for loop to iterate through the adjacency list of the current node
        for (Node t = adj[v]; t != z; t = t.next) {
            // if the adjacent vertex hasn't been visited, recursively do DFS on it
            if (visited[t.vert] == 0) {
                DF(t.vert);
            }
        }
    }


    // Breadth first search
    public void breadthFirst(int s) {
        visited = new int[V + 1]; // keeping track of visited arrays

        // initialising th queue
        int[] queue = new int[V];
        int front = -1, rear = -1;

        // marking the starting vertex as visited an enqueue it
        visited[s] = s;
        queue[++rear] = s;

        // while queue isn't emoty
        while (front != rear) {
            
            int v = queue[++front]; // dequeue a vertex from the front of the queue
            System.out.print(toChar(v) + " ");// printing the dequeued vertex

            // going through the adjacency list of the dequeued vertex
            Node n = adj[v];
            while (n != z) {
                int u = n.vert;

                // if u hasn't been visited mark it as visited and enqueue it
                if (visited[u] == 0) {
                    visited[u] = 1;
                    queue[++rear] = u;
                }
                // move to next adjacent vertex
                n = n.next;
            }
        }
    }
}

// displaying all the methods
public class GraphLists {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter the name of the text file which has the graph: ");
        String fileName = br.readLine();

        System.out.print("Enter the beginning vertex (as a number): ");
        int startingVertex = Integer.parseInt(br.readLine());

        Graph g = new Graph(fileName);

        System.out.println("\nDepth First Search beiginning from vertex " + g.toChar(startingVertex) + ":");
        g.DF(startingVertex);

        System.out.println("\n\nBreadth First Search starting from vertex " + g.toChar(startingVertex) + ":");
        g.breadthFirst(startingVertex);

        System.out.println("\n\nPrim's Minimum spanning tree beginning from vertex " + g.toChar(startingVertex) + ":");
        g.MST_Prim(startingVertex);
        g.showMST();

        System.out.println("\nDijkstra's SPT beginning from vertex " + g.toChar(startingVertex) + ":");
        g.SPT_Dijkstra(startingVertex);
    }
}
