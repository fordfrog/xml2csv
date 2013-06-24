package com.fordfrog.xml2csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ColumnFinderTest {

    @Test
    public void test() {
        InputStream inputStream = this.getClass().getResourceAsStream("/input-columns.xml");
        assertNotNull(inputStream);
        List<String> expected = Arrays.asList(new String[] { "item1/item2/value1", "value2", "value3", "value4" });

        List<String> columns = ColumnFinder.find(inputStream, "/root/item/");

        assertEquals(expected, columns);
    }

}
