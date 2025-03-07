package program;

sealed interface State<T> extends Program.Dsl<State.Service, T> {

  interface Service {
    void set(int value);
    int get();
  }

  record SetValue(int value) implements State<Void> {}
  record GetValue() implements State<Integer> {}

  @SuppressWarnings("unchecked")
  static <S extends Service> Program<S, Void> setValue(int value) {
    return (Program<S, Void>) new SetValue(value);
  }

  @SuppressWarnings("unchecked")
  static <S extends Service> Program<S, Integer> getValue() {
    return (Program<S, Integer>) new GetValue();
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(Service service) {
    return (T) switch (this) {
      case SetValue(int value) -> {
        service.set(value);
        yield null;
      }
      case GetValue() -> service.get();
    };
  }
}
