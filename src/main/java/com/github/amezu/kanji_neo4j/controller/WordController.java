package com.github.amezu.kanji_neo4j.controller;

import com.github.amezu.kanji_neo4j.KanjiNeo4jSessionFactory;
import com.github.amezu.kanji_neo4j.domain.Translation;
import com.github.amezu.kanji_neo4j.domain.Word;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.session.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping("/{id}")
    String getKanji(@PathVariable long id, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Iterable<Word> words = Set.of(session.load(Word.class, id));
        model.addAttribute("words", words);
        return "word-list";
    }

    @RequestMapping("/add")
    @ResponseBody
    ResponseEntity<String> addWord(
            @RequestParam("jp") String japanese,
            @RequestParam("ro") String romaji,
            @RequestParam("en") List<String> meanings) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();

        Filters sameJapaneseAndRomaji = new Filter("japanese", ComparisonOperator.EQUALS, japanese)
                .and(new Filter("romaji", ComparisonOperator.EQUALS, romaji));
        boolean wordExists = session.count(Word.class, sameJapaneseAndRomaji) != 0;
        if (wordExists) {
            return new ResponseEntity<>(
                    String.format("Word %s (%s) already exists", japanese, romaji),
                    HttpStatus.BAD_REQUEST);
        }

        Word word = new Word(japanese, romaji);
        for (String meaning : meanings) {
            String[] allLanguages = meaning.split("_");
            String english = allLanguages[0];
            String polish = allLanguages.length > 1 ? allLanguages[1] : "";
            Translation translation = new Translation(english, polish);
            word.addMeaning(translation);
        }
        session.save(word, 1);

        session.query("MATCH (w:Word) WHERE id(w)={id} MATCH (k:Kanji) WHERE w.japanese CONTAINS k.character CREATE (w) -[:CONTAINS]-> (k)",
                Map.of("id", word.getId()));

        return new ResponseEntity<>(
                String.format("Added word %s", word.getJapanese()),
                HttpStatus.OK);
    }
}
