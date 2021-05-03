package model;

public class Cell {
    private CellAddress address;
    private String rawString;
    private String calculatedString;

    public Cell(int row, int col) {
        address = new CellAddress(row, col);
    }

    public CellAddress getAddress() {
        return address;
    }

    public void update(String rawValue, String calculatedValue) {
        this.rawString = rawValue;
        this.calculatedString = calculatedValue;
    }

}
