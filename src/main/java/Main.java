import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final List<String> BuiltInCommands = List.of("type", "echo", "exit", "pwd", "cd");

    public static void main(String[] args) throws Exception
    {
        // TODO: Uncomment the code below to pass the first stage
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

//            parsing user input
            List<String> inputTokens = parseArguments(input);

            if (inputTokens.isEmpty()) continue;

//            for redirect output file
            String outputFile = null;
//            storage for tokens that doesn't present operators like '>'
            List<String> cleanTokens = new ArrayList<>();
            for (int i = 0; i < inputTokens.size(); i++) {
                String currentToken = inputTokens.get(i);
                if (currentToken.equals(">") || currentToken.equals("1>") || currentToken.equals("2>")) {
                    if (inputTokens.size() > i + 1) {
                        outputFile = inputTokens.get(i + 1);
                        break;
                    }
                }
                else {
                    cleanTokens.add(currentToken);
                }
            }

            if (cleanTokens.isEmpty()) continue;

//            get the 1st token as cmd
            String cmd = cleanTokens.getFirst();
//            treat the rest tokens as arguments
            List<String> argsList = cleanTokens.subList(1, cleanTokens.size());
            String parsedArgs = String.join(" ", argsList);

            if (!BuiltInCommands.contains(cmd) && isExecutable(cmd)) {
                executeProcess(cleanTokens, outputFile);
                continue;
            }

            PrintStream defaultOutputStream = System.out;


            try {
//                redirecting output stream to a file if command is built in command and output file exists.
                if (outputFile != null) {
                    Path path = Paths.get(outputFile);
                    if (path.getParent() != null) {
                        Files.createDirectories(path.getParent());
                    }
                    PrintStream fileOutputStream = new PrintStream(Files.newOutputStream(path));
                    System.setOut(fileOutputStream);
                }

                if (cmd.equals("exit")) {
                    System.exit(0);
                }
                else if (cmd.equals("echo")) {
                    System.out.println(parsedArgs);
                }
                else if (cmd.equals("type")) {
                    type(parsedArgs);
                }
                else if (cmd.equals("pwd")) {
                    printWorkingDirectory();
                }
                else if (cmd.equals("cd")) {
                    changeDirectory(parsedArgs);
                }
                else {
                    System.out.println(input + ": command not found");
                }
            } finally {
                System.out.flush();
                System.setOut(defaultOutputStream);
            }
        }
    }

    private static List<String> parseArguments(String input)
    {
        boolean insideSingleQuote = false;
        boolean insideDoubleQuote = false;
        boolean isBackslashOn = false;
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);

            if (current == '\\' && !insideSingleQuote && !isBackslashOn) {
                isBackslashOn = true;
            }
            else if (current == '\'' && !insideDoubleQuote && !isBackslashOn) {
                insideSingleQuote = !insideSingleQuote;
            }
            else if (current == '"' && !insideSingleQuote && !isBackslashOn) {
                insideDoubleQuote = !insideDoubleQuote;
            }
            else if (isBackslashOn) {
                currentToken.append(current);
                isBackslashOn = false;
            }
            else if (Character.isWhitespace(current) && !insideDoubleQuote && !insideSingleQuote) {
                if (!currentToken.isEmpty()) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            }
            else {
                currentToken.append(current);
            }
        }

        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private static void changeDirectory(String input)
    {
        if (input.equals("~")) {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String home = isWindows ? "USERPROFILE" : "HOME";
            String homeEnv = System.getenv(home);
            System.setProperty("user.dir", homeEnv);
            return;
        }
        Path path = Paths.get(input);

        if (path.isAbsolute() && Files.exists(path)) {
            System.setProperty("user.dir", input);
        }
        else {
            String cwd = System.getProperty("user.dir");
            Path desiredPath = Paths.get(cwd).resolve(input).normalize();

            if (Files.isDirectory(desiredPath)) {
                System.setProperty("user.dir", desiredPath.toString());
                return;
            }
            System.out.println("cd: " + input + ": No such file or directory");
        }
    }

    private static void printWorkingDirectory()
    {
        String dir = System.getProperty("user.dir");
        System.out.println(dir);
    }

    private static void type(String input)
    {
        boolean isBuiltInCmd = BuiltInCommands.contains(input);
        if (isBuiltInCmd) {
            System.out.println(input + " is a shell builtin");
        }
        else {
//                    TODO: search executable in PATH variable
            String pathVariable = System.getenv("PATH");
            String[] directories = pathVariable.split(File.pathSeparator);
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

            for (String dir : directories) {
                String fileExt = isWindows ? input + ".exe" : input;
                File file = new File(dir, fileExt);
                if (file.exists() && file.canExecute()) {
                    System.out.println(input + " is " + file.getAbsolutePath());
                    return;
                }
            }

            System.out.println(input + ": not found");
        }
    }

    private static boolean isExecutable(String input)
    {
        String pathVariable = System.getenv("PATH");
        String[] directories = pathVariable.split(File.pathSeparator);
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        for (String dir : directories) {
            String fileExt = isWindows ? input + ".exe" : input;
            File file = new File(dir, fileExt);
            if (file.exists() && file.canExecute())
                return true;
        }

        return false;
    }

    private static void executeProcess(List<String> commands, String outputFile)
    {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
//        processBuilder.redirectErrorStream(true);

        try {
            if (outputFile != null) {
                Path path = Paths.get(outputFile);
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                processBuilder.redirectOutput(path.toFile());
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                processBuilder.redirectError(path.toFile());
            }
            else {
                processBuilder.inheritIO();
            }

            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println("Got Exception : " + e.getMessage());
        }
    }
}
