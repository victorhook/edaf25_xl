package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class XLBufferedReader extends BufferedReader {
  public XLBufferedReader(File file) throws FileNotFoundException {
    super(new FileReader(file));
  }

  public void load(Map<String, String> map) throws IOException {
    try {
      while (ready()) {
        String line;
        while ((line = readLine()) != null) {
          String split[] = line.split("=");
          String address = split[0];
          String value = split[1];
          map.put(address, value);
        }
      }
    } catch (Exception e) {
      throw new XLException(e.getMessage());
    }
  }
}
