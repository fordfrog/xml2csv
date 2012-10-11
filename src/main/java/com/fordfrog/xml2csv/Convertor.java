/**
 * Copyright 2012 Miroslav Šulc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.fordfrog.xml2csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * XML to CSV convertor.
 *
 * @author fordfrog
 */
public class Convertor {

    /**
     * Converts input XML file to output CSV file.
     *
     * @param inputFile  input file path
     * @param outputFile output file path
     * @param columns    array of column names
     * @param filters    optional filters
     */
    public static void convert(final Path inputFile, final Path outputFile,
            final String[] columns, final Filters filters) {
        final XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();

        try (final InputStream inputStream = Files.newInputStream(inputFile);
                final Writer writer = Files.newBufferedWriter(
                        outputFile, Charset.forName("UtF-8"))) {
            final XMLStreamReader reader =
                    xMLInputFactory.createXMLStreamReader(inputStream);

            writeHeader(writer, columns);

            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamReader.START_ELEMENT:
                        processRoot(reader, writer, columns, filters);
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException("IO operation failed", ex);
        } catch (final XMLStreamException ex) {
            throw new RuntimeException("XML stream exception", ex);
        }
    }

    /**
     * Writes CVS header.
     *
     * @param writer  writer
     * @param columns array of columns
     *
     * @throws IOException Thrown if problem occurred while writing to output
     *                     file.
     */
    private static void writeHeader(final Writer writer,
            final String[] columns) throws IOException {
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                writer.append(',');
            }

            writer.append(quoteString(columns[i]));
        }

        writer.append('\n');
    }

    /**
     * Quotes and escapes string for output to CSV format.
     *
     * @param string string
     *
     * @return quoted string
     */
    private static String quoteString(final String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        return '"' + string.replace("\"", "\"\"") + '"';
    }

    /**
     * Processes root element and its subelements.
     *
     * @param reader  XML stream reader
     * @param writer  CSV file writer
     * @param columns array of columns
     * @param filters optional filters
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws IOException        Thrown if IO problem occurred.
     */
    private static void processRoot(final XMLStreamReader reader,
            final Writer writer, final String[] columns, final Filters filters)
            throws XMLStreamException, IOException {
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    processItem(reader, writer, columns, filters);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return;
            }
        }
    }

    /**
     * Processes item element.
     *
     * @param reader  XML stream reader
     * @param writer  CSV file writer
     * @param columns array of columns
     * @param filters optional filters
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws IOException        Thrown if IO problem occurred.
     */
    private static void processItem(final XMLStreamReader reader,
            final Writer writer, final String[] columns, final Filters filters)
            throws XMLStreamException, IOException {
        final Map<String, String> values = new HashMap<>(columns.length);

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    processValue(reader, columns, values);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (filters == null || filters.matchesFilters(values)) {
                        writeRow(writer, columns, values);
                    }

                    return;
            }
        }
    }

    /**
     * Writes XML item to CSV as CSV row.
     *
     * @param writer  CSV file writer
     * @param columns array of columns
     * @param values  map of values
     *
     * @throws IOException Thrown if problem occurred while writing to output
     *                     file.
     */
    private static void writeRow(final Writer writer, final String[] columns,
            final Map<String, String> values) throws IOException {
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                writer.append(',');
            }

            writer.append(quoteString(values.get(columns[i])));
        }

        writer.append('\n');
    }

    /**
     * Processes single value of XML item. Only columns contains in array are
     * added to the values map.
     *
     * @param reader  XML stream reader
     * @param columns array of columns
     * @param values  map for storing values
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processValue(final XMLStreamReader reader,
            final String[] columns, final Map<String, String> values)
            throws XMLStreamException {
        final String localName = reader.getLocalName();
        values.put(localName, reader.getElementText());
    }

    /**
     * Creates new instance of Convertor.
     */
    private Convertor() {
    }
}
