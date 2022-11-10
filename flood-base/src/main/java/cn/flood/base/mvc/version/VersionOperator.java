package cn.flood.base.mvc.version;

import java.util.stream.Stream;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/27 16:23
 */
public enum VersionOperator {

    LT("<"),
    GT(">"),
    LTE("<="),
    GTE(">="),
    NE("!="),
    EQ("==");
    private String code;
    VersionOperator(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VersionOperator parse(String code){
        return  Stream.of(VersionOperator.values()).filter(eu -> eu.code.equals(code)).
                findFirst().orElse(null);
    }

}
