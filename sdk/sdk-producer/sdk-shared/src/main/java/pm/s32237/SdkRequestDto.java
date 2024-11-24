package pm.s32237;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SdkRequestDto {

    @JsonProperty("requestDtoParametr")
    private String requestDtoParametr;

    public String getRequestDtoParametr() {
        return requestDtoParametr;
    }

    public void setRequestDtoParametr(String requestDtoParametr) {
        this.requestDtoParametr = requestDtoParametr;
    }

    public SdkRequestDto(String requestDtoParametr) {
        this.requestDtoParametr = requestDtoParametr;
    }

    public SdkRequestDto() {
    }

}
