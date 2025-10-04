package ${serviceImplPackageName};

import com.tkfc.boot.starter.elasticsearch.extend.BaseServiceImplES;
import ${modelPackageName}.${modelName};
import ${mapperPackageName}.${modelName}Mapper;
import ${servicePackageName}.${modelName}Service;
import org.springframework.stereotype.Service;

/**
 * ${description} - ServiceImpl
 *
 * @author ${author}
 * @since ${createTime}
 */
@Service
public class ${modelName}ServiceImpl extends BaseServiceImplES<${modelName}, ${modelName}Mapper> implements ${modelName}Service {
}


