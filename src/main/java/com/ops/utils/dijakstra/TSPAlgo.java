package com.ops.utils.dijakstra;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TSPAlgo
{
    private int numberOfNodes;
    private Stack<Integer> stack;
 
    public TSPAlgo()
    {
        stack = new Stack<Integer>();
    }
 
    public List<Integer> tsp(int adjacencyMatrix[][]) {
    	
    	List<Integer> vIndexes = new ArrayList<Integer>();
        numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
        vIndexes.add(1);
 
        while (!stack.isEmpty())
        {
            element = stack.peek();
            min = Integer.MAX_VALUE;
            for (int u = numberOfNodes; u>-1; u--)
            {           	
            	
                if (adjacencyMatrix[element][u] > -1 && visited[u] == 0)
                {
                    if (min > adjacencyMatrix[element][u])
                    {
                        min = adjacencyMatrix[element][u];
                        dst = u;
                        minFlag = true;
                    }
                } 
                            
            }
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
                vIndexes.add(dst);
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        
        return vIndexes;
    }
    
    public List<Integer> applyTsp(int[][] adjacencyMatrix){  
    	
    	TSPAlgo tspNearestNeighbour = new TSPAlgo();
        return tspNearestNeighbour.tsp(adjacencyMatrix);
    	
    }
}
