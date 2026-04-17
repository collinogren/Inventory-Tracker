package ogren.collin.inventorytracker.aws.services;

public interface ServiceCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}
