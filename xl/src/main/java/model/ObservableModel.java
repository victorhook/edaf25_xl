package model;

public interface ObservableModel {
    void addListenever(ModelObserver observer);
    void deleteAllListeners();
}
