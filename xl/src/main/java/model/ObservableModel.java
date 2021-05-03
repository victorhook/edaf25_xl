package model;

/**
 * An interface that a model can implement to
 */
public interface ObservableModel {

    /**
     * Adds a new observer to the model.
     * @param observer Observer object to notify when a change has been made.
     */
    void addObserver(ModelObserver observer);

    /**
     * Clears all observers.
     */
    void clearObservers();

    /**
     * Notifies all observers for the given address.
     * @param address The address of the cell to notify.
     * @param newText The value the cell now contains.
     */
    void notifyObservers(String address, String newText);
}
