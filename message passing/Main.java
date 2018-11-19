import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* Main class is the implementattion of producer consumer problem using inderect message passing.
* Producer sends data to a consumer using a queue of integers, while the consumer consumes integers
* from that queue
*/
public class Main {

    /*
    * man method is used to initialize all objects are needed for testing 
    * producer consumer solution
    */
    public static void main(String[] args) {
        Producer producer = new Producer();
        Consumer consumer = new Consumer(producer);
        producer.start();
        consumer.start();
        //read contents from the files to check if two processes communicated correctly
        readFile("produced.txt");
        readFile("consumed.txt");
    }
    
    
/*
 * Producer class is an implementation of an object that produces integer data
 * randomly and sends it inderectly to the consumer using a queue (FIFO) that can hold up to 3 items
 * producer extends thread allowing create a seperate thread for producing data
 */   
public static class Producer extends Thread{
    private static final int SIZE = 3;//max size of a queue
    int[] randNums;
    Queue<Integer> messages = new LinkedList<>();//a queue that holds messages
    
    /*
    * run() method is used to randomly generate 100 integer data and send it to the consumer 
    * by putting those integers on the message queue
    * run() is used to write produced items to a file called produced.txt
    */
    @Override
    public void run(){
        try{
        randNums = new int[100];
        fillArray(randNums);//fill an array with integers from 0 to 99
        shuffleArray(randNums);//randomly shuffle integers
        
        PrintWriter write = new PrintWriter("produced.txt", "UTF-8");// writer to a file
        write.println("Produced items:");
        //generate a random int, put it on the queue, and write it to a file
        for (int i = 0; i < 100; i++) {
                int nextRand = randNums[i];
                send(nextRand);
                write.println(nextRand);
        }
        write.close();
        }catch(Exception e){
            
        }
    }
    
    /*
    * send method is used to send an integer to the queue if there is available space
    * otherwise, the producer has to wait
    */
    public synchronized void send(int num) throws InterruptedException {
        //wait while the queue is full
        while(messages.size() == SIZE){
            wait();
        }
        messages.add(num);//add an int to the end of the queue
        notifyAll();//notify other threads
    }
    
    /*
    * receive method is used by the consumer to receive items produced by the producer
    * consumer has to wait while the queue is empty
    * consumed item is removed from the head of the queue
    */
    public synchronized int receive() throws InterruptedException{       
        notifyAll();// notify producer thread
        //wait if queue is empty
        while(messages.size() == 0){
            wait();
        }
        //consume the integer number
        int num = messages.remove();
       return num;
    }
    
    /*
    * fillArray method is used to fill an array with integers from 0 to 100
    * each int number value corresponds to its index in the array
    * array of int is taken as a parameter
    */
    private void fillArray(int[] arr){
        for(int i = 0; i < arr.length; i++){
            arr[i] = i;
        }
    }//end of fillArray 

    /*
    * shuffleArray method is used to shuffle integers in a given array
    * using fisher yates shuffle technique
    * array of integers is taken as a parameter
    */
    private void shuffleArray(int[] arr) {
      
    Random random = new Random();
    int randNum = 0;
    randNum = random.nextInt(100) + 1;
    
    for (int i = arr.length - 1; i > 0; i--){
        int index = random.nextInt(i + 1);
        // Simple swap
        int a = arr[index];
        arr[index] =  arr[i];
        arr[i] = a;
    
    }

    }// end shuffleList
    
}//end Producer


/*
 * Consumer class is an implementation of a consumer object that
 * consumes items from a queue put there by a producer
 */
public static class Consumer extends Thread{
    Producer producer;//producer object
    
    /*
     * Consumer constructor is used to initialize producer object,
     * from which a consumer can get produced items
     */
    public Consumer(Producer producer){
        this.producer = producer;
    }
    
    /*
     * run method is used to impelement the consumption of 
     * 100 randomly generated data 
     * every consumed integer is printed to a file for futher 
     * verification of correctness
     */
    @Override
    public void run(){
        
        try{
        PrintWriter write = new PrintWriter("consumed.txt", "UTF-8");// writer to a file
        write.println("Consumed items:");
        for (int i = 0; i < 100; i++) {
            try {
                int consumedItem = producer.receive();//consume integer
                write.println(consumedItem);//write it to a file
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        write.close();//close file
        }catch(Exception e){
            
        }
    }
    
    
}//end Consumer

    /*
     * readFile method is used to read all content from a file called fileName
     * where fileName will be either consumed.txt or produced.txt
     * everything from the file is printed out to the screen
     */
    public static void readFile(String fileName){

        try {
            
            File file = new File(fileName);
            Scanner inputFile = new Scanner(file);
            //read every line from the file until the next one is found in the file
            while (inputFile.hasNextLine()) {
                String line = inputFile.nextLine();
                System.out.println(line);
            }
            inputFile.close();

        } catch (Exception e) {
            System.out.println("reader: " + e.getMessage());
        }
    }
    
}
