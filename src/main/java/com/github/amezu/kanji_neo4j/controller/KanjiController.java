package com.github.amezu.kanji_neo4j.controller;

import com.github.amezu.kanji_neo4j.KanjiNeo4jSessionFactory;
import com.github.amezu.kanji_neo4j.domain.Kanji;
import com.github.amezu.kanji_neo4j.domain.Word;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class KanjiController {

    @RequestMapping(value = {"/kanji", "/"})
    String getAllKanjis(@RequestParam(required = false, defaultValue = "") String search, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Iterable<Kanji> kanjis;
        if (search.equals("")) {
            kanjis = session.loadAll(Kanji.class, 1);
        } else {
            kanjis = session.query(Kanji.class,
                    "MATCH (kanji)--(word) WHERE ANY (r IN kanji.reading WHERE r CONTAINS {reading}) RETURN *",
                    Map.of("reading", search));
        }
        model.addAttribute("kanjis", kanjis);
        return "kanji-list";
    }

    @RequestMapping("/kanji/{id}")
    String getKanji(@PathVariable long id, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Iterable<Kanji> kanjis = Set.of(session.load(Kanji.class, id));
        model.addAttribute("kanjis", kanjis);
        return "kanji-list";
    }

    @RequestMapping(value = "/kanji/add", produces = "text/plain")
    @ResponseBody
    ResponseEntity<String> addKanji(@RequestParam("char") String character, @RequestParam Integer strokes, @RequestParam("read") Set<String> reading) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();

        boolean kanjiExists = session.count(Kanji.class, List.of(new Filter("character", ComparisonOperator.EQUALS, character))) != 0;
        if (kanjiExists) {
            return new ResponseEntity<>(
                    String.format("Kanji %s already exists", character),
                    HttpStatus.BAD_REQUEST);
        }

        Kanji kanji = new Kanji(character, strokes, reading);

        Iterable<Word> words = session.query(Word.class,
                "MATCH (w:Word) WHERE w.japanese CONTAINS {kanji} RETURN *",
                Map.of("kanji", character));
        for (Word word : words) {
            kanji.addWord(word);
        }

        session.save(kanji, 1);
        return new ResponseEntity<>(
                String.format("Added kanji %s", kanji.getCharacter()),
                HttpStatus.OK);
    }

    @RequestMapping("/kanji/{id}/edit")
    String getKanjiEditForm(@PathVariable long id, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Kanji kanji = session.load(Kanji.class, id);
        model.addAttribute("kanji", kanji);
        return "kanji-edit";
    }

    @RequestMapping(value = "/kanji/{id}/update", produces = "text/plain")
    ResponseEntity<String> updateKanji(@PathVariable long id, @RequestParam Integer strokes, @RequestParam("read") Set<String> reading) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();

        Kanji kanji = session.load(Kanji.class, id);
        kanji.setStrokes(strokes);
        kanji.setReading(reading);
        session.save(kanji, 1);

        return new ResponseEntity<>(
                String.format("Edited kanji %s", kanji.getCharacter()),
                HttpStatus.OK);
    }
}
