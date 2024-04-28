// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.git.jumptobrowse.i18n;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;
import com.intellij.DynamicBundle;

public final class GitJumpToBrowseBundle {
  public static final @NonNls String BUNDLE = "messages.GitJumpToBrowseBundle";
  private static final DynamicBundle INSTANCE = new DynamicBundle(GitJumpToBrowseBundle.class, BUNDLE);

  private GitJumpToBrowseBundle() {}

  public static @NotNull @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
    return INSTANCE.getMessage(key, params);
  }

  public static @NotNull Supplier<@Nls String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
    return INSTANCE.getLazyMessage(key, params);
  }

  /**
   * @deprecated prefer {@link #message(String, Object...)} instead
   */
  @Deprecated
  public static @NotNull @Nls String getString(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key) {
    return message(key);
  }
}