package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import controller.FullCopyStrategy;
import controller.LoadImageCommand;
import controller.PasteCommand;
import controller.ResetCommand;
import controller.TranslationOnlyStrategy;
import controller.ZoomOnlyStrategy;
import model.ImageModel;
import model.Perspective;
import singleton.CommandManager;

/**
 * Vue principale - fenêtre JavaFX avec 3 panneaux :
 *   - gauche  : ThumbnailView  (vignette, fixe 220px)
 *   - centre  : PerspectiveView #1
 *   - droite  : PerspectiveView #2
 *
 * Menus :
 *   Fichier       → Charger image | Sauvegarder/Charger perspective | Quitter
 *   Édition       → Undo (Ctrl+Z) | Redo (Ctrl+Y)
 *   Presse-Papier → Copier Vue1→Vue2 avec stratégie | Réinitialiser vues
 */
public class MainView {

    private final Stage       stage;
    private final ImageModel  imageModel;
    private final Perspective perspective1;
    private final Perspective perspective2;

    private ThumbnailView   thumbnailView;
    private PerspectiveView perspectiveView1;
    private PerspectiveView perspectiveView2;

    private MenuItem undoItem;
    private MenuItem redoItem;

    public MainView(Stage stage, ImageModel imageModel,
                    Perspective perspective1, Perspective perspective2) {
        this.stage        = stage;
        this.imageModel   = imageModel;
        this.perspective1 = perspective1;
        this.perspective2 = perspective2;
    }

    // -------------------------------------------------------------------------
    // Construction de la scène
    // -------------------------------------------------------------------------

    public void show() {
        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar());

        // Sous-vues
        thumbnailView    = new ThumbnailView(imageModel);
        perspectiveView1 = new PerspectiveView(imageModel, perspective1);
        perspectiveView2 = new PerspectiveView(imageModel, perspective2);

        // Double-clic → ResetCommand sur chaque vue
        perspectiveView1.getCanvas().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                CommandManager.getInstance().executeCommand(
                        new ResetCommand(perspective1));
                refreshUndoRedoState();
            }
        });
        perspectiveView2.getCanvas().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                CommandManager.getInstance().executeCommand(
                        new ResetCommand(perspective2));
                refreshUndoRedoState();
            }
        });

        // Layout 3 colonnes
        // Le thumbnail a une largeur fixe; les 2 PerspectiveView se partagent le reste
        HBox viewsPane = new HBox();
        viewsPane.setFillHeight(true);

        Pane thumbPane = thumbnailView.getPane();
        Pane p1Pane    = perspectiveView1.getPane();
        Pane p2Pane    = perspectiveView2.getPane();

        HBox.setHgrow(thumbPane, Priority.NEVER);
        HBox.setHgrow(p1Pane,   Priority.ALWAYS);
        HBox.setHgrow(p2Pane,   Priority.ALWAYS);

        // Lier la hauteur des vues à celle du HBox
        thumbPane.prefHeightProperty().bind(viewsPane.heightProperty());
        p1Pane.prefHeightProperty().bind(viewsPane.heightProperty());
        p2Pane.prefHeightProperty().bind(viewsPane.heightProperty());

        viewsPane.getChildren().addAll(thumbPane, p1Pane, p2Pane);
        root.setCenter(viewsPane);
        root.setBottom(buildStatusBar());

        // Scène + raccourcis clavier
        Scene scene = new Scene(root, 1100, 600);
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN),
                () -> { CommandManager.getInstance().undo(); refreshUndoRedoState(); }
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN),
                () -> { CommandManager.getInstance().redo(); refreshUndoRedoState(); }
        );

        try {
            scene.getStylesheets().add(
                    getClass().getResource("/application/application.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        stage.setTitle("Laboratoire MVC — Image et perspectives");
        stage.setMinWidth(700);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }

    // -------------------------------------------------------------------------
    // Barre de menu
    // -------------------------------------------------------------------------

    private MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();

        // ---- Fichier ----
        Menu fichier = new Menu("Fichier");

        MenuItem loadItem = new MenuItem("Charger image");
        loadItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        loadItem.setOnAction(e -> {
            new LoadImageCommand(stage, imageModel).execute();
            refreshUndoRedoState();
        });

        MenuItem saveItem = new MenuItem("Sauvegarder perspective");
        saveItem.setOnAction(e -> savePerspectives());

        MenuItem loadPerspItem = new MenuItem("Charger perspective");
        loadPerspItem.setOnAction(e -> loadPerspectives());

        MenuItem quitItem = new MenuItem("Quitter");
        quitItem.setOnAction(e -> stage.close());

        fichier.getItems().addAll(
                loadItem, new SeparatorMenuItem(),
                saveItem, loadPerspItem,
                new SeparatorMenuItem(), quitItem
        );

        // ---- Édition ----
        Menu edition = new Menu("Édition");

        undoItem = new MenuItem("Undo");
        undoItem.setDisable(true);
        undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        undoItem.setOnAction(e -> {
            CommandManager.getInstance().undo();
            refreshUndoRedoState();
        });

        redoItem = new MenuItem("Redo");
        redoItem.setDisable(true);
        redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        redoItem.setOnAction(e -> {
            CommandManager.getInstance().redo();
            refreshUndoRedoState();
        });

        edition.setOnShowing(e -> refreshUndoRedoState());
        edition.getItems().addAll(undoItem, redoItem);

        // ---- Presse-Papier ----
        Menu pressePapier = new Menu("Presse-Papier");

        // Copier Vue1 → Vue2
        Menu copyV1toV2 = new Menu("Copier Vue 1 → Vue 2");
        MenuItem pasteZoom1to2 = new MenuItem("Zoom seulement");
        pasteZoom1to2.setOnAction(e -> paste(perspective1, perspective2, new ZoomOnlyStrategy()));

        MenuItem pasteTrans1to2 = new MenuItem("Translation seulement");
        pasteTrans1to2.setOnAction(e -> paste(perspective1, perspective2, new TranslationOnlyStrategy()));

        MenuItem pasteFull1to2 = new MenuItem("Zoom + Translation");
        pasteFull1to2.setOnAction(e -> paste(perspective1, perspective2, new FullCopyStrategy()));
        copyV1toV2.getItems().addAll(pasteZoom1to2, pasteTrans1to2, pasteFull1to2);

        // Copier Vue2 → Vue1
        Menu copyV2toV1 = new Menu("Copier Vue 2 → Vue 1");
        MenuItem pasteZoom2to1 = new MenuItem("Zoom seulement");
        pasteZoom2to1.setOnAction(e -> paste(perspective2, perspective1, new ZoomOnlyStrategy()));

        MenuItem pasteTrans2to1 = new MenuItem("Translation seulement");
        pasteTrans2to1.setOnAction(e -> paste(perspective2, perspective1, new TranslationOnlyStrategy()));

        MenuItem pasteFull2to1 = new MenuItem("Zoom + Translation");
        pasteFull2to1.setOnAction(e -> paste(perspective2, perspective1, new FullCopyStrategy()));
        copyV2toV1.getItems().addAll(pasteZoom2to1, pasteTrans2to1, pasteFull2to1);

        // Réinitialiser
        MenuItem resetAll = new MenuItem("Réinitialiser toutes les vues");
        resetAll.setOnAction(e -> {
            CommandManager.getInstance().executeCommand(new ResetCommand(perspective1));
            CommandManager.getInstance().executeCommand(new ResetCommand(perspective2));
            refreshUndoRedoState();
        });

        pressePapier.getItems().addAll(copyV1toV2, copyV2toV1,
                new SeparatorMenuItem(), resetAll);

        menuBar.getMenus().addAll(fichier, edition, pressePapier);
        return menuBar;
    }

    // -------------------------------------------------------------------------
    // Barre de statut
    // -------------------------------------------------------------------------

    private HBox buildStatusBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(3, 10, 3, 10));
        bar.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        Text label = new Text("Démonstration LOG121 © 2025  |  Molette = Zoom  |  Glisser = Translation  |  Double-clic = Reset");
        label.setFill(Color.web("#333333"));
        bar.getChildren().add(label);
        return bar;
    }

    // -------------------------------------------------------------------------
    // Sérialisation
    // -------------------------------------------------------------------------

    private void savePerspectives() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Sauvegarder la perspective");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier perspective (*.persp)", "*.persp")
        );
        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(imageModel);
            oos.writeObject(perspective1);
            oos.writeObject(perspective2);
            showAlert(Alert.AlertType.INFORMATION, "Sauvegarde réussie",
                    "Perspective sauvegardée : " + file.getName());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", ex.getMessage());
        }
    }

    private void loadPerspectives() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Charger une perspective");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier perspective (*.persp)", "*.persp")
        );
        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ImageModel  loadedImage = (ImageModel)  ois.readObject();
            Perspective loadedP1    = (Perspective) ois.readObject();
            Perspective loadedP2    = (Perspective) ois.readObject();

            perspective1.restoreState(
                    loadedP1.getZoomFactor(), loadedP1.getTranslateX(), loadedP1.getTranslateY());
            perspective2.restoreState(
                    loadedP2.getZoomFactor(), loadedP2.getTranslateX(), loadedP2.getTranslateY());

            if (loadedImage.hasImage()) {
                try { imageModel.loadImage(new File(loadedImage.getFilePath())); }
                catch (Exception ex) {
                    showAlert(Alert.AlertType.WARNING, "Image introuvable",
                            "Image originale introuvable : " + loadedImage.getFilePath());
                }
            }
            CommandManager.getInstance().clear();
            refreshUndoRedoState();

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------------

    /** Crée et exécute un PasteCommand avec la stratégie donnée. */
    private void paste(Perspective source, Perspective target,
                       pattern.CopyStrategy strategy) {
        CommandManager.getInstance().executeCommand(
                new PasteCommand(source, target, strategy));
        refreshUndoRedoState();
    }

    private void refreshUndoRedoState() {
        CommandManager cm = CommandManager.getInstance();
        undoItem.setDisable(!cm.canUndo());
        redoItem.setDisable(!cm.canRedo());
        undoItem.setText("Undo" + (cm.canUndo() ? " (" + cm.getUndoCount() + ")" : ""));
        redoItem.setText("Redo" + (cm.canRedo() ? " (" + cm.getRedoCount() + ")" : ""));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}