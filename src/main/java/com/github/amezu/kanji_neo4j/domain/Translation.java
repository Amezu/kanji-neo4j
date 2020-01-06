package com.github.amezu.kanji_neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Translation extends Entity {

    private String english;
    private String polish;
    private String color;

    public Translation() {
    }

    public Translation(String english, String polish) {
        this.english = english;
        this.polish = polish;
    }

    public String getEnglish() {
        return english;
    }

    public String getPolish() {
        return polish;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
