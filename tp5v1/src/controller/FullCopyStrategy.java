package controller;

import model.Perspective;
import pattern.CopyStrategy;

/**
 * Patron Strategy : copie ZOOM + TRANSLATION (état complet).
 *
 * Correspondance patron Strategy :
 *   ConcreteStrategy >  FullCopyStrategy
 */
public class FullCopyStrategy implements CopyStrategy {

    @Override
    public void apply(Perspective source, Perspective target) {
        target.restoreState(
                source.getZoomFactor(),
                source.getTranslateX(),
                source.getTranslateY()
        );
    }

    @Override
    public String toString() { return "Zoom + Translation"; }
}