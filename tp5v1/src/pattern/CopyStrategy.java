package pattern;

import model.Perspective;

/**
 * Patron Strategy — interface pour la fonctionnalité Copier-Coller (bonus).
 * Permet de choisir ce qui est copié d'une perspective : zoom seulement,
 * translation seulement, ou les deux.
 *
 * Correspondance patron :
 *   Strategy (GoF)    →  CopyStrategy (ici)
 *   algorithm()       →  apply(source, target)
 *
 * Stratégies concrètes :
 *   - ZoomOnlyStrategy        : copie uniquement le facteur d'échelle
 *   - TranslationOnlyStrategy : copie uniquement la translation
 *   - FullCopyStrategy        : copie zoom + translation
 */
public interface CopyStrategy {

    /**
     * Applique la stratégie de copie : transfère les paramètres de
     * la perspective source vers la perspective cible.
     *
     * @param source  la perspective dont on copie les paramètres
     * @param target  la perspective qui reçoit les paramètres
     */
    void apply(Perspective source, Perspective target);
}