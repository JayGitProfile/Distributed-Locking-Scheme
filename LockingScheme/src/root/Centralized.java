package root;

import root.operations.Orchestrator;

public class Centralized {

	public static void main(String[] args) throws InterruptedException {

		System.out.println("-------------CENTRALIZED LOCKING SCHEME-------------\n");
		Orchestrator orchestrator = new Orchestrator();
		orchestrator.threadWaits();
		
	}
}