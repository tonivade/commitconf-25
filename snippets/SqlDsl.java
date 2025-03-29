import static java.util.stream.Collectors.joining;

import java.util.List;

interface SqlDsl {

  interface Table {
    String name();
  }

  sealed interface Field<T> {
    default String toSql() {
      return switch (this) {
        case Value(var value) -> String.valueOf(value);
        case TableField(var table, var name) -> table + "." + name;
        case Alias(var field, var name) -> field.toSql() + " as " + name;
      };
    }
  }

  record Value<T>(T value) implements Field<T> {}

  record TableField<T>(String table, String name) implements Field<T> {}

  record Alias<T>(Field<T> field, String name) implements Field<T> {}

  sealed interface Filter<T> {
    default String toSql() {
      return switch (this) {
        case Equal(var left, var right) -> left.toSql() + " = " + right.toSql();
        case GreaterThan(var left, var right) -> left.toSql() + " > " + right.toSql();
        case LessThan(var left, var right) -> left.toSql() + " < " + right.toSql();
      };
    }
  }

  record Equal<T>(Field<T> left, Field<T> right) implements Filter<T> {}

  record GreaterThan<T>(Field<T> left, Field<T> right) implements Filter<T> {}

  record LessThan<T>(Field<T> left, Field<T> right) implements Filter<T> {}

  sealed interface Sorting<T> {
    default String toSql() {
      return switch (this) {
        case Ascending(var field) -> field.toSql() + " asc";
        case Descending(var field) -> field.toSql() + " desc";
      };
    }
  }

  record Ascending<T>(Field<T> field) implements Sorting<T> {
  }

  record Descending<T>(Field<T> field) implements Sorting<T> {
  }

  static <T> Sorting<T> asc(Field<T> field) {
    return new Ascending<>(field);
  }

  static <T> Sorting<T> desc(Field<T> field) {
    return new Descending<>(field);
  }

  record Query(
      List<Field<?>> fields,
      Table table,
      List<Filter<?>> filters,
      List<Sorting<?>> sorting) {

    Query from(Table table) {
      return new Query(fields, table, filters, sorting);
    }

    Query where(Filter<?>... filters) {
      return new Query(fields, table, List.of(filters), sorting);
    }

    Query sorting(Sorting<?>... sorting) {
      return new Query(fields, table, filters, List.of(sorting));
    }

    String toSql() {
      var sql = "select ";

      if (!filters.isEmpty()) {
        sql += fields.stream().map(Field::toSql).collect(joining(", "));
      } else {
        sql += "*";
      }

      sql += " from " + table.name();

      if (!filters.isEmpty()) {
        sql += " where " + filters.stream().map(Filter::toSql).collect(joining(" and "));
      }

      if (!sorting.isEmpty()) {
        sql += " order by " + sorting.stream().map(Sorting::toSql).collect(joining(", "));
      }

      return sql;
    }
  }

  static Query select(Field<?>... fields) {
    return new Query(List.of(fields), null, null, null);
  }

  static <T> Field<T> field(String table, String name) {
    return new TableField<>(table, name);
  }

  static <T> Field<T> value(T value) {
    return new Value<>(value);
  }

  static <T> Filter<T> eq(Field<T> field, T value) {
    return new Equal<>(field, value(value));
  }

  static <T> Filter<T> gt(Field<T> field, T value) {
    return new GreaterThan<>(field, value(value));
  }

  static <T> Filter<T> lt(Field<T> field, T value) {
    return new LessThan<>(field, value(value));
  }

  final class People implements Table {

    final Field<String> NAME = field("people", "name");
    final Field<Integer> AGE = field("people", "age");

    public String name() {
      return "people";
    }
  }


  static void main(String... args) {
    People PEOPLE = new People();

    var query = select(PEOPLE.NAME, PEOPLE.AGE)
        .from(PEOPLE)
        .where(gt(PEOPLE.AGE, 18))
        .sorting(asc(PEOPLE.AGE));

    System.console().println(query.toSql());
  }
}
