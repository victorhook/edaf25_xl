package model;

import expr.*;
import util.XLBufferedReader;
import util.XLPrintStream;

import java.io.*;
import java.util.*;

/**
 * This class represents the XL sheet.
 */
public class XLModel implements ObservableModel, Environment {

  public static final int COLUMNS = 10, ROWS = 10;

  private ExprParser parser;

  // Maps from: CellAddress -> CellRawString
  private Map<String, String> sheet;

  // Contains all all observers to notify when needed.
  private List<ModelObserver> observers;

  // Used to identify circular dependencies.
  private Set<String> visited;

  public XLModel() {
    parser = new ExprParser();
    observers = new ArrayList<>();
    visited = new HashSet<>();

    // Create & fill a new sheet.
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
    // Store the RAW text in the map.
    sheet.put(address.toString(), text);

    // Update all cells.
    updateAll();
  }

  /*
     Updates all cells.
     Note that this method does NOT modify any values, but simply calculates the expression results and
     notifies the observers.
   */
  public void updateAll() {
    sheet.entrySet().forEach(entry -> {                         // Map is format:   Address : Raw string value
      String address = entry.getKey();
      String rawString = entry.getValue();

      String calculatedValue = calculateValue(rawString);       // Calculate the value for the frontend.
      notifyObservers(address, calculatedValue);                // Notify listening observers with the calculated value.
    });
  }

  @Override
  public ExprResult value(String address) {
    // Read the *raw* text from the XL sheet.
    String valueInSheet = sheet.get(address);

    // Check for possible errors in addressing.
    if (valueInSheet.equals("")) {
      return new ErrorResult("Referencing an empty cell!");
    } else if (isComment(valueInSheet)) {
      return new ErrorResult("Referencing a comment");
    } else if (isCell(valueInSheet)) {
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

    // Call the parser again to find the value from the address.
    // Note: This will cause recursion if cells are referencing other cells
    // eg: A1 -> B1 will cause the value of B1 to be calculated first, then A1.
    try {
      return parser.build(valueInSheet).value(this);
    } catch (IOException e) {
      // Parser
      return new ErrorResult(e.getMessage());
    }
  }

  /* Returns the raw string of the cell. */
  public String readCellRaw(CellAddress address) {
    return sheet.get(address.toString());
  }

  /* Opens the content of a file and puts it in the sheet. */
  public void loadFile(File file) throws IOException {
    XLBufferedReader reader = new XLBufferedReader(file);
    reader.load(sheet);
    updateAll();
  }

  /* Saves the sheet do a file on disk. */
  public void saveFile(File file) throws FileNotFoundException {
    XLPrintStream printStream = new XLPrintStream(file.getName());
    printStream.save(sheet.entrySet());
  }

  @Override
  public void addObserver(ModelObserver observer) {
    observers.add(observer);
  }

  @Override
  public void clearObservers() {
    observers.clear();
  }

  @Override
  public void notifyObservers(String address, String newText) {
    CellAddress cellAddress = stringToCellAddress(address);
    observers.forEach(obs -> obs.modelHasChanged(cellAddress, newText));
  }

  /* Clears a given cell. */
  public void clearCell(CellAddress address) {
    update(address, "");
  }

  /* Clears all cells. */
  public void clearAll() {
    for (String address: sheet.keySet()) {
      sheet.put(address, "");
    }
    updateAll();
  }

  /* --- Private --- */

  /* Returns an error message. */
  private String errorMessage(String error) {
    return "# ERROR: " + error;
  }

  /* Turns an address string into a CellAddress object. */
  private CellAddress stringToCellAddress(String address) {
    int col = address.substring(0, 1).toCharArray()[0] - 'A';
    int row = Integer.parseInt(address.substring(1)) - 1;
    return new CellAddress(col, row);
  }

  /* Calculates the value of a string expressions.
     Returns the finished (calculated) result as a string.
   */
  private String calculateValue(String expr) {
    // Returns early if input is invalid or empty.
    if (expr == null || expr.equals("")) {
      return "";
    }

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
          resultText = errorMessage(result.error());
        } else {
          // No errors, then this is the final string value.
          // result.value()
          double resultValue = result.value();
          if (resultValue % 1 == 0)                         // If we result is .0, we display it as an integer.
            resultText = String.valueOf((int) resultValue);
          else                                              // If the result contains decimal values, we include 4 decimals.
            resultText = String.format("%.4f", resultValue);
        }
      } catch (IOException e) {
        resultText = errorMessage(e.getMessage());
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
