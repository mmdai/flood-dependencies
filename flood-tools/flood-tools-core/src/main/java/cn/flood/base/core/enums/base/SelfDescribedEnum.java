package cn.flood.base.core.enums.base;

/**
 *
 */
public interface SelfDescribedEnum {
    default String getName(){
        return name();
    }

    String name();

    String getDescription();
}
