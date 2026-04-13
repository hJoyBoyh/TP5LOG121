package pattern;

/**
 * Patron Observer : interface Subject (source des notifications).
 * ImageModel et Perspective implémenteront tous les deux cette interface,
 * car ce sont deux sujets distincts dans le patron Observer.
 *
 * Correspondance patron :
 *   Subject (GoF) >  Subject (ici)
 *   attach(Observer) >  addObserver(Observer)
 *   detach(Observer) >  removeObserver(Observer)
 *   notifyObservers() >  notifyObservers()
 */
public interface Subject {

    void addObserver(Observer o);

    void removeObserver(Observer o);

    void notifyObservers();
}