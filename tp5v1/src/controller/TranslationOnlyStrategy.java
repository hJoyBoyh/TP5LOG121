package controller;

import model.Perspective;
import pattern.CopyStrategy;

/**
 * Patron Strategy — copie UNIQUEMENT la translation (tx, ty).
 * Le zoom de la cible n'est pas modifié.
 *
 * Correspondance patron Strategy :
 *   ConcreteStrategy →  TranslationOnlyStrategy
 */
public class TranslationOnlyStrategy implements CopyStrategy {

    @Override
    public void apply(Perspective source, Perspective target) {
        // Copier seulement la translation — garder le zoom courant de la cible
        target.restoreState(
                target.getZoomFactor(),   // inchangé
                source.getTranslateX(),
                source.getTranslateY()
        );
    }

    @Override
    public String toString() { return "Translation seulement"; }
}