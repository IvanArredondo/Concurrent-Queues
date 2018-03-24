package q1;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicReference;

public class NodeLockFree<T> {
	
	T value;
	AtomicReference<NodeLockFree<T>> next;
	Timestamp enterTime;
	Timestamp exitTime;
	
	public NodeLockFree(T value) {
		this.value = value;
		next = new AtomicReference<NodeLockFree<T>>(null);
	}

}
