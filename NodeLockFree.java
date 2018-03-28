package q1;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicReference;

public class NodeLockFree<T>{
	
	T value;
	AtomicReference<NodeLockFree<T>> next;
	long enterTime;
	long exitTime;
	
	public NodeLockFree(T value2) {
		this.value = value2;
		next = new AtomicReference<NodeLockFree<T>>(null);
	}

}
