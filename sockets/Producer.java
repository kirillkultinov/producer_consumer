import java.net.*;
import java.io.*;
import java.util.*;
// Code base is used from the reference provided on Pilot

/*
* Producer class is used to create a producer object
* that used to create a socket connection over TCP and transfer 
* 100 randomly generated data
* Producer class is the implementation of a server socket if we talk about it in 
* server-client relationship
*/
public class Producer{
    /*
    * main function is used to create a server socket with all needed properties
    * and create new connection with consumer by accepting consumer socket
    */
    public static void main(String args[]){
        try{
            int producerPort = 3333;
            ServerSocket listen = new ServerSocket(producerPort);//producer socket
            listen.setSoTimeout(10000);// set timeout
                Socket consumer = listen.accept();
                Connection transferData = new Connection(consumer);
        }
        catch(IOException e){
            System.out.println("Listen: " + e.getMessage());
        }
    }
}

/*
* Connection class represents an object that implements 
* data exchange between a producer and a consumer
* Connection class extends Thread
*/
class Connection extends Thread{
    //DataInputStream in;//data stream for receiving data from the consumer
    DataOutputStream out;// data stream for sending data to the consumer
    Socket consumer;//consumer socket
    private int[] randNums;//an array that will hold 100 randomly generated data

    /*
    * Connection constructor is used to initiallize Connection object
    * Socket is taken as a parameter 
    * consumer socket is initiallized
    * data streams are initiallized
    * an array of random data is initiallized and filled with integers
    * the thread is started after the object is initiallized
    */
    public Connection(Socket consumerSocket){
        try{
            consumer = consumerSocket;
            out = new DataOutputStream(consumer.getOutputStream());

            randNums = new int[100];
            fillArray(randNums);//fill the array with int from 0 to 100
            shuffleArray(randNums);//shuffle all integers in the array

            this.start();//start the thread
        }
        catch(IOException e){
            System.out.println("Connection: " + e.getMessage());
        }
    }

    /*
    * run() method is used to send 100 data to the consumer using 
    * output stream
    * all data sent to the consumer is stored in a file called produced.txt
    */
    public void run(){

            int count = 0;//count how many data is sent
            //send data to a consumer and write data to a file
            try{
            PrintWriter write = new PrintWriter("produced.txt", "UTF-8");//create a new writer to a file
            write.println("Produced items:");
            //sending data
            while(count < 100){
                out.writeUTF(Integer.toString(randNums[count]));
                write.println(Integer.toString(randNums[count]));
                count++;
            }
            out.writeUTF("done");
            write.close();//close PrintWriter
           // System.out.println("Done sending data");

            readFile();//display produced items from the file

            }
            catch(Exception e){
                System.out.println("writer: " + e.getMessage());
            }

            try{
                consumer.close();//close consumer socket
            }
            catch(IOException e){
                //close failed
                System.out.println("close failed: " + e.getMessage());
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
      
    Random random = new Random();//create random number
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

    /*
    *readFile method is used to read all content from a file created
    * by a producer (produced.txt)
    * everything from the file is printed out to the screen
    */
    private void readFile(){

        try {
            
            File file = new File("produced.txt");
            Scanner inputFile = new Scanner(file);
            //print out each line while there is one
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