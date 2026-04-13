package controller;

import model.Perspective;
import pattern.CopyStrategy;

/**
 * Patron Command   : encapsule l'opération de collage (Paste) pour undo/redo.
 * Patron Strategy  : délègue la logique de copie à une CopyStrategy.
 * Patron Memento   : géré par la classe mère AbstractPerspectiveCommand.
 *
 * Correspondance patron Strategy :
 * Context >  PasteCommand
 * strategy >  CopyStrategy (ZoomOnly / TranslationOnly / Full)
 */
public class PasteCommand extends AbstractPerspectiveCommand {

    private final Perspective source;   // perspective copiée
    private final CopyStrategy strategy; // ce qui est copié

    /**
     * @param source    la perspective dont on copie les paramètres
     * @param target    la perspective qui reçoit les paramètres (gérée par la classe mère)
     * @param strategy  la stratégie : zoom seul, translation seule, ou les deux
     */
    public PasteCommand(Perspective source, Perspective target, CopyStrategy strategy) {
        // On passe 'target' à la classe mère car c'est la vue modifiée (qui doit être sauvegardée)
        super(target);
        this.source = source;
        this.strategy = strategy;
    }

    @Override
    protected void doAction() {
        // 'this.perspective' provient de AbstractPerspectiveCommand (c'est notre target)
        strategy.apply(source, this.perspective);
    }

    @Override
    public String toString() {
        return "PasteCommand[stratégie=" + strategy + "]";
    }
}