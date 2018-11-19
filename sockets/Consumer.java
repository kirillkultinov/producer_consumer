import java.net.*;
import java.io.*;
import java.util.*;

/*
* Consumer class is used to create a Consumer object
* which initialize a consumer socket object in order to recieve
* data from the producerPort
* Consumer in this case is a client side in a server-client communication
*/
public class Consumer{

    /*
    * main method is used to connect to the producer socket in order to
    * recieve data from it using DataInputStream
    * recieved data is written to a file consumed.txt and then
    * the content from the file is printed to the screen
    */
    public static void main(String args[]){
        Socket socket = null;

        try{
            int producerPort = 3333;
            socket = new Socket(args[0], producerPort);//initialize socket
            DataInputStream in = new DataInputStream(socket.getInputStream());//create a DataInputStream for recieving data
            String data = "";

            //recieving numbers from consumer and writing them to a file
            try{
            PrintWriter write = new PrintWriter("consumed.txt", "UTF-8");// create a writer to a file
            write.println("Consumed items:");
            //consume all integers until done is recieved from the server
            while(true){
                data = in.readUTF();
                if(!data.equals("done") ){
                   write.println(data);
                }
                else{
                    break;
                }
            }
            write.close();//close PrintWriter
            

            readFile();//read integers from a file

            }
            catch(Exception e){
                System.out.println("writer: " + e.getMessage());
            }
        }
        catch(UnknownHostException e){
            System.out.println("Socket: " + e.getMessage());
        }
        catch(EOFException e){
            System.out.println("EOF: " + e.getMessage());
        }
        catch(IOException e){
            System.out.println("IO: " + e.getMessage());
        }
        finally{
            if(socket != null){
                try{
                    socket.close();
                }
                catch(IOException e){
                //close failed
                System.out.println("close failed: " + e.getMessage());
            }
            }
        }
    }//end main

    /*
    *readFile method is used to read all content from a file created
    * by a consumer (consumed.txt)
    * everything from the file is printed out to the screen
    */
    private static void readFile(){

        try {
            
            File file = new File("consumed.txt");
            Scanner inputFile = new Scanner(file);

            while (inputFile.hasNextLine()) {
                String line = inputFile.nextLine();
                System.out.println(line);
            }
            inputFile.close();

        } catch (Exception e) {
            System.out.println("reader: " + e.getMessage());
        }
    }//end readFile
}