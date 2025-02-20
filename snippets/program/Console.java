package program;

import static java.lang.System.console;

sealed interface Console<T> extends Program.Dsl<Console.Service, T> {

  interface Service {

    default void writeLine(String line) {
      console().println(line);
    }

    default String readLine() {
      return console().readLine();
    }
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

  @SuppressWarnings("unchecked")
  static <S extends Service> Program<S, String> prompt(String question) {
    return (Program<S, String>) writeLine(question).andThen(readLine());
  }

  static <S extends Service> Program<S, Void> sayHello(String name) {
    return writeLine("Hello " + name);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(Service service) {
    return (T) switch (this) {
      case WriteLine(var line) -> {
        service.writeLine(line);
        yield null;
      }
      case ReadLine _ -> service.readLine();
    };
  }

  static void main() {
    var program = prompt("What's your name?").flatMap(Console::sayHello);

    program.eval(new Service() {});
  }
}
