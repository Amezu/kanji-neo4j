package com.github.amezu.kanji_neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Translation extends Entity {

    private String word;

    public Translation() {
    }

    public Translation(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
