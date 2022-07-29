package cn.flood.banner;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.Formatter;
import java.util.Properties;

/**
 *
 * <p>
 * Created on 2017年6月19日 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */
public class AbastractBanner implements Banner {

    /**
     * A line separator.
     */
    public static final String LINE_SEPARATOR = "------------------------------------------------------------";

    private static Logger logger = LoggerFactory.getLogger(AbastractBanner.class);

    @Override
    public void printBanner(final Environment environment, final Class<?> sourceClass, final PrintStream out) {
        String additional = getTitle();
        String asciiArt = collectEnvironmentInfo();
        if (additional != null && !additional.equals("")) {
            out.println(additional);
        }
        if (asciiArt != null &&  !asciiArt.equals("")) {
            out.print(asciiArt);
        }
    }

    protected String getTitle() {
        return null;
    }

    /**
     * Collect environment info with
     * details on the java and os deployment
     * versions.
     *
     * @return environment info
     */
    private String collectEnvironmentInfo() {
        final Properties properties = System.getProperties();

        try {
            Formatter formatter = new Formatter();
			formatter.format("Flood Framework Version: %s%n", SpringBootVersion.getVersion());
			formatter.format("%s%n", LINE_SEPARATOR);

			formatter.format("Java Home: %s%n", properties.get("java.home"));
			formatter.format("Java Vendor: %s%n", properties.get("java.vendor"));
			formatter.format("Java Version: %s%n", properties.get("java.version"));
			final Runtime runtime = Runtime.getRuntime();
			formatter.format("JVM Free Memory: %s%n", FileUtils.byteCountToDisplaySize(runtime.freeMemory()));
			formatter.format("JVM Maximum Memory: %s%n", FileUtils.byteCountToDisplaySize(runtime.maxMemory()));
			formatter.format("JVM Total Memory: %s%n", FileUtils.byteCountToDisplaySize(runtime.totalMemory()));
			formatter.format("%s%n", LINE_SEPARATOR);

			formatter.format("OS Architecture: %s%n", properties.get("os.arch"));
			formatter.format("OS Name: %s%n", properties.get("os.name"));
			formatter.format("OS Version: %s%n", properties.get("os.version"));
			formatter.format("OS Temp Directory: %s%n", FileUtils.getTempDirectoryPath());

			formatter.format("%s%n", LINE_SEPARATOR);

            return formatter.toString();
		} catch (Exception e) {
			logger.error("{}", e.getLocalizedMessage());
            return "Flood Framework Version: " + SpringBootVersion.getVersion();
		}
    }

}
