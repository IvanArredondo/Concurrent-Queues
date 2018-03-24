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
			e.enterTime = System.currentTimeMillis();
			tail.next = e;
			tail = e;
			//System.out.println("enq " + x);
		}finally {
			enqLock.unlock();
		}
	}
	
	public Node<T> deq() throws Exception{
		Node<T> result;
		deqLock.lock();
		try {
			if(head.next == null) {
				throw new Exception();
			}
			head.next.exitTime = System.currentTimeMillis();
			result = head.next;
			head = head.next;
			//if(result != null)System.out.println("" + result.enterTime);
			
		}finally {

			deqLock.unlock();
		}
		
		return result;
	}

}
