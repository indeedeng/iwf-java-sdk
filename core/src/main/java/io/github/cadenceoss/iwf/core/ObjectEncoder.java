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

public interface ObjectEncoder {

    /**
     * Each {@link ObjectEncoder} has an Encoding Type that it handles.
     *
     * @return encoding type that this converter handles.
     */
    String getEncodingType();

    /**
     * Implements conversion of a list of values.
     *
     * @param value Java value to convert.
     * @return converted value
     * Note that it will ObjectEncoderException if conversion of the value passed as parameter failed for any
     * reason.
     * @see #getEncodingType() getEncodingType javadoc for an important implementation detail
     */
    String toData(Object value);

    /**
     * Implements conversion of a single value.
     *
     * @param content          Serialized value to convert to a Java object.
     * @param valueType        type of the value stored in the {@code content}
     * @return converted Java object
     * Note that it will ObjectEncoderException if conversion of the data passed as parameter failed for any
     *                                reason.
     */
    <T> T fromData(String content, Class<T> valueType);
}