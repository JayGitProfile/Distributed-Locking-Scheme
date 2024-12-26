package root.operations;

import java.io.Serializable;

public class ProcessHandler implements Serializable{

	private static final long serialVersionUID = 1001919453L;
	private String processID;
	private String fileName;
	private int pid;
	private boolean isLockFree = false;
	private boolean isApproved = false;
	
	public ProcessHandler() {}

	public ProcessHandler(String processID, String fileName, int pid, boolean isLockFree, boolean isApproved) {
		super();
		this.processID = processID;
		this.fileName = fileName;
		this.pid = pid;
		this.isLockFree = isLockFree;
		this.isApproved = isApproved;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public boolean isLockFree() {
		return isLockFree;
	}

	public void setLockFree(boolean isLockFree) {
		this.isLockFree = isLockFree;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	@Override
	public String toString() {
		return "ProcessHandler [processID=" + processID + ", fileName=" + fileName + ", pid=" + pid + ", isLockFree="
				+ isLockFree + ", isApproved=" + isApproved + "]";
	}
	
}
