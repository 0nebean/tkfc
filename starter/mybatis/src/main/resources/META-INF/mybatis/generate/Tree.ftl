package ${voPackageName};

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import lombok.*;

import java.util.List;

/**
 * ${description} vo
 *
 * @author ${author}
 * @since ${createTime}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Body(tag = "${description}")
public class ${modelName}Tree {

    public ${modelName}Tree(String isRoot, Long parentId) {
        this.isRoot = isRoot;
        this.setId(parentId);
    }

    @BodyProperty(tag = "是否是根权限 (0:否 ,1:是)")
    private String isRoot;

    @BodyProperty(tag = "父级id")
    private Integer parentId;

    @BodyProperty(tag = "排序字段")
    private Integer sort;

    @BodyProperty(tag = "中文名")
    private String chName;

    @BodyProperty(tag = "是否激活")
    private String isActive;

    @BodyProperty(tag = "主键")
    private Long id;

    @BodyProperty(tag = "是否有子节点")
    private Boolean hasChild;

    @BodyProperty(tag = "是否展开")
    private Boolean _showChildren;

    @BodyProperty(tag = "viewUI tree select")
    private Boolean expand;

    @BodyProperty(tag = "viewUI tree _loading")
    private Boolean _loading;

    @BodyProperty(tag = "viewUI tree title")
    private String title;

    @BodyProperty(tag = "viewUI tree value")
    private Long value;

    @BodyProperty(tag = "是否选中")
    private Boolean checked;

    @BodyProperty(tag = "子节点")
    private List<${modelName}Tree> children;

}