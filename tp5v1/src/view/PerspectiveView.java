package view;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import controller.MouseController;
import model.ImageModel;
import model.Perspective;
import pattern.Observer;

/**
 * Patron Observer — ConcreteObserver (vue avec zoom et translation).
 * Observe ImageModel ET Perspective.
 */
public class PerspectiveView implements Observer {

    private final ImageModel  imageModel;
    private final Perspective perspective;
    private final Canvas      canvas;
    private final Pane        pane;

    public PerspectiveView(ImageModel imageModel, Perspective perspective) {
        this.imageModel  = imageModel;
        this.perspective = perspective;

        // Utiliser un Pane simple — le Canvas se lie à sa taille via bind()
        this.canvas = new Canvas();
        this.pane   = new Pane(canvas);

        // Lier la taille du Canvas à celle du Pane (resize automatique)
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        // Bordure bleue visible comme dans l'exemple
        pane.setStyle(
                "-fx-border-color: #0000cd;" +
                        "-fx-border-width: 3;" +
                        "-fx-background-color: #f0f0f0;"
        );
        pane.setMinWidth(200);

        // Redessiner quand le canvas change de taille
        canvas.widthProperty().addListener((obs, o, n) -> draw());
        canvas.heightProperty().addListener((obs, o, n) -> draw());

        // Abonnement Observer
        imageModel.addObserver(this);
        perspective.addObserver(this);

        // Contrôleur souris
        new MouseController(this, perspective);

        draw();
    }

    @Override
    public void update() {
        Platform.runLater(this::draw);
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w == 0 || h == 0) return;

        gc.setFill(Color.web("#f0f0f0"));
        gc.fillRect(0, 0, w, h);

        if (!imageModel.hasImage()) return;

        Image  img  = imageModel.getImage();
        double zoom = perspective.getZoomFactor();
        double tx   = perspective.getTranslateX();
        double ty   = perspective.getTranslateY();

        gc.save();
        gc.beginPath();
        gc.rect(0, 0, w, h);
        gc.clip();
        gc.drawImage(img, tx, ty, img.getWidth() * zoom, img.getHeight() * zoom);
        gc.restore();
    }

    public Pane        getPane()        { return pane; }
    public Canvas      getCanvas()      { return canvas; }
    public Perspective getPerspective() { return perspective; }
}