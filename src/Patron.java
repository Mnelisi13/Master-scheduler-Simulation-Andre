//M. M. Kuttel 2024 mkuttel@gmail.com

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/*
 This is the basicclass, representing the patrons at the bar
 */
  
public class Patron extends Thread {
	
	private Random random = new Random();// for variation in Patron behaviour

	private CountDownLatch startSignal; //all start at once, actually shared
	private Barman theBarman; //the Barman is actually shared though

	private int ID; //thread ID 
	private int lengthOfOrder;
	private long startTime, endTime, arrivalTime, waitingTime; //for all the metrics
	public static FileWriter fileW;
	private long drinkStartTime;
	private long drinkEndTime;
    private long executionTimeSum;
	private DrinkOrder [] drinksOrder;
	
	Patron( int ID,  CountDownLatch startSignal, Barman aBarman) {
		this.ID=ID;
		this.startSignal=startSignal;
		this.theBarman=aBarman;
		this.lengthOfOrder=random.nextInt(5)+1;//between 1 and 5 drinks
		drinksOrder=new DrinkOrder[lengthOfOrder];
	}
	
	public  void writeToFile(String data) throws IOException {
	    synchronized (fileW) {
	    	fileW.write(data);
	    }
	}
	
	public void run() {
		try {
			//Do NOT change the block of code below - this is the arrival times
			startSignal.countDown(); //this patron is ready
			startSignal.await(); //wait till everyone is ready
	        arrivalTime = random.nextInt(300)+ID*100;  // patrons arrive gradually later
	        sleep(arrivalTime);// Patrons arrive at staggered  times depending on ID 
			System.out.println("thirsty Patron "+ this.ID +" arrived");
			//END do not change
			
	        //create drinks order
	        for(int i=0;i<lengthOfOrder;i++) {
	        	drinksOrder[i]=new DrinkOrder(this.ID);
				/*if (i == 0) {
                    firstDrinkStartTime = System.currentTimeMillis(); // Record start time of first drink order
                }*/
	        }
			System.out.println("Patron "+ this.ID + " submitting order of " + lengthOfOrder +" drinks"); //output in standard format  - do not change this
	        startTime = System.currentTimeMillis();//started placing orders
			for(int i=0;i<lengthOfOrder;i++) {
				System.out.println("Order placed by " + drinksOrder[i].toString());
				if(i==0){
					drinkStartTime =System.currentTimeMillis();
					theBarman.placeDrinkOrder(drinksOrder[i]);
					drinksOrder[0].waitForOrder();
			drinkEndTime=System.currentTimeMillis();
				}
				theBarman.placeDrinkOrder(drinksOrder[i]);
				
			}
			
			executionTimeSum += drinksOrder[0].getExecutionTime();

			for(int i=1;i<lengthOfOrder;i++) {
				drinksOrder[i].waitForOrder();
				executionTimeSum += drinksOrder[i].getExecutionTime();
				
			}

			endTime = System.currentTimeMillis(); 
			long totalTime = endTime - startTime; // TurnAround time?
			waitingTime = totalTime-executionTimeSum;
			System.out.println("response time for "+this.ID+" is "+(drinkEndTime-drinkStartTime));
		    System.out.println("waiting time for patron :"+this.ID+" is "+waitingTime+"\n");
			writeToFile( String.format("%d,%d,%d,%d,%d\n",ID,arrivalTime,totalTime,waitingTime,(drinkEndTime-drinkStartTime)));
			System.out.println("Patron "+ this.ID + " got order in " + totalTime);
		} catch (InterruptedException e1) {//do nothing
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace(); 
		}
}
}
	

