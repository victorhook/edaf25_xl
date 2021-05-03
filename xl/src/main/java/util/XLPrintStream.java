package util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class XLPrintStream extends PrintStream {
  public XLPrintStream(String fileName) throws FileNotFoundException {
    super(fileName);
  }

  public void save(Set<Entry<String, String>> set) {
    for (Map.Entry<String,String>  cell: set) {
      String address = cell.getKey();
      String value = cell.getValue();
      if (value != null && !value.equals("")) {
        println(String.format("%s=%s", address, value));
      }
    }
    flush();
    close();
  }
}
