package project;

import java.util.Random;

public class InitialiseCore implements Runnable{
	
	int [][] A; 
	
    int start, N;
    
    private int randomMin = -128;
	private int randomMax = 128;

	
    public InitialiseCore(int[][] a, int s, int n)
    {
    	A=a;     	
    	start=s;
    	N=n;
    }

    public void run()
    {
    	int Cols = A[0].length;    	
    	
    	for(int i = start;i<N+start && i<Cols;i++) {
    		for(int j=0;j<Cols;j++) {
		    	A[i][j] = getRandomInteger(randomMin,randomMax);
		    }
		}	
    }
    
   
	public static int getRandomInteger(int minimum, int maximum){ 
		Random r = new Random();
		return r.nextInt((maximum - minimum) + 1) + minimum;		
	}

    
}
