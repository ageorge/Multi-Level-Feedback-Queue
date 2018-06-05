package scheduler;

/**
 * Class to hold the PCB details
 * @author anitageorge
 *
 */
public class PCB {
	private String id;
	private int arrivalTime;
	private int burstTime;
	private int startTime;
	private int tempstartTime;
	private int waitingTime;
	private int completionTime;
	private int turnaroundTime;
	private int responseTime;
	private int remBTTime;
	private String state;
	
	/**
	 * @param id
	 * @param arrivalTime
	 * @param burstTime
	 */
	public PCB(String id, int arrivalTime, int burstTime) {
		super();
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;
		state = "new";
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the arrivalTime
	 */
	public int getArrivalTime() {
		return arrivalTime;
	}
	
	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	/**
	 * @return the burstTime
	 */
	public int getBurstTime() {
		return burstTime;
	}
	
	/**
	 * @param burstTime the burstTime to set
	 */
	public void setBurstTime(int burstTime) {
		this.burstTime = burstTime;
	}
	
	/**
	 * @return the waitingTime
	 */
	public int getWaitingTime() {
		return waitingTime;
	}
	
	/**
	 * @param waitingTime the waitingTime to set
	 */
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	
	/**
	 * @return the turnaroundTime
	 */
	public int getTurnaroundTime() {
		return turnaroundTime;
	}
	
	/**
	 * @param turnaroundTime the turnaroundTime to set
	 */
	public void setTurnaroundTime(int turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}
	
	/**
	 * @return the responseTime
	 */
	public int getResponseTime() {
		return responseTime;
	}
	
	/**
	 * @param responseTime the responseTime to set
	 */
	public void setResponseTime(int responseTime) {
		this.responseTime = responseTime;
	}

	/**
	 * @return the completionTime
	 */
	public int getCompletionTime() {
		return completionTime;
	}

	/**
	 * @param completionTime the completionTime to set
	 */
	public void setCompletionTime(int completionTime) {
		this.completionTime = completionTime;
		turnaroundTime = completionTime - arrivalTime;
		waitingTime = turnaroundTime - burstTime;
		responseTime = startTime - arrivalTime;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * @return the startTime
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the remBTTime
	 */
	public int getRemBTTime() {
		return remBTTime;
	}

	/**
	 * @param remBTTime the remBTTime to set
	 */
	public void setRemBTTime(int remBTTime) {
		this.remBTTime = remBTTime;
	}
	
	/**
	 * @return the tempstartTime
	 */
	public int getTempstartTime() {
		return tempstartTime;
	}

	/**
	 * @param tempstartTime the tempstartTime to set
	 */
	public void setTempstartTime(int tempstartTime) {
		this.tempstartTime = tempstartTime;
	}
	/**
	 * Method to display the PCB details
	 */
	public void display() {
		System.out.println(id + "\t" + arrivalTime + "\t" + burstTime + "\t" + startTime + "\t" + responseTime + "\t" + completionTime + "\t" + waitingTime + "\t" + turnaroundTime);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}
	
}
