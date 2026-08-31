package com.tkfc.boot.starter.mybatis.toolkit;

import com.tkfc.boot.starter.mybatis.enums.GeneDataType;
import com.tkfc.boot.starter.mybatis.enums.GeneScope;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.function.SerializableClosure;
import com.tkfc.core.toolkit.*;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码生成工具
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-30 15:51:20
 */
@Slf4j
public class CreateJavaTool {


    private static final String TEMPLATE_CLASS_PATH = "META-INF/mybatis/generate/";
    private static final Pattern ENUM_TYPE_PATTERN = Pattern.compile("^enum\\s*\\((.*)\\)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENUM_VALUE_PATTERN = Pattern.compile("'((?:\\\\'|[^'])*)'");
    private static final Pattern COMMENT_BRACKET_PATTERN = Pattern.compile("[（(]([^（）()]*)[）)]");
    private static String modelStaticFile;
    private static Boolean isSplitTable = Boolean.FALSE;
    private static Boolean geneVo = Boolean.TRUE;
    private static GeneDataType geneDataType = GeneDataType.CRUD;
    private static GeneScope geneScope = GeneScope.SERVICE;
    private JFrame frame;
    private JTextField tabName_text;
    private JTextField modelName_text;
    private JTextField permShortName_text;
    private JTextField component_path_text;
    private JTextField icon_text;
    private JTextField modelPath_text;
    private JTextField enumsPath_text;
    private JTextField daoPath_text;
    private JTextField servicePath_text;
    private JTextField voPath_text;
    private JTextField actionPath_text;
    private JTextField pagePath_text;
    private JTextField author_text;
    private JTextField description_text;
    private JComboBox<String> is_split_table;
    private JComboBox<String> generate_vo;
    private JComboBox<String> gene_data_type;
    private JComboBox<String> gene_range;
    private JButton confirmBtn;

    private String projectPath;
    private String xmlPath;
    private String mapperPath;
    private String pagePath;
    private String servicePath;
    private String serviceImplPath;
    private String modelPath;
    private String enumsPath;
    private String voPath;
    private String actionPath;
    private String daoPackageName;
    private String modelPackageName;
    private String enumsPackageName;
    private String voPackageName;
    private String actionPackageName;
    private String servicePackageName;
    private String createTimeStr;
    private String driverClassName;
    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private Map<String, Object> geneParam;

    private static final int WIN_WIDTH = 525;
    private static final int WIN_HEIGHT = 810;
    private static int ELEMENT_START_Y = 20;

    public CreateJavaTool() {
        init();
    }

    private void moveItemY() {
        ELEMENT_START_Y += 40;
    }

    private void init() {
        frame = new JFrame();
        frame.setSize(WIN_WIDTH, WIN_HEIGHT);
        frame.setTitle("代码生成工具 by 0neBean");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setLocation(screenWidth / 2 - WIN_WIDTH / 2, screenHeight / 2 - WIN_HEIGHT / 2);
        frame.setLayout(null);

        gene_range = new JComboBox<>();
        JLabel lab = new JLabel("生成范围:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        String[] geneRangeItem = {"Service", "Action", "Page"};
        for (String item : geneRangeItem) {
            gene_range.addItem(item);
        }
        gene_range.setFont(new Font("宋体", Font.PLAIN, 14));
        gene_range.setBounds(100, ELEMENT_START_Y, 375, 30);
        gene_range.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (item.equals("Service")) {
                    disableActionOptions();
                    disablePageOptions();
                    geneScope = GeneScope.SERVICE;
                } else if (item.equals("Action")) {
                    enableActionOptions();
                    disablePageOptions();
                    geneScope = GeneScope.ACTION;
                } else if (item.equals("Page")) {
                    enablePageOptions();
                    enableActionOptions();
                    geneScope = GeneScope.PAGE;
                }
            }
        });
        frame.add(gene_range);
        moveItemY();

        gene_data_type = new JComboBox<>();
        lab = new JLabel("数据结构:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        String[] geneDataTypeItem = {"增删改查", "树形结构"};
        for (String item : geneDataTypeItem) {
            gene_data_type.addItem(item);
        }
        gene_data_type.setFont(new Font("宋体", Font.PLAIN, 14));
        gene_data_type.setBounds(100, ELEMENT_START_Y, 375, 30);
        gene_data_type.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (item.equals("增删改查")) {
                    geneDataType = GeneDataType.CRUD;
                } else if (item.equals("树形结构")) {
                    geneDataType = GeneDataType.TREE;
                }
            }
        });
        frame.add(gene_data_type);
        moveItemY();

        generate_vo = new JComboBox<>();
        lab = new JLabel("是否生成Vo:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        String[] generateVoItem = {"是(依赖welus)", "否"};
        for (String item : generateVoItem) {
            generate_vo.addItem(item);
        }
        generate_vo.setFont(new Font("宋体", Font.PLAIN, 14));
        generate_vo.setBounds(100, ELEMENT_START_Y, 375, 30);
        generate_vo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (item.equals("是(依赖welus)")) {
                    enableVoOptions();
                    geneVo = Boolean.TRUE;
                } else if (item.equals("否")) {
                    disableVoOptions();
                    geneVo = Boolean.FALSE;
                }
            }
        });
        frame.add(generate_vo);
        moveItemY();

        is_split_table = new JComboBox<>();
        lab = new JLabel("是否分表:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        String[] strArray = {"否", "是"};
        for (String item : strArray) {
            is_split_table.addItem(item);
        }
        is_split_table.setFont(new Font("宋体", Font.PLAIN, 14));
        is_split_table.setBounds(100, ELEMENT_START_Y, 375, 30);
        is_split_table.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (item.equals("是")) {
                    isSplitTable = Boolean.TRUE;
                    String modelName = modelName_text.getText();
                    if (StringUtil.isNotBlank(modelName)) {
                        modelName_text.setText(modelName.substring(0, modelName.length() - 1));
                        permShortName_text.setText(permShortName_text.getText().substring(0, permShortName_text.getText().lastIndexOf(StringPool.UNDERSCORE)));
                    }
                } else if (item.equals("否")) {
                    isSplitTable = Boolean.FALSE;
                    String premShortName = "PERM" + tabName_text.getText().substring(tabName_text.getText().indexOf(StringPool.UNDERSCORE)).toUpperCase();
                    modelName_text.setText(StringUtil.replaceUnderLineToClassNameCase(tabName_text.getText()));
                    permShortName_text.setText(premShortName);
                }
            }
        });
        frame.add(is_split_table);
        moveItemY();

        lab = new JLabel("表名:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        tabName_text = new JTextField(30);
        tabName_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        tabName_text.getDocument().addDocumentListener(inputEvent(() -> {
            String tabName = tabName_text.getText().trim();
            confirmBtn.setEnabled(Boolean.FALSE);
            if (StringUtil.isEmpty(tabName)) {
                description_text.setText(StringPool.EMPTY);
                modelName_text.setText(StringPool.EMPTY);
                permShortName_text.setText(StringPool.EMPTY);
                return;
            }
            description_text.setText(getTableComment(tabName));
            if (tabName.contains(StringPool.UNDERSCORE)) {
                String premShortName = "PERM" + tabName.substring(tabName.indexOf(StringPool.UNDERSCORE)).toUpperCase();
                modelName_text.setText(StringUtil.replaceUnderLineToClassNameCase(tabName));
                permShortName_text.setText(premShortName);
            }
            if (isSplitTable) {
                String modelName = modelName_text.getText();
                if (StringUtil.isNotEmpty(modelName) && modelName.length() > 1) {
                    modelName_text.setText(modelName.substring(0, modelName.length() - 1));
                }
                String permShortName = permShortName_text.getText();
                int underscoreIndex = permShortName.lastIndexOf(StringPool.UNDERSCORE);
                if (underscoreIndex > 0) {
                    permShortName_text.setText(permShortName.substring(0, underscoreIndex));
                }
            }
            confirmBtn.setEnabled(Boolean.TRUE);
        }));
        frame.add(tabName_text);
        moveItemY();

        lab = new JLabel("Model名称:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        modelName_text = new JTextField(30);
        modelName_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(modelName_text);
        moveItemY();

        lab = new JLabel("权限字符:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        permShortName_text = new JTextField(30);
        permShortName_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(permShortName_text);
        moveItemY();

        lab = new JLabel("组件路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        component_path_text = new JTextField(30);
        component_path_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        component_path_text.setText("项目/模块");
        frame.add(component_path_text);
        moveItemY();

        lab = new JLabel("菜单图标:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        icon_text = new JTextField(30);
        icon_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        icon_text.setText("<Icon type=\"md-flower\" />");
        frame.add(icon_text);
        moveItemY();

        lab = new JLabel("作者信息:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        author_text = new JTextField(30);
        author_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(author_text);
        moveItemY();

        lab = new JLabel("Model注释:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        description_text = new JTextField(30);
        description_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(description_text);
        moveItemY();

        lab = new JLabel("Model路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        modelPath_text = new JTextField(30);
        modelPath_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(modelPath_text);
        moveItemY();

        lab = new JLabel("Enum路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        enumsPath_text = new JTextField(30);
        enumsPath_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(enumsPath_text);
        moveItemY();

        lab = new JLabel("Vo路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        voPath_text = new JTextField(30);
        voPath_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(voPath_text);
        moveItemY();

        lab = new JLabel("Mapper路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        daoPath_text = new JTextField(30);
        daoPath_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(daoPath_text);
        moveItemY();

        lab = new JLabel("Service路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        servicePath_text = new JTextField(30);
        servicePath_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(servicePath_text);
        moveItemY();

        lab = new JLabel("Action路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        actionPath_text = new JTextField(30);
        actionPath_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(actionPath_text);
        moveItemY();

        lab = new JLabel("Page路径:");
        lab.setBounds(20, ELEMENT_START_Y, 100, 30);
        frame.add(lab);
        pagePath_text = new JTextField(30);
        pagePath_text.setBounds(100, ELEMENT_START_Y, 375, 30);
        frame.add(pagePath_text);
        moveItemY();

        confirmBtn = new JButton("确定");
        confirmBtn.setBounds(WIN_WIDTH / 2 - 50 / 2, ELEMENT_START_Y, 80, 30);
        confirmBtn.setEnabled(Boolean.FALSE);
        confirmBtn.addActionListener(e -> generate());
        frame.add(confirmBtn);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        initFreeMark();
        initPath();
        disablePageOptions();
        disableActionOptions();
        enableVoOptions();
    }

    // 移除目录选择按钮，保持相对路径手输/配置

    private void enableVoOptions() {
        voPath_text.setEnabled(Boolean.TRUE);
    }

    private void disableVoOptions() {
        voPath_text.setEnabled(Boolean.FALSE);
    }

    private void enableActionOptions() {
        actionPath_text.setEnabled(Boolean.TRUE);
    }

    private void disableActionOptions() {
        actionPath_text.setEnabled(Boolean.FALSE);
    }

    private void enablePageOptions() {
        component_path_text.setEnabled(Boolean.TRUE);
        icon_text.setEnabled(Boolean.TRUE);
        permShortName_text.setEnabled(Boolean.TRUE);
        pagePath_text.setEnabled(Boolean.TRUE);
    }

    private void disablePageOptions() {
        component_path_text.setEnabled(Boolean.FALSE);
        icon_text.setEnabled(Boolean.FALSE);
        permShortName_text.setEnabled(Boolean.FALSE);
        pagePath_text.setEnabled(Boolean.FALSE);
    }

    private void initFreeMark() {
        FreeMarkerTemplateUtil.initResourceLoader(this.getClass());
    }

    private void initPath() {
        projectPath = getProjectPath();
        xmlPath = PropUtil.getInstance().getConfig("spring.datasource.generate.model.mapper.file.path");
        pagePath = PropUtil.getInstance().getConfig("spring.datasource.generate.page.path");
        mapperPath = PropUtil.getInstance().getConfig("spring.datasource.generate.mapper.path");
        servicePath = PropUtil.getInstance().getConfig("spring.datasource.generate.service.path");
        serviceImplPath = PropUtil.getInstance().getConfig("spring.datasource.generate.service.impl.path");
        modelPath = PropUtil.getInstance().getConfig("spring.datasource.generate.model.path");
        enumsPath = PropUtil.getInstance().getConfig("spring.datasource.generate.enums.path");
        voPath = PropUtil.getInstance().getConfig("spring.datasource.generate.vo.path");
        actionPath = PropUtil.getInstance().getConfig("spring.datasource.generate.action.path");
        daoPackageName = PropUtil.getInstance().getConfig("spring.datasource.generate.mapper.package");
        modelPackageName = PropUtil.getInstance().getConfig("spring.datasource.generate.model.package");
        enumsPackageName = PropUtil.getInstance().getConfig("spring.datasource.generate.enums.package");
        voPackageName = PropUtil.getInstance().getConfig("spring.datasource.generate.vo.package");
        servicePackageName = PropUtil.getInstance().getConfig("spring.datasource.generate.service.package");
        actionPackageName = PropUtil.getInstance().getConfig("spring.datasource.generate.action.package");

        driverClassName = PropUtil.getInstance().getConfig("spring.datasource.driver-class-name");
        databaseUrl = PropUtil.getInstance().getConfig("spring.datasource.url");
        databaseUsername = PropUtil.getInstance().getConfig("spring.datasource.username");
        databasePassword = PropUtil.getInstance().getConfig("spring.datasource.password");

        createTimeStr = DateUtil.getCurrentDateFormatString();
        if (StringUtil.isEmpty(enumsPath)) {
            enumsPath = modelPath + "enums" + StringPool.SLASH;
        }
        if (StringUtil.isEmpty(enumsPackageName)) {
            enumsPackageName = modelPackageName + ".enums";
        }
        SystemTypeEnum osType = EnvUtil.getOsType();
        if (Objects.equals(osType, SystemTypeEnum.LINUX) || Objects.equals(osType, SystemTypeEnum.MAC)) {
            xmlPath = xmlPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            mapperPath = mapperPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            modelPath = modelPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            enumsPath = enumsPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            servicePath = servicePath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            serviceImplPath = serviceImplPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            voPath = voPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            actionPath = actionPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
            pagePath = pagePath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
        }
        daoPath_text.setText(mapperPath);
        modelPath_text.setText(modelPath);
        enumsPath_text.setText(enumsPath);
        servicePath_text.setText(servicePath);
        voPath_text.setText(voPath);
        actionPath_text.setText(actionPath);
        pagePath_text.setText(pagePath);
        author_text.setText("0neBean");
        description_text.setText("请完善");
    }

    /**
     * 返回项目路径
     *
     * @return str
     */
    private static String getProjectPath() {
        String proPath = System.getProperty("user.dir");
        proPath = proPath.replaceAll("\\\\", "\\\\\\\\");
        return proPath;
    }

    /**
     * 生成代码
     */
    private void generate() {
        try {
            if (StringUtil.isEmpty(tabName_text.getText())) {
                JOptionPane.showMessageDialog(frame, "表名不能为空");
                return;
            }
            if (StringUtil.isEmpty(modelName_text.getText())) {
                JOptionPane.showMessageDialog(frame, "Model名称不能为空");
                return;
            }
            if (StringUtil.isEmpty(author_text.getText())) {
                JOptionPane.showMessageDialog(frame, "作者信息不能为空");
                return;
            }
            if (StringUtil.isEmpty(description_text.getText())) {
                JOptionPane.showMessageDialog(frame, "注释不能为空");
                return;
            }

            if (geneScope.equals(GeneScope.PAGE)) {
                if (StringUtil.isEmpty(component_path_text.getText())) {
                    JOptionPane.showMessageDialog(frame, "组件路径不能为空");
                    return;
                }
                if (StringUtil.isEmpty(icon_text.getText())) {
                    JOptionPane.showMessageDialog(frame, "图标不能为空");
                    return;
                }
            }

            fillGeneParamMap();
            generateEnumFiles();//生成枚举文件
            if (geneVo) {
                generateVoFile();//生成vo文件
            }
            generateModelFile();//生成model文件
            generateMapperFile();//生成Dao文件
            generateXmlFile();//生成Mapper文件
            generateServiceFile();//生成Service文件
            generateServiceImplFile();//生成ServiceImpl文件
            if (geneScope.equals(GeneScope.ACTION)) {
                generateActionFile();//生成Action文件
            }
            if (geneDataType.equals(GeneDataType.TREE)) {
                generateTreeFile();
            }
            if (geneScope.equals(GeneScope.PAGE)) {
                generateActionFile();//生成Action文件
                generateApiJsFile();
                generateMenuSql();
                if (geneDataType.equals(GeneDataType.TREE)) {
                    generateTreePageListFile();
                    generateTreePageDetailFile();
                } else {
                    generatePageListFile();
                    generatePageDetailFile();
                }
            }
            JOptionPane.showMessageDialog(frame, "创建完成，刷新目录查看");
            System.exit(0);
        } catch (Exception e) {
            log.error("generate code got an error , e = ", e);
        }
    }

    private void generateXmlFile() throws Exception {
        final String suffix = "Mapper.xml";
        final String path = projectPath + xmlPath + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("Xml.ftl", mapperFile, geneParam);
    }

    private void generateMapperFile() throws Exception {
        final String suffix = "Mapper.java";
        final String path = projectPath + mapperPath + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("Mapper.ftl", mapperFile, geneParam);
    }


    private void generateServiceFile() throws Exception {
        final String suffix = "Service.java";
        final String path = projectPath + servicePath + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("Service.ftl", mapperFile, geneParam);
    }

    private void generateServiceImplFile() throws Exception {
        final String suffix = "ServiceImpl.java";
        final String path = projectPath + serviceImplPath + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("ServiceImpl.ftl", mapperFile, geneParam);
    }

    private void generateModelFile() throws Exception {
        final String suffix = ".java";
        final String path = projectPath + modelPath + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("Model.ftl", mapperFile, geneParam);
    }

    private void generateVoFile() throws Exception {
        final String suffix = "Vo.java";
        final String path = projectPath + voPath + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("Vo.ftl", mapperFile, geneParam);
    }

    private void generateActionFile() throws Exception {
        final String suffix = "Action.java";
        final String path = projectPath + actionPath + modelName_text.getText() + suffix;
        File actionFile = new File(path);
        generateFileByTemplate("Action.ftl", actionFile, geneParam);
    }

    private void generateTreeFile() throws Exception {
        final String suffix = "Tree.java";
        final String path = projectPath + voPath + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("Tree.ftl", mapperFile, geneParam);
    }

    private void generatePageListFile() throws Exception {
        final String suffix = "List.vue";
        final String path = projectPath + pagePath + modelStaticFile + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("PageList.ftl", mapperFile, geneParam);
    }

    private void generatePageDetailFile() throws Exception {
        final String suffix = "DetailDrawer.vue";
        final String path = projectPath + pagePath + modelStaticFile + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("PageDetail.ftl", mapperFile, geneParam);
    }

    private void generateTreePageListFile() throws Exception {
        final String suffix = "List.vue";
        final String path = projectPath + pagePath + modelStaticFile + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("TreePageList.ftl", mapperFile, geneParam);
    }

    private void generateTreePageDetailFile() throws Exception {
        final String suffix = "DetailDrawer.vue";
        final String path = projectPath + pagePath + modelStaticFile + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("TreePageDetail.ftl", mapperFile, geneParam);
    }

    private void generateApiJsFile() throws Exception {
        final String suffix = "Api.js";
        final String path = projectPath + pagePath + modelStaticFile + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("ApiJs.ftl", mapperFile, geneParam);
    }

    private void generateMenuSql() throws Exception {
        final String suffix = "Menu.sql";
        final String path = projectPath + pagePath + modelStaticFile + modelName_text.getText() + suffix;
        File mapperFile = new File(path);
        generateFileByTemplate("MenuSql.ftl", mapperFile, geneParam);
    }

    /**
     * 填充生成代码的参数
     */
    private void fillGeneParamMap() {
        geneParam = new HashMap<>();
        List<Map<String, Object>> fields = coverField(getColumns(tabName_text.getText()));
        List<Map<String, Object>> enumFieldArr = buildEnumFieldArr(fields);
        geneParam.put("fieldArr", fields);
        geneParam.put("enumFieldArr", enumFieldArr);
        geneParam.put("createTime", createTimeStr);
        geneParam.put("author", author_text.getText());
        geneParam.put("permShortName", permShortName_text.getText().toUpperCase());
        geneParam.put("voPackageName", voPackageName);
        geneParam.put("daoPackageName", daoPackageName);
        geneParam.put("tableName", tabName_text.getText());
        if (isSplitTable) {
            geneParam.put("tableName", tabName_text.getText().substring(0, tabName_text.getText().lastIndexOf(StringPool.UNDERSCORE) + 1));
        }
        geneParam.put("modelName", modelName_text.getText());
        geneParam.put("modelPackageName", modelPackageName);
        geneParam.put("enumsPackageName", enumsPackageName);
        geneParam.put("description", description_text.getText());
        geneParam.put("actionPackageName", actionPackageName);
        geneParam.put("servicePackageName", servicePackageName);
        geneParam.put("isSplitTable", isSplitTable);
        geneParam.put("icon", getIconText());
        geneParam.put("componentPath", component_path_text.getText());
        geneParam.put("geneDataType", geneDataType.equals(GeneDataType.TREE) ? "tree" : "crud");
        geneParam.put("modelVarName", StringUtil.toLowerCaseFirstOne(modelName_text.getText()));
        modelStaticFile = geneParam.get("modelVarName").toString() + StringPool.SLASH;
    }

    private List<Map<String, Object>> buildEnumFieldArr(List<Map<String, Object>> fields) {
        List<Map<String, Object>> enumFieldArr = new ArrayList<>();
        for (Map<String, Object> field : fields) {
            if (!Boolean.TRUE.equals(field.get("isEnum"))) {
                continue;
            }
            Map<String, Object> enumField = new HashMap<>();
            String originalName = field.get("originalName").toString();
            String enumClassName = buildEnumClassName(originalName);
            enumField.put("enumClassName", enumClassName);
            enumField.put("enumPackageName", enumsPackageName);
            enumField.put("enumDescription", field.get("comment"));
            enumField.put("enumGroupVal", buildEnumGroupVal(originalName));
            enumField.put("enumGroupDic", field.get("comment"));
            enumField.put("createTime", createTimeStr);
            enumField.put("author", author_text.getText());
            enumField.put("enumItems", buildEnumItems(field.get("enumValues"), parseEnumDescriptionMap(field.get("rawComment"))));
            enumFieldArr.add(enumField);
        }
        return enumFieldArr;
    }

    private String buildEnumGroupVal(String fieldOriginalName) {
        String modelName = modelName_text.getText();
        String modelPrefix = StringUtil.camelCaseToUnderline(modelName).toUpperCase(Locale.ROOT).replaceAll("^_+", StringPool.EMPTY);
        String fieldSuffix = fieldOriginalName.toUpperCase(Locale.ROOT);
        return modelPrefix + StringPool.UNDERSCORE + fieldSuffix;
    }

    private List<Map<String, Object>> buildEnumItems(Object enumValuesObj, Map<String, String> enumDescMap) {
        List<Map<String, Object>> enumItems = new ArrayList<>();
        if (!(enumValuesObj instanceof List<?> enumValues)) {
            return enumItems;
        }
        Set<String> existNameSet = new HashSet<>();
        int sort = 0;
        for (Object enumValueObj : enumValues) {
            if (enumValueObj == null) {
                continue;
            }
            String enumValue = enumValueObj.toString();
            String enumName = toEnumConstantName(enumValue);
            if (existNameSet.contains(enumName)) {
                enumName = enumName + StringPool.UNDERSCORE + sort;
            }
            existNameSet.add(enumName);
            Map<String, Object> enumItem = new HashMap<>();
            enumItem.put("name", enumName);
            enumItem.put("value", enumValue);
            enumItem.put("description", enumDescMap.getOrDefault(enumValue, enumValue));
            enumItem.put("sort", sort++);
            enumItems.add(enumItem);
        }
        return enumItems;
    }

    private String toEnumConstantName(String value) {
        if (StringUtil.isEmpty(value)) {
            return "UNKNOWN";
        }
        String enumName = value.trim().replaceAll("[^a-zA-Z0-9]+", StringPool.UNDERSCORE).toUpperCase(Locale.ROOT);
        enumName = enumName.replaceAll("^_+", StringPool.EMPTY).replaceAll("_+$", StringPool.EMPTY);
        if (StringUtil.isEmpty(enumName)) {
            return "UNKNOWN";
        }
        if (Character.isDigit(enumName.charAt(0))) {
            return "ENUM_" + enumName;
        }
        return enumName;
    }

    private void generateEnumFiles() throws Exception {
        List<Map<String, Object>> enumFieldArr = getEnumFieldArr();
        if (CollectionUtil.isEmpty(enumFieldArr)) {
            return;
        }
        for (Map<String, Object> enumField : enumFieldArr) {
            String enumClassName = enumField.get("enumClassName").toString();
            String enumPackageName = enumField.get("enumPackageName").toString();
            String path = projectPath + enumsPath_text.getText() + enumClassName + ".java";
            Map<String, Object> enumDataMap = new HashMap<>(enumField);
            enumDataMap.put("enumPackageName", enumPackageName);
            enumDataMap.put("enumClassName", enumClassName);
            File enumFile = new File(path);
            generateFileByTemplate("Enum.ftl", enumFile, enumDataMap);
        }
    }

    private List<Map<String, Object>> getEnumFieldArr() {
        Object enumFieldObj = geneParam.get("enumFieldArr");
        if (!(enumFieldObj instanceof List<?> enumFieldList)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> enumFieldArr = new ArrayList<>();
        for (Object item : enumFieldList) {
            if (item instanceof Map<?, ?> mapItem) {
                Map<String, Object> enumField = new HashMap<>();
                for (Map.Entry<?, ?> entry : mapItem.entrySet()) {
                    if (entry.getKey() != null) {
                        enumField.put(entry.getKey().toString(), entry.getValue());
                    }
                }
                enumFieldArr.add(enumField);
            }
        }
        return enumFieldArr;
    }

    private List<Map<String, Object>> coverField(List<Map<String, Object>> columns) {
        List<Map<String, Object>> res = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            Map<String, Object> resMap = new HashMap<>();
            String field = column.get("name").toString().toLowerCase();
            if (field.equals("id")) {
                continue;
            }
            String type = column.get("type").toString();
            String rawType = column.get("rawType").toString();
            String rawComment = Objects.toString(column.get("comment"), StringPool.EMPTY);
            String cleanComment = stripCommentBracketContent(rawComment);
            String classes = BeanUtil.getJavaTypeByJdbcType(type);
            resMap.put("originalName", field);
            String method_name = StringUtil.replaceUnderLineToClassNameCase(field);
            if (method_name.endsWith("Id")) {
                classes = "Long";
            }
            if (method_name.equals("tenantId")) {
                classes = "String";
            }
            List<String> enumValues = extractEnumValues(rawType);
            boolean isEnum = CollectionUtil.isNotEmpty(enumValues);
            if (isEnum) {
                classes = buildEnumClassName(field);
            }
            String jdbcType = BeanUtil.covertJdbcType2MybatisType(type);
            if (isEnum) {
                // MyBatis JdbcType 不支持 ENUM，数据库 enum 列按 VARCHAR 处理
                jdbcType = "VARCHAR";
            }
            resMap.put("jdbcType", jdbcType);
            resMap.put("columnType", classes);
            resMap.put("columnName", StringUtil.toLowerCaseFirstOne(method_name));
            resMap.put("comment", cleanComment);
            resMap.put("rawComment", rawComment);
            resMap.put("isEnum", isEnum);
            resMap.put("enumClassName", buildEnumClassName(field));
            resMap.put("enumPackageName", enumsPackageName);
            resMap.put("enumGroupVal", buildEnumGroupVal(field));
            resMap.put("enumValues", enumValues);
            res.add(resMap);
        }
        return res;
    }

    private String buildEnumClassName(String fieldOriginalName) {
        return modelName_text.getText() + StringUtil.replaceUnderLineToClassNameCase(fieldOriginalName) + "Enum";
    }

    private List<String> extractEnumValues(String rawType) {
        List<String> enumValues = new ArrayList<>();
        if (StringUtil.isEmpty(rawType)) {
            return enumValues;
        }
        Matcher enumTypeMatcher = ENUM_TYPE_PATTERN.matcher(rawType.trim());
        if (!enumTypeMatcher.find()) {
            return enumValues;
        }
        String enumValuesStr = enumTypeMatcher.group(1);
        Matcher enumValueMatcher = ENUM_VALUE_PATTERN.matcher(enumValuesStr);
        while (enumValueMatcher.find()) {
            enumValues.add(enumValueMatcher.group(1).replace("\\'", "'"));
        }
        return enumValues;
    }

    private String stripCommentBracketContent(String rawComment) {
        if (StringUtil.isEmpty(rawComment)) {
            return StringPool.EMPTY;
        }
        return rawComment.replaceAll("\\s*[（(].*[)）]\\s*$", StringPool.EMPTY).trim();
    }

    private Map<String, String> parseEnumDescriptionMap(Object rawCommentObj) {
        Map<String, String> enumDescMap = new HashMap<>();
        if (rawCommentObj == null) {
            return enumDescMap;
        }
        String rawComment = rawCommentObj.toString();
        if (StringUtil.isEmpty(rawComment)) {
            return enumDescMap;
        }
        Matcher bracketMatcher = COMMENT_BRACKET_PATTERN.matcher(rawComment);
        while (bracketMatcher.find()) {
            String bracketText = bracketMatcher.group(1);
            if (StringUtil.isEmpty(bracketText)) {
                continue;
            }
            String[] pairs = bracketText.split("[,，]");
            for (String pair : pairs) {
                if (StringUtil.isEmpty(pair)) {
                    continue;
                }
                String[] keyValue = pair.split("[:：]", 2);
                if (keyValue.length != 2) {
                    continue;
                }
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
                    enumDescMap.put(key, value);
                }
            }
        }
        return enumDescMap;
    }

    /**
     * 根据ftl生成代码方法
     *
     * @param templateName ftl模板名
     * @param file         文件
     * @param dataMap      渲染数据
     */
    private static void generateFileByTemplate(final String templateName, File file, Map<String, Object> dataMap) throws TemplateException, IOException {
        IoUtil.createDirectoryIfNotExists(file.getParentFile().getPath());
        FreeMarkerTemplateUtil.generateFile(dataMap, TEMPLATE_CLASS_PATH + templateName, file, FreeMarkerTemplateUtil.LoaderType.CLASSPATH);
    }

    public Connection getJdbcConnect() {
        if (null == driverClassName) {
            JOptionPane.showMessageDialog(frame, "连接数据库失败,请检查是否配置Apollo参数");
            System.exit(0);
        }

        try {
            Class.forName(driverClassName);
            return DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
        } catch (Exception e) {
            log.info("open db connection got an error = ", e);
        }
        return null;
    }

    public String getTableComment(String tableName) {
        String tableComment = null;
        String sql = String.format("SELECT TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE  TABLE_NAME = '%s'", tableName);
        Connection con = getJdbcConnect();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tableComment = rs.getString("TABLE_COMMENT");
            }
            return tableComment;
        } catch (SQLException e) {
            log.info("query db got an error = ", e);
        } finally {
            closeAll(con, pstmt, rs);
        }
        return null;
    }

    public List<Map<String, Object>> getColumns(String tableName) {
        String sql = "show full fields from " + tableName;
        Connection con = getJdbcConnect();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("name", rs.getString("Field"));
                m.put("comment", rs.getString("Comment"));
                String jdbcType = rs.getString("Type");
                m.put("rawType", jdbcType);
                if (StringUtil.isNotEmpty(jdbcType) && jdbcType.contains("(")) {
                    jdbcType = jdbcType.substring(0, jdbcType.indexOf("("));
                }
                m.put("type", jdbcType);
                list.add(m);
            }
            return list;
        } catch (SQLException e) {
            log.info("query db got an error = ", e);
        } finally {
            closeAll(con, pstmt, rs);
        }
        return null;
    }

    public void closeAll(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
            if (pstmt != null && !pstmt.isClosed()) {
                pstmt.close();
            }
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            log.info("close db connection got an error = ", e);
        }
    }

    private DocumentListener inputEvent(SerializableClosure closure) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                AsyncUtil.sleep(500);
                closure.accept();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                AsyncUtil.sleep(500);
                closure.accept();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
    }

    private String getIconText() {
        String iconText = icon_text.getText();
        Pattern pattern = Pattern.compile("type\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(iconText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return StringPool.EMPTY;
    }

}
