package scheduler;

import java.util.Comparator;
/**
 * Implementation of Comparator to sort the PCB based on the arrival times 
 * In case of same arrival time this is sorted by burst times
 * @author anitageorge
 *
 */
public class PCBComparator_AT implements Comparator<PCB> {

	@Override
	public int compare(PCB pcb1, PCB pcb2) {
		int result = 0;
		if(pcb1.getArrivalTime() < pcb2.getArrivalTime())
			result = -1;
		if(pcb1.getArrivalTime() > pcb2.getArrivalTime())
			result = 1;
		if(pcb1.getArrivalTime() == pcb2.getArrivalTime()) {
			if(pcb1.getBurstTime() < pcb2.getBurstTime())
				result = -1;
			else if(pcb1.getBurstTime() > pcb2.getBurstTime())
				result = 1;
		}
		return result;
	}

}
