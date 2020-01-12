package com.github.amezu.kanji_neo4j.controller;

import com.github.amezu.kanji_neo4j.KanjiNeo4jSessionFactory;
import com.github.amezu.kanji_neo4j.domain.Kanji;
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

import java.util.Collection;
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
        Word word = session.load(Word.class, id);
        model.addAttribute("word", word);
        return "word";
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
        fillWordMeanings(session, word, meanings);
        session.save(word, 1);

        session.query("MATCH (w:Word) WHERE id(w)={id} MATCH (k:Kanji) WHERE w.japanese CONTAINS k.character CREATE (w) -[:CONTAINS]-> (k)",
                Map.of("id", word.getId()));

        return new ResponseEntity<>(
                String.format("Added word %s (%s)", word.getJapanese(), word.getRomaji()),
                HttpStatus.OK);
    }

    @RequestMapping("/{id}/edit")
    String getKanjiEditForm(@PathVariable long id, Model model) {
        Session session = KanjiNeo4jSessionFactory.getInstance().getSession();
        Word word = session.load(Word.class, id);
        model.addAttribute("word", word);
        return "word-edit";
    }

    private void fillWordMeanings(Session session, Word word, @RequestParam("en") List<String> meanings) {
        for (String meaning : meanings) {
            String[] languagesArray = meaning.split("_");
            String english = languagesArray[0];
            String polish = languagesArray.length > 1 ? languagesArray[1] : "";
            Translation translation = getTranslation(session, english, polish);
            word.addMeaning(translation);
        }
    }

    private Translation getTranslation(Session session, String english, String polish) {
        Translation translation;
        Collection<Translation> translations = session.loadAll(Translation.class,
                new Filter("english", ComparisonOperator.EQUALS, english));
        if (translations.isEmpty()) {
            translation = new Translation(english, polish);
        } else {
            translation = translations.iterator().next();
            if (polish != "") {
                translation.setPolish(polish);
            }
        }
        return translation;
    }
}
