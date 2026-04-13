package controller;

import model.Perspective;

/**
 * Patron Command  : encapsule l'opération de zoom pour permettre undo/redo.
 * Patron Memento  : géré par la classe mère AbstractPerspectiveCommand.
 *
 * Correspondance patron Command :
 * ConcreteCommand  > ZoomCommand
 * doAction() >  applique zoomDelta
 */
public class ZoomCommand extends AbstractPerspectiveCommand {

    private final double zoomDelta;

    /**
     * @param perspective  la perspective ciblée (vue 1 ou vue 2)
     * @param zoomDelta    variation de zoom (+0.1 = zoom avant, -0.1 = zoom arrière)
     */
    public ZoomCommand(Perspective perspective, double zoomDelta) {
        super(perspective);
        this.zoomDelta = zoomDelta;
    }

    @Override
    protected void doAction() {
        this.perspective.applyZoomDelta(zoomDelta);
    }

    @Override
    public String toString() {
        return String.format("ZoomCommand[delta=%.2f]", zoomDelta);
    }
}