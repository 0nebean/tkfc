package com.tkfc.welus.report.dto.build;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * method属性 dto
 *
 * @author 0neBean
 * @since 2022-06-22 04:16:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfoDto {

    private String methodType;
    private String name;
    private String authType;
    private String[] path;
    private String[] author;

}
