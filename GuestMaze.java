import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

// Guest Class for Thread
class Guest implements Runnable {
    // Leader's knowledge of total guests
    private int nGuests;
    // If the guest ate the cake then switch to true
    private boolean eaten = false;
    // Counter for the leader
    private int counts = 0;
    private GuestMaze m;
    // Cake atomically shared among all guests
    private static final AtomicBoolean cake = new AtomicBoolean(true);
    // Flag atomically shared. Only leader can decide when to finish
    private static final AtomicBoolean finished = new AtomicBoolean();
    // We appoint number 0 as the leader and everyone else is the guests
    private int number;
    // Lock needed to appoint only one person go one at a time
    private static ReentrantLock lock = new ReentrantLock();

    public Guest(int number, GuestMaze m, int nGuests) {
        this.m = m;
        this.number = number;
        this.nGuests = nGuests;
    }

    @Override
    public void run() {
        // Keep the threads running until we are finished
        while (!finished.get()) {
            if (lock.tryLock()) {
                try {
                    lock.lock();
                    try {
                        // If the cake is not there and it's the leader in the maze -> reset cake and update counter
                        if (cake.get() == false && number == 0) {
                            System.out.println("I'm the leader in the maze and I have just put the cake back on");
                            counts++;
                            cake.compareAndSet(false, true);
                            // If we have reach total amount of cakes eaten at least once by every guest
                            if (counts == nGuests) {
                                // Leader can announce that all guests have visited
                                System.out.println("All guests have entered once!");
                                // Set to finish and end the maze!
                                finished.compareAndSet(false, true);
                            }
                        }
                        // If it's the guests who haven't ate and the cake is there -> eat the cake and marked that they have ate
                        else if (number != 0 && cake.get() == true && eaten == false) {
                            System.out.println("I'm guest:" + number + " in the maze and I just ate the cake");
                            cake.compareAndSet(true, false);
                            eaten = true;
                        }
                        // If it's the leader's turn and the cake is there -> they will just exit the maze without doing anything
                        // If it's the guest who has already ate or the cake isn't there -> they will exit the maze without doing anything
                    } finally {
                        // Once exit the maze, the next person can go in. Can be the same person again or different
                        lock.unlock();
                    }
                } finally {
                    lock.unlock();
                }

            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

public class GuestMaze {
    public List<Guest> Guests;

    public static void main(String args[]) throws Exception {
        int nGuest = 10;
        GuestMaze m = new GuestMaze();
        m.runMaze(m, nGuest);
    }

    public void runMaze(GuestMaze m, int nThreads) {
        if (nThreads < 2) {
            System.out.println("Need at-least 2 guests.");
            System.exit(0);
        }
        // Form the guestlist
        m.Guests = new ArrayList<>();
        // Assemble all guests before maze.
        for (int i = 0; i < nThreads; i++) {
            Guest guest = new Guest(i, m, nThreads - 1);
            m.Guests.add(guest);
            Thread thrGuest = new Thread(guest);
            // Guests start the maze
            thrGuest.start();
        }
    }
}
