package com.fast.lib.logger;

public interface FormatStrategy {

  void log(int priority, String tag, String message);
}
