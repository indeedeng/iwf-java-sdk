/*
 * Copyright (C) 2022 Temporal Technologies, Inc. All Rights Reserved.
 *
 * Copyright (C) 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this material except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.cadenceoss.iwf.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JacksonJsonObjectEncoder implements ObjectEncoder {

  private final ObjectMapper mapper;

  public JacksonJsonObjectEncoder() {
    mapper = new ObjectMapper();
    // preserve the original value of timezone coming from the server in Payload
    // without adjusting to the host timezone
    // may be important if the replay is happening on a host in another timezone
    mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new Jdk8Module());
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  public JacksonJsonObjectEncoder(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public String getEncodingType() {
    return "BuiltinJacksonJson";
  }

  @Override
  public String toData(Object value) {
    try {
      return mapper.writeValueAsString(value);

    } catch (JsonProcessingException e) {
      throw new ObjectEncoderException(e);
    }
  }

  @Override
  public <T> T fromData(String content, Class<T> valueClass) {
    if (content.isEmpty()) {
      return null;
    }
    try {
      @SuppressWarnings("deprecation")
      JavaType reference = mapper.getTypeFactory().constructType(valueClass, valueClass);
      return mapper.readValue(content, reference);
    } catch (IOException e) {
      throw new ObjectEncoderException(e);
    }
  }
}
