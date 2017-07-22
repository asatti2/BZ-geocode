package com.ops.utils.dijakstra;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class TestDemo
{
    private int numberOfNodes;
    private Stack<Integer> stack;
 
    public TestDemo()
    {
        stack = new Stack<Integer>();
    }
 
    public void tsp(int adjacencyMatrix[][])
    {
        numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(1 + "\t");
 
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
                System.out.print(dst + "\t");
                minFlag = false;
                continue;
            }
            stack.pop();
        }
    }
 
    public static void main(String... arg)
    {
        int number_of_nodes;
        Scanner scanner = null;
        try
        {
            System.out.println("Enter the number of nodes in the graph");
            scanner = new Scanner(System.in);
            number_of_nodes = scanner.nextInt();
            int adjacency_matrix[][] = new int[number_of_nodes + 1][number_of_nodes + 1];
            System.out.println("Enter the adjacency matrix");
            for (int i = 1; i <= number_of_nodes; i++)
            {
                for (int j = 1; j <= number_of_nodes; j++)
                {
                    adjacency_matrix[i][j] = scanner.nextInt();
                }
            }
            for (int i = 1; i <= number_of_nodes; i++)
            {
                for (int j = 1; j <= number_of_nodes; j++)
                {
                    if (adjacency_matrix[i][j] == 1 && adjacency_matrix[j][i] == 0)
                    {
                        adjacency_matrix[j][i] = 1;
                    }
                    
                }
            }
            System.out.println("the citys are visited as follows");
            TestDemo tspNearestNeighbour = new TestDemo();
            tspNearestNeighbour.tsp(adjacency_matrix);
        } catch (InputMismatchException inputMismatch)
         {
             System.out.println("Wrong Input format");
         }
        scanner.close();
    }
}