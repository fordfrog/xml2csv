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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Filter class.
 *
 * @author fordfrog
 */
public class Filter {

    /**
     * Name of the column to filter.
     */
    private String column;
    /**
     * Filter values.
     */
    private Collection<String> values;
    /**
     * If true then items with specified values are excluded, if true then only
     * items with specified values are included.
     */
    private boolean exclude;

    /**
     * Getter for {@link #column}.
     *
     * @return {@link #column}
     */
    public String getColumn() {
        return column;
    }

    /**
     * Setter for {@link #column},
     *
     * @param column {@link #column}
     */
    public void setColumn(final String column) {
        this.column = column;
    }

    /**
     * Getter for {@link #values}.
     *
     * @return {@link #values}
     */
    public Collection<String> getValues() {
        return Collections.unmodifiableCollection(values);
    }

    /**
     * Setter for {@link #values}.
     *
     * @param values {@link #values}
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setValues(final Collection<String> values) {
        this.values = values;
    }

    /**
     * Getter for {@link #exclude}.
     *
     * @return {@link #exclude}
     */
    public boolean isExclude() {
        return exclude;
    }

    /**
     * Setter for {@link #exclude}.
     *
     * @param exclude {@link #exclude}
     */
    public void setExclude(final boolean exclude) {
        this.exclude = exclude;
    }

    /**
     * Checks whether the item matches the filter.
     *
     * @param itemValues map of item columns and corresponding values
     *
     * @return true if item matches filter and should be included, false if item
     *         does not match filter and should be excluded
     */
    public boolean matchesFilter(final Map<String, String> itemValues) {
        if (values == null) {
            return exclude ? true : false;
        }

        final String itemValue = itemValues.get(column);

        if (itemValue == null) {
            return exclude ? true : false;
        } else if (values.contains(itemValue)) {
            return exclude ? false : true;
        } else {
            return exclude ? true : false;
        }
    }
}
