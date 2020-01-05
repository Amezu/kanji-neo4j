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

import java.util.ArrayList;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

@Controller
@SpringBootApplication
public class Main implements AutoCloseable {

    private final Driver driver;

    public Main() {
        String url = System.getenv().get("GRAPHENEDB_BOLT_URL");
        String user = System.getenv().get("GRAPHENEDB_BOLT_USER");
        String password = System.getenv().get("GRAPHENEDB_BOLT_PASSWORD");

        driver = GraphDatabase.driver(url, AuthTokens.basic(user, password));
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void close() {
        driver.close();
    }

    @RequestMapping("/")
    String getAllMessages(Map<String, Object> model) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (g:Greeting) WHERE g.message STARTS WITH {messagePart} RETURN g.message + ' (' + id(g) + ')' AS message",
                    parameters("messagePart", "Hello"));
            ArrayList<String> records = new ArrayList<String>();
            while (result.hasNext()) {
                Record record = result.next();
                records.add(record.get("message").asString());
            }
            model.put("records", records);
            return "index";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }

    @RequestMapping("/{id}")
    String getMessage(@PathVariable("id") int id, Map<String, Object> model) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (g:Greeting) WHERE id(g) = {id} RETURN g.message + ' (' + id(g) + ')' AS message",
                    parameters("id", id));
            ArrayList<String> records = new ArrayList<String>();
            while (result.hasNext()) {
                Record record = result.next();
                records.add(record.get("message").asString());
            }
            model.put("records", records);
            return "index";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }

    @RequestMapping("/add")
    String addMessage(Map<String, Object> model) {
        try (Session session = driver.session()) {
            final String message = session.writeTransaction(tx -> {
                StatementResult result = tx.run("CREATE (g:Greeting) " +
                                "SET g.message = $message " +
                                "RETURN g.message + ', from node ' + id(g)",
                        parameters("message", "Hello world!"));
                return result.single().get(0).asString();
            });
            model.put("message", message);
            return "error";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }
}
