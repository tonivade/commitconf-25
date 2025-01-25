import static java.lang.System.console;

import java.util.List;

interface ResumeDsl {

  record Date(int month, int year) {}

  record Phone(String number) {}

  record Email(String email) {}

  record Contact(Phone phone, Email email) {}

  record Position(String name) {}

  record Location(String city, String country) {}

  record Institution(String name) {}

  record Company(String name) {}

  record Period(Date start, Date end) {
    public Period(Date start) {
      this(start, null);
    }
  }

  sealed interface Education {
    record HighSchool(String title, Institution institution, Location location, Period period) implements Education {}

    record College(String title, Institution institution, Location location, Period period) implements Education {}

    record Master(String title, Institution institution, Location location, Period period) implements Education {}

    record Phd(String title, Institution institution, Location location, Period period) implements Education {}
  }

  sealed interface Experience {
    record Internship(String position, Company company, Location location, Period period) implements Experience {}

    record Work(String position, Company company, Location location, Period period) implements Experience {}
  }

  record Resume(
      String name,
      Date dateOfBirth,
      Position title,
      Location location,
      Contact contact,
      List<Education> education,
      List<Experience> experience) {
  }

  @SuppressWarnings("preview")
  static void main() {

    var me = new Resume(
        "Antonio Muñoz",
        new Date(11, 1977),
        new Position("Backend Engineer"),
        new Location("Madrid", "Spain"),
        new Contact(new Phone("123456789"), new Email("me@tonivade.es")),
        List.of(
            new Education.College(
                "Electronics Technician",
                new Institution("IFP Inglan"),
                new Location("Getafe", "Spain"),
                new Period(new Date(9, 1991), new Date(6, 1996))),
            new Education.College(
                "B.Eng in Telecommuncations",
                new Institution("UPM"),
                new Location("Madrid", "Spain"),
                new Period(new Date(9, 1996), new Date(6, 2001)))),
        List.of(
            new Experience.Internship(
                "Java Trainee",
                new Company("Siatcom"), new Location("Madrid", "Spain"),
                new Period(new Date(9, 2000), new Date(9, 2001))),
            new Experience.Work(
                "Java Developer",
                new Company("Vector Software Factory"),
                new Location("Madrid", "Spain"),
                new Period(new Date(7, 2002), new Date(7, 2005))),
            new Experience.Work(
                "Java Consultant",
                new Company("Novanotio"),
                new Location("Madrid", "Spain"),
                new Period(new Date(7, 2005), new Date(12, 2006))),
            new Experience.Work(
                "Senior Java Engineer",
                new Company("Indra"),
                new Location("Madrid", "Spain"),
                new Period(new Date(12, 2006), new Date(7, 2015))),
            new Experience.Work(
                "Senior Software Engineer",
                new Company("lastminute.com"),
                new Location("Madrid", "Spain"),
                new Period(new Date(7, 2015), new Date(12, 2019))),
            new Experience.Work(
                "Senior Backend Engineer",
                new Company("clarity.ai"),
                new Location("Madrid", "Spain"),
                new Period(new Date(12, 2019)))));

    console().println(me);
  }
}
