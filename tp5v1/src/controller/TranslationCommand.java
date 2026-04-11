package controller;

import model.Perspective;
import model.PerspectiveMemento;
import pattern.Command;

/**
 * Patron Command — encapsule l'opération de translation pour permettre undo/redo.
 * Patron Memento — sauvegarde l'état AVANT d'exécuter pour pouvoir restaurer.
 *
 * Correspondance patron Command :
 *   ConcreteCommand  →  TranslationCommand
 *   Receiver         →  Perspective
 *   execute()        →  sauvegarde Memento + applique deltaX/deltaY
 *   undo()           →  restaure depuis le Memento
 */
public class TranslationCommand implements Command {

    private final Perspective        perspective;
    private final double             deltaX;
    private final double             deltaY;
    private       PerspectiveMemento savedState;

    /**
     * @param perspective  la perspective ciblée (vue 1 ou vue 2)
     * @param deltaX       déplacement horizontal en pixels
     * @param deltaY       déplacement vertical en pixels
     */
    public TranslationCommand(Perspective perspective, double deltaX, double deltaY) {
        this.perspective = perspective;
        this.deltaX      = deltaX;
        this.deltaY      = deltaY;
    }

    /** Sauvegarde l'état courant puis applique la translation. */
    @Override
    public void execute() {
        savedState = new PerspectiveMemento(perspective);  // Memento AVANT
        perspective.applyTranslationDelta(deltaX, deltaY);
    }

    /** Restaure la position avant la translation. */
    @Override
    public void undo() {
        if (savedState != null) {
            savedState.restore(perspective);               // Memento RESTAURÉ
        }
    }

    @Override
    public String toString() {
        return String.format("TranslationCommand[dx=%.1f, dy=%.1f]", deltaX, deltaY);
    }
}