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

            String cmd = !input.contains(" ") ? input : input.substring(0, input.indexOf(" "));
            String rem = !input.contains(" ") ? "" : input.substring(input.indexOf(" ") + 1);

            if (cmd.equals("exit")) {
                System.exit(0);
            }
            else if (cmd.equals("echo")) {
                System.out.println(removeQuotes(rem));
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
                boolean executable = isExecutable(cmd);
                if (executable) {
                    if (rem.contains("'"))
                        executeProcess(cmd, getStrWithinSingleQuote(rem).toArray(new String[0]));
                    else
                        executeProcess(cmd, rem.split(" "));
                }
                else
                    System.out.println(input + ": command not found");
            }
        }
    }

    public static List<String> getStrWithinSingleQuote(String input)
    {
        Pattern pattern = Pattern.compile("'([^']*)'");
        Matcher matcher = pattern.matcher(input);
        List<String> result = new LinkedList<>();

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result;
    }

    private static String removeQuotes(String input)
    {
        Deque<Character> quotes = new ArrayDeque<>();
        char[] inputArr = input.toCharArray();
        StringBuilder strb = new StringBuilder();

        for (int i = 0; i < inputArr.length; i++) {
            char current = inputArr[i];
            boolean currentIsQuote = current == '\'' || current == '\"';
            if (currentIsQuote) {
//                if last quote and current quote is a pair, then pop the last quote from stack
                if (quotes.size() > 0 && quotes.peek() == current) quotes.pop();
//                if last quote and current quote is not a pair, then add current into stack
                else quotes.add(current);
            }
            else {
//                if within quote, simply append
                if (quotes.size() > 0)
                    strb.append(current);

//                if not within quote
                else {
                    current = current == '\t' ? ' ' : current;
                    boolean currentIsSpaceChar = current == ' ';
                    Character prev = (i - 1) < 0 ? null : inputArr[i - 1];
                    if (currentIsSpaceChar && prev != null && prev == ' ') {
//                        do not append if current and last char from input string is also tab or space character.
                    }
                    else {
                        strb.append(current);
                    }
                }
            }
        }
        return strb.toString();
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

    private static void executeProcess(String exePath, String[] options)
    {
        List<String> commands = new LinkedList<>();
        commands.add(exePath);
        commands.addAll(Arrays.asList(options));

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
