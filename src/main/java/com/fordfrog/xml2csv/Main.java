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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * Main class.
 *
 * @author fordfrog
 */
public class Main {

    /**
     * Main method.
     *
     * @param args
     */
    @SuppressWarnings("AssignmentToForLoopParameter")
    public static void main(final String[] args) {
        if (args == null || args.length == 0) {
            printUsage();

            return;
        }

        final Filters filters = new Filters();
        final Remappings remappings = new Remappings();
        String[] columns = null;
        Path inputFile = null;
        Path outputFile = null;
        Filter filter = null;
        Remapping remapping = null;
        char separator = ',';
        boolean trimValues = false;
        boolean join = false;
        String itemName = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--columns":
                    i++;
                    columns = args[i].split(",");
                    break;
                case "--filter-column":
                    filter = new Filter();
                    filters.addFilter(filter);
                    i++;
                    filter.setColumn(args[i]);
                    break;
                case "--filter-exclude":
                    filter.setExclude(true);
                    break;
                case "--filter-include":
                    filter.setExclude(false);
                    break;
                case "--filter-values":
                    i++;
                    filter.setValues(loadValues(Paths.get(args[i])));
                    break;
                case "--input":
                    i++;
                    inputFile = Paths.get(args[i]);
                    break;
                case "--item-name":
                    i++;
                    itemName = args[i];
                    break;
                case "--output":
                    i++;
                    outputFile = Paths.get(args[i]);
                    break;
                case "--remap-column":
                    remapping = new Remapping();
                    remappings.addRemapping(remapping);
                    i++;
                    remapping.setColumn(args[i]);
                    break;
                case "--remap-map":
                    i++;
                    remapping.setMap(loadMap(Paths.get(args[i])));
                    break;
                case "--separator":
                    i++;
                    if (args[i].length() == 1) {
                        separator = args[i].charAt(0);
                    } else {
                        throw new RuntimeException("Separator must be a character. ");
                    }
                    break;
                case "--trim":
                    trimValues = true;
                    break;
                case "--join":
                    join = true;
                    break;
                default:
                    throw new RuntimeException(MessageFormat.format(
                            "Unsupported command line argument: {0}", args[i]));
            }
        }

        Objects.requireNonNull(columns, "--columns argument must be specified, "
                + "example: --columns COL1,COL2");
        Objects.requireNonNull(columns, "--input argument must be specified, "
                + "example: --input input_file_path");
        Objects.requireNonNull(columns, "--output argument must be specified, "
                + "example: --output output_file_path");

        Convertor.convert(inputFile, outputFile, columns, filters, remappings, separator, trimValues, join, itemName);
    }

    /**
     * Prints application usage information.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void printUsage() {
        try (final InputStream inputStream =
                        Main.class.getResourceAsStream("/usage.txt");
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"))) {
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Failed to output usage information", ex);
        }
    }

    /**
     * Loads list of values from specified file.
     *
     * @param file file path
     *
     * @return collection of loaded values
     */
    private static Collection<String> loadValues(final Path file) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final Collection<String> values = new HashSet<>();

        try (final BufferedReader reader = Files.newBufferedReader(
                        file, Charset.forName("UTF-8"))) {
            String line = reader.readLine();

            while (line != null) {
                values.add(line);
                line = reader.readLine();
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to load values", ex);
        }

        return values;
    }

    /**
     * Loads key value pairs from specified file. Values must be separated with
     * comma.
     *
     * @param file file path
     *
     * @return map of loaded key value pairs
     */
    private static Map<String, String> loadMap(final Path file) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final Map<String, String> map = new HashMap<>();

        try (final BufferedReader reader = Files.newBufferedReader(
                        file, Charset.forName("UTF-8"))) {
            String line = reader.readLine();

            while (line != null) {
                if (!line.isEmpty()) {
                    final String[] pair = CsvUtils.parseValues(line);
                    map.put(pair[0], pair.length > 1 ? pair[1] : "");
                }

                line = reader.readLine();
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to load map", ex);
        }

        return map;
    }
}
