package project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;


@SpringBootApplication
public class Application implements CommandLineRunner {
	
	protected final Log logger = LogFactory.getLog(getClass());	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		int [][] X = null;
		int [][] Y = null;		
		StopWatch sw = new org.springframework.util.StopWatch();//fully qualified name		
		if(args.length > 1) {
			logger.info("Processing");			
			int rowColArg = (int) Math.pow(2, Integer.valueOf(args[1]));
			logger.info("number of rows and column "+rowColArg);
			X = initialiseMatrices(rowColArg, 4);
			Y = initialiseMatrices(rowColArg, 4);
		}else {
			logger.info("Error no parameter");
			return;
		}
		
		sw.start("Simple Matrix"); 
		simpleMultiplyMatrices(X,Y);
		sw.stop();
		
		sw.start("Multi Thread Matrix"); 
		multiMultiplyMatrices(X,Y,4);
		sw.stop();
		
		sw.start("Custom Parallel Thread Matrix"); 
		customMultiplyMatrices(X,Y,4);
		sw.stop();
		
		
		showstatus(sw);
		
	}
	
	
	private void showstatus(StopWatch sw) {
		TaskInfo[] listofTasks = sw.getTaskInfo();
		System.out.println("Total time in milliseconds for all tasks :\n"+sw.getTotalTimeMillis());
		for (TaskInfo task : listofTasks) {
		    System.out.format("[%s]:[%d]\n", 
		            task.getTaskName(), task.getTimeMillis());
		}
		
	}

	public int[][] initialiseMatrices(int M, int cores) {
		int[][] matrix = new int[M][M];
		try {			
			Thread[] Ts = new Thread[cores];
			int Rs = matrix.length/cores;
			for(int w=0;w<cores;w++)
			    {
				int Ra = Rs;
				if (w==cores-1)
				    Ra += matrix.length%cores;// ra=ra+matrix.length%cores
				InitialiseCore wc = new InitialiseCore(matrix,Rs*w,Ra);
				Ts[w] = new Thread(wc);
				Ts[w].start();
			    }
			for(int w=0;w<cores;w++) Ts[w].join();
		    } 
			catch (InterruptedException ie) {
				System.out.println(ie); 
				System.exit(1);
			}		    
		return matrix ;
    }

	public int[][] simpleMultiplyMatrices(int[][] X, int[][] Y) {
        int[][] product = new int[X.length][X.length];
        for(int i = 0; i < X.length; i++) {
            for (int j = 0; j < X.length; j++) {
                for (int k = 0; k < X.length; k++) {
                    product[i][j] += X[i][k] * Y[k][j] - 2 * X[i][k] * Y[k][j];
                }
            }
        }

        return product;
    }
	
	public int[][] multiMultiplyMatrices(int[][] matrixX, int[][] matrixY, int cores) {
		int[][] product = new int[matrixX.length][matrixX.length];
		try {
			// create worker threads
			Thread[] Ts = new Thread[cores]; // keep track of threads created.
			int Rs = matrixX.length/cores; // number of rows to assign to each thread
			for(int w=0;w<cores;w++)
			    {
				int Ra = Rs; // rows assigned 
				if (w==cores-1) // last thread may get more rows
				    Ra += matrixX.length%cores;
				MultiThread wc = new MultiThread(matrixX,matrixY,product,Rs*w,Ra);
				Ts[w] = new Thread(wc);
				Ts[w].start(); // start right away
			    }
			for(int w=0;w<cores;w++) Ts[w].join(); // wait for each to finish
		    } 
			catch (InterruptedException ie) {
				System.out.println(ie); 
				System.exit(1);
			}		    
		return product ;
    }
	
	public int[][] customMultiplyMatrices(int[][] matrixA, int[][] matrixB, int cores) {
		
		int[][] matrix = new int[matrixA.length][matrixB.length];
		int size = matrixA.length;
        int blockSize = matrixB.length < cores ? matrixB.length : matrixB.length / cores;
		
        
        Thread[] pool = new Thread[cores];

        class ThreadJob extends Thread {
            private int i0;

            ThreadJob(int i0) {
                this.i0 = i0;
            }

            public void run() {
                int sum;

                for (int j0 = 0; j0 < size; j0 += blockSize) {
                    for (int k0 = 0; k0 < size; k0 += blockSize) {
                        for (int i = i0; i < Math.min(i0 + blockSize, size); i++) {
                            for (int j = j0; j < Math.min(j0 + blockSize, size); j++) {
                                sum = 0;
                                for (int k = k0; k < Math.min(k0 + blockSize, size); k++) {
                                    sum += matrixA[i][k] * matrixB[j][k] - 2 * matrixA[i][k] * matrixB[k][j];
                                }
                                matrix[i][j] = sum;
                            }
                        }
                    }
                }
            }
        }
        int i = 0;
        System.out.println("size : "+size+":::"+blockSize);
        for (int i0 = 0; i0 < size; i0 += blockSize) {
            Thread thread = new ThreadJob(i0);
            pool[i] = thread;
            i++;
            thread.start(); 
        }
        int numOfThreads = i;
        System.out.println("numOfThreads : "+numOfThreads);
        for (i = 0; i < numOfThreads; i++) {
            try {
            	pool[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return matrix;
    }
	
	

}

