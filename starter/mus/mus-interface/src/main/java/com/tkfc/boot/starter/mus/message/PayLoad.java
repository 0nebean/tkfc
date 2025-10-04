package com.tkfc.boot.starter.mus.message;

import com.tkfc.boot.starter.mus.interfaces.IMailBox;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息载荷
 *
 * @author 0neBean
 * @since 2022-04-26 19:23:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayLoad {

    private String messageTag;

    private String topic;

    private byte[] message;

    private IMailBox mailBox;

}
