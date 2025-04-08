import java.util.Scanner;

class Process extends Thread {
    private int id;
    private int sender;
    private int receiver;
    private String message;
    private int totalProcesses;
    private static volatile int token = 1; // Start with process 1 holding the token
    private static boolean messageDelivered = false; // Flag to stop processes

    public Process(int id, int sender, int receiver, String message, int totalProcesses) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.totalProcesses = totalProcesses;
    }

    @Override
    public void run() {
        while (!messageDelivered) {
            synchronized (Process.class) {
                while (token != id && !messageDelivered) {
                    try {
                        Process.class.wait(); // Wait until it's this process's turn
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                if (messageDelivered) return; // Stop if message is already delivered
                
                if (id == sender) {
                    System.out.println("Sender process-> " + sender);
                    System.out.println("The token is received by the sender");
                    System.out.println("The message to be sent: " + message);
                }
                
                if (id == receiver) {
                    System.out.println("\nReceiver process-> " + receiver);
                    System.out.println("The message is received: " + message);
                    messageDelivered = true;
                    Process.class.notifyAll(); // Notify all threads to stop
                    return;
                }
                
                token = (token % totalProcesses) + 1; // Pass the token to the next process
                Process.class.notifyAll(); // Notify all waiting threads
            }
            
            try {
                Thread.sleep(500); // Simulate time delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class TokenRingThreaded {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the message: ");
        String message = sc.nextLine();
        System.out.println("Enter number of processes: ");
        int n = sc.nextInt();
        System.out.println("Enter the sender process: ");
        int sender = sc.nextInt();
        System.out.println("Enter the receiver process: ");
        int receiver = sc.nextInt();
        
        Process[] processes = new Process[n];
        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i + 1, sender, receiver, message, n);
            processes[i].start();
        }
    }
}