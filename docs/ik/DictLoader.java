package org.wltea.analyzer.dic;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.core.PathUtils;
import org.wltea.analyzer.help.ESPluginLoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 单例的
 * 加载MySQL中的词库内容
 *
 * @author daimm
 */
public class DictLoader {

    private static final Logger LOGGER = ESPluginLoggerFactory.getLogger(DictLoader.class.getName());

    private static final DictLoader INSTANCE = new DictLoader();

    private final String url;
    private final String username;
    private final String password;

    private final AtomicBoolean extensionWordFistLoad = new AtomicBoolean(false);
    private final AtomicReference<String> extensionWordLastLoadTimeRef = new AtomicReference<>(null);
    private final AtomicBoolean stopWordFistLoad = new AtomicBoolean(false);
    private final AtomicReference<String> stopWordLastLoadTimeRef = new AtomicReference<>(null);

    private DictLoader() {
        Properties mysqlConfig = new Properties();
        Path configPath = PathUtils.get(Dictionary.getSingleton().getDictRoot(), "jdbc.properties");
        try {
            mysqlConfig.load(new FileInputStream(configPath.toFile()));
            this.url = mysqlConfig.getProperty("jdbc.url");
            this.username = mysqlConfig.getProperty("jdbc.username");
            this.password = mysqlConfig.getProperty("jdbc.password");
        } catch (IOException e) {
            throw new IllegalStateException("加载jdbc.properties配置文件发生异常");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("加载数据库驱动时发生异常");
        }
    }

    public static DictLoader getInstance() {
        return INSTANCE;
    }

    public void loadMysqlExtensionWords() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql;

        if (extensionWordFistLoad.compareAndSet(false, true)) {
            // 首次加载全量的词
            sql = "SELECT word FROM extension_word";
        } else {
            // 后面按照修改时间只加载增量的词
            sql = "SELECT word FROM extension_word WHERE update_time >= '" + extensionWordLastLoadTimeRef.get() + "'";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowString = dateFormat.format(new Date());
        extensionWordLastLoadTimeRef.set(nowString);

        // 加载扩展词词库内容
        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            LOGGER.info("从mysql加载extensionWord, sql={}", sql);

            Set<String> extensionWords = new HashSet<>();
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                if (word != null) {
                    extensionWords.add(word);
                    // TODO 这里为了方便看日志，把加载到的扩展词全都打印出来了
                    LOGGER.info("从mysql加载extensionWord, word={}", word);
                }
            }

            // 放到字典里
            Dictionary.getSingleton().addWords(extensionWords);
        } catch (Exception e) {
            LOGGER.error("从mysql加载extensionWord发生异常", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    public void loadMysqlStopWords() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql;
        if (stopWordFistLoad.compareAndSet(false, true)) {
            sql = "SELECT word FROM stop_word";
        } else {
            sql = "SELECT word FROM stop_word WHERE update_time >= '" + stopWordLastLoadTimeRef.get() + "'";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowString = dateFormat.format(new Date());
        stopWordLastLoadTimeRef.set(nowString);

        // 加载词库内容
        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            LOGGER.info("从mysql加载stopWord, sql={}", sql);

            Set<String> stopWords = new HashSet<>();
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                if (word != null) {
                    stopWords.add(word);
                    LOGGER.info("从mysql加载stopWord，word={}", word);
                }
            }
            // 放到字典里
            Dictionary.getSingleton().addStopWords(stopWords);
        } catch (Exception e) {
            LOGGER.error("从mysql加载extensionWord发生异常", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
    }
}
