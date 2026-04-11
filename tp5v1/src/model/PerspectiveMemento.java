package model;

import java.io.Serializable;

/**
 * Patron Memento — sauvegarde l'état interne d'une Perspective.
 * Utilisé par ZoomCommand et TranslationCommand pour permettre le undo/redo
 * sans exposer l'état interne de Perspective.
 *
 * Correspondance patron Memento :
 *   Memento         →  PerspectiveMemento
 *   Originator      →  Perspective
 *   Caretaker       →  CommandManager (via les Command)
 *   state           →  zoomFactor, translateX, translateY
 *   getState()      →  getters ci-dessous
 */
public class PerspectiveMemento implements Serializable {

    private static final long serialVersionUID = 1L;

    // État capturé de la Perspective (immuable après création)
    private final double zoomFactor;
    private final double translateX;
    private final double translateY;

    /**
     * Crée un Memento en capturant l'état actuel de la perspective donnée.
     * @param perspective la perspective dont on sauvegarde l'état
     */
    public PerspectiveMemento(Perspective perspective) {
        this.zoomFactor = perspective.getZoomFactor();
        this.translateX = perspective.getTranslateX();
        this.translateY = perspective.getTranslateY();
    }

    // -------------------------------------------------------------------------
    // Getters (lecture seule — le Memento ne doit pas être modifiable)
    // -------------------------------------------------------------------------

    public double getZoomFactor() { return zoomFactor; }
    public double getTranslateX() { return translateX; }
    public double getTranslateY() { return translateY; }

    /**
     * Restaure l'état sauvegardé dans la perspective donnée.
     * @param perspective la perspective à restaurer
     */
    public void restore(Perspective perspective) {
        perspective.restoreState(zoomFactor, translateX, translateY);
    }

    @Override
    public String toString() {
        return String.format("Memento[zoom=%.2f, tx=%.1f, ty=%.1f]",
                zoomFactor, translateX, translateY);
    }
}