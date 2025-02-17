package pm.s32237.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class CodeAdapterService {

    private final ProtoFileService protoFileService;

    public CodeAdapterService(ProtoFileService protoFileService) {
        this.protoFileService = protoFileService;
    }

    public String generateDtoFromMethod(String methodString, String id) throws IOException {
        // Extract the parameters from the method string
        String parameterSection = methodString.substring(methodString.indexOf('(') + 1, methodString.indexOf(')')).trim();

        if (parameterSection.isEmpty()) {
            return "public class MyInputDto { public static class GeneratedDto {} }";
        }

        String[] parameters = parameterSection.split(",");
        List<String> fields = new ArrayList<>();

        // Parse the parameters
        for (String param : parameters) {
            String[] parts = param.trim().split("\\s+");
            if (parts.length == 2) {
                String type = parts[0];
                String name = parts[1];
                fields.add(generateFieldAndAccessor(type, name));
            }
        }

        // Generate the class with fields and accessors
        StringBuilder classBuilder = new StringBuilder();
        classBuilder.append("package org.example;\n \n  public class MyInputDto {\n    public static class GeneratedDto {\n\n");
        for (String field : fields) {
            classBuilder.append(indent(field, 8)).append("\n");
        }
        classBuilder.append("    }\n}");

        writeToFile(classBuilder.toString(), "C:\\Users\\ArtemCodeMachine\\IdeaProjects\\praca-magisterska\\localStack\\lambdaJava\\lambdaJava\\src\\main\\java\\org\\example\\MyInputDto.java");

        generateLambdaClass(methodString);

        protoFileService.generateProtoFileFromDtoString(classBuilder.toString(), "C:\\Users\\ArtemCodeMachine\\IdeaProjects\\praca-magisterska\\localStack\\lambdaJava\\lambdaJava\\src\\main\\java\\org\\example\\proto.proto", id);

        return classBuilder.toString();
    }

    public String generateLambdaClass(String methodString) {
        StringBuilder classBuilder = new StringBuilder();

        classBuilder.append("package org.example;\n" +
                "\n" +
                "import com.amazonaws.services.lambda.runtime.Context;\n" +
                "import com.amazonaws.services.lambda.runtime.RequestHandler;\n" +
                "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                "\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class MyLambdaFunction implements RequestHandler<Map<String, Object>, String> {\n" +
                "\n" +
                "    private static final ObjectMapper objectMapper = new ObjectMapper();\n" +
                "\n" +
                "    @Override\n" +
                "    public String handleRequest(Map<String, Object> input, Context context) {\n" +
                "        MyInputDto.GeneratedDto myInput = objectMapper.convertValue(input, MyInputDto.GeneratedDto.class);\n");
        classBuilder.append("       return ");
        classBuilder.append(generateMethodCall(methodString,  "myInput"));
        classBuilder.append("\n } \n");
        classBuilder.append(methodString);
        classBuilder.append("\n } \n");


        writeToFile(classBuilder.toString(), "C:\\Users\\ArtemCodeMachine\\IdeaProjects\\praca-magisterska\\localStack\\lambdaJava\\lambdaJava\\src\\main\\java\\org\\example\\MyLambdaFunction.java");

        return classBuilder.toString();
    }

    public String generateMethodCall(String methodString, String inputObject) {
        // Regular expression to parse method signature
        String methodSignatureRegex = "\\s*public\\s+\\S+\\s+(\\w+)\\s*\\((.*?)\\)\\s*\\{";
        Pattern pattern = Pattern.compile(methodSignatureRegex);
        Matcher matcher = pattern.matcher(methodString);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid method format: Unable to parse method signature.");
        }

        // Extract method name and parameters
        String methodName = matcher.group(1);
        String parameters = matcher.group(2);

        // Parse parameter names from the parameter string
        String[] paramList = parameters.isEmpty() ? new String[0] : parameters.split(",");
        StringBuilder paramAccessors = new StringBuilder();
        for (String param : paramList) {
            String paramName = param.trim().split("\\s+")[1]; // Extract the parameter name
            if (paramAccessors.length() > 0) {
                paramAccessors.append(", ");
            }
            paramAccessors.append(inputObject).append(".get").append(capitalize(paramName)).append("()");
        }

        // Generate the method call
        return "this." + methodName + "(" + paramAccessors + ");";
    }

    /**
     * Capitalizes the first letter of a given string.
     *
     * @param str the input string
     * @return the string with the first letter capitalized
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String generateFieldAndAccessor(String type, String name) {
        // Capitalize the first letter of the field name for methods
        String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1);

        return """
            private %s %s;

            public %s get%s() {
                return %s;
            }

            public void set%s(%s %s) {
                this.%s = %s;
            }
            """.formatted(
                type, name, // Field declaration
                type, capitalized, name, // Getter
                capitalized, type, name, name, name // Setter
        );
    }

    private static String indent(String text, int spaces) {
        String indentation = " ".repeat(spaces);
        return text.lines().map(line -> indentation + line).reduce((a, b) -> a + "\n" + b).orElse("");
    }

    public static void writeToFile(String content, String filePath) {
        BufferedWriter writer = null;
        try {
            // Create a File object with the specified file path
            File file = new File(filePath);

            // Create BufferedWriter to write to the file
            writer = new BufferedWriter(new FileWriter(file));

            // Write the content to the file
            writer.write(content);

            // Optionally, flush the writer (although it's done automatically on close)
            writer.flush();

            System.out.println("Content written to file successfully.");

        } catch (IOException e) {
            // Handle potential IOExceptions (e.g., file not found, permission issues)
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        } finally {
            try {
                // Ensure the writer is closed properly
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println("An error occurred while closing the writer: " + e.getMessage());
            }
        }
    }

}