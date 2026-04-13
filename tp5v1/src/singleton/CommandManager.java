package singleton;

import java.util.ArrayDeque;
import java.util.Deque;

import pattern.Command;

/**
 * Patron Singleton : instance unique de la gestion des commandes.
 * Patron Command  : mémorise les commandes exécutées pour undo/redo.
 *
 * Le CommandManager est le "Caretaker" du patron Memento :
 * il délègue la sauvegarde d'état aux commandes elles-mêmes.
 *
 * Correspondance patron Singleton :
 *   Singleton > CommandManager
 *   instance > instance (static)
 *   getInstance() >  getInstance()
 *
 * Correspondance patron Command :
 *   Invoker >  CommandManager
 *   executeCommand() >  executeCommand()
 *   undo() >  undo()
 *   redo() >  redo()
 */
public class CommandManager {

    private static CommandManager instance;

    /** Constructeur privé empêche l'instanciation directe */
    private CommandManager() {}

    /** Point d'accès global unique à l'instance */
    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    
    // Piles des commandes
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    // Opérations
    /**
     * Exécute une commande, la mémorise dans la pile undo,
     * et vide la pile redo (un nouveau geste annule le redo).
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();  // nouvelle action invalide le redo
    }

    /**
     * Annule la dernière commande exécutée.
     * Déplace la commande de undoStack vers redoStack.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Refait la dernière commande annulée (bonus Memento).
     * Déplace la commande de redoStack vers undoStack.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    /**
     * Vide les deux piles (ex: chargement d'une nouvelle image).
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    // État
    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public int getUndoCount() { return undoStack.size(); }
    public int getRedoCount() { return redoStack.size(); }
}