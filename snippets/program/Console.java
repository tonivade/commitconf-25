package program;

sealed interface Console<S extends Console.Service, T> extends Program.Dsl<S, T> {

  interface Service {
    void writeLine(String line);
    String readLine();
  }

  record WriteLine<S extends Service>(String line) implements Console<S, Void> {}
  record ReadLine<S extends Service>() implements Console<S, String> {}

  static <S extends Service> Console<S, Void> writeLine(String line) {
    return new WriteLine<>(line);
  }

  static <S extends Service> Console<S, String> readLine() {
    return new ReadLine<>();
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
  default T eval(S service) {
    return (T) switch (this) {
      case WriteLine<?>(var line) -> {
        service.writeLine(line);
        yield null;
      }
      case ReadLine<?> _ -> service.readLine();
    };
  }
}
