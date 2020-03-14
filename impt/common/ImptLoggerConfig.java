package impt.common;

public class ImptLoggerConfig {
    public class Color {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";
    }

    public class Level {
        public static final String DEFAULT = "DEFAULT";
        public static final String PROMPT = "PROMPT";
        public static final String INFO = "INFO";
        public static final String ERROR = "ERROR";
        public static final String DEBUG = "DEBUG";
    }
}