package net.starype.quiz.api.parser;

import net.starype.quiz.api.util.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompiledSearch implements IndexDatabase {

    private static class CompiledSearchSerializer extends Serializer {
        private Set<ArgumentValue> arguments;
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
            data = new HashMap<>();
        }

        private void compile() {
            arguments = new HashSet<>();
            for(String str : Arrays.asList("name", "file", "tags", "difficulty")) {
                arguments.add(new ArgumentValue(str, new String(data.get(str).array())));
            }
        }

        public Set<ArgumentValue> getArguments() {
            return arguments;
        }

        @Override
        public Optional<Map<String, ByteBuffer>> evaluate(ByteBuffer data) {
            Optional<Map<String, ByteBuffer>> r = super.evaluate(data);
            this.data = r.orElse(this.data);
            compile();
            return r;
        }

        public void load(ByteBuffer data) {
            // First parse the data
            this.data = evaluate(data).orElseThrow();

            // Then parse the tags
            tags = Set.of(new String(this.data.get("tags").array()).split(";"));
            compile();
        }

        public ByteBuffer save() {
            // Combine the map using the Serializer
            return evaluate(data).orElseThrow();
        }

        public String file() {
            return new String(data.get("file").array());
        }

        public CheckSum checkSum() {
            return CheckSum.fromRawCheckSum(data.get("checksum"));
        }

        public String name() {
            return new String(data.get("name").array());
        }

        public Set<? extends String> tags() {
            return tags;
        }

        public String getDifficulty() {
            return new String(data.get("difficulty").array());
        }

        public void setName(String name) {
            data.put("name", ByteBuffer.wrap(name.getBytes()));
            compile();
        }

        public void setDifficulty(String name) {
            data.put("difficulty", ByteBuffer.wrap(name.getBytes()));
            compile();
        }

        public void setFile(String file) {
            data.put("file", ByteBuffer.wrap(file.getBytes()));
            compile();
        }

        public void setChecksum(CheckSum checksum) {
            this.data.put("checksum", checksum.rawData());
            compile();
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
            this.data.put("tags", ByteBuffer.wrap(tags.stream()
                    .reduce((s1, s2) -> s1 + ";" + s2)
                    .orElseThrow().getBytes()));
            compile();
        }
    }

    private List<CompiledSearchSerializer> serializedArgument;
    private boolean isCompiled = false;
    private final String compiledDB;
    private final List<? extends String> trackedFile;

    private CompiledSearch(List<? extends String> trackedFile, String compiledDB)  {
        this.serializedArgument = new ArrayList<>();
        this.trackedFile = trackedFile;
        this.compiledDB = compiledDB;
    }

    public static CompiledSearch fromListOfFile(List<? extends String> trackedFile, String compiledDB) {
        return new CompiledSearch(trackedFile, compiledDB);
    }

    public static Optional<CompiledSearch> fromDirectory(String directoryPath, String compiledDB) {
        File directory = new File(directoryPath);
        if(!directory.isDirectory()) return Optional.empty();

        // Get all the file from the directory
        final List<String> files = FileUtils.listAllFiles(directory)
                .stream()
                .map(File::getPath)
                .collect(Collectors.toList());

        return Optional.of(new CompiledSearch(files, compiledDB));
    }

    public boolean save() {
        try {
            // First create a new output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Secondly output the number of file
            outputStream.write(ByteBuffer.allocate(4).putInt(serializedArgument.size()).array());

            // Then output each file individually
            for(CompiledSearchSerializer compiledSearchSerializer : serializedArgument) {
                // Compile the data of the serializedArgument
                ByteBuffer buffer = compiledSearchSerializer.save();

                // Write the ByteBuffer size
                outputStream.write(ByteBuffer.allocate(4).putInt(buffer.array().length).array());

                // Write the buffer in the stream
                outputStream.write(buffer.array());
            }

            // Then try to write the outputStream to the file
            FileOutputStream fileOutputStream = new FileOutputStream(compiledDB);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.close();
            outputStream.close();

            return true;
        }
        catch (IOException ignored) {
            return false;
        }
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
        List<String> recompileRequired = getRecompileList();;

        // For each of file that must be recompile perform the recompilation
        recompileRequired.forEach(this::compile);
    }

    private void compile(String file) {
        // First parse the whole file to retrieve the following information
        String name = "generic-name";
        Set<String> tags = Set.of("tag1", "tag2", "tag3");
        String difficulty = "EASY";

        // Compute the CheckSum calculation for the new file
        CheckSum checkSum = CheckSum.fromFile(file).orElseThrow();

        // Update the DB
        // First remove all the old version of this file
        serializedArgument = serializedArgument.stream()
                .filter(serializedArgument -> !serializedArgument.checkSum().equals(checkSum))
                .collect(Collectors.toList());

        // Finally adding the new file in the DB
        CompiledSearchSerializer compiledSearchSerializer = new CompiledSearchSerializer();
        compiledSearchSerializer.setFile(file);
        compiledSearchSerializer.setName(name);
        compiledSearchSerializer.setTags(tags);
        compiledSearchSerializer.setDifficulty(difficulty);
        compiledSearchSerializer.setChecksum(checkSum);
        serializedArgument.add(compiledSearchSerializer);
    }

    private List<String> getRecompileList() {
        List<String> recompileRequired = new ArrayList<>();

        // Add all file that are in the recompileRequired but not in the last DB
        recompileRequired.addAll(trackedFile.stream()
                .filter(file -> serializedArgument.stream()
                        .noneMatch(serializedArgument -> serializedArgument.file().equals(file)))
                .collect(Collectors.toList()));

        // Detect all the file which differs by their CheckSum
        recompileRequired.addAll(trackedFile.stream()
                .filter(file -> serializedArgument
                        .stream()
                        .filter(serializedArgument -> file.equals(serializedArgument.file()))
                        .findFirst()
                        .map(serializedArgument -> serializedArgument.checkSum().equals(CheckSum.fromFile(file).orElse(CheckSum.NIL)))
                        .orElse(Boolean.FALSE)
                )
                .collect(Collectors.toList()));

        recompileRequired = recompileRequired.stream().distinct().collect(Collectors.toList());

        // Discard all file that are no longer in the DB
        serializedArgument = serializedArgument.stream()
                .filter(serializedArgument -> trackedFile.stream().anyMatch(file -> serializedArgument.file().equals(file)))
                .collect(Collectors.toList());

        return recompileRequired;
    }

    @Override
    public List<String> query(IndexQuery query) {
        // Assert that the DB has been compiled
        if(!isCompiled) {
            throw new RuntimeException("Cannot create queries before the compilation of the DB");
        }

        // Perform the query
        return serializedArgument.stream()
                .filter(serializedArgument -> query.match(serializedArgument.getArguments()))
                .map(CompiledSearchSerializer::file)
                .collect(Collectors.toList());
    }
}
