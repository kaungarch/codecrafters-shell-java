import java.io.File;
import java.nio.file.Files;
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

            List<String> parsedInputs = parseArguments(input);
            String inputString = String.join(" ", parsedInputs);

            String cmd = parsedInputs.getFirst();
            String rem = inputString.replaceFirst(cmd + " ", "");

            if (cmd.equals("exit")) {
                System.exit(0);
            }
            else if (cmd.equals("echo")) {
                String result = String.join(" ", parseArguments(rem));
                System.out.println(result);
            }
            else if (cmd.equals("type")) {
                type(rem);
            }
            else if (cmd.equals("pwd")) {
                printWorkingDirectory();
            }
            else if (cmd.equals("cd")) {
                changeDirectory(rem);
            }
            else {
                boolean cmdIsExecutable = isExecutable(cmd);
                if (cmdIsExecutable) {
                    executeProcess(cmd, Arrays.stream(rem.split(" ")).toList());
                }
                else
                    System.out.println(input + ": command not found");
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

    private static void executeProcess(List<String> commands)
    {
//        List<String> commands = new LinkedList<>();
//        commands.add(exePath);
//        commands.addAll(options);

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);
        pb.inheritIO();

        try (
                Process process = pb.start()
        ) {

            int exitCode = process.waitFor();

        } catch (Exception e) {
            System.out.println("Got exception: " + e.getMessage());
        }
    }
}
