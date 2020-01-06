package com.github.amezu.kanji_neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Word extends Entity {

    private String japanese;
    private String romaji;
    @Relationship(type = "MEANS")
    private Set<Translation> meanings;
    @Relationship(type = "CONTAINS")
    private Set<Kanji> kanjis;

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

    public Set<Translation> getMeanings() {
        return meanings;
    }

    public Set<Kanji> getKanjis() {
        return kanjis;
    }

    public void addMeaning(Translation translation) {
        if (meanings == null) {
            meanings = new HashSet<>();
        }
        meanings.add(translation);
    }

    public void addKanji(Kanji kanji) {
        if (kanjis == null) {
            kanjis = new HashSet<>();
        }
        kanjis.add(kanji);
    }
}