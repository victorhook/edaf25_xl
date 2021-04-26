package model;

import expr.Environment;
import expr.Expr;
import expr.ExprParser;
import gui.CellSelectionObserver;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import util.XLBufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class XLModel{

  public static final int COLUMNS = 10, ROWS = 10;

  private ExprParser parser;
  private List<CellSelectionObserver> observers;

  public XLModel() {
    parser = new ExprParser();
  }

  /**
   * Called when the code for a cell changes.
   *
   * @param address address of the cell that is being edited
   * @param text    the new code for the cell - can be raw text (starting with #) or an expression
   */
  public void update(CellAddress address, String text) {
    System.out.printf("Address: %s, text: %s\n", address.toString(), text);
    try {
      Expr exression = parser.build(text);

      exression.
      System.out.println(exression.toString());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void loadFile(File file) throws FileNotFoundException {
    XLBufferedReader reader = new XLBufferedReader(file);
  }

  public void saveFile(File file) {
  }
}
