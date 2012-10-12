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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV utility class.
 *
 * @author fordfrog
 */
public class CsvUtils {

    /**
     * Quotes and escapes string for output to CSV format.
     *
     * @param string string
     *
     * @return quoted string
     */
    public static String quoteString(final String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        return '"' + string.replace("\"", "\"\"") + '"';
    }

    /**
     * Parses values from CSV line string using comma as separator.
     *
     * @param line line string
     *
     * @return array of line values
     */
    public static String[] parseValues(final String line) {
        final List<String> values = new ArrayList<>(2);
        int pos = 0;

        while (pos + 1 < line.length()) {
            final boolean quoted =
                    line.charAt(pos) == '"' || line.charAt(pos) == '\'';

            if (quoted) {
                final int endPos = findEndQuote(line, pos);
                final String value = line.substring(pos + 1, endPos);
                values.add(unescape(value, line.charAt(pos)));

                // we must add one for quote and one for another value separator
                pos = endPos + 2;
            } else {
                final int endPos = line.indexOf(',', pos);
                final String value = line.substring(
                        pos, endPos == -1 ? line.length() : endPos);
                values.add(value);
                pos = endPos == -1 ? line.length() : endPos + 1;
            }
        }

        return values.toArray(new String[values.size()]);
    }

    /**
     * Finds end quote for the string starting at start position.
     *
     * @param line     line string
     * @param startPos start position including leading quote
     *
     * @return position of ending quote
     */
    private static int findEndQuote(final String line, final int startPos) {
        final char quoteChar = line.charAt(startPos);
        boolean quoted = true;

        for (int i = startPos + 1; i < line.length(); i++) {
            final char chr = line.charAt(i);

            if (chr == quoteChar) {
                if (i > 0 && line.charAt(i - 1) != '\\' || i == 0) {
                    quoted = !quoted;

                    if (!quoted && (i + 1 == line.length()
                            || line.charAt(i + 1) == ',')) {
                        return i;
                    }
                }
            }
        }

        throw new RuntimeException(MessageFormat.format(
                "End quote was not found on line: {0}", line));
    }

    /**
     * Unescapes specified quote character. Bot double quote and backslash
     * escapes are supported.
     *
     * @param string    string
     * @param quoteChar quote character
     *
     * @return unescaped string
     */
    @SuppressWarnings("AssignmentToForLoopParameter")
    private static String unescape(final String string, final char quoteChar) {
        final StringBuilder sbString = new StringBuilder(string.length());
        boolean escaped = false;

        for (int i = 0; i < string.length(); i++) {
            final char chr = string.charAt(i);

            if (chr == '\\') {
                if (i + 1 < string.length()
                        && string.charAt(i + 1) == quoteChar) {
                    i++;
                    sbString.append(quoteChar);
                } else {
                    sbString.append(chr);
                }
            } else if (chr == quoteChar) {
                if (escaped) {
                    sbString.append(chr);
                }

                escaped = !escaped;
            } else {
                sbString.append(chr);
            }
        }

        return sbString.toString();
    }

    /**
     * Creates new instance of CsvUtils.
     */
    private CsvUtils() {
    }
}
