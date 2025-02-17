package pm.s32237.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pm.s32237.controller.dto.CodeRequestDto;
import pm.s32237.controller.dto.LambdaResponseDto;
import pm.s32237.model.Lambda;
import pm.s32237.repo.LambdaRepo;
import pm.s32237.service.CodeAdapterService;
import pm.s32237.service.LambdaService;
import pm.s32237.service.ProtoFileService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
    public String trigger(@RequestBody CodeRequestDto codeRequest) throws IOException {
        String id = String.valueOf(UUID.randomUUID());
        codeAdapterService.generateDtoFromMethod(codeRequest.getCode(), id);
        lambdaService.prepareJarForLambda(id);
        lambdaRepo.save(new Lambda(id, codeRequest.getCode(), codeRequest.getComment()));
        return "Done!";
    }

    @GetMapping("/lambdas")
    public ResponseEntity<List<LambdaResponseDto>> getAllLambdas() {
        List<LambdaResponseDto> lambdas = lambdaRepo.findAll().stream()
                .map(lambda -> new LambdaResponseDto(lambda.getId(), lambda.getComment(), lambda.getCode()))
                .toList();
        return ResponseEntity.ok(lambdas);
    }

    @PutMapping("/lambdas")
    public ResponseEntity<LambdaResponseDto> editLambda(@RequestBody LambdaResponseDto codeRequest) throws IOException {
        var existingLambda = lambdaRepo.findById(codeRequest.getId())
                .orElseThrow(() -> new RuntimeException("Lambda not found"));

        String id = String.valueOf(UUID.randomUUID());
        codeAdapterService.generateDtoFromMethod(codeRequest.getCode(), id);
        lambdaService.prepareJarForLambda(id);
        lambdaService.deleteJarFromS3(existingLambda.getId());
        lambdaService.deleteLambdaFunction(existingLambda.getId());
        lambdaRepo.save(new Lambda(id, codeRequest.getComment(), codeRequest.getCode()));
        protoFileService.deleteProtoFromS3(existingLambda.getId());
        lambdaRepo.deleteById(existingLambda.getId());

        return ResponseEntity.ok(new LambdaResponseDto(existingLambda.getId(), existingLambda.getComment(), existingLambda.getCode()    ));
    }

    @DeleteMapping("/lambdas/{id}")
    public ResponseEntity<LambdaResponseDto> deleteLambda(@PathVariable String id) {
        var existingLambda = lambdaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lambda not found"));
        lambdaService.deleteJarFromS3(existingLambda.getId());
        lambdaService.deleteLambdaFunction(existingLambda.getId());
        protoFileService.deleteProtoFromS3(existingLambda.getId());
        lambdaRepo.deleteById(existingLambda.getId());

        return ResponseEntity.ok(new LambdaResponseDto(existingLambda.getId(), existingLambda.getComment(), existingLambda.getCode()    ));
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
