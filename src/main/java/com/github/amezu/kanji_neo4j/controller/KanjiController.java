package com.github.amezu.kanji_neo4j.controller;

import com.github.amezu.kanji_neo4j.KanjiNeo4jSessionFactory;
import com.github.amezu.kanji_neo4j.domain.Kanji;
import com.github.amezu.kanji_neo4j.domain.Word;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/kanji")
public class KanjiController {

    @RequestMapping
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

    @RequestMapping("/{id}")
    String getKanji(@PathVariable long id, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Iterable<Kanji> kanjis = Set.of(session.load(Kanji.class, id));
        model.addAttribute("kanjis", kanjis);
        return "kanji-list";
    }

    @RequestMapping(value = "/add", produces = "text/plain")
    @ResponseBody
    String addKanji(@RequestParam("char") String character, @RequestParam Integer strokes, @RequestParam("read") Set<String> reading) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();

        boolean kanjiExists = session.count(Kanji.class, List.of(new Filter("character", ComparisonOperator.EQUALS, character))) != 0;
        if (kanjiExists) {
            return (String.format("Kanji %s already exists", character));
        }

        Kanji kanji = new Kanji(character, strokes, reading);

        Iterable<Word> words = session.query(Word.class,
                "MATCH (w:Word) WHERE w.japanese CONTAINS {kanji} RETURN *",
                Map.of("kanji", character));
        for (Word word : words) {
            kanji.addWord(word);
        }

        session.save(kanji, 1);
        return String.format("Added kanji %s", kanji.getCharacter());
    }
}
