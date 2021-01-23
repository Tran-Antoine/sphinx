package net.starype.quiz.api.parser;

import java.nio.ByteBuffer;
import java.util.Set;

public interface DBParser {
    Set<DBEntry> read(String file);
}
