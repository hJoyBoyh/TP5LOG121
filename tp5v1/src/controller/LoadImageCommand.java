package controller;

import java.io.File;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import model.ImageModel;
import pattern.Command;
import singleton.CommandManager;

/**
 * Patron Command : opération de chargement d'une image.
 * Déclenché par le menu Fichier > Charger image.
 *
 * Note : le chargement d'une image n'est pas annulable (undo non supporté
 * pour cette commande : vider la pile undo à l'ouverture est le comportement
 * standard dans ce type d'application).
 *
 */
public class LoadImageCommand implements Command {

    private final Stage stage;
    private final ImageModel imageModel;

    public LoadImageCommand(Stage stage, ImageModel imageModel) {
        this.stage = stage;
        this.imageModel = imageModel;
    }

    @Override
    public void execute() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                imageModel.loadImage(file);
                // Réinitialiser l'historique des commandes au chargement d'une nouvelle image
                CommandManager.getInstance().clear();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    public void undo() {
        // Chargement d'image non annulable
    }
}