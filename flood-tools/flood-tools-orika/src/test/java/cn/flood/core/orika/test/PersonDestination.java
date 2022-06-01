package cn.flood.core.orika.test;

import lombok.Data;

/**
 * The destination object of person data.
 */
@Data
public class PersonDestination {

    /**
     * The given name.
     */
    private String givenName;

    /**
     * The sir name.
     */
    private String sirName;

    /**
     * The age.
     */
    private int age;

}
