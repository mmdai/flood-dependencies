package cn.flood.base.core.enums.base;

/**
 *
 */
public interface CommonEnum extends CodeBasedEnum, SelfDescribedEnum {
    default boolean match(String value){
        if (value == null){
            return false;
        }
        return value.equals(String.valueOf(getCode()))
                || value.equals(getName());
    }
}
