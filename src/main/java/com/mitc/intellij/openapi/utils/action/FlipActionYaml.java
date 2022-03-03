package com.mitc.intellij.openapi.utils.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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

import static com.mitc.intellij.openapi.utils.specs.Format.JSON;
import static com.mitc.intellij.openapi.utils.utils.StringUtils.equalsOneOf;
import static java.util.Objects.isNull;

public class FlipActionYaml extends AnAction {

    private static final Logger log = Logger.getInstance(FlipActionYaml.class);
    private final FileHelper fH = new FileHelper();

    @Override
    public void update(AnActionEvent e) {
        VirtualFile vFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(
                equalsOneOf(Objects.requireNonNull(vFile).getFileType()
                        .getName().toLowerCase(Locale.ROOT), "yaml", "yml"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        VirtualFile vFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (!isNull(vFile)) {

            try {
                File file = new File(vFile.getPath());
                String path = file.getAbsolutePath();
                if (Files.exists(Paths.get(path))) {
                    Object obj = new ObjectMapper(new YAMLFactory()).readValue(file, Object.class);
                    new JsonMapper().writerWithDefaultPrettyPrinter().writeValue(new File(fH.flipName(path, JSON)), obj);
                    VirtualFileManager.getInstance().syncRefresh();
                }
            } catch (Exception ex) {
                log.error("Exception while flipping.. , {}" + ex.getMessage());
            }

        }
    }
}
