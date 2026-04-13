package model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import pattern.AbstractSubject;

/**
 * Patron Observer  : ConcreteSubject #1
 * Patron Singleton : NON (ImageModel n'est pas Singleton)
 * Serializable     : sauvegarde le chemin du fichier et recharge l'image
 *
 * Correspondance patron Observer :
 *   ConcreteSubject >  ImageModel
 *   subjectState >  filePath, image
 *   attach(Observer) >  addObserver(Observer)
 *   notifyObservers() >  notifyObservers()
 */
public class ImageModel extends AbstractSubject  {

    private static final long serialVersionUID = 1L;

    // Chemin du fichier image (sérialisable, contrairement à BufferedImage)
    private String filePath;

    // Image JavaFX (transient = non sérialisée directement)
    private transient Image image;

    public ImageModel() {
        this.filePath = null;
        this.image = null;
    }

    // Chargement de l'image

    /**
     * Charge une image depuis un fichier et notifie les observateurs.
     * @param file le fichier image sélectionné par l'utilisateur
     */
    public void loadImage(File file) throws IOException {
        this.filePath = file.getAbsolutePath();
        BufferedImage buffered = ImageIO.read(file);
        this.image = SwingFXUtils.toFXImage(buffered, null);
        notifyObservers();
    }

    // Sérialisation personnalisée
    // Raison : Image JavaFX n'est pas Serializable.
    // On sauvegarde uniquement le chemin (filePath) et on recharge l'image
    // lors de la désérialisation.

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.observers = new ArrayList<>();
        // Recharger l'image depuis le chemin sauvegardé
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                BufferedImage buffered = ImageIO.read(file);
                this.image = SwingFXUtils.toFXImage(buffered, null);
            }
        }
    }

   

    public Image getImage() {
        return image;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean hasImage() {
        return image != null;
    }
}