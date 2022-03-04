package com.mitc.intellij.openapi.utils.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.mitc.intellij.openapi.utils.specs.Spec;
import com.mitc.intellij.openapi.utils.utils.FileHelper;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

import static com.mitc.intellij.openapi.utils.specs.Format.YML;
import static com.mitc.intellij.openapi.utils.utils.StringUtils.equalsOneOf;
import static java.util.Objects.isNull;

public class MergeActionYaml extends AnAction {

    private static final Logger log = Logger.getInstance(MergeActionYaml.class);
    private final FileHelper fH = new FileHelper();

    @Override
    public void update(AnActionEvent e) {
        boolean enabled = false;
        VirtualFile vFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (!isNull(vFile))
            enabled = equalsOneOf(vFile.getFileType().getName().toLowerCase(Locale.ROOT), "yaml", "yml");
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        VirtualFile vFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (!isNull(vFile)) {

            try {
                File file = new File(vFile.getPath());
                String path = file.getAbsolutePath();
                if (Files.exists(Paths.get(path))) {
                    Spec spec = fH.checkOas(file);
                    switch (spec) {
                        case OAS2:
                            Swagger oas2 = new SwaggerParser().read(path);
                            io.swagger.util.Yaml.pretty().writeValue(new File(fH.mergeName(path, YML)), oas2);
                            VirtualFileManager.getInstance().syncRefresh();
                            break;
                        case OAS3:
                            OpenAPI oas3 = new OpenAPIV3Parser().read(path);
                            io.swagger.v3.core.util.Yaml.pretty().writeValue(new File(fH.mergeName(path, YML)), oas3);
                            VirtualFileManager.getInstance().syncRefresh();
                            break;
                        default:
                            log.info("Can't identify the correct specification..");
                            break;
                    }
                }

            } catch (Exception ex) {
                log.info("Issue found while merging.. " + ex.getMessage());
            }
        }
    }

}
