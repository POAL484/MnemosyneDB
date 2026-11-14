package org.mnemosynehermesteam.core;

import java.util.UUID;

public record FullKey (
  UUID userId,
  String key
){}
