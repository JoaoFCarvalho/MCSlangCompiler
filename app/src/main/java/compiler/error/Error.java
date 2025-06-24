package compiler.error;

public class Error {
    private final String message;
    private final int position;

    public Error(String message, int position) {
        this.message = message;
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("Error at position %d: %s", position, message);
    }

    /**
     * Prints the error message, line number, and the line from the source code where the error occurred.
     */
    public void printWithSource(String source) {
        String[] lines = source.split("\n");
        int linePos = 0;
        int columnPos = 0;
        int lineNumber = 1;
        String errorLine = "";

        for (String line : lines) {
            linePos += line.length() + 1; // +1 for the newline character
            if (linePos >= position) {
                errorLine = line;
                columnPos = position - (linePos - line.length() - 1);
                break;
            }
            lineNumber++;
        }

        System.out.println(this);
        System.out.printf(" %3d | %s\n", lineNumber, errorLine);
        System.out.print("     | ");
        for (int i = 0; i < columnPos; i++) {
            System.out.print(" ");
        }
        System.out.println("^");
    }
}
