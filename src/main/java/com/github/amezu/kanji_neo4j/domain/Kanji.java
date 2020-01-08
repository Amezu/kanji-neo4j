package com.github.amezu.kanji_neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NodeEntity
public class Kanji extends Entity {

    private String character;
    private Integer strokes;
    private Set<String> reading;
    @Relationship(type = "CONTAINS", direction = Relationship.INCOMING)
    private Set<Word> words;

    public Kanji() {
    }

    public Kanji(String character, Integer strokes, Set<String> reading) {
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

    public Set<String> getReading() {
        return reading;
    }

    public void setStrokes(Integer strokes) {
        this.strokes = strokes;
    }

    public void setReading(Set<String> reading) {
        this.reading = reading;
    }

    public Set<Word> getWords() {
        return words;
    }

    public void addWord(Word word) {
        if (words == null) {
            words = new HashSet<>();
        }
        words.add(word);
    }
}