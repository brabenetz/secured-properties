/*-
 * #%L
 * Secured Properties
 * ===============================================================
 * Copyright (C) 2016 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.brabenetz.lib.securedproperties.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to extract versions from plugins and dependencies into the properties-section of a pom.xml.
 */
public class ExtractPomVersions {

    /**
     * Just run this class as application.
     */
    public static void main(final String[] args) throws Exception {
        new ExtractPomVersions().extractVersionsIntoProperties();
    }

    private void extractVersionsIntoProperties() throws ParserConfigurationException, SAXException, IOException, Exception {
        File pomFile = new File("pom.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(pomFile);

        Map<String, String> extractedProperties = new HashMap<>();

        extractedProperties.putAll(extractProperties(doc.getElementsByTagName("plugin"), "plugin-"));
        extractedProperties.putAll(extractProperties(doc.getElementsByTagName("dependency"), "dependency-"));
        addProperties(doc, extractedProperties);

        writeDocument(doc, pomFile);
        System.out.println("Success updated pom.xml");
    }

    private void addProperties(final Document doc, final Map<String, String> extractedProperties) {
        List<String> properties = new ArrayList<>(extractedProperties.keySet());
        Collections.sort(properties);
        for (String property : properties) {
            List<Node> propertiesNode = getChildElementsByTagName(doc.getDocumentElement(), "properties");
            Element propertyElement = doc.createElement(property);
            propertyElement.setTextContent(extractedProperties.get(property));
            propertiesNode.get(0).appendChild(propertyElement);
        }
    }

    private void writeDocument(final Document doc, final File file) throws Exception {
        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private Map<String, String> extractProperties(final NodeList pluginList, final String propertyPrefix) {
        Map<String, String> extractedProperties = new HashMap<>();

        for (int i = 0; i < pluginList.getLength(); i++) {

            Node pluginNode = pluginList.item(i);

            if (pluginNode.getNodeType() == Node.ELEMENT_NODE) {

                Element pluginElement = (Element) pluginNode;

                // System.out.println("groupId : " + getTextContent(pluginElement, "groupId"));
                // System.out.println("artifactId : " + getTextContent(pluginElement, "artifactId"));
                // System.out.println("version : " + getTextContent(pluginElement, "version"));
                String artifactId = getTextContent(pluginElement, "artifactId");
                String version = getTextContent(pluginElement, "version");
                if (StringUtils.isNotEmpty(version) && !StringUtils.startsWith(version, "${")) {
                    String propertyName = propertyPrefix + artifactId + ".version";
                    setTextContent(pluginElement, "version", "${" + propertyName + "}");
                    extractedProperties.put(propertyName, version);
                }

            }
        }
        return extractedProperties;
    }

    private String getTextContent(final Element element, final String tagName) {
        List<Node> elements = getChildElementsByTagName(element, tagName);
        if (elements.size() > 1) {
            throw new IllegalArgumentException(
                    "The element '" + tagName + "' was found more than ones (" + elements.size() + "). " + element.getTextContent());
        }
        if (elements.size() == 1) {
            return elements.get(0).getTextContent();
        }
        return null;
    }

    private void setTextContent(final Element element, final String tagName, final String newValue) {
        List<Node> elements = getChildElementsByTagName(element, tagName);
        Validate.isTrue(elements.size() == 1, "Expected one element for '%s' but got %s", tagName, elements.size());
        elements.get(0).setTextContent(newValue);
    }

    private List<Node> getChildElementsByTagName(final Element element, final String tagName) {
        List<Node> childNodesByTagName = new ArrayList<>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {

            Node childNode = childNodes.item(i);
            if (tagName.equals(childNode.getNodeName())) {
                childNodesByTagName.add(childNode);
            }
        }

        return childNodesByTagName;
    }
}
