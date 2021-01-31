package net.starype.quiz.api.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.toml.TomlParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class NightConfigParserUtils {
    public static CommentedConfig loadConfigFromFile(String filePath) throws IOException {
        ConfigParser<CommentedConfig> parser = new TomlParser();
        Reader reader = new FileReader(new File(filePath));
        CommentedConfig result = parser.parse(reader);
        reader.close();
        return result;
    }

    public static CommentedConfig loadConfigFromString(String str) {
        return new TomlParser().parse(str);
    }
}
