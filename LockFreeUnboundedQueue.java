package q1;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeUnboundedQueue<T>{

	AtomicReference<NodeLockFree<T>> head;
	AtomicReference<NodeLockFree<T>> tail;

	public LockFreeUnboundedQueue(){
		NodeLockFree<T> sentinel = new NodeLockFree<T>(null);
		head = new AtomicReference<NodeLockFree<T>>(sentinel);
		tail = new AtomicReference<NodeLockFree<T>>(sentinel);
	}

	public void enq(T value){
		NodeLockFree<T> node = new NodeLockFree<T>(value);
		while(true) {
			NodeLockFree<T> last = tail.get();
			NodeLockFree<T> next = last.next.get();
			if(last == tail.get()) {
				if(next == null) {
					node.enterTime = System.currentTimeMillis();
					if(last.next.compareAndSet(next, node)) {
						tail.compareAndSet(last, node);
						return;
					}
				}else {
					tail.compareAndSet(last, next);
				}
			}
		}
	}

	public NodeLockFree<T> deq() throws Exception{
		while(true) {
			NodeLockFree<T> first = head.get();
			NodeLockFree<T> last = tail.get();
			NodeLockFree<T> next = first.next.get();
			if(first == head.get()) {
				if(first == last) {
					if(next == null) {
						throw new Exception();
					}
					tail.compareAndSet(last, next);
				} else {
					//T value = next.value;	//changed method to return node instead
					next.exitTime = System.currentTimeMillis();
					if(head.compareAndSet(first, next)) {	//its weird that we're updating the head with the node which we're supposedly dequeing since we're getting its value in the pervious line
						return next;
					}
				}
			}
		}
	}

}
