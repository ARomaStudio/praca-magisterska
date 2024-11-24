package sri.s32237.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class IdlService {

    private final ResourceLoader resourceLoader;

    public IdlService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource loadAsResource(String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        return new UrlResource(resource.getURI());
    }

}
