package controller;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import model.Perspective;
import singleton.CommandManager;
import view.PerspectiveView;

public class MouseController {

    private final PerspectiveView view;
    private final Perspective perspective;

    // On mémorise le point de départ global et le dernier point pour l'affichage fluide
    private double startMouseX;
    private double startMouseY;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;

    public MouseController(PerspectiveView view, Perspective perspective) {
        this.view = view;
        this.perspective = perspective;

        view.getCanvas().setOnScroll(this::handleScroll);
        view.getCanvas().setOnMousePressed(this::handleMousePressed);
        view.getCanvas().setOnMouseDragged(this::handleMouseDragged);
        view.getCanvas().setOnMouseReleased(this::handleMouseReleased);

        view.getCanvas().setOnMouseEntered(e -> view.getCanvas().setCursor(Cursor.OPEN_HAND));
        view.getCanvas().setOnMouseExited(e -> view.getCanvas().setCursor(Cursor.DEFAULT));
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY() > 0 ? Perspective.ZOOM_STEP : -Perspective.ZOOM_STEP;
        CommandManager.getInstance().executeCommand(new ZoomCommand(perspective, delta));
        event.consume();
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            startMouseX = event.getX();
            startMouseY = event.getY();
            lastMouseX = startMouseX;
            lastMouseY = startMouseY;
            isDragging = false;
            view.getCanvas().setCursor(Cursor.CLOSED_HAND);
        }
        event.consume();
    }

    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            isDragging = true;
            double deltaX = event.getX() - lastMouseX;
            double deltaY = event.getY() - lastMouseY;

            // Mise à jour de la vue en temps réel sans creer pls objet Command 
            perspective.applyTranslationDelta(deltaX, deltaY);

            lastMouseX = event.getX();
            lastMouseY = event.getY();
        }
        event.consume();
    }

    private void handleMouseReleased(MouseEvent event) {
        view.getCanvas().setCursor(Cursor.OPEN_HAND);
        
        if (event.getButton() == MouseButton.PRIMARY && isDragging) {
            double totalDeltaX = event.getX() - startMouseX;
            double totalDeltaY = event.getY() - startMouseY;

            if (Math.abs(totalDeltaX) >= 1.0 || Math.abs(totalDeltaY) >= 1.0) {
                // On annule silencieusement la translation visuelle qu'on vient de faire
                perspective.applyTranslationDelta(-totalDeltaX, -totalDeltaY);
                
                // pour laisser la vraie Command l'exécuter proprement 
                // Cela permet à la commande de prendre son Memento (sauvegarde) au bon endroit ! et non a chaque pixel
                TranslationCommand cmd = new TranslationCommand(perspective, totalDeltaX, totalDeltaY);
                CommandManager.getInstance().executeCommand(cmd);
            }
            isDragging = false;
        }
        event.consume();
    }
}