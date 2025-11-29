package dev.aulait.amv.arch.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.file.FileUtils;
import org.junit.jupiter.api.Test;

class SecurityUtilsTests {

  @Test
  void testCryptToken() {
    String token = "test";

    // Perform encryption
    String encryptToken = SecurityUtils.encrypt(token);
    assertNotEquals(token, encryptToken);
    assertTrue(encryptToken.startsWith("amvenc:"));

    // Perform decryption
    String decryptToken = SecurityUtils.decrypt(encryptToken);
    assertEquals(token, decryptToken);

    FileUtils.delete(DirectoryManager.SECURITY_ROOT.resolve("encrypted-keyset.json"));
  }
}
