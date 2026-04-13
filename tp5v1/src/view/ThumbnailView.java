package view;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import model.ImageModel;
import pattern.Observer;

/**
 * Patron Observer : vignette (thumbnail).
 * Affiche l'image entière réduite, centrée, ratio conservé.
 */
public class ThumbnailView implements Observer {

    private static final double FIXED_WIDTH = 220;

    private final ImageModel imageModel;
    private final Canvas     canvas;
    private final Pane       pane;

    public ThumbnailView(ImageModel imageModel) {
        this.imageModel = imageModel;

        this.canvas = new Canvas();
        this.pane   = new Pane(canvas);

        // Largeur fixe, hauteur liée au Pane
        pane.setPrefWidth(FIXED_WIDTH);
        pane.setMinWidth(FIXED_WIDTH);
        pane.setMaxWidth(FIXED_WIDTH);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        pane.setStyle(
                "-fx-border-color: #22225a;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-color: #f5f5f5;"
        );

        canvas.widthProperty().addListener((obs, o, n) -> draw());
        canvas.heightProperty().addListener((obs, o, n) -> draw());

        imageModel.addObserver(this);
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

        gc.setFill(Color.web("#f5f5f5"));
        gc.fillRect(0, 0, w, h);

        if (!imageModel.hasImage()) {
            gc.setFill(Color.web("#999999"));
            gc.fillText("Aucune image", 50, h / 2);
            return;
        }

        Image  img   = imageModel.getImage();
        double ratio = Math.min((w - 10) / img.getWidth(), (h - 10) / img.getHeight());
        double drawW = img.getWidth()  * ratio;
        double drawH = img.getHeight() * ratio;
        double offX  = (w - drawW) / 2.0;
        double offY  = (h - drawH) / 2.0;

        gc.drawImage(img, offX, offY, drawW, drawH);
    }

    public Pane getPane() { return pane; }
}