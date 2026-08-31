package ${modelPackageName};

import com.tkfc.boot.starter.elasticsearch.extend.BaseModelES;
import com.tkfc.core.common.annotations.elasticsearch.ESDocument;
import com.tkfc.core.common.annotations.elasticsearch.ESId;
import com.tkfc.core.common.annotations.web.param.Body;
import lombok.*;

/**
 * ${description}
 *
 * @author ${author}
 * @since ${createTime}
 */
@ESDocument(indexName = "${indexName}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "${description}")
public class ${modelName} extends BaseModelES {

    @ESId
    private String id;
}


