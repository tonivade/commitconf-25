package program;

import static java.util.stream.Collectors.joining;
import static program.Console.prompt;
import static program.Console.readLine;
import static program.Console.writeLine;
import static program.Program.map2;
import static program.Todo.State.COMPLETED;
import static program.Todo.State.NOT_COMPLETED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

sealed interface Todo<S extends Todo.Repository, T> extends Program.Dsl<S, T> {

  interface Repository {
    void create(TodoEntity todo);
    void update(int id, UnaryOperator<TodoEntity> update);
    Optional<TodoEntity> find(int id);
    List<TodoEntity> findAll();
    void delete(int id);
    void deleteAll();
  }

  enum State {
    NOT_COMPLETED, COMPLETED
  }

  record TodoEntity(int id, String title, State state) {
    TodoEntity withState(State state) {
      return new TodoEntity(id, title, state);
    }
  }

  record Create<S extends Repository>(TodoEntity todo) implements Todo<S, Void> {}
  record Update<S extends Repository>(int id, UnaryOperator<TodoEntity> update) implements Todo<S, Void> {}
  record FindOne<S extends Repository>(int id) implements Todo<S, Optional<TodoEntity>> {}
  record FindAll<S extends Repository>() implements Todo<S, List<TodoEntity>> {}
  record DeleteOne<S extends Repository>(int id) implements Todo<S, Void> {}
  record DeleteAll<S extends Repository>() implements Todo<S, Void> {}

  @Override
  @SuppressWarnings("unchecked")
  default T eval(S repository) {
    return (T) switch (this) {
      case Create<?>(var todo) -> {
        repository.create(todo);
        yield null;
      }
      case Update<?>(var id, var update) -> {
        repository.update(id, update);
        yield null;
      }
      case FindOne<?>(int id) -> repository.find(id);
      case FindAll<?> _ -> repository.findAll();
      case DeleteOne<?>(int id) -> {
        repository.delete(id);
        yield null;
      }
      case DeleteAll<?> _ -> {
        repository.deleteAll();
        yield null;
      }
    };
  }

  static Program<Context, Integer> printMenu() {
    return Console.<Context>writeLine("Menu")
      .andThen(writeLine("1. Create"))
      .andThen(writeLine("2. List"))
      .andThen(writeLine("3. Find"))
      .andThen(writeLine("4. Delete"))
      .andThen(writeLine("5. Clear"))
      .andThen(writeLine("6. Completed"))
      .andThen(writeLine("7. Exit"))
      .andThen(readLine())
      .map(Integer::parseInt);
  }

  static Program<Context, Void> executeAction(int action) {
    return switch (action) {
      case 1 -> createTodo();
      case 2 -> findAll();
      case 3 -> findTodo();
      case 4 -> deleteTodo();
      case 5 -> deleteAll();
      case 6 -> markCompleted();
      case 7 -> writeLine("Bye!");
      default -> throw new IllegalArgumentException();
    };
  }

  static Program<Context, String> promptTitle() {
    return prompt("Enter title");
  }

  static Program<Context, Integer> promptId() {
    return Console.<Context>prompt("Enter id").map(Integer::parseInt);
  }

  static Program<Context, Void> findAll() {
    return new FindAll<Context>()
      .map(list -> list.stream().map(Object::toString).collect(joining("\n")))
      .andThen(Console::writeLine)
      .andThen(loop());
  }

  static Program<Context, Void> deleteAll() {
    return new DeleteAll<Context>()
      .andThen(writeLine("all todo removed"))
      .andThen(loop());
  }

  static Program<Context, Void> createTodo() {
    return map2(promptId(), promptTitle(),
        (id, title) -> new TodoEntity(id, title, NOT_COMPLETED))
      .andThen(Create::new)
      .andThen(writeLine("todo created"))
      .andThen(loop());
  }

  static Program<Context, Void> deleteTodo() {
    return promptId()
      .andThen(DeleteOne::new)
      .andThen(writeLine("todo removed"))
      .andThen(loop());
  }

  static Program<Context, Void> findTodo() {
    return promptId()
      .andThen(FindOne::new)
      .map(optional -> optional.map(Object::toString).orElse("not found"))
      .andThen(Console::writeLine)
      .andThen(loop());
  }

  static Program<Context, Void> markCompleted() {
    return promptId()
      .andThen(id -> new Update<>(id, entity -> entity.withState(COMPLETED)))
      .andThen(writeLine("todo compmleted"))
      .andThen(loop());
  }

  static Program<Context, Void> loop() {
    return printMenu().andThen(Todo::executeAction);
  }

  static Program<Context, String> whatsYourName() {
    return prompt("What's your name?");
  }

  static void main(String... args) {
    var program = whatsYourName()
      .andThen(Console::sayHello)
      .andThen(loop());

    program.eval(new Context());
  }

  final class Context implements Todo.Repository, Console.Service {

    Map<Integer, TodoEntity> repository = new HashMap<>();

    @SuppressWarnings("preview")
    @Override
    public void writeLine(String line) {
      System.console().println(line);
    }

    @Override
    public String readLine() {
      return System.console().readLine();
    }

    @Override
    public void create(TodoEntity todo) {
      repository.put(todo.id(), todo);
    }

    @Override
    public void update(int id, UnaryOperator<TodoEntity> update) {
      var todo = repository.get(id);
      repository.put(id, update.apply(todo));
    }

    @Override
    public Optional<TodoEntity> find(int id) {
      return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<TodoEntity> findAll() {
      return List.copyOf(repository.values());
    }

    @Override
    public void delete(int id) {
      repository.remove(id);
    }

    @Override
    public void deleteAll() {
      repository.clear();
    }
  }
}
