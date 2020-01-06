package com.github.amezu.kanji_neo4j.controller;

import com.github.amezu.kanji_neo4j.KanjiNeo4jSessionFactory;
import com.github.amezu.kanji_neo4j.domain.Kanji;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kanji")
public class KanjiController {

    @RequestMapping
    String getAllKanjis(@RequestParam(value = "search", required = false, defaultValue = "") String reading, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Iterable<Kanji> kanjis;
        if (reading.equals("")) {
            kanjis = session.loadAll(Kanji.class, 0);
        } else {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("reading", reading);
            kanjis = session.query(Kanji.class, "MATCH (k:Kanji) WHERE {reading} IN k.reading RETURN *", parameters);
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
