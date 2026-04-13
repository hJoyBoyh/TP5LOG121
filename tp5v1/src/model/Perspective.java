package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pattern.AbstractSubject;
import pattern.Observer;
import pattern.Subject;

/**
 * Patron Observer : ConcreteSubject #2
 * Serializable    : zoom et translation sont des primitives, donc directement sérialisables
 *
 * Représente l'état d'affichage d'une vue : facteur de zoom et décalage (translation).
 * Chaque PerspectiveView possède sa propre instance de Perspective.
 *
 * Correspondance patron Observer :
 *   ConcreteSubject > Perspective
 *   subjectState >  zoomFactor, translateX, translateY
 *   attach(Observer) > addObserver(Observer)
 *   notifyObservers() > notifyObservers()
 */
public class Perspective extends AbstractSubject  {

    private static final long serialVersionUID = 1L;

    // État de la perspective
    private double zoomFactor;
    private double translateX;
    private double translateY;

    // Constantes
    public static final double ZOOM_MIN    = 0.1;
    public static final double ZOOM_MAX    = 10.0;
    public static final double ZOOM_DEFAULT = 1.0;
    public static final double ZOOM_STEP   = 0.1;


    public Perspective() {
        this.zoomFactor = ZOOM_DEFAULT;
        this.translateX = 0.0;
        this.translateY = 0.0;
    }

    /** Constructeur de copie : utilisé par PerspectiveMemento */
    public Perspective(Perspective other) {
        this.zoomFactor = other.zoomFactor;
        this.translateX = other.translateX;
        this.translateY = other.translateY;
    }
    

    // Opérations sur la perspective

    /**
     * Modifie le facteur de zoom (borné entre ZOOM_MIN et ZOOM_MAX).
     * Notifie les observateurs après chaque modification.
     */
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, zoomFactor));
        notifyObservers();
    }

    /**
     * Applique un delta de zoom relatif (ex: +0.1 ou -0.1).
     */
    public void applyZoomDelta(double delta) {
        setZoomFactor(this.zoomFactor + delta);
    }

    /**
     * Modifie la translation.
     * Notifie les observateurs après chaque modification.
     */
    public void setTranslation(double translateX, double translateY) {
        this.translateX = translateX;
        this.translateY = translateY;
        notifyObservers();
    }

    /**
     * Applique un delta de translation relatif.
     */
    public void applyTranslationDelta(double deltaX, double deltaY) {
        setTranslation(this.translateX + deltaX, this.translateY + deltaY);
    }

    /**
     * Remet la perspective à son état initial.
     */
    public void reset() {
        this.zoomFactor = ZOOM_DEFAULT;
        this.translateX = 0.0;
        this.translateY = 0.0;
        notifyObservers();
    }

    /**
     * Recharge les observateurs après désérialisation.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.observers = new ArrayList<>();
    }

    // Getters / Setters directs (sans notification : pour Memento)
    public double getZoomFactor()  { return zoomFactor; }
    public double getTranslateX()  { return translateX; }
    public double getTranslateY()  { return translateY; }

    /**
     * Restaure l'état complet sans notification (utilisé par Memento).
     */
    public void restoreState(double zoomFactor, double translateX, double translateY) {
        this.zoomFactor = zoomFactor;
        this.translateX = translateX;
        this.translateY = translateY;
        notifyObservers();
    }

    @Override
    public String toString() {
        return String.format("Perspective[zoom=%.2f, tx=%.1f, ty=%.1f]",
                zoomFactor, translateX, translateY);
    }
}