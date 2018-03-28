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
	static ArrayList<Node> enqListOfNodes;
	static ArrayList<NodeLockFree> deqListOfNodesLockFree;
	static ArrayList<NodeLockFree> enqListOfNodesLockFree;

	static BlockingUnboundedQueue<Integer> blockingUnboundedQueue;
	static LockFreeUnboundedQueue<Integer> lockFreeUnboundedQueue;

	static boolean printOutput;

	public static void main(String[] args) {

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
			lockFreeUnboundedQueue = new LockFreeUnboundedQueue<>();

			deqListOfNodes = new ArrayList<>();
			enqListOfNodes = new ArrayList<>();
			deqListOfNodesLockFree = new ArrayList<>();
			enqListOfNodesLockFree = new ArrayList<>();

			printOutput = true;	//change this to true to see the que operations in order as per question 1a) and b)

		}catch(Exception e) {
			e.printStackTrace();
		}

		//run these one by one
		runBlockingQueueVerifier();
		//runLockFreeQueueVerifier();
	}

	private static void runBlockingQueueVerifier() {

		uniqueId.set(0);
		continueOps.set(true);
		deqCounter.set(0);
		deqListOfNodes.clear();
		enqListOfNodes.clear();

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
						deqListOfNodes.add(blockingUnboundedQueue.deq());
						//System.out.println(" the counter is: " + counter);
						if(++counter == n) {	//keeping track of how many have been dequeued
							continueDeq = false;
							break;
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

		long startTime = System.currentTimeMillis();

		Thread[] enqThreads = new Thread[p];
		for (int i = 0; i < p; i++) {
			enqThreads[i] = new Thread(enqItemsRunnable);
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

		if(printOutput) {

			for (Node node : deqListOfNodes) {
				enqListOfNodes.add(node);
			}

			//first we sort two arrays of the same nodes, one of them sorted by enter time and the other by exit time
			enqListOfNodes.sort((o1, o2) -> Long.compare(o1.enterTime, o2.enterTime));
			deqListOfNodes.sort((o1, o2) -> Long.compare(o1.exitTime, o2.exitTime));
			int counter = 0;
			boolean next = true;

			//we iterate through the enq list comparing to the deq list and incrementing the index of the deq list so we only traverse each list once.
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
			}
		}else {
			System.out.println("Blocking Queue run time: " + (System.currentTimeMillis()-startTime));
		}

	}

	private static void runLockFreeQueueVerifier() {

		uniqueId.set(0);
		continueOps.set(true);
		deqCounter.set(0);
		deqListOfNodesLockFree.clear();
		enqListOfNodesLockFree.clear();

		Runnable enqItemsRunnable  = new Runnable() {

			@Override
			public void run() {

				while(continueOps.get()) {
					lockFreeUnboundedQueue.enq(uniqueId.getAndIncrement());
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
						deqListOfNodesLockFree.add(lockFreeUnboundedQueue.deq());
						//System.out.println(" the counter is: " + counter);
						if(++counter == n) {
							continueDeq = false;
							break;
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
		
		long startTime = System.currentTimeMillis();

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

		if(printOutput) {

			for (NodeLockFree node : deqListOfNodesLockFree) {
				enqListOfNodesLockFree.add(node);
			}

			//first we sort two arrays of the same nodes, one of them sorted by enter time and the other by exit time
			enqListOfNodesLockFree.sort((o1, o2) -> Long.compare(o1.enterTime, o2.enterTime));
			deqListOfNodesLockFree.sort((o1, o2) -> Long.compare(o1.exitTime, o2.exitTime));
			int counter = 0;
			boolean next = true;

			//we iterate through the enq list comparing to the deq list and incrementing the index of the deq list so we only traverse each list once.
			for (NodeLockFree node : enqListOfNodesLockFree) {
				while(next) {
					if(node.enterTime <= deqListOfNodesLockFree.get(counter).exitTime) {
						System.out.println("enq " + node.value + " " + node.enterTime);
						next = false;
					}else {
						next = true;
						System.out.println("deq " + deqListOfNodesLockFree.get(counter).value + " " + deqListOfNodesLockFree.get(counter).exitTime);
						counter++;
					}
				}
				next = true;
				//System.out.println("" + node.exitTime);
			}
		}else {
			System.out.println("Lock free Queue run time: " + (System.currentTimeMillis()-startTime));
		}
	}
}
