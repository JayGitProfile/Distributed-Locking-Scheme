package root.operations;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LockHandler {

	public final String processId = "P1";
	public int pid = 1, cpid = 0;
	public String sharedFile = "myFile.txt";
	public InetAddress netAddress = InetAddress.getLoopbackAddress();
	//Ports for Centralized, P1, P2, P3. Specified in the compiled jars
	public int[] ports = new int[]{ /*{port1, port2, port3, port4}*/ };
	//specify ports above to execute the project manually instead of jars
	//execution fails without ports, specify same ports in Orchestrator
	ServerSocket serverSocket;
	public Thread connTh = null;
	public boolean isReqProcessed = false;

	public LockHandler() {
		System.out.println("Initiating Process: "+processId);
		connTh = (new Thread(){
			@Override
			public void run(){
				connections();
				return;
			}
		});
		connTh.start();		
		requestFileLock();
	}

	public String getProcessId() {
		return this.processId;
	}

	public void threadWaits() throws InterruptedException {
		if(connTh != null)	connTh.join();
	}

	public void connections(){
		try {
			serverSocket = new ServerSocket(ports[pid], 0, netAddress);
			while(!isReqProcessed){
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
			if(event.getPid()==cpid){
				if(!isReqProcessed) {
					if(event.isApproved() && event.getFileName().equalsIgnoreCase(sharedFile)){
						System.out.println("> '"+sharedFile+"' LOCKED");
						Thread.sleep(5000);
						writeFile();
						isReqProcessed = true;
						requestFileLock();
						System.out.println("~ file FREED");
					}
				}
				else {
					//System.out.println("Yet to be processed");
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void requestFileLock(){
		try {
			Socket socket = new Socket(netAddress, ports[cpid]);
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(new ProcessHandler(processId, sharedFile, pid, isReqProcessed, isReqProcessed));

			output.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeFile(){
		File file = new File(sharedFile);
		try {
			if(file.exists()){
				String byteFile = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), "UTF-8");
				if(byteFile.length() > 0){
					int byteCounter = Integer.parseInt(byteFile) + 1;
					Files.write(Paths.get(file.getAbsolutePath()), String.valueOf(byteCounter).getBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
