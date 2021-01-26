package net.starype.quiz.api.reader;

import net.starype.quiz.api.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StringUtilsTest {
    @Test
    public void test_pack() {
        Assert.assertEquals(StringUtils.pack(List.of("a", "b", "c")), "a;b;c");
        Assert.assertEquals(StringUtils.pack(List.of("a", "b", "\\c")), "a;b;\\\\c");
        Assert.assertEquals(StringUtils.pack(List.of("a", "b;", "\\c")), "a;b\\;;\\\\c");
    }

    @Test
    public void test_unpack() {
        Assert.assertEquals(StringUtils.unpack("a;b;c"), List.of("a", "b", "c"));
        Assert.assertEquals(StringUtils.unpack("a;b;\\\\c"), List.of("a", "b", "\\c"));
        Assert.assertEquals(StringUtils.unpack("a;b\\;;\\\\c"), List.of("a", "b;", "\\c"));
    }

    @Test
    public void test_map_pack() {
        // Assert.assertEquals("a0=v0;a1=v1", StringUtils.packMap(Map.of("a0", "v0", "a1", "v1")));
        // Assert.assertEquals("a1=v1;a\\\\0=v0", StringUtils.packMap(Map.of("a\\0", "v0", "a1", "v1")));
        // Assert.assertEquals("a1=v1;a\\\\\\;0=v0", StringUtils.packMap(Map.of("a\\;0", "v0", "a1", "v1")));
    }

    @Test
    public void test_map_unpack() {
        Assert.assertEquals(Map.of("a0", "v0", "a1", "v1"), StringUtils.unpackMap("a1=v1;a0=v0"));
        Assert.assertEquals(Map.of("a\\0", "v0", "a1", "v1"), StringUtils.unpackMap("a\\\\0=v0;a1=v1"));
        Assert.assertEquals(Map.of("a\\;0", "v0", "a1", "v1"), StringUtils.unpackMap("a1=v1;a\\\\\\;0=v0"));
    }
}
