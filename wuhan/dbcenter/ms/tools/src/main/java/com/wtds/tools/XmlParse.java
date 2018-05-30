package com.wtds.tools;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.*;

public class XmlParse {
	Document doc;
	public Element rootEle;

	public XmlParse(String fileName) {
		SAXBuilder sax = new SAXBuilder();
		try {
			// fileName="c:\\tableConfig.xml";
			doc = sax.build(fileName);
			rootEle = doc.getRootElement();
			// System.out.println("从文件 " + fileName + " 中获取配置信息。 ");
		} catch (JDOMException e) {
			e.printStackTrace();
			System.out.println("JDOM解析异常 ");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("文件获取异常 ");
		}
	}

	public Element findTableElement(String tableName) {
		List<Element> tableEles = this.rootEle.getChildren();
		for (Iterator it = tableEles.iterator(); it.hasNext();) {
			Element tableEle = (Element) it.next();
			if (tableName.equalsIgnoreCase(tableEle.getAttributeValue("name"))) {
				// System.out.println("获取表 " + tableName + "信息 。");
				return tableEle;
			}
		}
		System.out.println("配置文件中无表 " + tableName + "信息 。");
		return null;
	}

	public List<Element> getElements() {
		List<Element> elements = this.rootEle.getChildren();
		return elements;
	}
}
