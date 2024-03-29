package cn.flood.db.mybatis.plus;

import cn.flood.db.mybatis.xml.XMLConfigBuilder;
import java.io.InputStream;
import java.util.Properties;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.parsing.XPathParser;

/**
 * config xml 文件构建 config对象
 *
 * @author mmdai
 */
public class MybatisXMLConfigBuilder extends XMLConfigBuilder {

  public MybatisXMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
    this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment,
        props);
  }

  protected MybatisXMLConfigBuilder(XPathParser parser, String environment, Properties props) {
    super(new MybatisConfiguration());
    ErrorContext.instance().resource("SQL Mapper Configuration");
    this.configuration.setVariables(props);
    this.parsed = false;
    this.environment = environment;
    this.parser = parser;
  }

//    @Override
//    protected void mapperElement(XNode parent) throws Exception {
//        if (parent != null) {
//            for (XNode child : parent.getChildren()) {
//                if ("package".equals(child.getName())) {
//                    String mapperPackage = child.getStringAttribute("name");
//                    configuration.addMappers(mapperPackage);
//                } else {
//                    String resource = child.getStringAttribute("resource");
//                    String url = child.getStringAttribute("url");
//                    String mapperClass = child.getStringAttribute("class");
//                    if (resource != null && url == null && mapperClass == null) {
//                        ErrorContext.instance().resource(resource);
//                        InputStream inputStream = Resources.getResourceAsStream(resource);
//                        // 这里拓展用自定义的方式加载 mapper
//                        MybatisXMLMapperBuilder mapperParser = new MybatisXMLMapperBuilder(inputStream, configuration, resource,
//                                configuration.getSqlFragments());
//                        mapperParser.parse();
//                    } else if (resource == null && url != null && mapperClass == null) {
//                        ErrorContext.instance().resource(url);
//                        InputStream inputStream = Resources.getUrlAsStream(url);
//                        // 这里拓展用自定义的方式加载 mapper
//                        MybatisXMLMapperBuilder mapperParser = new MybatisXMLMapperBuilder(inputStream, configuration, resource,
//                                configuration.getSqlFragments());
//                        mapperParser.parse();
//                    } else if (resource == null && url == null && mapperClass != null) {
//                        Class<?> mapperInterface = Resources.classForName(mapperClass);
//                        configuration.addMapper(mapperInterface);
//                    } else {
//                        throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
//                    }
//                }
//            }
//        }
//    }
}
