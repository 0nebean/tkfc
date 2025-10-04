package com.tkfc.sdk.secruity;

import com.tkfc.core.toolkit.PropUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.util.EncodingUtils;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加解密工具类
 *
 * @author 0neBean
 */
@Slf4j
@Component
public class SdkSecPasswordEncoder {


    private static final class Digester {
        private final String algorithm;
        private int iterations;

        Digester(String algorithm, int iterations) {
            createDigest(algorithm);
            this.algorithm = algorithm;
            this.setIterations(iterations);
        }

        public byte[] digest(byte[] value) {
            MessageDigest messageDigest = createDigest(this.algorithm);

            for (int i = 0; i < this.iterations; ++i) {
                value = messageDigest.digest(value);
            }

            return value;
        }

        void setIterations(int iterations) {
            if (iterations <= 0) {
                throw new IllegalArgumentException("Iterations value must be greater than zero");
            } else {
                this.iterations = iterations;
            }
        }

        private static MessageDigest createDigest(String algorithm) {
            try {
                return MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException var2) {
                throw new IllegalStateException("No such hashing algorithm", var2);
            }
        }
    }

    private final Digester digester;
    private final byte[] secret;
    private final BytesKeyGenerator saltGenerator;
    private static final int DEFAULT_ITERATIONS = 1024;

    public SdkSecPasswordEncoder() {
        this("SHA-256", PropUtil.getInstance().getConfig("spring.security.password.encryption.secret", PropUtil.DEFAULT_NAME_SPACE));
        log.info("SdkSecPasswordEncoder secret = {}", PropUtil.getInstance().getConfig("spring.security.password.encryption.secret", PropUtil.DEFAULT_NAME_SPACE));
    }

    public String encode(CharSequence rawPassword) {
        return this.encode(rawPassword, this.saltGenerator.generateKey());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = this.decode(encodedPassword);
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return this.matches(digested, this.digest(rawPassword, salt));
    }

    private SdkSecPasswordEncoder(String algorithm, CharSequence secret) {
        this.digester = new Digester(algorithm, DEFAULT_ITERATIONS);
        this.secret = Utf8.encode(secret);
        this.saltGenerator = KeyGenerators.secureRandom();
    }

    private String encode(CharSequence rawPassword, byte[] salt) {
        byte[] digest = this.digest(rawPassword, salt);
        return new String(Hex.encode(digest));
    }

    private byte[] digest(CharSequence rawPassword, byte[] salt) {
        byte[] digest = this.digester.digest(EncodingUtils.concatenate(salt, this.secret, Utf8.encode(rawPassword)));
        return EncodingUtils.concatenate(salt, digest);
    }

    private byte[] decode(CharSequence encodedPassword) {
        return Hex.decode(encodedPassword);
    }

    private boolean matches(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        } else {
            int result = 0;

            for (int i = 0; i < expected.length; ++i) {
                result |= expected[i] ^ actual[i];
            }

            return result == 0;
        }
    }

}
