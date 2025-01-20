import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Map;

interface Siquel {

    sealed interface Field<T> {
        record Value<T>(T value) implements Field<T> {}
        record TableField<T>(String table, String name) implements Field<T> {}
        record Alias<T>(Field<T> field, String name) implements Field<T> {}

        default String toSql() {
            return switch (this) {
                case Value(var value) -> String.valueOf(value);
                case TableField(var table, var name) -> table + "." + name;
                case Alias(var field, var name) -> field.toSql() + " as " + name;
            };
        }
    }

    interface Table {
        String name();
    }

    sealed interface Filter<T> {
        record Equal<T>(Field<T> left, Field<T> right) implements Filter<T> {}
        record GreaterThan<T>(Field<T> left, Field<T> right) implements Filter<T> {}
        record LessThan<T>(Field<T> left, Field<T> right) implements Filter<T> {}

        default String toSql() {
            return switch (this) {
                case Equal(var left, var right) -> left.toSql() + " = " + right.toSql();
                case GreaterThan(var left, var right) -> left.toSql() + " > " + right.toSql();
                case LessThan(var left, var right) -> left.toSql() + " < " + right.toSql();
            };
        }
    }

    enum Order {
        ASC, DESC
    }

    record Sorting<T>(Field<T> field, Order order) {
        String toSql() {
            return field.toSql() + " " + order.name();
        }
    }

    static <T> Sorting<T> asc(Field<T> field) {
        return new Sorting<>(field, Order.ASC);
    }

    static <T> Sorting<T> desc(Field<T> field) {
        return new Sorting<>(field, Order.DESC);
    }

    record Record(Map<Field<?>, Object> values) {

        @SuppressWarnings("unchecked")
        <T> T get(Field<T> field) {
            return (T) values.get(field);
        }
    }

    record Result(List<Record> record) {

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
                    sql += fields.stream().map(Field::toSql).collect(joining(", ")) + " from " + table.name();
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

            Result execute() {
                return new Result(null);
            }
        }

    static Query select(Field<?>... fields) {
        return new Query(List.of(fields), null, null, null);
    }

    static <T> Field<T> field(String table, String name) {
        return new Field.TableField<>(table, name);
    }

    static <T> Filter<T> equal(Field<T> field, T value) {
        return new Filter.Equal<>(field, new Field.Value<T>(value));
    }

    static <T> Filter<T> gt(Field<T> field, T value) {
        return new Filter.GreaterThan<>(field, new Field.Value<T>(value));
    }

    static <T> Filter<T> lt(Field<T> field, T value) {
        return new Filter.LessThan<>(field, new Field.Value<T>(value));
    }

    final class People implements Table {

        public String name() {
            return "people";
        }

        Field<String> NAME = field("people", "name");
        Field<Integer> AGE = field("people", "age");
    }
        
    People PEOPLE = new People();

    static void main(String... args) {
        var query = select(
            PEOPLE.NAME,
            PEOPLE.AGE)
        .from(PEOPLE)
        .where(gt(PEOPLE.AGE, 18))
        .sorting(asc(PEOPLE.AGE));

        System.out.println(query.toSql());

        var result = query.execute();

        System.out.println(result);
    }
}
