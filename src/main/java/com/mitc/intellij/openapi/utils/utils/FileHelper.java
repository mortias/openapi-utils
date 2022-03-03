package com.mitc.intellij.openapi.utils.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.mitc.intellij.openapi.utils.specs.Format;
import com.mitc.intellij.openapi.utils.specs.Spec;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.LinkedHashMap;

import static com.mitc.intellij.openapi.utils.specs.Spec.*;

public class FileHelper {

    private static final Logger log = Logger.getInstance(FileHelper.class);

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final TypeReference<LinkedHashMap<String, Object>> linkedHashType = new TypeReference<>() {
    };

    public Spec checkOas(final File file) {
        try {
            LinkedHashMap<String, Object> stack = mapper.readValue(file, linkedHashType);
            return stack.containsKey("swagger") ? OAS2 :
                    stack.containsKey("openapi") ? OAS3 : UNDEFINED;
        } catch (Exception ex) {
            log.error("Exception defining Spec, {}" + ex.getMessage());
            return UNDEFINED;
        }
    }

    public String mergeName(String path, Format format) {
        return FilenameUtils.getFullPath(path) + FilenameUtils.getBaseName(path) + "_merged"
                + FilenameUtils.EXTENSION_SEPARATOR + format.name().toLowerCase();
    }

    public String flipName(String path, Format format) {
        return FilenameUtils.getFullPath(path) + FilenameUtils.getBaseName(path) + "_flipped"
                + FilenameUtils.EXTENSION_SEPARATOR + format.name().toLowerCase();
    }

}
