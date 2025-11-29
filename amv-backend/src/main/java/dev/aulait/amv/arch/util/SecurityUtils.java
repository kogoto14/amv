package dev.aulait.amv.arch.util;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.TinkJsonProtoKeysetFormat;
import com.google.crypto.tink.aead.AeadConfig;
import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.file.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Base64;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

  private static final KeysetHandle KEYSET_HANDLE = buildKeysetHandle();
  private static final String KEYSET_FILE_NAME = "encrypted-keyset.json";
  private static final String PREFIX_AMV = "amvenc:";

  private static KeysetHandle buildKeysetHandle() {
    try {
      AeadConfig.register();
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
    Path keysetPath = DirectoryManager.SECURITY_ROOT.resolve(KEYSET_FILE_NAME);
    File keyFile = new File(keysetPath.toString());

    if (!keyFile.exists()) {
      createKeysetFile();
    }

    try {
      return CleartextKeysetHandle.read(JsonKeysetReader.withString(FileUtils.read(keysetPath)));
    } catch (GeneralSecurityException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void createKeysetFile() {
    try {
      String serializedKeyset =
          TinkJsonProtoKeysetFormat.serializeKeyset(
              KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM")),
              InsecureSecretKeyAccess.get());
      FileUtils.write(DirectoryManager.SECURITY_ROOT.resolve(KEYSET_FILE_NAME), serializedKeyset);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static String encrypt(String token) {
    if (StringUtils.isEmpty(token) || token.startsWith(PREFIX_AMV)) return token;
    try {
      Aead aead = KEYSET_HANDLE.getPrimitive(RegistryConfiguration.get(), Aead.class);
      byte[] ciphertext = aead.encrypt(token.getBytes(StandardCharsets.UTF_8), null);
      return PREFIX_AMV + Base64.getEncoder().encodeToString(ciphertext);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static String decrypt(String encryptedToken) {
    if (StringUtils.isEmpty(encryptedToken)) return encryptedToken;
    try {
      Aead daead = KEYSET_HANDLE.getPrimitive(RegistryConfiguration.get(), Aead.class);
      return new String(
          daead.decrypt(
              Base64.getDecoder()
                  .decode(encryptedToken.substring(PREFIX_AMV.length(), encryptedToken.length())),
              null));
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }
}
