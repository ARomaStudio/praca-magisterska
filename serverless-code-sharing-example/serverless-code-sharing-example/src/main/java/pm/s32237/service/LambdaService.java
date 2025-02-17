package pm.s32237.service;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateRoleRequest;
import software.amazon.awssdk.services.iam.model.CreateRoleResponse;
import software.amazon.awssdk.services.iam.model.GetRoleRequest;
import software.amazon.awssdk.services.iam.model.GetRoleResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class LambdaService {

    private final String bucketName = "lambda-jars";
    private final String roleName = "lambda-execution-role";
    private final String jarFilePath = "C:\\Users\\ArtemCodeMachine\\IdeaProjects\\praca-magisterska\\localStack\\lambdaJava\\lambdaJava\\target\\lambda-java-example-1.0-SNAPSHOT.jar";

    private final S3Client s3Client;
    private final LambdaClient lambdaClient;
    private final IamClient iamClient;

    public LambdaService(S3Client s3Client, LambdaClient lambdaClient, IamClient iamClient) {
        this.s3Client = s3Client;
        this.lambdaClient = lambdaClient;
        this.iamClient = iamClient;
    }

    public void prepareJarForLambda(String id) {
        System.setProperty("maven.multiModuleProjectDirectory", "C:\\Users\\ArtemCodeMachine\\IdeaProjects\\praca-magisterska\\localStack\\lambdaJava\\lambdaJava");
        System.setProperty("maven.home", "C:\\Users\\ArtemCodeMachine\\.m2\\wrapper\\dists\\apache-maven-3.9.5-bin\\2adeog8mj13csp1uusqnc1f2mo\\apache-maven-3.9.5");
        System.setProperty("JAVA_HOME", "C:\\Users\\ArtemCodeMachine\\.jdks\\corretto-21.0.2");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("C:\\Users\\ArtemCodeMachine\\IdeaProjects\\praca-magisterska\\localStack\\lambdaJava\\lambdaJava\\pom.xml"));
        request.setGoals(Collections.singletonList("clean package"));

        Invoker invoker = new DefaultInvoker();
        try {
            invoker.execute(request);
            System.out.println("Maven command executed successfully!");
        } catch (Exception e) {
            System.err.println("Error executing Maven command: " + e.getMessage());
            e.printStackTrace();
        }

        uploadJarToS3(id);
        createLambdaFunction(ensureIamRole(), id);
    }

    private void uploadJarToS3(String id) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(id + ".jar")
                        .build(),
                RequestBody.fromFile(Paths.get(jarFilePath)));
    }

    public void deleteJarFromS3(String id) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(id + ".jar")
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        System.out.println("Jar file deleted: " + id + ".jar");
    }

    private String ensureIamRole() {
        try {
            GetRoleResponse roleResponse = iamClient.getRole(GetRoleRequest.builder().roleName(roleName).build());
            return roleResponse.role().arn();
        } catch (Exception e) {
            // Role doesn't exist; create it
            String trustPolicy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {
                        "Service": "lambda.amazonaws.com"
                      },
                      "Action": "sts:AssumeRole"
                    }
                  ]
                }
                """;

            CreateRoleResponse createRoleResponse = iamClient.createRole(CreateRoleRequest.builder()
                    .roleName(roleName)
                    .assumeRolePolicyDocument(trustPolicy)
                    .build());

            System.out.println("Created IAM Role: " + createRoleResponse.role().arn());
            return createRoleResponse.role().arn();
        }
    }

    private void createLambdaFunction(String roleArn, String id) {
        FunctionCode functionCode = FunctionCode.builder()
                .s3Bucket(bucketName)
                .s3Key(id + ".jar")
                .build();

        CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                .functionName(id)
                .runtime(Runtime.JAVA21)
                .role(roleArn)
                .handler("org.example.MyLambdaFunction::handleRequest")
                .code(functionCode)
                .build();

        lambdaClient.createFunction(functionRequest);

        System.out.println("Lambda Function deployed: " + id);
    }

    public void deleteLambdaFunction(String functionName) {
        DeleteFunctionRequest deleteFunctionRequest = DeleteFunctionRequest.builder()
                .functionName(functionName)
                .build();

        lambdaClient.deleteFunction(deleteFunctionRequest);

        System.out.println("Lambda Function deleted: " + functionName);
    }

}
