package com.tkfc.boot.starter.elasticsearch.toolkit;

import lombok.extern.slf4j.Slf4j;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.toolkit.DateUtil;
import com.tkfc.core.toolkit.EnvUtil;
import com.tkfc.core.toolkit.FreeMarkerTemplateUtil;
import com.tkfc.core.toolkit.IoUtil;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.StringUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成工具
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-30 15:51:20
 */
@Slf4j
public class CreateJavaToolES {

    private static final String TEMPLATE_CLASS_PATH = "META-INF/es/generate/";

    private static final int WIN_WIDTH = 520;
    private static final int WIN_HEIGHT = 360;
    private static int ELEMENT_START_Y = 20;

    private JFrame frame;
    private JTextField modelNameText;
    private JTextField authorText;
    private JTextField descriptionText;

    private JTextField modelDirText;
    private JTextField mapperDirText;
    private JTextField serviceDirText;
    private JTextField serviceImplDirText;

    private String createTimeStr;
    private String projectPath;
    private String modelPath;
    private String mapperPath;
    private String servicePath;
    private String serviceImplPath;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CreateJavaToolES::new);
    }

    public CreateJavaToolES() {
        init();
    }

    private void init() {
        frame = new JFrame();
        frame.setSize(WIN_WIDTH, WIN_HEIGHT);
        frame.setTitle("ES代码生成工具 by 0neBean");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        frame.setLocation(screenSize.width / 2 - WIN_WIDTH / 2, screenSize.height / 2 - WIN_HEIGHT / 2);
        frame.setLayout(null);

        // 模型名
        JLabel lab = new JLabel("Model名称:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        modelNameText = new JTextField(30);
        modelNameText.setBounds(120, ELEMENT_START_Y, 360, 30);
        modelNameText.getDocument().addDocumentListener(inputEvent(() ->
                descriptionText.setText(defaultDescription(modelNameText.getText()))
        ));
        frame.add(modelNameText);
        moveItemY();

        // 作者
        lab = new JLabel("作者信息:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        authorText = new JTextField(30);
        authorText.setBounds(120, ELEMENT_START_Y, 360, 30);
        frame.add(authorText);
        moveItemY();

        // 注释
        lab = new JLabel("Model注释:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        descriptionText = new JTextField(30);
        descriptionText.setBounds(120, ELEMENT_START_Y, 360, 30);
        frame.add(descriptionText);
        moveItemY();

        // 目录输入 - Model
        lab = new JLabel("Model目录:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        modelDirText = new JTextField(30);
        modelDirText.setBounds(120, ELEMENT_START_Y, 360, 30);
        frame.add(modelDirText);
        moveItemY();

        // 目录输入 - Mapper
        lab = new JLabel("Mapper目录:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        mapperDirText = new JTextField(30);
        mapperDirText.setBounds(120, ELEMENT_START_Y, 360, 30);
        frame.add(mapperDirText);
        moveItemY();

        // 目录输入 - Service
        lab = new JLabel("Service目录:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        serviceDirText = new JTextField(30);
        serviceDirText.setBounds(120, ELEMENT_START_Y, 360, 30);
        frame.add(serviceDirText);
        moveItemY();

        // 目录输入 - ServiceImpl
        lab = new JLabel("ServiceImpl目录:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        serviceImplDirText = new JTextField(30);
        serviceImplDirText.setBounds(120, ELEMENT_START_Y, 360, 30);
        frame.add(serviceImplDirText);
        moveItemY();

        JButton btn = new JButton("生成");
        btn.setBounds(WIN_WIDTH / 2 - 50 / 2, ELEMENT_START_Y, 80, 30);
        btn.addActionListener(e -> generate());
        frame.add(btn);

        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        initFreeMark();
        initDefaults();
        initPath();
    }

    private void moveItemY() {
        ELEMENT_START_Y += 40;
    }

    private void initFreeMark() {
        FreeMarkerTemplateUtil.initResourceLoader(this.getClass());
    }

    private void initDefaults() {
        createTimeStr = DateUtil.getCurrentDateFormatString();
        authorText.setText("0neBean");
        descriptionText.setText("请完善");
    }

    private void initPath() {
        projectPath = getProjectPath();
        // 读取与 CreateJavaTool 相同的配置键
        mapperPath = PropUtil.getInstance().getConfig("spring.datasource.generate.mapper.path");
        servicePath = PropUtil.getInstance().getConfig("spring.datasource.generate.service.path");
        serviceImplPath = PropUtil.getInstance().getConfig("spring.datasource.generate.service.impl.path");
        modelPath = PropUtil.getInstance().getConfig("spring.datasource.generate.model.path");

        SystemTypeEnum osType = EnvUtil.getOsType();
        if (SystemTypeEnum.LINUX.equals(osType) || SystemTypeEnum.MAC.equals(osType)) {
            if (StringUtil.isNotEmpty(mapperPath)) mapperPath = mapperPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            if (StringUtil.isNotEmpty(servicePath)) servicePath = servicePath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            if (StringUtil.isNotEmpty(serviceImplPath)) serviceImplPath = serviceImplPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            if (StringUtil.isNotEmpty(modelPath)) modelPath = modelPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
        }

        // 直接填充文本框（保持与 CreateJavaTool 一致，存放相对路径）
        mapperDirText.setText(mapperPath);
        serviceDirText.setText(servicePath);
        serviceImplDirText.setText(serviceImplPath);
        modelDirText.setText(modelPath);
    }

    // 移除目录选择按钮，保持相对路径手输/配置

    private void generate() {
        try {
            if (StringUtil.isEmpty(modelNameText.getText())) {
                JOptionPane.showMessageDialog(frame, "Model名称不能为空");
                return;
            }
            if (StringUtil.isEmpty(authorText.getText())) {
                JOptionPane.showMessageDialog(frame, "作者信息不能为空");
                return;
            }
            if (StringUtil.isEmpty(descriptionText.getText())) {
                JOptionPane.showMessageDialog(frame, "注释不能为空");
                return;
            }
            if (StringUtil.isEmpty(modelDirText.getText()) ||
                    StringUtil.isEmpty(mapperDirText.getText()) ||
                    StringUtil.isEmpty(serviceDirText.getText()) ||
                    StringUtil.isEmpty(serviceImplDirText.getText())) {
                JOptionPane.showMessageDialog(frame, "请选择所有输出目录");
                return;
            }

            Map<String, Object> data = new HashMap<>();
            String modelName = modelNameText.getText().trim();
            String modelVarName = StringUtil.toLowerCaseFirstOne(modelName);

            // 包名从目录推断（兼容相对/绝对路径）
            String modelPkg = derivePackageNameFromPath(modelDirText.getText());
            String mapperPkg = derivePackageNameFromPath(mapperDirText.getText());
            String servicePkg = derivePackageNameFromPath(serviceDirText.getText());
            String serviceImplPkg = derivePackageNameFromPath(serviceImplDirText.getText());

            if (StringUtil.isEmpty(modelPkg) || StringUtil.isEmpty(mapperPkg)
                    || StringUtil.isEmpty(servicePkg) || StringUtil.isEmpty(serviceImplPkg)) {
                JOptionPane.showMessageDialog(frame, "目录需位于 src/main/java 下以正确推断包名");
                return;
            }

            data.put("createTime", createTimeStr);
            data.put("author", authorText.getText());
            data.put("description", descriptionText.getText());
            data.put("modelName", modelName);
            data.put("modelVarName", modelVarName);
            data.put("indexName", StringUtil.camelCaseToUnderline(modelVarName));
            data.put("modelPackageName", modelPkg);
            data.put("mapperPackageName", mapperPkg);
            data.put("servicePackageName", servicePkg);
            data.put("serviceImplPackageName", serviceImplPkg);

            // 生成文件（兼容 MyBatis CreateJavaTool 的相对路径前缀规则）
            File modelFile = new File(joinPath(resolveDirForGeneration(modelDirText.getText()), modelName + ".java"));
            File mapperFile = new File(joinPath(resolveDirForGeneration(mapperDirText.getText()), modelName + "Mapper.java"));
            File serviceFile = new File(joinPath(resolveDirForGeneration(serviceDirText.getText()), modelName + "Service.java"));
            File serviceImplFile = new File(joinPath(resolveDirForGeneration(serviceImplDirText.getText()), modelName + "ServiceImpl.java"));

            generateFileByTemplate("Model.ftl", modelFile, data);
            generateFileByTemplate("Mapper.ftl", mapperFile, data);
            generateFileByTemplate("Service.ftl", serviceFile, data);
            generateFileByTemplate("ServiceImpl.ftl", serviceImplFile, data);

            JOptionPane.showMessageDialog(frame, "创建完成，刷新目录查看");
            System.exit(0);
        } catch (Exception e) {
            log.error("generate es code got an error", e);
            JOptionPane.showMessageDialog(frame, "生成失败: " + e.getMessage());
        }
    }

    private static void generateFileByTemplate(final String templateName, File file, Map<String, Object> dataMap) throws Exception {
        IoUtil.createDirectoryIfNotExists(file.getParentFile().getPath());
        FreeMarkerTemplateUtil.generateFile(dataMap, TEMPLATE_CLASS_PATH + templateName, file, FreeMarkerTemplateUtil.LoaderType.CLASSPATH);
    }

    private String derivePackageNameFromPath(String dirPath) {
        if (StringUtil.isEmpty(dirPath)) {
            return StringPool.EMPTY;
        }
        String base = resolveDirForGeneration(dirPath);
        String normalized = base.replace("\\", "/");
        int idx = normalized.indexOf("/src/main/java/");
        if (idx == -1) {
            return StringPool.EMPTY;
        }
        String pkgPath = normalized.substring(idx + "/src/main/java/".length());
        if (pkgPath.endsWith("/")) {
            pkgPath = pkgPath.substring(0, pkgPath.length() - 1);
        }
        return pkgPath.replace('/', '.');
    }

    private String joinPath(String dir, String fileName) {
        if (dir.endsWith(StringPool.SLASH) || dir.endsWith(StringPool.BACK_SLASH)) {
            return dir + fileName;
        }
        return dir + File.separator + fileName;
    }

    private String resolveDirForGeneration(String path) {
        if (StringUtil.isEmpty(path)) {
            return path;
        }
        String input = path;
        // 如果是绝对路径
        File absoluteCandidate = new File(input);
        if (absoluteCandidate.isAbsolute()) {
            // 若该绝对路径存在，直接使用（来自目录选择器的情况）
            if (absoluteCandidate.exists()) {
                return absoluteCandidate.getAbsolutePath();
            }
            // 若不存在，则按 MyBatis 工具的策略视为项目内相对路径：projectPath + input
            return projectPath + input;
        }
        // 非绝对路径，拼接项目根
        return joinPath(projectPath, input);
    }

    private DocumentListener inputEvent(Runnable runnable) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                runnable.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) { }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        };
    }

    private String defaultDescription(String modelName) {
        if (StringUtil.isEmpty(modelName)) {
            return "请完善";
        }
        return modelName + " 实体";
    }

    private static String getProjectPath() {
        String proPath = System.getProperty("user.dir");
        proPath = proPath.replaceAll("\\\\", "\\\\\\\\");
        return proPath;
    }
}
