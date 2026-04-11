package controller;

import model.Perspective;
import model.PerspectiveMemento;
import pattern.Command;

/**
 * Patron Command — remet une perspective à son état initial (zoom=1, tx=0, ty=0).
 * Utile pour un bouton "Réinitialiser" ou double-clic sur la vue.
 *
 * Correspondance patron Command :
 *   ConcreteCommand  →  ResetCommand
 *   execute()        →  sauvegarde Memento + reset
 *   undo()           →  restaure l'état précédent le reset
 */
public class ResetCommand implements Command {

    private final Perspective        perspective;
    private       PerspectiveMemento savedState;

    public ResetCommand(Perspective perspective) {
        this.perspective = perspective;
    }

    @Override
    public void execute() {
        savedState = new PerspectiveMemento(perspective);
        perspective.reset();
    }

    @Override
    public void undo() {
        if (savedState != null) {
            savedState.restore(perspective);
        }
    }

    @Override
    public String toString() {
        return "ResetCommand";
    }
}