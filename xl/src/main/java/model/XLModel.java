package model;

import expr.*;
import gui.CellSelectionObserver;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import util.XLBufferedReader;
import util.XLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class XLModel implements ObservableModel, Environment {

  public static final int COLUMNS = 10, ROWS = 10;

  private ExprParser parser;
  private Map<String, String> sheet;
  private List<ModelObserver> observers;
  private Set<String> visited;

  public XLModel() {
    parser = new ExprParser();
    observers = new ArrayList<>();
    visited = new HashSet<>();

    // Create sheet.
    sheet = new HashMap<>();
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLUMNS; c++) {
        sheet.put(new CellAddress(r, c).toString(), "");
      }
    }
  }

  /**
   * Called when the code for a cell changes.
   *
   * @param address address of the cell that is being edited
   * @param text    the new code for the cell - can be raw text (starting with #) or an expression
   */
  public void update(CellAddress address, String text) {
    // Store the text in the map.
    sheet.put(address.toString(), text);
    // Update all cells.
    updateAll();
  }



  /*
     Updates ALL cells.
     Note that this method does NOT modify any values, but simply calculates the expression results and
     notifies the observers.
   */
  public void updateAll() {
    sheet.entrySet().forEach(entry -> update(entry.getKey(), entry.getValue()));
  }

  @Override
  public ExprResult value(String address) {
    // Read the *raw* text from the XL sheet.
    String valueInSheet = sheet.get(address);

    // Check for possible errors in addressing.
    if (valueInSheet == null) {
      System.out.printf("%s: %s, %s\n", address, valueInSheet, sheet);
      return new ErrorResult("Referencing an empty cell!");
    } else if (isComment(valueInSheet)) {
      return new ErrorResult("Referencing a comment");
    }

    if (isCell(valueInSheet)) {
      // If the value is an address, we need to cache it to ensure we're not circulating!
      // String is format of: CELL_FROM->CELL_TO (without the arrow)
      String cacheString = address + valueInSheet;

      if (visited.contains(cacheString)) {
        // If we've already visited this address combination, we exit.
        return new ErrorResult("Circular dependencies!");
      }

      // Mark that we've visited the address combination.
      visited.add(cacheString);
    }

    try {
      return parser.build(valueInSheet).value(this);
    } catch (IOException e) {
      return new ErrorResult(e.getMessage());
    }
  }


  /* Returns the raw string of the cell. */
  public String readCellRaw(CellAddress address) {
    return sheet.get(address.toString());
  }

  public void loadFile(File file) throws IOException {
    XLBufferedReader reader = new XLBufferedReader(file);
    String line;
    while ((line = reader.readLine()) != null) {
      String split[] = line.split("=");
      String cell = split[0];
      String value = split[1];
      //sheet.put(new CellAddress(), value);
    }
  }

  public void saveFile(File file) {
  }

  @Override
  public void addListenever(ModelObserver observer) {
    observers.add(observer);
  }

  @Override
  public void deleteAllListeners() {
    observers.clear();
  }


  /* --- Private --- */

  /* Calculates the value of a string expressions. */
  private String calculateValue(String text) {
    String resultText = "";

    if (text != null && !text.equals("")) {
      resultText = parseExpr(text);
    }

    return resultText;
  }

  /* Updates a given cell. */
  private void update(String address, String text) {
    int col = address.substring(0, 1).toCharArray()[0] - 'A';
    int row = Integer.parseInt(address.substring(1)) - 1;

    CellAddress cellAddress = new CellAddress(col, row);
    String value = calculateValue(text);
    notifyObservers(cellAddress, value);
  }

  /* Notifies all observers. */
  private void notifyObservers(CellAddress address, String value) {
    observers.forEach(obs -> obs.modelChange(address, value));
  }

  /* Helper method to parse an expression.
     Returns the finished (polished) result as a string.
   */
  private String parseExpr(String expr) {
    // Clear visited address combinations.
    visited.clear();
    String resultText = "";

    if (isComment(expr)) {
      resultText = expr.substring(1);
    } else {
      try {
        // Build the expression.
        Expr epxr = parser.build(expr);

        // Parse the expression, which can lead to value() being called.
        ExprResult result = epxr.value(this);

        // Do some cleanup of the text format.
        if (result.isError()) {
          resultText = "# ERROR: " + result.error();
        } else {
          resultText = String.valueOf(result.value());
        }
      } catch (IOException e) {
        resultText = "# ERROR: " + e.getMessage();
      }
    }
    return resultText;
  }

  /* Helper method to tell if the input string is a comment or not. */
  private boolean isComment(String input) {
    return input.startsWith("#");
  }

  /* Helper method to tell if a given address is a cell or not. */
  private boolean isCell(String address) {
    return sheet.keySet().contains(address);
  }

}
