import java.util.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class is the implementation of a producer-consumer problem
 * in which data is passed from a producer to a consumer through a logical ring buffer
 * using semaphores
 */
public class Main {

    /*
     * main function is used to test the correctness of the code 
     * and show that two processes cooperated properly
     */
    public static void main(String[] args) {
        Semaphore sp = new Semaphore(1);//producer semaphore
        Semaphore sc = new Semaphore(0);//consumer semaphore
        RingBuffer buffer = new RingBuffer(sp, sc);// object that contains semaphores and ring buffer
        Producer producer = new Producer(buffer);//producer
        Consumer consumer = new Consumer(buffer);//consumer
        producer.start();
        consumer.start();
        //read contents from the files to check if two processes communicated correctly
        readFile("produced.txt");
        readFile("consumed.txt");
      
    }

    /*
     * Producer class is an implemenation of a producer object
     * Producer sends 100 randomly generated data to a consumer
     * by putting it in a logical ring buffer
     */
    public static class Producer extends Thread{
        RingBuffer buffer;//ringBuffer object that is used to put and take items from a ring buffer
        int produced;// temp variable for storing a currently produced item
        int[] randNums;//array for storing random integer numbers
        /*
         * Producer constructor is used to initialize a RingBuffer object,
         * so methods of that object can accessed by the producer
         */
        public Producer(RingBuffer buffer){
            this.buffer = buffer;
            produced = 0;
            randNums = new int[100];//array for storing random integers
            fillArray(randNums);//fill an array with integers from 0 to 99
            shuffleArray(randNums);//randomly shuffle integers
        }
        
        /*
         * run method is used to send 100 data to a consumer 
         * by putting it on the ring buffer
         * each produced item is written to a file called producer.txt
         */
        @Override
        public void run(){
            try{
                PrintWriter write = new PrintWriter("produced.txt", "UTF-8");// writer to a file
                write.println("Produced items:");
                Random rand = new Random();
                for (int i = 0; i < 100; i++) {
                    produced = randNums[i];
                    buffer.putItem(produced);//put item on the ring buffer
                    write.println(produced);//write produced item to a file
                }
                write.close();//close file
            }catch(Exception e){
            
            }
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
     * Consumer class is the implementation of a consumer object
     * that consumes 100 integer data from a producer
     * by taking it from a logical ring buffer
     */
    public static class Consumer extends Thread{
        RingBuffer buffer;//RingBuffer object that is used to take produced items
        int consumed;// temp variable to store currently consumed item
        
        /*
         * Consumer constructor is used to initialize RingBuffer object so 
         * method for taking items can be accessed by the consumer object
         */
        public Consumer(RingBuffer buffer){
            this.buffer = buffer;
            consumed = 0;
        }
        
        /*
         * run method is used to consume all data produced 
         * and store each integer in a file for futher verification of correctness
         */
        @Override
        public void run(){
            try{
                PrintWriter write = new PrintWriter("consumed.txt", "UTF-8");// writer to a file
                write.println("Consumed items:");
                for (int i = 0; i < 100; i++) {
                    consumed = buffer.takeItem();//take item from a ring buffer
                    write.println(consumed);// write consumed item to a file
                }
                write.close();
            }catch(Exception e){
            
            }
        }
    }//end Consumer
    
    /*
     * RingBuffer class is the implementation of an object
     * that is intended to be used by a consumer and a producer 
     * for producing and consuming integer data respectively
     * semaphores are used to control access to the resource (ring buffer)
     */
    public static class RingBuffer{
        Semaphore sp;//producer semaphore
        Semaphore sc;//consumer semaphore
        final int SIZE = 10;// max size of a ring buffer
        int[] nums;//the ring buffer
        int in;//index for producing next int
        int out;// index for consuming next int
        
        /*
         * RingBuffer constructor is used to initialize 
         * all necessary elements for the logical ring buffer 
         * for proper communication between the producer and the consumer
         */
        public RingBuffer(Semaphore sp, Semaphore sc){
            this.sp = sp;//producer semaphore
            this.sc = sc;// consumer semaphore
            nums = new int[SIZE];// initialize ring buffer
            //initally in and out indexes are equal to 0
            in = 0;
            out = 0;
        }
        
        /*
         * putItem method is used to implement the proper placing of an item to
         * the logical ring buffer by a producer
         * first producer semaphore is acquired and an item is placed
         * after consumer semaphore is released indicating that there is an item to consume
         */
        public void putItem(int num){
            try {
                sp.acquire();
            } catch (Exception e) {
            }
            nums[in % SIZE] = num;
            sc.release();
            in++;//update index
        }
        
        /*
         * takeItem method is used to implement the proper removing of an item from
         * the logical ring buffer by a consumer
         * first consumer semaphore is acquired and an item is removed
         * after producer semaphore is released indicating allowing producer to produce an item
         */
        public int takeItem(){
            try{
                sc.acquire();
            }catch(Exception e){
            }
            int consumed = nums[out % SIZE];
            sp.release();
            out++;//update index
            return consumed;
        }
    }//end RingBuffer

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
}//end Main
