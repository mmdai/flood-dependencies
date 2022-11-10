package cn.flood.tools.orika.test;

import lombok.Data;

import java.util.Date;

/**
 * The source object of person data.
 */
@Data
public class PersonSource {

    /**
     * The first name.
     */
    private String firstName;

    /**
     * The last name.
     */
    private String lastName;

    private Date birthDay;

    /**
     * The age.
     */
    private int age;

}
