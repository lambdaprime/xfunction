/*
 * Copyright 2019 lambdaprime
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.xfunction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Set of functions to process XML
 */
public class Xml {

    /**
     * Run XPath query to a given file and return results as a list
     */
    public static List<String> query(Path xml, String xpath) {
        List<String> out = new ArrayList<>();
        Consumer<Node> visitor = saveVisitor(out);
        try {
            xpath_(new InputSource(new FileInputStream(xml.toFile())), xpath, visitor);
            return out;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Run XPath query to a given XML string and return results as a list
     */
    public static List<String> query(String xml, String xpath) {
        List<String> out = new ArrayList<>();
        Consumer<Node> visitor = saveVisitor(out);
        xpath_(new InputSource(new StringReader(xml)), xpath, visitor);
        return out;
    }

    /**
     * Replace all elements which satisfy given XPath expression.
     * @param xml input file where to perform the replace
     * @param xpath XPath expression
     * @param value new value to replace with
     */
    public static void replace(Path xml, String xpath, String value) {
        try {
            Consumer<Node> visitor = replaceVisitor(value);
            String str = asString(xpath_(new InputSource(new FileInputStream(xml.toFile())), xpath, visitor));
            Files.writeString(xml, str, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Replace all elements which satisfy given XPath expression.
     * @param xml input string where to perform the replace
     * @param xpath XPath expression
     * @param value new value to replace with
     */
    public static String replace(String xml, String xpath, String value) {
        Consumer<Node> visitor = replaceVisitor(value);
        return asString(xpath_(new InputSource(new StringReader(xml)), xpath, visitor));
    }

    private static void parseNodeList(NodeList l, Consumer<Node> visitor) {
        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE: {
                parseNodeList(n.getChildNodes(), visitor);
                break;
            }
            case Node.TEXT_NODE: {
                String value = n.getNodeValue();
                if (value.trim().isEmpty()) continue;
                visitor.accept(n);
            }}
        }
    }

    private static Consumer<Node> saveVisitor(List<String> out) {
        return n -> {
            out.add(n.getNodeValue());
        };
    }

    private static Consumer<Node> replaceVisitor(String value) {
        return n -> {
            n.setNodeValue(value);
        };
    }

    private static String asString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Document xpath_(InputSource src, String xpath, Consumer<Node> visitor) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
            XPathEvaluationResult<?> result = XPathFactory.newInstance().newXPath()
                    .evaluateExpression(xpath, doc);
            switch (result.type()) {
            case NODE: {
                Node n = (Node)result.value();
                visitor.accept(n);
                break;
            }
            case NODESET: {
                XPathNodes l = (XPathNodes) result.value();
                l.forEach(n -> {
                    parseNodeList(n.getChildNodes(), visitor);
                });
                break;
            }}
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}