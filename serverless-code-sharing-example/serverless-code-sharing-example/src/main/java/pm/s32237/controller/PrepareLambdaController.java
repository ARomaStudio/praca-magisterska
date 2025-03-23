package pm.s32237.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pm.s32237.controller.dto.CodeRequestDto;
import pm.s32237.controller.dto.LambdaResponseDto;
import pm.s32237.controller.dto.LambdaVersionDto;
import pm.s32237.model.Lambda;
import pm.s32237.repo.LambdaRepo;
import pm.s32237.service.CodeAdapterService;
import pm.s32237.service.LambdaService;
import pm.s32237.service.ProtoFileService;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
public class PrepareLambdaController {

    private final CodeAdapterService codeAdapterService;
    private final ProtoFileService protoFileService;
    private final LambdaService lambdaService;
    private final LambdaRepo lambdaRepo;

    public PrepareLambdaController(CodeAdapterService codeAdapterService,
                                   ProtoFileService protoFileService,
                                   LambdaService lambdaService,
                                   LambdaRepo lambdaRepo) {
        this.codeAdapterService = codeAdapterService;
        this.protoFileService = protoFileService;
        this.lambdaService = lambdaService;
        this.lambdaRepo = lambdaRepo;
    }

    @PostMapping("/adapt")
    public ResponseEntity<Map<String, String>> trigger(@RequestBody CodeRequestDto codeRequest) throws IOException {
        String id = String.valueOf(UUID.randomUUID());
        codeAdapterService.generateDtoFromMethod(codeRequest.getCode(), id);
        lambdaService.prepareJarForLambda(id);
        
        Lambda lambda = new Lambda(id, codeRequest.getCode(), codeRequest.getComment());
        lambdaRepo.save(lambda);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Lambda function created successfully");
        response.put("id", id);
        response.put("version", "1");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lambdas")
    public ResponseEntity<List<LambdaResponseDto>> getAllLambdas() {
        List<LambdaResponseDto> lambdas = lambdaRepo.findAll().stream()
                .map(lambda -> new LambdaResponseDto(
                    lambda.getId(), 
                    lambda.getComment(), 
                    lambda.getCode(),
                    lambda.isActive(),
                    lambda.getVersionNumber(),
                    lambda.getThreadId()
                ))
                .sorted((a, b) -> a.getThreadId().compareTo(b.getThreadId()))
                .toList();
        return ResponseEntity.ok(lambdas);
    }

    @PutMapping("/lambda")
    public ResponseEntity<LambdaResponseDto> editLambda(@RequestBody LambdaResponseDto codeRequest) throws IOException {
        var existingLambda = lambdaRepo.findById(codeRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lambda not found"));

        // Create new version
        String newId = String.valueOf(UUID.randomUUID());
        codeAdapterService.generateDtoFromMethod(codeRequest.getCode(), newId);
        lambdaService.prepareJarForLambda(newId);
        
        // Create new version
        Lambda newLambda = new Lambda(newId, codeRequest.getCode(), codeRequest.getComment());
        newLambda.setVersionNumber(existingLambda.getVersionNumber() + 1);
        newLambda.setActive(codeRequest.isActive());
        newLambda.setThreadId(existingLambda.getThreadId());
        lambdaRepo.save(newLambda);

        return ResponseEntity.ok(new LambdaResponseDto(
            newLambda.getId(), 
            newLambda.getComment(), 
            newLambda.getCode(), 
            newLambda.isActive(),
            newLambda.getVersionNumber(),
            newLambda.getThreadId()
        ));
    }

    @DeleteMapping("/lambda/{id}")
    public ResponseEntity<LambdaResponseDto> deleteLambda(@PathVariable String id) {
        var existingLambda = lambdaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lambda not found"));
        lambdaService.deleteJarFromS3(existingLambda.getId());
        lambdaService.deleteLambdaFunction(existingLambda.getId());
        protoFileService.deleteProtoFromS3(existingLambda.getId());
        lambdaRepo.deleteById(existingLambda.getId());

        return ResponseEntity.ok(new LambdaResponseDto(
            existingLambda.getId(), 
            existingLambda.getComment(), 
            existingLambda.getCode(),
            existingLambda.isActive(),
            existingLambda.getVersionNumber(),
            existingLambda.getThreadId()
        ));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam String key) {

        byte[] fileData = protoFileService.downloadFile("proto-files", key);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key + ".proto");

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

}
