package application;

import javafx.application.Application;
import javafx.stage.Stage;

import model.ImageModel;
import model.Perspective;
import view.MainView;

/**
 * Point d'entrée de l'application.
 * Instancie le modèle, crée les perspectives et initialise la vue principale.
 *
 * Architecture MVC :
 *   - Modèle  : ImageModel + Perspective (×2)
 *   - Vue     : MainView (contient ThumbnailView + PerspectiveView ×2)
 *   - Ctrl    : MouseController, ZoomCommand, TranslationCommand (créés dans MainView)
 */
public class Main extends Application {
@Override
	public void start(Stage primaryStage) {
		try {
			// --- MODÈLE ---
			// Une seule image partagée entre toutes les vues
			ImageModel imageModel = new ImageModel();

			// Deux perspectives indépendantes (une par PerspectiveView)
			Perspective perspective1 = new Perspective();
			Perspective perspective2 = new Perspective();

			// --- VUE PRINCIPALE ---
			// MainView reçoit le modèle et construit les sous-vues + contrôleurs
			MainView mainView = new MainView(primaryStage, imageModel, perspective1, perspective2);
			mainView.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}