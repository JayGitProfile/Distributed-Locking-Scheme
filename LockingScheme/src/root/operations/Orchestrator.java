package root.operations;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Orchestrator {

	public int pid = 0;
	public String sharedFile = "myFile.txt";
	public InetAddress netAddress = InetAddress.getLoopbackAddress();
	//Ports for P0, P1, P2, P3. Specified in the compiled jars
	public int[] ports = new int[]{ /*{port1, port2, port3, port4}*/ };
	//specify ports above to execute the project manually instead of jars
	//execution fails without ports, specify same ports in LockHandler
	public volatile List<ProcessHandler> processList = new ArrayList<>();
	public int locker;
	public boolean isLocked = false;
	ServerSocket serverSocket;
	public Thread connTh = null, orderTh = null;

	public Orchestrator() {
		this.locker = 0;
		this.isLocked = false;
		try {
			File file = new File(sharedFile);
			Files.write(Paths.get(file.getAbsolutePath()), Integer.toString(pid).getBytes());

			connTh = (new Thread(){
				@Override
				public void run(){
					connections();
					return;
				}
			});
			connTh.start();

			orderTh = (new Thread(){
				@Override
				public void run(){
					lockOrder();
					return;
				}
			});
			orderTh.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void threadWaits() throws InterruptedException {
		if(connTh != null)	connTh.join();

		if(orderTh != null)	orderTh.join();
	}

	public void connections(){
		try {
			serverSocket = new ServerSocket(ports[pid], 0, netAddress);
			while(true){
				Socket clientSocket = serverSocket.accept();
				ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
				ProcessHandler event = (ProcessHandler)input.readObject();
				clientSocket.close();
				(new Thread(){
					@Override
					public void run(){
						lock(event);
						return;
					}
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void lock(ProcessHandler event){
		try {	
			if(event.isLockFree()){
				isLocked = false;
				locker = pid;
				System.out.println("~ file FREED: " + event.getProcessID());
			}else{
				processList.add(event);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void lockOrder(){
		while(true){
			if(!isLocked){
				if(!processList.isEmpty()){
					isLocked = true;
					ProcessHandler event = processList.get(0);
					System.out.println("> file LOCKED: "+event.getProcessID());
					locker = event.getPid();					
					processList.remove(0);
					allocateFile();
				}
			}
		}
	}

	public void allocateFile(){
		try {
			Socket socket = new Socket(netAddress, ports[locker]);
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());;
			output.writeObject(new ProcessHandler("P0", sharedFile, pid, false, true));

			output.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
