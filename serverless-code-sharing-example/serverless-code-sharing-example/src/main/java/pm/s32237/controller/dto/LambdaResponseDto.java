package pm.s32237.controller.dto;

public class LambdaResponseDto {

    private String id;

    private String code;

    private String comment;

    private boolean active;

    private Integer versionNumber;

    private String threadId;

    public LambdaResponseDto() {
    }

    public LambdaResponseDto(String id, String comment, String code, boolean active, Integer versionNumber, String threadId) {
        this.id = id;
        this.comment = comment;
        this.code = code;
        this.active = active;
        this.versionNumber = versionNumber;
        this.threadId = threadId;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

}
