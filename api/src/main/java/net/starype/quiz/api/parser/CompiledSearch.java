package net.starype.quiz.api.parser;

import net.starype.quiz.api.util.ByteBufferUtils;
import net.starype.quiz.api.util.CheckSum;
import net.starype.quiz.api.util.SerializedArgument;
import net.starype.quiz.api.util.Serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class CompiledSearch {

    private static class CompiledSearchSerializer extends Serializer {
        private Map<String, ByteBuffer> data;
        private Set<String> tags;

        public CompiledSearchSerializer() {
            super(Arrays.asList(
                    new SerializedArgument("checksum"),
                    new SerializedArgument("name"),
                    new SerializedArgument("file"),
                    new SerializedArgument("tags"),
                    new SerializedArgument("difficulty")
            ));
        }

        public void load(ByteBuffer data) {
            // First parse the data
            this.data = evaluate(data).orElseThrow();

            // Then parse the tags
            tags = Set.of(this.data.get("tags").toString().split(";"));
        }

        public ByteBuffer save() {
            // First compute the new checksum
            ByteBuffer buffer = ByteBufferUtils.concat(data.get("name"), data.get("tags"), data.get("file"));
            CheckSum checkSum = CheckSum.fromByteBuffer(buffer);
            data.put("checksum", checkSum.rawData());

            // Combine the map using the Serializer
            return evaluate(data).orElseThrow();
        }

        public String file() {
            return data.get("file").toString();
        }

        public CheckSum checkSum() {
            return CheckSum.fromByteBuffer(data.get("checksum"));
        }

        public String name() {
            return data.get("name").toString();
        }

        public Set<String> tags() {
            return tags;
        }

        public void setName(String name) {
            data.put("name", ByteBuffer.wrap(name.getBytes()));
        }

        public void setFile(String file) {
            data.put("file", ByteBuffer.wrap(file.getBytes()));
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
            this.data.put("tags", ByteBuffer.wrap(tags.stream()
                    .reduce((s1, s2) -> s1 + ";" + s2)
                    .orElseThrow().getBytes()));
        }
    }

    private List<CompiledSearchSerializer> serializedArgument;
    private boolean isCompiled = false;
    private final File compiledDB;
    private final List<? extends File> trackedFile;

    private CompiledSearch(List<? extends File> trackedFile, File compiledDB)  {
        this.trackedFile = trackedFile;
        this.compiledDB = compiledDB;
    }

    public static CompiledSearch fromListOfFile(List<? extends File> files, File compiledDB) {
        return new CompiledSearch(files, compiledDB);
    }

    public void compile() {
        // If the file has already been compiled
        if(isCompiled) return;
        isCompiled = true;

        // First read the DB file if exists
        // Read the compiledDB file
        try {
            FileInputStream fileInputStream = new FileInputStream(compiledDB);
            ByteBuffer buffer = ByteBuffer.wrap(fileInputStream.readAllBytes());

            // Read the number of File present in the compiledDB
            int nbFile = buffer.getInt();

            // For each file parse the serializer
            for(int i = 0; i < nbFile; ++i) {
                // Read the number of bytes present in the compiledDB
                int nbBytes = buffer.getInt();

                // Read the required number of bytes
                byte[] data = new byte[nbBytes];
                buffer.get(data);
                CompiledSearchSerializer compiledSearchSerializer = new CompiledSearchSerializer();
                compiledSearchSerializer.evaluate(ByteBuffer.wrap(data));
                serializedArgument.add(compiledSearchSerializer);
            }
        }
        catch (IOException ignored) { }

        // Secondly compare each file in the list with the DB
        // Retrieve a list of file to be recompile
        List<File> recompileRequired = new ArrayList<>();

        // Add all file that are in the recompileRequired but not in the last DB
        recompileRequired.addAll(trackedFile.stream()
                .filter(file -> serializedArgument
                        .stream()
                        .noneMatch(serializedArgument -> serializedArgument.file().equals(file.getName())))
                .collect(Collectors.toList()));

        // Detect all the file which differs by their CheckSum
        recompileRequired.addAll(trackedFile.stream()
                .filter(file -> serializedArgument
                        .stream()
                        .filter(serializedArgument -> file.getName().equals(serializedArgument.file()))
                        .findFirst()
                        .map(serializedArgument -> serializedArgument.checkSum().equals(CheckSum.fromFile(file.getAbsolutePath()).orElse(CheckSum.NIL)))
                        .orElse(Boolean.FALSE)
                )
                .collect(Collectors.toList()));

        recompileRequired = recompileRequired.stream().distinct().collect(Collectors.toList());

        // Discard all file that are no longer in the DB
        serializedArgument = serializedArgument.stream()
                .filter(serializedArgument -> trackedFile.stream().anyMatch(file -> serializedArgument.file().equals(file.getName())))
                .collect(Collectors.toList());

    }
}
