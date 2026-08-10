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

            String cmd = input.indexOf(" ") == -1 ? input : input.substring(0, input.indexOf(" "));
            String rem = input.indexOf(" ") == -1 ? "" : input.substring(input.indexOf(" ") + 1);

            if (cmd.equals("exit")) {
                break;
            }
            else if (cmd.equals("echo")) {
                System.out.println(rem);
            }
            else if (cmd.equals("type")) {
                boolean isBuiltInCmd = builtInCommands.contains(rem);
                if (isBuiltInCmd) System.out.println(rem + " is a shell builtin");
                else System.out.println(rem + ": not found");
            }
            else {
                System.out.println(input + ": command not found");
            }

        }

    }
}
