package cn.flood.tools.orika.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    List<PersonSource> list = new ArrayList<>();
    PersonSource source = new PersonSource();
    source.setFirstName("John");
    source.setLastName("Smith");
    source.setAge(23);
    source.setBirthDay(new Date());
    list.add(source);
    System.out.println(source);  // => "PersonSource(firstName=John, lastName=Smith, age=23)"
    PersonDestination destination = orikaMapperFacade.map(source, PersonDestination.class);
    System.out
        .println(destination);  // => "PersonDestination(givenName=John, sirName=Smith, age=23)"

    PersonSource source1 = new PersonSource();
    source1.setFirstName("Dai");
    source1.setLastName("Mingming");
    source1.setAge(200);
    source1.setBirthDay(new Date());
    list.add(source1);
    List<PersonDestination> destinationList = orikaMapperFacade.mapAsList(list, PersonDestination.class);
    System.out.println(destinationList.toArray());
    destinationList.stream().forEach(s->System.out.println(s.toString()));
  }

}
