/*
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.edible.ocr;

/**
 * Class to hold metadata for failed OCR results.
 */
public final class OcrResultFailure {
  private final long timeRequired;
  private final long timestamp;
  
  OcrResultFailure(long timeRequired) {
    this.timeRequired = timeRequired;
    this.timestamp = System.currentTimeMillis();
  }
  
  public long getTimeRequired() {
    return timeRequired;
  }
  
  public long getTimestamp() {
    return timestamp;
  }
  
  @Override
  public String toString() {
    return timeRequired + " " + timestamp;
  }
}
