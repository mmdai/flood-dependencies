package cn.flood.i18n;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocaleParser {

    private static final Pattern KEY_WORD_PATTERN = Pattern.compile("\\{&.*?}");
    private static final String ZH_CN_1 = "zh-CN";
    private static final String ZH_CN_2 = "zh_CN";
    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleParser.class);

    @Autowired
    private MessageSource messageSource;

    public LocaleParser(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    public String replacePlaceHolderByLocale(String message, String localeStr) {
        Locale locale;
        try {
            locale = LocaleUtils.toLocale(localeStr);
        } catch (Exception e) {
            LOGGER.warn("Failed to get locale: {}, set according to actual parameters", localeStr.replace("\r", "").replace("\n", ""));
            if (ZH_CN_1.equals(localeStr) || ZH_CN_2.equals(localeStr)) {
                locale = Locale.CHINA;
            } else {
                locale = Locale.US;
            }
        }
        if (StringUtils.isBlank(localeStr)) {
            LOGGER.warn("Failed to get locale: {}, set to default en_US", localeStr.replace("\r", "").replace("\n", ""));
            locale = Locale.US;
        }
        if (!locale.equals(Locale.US) && !locale.equals(Locale.CHINA)) {
            LOGGER.warn("Does not support locale: {}, set to default en_US", localeStr.replace("\r", "").replace("\n", ""));
            locale = Locale.US;
        }
        Matcher m = KEY_WORD_PATTERN.matcher(message);
        try {
            while (m.find()) {
                String replaceStr = m.group();
                String tmp = messageSource.getMessage(replaceStr, null, locale);
                message = message.replace(replaceStr, tmp);
            }
        } catch (Exception e) {
            LOGGER.warn("Can not replace message: {}, caused by: {}", message, e.getMessage(), e);
        }
        return message;
    }
}
