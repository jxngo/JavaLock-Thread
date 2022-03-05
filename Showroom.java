import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Guest Class for Thread
class Guest implements Runnable {
    private int i;
    // Door signs 
    private static final boolean available = true;
    private static final boolean busy      = false; 
    // 
    private static final AtomicBoolean roomState = new AtomicBoolean(available);
    // Indicate if the guest has already visited
    private boolean visited   = false;
    // Int value for max capacity
    private int maxCapacity;
    //
    private Showroom m;
    // Lock needed to appoint only one person go one at a time
    private static ReentrantLock lock = new ReentrantLock();
    // AtomicInteger used to count guests if they haven't visited the room already
    // Once we hit all n-guests we can end the showtime
    private static final AtomicInteger count = new AtomicInteger();
    // Atomicaly shared to indicate closing time
    private static final AtomicBoolean closed = new AtomicBoolean();
    

    public Guest(int i, Showroom m, int maxCapacity) {
        this.i = i;
        this.m = m;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void run() {
       // While the showroom isn't closed.
       while (!closed.get()) {
           if (count.get() == maxCapacity) {
               System.out.println("Showroom is now closed!");
               closed.compareAndSet(false, true);
               continue;
            } 
           if (lock.tryLock() && roomState.get() == available) {
               try {
                   lock.lock();
                   roomState.compareAndSet(available, busy);
                   try {
                       // We are checking if they have visited to increment the counter
                       if (!visited) {
                           System.out.println("Guest " + i + " has entered for the first time!");
                           count.getAndIncrement();
                           visited = true;
                       }
                       // If the guest has already visited, they will go in without outputing since 
                       // it will just spam the output.
                   } finally {
                       lock.unlock();
                       roomState.compareAndSet(busy, available);
                   }
               } finally {
                   lock.unlock();
               }
           }
           else {
               try {
                   Thread.sleep(1000);
               }
               catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
    }
    }
      
}

public class Showroom {
    public List<Guest> Guests;

    public static void main(String args[]) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("It is the opening day of Minotaur famous crystal vase. You can assume the lobby will be full once we are opened. What is the max capacity you would like to put?");
        int maxCapacity = scan.nextInt();
        Showroom m = new Showroom();
        m.runRoom(m, maxCapacity);
    }
    public void runRoom(Showroom m, int nThreads) {
        if (nThreads < 1) {
            System.out.println("Need at-least 1 guest.");
            System.exit(0);
        }
        // Form the guestlist
        m.Guests = new ArrayList<>();
        // Assemble all guests before maze.
        for (int i = 0; i < nThreads; i++) {
            Guest guest = new Guest(i, m, nThreads);
            m.Guests.add(guest);
            Thread thrGuest = new Thread(guest);
            // Guests start the maze
            thrGuest.start();
        }
    }
}
