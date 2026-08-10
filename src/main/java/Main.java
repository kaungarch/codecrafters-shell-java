import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception
    {
        // TODO: Uncomment the code below to pass the first stage
        List<String> builtInCommands = List.of("type", "echo", "exit");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                break;
            }
            else if (input.startsWith("echo ")) {
                System.out.println(input.substring(5));
            }
            else if (input.startsWith("type ")) {
                String arg = input.substring(5);
                boolean isBuiltInCmd = builtInCommands.contains(arg);
                if (isBuiltInCmd) System.out.println(arg + " is a shell builtin");
                else System.out.println(arg + ": not found");
            }
            else {
                System.out.println(input + ": command not found");
            }

        }

    }
}
