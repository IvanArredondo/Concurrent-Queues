package q1;


import java.sql.Timestamp;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingUnboundedQueue<T> {
	
	ReentrantLock enqLock, deqLock;
	volatile Node<T> head, tail;
	
	public BlockingUnboundedQueue() {
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
		head = new Node<T>(null);
		tail = head;
	}
	
	public void enq(T x) {
		enqLock.lock();
		try {
			Node<T> e = new Node<T>(x);
			e.enterTime = new Timestamp(System.currentTimeMillis());
			tail.next = e;
			tail = e;
			//System.out.println("adding: " + x);
		}finally {
			enqLock.unlock();
		}
	}
	
	public Timestamp deq() throws Exception{
		Timestamp result = null;
		deqLock.lock();
		try {
			if(head.next == null) {
				throw new Exception();
			}
			head.next.exitTime = new Timestamp(System.currentTimeMillis());
			result = head.next.enterTime;
			head = head.next;
			if(result != null)System.out.println(result.toString());
			
		}finally {

			deqLock.unlock();
		}
		
		return result;
	}

}
