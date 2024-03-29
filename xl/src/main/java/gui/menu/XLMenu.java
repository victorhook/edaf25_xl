package gui.menu;

import gui.XL;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import model.XLModel;

public class XLMenu extends MenuBar {
  public XLMenu(XL xl, Stage stage) {
    Menu fileMenu = new Menu("File");
    MenuItem save = new SaveMenuItem(xl, stage);
    MenuItem load = new LoadMenuItem(xl, stage);
    MenuItem exit = new MenuItem("Exit");
    exit.setOnAction(event -> stage.close());
    fileMenu.getItems().addAll(save, load, exit);

    Menu editMenu = new Menu("Edit");
    MenuItem clear = new MenuItem("Clear");
    clear.setOnAction(event -> {
      xl.clearSelected();
    });
    MenuItem clearAll = new MenuItem("ClearAll");
    clearAll.setOnAction(event -> {
      xl.clearAll();
    });
    editMenu.getItems().addAll(clear, clearAll);
    getMenus().addAll(fileMenu, editMenu);
  }
}
