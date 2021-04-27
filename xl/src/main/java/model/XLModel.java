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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XLModel implements ObservableModel, Environment {

  public static final int COLUMNS = 10, ROWS = 10;

  private ExprParser parser;
  private Map<String, String> sheet;
  private List<ModelObserver> observers;

  public XLModel() {
    parser = new ExprParser();
    observers = new ArrayList<>();

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
    // Store the text in the matrix.
    sheet.put(address.toString(), text);

    String resultText;

    try {
      Expr epxr = parser.build(text);
      ExprResult result = epxr.value(this);
      if (result.isError()) {
        resultText = result.error();
      } else {
        resultText = String.valueOf(result.value());
      }
    } catch (IOException e) {
      resultText = e.toString();
    }

    String finalResultText = resultText;
    observers.forEach(obs -> obs.modelChange(address, finalResultText));
  }

  @Override
  public ExprResult value(String address) {
    try {
      return parser.build(sheet.get(address)).value(this);
    } catch (IOException e) {
      return new ErrorResult("IOException");
    }

  }

  public String readCell(CellAddress address) {
    return sheet.get(address.toString());
  }

  public void loadFile(File file) throws FileNotFoundException {
    XLBufferedReader reader = new XLBufferedReader(file);
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

}
