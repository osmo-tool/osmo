package osmo.miner.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import osmo.miner.model.Node;

public class HierarchyHandler extends DefaultHandler2 {
  private Node root = new Node(null, "root");
  private Node current = null;

  public HierarchyHandler() {
    current = root;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {

  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    current = current.addChild(qName);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    current = current.getParent();
  }

  @Override
  public String toString() {
    // System.out.println("root:" + current);
    StringBuilder sb = new StringBuilder();
    sb.append(current.getName());
    appendChildren(current, sb, 1);
    return sb.toString();
  }

  private void appendChildren(Node node, StringBuilder sb, int depth) {
    for (Node child : node.getChildren()) {
      sb.append("\n");
      for (int i = 0; i < depth; i++) {
        sb.append("--");
      }
      // System.out.println("Appending:" + child);
      sb.append(child.getName());
      appendChildren(child, sb, depth + 1);
    }
  }

  public Node getRoot() {
    return root;
  }
}
