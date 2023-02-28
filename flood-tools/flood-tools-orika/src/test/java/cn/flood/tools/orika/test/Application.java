package cn.flood.tools.orika.test;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The sample application of "orika-spring-boot-starter". Maps {@link PersonSource} to {@link
 * PersonDestination}.
 */
@RequiredArgsConstructor
@SpringBootApplication
public class Application implements ApplicationRunner {

  /**
   * The Orika's mapper interface.
   */
  private final MapperFacade orikaMapperFacade;

  /**
   * The entry point of application.
   *
   * @param args unused.
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(ApplicationArguments args) {

    PersonSource source = new PersonSource();
    source.setFirstName("John");
    source.setLastName("Smith");
    source.setAge(23);
    source.setBirthDay(new Date());

    System.out.println(source);  // => "PersonSource(firstName=John, lastName=Smith, age=23)"
    PersonDestination destination = orikaMapperFacade.map(source, PersonDestination.class);
    System.out
        .println(destination);  // => "PersonDestination(givenName=John, sirName=Smith, age=23)"

  }

}
