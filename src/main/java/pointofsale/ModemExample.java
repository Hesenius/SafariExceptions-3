package pointofsale;

import java.io.IOException;
import java.net.Socket;

class ModemDidNotConnectException extends Exception {

}
class ModemLib {
  public static void dialModem() throws ModemDidNotConnectException {}
}

public class ModemExample {
  public static boolean USE_INTERNET = true;
  public static void getPaidByCC()
      throws ModemDidNotConnectException, IOException {
    try {
      if (USE_INTERNET) {
        Socket s = new Socket("127.0.0.1", 9000);
      } else {
        ModemLib.dialModem();
      }
      // communicate to get money
//    } catch (Exception me) { // OH NO, also catches everything else
    // multi-catch, gets any of the several types, but the types
    // must NOT be hierachically related
    } catch (ModemDidNotConnectException | IOException me) {
      // perhaps make three retries
      // if retry count == limit
      throw me;
    }
  }

  public static void sellStuff() {
    // collect stuff
    // input prices
    try {
      getPaidByCC();
    } catch (ModemDidNotConnectException me) {
      // business process solution
      // set a flag to ask for a different card???
    } catch (IOException ie) {
//      really?
    }

    // imagine "status codes"
//    if (status == -1) do this
//    else if (status == -2) do that
    // NOW what happens if status -3 is added??!!
  }


}
