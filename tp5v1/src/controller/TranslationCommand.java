package controller;

import model.Perspective;

/**
 * Patron Command : encapsule l'opération de translation pour permettre undo/redo.
 * Patron Memento : géré par la classe mère AbstractPerspectiveCommand.
 *
 * Correspondance patron Command :
 * ConcreteCommand >  TranslationCommand
 * doAction() > applique deltaX/deltaY
 */
public class TranslationCommand extends AbstractPerspectiveCommand {

    private final double deltaX;
    private final double deltaY;

    /**
     * @param perspective  la perspective ciblée (vue 1 ou vue 2)
     * @param deltaX       déplacement horizontal en pixels
     * @param deltaY       déplacement vertical en pixels
     */
    public TranslationCommand(Perspective perspective, double deltaX, double deltaY) {
        super(perspective);
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    protected void doAction() {
        // L'attribut 'this.perspective' est hérité de la classe mère
        this.perspective.applyTranslationDelta(deltaX, deltaY);
    }

    @Override
    public String toString() {
        return String.format("TranslationCommand[dx=%.1f, dy=%.1f]", deltaX, deltaY);
    }
}