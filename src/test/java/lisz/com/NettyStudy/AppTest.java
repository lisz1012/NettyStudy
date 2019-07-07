package lisz.com.NettyStudy;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase{
    public void test1() {
    	Queue<Cell> heap = new PriorityQueue<>(new MyComparator());
    	for (int i = 0; i < 10; i++) {
    		heap.offer(new Cell(i, 8));
		}
    	while (!heap.isEmpty()) {
			System.out.print(heap.poll());
		}
    }
}

class Cell {
	int id;
	int val;
	public Cell(int id, int val) {
		this.id = id;
		this.val = val;
	}
	@Override
	public String toString() {
		return id + " ";
	}
}

class MyComparator implements Comparator<Cell> {
	@Override
	public int compare(Cell o1, Cell o2) {
		if (o1.val < o2.val) {
			return -1;
		} else if (o1.val > o2.val) {
			return 1;
		}
		return 0;
	}
	
}