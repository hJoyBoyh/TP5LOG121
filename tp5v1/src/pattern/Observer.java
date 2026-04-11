package pattern;

/**
 * Patron Observer — interface Observer (abonné).
 * Toute vue qui veut être notifiée des changements du modèle doit
 * implémenter cette interface.
 *
 * Correspondance patron :
 *   Observer (GoF)  →  Observer (ici)
 *   update()        →  update()
 */
public interface Observer {

    /**
     * Appelé par le Subject lorsque son état change.
     * La vue doit se redessiner en réponse à cet appel.
     */
    void update();
}