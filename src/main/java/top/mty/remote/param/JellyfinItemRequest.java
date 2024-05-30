package top.mty.remote.param;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class JellyfinItemRequest {
  boolean enableTotalRecordCount = true;

  boolean enableImages = false;

  List<String> ids = new ArrayList<>();

  public JellyfinItemRequest(String... ids) {
    this.ids.addAll(Arrays.asList(ids));
  }
}
