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

import java.util.Collections;
import java.util.Map;

/**
 * Remapping information.
 *
 * @author fordfrog
 */
public class Remapping {

    /**
     * Column name.
     */
    private String column;
    /**
     * Map of original values and new values.
     */
    private Map<String, String> map;

    /**
     * Getter for {@link #column}.
     *
     * @return {@link #column}
     */
    public String getColumn() {
        return column;
    }

    /**
     * Setter {@link #column}.
     *
     * @param column {@link #column}
     */
    public void setColumn(final String column) {
        this.column = column;
    }

    /**
     * Getter for {@link #map}.
     *
     * @return {@link #map}
     */
    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Setter for {@link #map}.
     *
     * @param map {@link #map}
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setMap(final Map<String, String> map) {
        this.map = map;
    }

    /**
     * Replaces values in {@link #column} with values from {@link #map} if
     * current value is present in {@link #map} as key.
     *
     * @param itemValues item values
     */
    public void replaceValues(final Map<String, String> itemValues) {
        final String itemValue = itemValues.get(column);

        if (itemValue == null) {
            return;
        }

        final String newValue = map.get(itemValue);

        if (newValue != null) {
            itemValues.put(column, newValue);
        }
    }
}
