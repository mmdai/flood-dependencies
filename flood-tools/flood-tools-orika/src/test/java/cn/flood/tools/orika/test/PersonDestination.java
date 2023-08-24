package cn.flood.tools.orika.test;

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

  private String birthDayFormat;

  /**
   * The age.
   */
  private int age;

  @Override
  public String toString() {
    return "PersonDestination{" +
            "givenName='" + givenName + '\'' +
            ", sirName='" + sirName + '\'' +
            ", birthDayFormat='" + birthDayFormat + '\'' +
            ", age=" + age +
            '}';
  }
}
