package controller;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import model.Perspective;
import singleton.CommandManager;
import view.PerspectiveView;

/**
 * Contrôleur souris — traduit les gestes de l'utilisateur en Commands.
 *
 *   Molette (scroll)    →  ZoomCommand    (+/- ZOOM_STEP selon direction)
 *   Bouton gauche drag  →  TranslationCommand (delta depuis dernière position)
 *
 * Chaque commande est transmise au CommandManager (Singleton) qui
 * l'exécute ET la mémorise dans la pile undo.
 *
 * Correspondance MVC :
 *   Controller  →  MouseController
 *   Model       →  Perspective  (modifiée indirectement via Command)
 *   View        →  PerspectiveView  (source des événements souris)
 *
 * Correspondance patron Command :
 *   Invoker     →  MouseController (crée et transmet les commandes)
 */
public class MouseController {

    private final PerspectiveView view;
    private final Perspective     perspective;

    // Dernière position connue de la souris (pour calculer le delta de translation)
    private double lastMouseX;
    private double lastMouseY;

    // -------------------------------------------------------------------------
    // Constructeur — branche tous les listeners sur le Canvas de la vue
    // -------------------------------------------------------------------------

    public MouseController(PerspectiveView view, Perspective perspective) {
        this.view        = view;
        this.perspective = perspective;

        view.getCanvas().setOnScroll(this::handleScroll);
        view.getCanvas().setOnMousePressed(this::handleMousePressed);
        view.getCanvas().setOnMouseDragged(this::handleMouseDragged);
        view.getCanvas().setOnMouseReleased(this::handleMouseReleased);

        // Curseur main pour indiquer que le panneau est déplaçable
        view.getCanvas().setOnMouseEntered(e ->
                view.getCanvas().setCursor(Cursor.OPEN_HAND));
        view.getCanvas().setOnMouseExited(e ->
                view.getCanvas().setCursor(Cursor.DEFAULT));
    }

    // -------------------------------------------------------------------------
    // Gestionnaires d'événements
    // -------------------------------------------------------------------------

    /**
     * Molette vers le haut  → zoom avant (+ZOOM_STEP)
     * Molette vers le bas   → zoom arrière (-ZOOM_STEP)
     */
    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY() > 0
                ? Perspective.ZOOM_STEP
                : -Perspective.ZOOM_STEP;

        ZoomCommand cmd = new ZoomCommand(perspective, delta);
        CommandManager.getInstance().executeCommand(cmd);

        event.consume();
    }

    /** Mémorise la position initiale du clic pour calculer le delta. */
    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            view.getCanvas().setCursor(Cursor.CLOSED_HAND);
        }
        event.consume();
    }

    /**
     * À chaque déplacement, crée une TranslationCommand avec le delta
     * depuis la position précédente, puis met à jour lastMouseX/Y.
     */
    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            double deltaX = event.getX() - lastMouseX;
            double deltaY = event.getY() - lastMouseY;

            // Ignorer les micro-mouvements (évite de polluer la pile undo)
            if (Math.abs(deltaX) >= 1.0 || Math.abs(deltaY) >= 1.0) {
                TranslationCommand cmd =
                        new TranslationCommand(perspective, deltaX, deltaY);
                CommandManager.getInstance().executeCommand(cmd);

                lastMouseX = event.getX();
                lastMouseY = event.getY();
            }
        }
        event.consume();
    }

    /** Restaure le curseur à main ouverte après relâchement. */
    private void handleMouseReleased(MouseEvent event) {
        view.getCanvas().setCursor(Cursor.OPEN_HAND);
        event.consume();
    }
}