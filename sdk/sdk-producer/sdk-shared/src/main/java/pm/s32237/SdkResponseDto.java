package pm.s32237;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SdkResponseDto {

    @JsonProperty("responseDtoParametr")
    private String responseDtoParametr;

    public SdkResponseDto() {
    }

    public SdkResponseDto(String responseDtoParametr) {
        this.responseDtoParametr = responseDtoParametr;
    }

    public String getResponseDtoParametr() {
        return responseDtoParametr;
    }

    public void setResponseDtoParametr(String responseDtoParametr) {
        this.responseDtoParametr = responseDtoParametr;
    }

}
