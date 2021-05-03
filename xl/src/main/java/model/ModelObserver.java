package model;

/**
 * A simple observer interface used to observe changes on a model.
 */
public interface ModelObserver {
    /**
     *
     * @param cell The address of the cell that has changed.
     * @param value The new value that the cell now contains.
     */
    void modelHasChanged(CellAddress cell, String value);
}
