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
import java.util.List;

public class XLModel implements ObservableModel, Environment {

  public static final int COLUMNS = 10, ROWS = 10;

  private ExprParser parser;
  private String sheet[][];
  private List<ModelObserver> observers;

  public XLModel() {
    parser = new ExprParser();
    sheet = new String[ROWS][COLUMNS];
    observers = new ArrayList<>();
  }

  /**
   * Called when the code for a cell changes.
   *
   * @param address address of the cell that is being edited
   * @param text    the new code for the cell - can be raw text (starting with #) or an expression
   */
  public void update(CellAddress address, String text) {
    // Store the text in the matrix.
    sheet[address.row][address.col] = text;

    String resultText;

    ExprResult result = value(text);
    if (result.isError()) {
      resultText = result.error();
    } else {
      resultText = String.valueOf(result.value());
    }

    String finalResultText = resultText;
    observers.forEach(obs -> obs.modelChange(address, finalResultText));
  }

  @Override
  public ExprResult value(String name) {
    try {
      return parser.build(name).value(this);
    } catch(XLException e) {
      System.out.println("Parse exception!");
      return new ErrorResult("XLEception");
      //e.printStackTrace();
    } catch(IOException e) {
      System.out.println("IOException!");
      return new ErrorResult("IOException");
    } catch(NullPointerException e) {
      System.out.printf("Nullpointer, text is probably empty: %s\n", name);
      return new ErrorResult("Nullpointer");
    }
  }

  public String readCell(CellAddress address) {
    return sheet[address.row][address.col];
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
