/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.amezu.kanji_neo4j;

import com.github.amezu.kanji_neo4j.model.Kanji;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@SpringBootApplication
public class KanjiNeo4jApplication implements AutoCloseable {

    private final SessionFactory factory;

    public KanjiNeo4jApplication() {
        String uri = System.getenv().get("GRAPHENEDB_BOLT_URL");
        String user = System.getenv().get("GRAPHENEDB_BOLT_USER");
        String password = System.getenv().get("GRAPHENEDB_BOLT_PASSWORD");

        Configuration configuration = new Configuration.Builder()
                .uri(uri)
                .credentials(user, password)
                .build();

        factory = new SessionFactory(configuration, "com.github.amezu.kanji_neo4j.model", "com.github.amezu.kanji_neo4j");
    }

    public static void main(String[] args) {
        SpringApplication.run(KanjiNeo4jApplication.class, args);
    }

    @Override
    public void close() {
        factory.close();
    }

    @RequestMapping(value = {"/kanji", ""})
    String getAllKanjis(@RequestParam(value = "search", required = false, defaultValue = "") String reading, Model model) {
        Session session = factory.openSession();
        Collection<Kanji> kanjis = session.loadAll(Kanji.class, 0);
        model.addAttribute("kanjis", kanjis);
        return "index";
    }

    @RequestMapping("/kanji/add")
    String addKanji(@RequestParam("char") String character, @RequestParam Integer strokes, @RequestParam("read") List<String> reading, Map<String, Object> model) {
        Session session = factory.openSession();
        Kanji kanji = new Kanji(character, strokes, reading);
        session.save(kanji, 1);
        model.put("message", "Added kanji " + kanji.getCharacter());
        return "error";
    }
}
