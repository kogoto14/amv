package dev.aulait.amv.arch.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShortUuidUtils {

  public static String generate() {
    return compress(UUID.randomUUID());
  }

  public static String compress(UUID uuid) {
    ByteBuffer buf = ByteBuffer.allocate(Long.BYTES * 2);
    buf.putLong(uuid.getMostSignificantBits());
    buf.putLong(uuid.getLeastSignificantBits());
    byte[] array = buf.array();
    String b64str = Base64.getUrlEncoder().encodeToString(array);
    return b64str.substring(0, b64str.length() - 2);
  }

  public static UUID decompress(String compressedUuid) {
    ByteBuffer buf = ByteBuffer.wrap(Base64.getUrlDecoder().decode(compressedUuid + "=="));
    return new UUID(buf.getLong(), buf.getLong());
  }
}
