package pattern;

/**
 * Patron Command : interface Command.
 * Chaque opération (zoom, translation, chargement) sera encapsulée
 * dans une classe concrète qui implémente cette interface.
 * Cela permet de mémoriser et d'annuler (undo) chaque opération.
 *
 * Correspondance patron :
 *   Command (GoF) >  Command (ici)
 *   execute() >  execute()
 *   undo() >  undo()
 */
public interface Command {

    /**
     * Exécute l'opération et sauvegarde l'état précédent (pour undo).
     */
    void execute();

    /**
     * Annule l'opération en restaurant l'état précédent.
     */
    void undo();
}