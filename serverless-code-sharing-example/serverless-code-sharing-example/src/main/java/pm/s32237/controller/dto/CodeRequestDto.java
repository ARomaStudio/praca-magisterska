package pm.s32237.controller.dto;

public class CodeRequestDto {
    private String code;
    private String comment;

    public CodeRequestDto() {
    }

    public CodeRequestDto(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}