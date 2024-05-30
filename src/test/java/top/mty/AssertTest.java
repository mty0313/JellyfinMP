package top.mty;

import org.junit.Test;
import top.mty.common.Assert;

public class AssertTest {

  @Test
  public void testAssert0() {
    String testString = null;
    Assert.notEmpty(testString, "testString");
  }
}
