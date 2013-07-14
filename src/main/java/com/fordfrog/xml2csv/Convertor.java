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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

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
     * @param remappings optional remappings
     * @param separator field separator
     * @param join whether to join multiple values or not
     * @param itemName XPath which refers to XML element which will be converted to a row
     */
    public static void convert(final Path inputFile, final Path outputFile,
            final String[] columns, final Filters filters,
            final Remappings remappings, final char separator, final boolean trim, final boolean join, final String itemName) {
        try (final InputStream inputStream = Files.newInputStream(inputFile);
                final Writer writer = Files.newBufferedWriter(
                        outputFile, Charset.forName("UtF-8"))) {
            convert(inputStream, writer, columns, filters, remappings, separator, trim, join, itemName);
        } catch (final IOException ex) {
            throw new RuntimeException("IO operation failed", ex);
        } 
    }

    /**
     * Converts input stream with XML to CSV saved into writer.
     *
     * @param inputStream  input stream
     * @param writer writer
     * @param columns    array of column names
     * @param filters    optional filters
     * @param remappings optional remappings
     * @param separator field separator
     * @param trim whether to trim values or not
     * @param join whether to join multiple values or not
     * @param itemName XPath which refers to XML element which will be converted to a row
     */
    public static void convert(final InputStream inputStream, final Writer writer,
            final String[] columns, final Filters filters,
            final Remappings remappings, final char separator, final boolean trim, final boolean join, final String itemName) {
        final XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();
        String itemXPath = itemName;
        if (itemName.trim().isEmpty()){
            throw new IllegalArgumentException("itemName is an empty string. ");
        }
        if (itemName.trim().length() != 1 && itemName.endsWith("/")){
            throw new IllegalArgumentException("itemName cannot end with a shash (/). ");
        }

        try {
            final XMLStreamReader reader =
                    xMLInputFactory.createXMLStreamReader(inputStream);

            writeHeader(writer, columns, separator);

            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamReader.START_ELEMENT:
                        processRoot(
                                reader, writer, columns, filters, remappings, separator, trim, join, getParentName(null, reader.getLocalName()), itemXPath);
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
     * @param separator field separator
     *
     * @throws IOException Thrown if problem occurred while writing to output
     *                     file.
     */
    private static void writeHeader(final Writer writer,
            final String[] columns, final char separator) throws IOException {
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                writer.append(separator);
            }

            writer.append(CsvUtils.quoteString(columns[i]));
        }

        writer.append('\n');
    }

    /**
     * Processes root element and its subelements.
     *
     * @param reader     XML stream reader
     * @param writer     CSV file writer
     * @param columns    array of columns
     * @param filters    optional filters
     * @param remappings optional remappings
     * @param separator field separator
     * @param trim whether to trim values or not
     * @param join whether to join multiple values or not
     * @param parentElement XPath which refers to parent element
     * @param itemName XPath which refers to XML element which will be converted to a row
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws IOException        Thrown if IO problem occurred.
     */
    private static void processRoot(final XMLStreamReader reader,
            final Writer writer, final String[] columns, final Filters filters,
            final Remappings remappings, final char separator, final boolean trim, final boolean join, final String parentElement, final String itemName) throws XMLStreamException,
            IOException {
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    String currentElementPath = getParentName(parentElement, reader.getLocalName());
                    if ((currentElementPath).compareTo(itemName) == 0) {
                        Map<String, List<String>> values = new HashMap<>(columns.length);
                        processItem(reader, writer, columns, filters, remappings, separator, trim, join, currentElementPath, values, itemName);
                    } else {
                        processRoot(reader, writer, columns, filters, remappings, separator, trim, join, currentElementPath, itemName);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    return;
            }
        }
    }

    /**
     * Processes item element.
     *
     * @param reader     XML stream reader
     * @param writer     CSV file writer
     * @param columns    array of columns
     * @param filters    optional filters
     * @param remappings optional remappings
     * @param separator field separator
     * @param trim whether to trim values or not
     * @param join whether to join multiple values or not
     * @param parentElement XPath which refers to parent element
     * @param values values of XML element for current row
     * @param itemName element name which will be converted to row
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws IOException        Thrown if IO problem occurred.
     */
    private static void processItem(final XMLStreamReader reader,
            final Writer writer, final String[] columns, final Filters filters,
            final Remappings remappings, final char separator, final boolean trim, final boolean join, final String parentElement, final Map<String, List<String>> values, final String itemName) throws XMLStreamException,
            IOException {
        StringBuilder sb = new StringBuilder();

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    String currentElementPath = getParentName(parentElement, reader.getLocalName());
                    processItem(reader, writer, columns, filters, remappings, separator, trim, join, currentElementPath, values, itemName);
                    break;
                case XMLStreamReader.CHARACTERS:
                    sb.append(reader.getText());
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if ((parentElement).compareTo(itemName) == 0) {
                        final Map<String, String> singleValues = new HashMap<>(columns.length);
                        for (Entry<String, List<String>> mapEntry : values.entrySet()) {
                            singleValues.put(mapEntry.getKey(), prepareValue(mapEntry.getValue(), ", ", trim, join));
                        }
                        if (filters == null || filters.matchesFilters(singleValues)) {
                            if (remappings != null) {
                                remappings.replaceValues(singleValues);
                            }
    
                            writeRow(writer, columns, singleValues, separator);
                        }
                      } else {
                          processValue(parentElement.replaceFirst(Pattern.quote(itemName + "/"), ""), sb.toString(), values);                      
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
     * @param separator field separator
     *
     * @throws IOException Thrown if problem occurred while writing to output
     *                     file.
     */
    private static void writeRow(final Writer writer, final String[] columns,
            final Map<String, String> values, final char separator) throws IOException {
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                writer.append(separator);
            }

            writer.append(CsvUtils.quoteString(values.get(columns[i])));
        }

        writer.append('\n');
    }
    
    /**
     * Joins elements from the list using given separator or return first element from the list. Use trim=<code>true</code> to trim values. 
     *  
     * @param values list of values
     * @param valueSeparator string used to separate values
     * @param trim whether to trim values or not
     * @param join whether to join multiple values or not
     * 
     * @return String containing separated values from the list or first element from the list. 
     */
    private static String prepareValue(List<String> values, final String valueSeparator, final boolean trim, final boolean join) {
        if (values.isEmpty()) {
            return null;
        }
        if (join) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                if (trim) {
                    value = value.trim();
                }
                sb.append(value);
                if (i < values.size() - 1) {
                    sb.append(valueSeparator);
                }
            }
            return sb.toString();
        } else {
            String value = values.get(0);
            return trim ? value.trim() : value;
        }
    }
    
    /**
     * Prepare path to the current element. 
     * 
     * @param parentName path to parent element
     * @param currentElement XML element name
     * @return path to the current element
     */
    private static String getParentName(final String parentName, final String currentElement) {
        return (parentName == null ? "" : parentName) + "/" + currentElement;
    }

    /**
     * Adds a single value of XML item. Only columns contains in array are
     * added to the values map.
     *
     * @param elementName name of XML element
     * @param value value to be added
     * @param values map for storing values
     */
    private static void processValue(String elementName, String value, Map<String, List<String>> values) {
        if (!values.containsKey(elementName)) {
            values.put(elementName, new ArrayList<String>());
        }
        values.get(elementName).add(value);        
    }

    /**
     * Creates new instance of Convertor.
     */
    private Convertor() {
    }
}
