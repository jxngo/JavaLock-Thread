# JavaLock-Thread
## Problem 1: Minotaur's Birthday Party
### How to Run
I chose a concrete number for part 1. To change the input, locate the main and update the int nGuest = n. 

### Approach and Experimental Evaluation
There are two parts that required planning to be able to solve the maze problem.
1) The algorithm itself to solve the maze riddle
2) Applying the lock and AtomicBoolean correctly

The algorithm was focused first. It took me 1-2 hours to solve the problem logically and then articulate the logic into the paper and code editor. In the time of solving the problem, I understood what I have to do by reading up the prisoner problem in chapter 1, and watching the youtube video about the 2 switches problem. At last illustrating the ideas on a whiteboard helped solidify my understanding for the problem. When it comes to coding the algorithm, multiple conditional statements were included to distinguish the leader from the guests and AtomicBoolean were used to indicate when the cake has been eaten or not and flag signal to terminate the threads. When creating the threads in the main class, the ith in for-loop were used to classified each threads. In this case, 0 were used as the "leader" to keep track of the counter. The number 0 thread or the leader plays a big role in the conditional statement because the leader will only reset the cake and increment the counter if the cake isn't there (false) and if the leader is the one in the maze. The guest's role were to change the cake to false if the cake is there and if they haven't eaten yet. 
Any other conditions that weren't required like if the leader is present in maze and the cake is already there and if the guest has eaten the cake already or if the cake isn't there will be skipped as if they went into the maze and then just leave without no actions. 

Now, the second part of the planning (implementing the lock and AtomicBoolean to the algorithm) took majority of the days because of my lacked understanding of the lock implementation and AtomicBoolean. My initial problem was not knowing which lock to apply and incorrect syntax/format for the both implementations. For example, one sub-problem of my lacked understanding that took a long time as syntax for AtomicBoolean. The AtomicBoolean didn't seem to update for the rest of the threads. It took many trials and error but I didn't get it down until I reread the documentation. I used boolean.get and boolean.set instead of boolean.compareAndSet. I came to realize from documentations that get and set doesn't update the value atomically! After fixing that, it still didn't work. I figured out that I forgot to add static when declaring the AtomicBoolean. I used reentrant and after few looks on documentation of it, I successfully implemented the lock onto the threads.

### Efficiency
The algorithm in addition of concurrency runs at worst O(n^2). The while-loop in run() keeps iterating until the flag changes to true when all guests have visited atleast once. However since it is concurrent, the threads ordering is random since all threads are simultaneously computing. Hence O(n^2) because same threads are repeating.  

## Problem 2: Minotaur's Crystal Vase
### How to Run
Part 2 is presented with scanner to get input on command line. It is straight forward no need to edit anything on the file.
### Approach and Experimental Evaluation
The approach was very similiar to Minotaur's Maze. Out of the three options, number 2 closely resemblance part 1 question. I implemented reentrant lock with multiple AtomicBoolean and AtomicInteger to keep track of counter so that we can terminate once all guests have atleast visited the room once.
Part 2 took half of an hour to coded up without any errors. 

### Questions and Concerns
The problem itself didn't mention what to output so instead, I wrote the code to print out "Guest-n visits for the first time" to keep track of all guests entering atleast once so that we can terminate. I chose not to print "Guest-n in the room" because it will get spammed down since the guests can revisit the room again. 

### Efficiency 
The algorithm runs at worst O(n^2) because we are required to have all guests entered the room atleast one O(n) and threads implementation will result us in randomizing the turns O(n*n)

### Advantages and Disadvantages for #2
Advantages of #2 presents the actual lock functionality where only one thread can compute while others are spinning. Which meets the criteria for Minotaur's showroom. Only one guest can come in one-at-a-time. 
Disadvantages of #2 is that many threads are computing for the lock which can cause traffic and slows down the process. 
