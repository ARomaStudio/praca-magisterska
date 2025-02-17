package pm.s32237.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.springframework.stereotype.Service;
import pm.s32237.ProtobufExample;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Service
public class SdkClientService {

    private final LambdaClient lambdaClient;

    public SdkClientService(LambdaClient lambdaClient) {
        this.lambdaClient = lambdaClient;
    }

    public String invokeLambda(String functionName, String payload) throws InvalidProtocolBufferException {
        functionName = "my-java-lambda";
        ProtobufExample.MyInputDto dto = ProtobufExample.MyInputDto.newBuilder().setName("name").setType("type").build();
        payload= JsonFormat.printer().print(dto);;
        // Create InvokeRequest with the function name and payload
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(payload)) // Send payload as string
                .build();

        // Invoke Lambda function
        InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);

        // Return response payload as string
        return invokeResponse.payload().asUtf8String();
    }

}
