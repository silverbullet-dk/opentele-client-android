package dk.silverbullet.telemed.rest.listener;

public interface RetrieveEntityListener<T> {
    void retrieveError();
    void retrieved(T result);
}
