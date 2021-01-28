package net.starype.quiz.api.parser;

import java.util.Optional;

public interface FilePathReader {

    Optional<String> read(String path);
}
