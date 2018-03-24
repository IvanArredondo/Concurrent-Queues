package q1;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

	static int p;
	static int q;
	static int n;

	static AtomicInteger uniqueId;
	static AtomicBoolean continueOps;
	static AtomicInteger deqCounter;

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
						blockingUnboundedQueue.deq();
						if(++counter == 100) {
							continueDeq = false;
							System.out.println(" the counter is: " + counter);
						}
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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


	}

}
