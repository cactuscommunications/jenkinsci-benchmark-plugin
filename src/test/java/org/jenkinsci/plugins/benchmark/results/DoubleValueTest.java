/**
 * MIT license
 * Copyright 2017 Autodesk, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.jenkinsci.plugins.benchmark.results;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jenkinsci.plugins.benchmark.exceptions.ValidationException;
import org.jenkinsci.plugins.benchmark.parsers.JsonToPlugin.MapJsonToPlugin;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test of the JSON mapper
 *
 * @author Daniel Mercier
 * @since 6/20/2017.
 */
public class DoubleValueTest {


    // The expected behavior is to get the largest value that does not correspond to the largest build number
    @Test
    public void testGetMaximum() throws ValidationException, ParserConfigurationException, SAXException, IOException {

        TestGroup testGroup = new TestGroup(null, "__root__");
        DoubleValue value = new DoubleValue(testGroup, "BestGroup", "DoubleValue");

        value.setValue(0, 0.1);
        value.setValue(1, 0.2);
        value.setValue(2, 0.3);
        value.setValue(3, 0.4);

        assertEquals(0.3, value.getMaximum(), 0.0);
    }

    // The expected behavior is to get the largest value that does not correspond to the largest build number
    @Test
    public void testGetMaximumUnordered() throws ValidationException, ParserConfigurationException, SAXException, IOException {

        TestGroup testGroup = new TestGroup(null, "__root__");
        DoubleValue value = new DoubleValue(testGroup, "BestGroup", "DoubleValue");

        value.setValue(0, 0.2);
        value.setValue(1, 0.1);
        value.setValue(2, 0.4);
        value.setValue(3, 0.3);

        assertEquals(0.4, value.getMaximum(), 0.0);
    }

    // The expected behavior is to get the smallest value that does not correspond to the largest build number
    @Test
    public void testGetMinimum() throws ValidationException, ParserConfigurationException, SAXException, IOException {

        TestGroup testGroup = new TestGroup(null, "__root__");
        DoubleValue value = new DoubleValue(testGroup, "BestGroup", "DoubleValue");

        value.setValue(0, 0.1);
        value.setValue(1, 0.2);
        value.setValue(2, 0.3);
        value.setValue(3, 0.4);

        assertEquals(0.1, value.getMinimum(), 0.0);
    }

    // The expected behavior is to get the smallest value that does not correspond to the largest build number
    @Test
    public void testGetMinimumUnordered() throws ValidationException, ParserConfigurationException, SAXException, IOException {

        TestGroup testGroup = new TestGroup(null, "__root__");
        DoubleValue value = new DoubleValue(testGroup, "BestGroup", "DoubleValue");

        value.setValue(0, 0.2);
        value.setValue(1, 0.3);
        value.setValue(2, 0.4);
        value.setValue(3, 0.1);

        assertEquals(0.2, value.getMinimum(), 0.0);
    }

}
