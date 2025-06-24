package compiler.error;

import java.util.*;

public class ErrorList extends RuntimeException {
    private final List<Error> errors = new ArrayList<>();

    public ErrorList() {
    }

    public ErrorList(String message, int position) {
        errors.add(new Error(message, position));
    }

    public void add(Error error) {
        errors.add(error);
    }

    public void add(String message, int position) {
        errors.add(new Error(message, position));
    }

    public List<Error> getAll() {
        return Collections.unmodifiableList(errors);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public int size() {
        return errors.size();
    }

    public void clear() {
        errors.clear();
    }

    public void printWithSource(String source) {
        for (Error error : errors) {
            error.printWithSource(source);
        }
    }

    @Override
    public String toString() {
        return errors.toString();
    }
}
