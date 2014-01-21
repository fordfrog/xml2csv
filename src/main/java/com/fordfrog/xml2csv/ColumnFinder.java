/**
 * Copyright 2013 Marcin Mielnicki
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Small tool which can be used to find names of all XML elements in a given XML
 * file. <br>
 * Result can be used as a argument for XML to CSV conversion utility.
 * 
 * @see Convertor
 * 
 * @author marcinm
 * 
 */
public class ColumnFinder {

    /**
     * Private constructor.
     */
    private ColumnFinder() {
    }

    /**
     * This tool prints XPath expressions for each XML element which contains
     * text in a given XML file. Additionally you can limit result to XML
     * elements which are children of given XML element.
     * <p>
     * Usage: ColumnFinder input-xml [parent-xpath]
     * <p>
     * For given XML:
     * 
     * <pre>
     * {@code
     * <root>
     *     <item>
     *         <value1>1</value1>
     *         <value2>2</value2>
     *         <value3>3</value3>
     *     </item>
     *     <item>
     *         <value1>a</value1>
     *         <value3>b</value3>
     *     </item>
     * </root>}
     * </pre>
     * 
     * it will print:
     * 
     * <pre>
     * /root/item/value1,/root/item/value2,/root/item/value3
     * </pre>
     * 
     * If you pass {@code /root/item} as a parameter it will print:
     * 
     * <pre>
     * value1,value2,value3
     * </pre>
     * 
     * 
     * @param args
     *            command array containing path to an XML file and name of a XML
     *            element
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
	Path inputFile = null;
	String itemName = null;
	if (args == null || args.length < 1) {
	    System.err.println("Usage: " + ColumnFinder.class.getName()
		    + " input-xml parent-xpath");
	    return;
	} else if (args.length == 1) {
	    itemName = "";
	} else {
	    itemName = args[1];
	}
	inputFile = Paths.get(args[0]);
	System.out.println(toString(find(Files.newInputStream(inputFile),
		itemName)));
    }

    /**
     * Joins elements of list into single string using comma as a separator.
     * 
     * @param list
     *            list of string values
     * @return string with elements from given list separated by comma
     */
    private static String toString(final List<String> list) {
	if (list.size() == 0) {
	    return "";
	}
	StringBuilder sb = new StringBuilder();
	sb.append(list.get(0));
	for (int i = 1; i < list.size(); i++) {
	    sb.append(',');
	    sb.append(list.get(i));
	}
	return sb.toString();
    }

    /**
     * Returns XPath expressions for XML elements which contains text in a given
     * XML document. Result is limited to XML elements which are children of
     * given XML element.
     * 
     * @param inputStream
     *            {@link InputStream} containing XML document
     * @param parentElement
     *            XPath which refers to parent XML element
     * @return list of XPath expressions for elements which contains text
     */
    public static List<String> find(final InputStream inputStream,
	    final String parentElement) {
	Set<String> elements = findElements(inputStream);
	List<String> result = new ArrayList<>();
	for (String element : elements) {
	    if (element.startsWith(parentElement)) {
		result.add(element.substring(parentElement.length(),
			element.length()));
	    }
	}
	return result;
    }

    /**
     * Returns XPath expressions for all XML elements in a given XML document.
     * 
     * @param inputStream
     *            {@link InputStream} containing XML document
     * 
     * @return list of XPath expressions for all XML elements which contains
     *         text
     */
    private static Set<String> findElements(final InputStream inputStream) {
	Set<String> elementNames = new LinkedHashSet<>();
	final XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();
	try {
	    final XMLStreamReader reader = xMLInputFactory
		    .createXMLStreamReader(inputStream);

	    find(elementNames, reader, "");
	} catch (final XMLStreamException ex) {
	    throw new RuntimeException("XML stream exception", ex);
	}
	return elementNames;
    }

    /**
     * Finds XPath expressions for all XML elements in given
     * {@link XMLStreamReader} and adds it to given set.
     * 
     * @param elementNames
     *            set with XPath expressions
     * @param reader
     *            XML stream reader
     * @param currentElement
     *            current XML element
     * @throws XMLStreamException
     *             if unexpected XML processing error occurs
     */
    private static void find(final Set<String> elementNames,
	    final XMLStreamReader reader, final String currentElement)
	    throws XMLStreamException {
	boolean hasText = false;
	String currentName = null;
	while (reader.hasNext()) {
	    switch (reader.next()) {
	    case XMLStreamReader.START_ELEMENT:
		currentName = currentElement + "/" + reader.getLocalName();
		find(elementNames, reader, currentName);
		break;
	    case XMLStreamReader.CHARACTERS:
		if (reader.isWhiteSpace()) {
		    break;
		}
		hasText = true;
		break;
	    case XMLStreamReader.END_ELEMENT:
		if (hasText) {
		    elementNames.add(currentElement);
		}
		return;
	    }
	}
    }

}
