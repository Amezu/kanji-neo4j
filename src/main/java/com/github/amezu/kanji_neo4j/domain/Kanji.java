package com.github.amezu.kanji_neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

@NodeEntity
public class Kanji extends Entity {

    private String character;
    private Integer strokes;
    private List<String> reading;

    public Kanji() {
    }

    public Kanji(String character, Integer strokes, List<String> reading) {
        this.character = character;
        this.strokes = strokes;
        this.reading = reading;
    }

    public String getCharacter() {
        return character;
    }

    public Integer getStrokes() {
        return strokes;
    }

    public List<String> getReading() {
        return reading;
    }
}