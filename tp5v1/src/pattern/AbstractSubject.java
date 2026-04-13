package pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSubject implements Subject, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // transient car on ne sérialise pas les interfaces graphiques (Vues)
    protected transient List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        if (observers != null) {
            for (Observer o : observers) {
                o.update();
            }
        }
    }

    // Utilisé par les classes enfants lors de la désérialisation
    protected void initObservers() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
    }
}