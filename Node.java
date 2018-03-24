package q1;

import java.sql.Timestamp;

public class Node<T> {
	T value;
	volatile Node<T> next;
	Timestamp enterTime;
	Timestamp exitTime;
	
	public Node(T value) {
		this.value = value;
		next = null;
	}

}
