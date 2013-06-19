package com.fordfrog.xml2csv;

import static org.junit.Assert.assertEquals;

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
                "value3" }, null, null, ';');

        String expected = readFile(outputFile, StandardCharsets.UTF_8);

        assertEquals(expected, writer.toString());
    }

}
