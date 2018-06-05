# Multi-Level-Feedback-Queue
Simulation of Multi-level feedback queue CPU scheduler

• The scheduler Q consists of 3 linear queues, i.e., Q0, Q1, and Q2. 
  - Q0 is round robin with time quantum 8 (RR8),
  - Q1 is round robin with time quantum 16 (RR16), and
  - Q2 is FCFS.
• Process cannot be executed in the lower queue if there are any jobs in all higher queues. For example, Q0 has 5 processes, Q1 has 1 process, and Q2 has 1 process. Then, first the process in Q0 should be executed (and completed), and then a process in Q1 is executed. Finally, Q2 will get CPU resource.
• A new process enters queue Q0 which is served RR8.
• When it gains CPU, a process receives 8 milliseconds.
• If it does not finish in 8 milliseconds, process is moved to queue Q1.
• At Q1 process is again served RR16 and receives 16 additional milliseconds.
• If it still does not complete, it is preempted and moved to queue Q2.
• The processes are to be created with the following fields in the PCB (Process Control Block): Process ID, arrival time, actual execution time, queue number. The creation is done randomly, and includes at leas 20 processes.
• Output includes a time line, i.e., every time step, indicate which processes are created (if any), which ones are completed (if any), processes which moved into different queue, etc. 
