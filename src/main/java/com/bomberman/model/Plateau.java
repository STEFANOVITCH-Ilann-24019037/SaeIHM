package com.bomberman.model;

import java.util.*;

public class Plateau {
    private Case[][] grille;
    private int largeur, hauteur;
    private List<Bombe> bombes;
    private List<CaseExplosion> explosions;
    private List<Bonus> bonus;
    private static final int MAX_BONUS = 6; // Limite à 6 bonus simultanés

    public Plateau(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.grille = new Case[largeur][hauteur];
        this.bombes = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.bonus = new ArrayList<>();
        genererPlateau();
    }

    private void genererPlateau() {
        Random random = new Random();

        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                if (x == 0 || y == 0 || x == largeur - 1 || y == hauteur - 1) {
                    grille[x][y] = new CaseIncassable(x, y);
                } else if (x % 2 == 0 && y % 2 == 0) {
                    grille[x][y] = new CaseIncassable(x, y);
                } else if (isZoneSpawn(x, y)) {
                    grille[x][y] = new CaseJouable(x, y); // Zones de spawn des joueurs
                } else if (random.nextDouble() < 0.6) {
                    grille[x][y] = new CaseCassable(x, y);
                } else {
                    grille[x][y] = new CaseJouable(x, y);
                }
            }
        }
    }

    private boolean isZoneSpawn(int x, int y) {
        // Zone spawn joueur 1 (coin haut-gauche)
        if ((x >= 1 && x <= 2) && (y >= 1 && y <= 2)) return true;

        // Zone spawn joueur 2 (coin bas-droite)
        if ((x >= largeur-3 && x <= largeur-2) && (y >= hauteur-3 && y <= hauteur-2)) return true;

        return false;
    }

    public void ajouterBombe(Bombe bombe) {
        bombes.add(bombe);
    }

    public void ajouterBonus(Bonus bonus) {
        // Vérifier si on peut ajouter un bonus (limite de 6)
        long bonusActifs = this.bonus.stream().filter(b -> !b.isConsomme()).count();
        if (bonusActifs < MAX_BONUS) {
            this.bonus.add(bonus);
        }
    }

    public void exploserBombe(Bombe bombe) {
        int x = bombe.getX();
        int y = bombe.getY();
        int portee = bombe.getPortee();

        // Explosion au centre
        explosions.add(new CaseExplosion(x, y));

        // Explosion dans les 4 directions
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i <= portee; i++) {
                int newX = x + dir[0] * i;
                int newY = y + dir[1] * i;

                if (!isValidPosition(newX, newY)) break;

                Case c = grille[newX][newY];
                if (c instanceof CaseIncassable) break;

                explosions.add(new CaseExplosion(newX, newY));

                if (c instanceof CaseCassable) {
                    ((CaseCassable) c).detruire();

                    // Chance de générer un bonus (réduite pour limiter le nombre)
                    long bonusActifs = bonus.stream().filter(b -> !b.isConsomme()).count();
                    if (bonusActifs < MAX_BONUS && Math.random() < 0.25) { // 25% de chance
                        Bonus.TypeBonus[] types = Bonus.TypeBonus.values();
                        ajouterBonus(new Bonus(newX, newY, types[(int)(Math.random() * types.length)]));
                    }
                    break;
                }
            }
        }

        bombe.exploser();
    }

    public void mettreAJour() {
        // Vérifier les bombes à exploser
        Iterator<Bombe> bombeIter = bombes.iterator();
        while (bombeIter.hasNext()) {
            Bombe bombe = bombeIter.next();
            if (bombe.doitExploser()) {
                exploserBombe(bombe);
                bombeIter.remove();
            }
        }

        // Nettoyer les explosions expirées
        explosions.removeIf(CaseExplosion::isExpired);

        // Nettoyer les bonus consommés (optionnel, pour libérer la mémoire)
        bonus.removeIf(Bonus::isConsomme);
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }

    public boolean estDansExplosion(int x, int y) {
        return explosions.stream().anyMatch(exp -> exp.getX() == x && exp.getY() == y);
    }

    public Bonus getBonusAt(int x, int y) {
        return bonus.stream()
                .filter(b -> b.getX() == x && b.getY() == y && !b.isConsomme())
                .findFirst()
                .orElse(null);
    }

    public int getNombreBonusActifs() {
        return (int) bonus.stream().filter(b -> !b.isConsomme()).count();
    }

    // Getters
    public Case getCase(int x, int y) { return grille[x][y]; }
    public int getLargeur() { return largeur; }
    public int getHauteur() { return hauteur; }
    public List<Bombe> getBombes() { return bombes; }
    public List<CaseExplosion> getExplosions() { return explosions; }
    public List<Bonus> getBonus() { return bonus; }
    public int getMaxBonus() { return MAX_BONUS; }
}