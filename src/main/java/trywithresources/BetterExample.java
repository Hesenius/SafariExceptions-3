package trywithresources;

import java.io.*;

public class BetterExample {
  public static void main(String[] args) throws FileNotFoundException {
    BufferedReader br = new BufferedReader(new FileReader("a.txt"));
    try (
        br; // can simply "refer" to a resource
        PrintWriter pw = new PrintWriter(new FileWriter("output.txt"));
    ) {
      String line;
      while ((line = br.readLine()) != null) {
        pw.println(line);
      }
    } catch (IOException ioe) {
      System.out.println("oops: " + ioe.getMessage());
    }
//    br = null; // NOPE! "resources" must be final or effectively final
    // auto-generated finally...
    // calls close on each "resource" in reverse order of appearance
    // in the TWR resource list (the parens stuff :)
    // catches all the relevant exceptions itself
    // The FIRST exception (which might be an unhandled business logic
    // exception, or could be a closure failure)
    // is rethrown after all closing complete
    // all the subsequent exceptions appear INSIDE the first as
    // "suppresed" exception
    // Throwable t -> t.getSuppressed()
    // can add a finally of our own
  }
}
