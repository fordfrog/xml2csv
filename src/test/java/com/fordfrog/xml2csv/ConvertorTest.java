package com.fordfrog.xml2csv;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class ConvertorTest {

    String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(this.getClass().getResource(path).getFile()));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }


    @Test
    public void testConvertSiple()
            throws IOException, URISyntaxException {
        String inputFile = "/input-simple.xml";
        String outputFile = "/output-simple.csv";
        Writer writer = new StringWriter();
        Convertor.convert(this.getClass().getResourceAsStream(inputFile), writer, new String[] { "value1", "value2",
                "value3" }, null, null, ';', false, false, "/root/item");

        String expected = readFile(outputFile, StandardCharsets.UTF_8);

        assertEquals(expected, writer.toString());
    }
    
    @Test
    public void testConvertNewLines()
            throws IOException, URISyntaxException {
        Writer writer = new StringWriter();
        Convertor.convert(new ByteArrayInputStream("<r><i><v>1\n1</v></i></r>".getBytes()), writer, new String[] { "v" }, null, null, ';', false, false, "/r/i");

        assertEquals("\"v\"\n\"1\n1\"\n", writer.toString());
    }

    @Test
    public void testConvertNewLinesBetweenXMLEscape()
            throws IOException, URISyntaxException {
        Writer writer = new StringWriter();
        Convertor.convert(new ByteArrayInputStream("<r><i><v>&lt;p /&gt;\n&lt;p /&gt;</v></i></r>".getBytes()), writer, new String[] { "v" }, null, null, ';', false, false, "/r/i");

        assertEquals("\"v\"\n\"<p />\n<p />\"\n", writer.toString());
    }
    
    @Test
    public void testConvertTrimValues()
            throws IOException, URISyntaxException {
        String inputFile = "/input-simple.xml";
        String outputFile = "/output-trim.csv";
        Writer writer = new StringWriter();
        Convertor.convert(this.getClass().getResourceAsStream(inputFile), writer, new String[] { "value1", "value2",
                "value3" }, null, null, ';', true, false, "/root/item");

        String expected = readFile(outputFile, StandardCharsets.UTF_8);

        assertEquals(expected, writer.toString());
    }
    
    @Test
    public void testConvertMutipleSelectFirst()
            throws IOException, URISyntaxException {
        String inputFile = "/input-multiple.xml";
        String outputFile = "/output-multiple-select-first.csv";
        Writer writer = new StringWriter();

        Convertor.convert(this.getClass().getResourceAsStream(inputFile), writer, new String[] { "value1", "value2",
                "value3" }, null, null, ',', false, false, "/root/item");

        String expected = readFile(outputFile, StandardCharsets.UTF_8);
        assertEquals(expected, writer.toString());
    }
    
    @Test
    public void testConvertMutipleJoin()
            throws IOException, URISyntaxException {
        String inputFile = "/input-multiple.xml";
        String outputFile = "/output-multiple-join.csv";
        Writer writer = new StringWriter();

        Convertor.convert(this.getClass().getResourceAsStream(inputFile), writer, new String[] { "value1", "value2",
                "value3" }, null, null, ',', false, true, "/root/item");

        String expected = readFile(outputFile, StandardCharsets.UTF_8);
        assertEquals(expected, writer.toString());
    }
    
    @Test
    public void testConvertDeep()
            throws IOException, URISyntaxException {
        String inputFile = "/input-deep.xml";
        String outputFile = "/output-deep.csv";
        Writer writer = new StringWriter();
        Convertor.convert(this.getClass().getResourceAsStream(inputFile), writer, new String[] { "value1", "value2",
                "value3" }, null, null, ';', false, false, "/root/item0/item1/item2");

        String expected = readFile(outputFile, StandardCharsets.UTF_8);

        assertEquals(expected, writer.toString());
    }
    

    @Test
    public void testConvertHierarchy()
            throws IOException, URISyntaxException {
        String inputFile = "/input-hierarchy.xml";
        String outputFile = "/output-hierarchy.csv";
        Writer writer = new StringWriter();

        Convertor.convert(this.getClass().getResourceAsStream(inputFile), writer, new String[] { "header/value1",
                "body/value3", "body/value4/value41", "body/value4/value42" }, null, null, ',', false, false, "/root/item");

        String expected = readFile(outputFile, StandardCharsets.UTF_8);
        assertEquals(expected, writer.toString());
    }

}
