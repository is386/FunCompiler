import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static ArrayList<String> parseInput() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> inputList = new ArrayList<>();

        while (scanner.hasNextLine()) {
            inputList.add(scanner.nextLine().stripLeading());
        }

        scanner.close();
        return inputList;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<String> source = parseInput();
        Parser parser = new Parser(source);
        parser.parse();
    }
}
