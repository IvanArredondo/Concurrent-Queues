package q1;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean; 
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

	static int p;
	static int q;
	static int n;

	static AtomicInteger uniqueId;
	static AtomicBoolean continueOps;
	static AtomicInteger deqCounter;

	static ArrayList<Node> deqListOfNodes;

	static BlockingUnboundedQueue<Integer> blockingUnboundedQueue;
	//static LockFreeUnboundedQueue<Integer> lockFreeUnboundedQueue;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			if(args.length < 3) {
				throw new Exception("Missing arguments, only "+args.length+" were specified!");
			}

			p = Integer.parseInt(args[0]);
			q = Integer.parseInt(args[1]);
			n = Integer.parseInt(args[2]);
			uniqueId = new AtomicInteger(0);
			continueOps = new AtomicBoolean(true);
			deqCounter = new AtomicInteger(0);
			blockingUnboundedQueue = new BlockingUnboundedQueue<>();

			deqListOfNodes = new ArrayList<>();

		}catch(Exception e) {
			e.printStackTrace();
		}

		Runnable enqItemsRunnable  = new Runnable() {

			@Override
			public void run() {

				while(continueOps.get()) {
					blockingUnboundedQueue.enq(uniqueId.getAndIncrement());
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};

		Runnable deqItemsRunnable = new Runnable() {

			@Override
			public void run() {

				int counter = 0;
				boolean continueDeq = true;

				while(continueDeq) {
					try {
						//System.out.println("" + blockingUnboundedQueue.deq().toString());
						deqListOfNodes.add(blockingUnboundedQueue.deq());
						if(++counter == 100) {
							continueDeq = false;
							//System.out.println(" the counter is: " + counter);
						}
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (Exception e) {
						//System.out.println("starved");
					}
				}

			}
		};

		Thread[] enqThreads = new Thread[p];
		for (int i = 0; i < p; i++) {
			enqThreads[i] = new Thread(enqItemsRunnable);
			//System.out.println("starting a thread");
			enqThreads[i].start();
		}

		Thread[] deqThreads = new Thread[q];
		for (int i = 0; i < q; i++) {
			deqThreads[i] = new Thread(deqItemsRunnable);
			deqThreads[i].start();
		}



		for (Thread thread : deqThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		continueOps.set(false);

		for (Thread thread : enqThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		ArrayList<Node> enqListOfNodes = deqListOfNodes;
		
		//first we sort two arrays of the same nodes, one of them sorted by enter time and the other by exit time
		enqListOfNodes.sort((o1, o2) -> Long.compare(o1.enterTime, o2.enterTime));
		deqListOfNodes.sort((o1, o2) -> Long.compare(o1.exitTime, o2.exitTime));
		int counter = 0;
		boolean next = true;
		
		//we iterate throug the enq list comparing to the deq list and incrementing the index of the deq list so we only traverse each list once.
		for (Node node : enqListOfNodes) {
			while(next) {
				if(node.enterTime <= deqListOfNodes.get(counter).exitTime) {
					System.out.println("enq " + node.value + " " + node.enterTime);
					next = false;
				}else {
					next = true;
					System.out.println("deq " + deqListOfNodes.get(counter).value + " " + deqListOfNodes.get(counter).exitTime);
					counter++;
				}
			}
			next = true;
			//System.out.println("" + node.exitTime);
		}


	}

}
