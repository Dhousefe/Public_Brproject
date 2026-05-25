/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.data.xml;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.logging.CLogger;

import ext.mods.Config;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.model.location.SpawnLocation;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public interface IXmlReader
{
	CLogger LOGGER = new CLogger(IXmlReader.class.getName());
	
	String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	void load();
	
	void parseDocument(Document doc, Path path);
	
	default void parseDataFile(String file)
	{
		parseFile(Config.DATA_PATH.resolve(file), false, true, true);
	}
	
	default void parseFile(String path)
	{
		parseFile(Paths.get(path), false, true, true);
	}
	
	default StatSet parseStatSet(Node node)
	{
		StatSet set = new StatSet();
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
			set.set(attrs.item(i).getNodeName(), attrs.item(i).getNodeValue());
		return set;
	}
	
	default void parseFile(Path path, boolean validate, boolean ignoreComments, boolean ignoreWhitespaces)
	{
		if (Files.isDirectory(path))
		{
			final List<Path> pathsToParse = new LinkedList<>();
			try
			{
				Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>()
				{
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					{
						pathsToParse.add(file);
						return FileVisitResult.CONTINUE;
					}
				});
				
				pathsToParse.forEach(p -> parseFile(p, validate, ignoreComments, ignoreWhitespaces));
			}
			catch (IOException e)
			{
				LOGGER.warn("Could not parse directory: {} ", e, path);
			}
		}
		else
		{
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			dbf.setNamespaceAware(true);
			dbf.setValidating(validate);
			dbf.setIgnoringComments(ignoreComments);
			dbf.setIgnoringElementContentWhitespace(ignoreWhitespaces);
			dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			
			try
			{
				final DocumentBuilder db = dbf.newDocumentBuilder();
				db.setErrorHandler(new XMLErrorHandler());
				parseDocument(db.parse(path.toAbsolutePath().toFile()), path);
			}
			catch (SAXParseException e)
			{
				LOGGER.warn("Could not parse file: {} at line: {}, column: {} :", e, path, e.getLineNumber(), e.getColumnNumber());
			}
			catch (ParserConfigurationException | SAXException | IOException e)
			{
				LOGGER.warn("Could not parse file: {} ", e, path);
			}
		}
	}
	
	default Boolean parseBoolean(Node node, Boolean defaultValue)
	{
		return node != null ? Boolean.valueOf(node.getNodeValue()) : defaultValue;
	}
	
	default Boolean parseBoolean(Node node)
	{
		return parseBoolean(node, null);
	}
	
	default Boolean parseBoolean(NamedNodeMap attrs, String name)
	{
		return parseBoolean(attrs.getNamedItem(name));
	}
	
	default Boolean parseBoolean(NamedNodeMap attrs, String name, Boolean defaultValue)
	{
		return parseBoolean(attrs.getNamedItem(name), defaultValue);
	}
	
	default Byte parseByte(Node node, Byte defaultValue)
	{
		return node != null ? Byte.decode(node.getNodeValue()) : defaultValue;
	}
	
	default Byte parseByte(Node node)
	{
		return parseByte(node, null);
	}
	
	default Byte parseByte(NamedNodeMap attrs, String name)
	{
		return parseByte(attrs.getNamedItem(name));
	}
	
	default Byte parseByte(NamedNodeMap attrs, String name, Byte defaultValue)
	{
		return parseByte(attrs.getNamedItem(name), defaultValue);
	}
	
	default Short parseShort(Node node, Short defaultValue)
	{
		return node != null ? Short.decode(node.getNodeValue()) : defaultValue;
	}
	
	default Short parseShort(Node node)
	{
		return parseShort(node, null);
	}
	
	default Short parseShort(NamedNodeMap attrs, String name)
	{
		return parseShort(attrs.getNamedItem(name));
	}
	
	default Short parseShort(NamedNodeMap attrs, String name, Short defaultValue)
	{
		return parseShort(attrs.getNamedItem(name), defaultValue);
	}
	
	default int parseInt(Node node, Integer defaultValue)
	{
		return node != null ? Integer.decode(node.getNodeValue()) : defaultValue;
	}
	
	default int parseInt(Node node)
	{
		return parseInt(node, -1);
	}
	
	default Integer parseInteger(Node node, Integer defaultValue)
	{
		return node != null ? Integer.decode(node.getNodeValue()) : defaultValue;
	}
	
	default Integer parseInteger(Node node)
	{
		return parseInteger(node, null);
	}
	
	default Integer parseInteger(NamedNodeMap attrs, String name)
	{
		return parseInteger(attrs.getNamedItem(name));
	}
	
	default Integer parseInteger(Node node, String name, Integer defaultValue)
	{
		return parseInteger(node.getAttributes(), name, defaultValue);
	}
	
	default int parseInt(Node node, String name, int defaultValue)
	{
		Integer result = parseInteger(node.getAttributes(), name, defaultValue);
		return result != null ? result : defaultValue;
	}
	
	default Integer parseInteger(NamedNodeMap attrs, String name, Integer defaultValue)
	{
		return parseInteger(attrs.getNamedItem(name), defaultValue);
	}
	
	default Long parseLong(Node node, Long defaultValue)
	{
		return node != null ? Long.decode(node.getNodeValue()) : defaultValue;
	}
	
	default Long parseLong(Node node)
	{
		return parseLong(node, null);
	}
	
	default Long parseLong(NamedNodeMap attrs, String name)
	{
		return parseLong(attrs.getNamedItem(name));
	}
	
	default Long parseLong(NamedNodeMap attrs, String name, Long defaultValue)
	{
		return parseLong(attrs.getNamedItem(name), defaultValue);
	}
	
	default Float parseFloat(Node node, Float defaultValue)
	{
		return node != null ? Float.valueOf(node.getNodeValue()) : defaultValue;
	}
	
	default Float parseFloat(Node node)
	{
		return parseFloat(node, null);
	}
	
	default Float parseFloat(NamedNodeMap attrs, String name)
	{
		return parseFloat(attrs.getNamedItem(name));
	}
	
	default Float parseFloat(NamedNodeMap attrs, String name, Float defaultValue)
	{
		return parseFloat(attrs.getNamedItem(name), defaultValue);
	}
	
	default Double parseDouble(Node node, Double defaultValue)
	{
		return node != null ? Double.valueOf(node.getNodeValue()) : defaultValue;
	}
	
	default Double parseDouble(Node node)
	{
		return parseDouble(node, null);
	}
	
	default Double parseDouble(NamedNodeMap attrs, String name)
	{
		return parseDouble(attrs.getNamedItem(name));
	}
	
	default Double parseDouble(NamedNodeMap attrs, String name, Double defaultValue)
	{
		return parseDouble(attrs.getNamedItem(name), defaultValue);
	}
	
	default String parseString(Node node, String defaultValue)
	{
		return node != null ? node.getNodeValue() : defaultValue;
	}
	
	default String parseString(Node node)
	{
		return parseString(node, null);
	}
	
	default String parseString(NamedNodeMap attrs, String name)
	{
		return parseString(attrs.getNamedItem(name));
	}
	
	default String parseString(NamedNodeMap attrs, String name, String defaultValue)
	{
		return parseString(attrs.getNamedItem(name), defaultValue);
	}
	
	default <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz, T defaultValue)
	{
		if (node == null)
		{
			return defaultValue;
		}
		
		try
		{
			return Enum.valueOf(clazz, node.getNodeValue());
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.warn("Invalid value specified for node: {} specified value: {} should be enum value of \"{}\" using default value: {}", node.getNodeName(), node.getNodeValue(), clazz.getSimpleName(), defaultValue);
			return defaultValue;
		}
	}
	
	default <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz)
	{
		return parseEnum(node, clazz, null);
	}
	
	default <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name)
	{
		return parseEnum(attrs.getNamedItem(name), clazz);
	}
	
	default <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name, T defaultValue)
	{
		return parseEnum(attrs.getNamedItem(name), clazz, defaultValue);
	}
	
	default Point2D parsePoint2D(Node node)
	{
		if (node == null)
			return null;
		
		final NamedNodeMap nodeAttrs = node.getAttributes();
		if (nodeAttrs == null)
			return null;
		
		return new Point2D(parseInteger(nodeAttrs, "x"), parseInteger(nodeAttrs, "y"));
	}
	
	default Location parseLocation(NamedNodeMap attrs, String name)
	{
		final Node node = attrs.getNamedItem(name);
		if (node == null)
			return null;
		
		final int[] coords = Arrays.stream(node.getNodeValue().split(";")).mapToInt(Integer::parseInt).toArray();
		return new Location(coords[0], coords[1], coords[2]);
	}
	
	default SpawnLocation parseSpawnLocation(NamedNodeMap attrs, String name)
	{
		final Node node = attrs.getNamedItem(name);
		if (node == null)
			return null;
		
		final int[] coords = Arrays.stream(node.getNodeValue().split(";")).mapToInt(Integer::parseInt).toArray();
		return new SpawnLocation(coords[0], coords[1], coords[2], coords[3]);
	}
	
	default IntIntHolder parseIntIntHolder(NamedNodeMap attrs, String name)
	{
		final Node node = attrs.getNamedItem(name);
		if (node == null)
			return null;
		
		final int[] values = Arrays.stream(node.getNodeValue().split(";")).mapToInt(Integer::parseInt).toArray();
		return new IntIntHolder(values[0], values[1]);
	}
	
	default StatSet parseAttributes(Node node)
	{
		final NamedNodeMap attrs = node.getAttributes();
		final StatSet map = new StatSet();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node att = attrs.item(i);
			map.put(att.getNodeName(), att.getNodeValue());
		}
		return map;
	}
	
	default void addAttributes(StatSet set, NamedNodeMap attrs)
	{
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node att = attrs.item(i);
			set.put(att.getNodeName(), att.getNodeValue());
		}
	}
	
	default Map<String, Object> parseParameters(Node n)
	{
		final Map<String, Object> parameters = new HashMap<>();
		for (Node parameters_node = n.getFirstChild(); parameters_node != null; parameters_node = parameters_node.getNextSibling())
		{
			NamedNodeMap attrs = parameters_node.getAttributes();
			switch (parameters_node.getNodeName().toLowerCase())
			{
				case "param":
					parameters.put(parseString(attrs, "name"), parseString(attrs, "value"));
					break;
				
				case "skill":
					parameters.put(parseString(attrs, "name"), new IntIntHolder(parseInteger(attrs, "id"), parseInteger(attrs, "level")));
					break;
				
				case "location":
					parameters.put(parseString(attrs, "name"), new SpawnLocation(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z"), parseInteger(attrs, "heading", 0)));
					break;
			}
		}
		return parameters;
	}
	
	default Location parseLocation(Node n)
	{
		final NamedNodeMap attrs = n.getAttributes();
		final int x = parseInteger(attrs, "x");
		final int y = parseInteger(attrs, "y");
		final int z = parseInteger(attrs, "z");
		
		return new Location(x, y, z);
	}
	
	default SpawnLocation parseSpawnLocation(Node n)
	{
		final NamedNodeMap attrs = n.getAttributes();
		final int x = parseInteger(attrs, "x");
		final int y = parseInteger(attrs, "y");
		final int z = parseInteger(attrs, "z");
		final int heading = parseInteger(attrs, "heading", 0);
		
		return new SpawnLocation(x, y, z, heading);
	}
	
	default void forEach(Node node, Consumer<Node> action)
	{
		forEach(node, a -> true, action);
	}
	
	default void forEach(Node node, String nodeName, Consumer<Node> action)
	{
		forEach(node, innerNode ->
		{
			if (nodeName.contains("|"))
			{
				final String[] nodeNames = nodeName.split("\\|");
				for (String name : nodeNames)
				{
					if (!name.isEmpty() && name.equals(innerNode.getNodeName()))
						return true;
				}
				return false;
			}
			return nodeName.equals(innerNode.getNodeName());
		}, action);
	}
	
	default void forEach(Node node, Predicate<Node> filter, Consumer<Node> action)
	{
		final NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node targetNode = list.item(i);
			if (filter.test(targetNode))
				action.accept(targetNode);
		}
	}
	
	public static boolean isNode(Node node)
	{
		return node.getNodeType() == Node.ELEMENT_NODE;
	}
	
	public static boolean isText(Node node)
	{
		return node.getNodeType() == Node.TEXT_NODE;
	}
	
	class XMLErrorHandler implements ErrorHandler
	{
		@Override
		public void warning(SAXParseException e) throws SAXParseException
		{
			throw e;
		}
		
		@Override
		public void error(SAXParseException e) throws SAXParseException
		{
			throw e;
		}
		
		@Override
		public void fatalError(SAXParseException e) throws SAXParseException
		{
			throw e;
		}
	}
}