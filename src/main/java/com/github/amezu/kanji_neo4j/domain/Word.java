package com.github.amezu.kanji_neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

@NodeEntity
public class Word extends Entity {

    private String japanese;
    private String romaji;
    @Relationship(type = "MEANS")
    private List<Translation> meanings;

    public Word() {
    }

    public Word(String japanese, String romaji) {
        this.japanese = japanese;
        this.romaji = romaji;
    }

    public String getJapanese() {
        return japanese;
    }

    public String getRomaji() {
        return romaji;
    }

    public List<Translation> getMeanings() {
        return meanings;
    }

    public void addMeaning(Translation translation) {
        if (meanings == null) {
            meanings = new ArrayList<>();
        }
        meanings.add(translation);
    }
}