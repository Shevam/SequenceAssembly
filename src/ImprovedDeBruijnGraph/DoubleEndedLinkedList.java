package improvedDeBruijnGraph;
import java.util.LinkedList;

public class DoubleEndedLinkedList {

	DirectedEdge head, tail;
	
	public DoubleEndedLinkedList() { head = tail = null; }
	
	public boolean isEmpty() { return head == null; }
	
	public DirectedEdge getHead() { return head; }
	
	public DirectedEdge getTail() { return tail; }

	public void insert(DirectedEdge newEdge) {
		if (isEmpty()) {
			head = tail = newEdge;
		} else {
			tail.setNext(newEdge);
			tail = newEdge;
		}
	}
	
	public DirectedEdge getEdgeTo(Node endNode) {
		DirectedEdge current = head;
		while (current != null && current.getEnd() != endNode) {
			current = current.getNext();
		}
		return current;
	}
	
	public DirectedEdge remove(DirectedEdge edgeToRemove) {
		// TODO test if to be used
		if (isEmpty()) {
			return null;
		}
		
		if (head == edgeToRemove) {
			head = head.getNext();
			if (tail == edgeToRemove) {
				tail = null;
			}
			return edgeToRemove;
		}
		
		DirectedEdge current = head;
		DirectedEdge previous = current;
		while (current != null && current != edgeToRemove) {
			previous = current;
			current = current.getNext();
		}
		
		if (current == null) {
			return null;
		}
		
		if (tail == current) {
			tail = previous;
		}
		
		previous.setNext(current.getNext());
		return current;
	}
	
	public LinkedList<DirectedEdge> toLinkedList() {
		DirectedEdge current = head;
		LinkedList<DirectedEdge> edgeList = new LinkedList<DirectedEdge>();
		while (current != null) {
			edgeList.add(current);
			current = current.getNext();
		}
		return edgeList;
	}
}
