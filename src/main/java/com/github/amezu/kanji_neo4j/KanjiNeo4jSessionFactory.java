package com.github.amezu.kanji_neo4j;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class KanjiNeo4jSessionFactory {

    private static KanjiNeo4jSessionFactory instance = new KanjiNeo4jSessionFactory();
    private static SessionFactory sessionFactory;

    static {
        Configuration configuration = new Configuration.Builder()
                .uri(System.getenv().get("GRAPHENEDB_BOLT_URL"))
                .credentials(System.getenv().get("GRAPHENEDB_BOLT_USER"),
                        System.getenv().get("GRAPHENEDB_BOLT_PASSWORD"))
                .build();

        sessionFactory = new SessionFactory(configuration, "com.github.amezu.kanji_neo4j.domain", "com.github.amezu.kanji_neo4j");
    }

    public static KanjiNeo4jSessionFactory getInstance() {
        return instance;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    private KanjiNeo4jSessionFactory() {
    }
}
