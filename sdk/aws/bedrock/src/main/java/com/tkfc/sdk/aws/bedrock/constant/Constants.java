package com.tkfc.sdk.aws.bedrock.constant;

import java.util.regex.Pattern;

/**
 * claude 常量
 *
 * @author 0neBean
 * @version 1.0a
 * @since 2024-09-04 10:40:11
 */
public interface Constants {

    interface bedrock {
        Pattern TRANSLATED_REGEX = Pattern.compile("<TRANSLATED>([\\s\\S]*?)</TRANSLATED>");
        Double TEMPERATURE_DEFAULT = 0.0;
        int MAX_TOKENS_DEFAULT = 4096;
        Double NOVA_TOP_P = 0.9;
        int NOVA_TOP_K = 20;


        /**
         * 连接超时 单位秒
         */
        Long CONNECTION_TIMEOUT = 60L;
        /**
         * 请求超时 单位秒
         */
        Long SOCKET_TIMEOUT = 1800L;
        String ANTHROPIC_VERSION_DEFAULT = "bedrock-2023-05-31";
        String CHAT_MODEL_ID_SONNET = "anthropic.claude-3-sonnet-20240229-v1:0";
        String NOVA_TRANS_MODEL_ID = "us.amazon.nova-micro-v1:0";
        String USER_DEFAULT = "user";
        String SYS_AP_DEFAULT = "You are a highly skilled translator with expertise in many languages." +
                "Your task is to identify the language of the (from_language) I provide and accurately translate it into the (to_language) while preserving the meaning, tone." +
                "And it is strictly forbidden to translate the original text into a language other than the target language." +
                "Please maintain proper grammar, spelling, and punctuation in the translated version." +
                "I will give you a json array text, help me translate all the values, and fill the results into new field named 'tarnsValue' for each item of json." +
                "And put final translate result json array into <TRANSLATED></TRANSLATED> tag. " +
                "And make sure that the result is valid JSON array, and that each key has a corresponding value";

    }

}
