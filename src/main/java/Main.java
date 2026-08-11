import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static final List<String> BuiltInCommands = List.of("type", "echo", "exit", "pwd");

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
                break;
            }
            else if (cmd.equals("echo")) {
                System.out.println(rem);
            }
            else if (cmd.equals("type")) {
                type(rem);
            }
            else if (cmd.equals("pwd")) {
                printWorkingDirectory();
            }
            else {
                boolean executable = isExecutable(cmd);
                if (executable) {
                    executeProcess(cmd, rem.split(" "));
                }
                else
                    System.out.println(input + ": command not found");
            }
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
            System.out.println(e.getMessage());
        }
    }
}
