package controller;

import model.Perspective;
import model.PerspectiveMemento;
import pattern.Command;

/**
 * Patron Command  — encapsule l'opération de zoom pour permettre undo/redo.
 * Patron Memento  — sauvegarde l'état AVANT d'exécuter pour pouvoir restaurer.
 *
 * Correspondance patron Command :
 *   Command (GoF)    →  Command (interface)
 *   ConcreteCommand  →  ZoomCommand
 *   Receiver         →  Perspective
 *   execute()        →  sauvegarde Memento + applique zoomDelta
 *   undo()           →  restaure depuis le Memento
 *
 * Correspondance patron Memento :
 *   Originator   →  Perspective
 *   Memento      →  PerspectiveMemento
 *   Caretaker    →  ZoomCommand (conserve savedState)
 */
public class ZoomCommand implements Command {

    private final Perspective      perspective;
    private final double           zoomDelta;
    private       PerspectiveMemento savedState;

    /**
     * @param perspective  la perspective ciblée (vue 1 ou vue 2)
     * @param zoomDelta    variation de zoom (+0.1 = zoom avant, -0.1 = zoom arrière)
     */
    public ZoomCommand(Perspective perspective, double zoomDelta) {
        this.perspective = perspective;
        this.zoomDelta   = zoomDelta;
    }

    /** Sauvegarde l'état courant puis applique le zoom. */
    @Override
    public void execute() {
        savedState = new PerspectiveMemento(perspective);  // Memento AVANT
        perspective.applyZoomDelta(zoomDelta);
    }

    /** Restaure l'état sauvegardé avant le zoom. */
    @Override
    public void undo() {
        if (savedState != null) {
            savedState.restore(perspective);               // Memento RESTAURÉ
        }
    }

    @Override
    public String toString() {
        return String.format("ZoomCommand[delta=%.2f]", zoomDelta);
    }
}