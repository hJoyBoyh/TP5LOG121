package controller;

import model.Perspective;
import model.PerspectiveMemento;
import pattern.Command;
import pattern.CopyStrategy;

/**
 * Patron Command   — encapsule l'opération de collage (Paste) pour undo/redo.
 * Patron Strategy  — délègue la logique de copie à une CopyStrategy.
 * Patron Memento   — sauvegarde l'état cible AVANT le collage.
 *
 * Utilisé par le menu Presse-Papier → Coller.
 *
 * Correspondance patron Command :
 *   ConcreteCommand  →  PasteCommand
 *   execute()        →  applique la stratégie sur (source → target)
 *   undo()           →  restaure l'état de la cible avant le collage
 *
 * Correspondance patron Strategy :
 *   Context          →  PasteCommand
 *   strategy         →  CopyStrategy (ZoomOnly / TranslationOnly / Full)
 */
public class PasteCommand implements Command {

    private final Perspective        source;   // perspective copiée
    private final Perspective        target;   // perspective qui reçoit
    private final CopyStrategy       strategy; // ce qui est copié
    private       PerspectiveMemento savedState;

    /**
     * @param source    la perspective dont on copie les paramètres
     * @param target    la perspective qui reçoit les paramètres
     * @param strategy  la stratégie : zoom seul, translation seule, ou les deux
     */
    public PasteCommand(Perspective source, Perspective target, CopyStrategy strategy) {
        this.source   = source;
        this.target   = target;
        this.strategy = strategy;
    }

    @Override
    public void execute() {
        savedState = new PerspectiveMemento(target);  // sauvegarder AVANT
        strategy.apply(source, target);               // appliquer la stratégie
    }

    @Override
    public void undo() {
        if (savedState != null) {
            savedState.restore(target);
        }
    }

    @Override
    public String toString() {
        return "PasteCommand[stratégie=" + strategy + "]";
    }
}