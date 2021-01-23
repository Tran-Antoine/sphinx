package net.starype.quiz.api.parser;

import java.util.List;

public interface IndexDatabase {
    List<DBEntry> query(IndexQuery query);
}
