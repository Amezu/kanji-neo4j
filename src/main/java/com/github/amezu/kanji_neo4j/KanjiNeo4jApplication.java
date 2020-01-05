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

import org.neo4j.driver.v1.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

@Controller
@SpringBootApplication
public class KanjiNeo4jApplication implements AutoCloseable {

    private final Driver driver;

    public KanjiNeo4jApplication() {
        String url = System.getenv().get("GRAPHENEDB_BOLT_URL");
        String user = System.getenv().get("GRAPHENEDB_BOLT_USER");
        String password = System.getenv().get("GRAPHENEDB_BOLT_PASSWORD");

        driver = GraphDatabase.driver(url, AuthTokens.basic(user, password));
    }

    public static void main(String[] args) {
        SpringApplication.run(KanjiNeo4jApplication.class, args);
    }

    @Override
    public void close() {
        driver.close();
    }

    @RequestMapping(value = {"/kanji", ""})
    String getAllKanjis(@RequestParam(value = "search", required = false, defaultValue = "") String reading, Map<String, Object> model) {
        try (Session session = driver.session()) {
            String query = reading.equals("")
                    ? "MATCH (k:Kanji) RETURN *"
                    : "MATCH (k:Kanji) WHERE {reading} IN k.reading RETURN *";
            StatementResult result = session.run(
                    query,
                    parameters("reading", reading));
            ArrayList<String> records = new ArrayList<>();
            while (result.hasNext()) {
                Value node = result.next().get("k");
                String text = String.format("%s (%d strokes) \n\r\n\r readings: %s",
                        node.get("character").asString(),
                        node.get("strokes").asInt(),
                        node.get("reading").asList(Value::asString).toString().replace("[", "").replace("]", ""));
                records.add(text);
            }
            model.put("records", records);
            return "index";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }
    
    @RequestMapping("/kanji/{id}")
    String getKanji(@PathVariable int id, Map<String, Object> model) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (k:Kanji) WHERE id(k) = {id} RETURN *",
                    parameters("id", id));
            ArrayList<String> records = new ArrayList<>();
            while (result.hasNext()) {
                Value node = result.next().get("k");
                String text = String.format("%s (%d strokes) \n\r\n\r readings: %s",
                        node.get("character").asString(),
                        node.get("strokes").asInt(),
                        node.get("reading").asList(Value::asString).toString().replace("[", "").replace("]", ""));
                records.add(text);
            }
            model.put("records", records);
            return "index";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }

    @RequestMapping("/kanji/add")
    String addKanji(@RequestParam("char") String character, @RequestParam("read") List<String> reading, @RequestParam Integer strokes, Map<String, Object> model) {
        try (Session session = driver.session()) {
            final String message = session.writeTransaction(tx -> {
                StatementResult result = tx.run(
                        "CREATE (k:Kanji) SET k.character = {character}, k.reading = {reading}, k.strokes = {strokes} RETURN k.character + ', from node ' + id(k)",
                        parameters("character", character, "reading", reading, "strokes", strokes));
                return result.single().get(0).asString();
            });
            model.put("message", "Added kanji " + message);
            return "error";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }
}
