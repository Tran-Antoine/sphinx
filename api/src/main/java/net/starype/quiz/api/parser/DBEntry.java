package net.starype.quiz.api.parser;

import net.starype.quiz.api.util.CheckSum;
import net.starype.quiz.api.util.SerializedArgument;
import net.starype.quiz.api.util.Serializer;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBEntry extends Serializer {
    private final DBTable table;
    private Set<ArgumentValue> indexedArguments;
    private Map<String, ByteBuffer> data;

    public DBEntry(DBTable table) {
        super(Stream.concat(table.getArguments()
                .stream(), Stream.of("checksum", "file"))
                .distinct()
                .map(SerializedArgument::new)
                .collect(Collectors.toList()));
        this.table = table;
        data = new HashMap<>();
        indexedArguments = new HashSet<>();
        table.getIndexedArguments().forEach(arg -> indexedArguments.add(new ArgumentValue(arg, "")));
    }

    public CheckSum checkSum() {
        return CheckSum.fromRawCheckSum(data.get("checksum"));
    }

    public void setCheckSum(CheckSum checkSum) {
        data.put("checksum", checkSum.rawData());
        compile();
    }

    public String file() {
        return get("file").orElseThrow();
    }

    public void setFile(String file) {
        set("file", file);
    }

    public Set<ArgumentValue> getIndexedArguments() {
        return indexedArguments;
    }

    public Optional<String> get(String entry) {
        return Optional.ofNullable(data.getOrDefault(entry, null)).map(buffer -> new String(buffer.array()));
    }

    public void set(String entry, String value) {
        if(!table.containsArgument(entry) && !entry.equals("file") && !entry.equals("checksum"))
            return;
        data.put(entry, ByteBuffer.wrap(value.getBytes()));
        updateIndexedArgument(entry, value);
        compile();
    }

    @Override
    public Optional<Map<String, ByteBuffer>> evaluate(ByteBuffer data) {
        Optional<Map<String, ByteBuffer>> returnData = super.evaluate(data);
        this.data = returnData.orElse(this.data);
        returnData.ifPresent(stringByteBufferMap -> stringByteBufferMap
                .forEach((entry, buffer) -> updateIndexedArgument(entry, new String(buffer.array()))));
        compile();
        return returnData;
    }

    private void updateIndexedArgument(String entry, String value) {
        indexedArguments.stream()
                .filter(argumentValue -> argumentValue.getName().equals(entry))
                .collect(Collectors.toList())
                .forEach(indexedArguments -> indexedArguments.setValue(value));
    }

    public void load(ByteBuffer data) {
        this.data = evaluate(data).orElseThrow();
        this.data.forEach((entry, buffer) -> updateIndexedArgument(entry, new String(buffer.array())));
        compile();
    }

    public ByteBuffer save() {
        return evaluate(data).orElseThrow();
    }

    private void compile() {
        indexedArguments = new HashSet<>();
        for(String indexedArgument : table.getIndexedArguments()) {
            indexedArguments.add(new ArgumentValue(indexedArgument,
                    new String(data.getOrDefault(indexedArgument, ByteBuffer.allocate(1)).array())));
        }
    }

}
