package controller;

import model.Perspective;
import pattern.CopyStrategy;

/**
 * Patron Strategy : copie UNIQUEMENT le facteur de zoom.
 * La translation de la cible n'est pas modifiée.
 *
 * Correspondance patron Strategy :
 *   Strategy >  CopyStrategy (interface)
 *   ConcreteStrategy > ZoomOnlyStrategy
 *   algorithm() > apply(source, target)
 */
public class ZoomOnlyStrategy implements CopyStrategy {

    @Override
    public void apply(Perspective source, Perspective target) {
        // Copier seulement le zoom : garder la translation courante de la cible
        target.restoreState(
                source.getZoomFactor(),
                target.getTranslateX(),   // inchangée
                target.getTranslateY()    // inchangée
        );
    }

    @Override
    public String toString() { return "Zoom seulement"; }
}