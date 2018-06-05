package scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class the implements the roundrobin algorithm on different quantum times
 * @author anitageorge
 *
 */
public class RoundRobin {
	
	private int quantumTime;
	private List<PCB> jobqueue;
	
	/**
	 * @param quantumTime
	 * @param jobqueue
	 */
	public RoundRobin(int quantumTime) {
		super();
		this.quantumTime = quantumTime;
		this.jobqueue = new ArrayList<>();
	}
	
	/**
	 * @return the quantumTime
	 */
	public int getQuantumTime() {
		return quantumTime;
	}
	
	/**
	 * @param quantumTime the quantumTime to set
	 */
	public void setQuantumTime(int quantumTime) {
		this.quantumTime = quantumTime;
	}
	
	/**
	 * @return the jobqueue
	 */
	public List<PCB> getJobqueue() {
		return jobqueue;
	}
	
	/**
	 * @param jobqueue the jobqueue to set
	 */
	public void setJobqueue(List<PCB> jobqueue) {
		this.jobqueue = jobqueue;
	}
	
	/**
	 * Method to add the PCB into the job queue
	 * @param pcb
	 */
	public void addPCB(PCB pcb) {
		jobqueue.add(pcb);
		jobqueue.sort(new PCBComparator_AT());
	}
	
	/**
	 * Update the details of a PCB in the queue
	 * @param oldPCB
	 * @param newPCB
	 */
	public void updatePCB(PCB oldPCB, PCB newPCB) {
		if(jobqueue.contains(oldPCB)) {
			jobqueue.remove(oldPCB);
			jobqueue.add(newPCB);
			jobqueue.sort(new PCBComparator_AT());
		}
	}
	
	/**
	 * Returns the next PCB in the queue
	 * @return
	 */
	public PCB getNextPCB() {
		if(!isQueueEmpty()) {
			return jobqueue.get(0);
		}
		return null;
	}
	
	/**
	 * Method that checks if the job queue is empty or not
	 * @return
	 */
	public boolean isQueueEmpty() {
		return jobqueue.isEmpty();
	}

	/**
	 * Method that removes a completed PCB from the job queue and adds it to the completed queue
	 * @param pcb
	 */
	public void removePCB(PCB pcb) {
		jobqueue.remove(pcb);
	}
}
