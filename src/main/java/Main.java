import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

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
                System.out.println(type(rem));
            }
            else {
                System.out.println(input + ": command not found");
            }

        }

    }

    private static String type(String rem)
    {
        boolean isBuiltInCmd = BuiltInCommands.contains(rem);
        if (isBuiltInCmd)
            return rem + " is a shell builtin";
        else {
//                    TODO: search executable in PATH variable
            String pathVariable = System.getenv("PATH");
            String[] directories = pathVariable.split(File.pathSeparator);
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

            for (String dir : directories) {
                String fileExt = isWindows ? rem + ".exe" : rem;
                File file = new File(dir, fileExt);
                if (file.exists() && file.canExecute())
                    return rem + " is " + file.getAbsolutePath();
            }

            return rem + ": not found";

        }
    }
}
