package net.starype.quiz.api.game;

import net.starype.quiz.api.parser.CompiledSearch;

import java.io.File;

public class TestDB {
    public static void main(String[] args) {
        // System.out.println(new File("test/").getAbsoluteFile().getPath());
        CompiledSearch compiledSearch = CompiledSearch.fromDirectory("test/", "db.bin").orElseThrow();
        compiledSearch.compile();
        compiledSearch.save();
    }
}
