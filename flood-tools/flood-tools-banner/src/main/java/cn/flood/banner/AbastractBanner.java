package cn.flood.banner;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.Formatter;
import java.util.Properties;

//import java.time.LocalDateTime;


/**
 * 
 *  TODO banner config
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
        String asciiArt = collectEnvironmentInfo(environment, sourceClass);
        if (additional != null && additional != "") {
            out.println(additional);
        }
        if (asciiArt != null && asciiArt != "") {
            out.print(asciiArt);
        }
    }

    protected String getTitle() {
        return "(Flood Framework)";
    }

    /**
     * Collect environment info with
     * details on the java and os deployment
     * versions.
     *
     * @param environment the environment
     * @param sourceClass the source class
     * @return environment info
     */
    private String collectEnvironmentInfo(final Environment environment, final Class<?> sourceClass) {
        final Properties properties = System.getProperties();
        
        try {
			Formatter formatter = new Formatter();
			formatter.format("Flood Framework Version: %s%n", "2.3.9");
			formatter.format("%s%n", LINE_SEPARATOR);

			formatter.format("Java Home: %s%n", properties.get("java.home"));
			formatter.format("Java Vendor: %s%n", properties.get("java.vendor"));
			formatter.format("Java Version: %s%n", properties.get("java.version"));
			final Runtime runtime = Runtime.getRuntime();
			formatter.format("JVM Free Memory: %s%n", FileUtils.byteCountToDisplaySize(runtime.freeMemory()));
			formatter.format("JVM Maximum Memory: %s%n", FileUtils.byteCountToDisplaySize(runtime.maxMemory()));
			formatter.format("JVM Total Memory: %s%n", FileUtils.byteCountToDisplaySize(runtime.totalMemory()));
//			formatter.format("JCE Installed: %s%n", StringUtils.capitalize(BooleanUtils.toStringYesNo(isJceInstalled())));
			formatter.format("%s%n", LINE_SEPARATOR);

			formatter.format("OS Architecture: %s%n", properties.get("os.arch"));
			formatter.format("OS Name: %s%n", properties.get("os.name"));
			formatter.format("OS Version: %s%n", properties.get("os.version"));
//			formatter.format("OS Date/Time: %s%n", LocalDateTime.now());
			formatter.format("OS Temp Directory: %s%n", FileUtils.getTempDirectoryPath());

			formatter.format("%s%n", LINE_SEPARATOR);


			injectEnvironmentInfoIntoBanner(formatter, environment, sourceClass);

			return formatter.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("{}", e);
		}
        return "Flood Framework Version:  2.3.9";
    }


    /**
     * Inject environment info into banner.
     *
     * @param formatter   the formatter
     * @param environment the environment
     * @param sourceClass the source class
     */
    protected void injectEnvironmentInfoIntoBanner(final Formatter formatter,
                                                   final Environment environment,
                                                   final Class<?> sourceClass) {
    }

    /*private static boolean isJceInstalled() {
        try {
            final int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
            return maxKeyLen == Integer.MAX_VALUE;
        } catch (final Exception e) {
            return false;
        }
    }*/
}
