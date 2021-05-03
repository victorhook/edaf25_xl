package gui;

import gui.menu.XLMenu;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CellAddress;
import model.XLModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XL extends Application {
  ObjectProperty<GridCell> currentCell = new SimpleObjectProperty<>();
  Map<String, GridCell> cells = new HashMap<>();
  XLModel model = new XLModel();

  public XL() {
    model.addObserver((CellAddress address, String newValue) -> {
        cellValueUpdated(address.toString(), newValue);
    });
  }

  public void onCellSelected(GridCell cell) {
    currentCell.set(cell);
  }

  @Override
  public void start(Stage stage) throws Exception {
    GridPane sheet = new GridPane();
    for (int c = 0; c < XLModel.COLUMNS; ++c) {
      Label lbl = new ColumnHeader(c);
      GridPane.setConstraints(lbl, c + 1, 0);
      sheet.getChildren().add(lbl);
    }
    Label addressLbl = new Label("?? =");
    addressLbl.setMinWidth(35);

    /* -- Create cells -- */
    for (int r = 0; r < XLModel.ROWS; ++r) {
      Label lbl = new RowHeader(r);
      GridPane.setConstraints(lbl, 0, r + 1);
      sheet.getChildren().add(lbl);
    }
    for (int r = 0; r < XLModel.ROWS; ++r) {
      for (int c = 0; c < XLModel.COLUMNS; ++c) {
        CellAddress address = new CellAddress(c, r);
        GridCell cell = new GridCell(address, this::onCellSelected);
        cells.put(address.toString(), cell);
        GridPane.setConstraints(cell, c + 1, r + 1);
        sheet.getChildren().add(cell);
      }
    }

    /* -- Editor -- */
    TextField editor = new TextField();
    editor.setMinWidth(320);
    editor.setDisable(true);
    editor.setOnAction(event -> {
      // This listener is called when the user presses the enter key in the editor.
      GridCell cell = currentCell.get();
      if (cell != null) {

        model.update(cell.address, editor.getText());
        model.updateAll();
      }
    });

    /* -- Current cell callbacks -- */
    currentCell.addListener((observable, oldValue, newValue) -> {
      if (oldValue != null) {
        oldValue.onDeselect();
      }
      if (newValue != null) {
        addressLbl.setText(newValue.address.toString() + " =");
        editor.setDisable(false);

        // Update text editor.
        String text = model.readCellRaw((newValue.address));
        editor.setText(text);

        editor.requestFocus();
      } else {
        addressLbl.setText("?? =");
        editor.setDisable(true);
      }
    });

    /* -- UI & cells. -- */
    HBox editBox = new HBox(5);
    editBox.setAlignment(Pos.BASELINE_LEFT);
    editBox.getChildren().add(addressLbl);
    editBox.getChildren().add(editor);
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(sheet);

    VBox content = new VBox(5);
    content.setPadding(new Insets(10));
    content.getChildren().add(editBox);
    content.getChildren().add(scrollPane);

    scrollPane.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    VBox container = new VBox();
    container.getChildren().add(new XLMenu(this, stage));
    container.getChildren().add(content);
    VBox.setVgrow(content, Priority.ALWAYS);

    /* -- Create the scene -- */
    Scene scene = new Scene(container);
    stage.setTitle("XL - Sheet1");
    stage.setScene(scene);
    stage.show();
  }

  public void cellValueUpdated(String address, String value) {
    GridCell cell = cells.get(address);
    if (cell != null) {
      cell.setText(value);                    // Updates the editor text
      cell.setTooltip(new Tooltip(value));    // Updates the tooltip text.
    }
  }

  public void loadFile(File file) {
    try {
      model.loadFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void clearSelected() {
    model.clearCell(currentCell.get().address);
  }

  public void clearAll() {
    model.clearAll();
  }

  public void saveFile(File file) {
    model.saveFile(file);
  }
}
