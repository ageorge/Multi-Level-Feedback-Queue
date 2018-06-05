package scheduler;

import java.util.Comparator;

/**
 * Implementation of Comparator to sort the PCB based on the burst times 
 * @author anitageorge
 */
public class PCBComparator_BT implements Comparator<PCB> {

	@Override
	public int compare(PCB pcb1, PCB pcb2) {
		int result = 0;
		if(pcb1.getBurstTime() < pcb2.getBurstTime())
			result = -1;
		if(pcb1.getBurstTime() > pcb2.getBurstTime())
			result = 1;
		return result;
	}

}
