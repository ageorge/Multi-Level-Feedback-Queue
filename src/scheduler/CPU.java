package scheduler;
/**
 * Class that holds the CPU details
 * @author anitageorge
 *
 */
public class CPU {
	private int id;
	private boolean state;
	private PCB processing_PCB;
	private String ganttChart;
	
	/**
	 * Used to initialize cpu's to default values
	 * @param id
	 * @param state
	 */
	public CPU(int id) {
		super();
		this.id = id;
		this.state = false;
		processing_PCB = null;
		ganttChart = "";
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the state
	 */
	public boolean isBusy() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(boolean state) {
		this.state = state;
	}

	/**
	 * @return the processing_PCB
	 */
	public PCB getProcessing_PCB() {
		return processing_PCB;
	}

	/**
	 * 
	 * @param processing_PCB
	 */
	public void setProcessing_PCB(PCB processing_PCB) {
		this.processing_PCB = processing_PCB;
	}

	/**
	 * @param ganttChart the ganttChart to set
	 */
	public void setGanttChart(String str) {
		if(ganttChart.equals(""))
			ganttChart = str;
		else
			ganttChart = ganttChart + "\t" + str;
	}

	/**
	 * Method to output the contents of the CPU (in case of multiple cpu's)
	 */
	public void printOutput() {
		String cpu_name = "FCFS";
		if(id == 0) {
			cpu_name = "RR8";
		} else if(id == 1) {
			cpu_name = "RR16";
		}
		System.out.print("[CPU: "+cpu_name+"] ");
		if(!ganttChart.equals(""))
			System.out.println(ganttChart);
		else
			System.out.println("idle");
	}
	
	/**
	 * Method to output the content of the CPU - general way
	 */
	public void printgeneralOutput() {
		System.out.print("[CPU] ");
		if(!ganttChart.equals(""))
			System.out.println(ganttChart);
		else
			System.out.println("idle");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String status = state ? "busy processing " + processing_PCB.getId() : "free";
		return "CPU#"+id + " : " + status;
	}
	
}
