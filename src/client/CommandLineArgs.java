package client;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class CommandLineArgs {

    @Parameter
    List<String> parameters = new ArrayList<>();

    @Parameter (names = {"-t", "--type"}, description = "Action type: [set|get|delete|exit]")
    String type;

    @Parameter (names = {"-k", "--key"}, description = "Database key")
    String key;

    @Parameter (names = {"-v", "--value"}, description = "Value to be stored")
    String value;

    @Parameter (names = {"-in"}, description = "Request provided through a file")
    String fileInput;

}
