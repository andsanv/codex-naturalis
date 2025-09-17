package common;

import java.util.HashMap;
import java.util.Map;

public final class Config {
  public static int RMIServerPort = 21417;
  public static String RMIServerName = "ServerActions";

  public static String ServerIP = "127.0.0.1";
  public static int MainSocketPort = 23837;

  public static void setRMIServerPort(int port) {
    RMIServerPort = port;
  }

  public static void setServerIP(String ip) {
    ServerIP = ip;
  }

  public static void setMainSocketPort(int port) {
    MainSocketPort = port;
  }

  public static Boolean setUp(String[] args) {
    Map<String, String> params = new HashMap<>();

    for (String arg : args) {
      if (arg.startsWith("--")) {
        String[] param = arg.substring(2).split("=", 2);
        if (param.length == 2) {
          params.put(param[0], param[1]);
        } else {
          System.out.println("Invalid parameter: " + arg);
          return false;
        }
      }
    }

    if (params.containsKey("server-ip"))
      Config.setServerIP(params.get("server-ip"));
    if (params.containsKey("socket-port"))
      Config.setMainSocketPort(Integer.parseInt(params.get("socket-port")));
    if (params.containsKey("rmi-port"))
      Config.setRMIServerPort(Integer.parseInt(params.get("rmi-port")));

    return true;
  }
}
