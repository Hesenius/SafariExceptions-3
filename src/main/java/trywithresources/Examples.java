package trywithresources;

import java.io.*;

public class Examples {
  public static void main(String[] args) {
    BufferedReader br = null;
    PrintWriter pw = null;
    try {
      FileReader input = new FileReader("a.txt");
      FileWriter output = new FileWriter("output.txt");
      /*BufferedReader */br = new BufferedReader(input);
      /*PrintWriter */pw = new PrintWriter(output);
      String line;
      while ((line = br.readLine()) != null) {
        pw.println(line);
      }
//      pw.close(); // this might not happen in the case of an exceptoin above
    } catch (IOException ioe) {
      System.out.println("oops: " + ioe.getMessage());
    } finally { // finally always "at least starts"
      // but, if we get an unhandled exception in here, it might not finish
      if(Math.random() > 0.5) throw new RuntimeException();
      if (pw != null) {
        pw.close();
      }
      if (br != null) {
        try {
          br.close(); // was ... OUT OF SCOPE!!!
        } catch (IOException e) {
          // report this???;
        }
      }
    }
  }
}
