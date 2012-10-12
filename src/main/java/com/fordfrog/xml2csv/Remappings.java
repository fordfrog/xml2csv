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
 * Remappings handler.
 *
 * @author fordfrog
 */
public class Remappings {

    /**
     * Collection of remappings.
     */
    private final Collection<Remapping> remappings = new ArrayList<>(10);

    /**
     * Adds remapping to the collection of remappings.
     *
     * @param remapping remapping
     */
    public void addRemapping(final Remapping remapping) {
        remappings.add(remapping);
    }

    /**
     * Replaces values in item using all specified remappings.
     *
     * @param itemValues item values
     */
    public void replaceValues(final Map<String, String> itemValues) {
        for (final Remapping remapping : remappings) {
            remapping.replaceValues(itemValues);
        }
    }
}
