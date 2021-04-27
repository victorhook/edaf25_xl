package model;

public interface ModelObserver {
    void modelChange(CellAddress cell, String value);
}
