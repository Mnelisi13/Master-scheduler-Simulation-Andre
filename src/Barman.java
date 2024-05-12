
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/*
 Barman Thread class.
 */

public class Barman extends Thread {
	

	private CountDownLatch startSignal;
	private BlockingQueue<DrinkOrder> orderQueue;
	private int patrons = 0;
    private int totalTime =0;
	private HashSet<Integer> set;
	Barman(  CountDownLatch startSignal,int schedAlg) {
		if (schedAlg==0)
			this.orderQueue = new LinkedBlockingQueue<>();
		//FIX below
		else {System.out.println("This is the SJF Scheduler!!");
			this.orderQueue = new PriorityBlockingQueue<>(10, Comparator.comparingInt((DrinkOrder o) -> o.getExecutionTime()));}
		
	    this.startSignal=startSignal;
	}
	
	public void placeDrinkOrder(DrinkOrder order) throws InterruptedException {
        orderQueue.put(order);
    }
	public void run() {
		try {
			DrinkOrder nextOrder;
			set = new HashSet<>();
			startSignal.countDown(); //barman ready
			startSignal.await(); //check latch - don't start until told to do so
			long startTime = System.currentTimeMillis(); //serving start time.

			while(true) {
				nextOrder=orderQueue.take(); //orders
				set.add(nextOrder.getOrderer());
				System.out.println("---Barman preparing order for patron "+ nextOrder.toString());
				sleep(nextOrder.getExecutionTime()); //processing order
				System.out.println("---Barman has made order for patron "+ nextOrder.toString());
				nextOrder.orderDone();
				//long currTime = System.currentTimeMillis();
				long currentTime = System.currentTimeMillis();
                if ((currentTime - startTime) / 1000 % 1 == 0) { // Check every 20 seconds
                    long elapsedTime = currentTime - startTime;
                    double throughput = (double) set.size();
                    System.out.println("Throughput: " + throughput + " patrons served per seconds!, Elapsed time: "+elapsedTime/1000.0+"s.");
					set.clear();
                }
			}
		} catch (InterruptedException e1) { 
			System.out.println("---Barman is packing up ");
		}
	}
}


