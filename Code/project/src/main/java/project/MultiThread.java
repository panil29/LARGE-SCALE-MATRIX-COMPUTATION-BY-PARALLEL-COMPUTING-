package project;

public class MultiThread implements Runnable{
	
	int [][] A;  
	int[][] B;
	int[][] R;
    int start, N; // starting row and number of rows.
    public MultiThread(int[][] a, int[][] b, int[][] c, int s, int n)
    {
    	A=a; 
    	B=b; 
    	R=c; 
    	start=s;
    	N=n;
    }

    public void run()
    {
    	int Cols = A[0].length;
    	for(int i = start;i<N+start && i<Cols;i++) {
    		for(int j=0;j<Cols;j++)
    		{
			    R[i][j] = 0;
			    for(int k=0;k<Cols;k++)
				R[i][j] += A[i][k] * B[k][j] - 2 * A[i][k] * B[k][j];
    		}	
    	}
    }
    
}
