package controller;

import model.Perspective;

/**
 * Patron Command : remet une perspective à son état initial (zoom=1, tx=0, ty=0).
 * Utile pour un bouton "Réinitialiser" ou double-clic sur la vue.
 * Patron Memento : géré par la classe mère AbstractPerspectiveCommand.
 *
 * Correspondance patron Command :
 * ConcreteCommand >  ResetCommand
 * doAction() >  applique la réinitialisation
 */
public class ResetCommand extends AbstractPerspectiveCommand {

    public ResetCommand(Perspective perspective) {
        super(perspective);
    }

    @Override
    protected void doAction() {
        // L'attribut 'this.perspective' est hérité de la classe mère
        this.perspective.reset();
    }

    @Override
    public String toString() {
        return "ResetCommand";
    }
}