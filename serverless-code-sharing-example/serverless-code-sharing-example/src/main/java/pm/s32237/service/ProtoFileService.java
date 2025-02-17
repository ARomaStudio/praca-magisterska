package pm.s32237.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;


@Service
public class ProtoFileService {

    private final S3Client s3Client;

    public ProtoFileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public byte[] downloadFile(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key + ".proto")
                .build();

        ResponseBytes responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return responseBytes.asByteArray();
    }

    public void generateProtoFileFromDtoString(String dtoString, String outputFilePath, String id) throws IOException {
        String className = extractClassName(dtoString);
        String protoContent = buildProtoFromDto(dtoString, className);
        writeProtoToFile(protoContent, outputFilePath);
        uploadProtoToS3(id);
    }

    private static String extractClassName(String dtoString) {
        Pattern classNamePattern = Pattern.compile("class\\s+(\\w+)\\s*\\{");
        Matcher matcher = classNamePattern.matcher(dtoString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid DTO string: Class name not found.");
    }

    private static String buildProtoFromDto(String dtoString, String className) {
        StringBuilder protoBuilder = new StringBuilder();
        protoBuilder.append("syntax = \"proto3\";\n")
                .append("package pm.s32237;\n\n")
                .append("option java_package = \"pm.s32237\";\n")
                .append("option java_outer_classname = \"ProtobufExample\";\n\n")
                .append("message ").append(className).append(" {\n");

        Pattern fieldPattern = Pattern.compile("(private|protected|public)\\s+(\\w+)\\s+(\\w+);");
        Matcher matcher = fieldPattern.matcher(dtoString);
        int fieldNumber = 1;

        while (matcher.find()) {
            String javaType = matcher.group(2);
            String fieldName = matcher.group(3);
            String protoType = mapJavaTypeToProtoType(javaType);

            protoBuilder.append("    ").append(protoType).append(" ").append(fieldName)
                    .append(" = ").append(fieldNumber).append(";\n");
            fieldNumber++;
        }

        protoBuilder.append("}\n");
        return protoBuilder.toString();
    }

    private void uploadProtoToS3(String id) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket("proto-files")
                        .key(id + ".proto")
                        .build(),
                RequestBody.fromFile(Paths.get("C:\\Users\\ArtemCodeMachine\\IdeaProjects\\praca-magisterska\\localStack\\lambdaJava\\lambdaJava\\src\\main\\java\\org\\example\\proto.proto")));
    }

    public void deleteProtoFromS3(String id) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket("proto-files")
                .key(id + ".proto")
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        System.out.println("Proto file deleted: " + id + ".proto");
    }

    private static String mapJavaTypeToProtoType(String javaType) {
        switch (javaType) {
            case "int":
            case "Integer":
                return "int32";
            case "long":
            case "Long":
                return "int64";
            case "float":
            case "Float":
                return "float";
            case "double":
            case "Double":
                return "double";
            case "boolean":
            case "Boolean":
                return "bool";
            case "String":
                return "string";
            default:
                throw new IllegalArgumentException("Unsupported Java type: " + javaType);
        }
    }

    private static void writeProtoToFile(String protoContent, String outputFilePath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
            fileWriter.write(protoContent);
        }
    }

}
