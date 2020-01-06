package com.github.amezu.kanji_neo4j.controller;

import com.github.amezu.kanji_neo4j.KanjiNeo4jSessionFactory;
import com.github.amezu.kanji_neo4j.domain.Kanji;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kanji")
public class KanjiController {

    @RequestMapping
    String getAllKanjis(@RequestParam(required = false, defaultValue = "") String search, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Iterable<Kanji> kanjis;
        if (search.equals("")) {
            kanjis = session.loadAll(Kanji.class, 0);
        } else {
            kanjis = session.query(Kanji.class,
                    "MATCH (k:Kanji) WHERE ANY (r IN k.reading WHERE r CONTAINS {reading}) RETURN *",
                    Map.of("reading", search));
        }
        model.addAttribute("kanjis", kanjis);
        return "kanji-list";
    }

    @RequestMapping("/add")
    String addKanji(@RequestParam("char") String character, @RequestParam Integer strokes, @RequestParam("read") List<String> reading, Map<String, Object> model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Kanji kanji = new Kanji(character, strokes, reading);
        session.save(kanji, 1);
        model.put("message", "Added kanji " + kanji.getCharacter());
        return "info";
    }
}
