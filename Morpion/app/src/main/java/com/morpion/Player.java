package com.morpion;

import java.io.Serializable;

/**
 * Created by annam on 26/03/2017.
 */

class Player implements Serializable{
    private String name;
    private char playerMarker;
    private int score;

    Player(String name, char playerMarker, int score){
        this.name = name;
        this.playerMarker = playerMarker;
        this.score = score;
    }

    String getName() {

        return name;
    }

    char getPlayerMarker() {

        return playerMarker;
    }

    int getScore() {

        return score;
    }

    void setScore(int score) {

        this.score = score;
    }
}
