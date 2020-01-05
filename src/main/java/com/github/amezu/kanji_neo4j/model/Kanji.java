package com.github.amezu.kanji_neo4j.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

@NodeEntity
public class Kanji {

    @Id
    @GeneratedValue
    private Long id;
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

    public Long getId() {
        return id;
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