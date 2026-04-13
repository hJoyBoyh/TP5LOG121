package controller;

import model.Perspective;
import model.PerspectiveMemento;
import pattern.Command;

public abstract class AbstractPerspectiveCommand implements Command {
    
    protected final Perspective perspective;
    private PerspectiveMemento savedState;

    public AbstractPerspectiveCommand(Perspective perspective) {
        this.perspective = perspective;
    }

    // final empêche les enfants de changer l'ordre de cette logique cruciale
    @Override
    public final void execute() {
        savedState = new PerspectiveMemento(perspective); // 1. Memento sauvegardé
        doAction();                                       
    }

    //  Restauration de l'état précédent
    @Override
    public final void undo() {
        if (savedState != null) {
            savedState.restore(perspective);        
        }
    }

    // Ce que chaque commande spécifique doit faire
    protected abstract void doAction();
}