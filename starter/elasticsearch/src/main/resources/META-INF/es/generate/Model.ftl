package ${modelPackageName};

import com.tkfc.boot.starter.elasticsearch.extend.BaseModelES;
import com.tkfc.core.common.annotations.elasticsearch.ESDocument;
import com.tkfc.core.common.annotations.elasticsearch.ESId;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ${modelName} extends BaseModelES {

    @ESId
    private String id;
}


