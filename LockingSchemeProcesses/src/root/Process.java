package root;

import root.operations.LockHandler;

public class Process {

	public static void main(String[] args) throws InterruptedException {
		
		System.out.println("-------------PROCESS P1-------------\n");
		LockHandler lock = new LockHandler();
		lock.threadWaits();
	}

}
