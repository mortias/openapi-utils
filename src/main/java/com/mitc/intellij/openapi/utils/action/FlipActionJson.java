package com.mitc.intellij.openapi.utils.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.mitc.intellij.openapi.utils.utils.FileHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

import static com.mitc.intellij.openapi.utils.specs.Format.YML;
import static com.mitc.intellij.openapi.utils.utils.StringUtils.equalsOneOf;
import static java.util.Objects.isNull;

public class FlipActionJson extends AnAction {

    private static final Logger log = Logger.getInstance(FlipActionJson.class);
    private final FileHelper fH = new FileHelper();

    @Override
    public void update(AnActionEvent e) {
        VirtualFile vFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(
                equalsOneOf(Objects.requireNonNull(vFile).getFileType()
                        .getName().toLowerCase(Locale.ROOT), "json"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        VirtualFile vFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (!isNull(vFile)) {

            try {
                File file = new File(vFile.getPath());
                String path = file.getAbsolutePath();
                if (Files.exists(Paths.get(path))) {
                    JsonNode obj = new ObjectMapper().readTree(file);
                    new YAMLMapper().writerWithDefaultPrettyPrinter().writeValue(new File(fH.flipName(path, YML)), obj);
                    VirtualFileManager.getInstance().syncRefresh();
                }
            } catch (Exception ex) {
                log.info("Issue found while flipping.. " + ex.getMessage());
            }

        }
    }
}
