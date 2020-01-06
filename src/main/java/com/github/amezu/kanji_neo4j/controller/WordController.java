package com.github.amezu.kanji_neo4j.controller;

import com.github.amezu.kanji_neo4j.KanjiNeo4jSessionFactory;
import com.github.amezu.kanji_neo4j.domain.Kanji;
import com.github.amezu.kanji_neo4j.domain.Translation;
import com.github.amezu.kanji_neo4j.domain.Word;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/word")
public class WordController {

    @RequestMapping
    public String getAllWords(@RequestParam(required = false, defaultValue = "") String search, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();

        Iterable<Word> words;
        if (search.equals("")) {
            words = session.loadAll(Word.class, 1);
        } else {
            words = session.query(Word.class,
                    "MATCH (w:Word) WHERE ANY (r IN w.romaji WHERE r CONTAINS {search}) RETURN *",
                    Map.of("search", search));
        }

        model.addAttribute("words", words);
        return "word-list";
    }

    @RequestMapping("/add")
    String addWord(@RequestParam("jp") String japanese, @RequestParam("ro") String romaji, @RequestParam("en") List<String> meanings, Map<String, Object> model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();

        Word word = new Word(japanese, romaji);

        for (String meaning : meanings) {
            String[] allLanguages = meaning.split("_");
            String english = allLanguages[0];
            String polish = allLanguages.length > 1 ? allLanguages[1] : "";
            Translation translation = new Translation(english, polish);
            word.addMeaning(translation);
        }

        Iterable<Kanji> kanjis = session.query(Kanji.class,
                "MATCH (k:Kanji) WHERE '{word}' CONTAINS k.character RETURN *",
                Map.of("word", japanese));
        for (Kanji kanji : kanjis) {
            word.addKanji(kanji);
        }

        session.save(word, 1);

        session.query("MATCH (w:Word) WHERE id(w)={id} MATCH (k:Kanji) WHERE w.japanese CONTAINS k.character CREATE (w) -[:CONTAINS]-> (k)",
                Map.of("id", word.getId()));

        model.put("message", String.format("Added word %s", word.getJapanese()));
        return "error";
    }
}
