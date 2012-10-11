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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Filters handler.
 *
 * @author fordfrog
 */
public class Filters {

    /**
     * List of all collection of all defined filters.
     */
    private final Collection<Filter> filters = new ArrayList<>(10);

    /**
     * Adds filter to the collection of filters.
     *
     * @param filter filter
     */
    public void addFilter(final Filter filter) {
        filters.add(filter);
    }

    /**
     * Checks whether item matches all defined filters.
     *
     * @param itemValues map of item columns and corresponding values
     *
     * @return true if item matches all filters, false if item does not match at
     *         least one filter
     */
    public boolean matchesFilters(final Map<String, String> itemValues) {
        for (final Filter filter : filters) {
            if (!filter.matchesFilter(itemValues)) {
                return false;
            }
        }

        return true;
    }
}
