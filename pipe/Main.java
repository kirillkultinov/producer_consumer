import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class holds all of the implementation for a producer-consumer problem 
 * that is solved by using a pipe
 */
public class Main {
    
    /*
     * main method is used to initialize all objects required to start a producer and a consumer
     */
   public static void main(String[] args) {
        Pipe pipe = new Pipe();//initialize a pipe that will shared between a producer and a consumer
        
        Producer producer = new Producer(pipe);//create a producer
        Consumer consumer = new Consumer(pipe);//create a consumer
       //start both
        producer.start();
        consumer.start();
       //read contents from the files to check if two processes communicated correctly
        readFile("produced.txt");
        readFile("consumed.txt");
   }
   
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

    /*
     * Pipe class is used to implement a pipe object that will be using
     * by a producer and a consumer in order to put and remove an item to/from it
     */
    public static class Pipe{
        private boolean empty;//indicator if the pipe is full or empty
        private int num;//an integer that will hold a produced element by a producer
        
        /*
         * Pipe constructor is use to initialize all required fields for a producer and a consumer
         */
        public Pipe(){
            empty = true;
            num = 0;
        }
        
        /*
         * placeItem method is used to place an item on the pipe by a producer
         * the method is sychronized so it can be accessed only by one object at a time
         * a producer will have to wait until the pipe is empty
         * so the item placed was consumed by the consumer
         */
        public synchronized void placeItem(int num){
            while(!empty){
                try {
                    wait();//wait
                } catch (InterruptedException ex) {
                    System.out.println("error is here " + ex.toString());
                }
            }//end while
            
            this.num= num;//place an item
            empty = false;//indicate that the pipe is full
            notifyAll();//notify all objects
        }
        /*
         * grabItem method is used to remove an item from the pipe by the consumer
         * the method is sychronized so it can be accessed only by one object at a time
         * a consumer will have to wait until the pipe is full
         * consumed item is returned to the consumer
         */
        public synchronized int grabItem(){
            while(empty){
                try {
                    wait();//wait
                } catch (InterruptedException ex) {
                    System.out.println("error is here " + ex.toString());
                    
                }
            }//end while
            
            empty = true;//indicate that the item was consumed
            notifyAll();//notify all objects
            return this.num;
        }
        
    }//end Pipe
    
    /*
     * Producer class is an implementation of an object that is used to produce
     * 100 data randomly and put it on the pipe
     * the class extends a thread
     */
    public static class Producer extends Thread{
        Pipe pipe;//a pipe to put items on
        private int[] randNums;//array of all random integers
        
        /*
         * Producer construcor is used to initialize all objects and data types
         * related to the producer and fill the array with integers
         */
        public Producer(Pipe pipe){
        this.pipe = pipe;//intialize the pipe
        randNums = new int[100];
        fillArray(randNums);//fill an array with integers from 0 to 99
        shuffleArray(randNums);//randomly shuffle integers
        }

        /*
         * run function is used to place all integers on the pipe and
         * write these integers to a file called produced.txt
         */
        @Override
        public void run() {
            try{
                
            
            PrintWriter write = new PrintWriter("produced.txt", "UTF-8");// writer to a file
            write.println("Produced items:");
            //put items on a pipe and write to a file
            for (int i = 0; i < randNums.length; i++) {
                pipe.placeItem(randNums[i]);
                write.println(randNums[i]);
              
            }
            //place a signal item to the pipe
            pipe.placeItem(9999);
            write.println(9999);
            write.close();//close file writer
            }
            catch(Exception e){
                
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
     * Consumer class is an implementation of a consumer object
     * that is used to consume all items that a producer puts on a pipe
     */
    public static class Consumer extends Thread{

    Pipe pipe;// pipe used by a consumer to take items from
    /*
     * Consumer constructor is used to initialize a pipe
     * for futher consumption of integers
     */
    public Consumer(Pipe pipe){
        this.pipe = pipe;

    }
    
    /*
     * run method is used to consume all elements that
     * producer puts on the pipe and write them to a file
     */
    @Override
    public  void run(){
        try{
           PrintWriter write = new PrintWriter("consumed.txt", "UTF-8");//file writer
           write.println("consumed items:");  
            
            
        int readNum =  pipe.grabItem();// consume first element
        write.println(readNum);// write to a file
        //consume integer data until 9999 is consumed (stop signal)
        while(readNum != 9999){
            readNum = pipe.grabItem();
            write.println(readNum);  
        }
        write.close();//close file writer
        }
        catch(Exception e){
            
        }
    }   
    }//end Consumer
    
}
