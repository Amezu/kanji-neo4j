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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

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
    String index(Map<String, Object> model) {

        try (Session session = driver.session()) {
            final String message = addAndGet(session, "Hello world!");
            model.put("message", message);
            return "error";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }

    private String addAndGet(Session session, String message) {
        return session.writeTransaction(tx -> {
            StatementResult result = tx.run("CREATE (a:Greeting) " +
                            "SET a.message = $message " +
                            "RETURN a.message + ', from node ' + id(a)",
                    Values.parameters("message", message));
            return result.single().get(0).asString();
        });
    }
}
