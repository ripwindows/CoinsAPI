package me.ripwindows.coinsapi.utils.queries;

import java.util.Collection;

public interface Query {
    String build();
    default String separate(Collection<String> collection, String separator) {
        StringBuilder builder = new StringBuilder();
        String sep = "";
        for (String item : collection) {
            builder.append(sep).append(item);
            sep = separator;
        }
        return builder.toString();
    }

    default String separate(Collection<String> collection) {
        return separate(collection, ",");
    }

    static SelectQuery select(String from) {
        return new SelectQuery(from);
    }

    static DeleteQuery delete(String from) {
        return new DeleteQuery(from);
    }

    static InsertQuery insert(String into) {
        return new InsertQuery(into);
    }

    static UpdateQuery update(String into) {
        return new UpdateQuery(into);
    }
}
