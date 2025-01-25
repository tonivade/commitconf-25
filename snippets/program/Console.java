package program;

sealed interface Console<T> extends Program.Dsl<Console.Service, T> {

  interface Service {
    void writeLine(String line);
    String readLine();
  }

  record WriteLine(String line) implements Console<Void> {}
  record ReadLine() implements Console<String> {}

  @SuppressWarnings("unchecked")
  static <S extends Service> Program<S, Void> writeLine(String line) {
    return (Program<S, Void>) new WriteLine(line);
  }

  @SuppressWarnings("unchecked")
  static <S extends Service> Program<S, String> readLine() {
    return (Program<S, String>) new ReadLine();
  }

  static <S extends Service> Program<S, String> prompt(String question) {
    Program<S, Void> writeLine = writeLine(question);
    return writeLine.andThen(readLine());
  }

  static <S extends Service> Program<S, Void> sayHello(String name) {
    return writeLine("Hello " + name);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T eval(Service service) {
    return (T) switch (this) {
      case WriteLine(var line) -> {
        service.writeLine(line);
        yield null;
      }
      case ReadLine _ -> service.readLine();
    };
  }

  static Program<Service, String> whatsYourName() {
    return prompt("What's your name?");
  }

  static void main(String... args) {
    var program = whatsYourName().andThen(Console::sayHello);

    program.eval(new DefaultService() {});
  }

  interface DefaultService extends Console.Service {

    @SuppressWarnings("preview")
    @Override
    default void writeLine(String line) {
      System.console().println(line);
    }

    @Override
    default String readLine() {
      return System.console().readLine();
    }
  }
}
