package com.github.amezu.kanji_neo4j;

import com.github.amezu.kanji_neo4j.model.Kanji;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kanji")
public class KanjiController {

    @RequestMapping
    String getAllKanjis(@RequestParam(value = "search", required = false, defaultValue = "") String reading, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Collection<Kanji> kanjis = session.loadAll(Kanji.class, 0);
        model.addAttribute("kanjis", kanjis);
        return "index";
    }

    @RequestMapping("/add")
    String addKanji(@RequestParam("char") String character, @RequestParam Integer strokes, @RequestParam("read") List<String> reading, Map<String, Object> model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Kanji kanji = new Kanji(character, strokes, reading);
        session.save(kanji, 1);
        model.put("message", "Added kanji " + kanji.getCharacter());
        return "error";
    }
}
