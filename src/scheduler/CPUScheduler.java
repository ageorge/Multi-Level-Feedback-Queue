package scheduler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Main class that simulates the cpu scheduling
 * @author anitageorge
 *
 */
public class CPUScheduler {
	
	private int max_burstTime;
	private List<PCB> jobs = new ArrayList<>();
	private RoundRobin RR8 = new RoundRobin(8);
	private RoundRobin RR16 = new RoundRobin(16);
	private RoundRobin FCFS;
	private List<PCB> completed_jobs = new ArrayList<>();
	private List<CPU> cpus;
	private final int cpu_RR8 = 0;
	private final int cpu_RR16 = 1;
	private final int cpu_FCFS = 2;
	private int clock = 0;
	private int jobtime_rr8 = -1, jobtime_rr16 = -1, jobtime_fcfs = -1, temp_finish_time = -1;;
	private PCB current_pcb_rr8 = null;
	private PCB current_pcb_rr16 = null;
	private PCB current_pcb_fcfs = null;
	private PCB current_pcb = null;
	private String message;
	private boolean ageingfactor;
	
	public static Map<String, Integer> results = new HashMap<>();
	public static Map<String, Integer> results_ageing = new HashMap<>();
	
	/**
	 * Constructor to initailize the application with or without the ageing factor
	 * @param ageingfactor
	 */
	public CPUScheduler(boolean ageingfactor) {
		super();
		this.ageingfactor = ageingfactor;
	}

	/**
	 * Method used to implement the Multilevel feedback Queue scheduler
	 */
	public void schedule() {
		int temp_start_rr16 = 0,  temp_start_fcfs = 0;
		PCB pcb = null;
		
		while(!checkcomplete()) { //schedule until all jobs are processed
			message = "";
			
			if(pcb == null && !jobs.isEmpty()) { //retrieve the next PCB 
				pcb = jobs.get(0);
			}
			
			if(pcb!=null && clock >= pcb.getArrivalTime()) { //Add the PCB to the multilevel queues (RR8)
				pcb.setState("Queue RR8");
				message = message + " " + pcb.getId() + " added to RR8.";
				RR8.addPCB(pcb);
				jobs.remove(0);
				pcb = null;
			}
			
			// Implementation for 3 CPU's 
			if(cpus.size() > 1) {
				// For each of the 3 CPU's: Do the following
				// 1. check if available: if yes allocate the next job to it
				// 2. check if time in the queue is completed: if yes then Update the cpu status and 
				// 3. check if the PCB has finished execution: if no then remove the job from this queue and move it to the next level queue
				// 4. if yes: move to completed section and update PCB
				if(!cpus.get(cpu_RR8).isBusy()) { //Step 1: RoundRobin 8 time units
					current_pcb_rr8 = allocateNextJob(RR8, cpu_RR8);
				} 
				if(clock == jobtime_rr8) { //Step 2
					RR8.removePCB(current_pcb_rr8);
					message = message + " " + current_pcb_rr8.getId() + " removed from RR8.";
					cpus.get(cpu_RR8).setState(false);
					if(current_pcb_rr8.getRemBTTime() > 0) { //Step 3
						message = message + " " + current_pcb_rr8.getId() + " added to RR16.";
						RR16.addPCB(current_pcb_rr8);
					} else { //Step 4
						message = message + " " + current_pcb_rr8.getId() + " from RR8 is completed.";
						current_pcb_rr8.setCompletionTime(clock);
						completed_jobs.add(current_pcb_rr8);
					}
					cpus.get(cpu_RR8).setGanttChart(current_pcb_rr8.getId() + "(" + current_pcb_rr8.getStartTime() + " - "+clock+")");
					current_pcb_rr8 = allocateNextJob(RR8, cpu_RR8);
				}
				if(!cpus.get(cpu_RR16).isBusy()) { //Step 1: RoundRobin 16 time units
					current_pcb_rr16  = allocateNextJob(RR16, cpu_RR16);
					if(current_pcb_rr16 != null)
						temp_start_rr16 = clock;
				} 
				if(clock == jobtime_rr16) { //Step 2
					RR16.removePCB(current_pcb_rr16);
					message = message + " " + current_pcb_rr16.getId() + " removed from RR16.";
					cpus.get(cpu_RR16).setState(false);
					if(current_pcb_rr16.getRemBTTime() > 0) { //Step 3
						message = message + " " + current_pcb_rr16.getId() + " added to FCFS.";
						FCFS.addPCB(current_pcb_rr16);
					} else { //Step 4
						current_pcb_rr16.setCompletionTime(clock);
						completed_jobs.add(current_pcb_rr16);
						message = message + " " + current_pcb_rr16.getId() + " from RR16 is completed.";
					}
					cpus.get(cpu_RR16).setGanttChart(current_pcb_rr16.getId() + "(" + temp_start_rr16 + " - "+clock+")");
					current_pcb_rr16  = allocateNextJob(RR16, cpu_RR16);
					if(current_pcb_rr16 != null)
						temp_start_rr16 = clock;
				}
				if(!cpus.get(cpu_FCFS).isBusy()) {  //Step 1: FCFS (RoundRobin max_burstTime time units)
					current_pcb_fcfs = allocateNextJob(FCFS, cpu_FCFS);
					if(current_pcb_fcfs != null)
						temp_start_fcfs = clock;
				}
				if(clock == jobtime_fcfs) { //Step 2 and 4
					cpus.get(cpu_FCFS).setState(false);
					current_pcb_fcfs.setCompletionTime(clock);
					message = message + " " + current_pcb_fcfs.getId() + " from FCFS is completed.";
					completed_jobs.add(current_pcb_fcfs);
					cpus.get(cpu_FCFS).setGanttChart(current_pcb_fcfs.getId() + "(" + temp_start_fcfs + " - "+clock+")");
					message = message + " " + current_pcb_fcfs.getId() + " removed from FCFS.";
					FCFS.removePCB(current_pcb_fcfs);
					current_pcb_fcfs = allocateNextJob(FCFS, cpu_FCFS);
					if(current_pcb_fcfs != null)
						temp_start_fcfs = clock;
				}
			} else if(cpus.size() == 1) { //Implementation of Single CPU shared between three queues - RR8, RR16 and FCFS
				allocateNextPCB_SingleCPU();

				// 1. if a job completes it cpu quantum time: do the following
				// 2. if job is not completed: move to next level queue
				// 3. if yes then update PCB and move to complete section
				if(clock == temp_finish_time) { //Step 1
					cpus.get(cpu_RR8).setState(false);
					if(current_pcb.getRemBTTime() > 0) {  //Step 2
						switch(current_pcb.getState()) {
						case "RR8":
							message = message + " " + current_pcb.getId() + " moved to RR16.";
							RR16.addPCB(current_pcb);
							RR8.removePCB(current_pcb);
							cpus.get(cpu_RR8).setGanttChart(current_pcb.getId() + "(RR8: " + current_pcb.getStartTime() + " - "+clock+")");
							break;
						case "RR16":
							message = message + " " + current_pcb.getId() + " moved to FCFS.";
							FCFS.addPCB(current_pcb);
							RR16.removePCB(current_pcb);
							cpus.get(cpu_RR8).setGanttChart(current_pcb.getId() + "(RR16:" + current_pcb.getTempstartTime() + " - "+clock+")");
							break;
						}
					} else {  //Step 3
						current_pcb.setCompletionTime(clock);
						message = message + " " + current_pcb.getId() + " is completed.";
						switch(current_pcb.getState()) {
						case "RR8":
							RR8.removePCB(current_pcb);
							message = message + " " + current_pcb.getId() + " removed from RR8.";
							cpus.get(cpu_RR8).setGanttChart(current_pcb.getId() + "(RR8: " + current_pcb.getStartTime() + " - "+clock+")");
							break;
						case "RR16":
							RR16.removePCB(current_pcb);
							message = message + " " + current_pcb.getId() + " removed from RR16.";
							cpus.get(cpu_RR8).setGanttChart(current_pcb.getId() + "(RR16:" + current_pcb.getTempstartTime() + " - "+clock+")");
							break;
						case "FCFS":
							FCFS.removePCB(current_pcb);
							message = message + " " + current_pcb.getId() + " removed from FCFS.";
							cpus.get(cpu_RR8).setGanttChart(current_pcb.getId() + "(FCFS:" + current_pcb.getTempstartTime() + " - "+clock+")");
							break;
						}
						completed_jobs.add(current_pcb);
					}
					allocateNextPCB_SingleCPU();
				}
			}
			
			int baselineTime = max_burstTime*2;
			
			if(clock > baselineTime)
				ageing_analysis();
			
			if(!message.equals("")) {
				System.out.print("\nClock:" + clock + "\t");
				System.out.print(message);
			}
			clock++;
		}
	}
	
	/**
	 * Method to allocate the next PCB to the multilevel queues - Single CPU case
	 */
	public void allocateNextPCB_SingleCPU() {
		// 1. check if the cpu is free
		// 2. if yes allocate the job from RR8 if available to the cpu
		// 3. if not available check RR16 and allocate the job
		// 4. if not available check FCFS and allocate the job
		// 5. if cpu is busy:
		// 6. In the lower level queue blocks check for higher level job arrivals
		// 7. if yes, then allocate the cpu for the job in the higher level queue
		if(!cpus.get(cpu_RR8).isBusy()) { //Step 1
			if(!RR8.isQueueEmpty()) { //Step 2
				current_pcb = allocateNextJob(RR8, cpu_RR8);
				current_pcb.setState("RR8");
				temp_finish_time = jobtime_rr8;
			} else if(!RR16.isQueueEmpty()) { //Step 3
				current_pcb = allocateNextJob(RR16, cpu_RR8);
				current_pcb.setState("RR16");
				temp_finish_time = jobtime_rr16;
			} else if(!FCFS.isQueueEmpty()) { //Step 4
				current_pcb = allocateNextJob(FCFS, cpu_RR8);
				current_pcb.setState("FCFS");
				temp_finish_time = jobtime_fcfs;
			} 
		} else if(cpus.get(cpu_RR8).isBusy()) { //Step 5
			current_pcb = cpus.get(cpu_RR8).getProcessing_PCB();
			if("RR16".equals(current_pcb.getState()) || "FCFS".equals(current_pcb.getState())) { //Step 6
				int remTime = 0;
				if(!RR8.isQueueEmpty()) {
					PCB newPCB = current_pcb;
					remTime = current_pcb.getRemBTTime() - (clock - current_pcb.getTempstartTime());
					newPCB.setRemBTTime(remTime);
					if(current_pcb.getState().contains("RR16"))
						RR16.updatePCB(current_pcb, newPCB);
					else if(current_pcb.getState().contains("FCFS"))
						FCFS.updatePCB(current_pcb, newPCB);
					message = message + " " + "New Job in HigherLevel Queue: " + current_pcb.getId() + " moved back to waiting state";
					current_pcb = allocateNextJob(RR8, cpu_RR8); //Step 7
					current_pcb.setState("RR8");
					temp_finish_time = jobtime_rr8;
				} else if(!RR16.isQueueEmpty()) {
					if(current_pcb.getState().contains("FCFS")) {
						PCB newPCB = current_pcb;
						remTime = current_pcb.getRemBTTime() - (clock - current_pcb.getTempstartTime());
						newPCB.setRemBTTime(remTime);
						FCFS.updatePCB(current_pcb, newPCB);
						message = message + " " + "New Job in HigherLevel Queue: " + current_pcb.getId() + " moved back to waiting state";
						current_pcb = allocateNextJob(RR16, cpu_RR8); //Step 7
						current_pcb.setState("RR16");
						temp_finish_time = jobtime_rr16;
					}
				} 
				
			} 
		}
	}
	
	/**
	 * Method to retrieve the PCB from the respective queue and allocate it to the CPU 
	 * @param jobqueue
	 * @param cpuIndex
	 * @return
	 */
	public PCB allocateNextJob(RoundRobin jobqueue, int cpuIndex) {
		int processTime;
		int quantum = jobqueue.getQuantumTime();
		PCB current_pcb = jobqueue.getNextPCB(); 
		if(current_pcb != null) { //execute only if a PCB is available
			message = message + " " + current_pcb.getId() + " added to cpu#" + cpuIndex + ".";
			switch(quantum) { //calculating the estimated processing time based on quantum time for RR8, RR16 and FCFS
			case 8:
				jobtime_rr8 = clock + (current_pcb.getBurstTime() <= 8 ? current_pcb.getBurstTime() : 8);
				processTime = current_pcb.getBurstTime() < quantum ? current_pcb.getBurstTime() : quantum;
				current_pcb.setRemBTTime(current_pcb.getBurstTime() - processTime);
				current_pcb.setStartTime(clock);
				break;
			case 16:
				jobtime_rr16 = clock + (current_pcb.getRemBTTime() <= 16 ? current_pcb.getRemBTTime() : 16);
				processTime = current_pcb.getRemBTTime() < quantum ? current_pcb.getRemBTTime() : quantum;
				current_pcb.setTempstartTime(clock);
				current_pcb.setRemBTTime(current_pcb.getRemBTTime() - processTime);
				break;
			default:
				jobtime_fcfs = clock + current_pcb.getRemBTTime();
				processTime = current_pcb.getRemBTTime() < quantum ? current_pcb.getRemBTTime() : quantum;
				current_pcb.setTempstartTime(clock);
				current_pcb.setRemBTTime(current_pcb.getRemBTTime() - processTime);
				break;
			}
			//Allocating the PCB to the CPU
			cpus.get(cpuIndex).setState(true);
			cpus.get(cpuIndex).setProcessing_PCB(current_pcb);
		} 
		return current_pcb;
	}
	
	/**
	 * Method to determine if all the jobs are processed
	 * @return
	 */
	public boolean checkcomplete() {
		if(jobs.isEmpty()) {
			if(RR8.isQueueEmpty() && RR16.isQueueEmpty() && FCFS.isQueueEmpty())
				return true;
		}
		return false;
	}
	
	/**
	 * Method to read the PCB data from a file
	 * @param filename
	 */
	public void readPCBs(String filename) {
		BufferedReader br;
		PCB pcb = null;
		try {
			br = new BufferedReader(new FileReader("resrc/"+filename));
			String input = br.readLine();
			if(input.contains("PID") || input.contains("ProcessId")) { //Ignore the header if available
				input = br.readLine();
			} 
			while(input != null) {
				String content[] = input.split(","); //Retrieve the individual data from each line
				String pid = content[0];
				int at = Integer.parseInt(content[1]);
				int bt = Integer.parseInt(content[2]);
				pcb = new PCB(pid, at, bt);
				jobs.add(pcb); //Adding each PCB to a list to hold them until execution completes
				input = br.readLine();
			}
			findMaxBurstTime();
			Collections.sort(jobs, new PCBComparator_AT()); //Sorting the jobs based on Arrival Time to use in the Queue
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to find the maximum bust time for the FCFS queue
	 */
	public void findMaxBurstTime() {
		Collections.sort(jobs, new PCBComparator_BT());
		max_burstTime = jobs.get(jobs.size()-1).getBurstTime();
		FCFS = new RoundRobin(max_burstTime);
	}
	
	/**
	 * Method to set the number of CPU's for the simulation
	 * @param cpu_num
	 */
	public void setCPU(int cpu_num) {
		cpus = new ArrayList<>();
		for(int i = 0; i < cpu_num; i++) {
			cpus.add(new CPU(i));
		}
	}
	
	/**
	 * Method to print the results after execution is completed
	 */
	public void print_results() {
		
		System.out.println("\n\nExecution Completed");
		System.out.println("Job Queue status:"); //All readyQueues are empty after execution completes
		System.out.println("RR8:" + RR8.getJobqueue());
		System.out.println("RR16:" + RR16.getJobqueue());
		System.out.println("FCFS:" + FCFS.getJobqueue());
		System.out.println();
		
		System.out.println("CPU status:"); //Display the Gantt chart for each CPU
		if(cpus.size() > 1) {
			for(CPU cpu : cpus) {
				cpu.printOutput();
			}
		} else {
			cpus.get(0).printgeneralOutput();
		}
		System.out.println();
		
		// Display the PCB detail
		Collections.sort(completed_jobs, new PCBComparator_AT());
		System.out.println("Completed jobs: " + completed_jobs.size());
		System.out.println("PCB status:");
		System.out.println("ID\tAT\tBT\tST\tRT\tCT\tWT\tTT");
		for(PCB p : completed_jobs) {
			p.display();
		}
		
		System.out.println();
		performance_calculation();
		
		System.out.println("-------------------------------------------------------------------------");
	}
	
	/**
	 * Method to print the status of each data set: for multiple execution
	 */
	public void print_status() {
		System.out.println("-------------------------------------------------------------------------");
		
		System.out.println("JOBS:" + jobs.size() + " " + jobs);
		System.out.println("MAXBT:" + max_burstTime);
		System.out.println("CPU's #" + cpus.size());
	}
	
	/**
	 * Method to measure the performance of the algorithm by calculating
	 * Average Waiting Time
	 * Average Response Time
	 * Average Turnaround Time
	 */
	public void performance_calculation() {

		int totalWT = 0, totalTT = 0, totalRT = 0;
		int totalPCBs = completed_jobs.size();
		for(PCB pcb:completed_jobs) { //Finding the total WT,TT,RT
			totalWT += pcb.getWaitingTime();
			totalTT += pcb.getTurnaroundTime();
			totalRT += pcb.getResponseTime();
		}
		int avgWT = totalWT/totalPCBs;
		int avgTT = totalTT/totalPCBs;
		int avgRT = totalRT/totalPCBs;
		
		System.out.println("The average Waiting Time is : " + avgWT);
		System.out.println("The average Turnaround Time is : " + avgTT);
		System.out.println("The average Response Time is : " + avgRT);
		
		//Storing the results of this data set into a static variable for evaluation
		results.put("#" + cpus.size() + " WT", avgWT);
		results.put("#" + cpus.size() + " TT", avgTT);
		results.put("#" + cpus.size() + " RT", avgRT);
	}
	
	/**
	 * Ageing analysis for starvation detection
	 */
	public void ageing_analysis() {
		int baselineTime = max_burstTime*2;
		int time;
		if(cpus.size() == 1) { //Implemented only for 1 CPU case
			CPU cpu = cpus.get(0);
			Iterator<PCB> iterator = FCFS.getJobqueue().iterator();
			while(iterator.hasNext()) {
				PCB pcb = iterator.next();
				time = clock - pcb.getStartTime();
				if(time > baselineTime) { //Checking for starvation
					if(cpu.getProcessing_PCB().getId().equals(pcb.getId())) {
						break;
					}
					iterator.remove();
					RR16.addPCB(pcb); //Pre-empt the PCB to higher level queue
					System.out.print("\nClock:"+clock+"\t Starvation Detected: " + pcb.getId() + " is preempted to RR16");
				}
			}
		}
	}
	
	/**
	 * Method to print the results of performance evaluation of two data sets
	 */
	public static void performance_evaluation() {
		int wt1 = 0, wt2  = 0, tt1  = 0, tt2  = 0, rt1  = 0, rt2  = 0;
		System.out.println("\n Performance Analysis");
		for(String key:results.keySet()) {
			int val = results.get(key);
			switch(key) {
			case "#3 WT":
				wt1 = val;
				break;
			case "#3 TT":
				tt1 = val;
				break;
			case "#3 RT":
				rt1 = val;
				break;
			case "#1 WT":
				wt2 = val;
				break;
			case "#1 TT":
				tt2 = val;
				break;
			case "#1 RT":
				rt2 = val;
				break;
			}
			
			System.out.println(key + ":" + results.get(key));
		}
		
		String wt = wt1 <= wt2 ? "3" : "1";
		String tt = tt1 <= tt2 ? "3" : "1";
		String rt = rt1 <= rt2 ? "3" : "1";
		
		System.out.println("\nDesirable Outcome is: minimum waiting time, minimum turnaround time and minimum response time.");
		System.out.println("Analysing both cases: The better performance was by");
		System.out.println("Waiting Time: " + wt + " CPU's");
		System.out.println("Turnaround Time: " + tt + " CPU's");
		System.out.println("Response Time: " + rt + " CPU's");
		

		System.out.println("\nLegends: ");
		System.out.println("AT = Arrival Time ");
		System.out.println("BT = Burst Time ");
		System.out.println("ST = Start Time ");
		System.out.println("RT = Response Time ");
		System.out.println("CT = Completion Time ");
		System.out.println("WT = Waiting Time ");
		System.out.println("TT = Turnaround Time ");
	}

	/**
	 * Main Method - Starting point of the application
	 * @param args
	 */
	public static void main(String[] args) {
		CPUScheduler scheduler = new CPUScheduler(false); //CASE 1: Normal Execution 3 CPU's
//		Scanner sc = new Scanner(System.in);
//		System.out.println("Enter the number of CPU's");
		int cpu_num = 3;
		
		scheduler.setCPU(cpu_num);
		scheduler.readPCBs("input.txt");
		scheduler.print_status();
		
		scheduler.schedule();
		
		scheduler.print_results();
		
		CPUScheduler scheduler2 = new CPUScheduler(false); //CASE 2: Normal Execution 1 CPU
//		System.out.println("Enter the number of CPU's");
		int cpu_num2 = 1;
		
		scheduler2.setCPU(cpu_num2);
		scheduler2.readPCBs("input.txt");
		scheduler2.print_status();
		
		scheduler2.schedule();
		
		scheduler2.print_results();
		
		performance_evaluation(); //Performance evaluation between case 1 and case 2
		
		System.out.println("Executing with Ageing factor into consideration");
		
		CPUScheduler scheduler3 = new CPUScheduler(true); //CASE 3: Execution of 1 CPU with starvation detection enabled
		
		scheduler3.setCPU(cpu_num2);
		scheduler3.readPCBs("input.txt");
		scheduler3.print_status();
		
		scheduler3.schedule();
		
		scheduler3.print_results();
	}
}
