package com.fast.lib.logger;

public interface LogStrategy {

  void log(int priority, String tag, String message);
}
