import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<String> BuiltInCommands = List.of("type", "echo", "exit");

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
            else {
                executeProcess(cmd, rem.split(" "));
                System.out.println(input + ": command not found");
            }

        }

    }

    private static void type(String rem)
    {
        boolean isBuiltInCmd = BuiltInCommands.contains(rem);
        if (isBuiltInCmd) {
            System.out.println(rem + " is a shell builtin");
        }
        else {
//                    TODO: search executable in PATH variable
            String pathVariable = System.getenv("PATH");
            String[] directories = pathVariable.split(File.pathSeparator);
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

            for (String dir : directories) {
                String fileExt = isWindows ? rem + ".exe" : rem;
                File file = new File(dir, fileExt);
                if (file.exists() && file.canExecute())
                    System.out.println(rem + " is " + file.getAbsolutePath());
            }

            System.out.println(rem + ": not found");
        }
    }

    private static void executeProcess(String exePath, String[] options)
    {
        List<String> commands = List.of(exePath);
        commands.addAll(Arrays.asList(options));

        System.out.println("Program was passed " + commands.size() + " args (including program name).");

        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectErrorStream(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
