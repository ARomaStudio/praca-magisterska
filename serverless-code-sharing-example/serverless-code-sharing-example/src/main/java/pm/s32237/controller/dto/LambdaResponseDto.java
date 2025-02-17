package pm.s32237.controller.dto;

public class LambdaResponseDto {

    private String id;

    private String code;

    private String comment;

    public LambdaResponseDto() {
    }

    public LambdaResponseDto(String id, String comment, String code) {
        this.id = id;
        this.comment = comment;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
