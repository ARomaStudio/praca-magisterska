package pm.s32237.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class RestConfig implements WebMvcConfigurer {

    @Autowired
    ProtobufHttpMessageConverter protobufHttpMessageConverter;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(protobufHttpMessageConverter);
    }

}
