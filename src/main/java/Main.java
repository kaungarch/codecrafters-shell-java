import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) throws Exception
    {
        // TODO: Uncomment the code below to pass the first stage
        List<String> builtInCommands = List.of("type", "echo", "exit");
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
                boolean isBuiltInCmd = builtInCommands.contains(rem);
                if (isBuiltInCmd) {
                    System.out.println(rem + " is a shell builtin");
                }
                else {
//                    TODO: search executable in PATH variable
                    String pathVariable = System.getenv("PATH");
                    String[] directories = pathVariable.split(File.pathSeparator);
                    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

//                    TODO: search executables that match to rem in all dir
                    Predicate<String> pred = dir -> {
                        Path exePath = isWindows ? Paths.get(dir).resolve(rem + ".exe") : Paths.get(dir).resolve(rem);

                        return Files.isRegularFile(exePath) && Files.isExecutable(exePath) && Files.isExecutable(exePath);
                    };

                    Arrays.stream(directories).filter(pred).findFirst()
                            .ifPresentOrElse(
                                    dir -> {
                                        System.out.println(rem + " is " + Paths.get(dir).resolve(rem));
                                    },
                                    () -> {
                                        System.out.println(rem + ": not found");
                                    }
                            );
                }
            }
            else {
                System.out.println(input + ": command not found");
            }

        }

    }
}
