package cn.flood.base.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: DomParserUtil</p>
 * <p>Description: dom4j解析xml工具类</p>
 *
 * @author mmdai
 * @date 2018年10月25日
 */
public class XmlUtil {

  private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);

  /**
   * 获取指定节点的值
   *
   * @param dataStr
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static Map<String, Object> getNodeValue(String dataStr) {
    try {
      // 将字符串转为xml
      Document document = DocumentHelper.parseText(dataStr);
      Element root = document.getRootElement();//获取根结点
      Map<String, Object> map = new HashMap<String, Object>();
      for (Iterator i = root.elementIterator(); i.hasNext(); ) {
        Element employee = (Element) i.next();
        for (Iterator j = employee.elementIterator(); j.hasNext(); ) {
          Element node = (Element) j.next();
          map.put(node.getName(), node.getText());
        }
      }
      return map;
    } catch (DocumentException e) {
      logger.error("dom4j error:{}", e.getMessage());
      return null;
    }

  }

  /**
   * <p>Title: toMap</p>
   * <p>Description: 获取xml所有节点的数据</p>
   *
   * @param str
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static Map<String, Object> toMap(String str) {
    Document doc = null;
    try {
      doc = DocumentHelper.parseText(str);
    } catch (DocumentException e1) {
      logger.error("dom4j error:{}", e1.getMessage());
      return null;
    }
    Map<String, Object> map = new HashMap<String, Object>();
    if (doc == null) {
      return map;
    }
    Element root = doc.getRootElement();
    for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
      Element e = (Element) iterator.next();
      List list = e.elements();
      if (list.size() > 0) {
        map.put(e.getName(), dom2Map(e));
      } else {
        map.put(e.getName(), e.getText());
      }
    }
    return map;
  }

  /**
   * <p>Title: Dom2Map</p>
   * <p>Description: 递归解析xml</p>
   *
   * @param e
   * @return
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private static Map dom2Map(Element e) {
    Map map = new HashMap();
    List list = e.elements();
    if (list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        Element iter = (Element) list.get(i);
        List mapList = new ArrayList();
        if (iter.elements().size() > 0) {
          Map m = dom2Map(iter);
          if (map.get(iter.getName()) != null) {
            Object obj = map.get(iter.getName());
            if (!"java.util.ArrayList".equals(obj.getClass().getName())) {
              mapList = new ArrayList();
              mapList.add(obj);
              mapList.add(m);
            }
            if ("java.util.ArrayList".equals(obj.getClass().getName())) {
              mapList = (List) obj;
              mapList.add(m);
            }
            map.put(iter.getName(), mapList);
          } else {
            map.put(iter.getName(), m);
          }
        } else {
          if (map.get(iter.getName()) != null) {
            Object obj = map.get(iter.getName());
            if (!"java.util.ArrayList".equals(obj.getClass().getName())) {
              mapList = new ArrayList();
              mapList.add(obj);
              mapList.add(iter.getText());
            }
            if ("java.util.ArrayList".equals(obj.getClass().getName())) {
              mapList = (List) obj;
              mapList.add(iter.getText());
            }
            map.put(iter.getName(), mapList);
          } else {
            map.put(iter.getName(), iter.getText());
          }
        }
      }
    } else {
      map.put(e.getName(), e.getText());
    }
    return map;
  }

  public static void main(String[] args) {
    String temp2 = "<DescribeSnapshotsResponse xmlns=\"http://ec2.amazonaws.com/doc/2010-08-31/\">"
        + "<item>"
        + "<snapshotId>110110snapId</snapshotId>"
        + "<volumeId>33434volumeId</volumeId>"
        + "<status>started</status>"
        + "<startTime>2010-07-29T04:12:01.000Z</startTime>"
        + "<progress>10%</progress>"
        + "<ownerId>tianjun</ownerId>"
        + "<volumeSize>33</volumeSize>"
        + "<description>setDescription</description>"
        + "<ownerAlias>setOwnerAlias</ownerAlias>"
        + "<tagSet>"
        + "  <item>"
        + "    <key>setkey</key>"
        + "    <value>setValue</value>"
        + "  </item>"
        + "  <item>"
        + "    <key>tep2</key>"
        + "    <value>dddValue</value>"
        + "  </item>"
        + "  <item>"
        + "    <key>tep2</key>"
        + "    <value>dddValue</value>"
        + "  </item>"
        + "</tagSet>"
        + "</item>"
        + "<item>"
        + "<snapshotId>110110snapId</snapshotId>"
        + "<volumeId>33434volumeId</volumeId>"
        + "<status>started</status>"
        + "<startTime>2010-07-29T04:12:01.000Z</startTime>"
        + "<progress>10%</progress>"
        + "<ownerId>tianjun</ownerId>"
        + "<volumeSize>33</volumeSize>"
        + "<description></description>"
        + "<ownerAlias>setOwnerAlias</ownerAlias>"
        + "<tagSet>"
        + "  <item>"
        + "    <key>setkey</key>"
        + "    <value>setValue</value>"
        + "  </item>"
        + "  <item>"
        + "    <key>tep2</key>"
        + "    <value>dddValue</value>"
        + "  </item>"
        + "  <item>"
        + "    <key>tep2</key>"
        + "    <value>dddValue</value>"
        + "  </item>"
        + "</tagSet>"
        + "</item>" + "</DescribeSnapshotsResponse>";
    Map<String, Object> map = null;
    try {
      map = toMap(temp2);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(map);
  }

}
