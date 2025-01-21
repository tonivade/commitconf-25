package guess;

sealed interface Console<T> extends Program<T> {

  record WriteLine(String line) implements Console<Void> {}
  record ReadLine() implements Console<String> {}

  static Console<Void> writeLine(String line) {
    return new WriteLine(line);
  }

  static Program<String> readLine() {
    return new ReadLine();
  }

  @Override
  @SuppressWarnings({ "preview", "unchecked" })
  default T eval() {
    return (T) switch (this) {
      case WriteLine(var line) -> {
        System.console().println(line);
        yield null;
      }
      case ReadLine _ -> System.console().readLine();
    };
  }
}
