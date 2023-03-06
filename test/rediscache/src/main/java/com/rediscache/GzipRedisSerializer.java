package com.rediscache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class GzipRedisSerializer implements RedisSerializer<Object> {
    private RedisSerializer<Object> originSerializer;

    public GzipRedisSerializer(RedisSerializer<Object> originSerializer) {
        this.originSerializer = originSerializer;
    }

    public byte[] serialize(Object t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        } else {
            ByteArrayOutputStream bos = null;
            GZIPOutputStream gzip = null;

            byte[] var5;
            try {
                byte[] bytes = this.originSerializer.serialize(t);
                bos = new ByteArrayOutputStream();
                gzip = new GZIPOutputStream(bos);
                gzip.write(bytes);
                gzip.finish();
                var5 = bos.toByteArray();
            } catch (Exception var9) {
                throw new SerializationException("Gzip Serialization Error", var9);
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(gzip);
            }

            return var5;
        }
    }

    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes != null && bytes.length != 0) {
            ByteArrayOutputStream bos = null;
            ByteArrayInputStream bis = null;
            GZIPInputStream gzip = null;

            try {
                bos = new ByteArrayOutputStream();
                bis = new ByteArrayInputStream(bytes);
                gzip = new GZIPInputStream(bis);
                byte[] buff = new byte[4096];

                int n;
                while((n = gzip.read(buff, 0, 4096)) > 0) {
                    bos.write(buff, 0, n);
                }

                Object var7 = this.originSerializer.deserialize(bos.toByteArray());
                return var7;
            } catch (Exception var11) {
                throw new SerializationException("Gzip deserizelie error", var11);
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(bis);
                IOUtils.closeQuietly(gzip);
            }
        } else {
            return null;
        }
    }
}
