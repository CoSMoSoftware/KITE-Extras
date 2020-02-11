package io.cosmosoftware.kite.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

  /**
   * This command can be run on Linux to get the first nw interface listed with ifconfig
   * eth0      Link encap:Ethernet  HWaddr 12:f0:68:77:94:5d
   *           inet addr:172.31.94.150  Bcast:172.31.95.255  Mask:255.255.240.0
   *           .....
   * lo        Link encap:Local Loopback
   *           inet addr:127.0.0.1  Mask:255.0.0.0
   *           .....
   *
   * -----> will give 'eth0'
   */
  public static String GET_FIRST_INTERFACE_SCRIPT_LINUX = "ifconfig | awk '{print $1}' | head -n 1";

  /**
   * Calling this url gives back the public IP of the machine making the call
   */
  public static String GET_PUBLIC_IP_URL = "http://bot.whatismyipaddress.com";


  /**
   * Gets the public ip.
   *
   * @return the public ip
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String getMyPublicIp() throws IOException {
    InputStream is = null;
    InputStreamReader isr = null;
    BufferedReader br = null;
    String res = null;
    try {
      URL url_name = new URL(GET_PUBLIC_IP_URL);
      is = url_name.openStream();
      isr = new InputStreamReader(is);
      br = new BufferedReader(isr);
      res = br.readLine().trim();
    } catch (MalformedURLException e) {
      throw new IOException(e);
    } finally {
      try {
        if (br != null) {
          br.close();
        }
        if (isr != null) {
          isr.close();
        }
        if (is != null) {
          is.close();
        }
      } catch (Exception e) {
        // ignore
      }
    }

    return res;
  }

}
