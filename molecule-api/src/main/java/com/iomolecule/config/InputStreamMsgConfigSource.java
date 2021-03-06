/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iomolecule.config;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * A simple InputStream based MsgConfigSource Implementation.
 * This implementation allows multiple input streams to be composed as a singe message configuration source instance.
 *
 * The current implementation only supports 'JSON' streams.
 * Internally the input streams are loaded as JSON nodes and they are logically merged in to a singe JSON node tree.
 * The order of merge is as per the order of the input streams.
 */
@Slf4j
public class InputStreamMsgConfigSource extends InputStreamConfigurationSource implements MsgConfigSource{


    public InputStreamMsgConfigSource(boolean allowNullStreams, boolean throwErrorOnMismatchedTypes, InputStream... inputStreams) {
        super(allowNullStreams, throwErrorOnMismatchedTypes, inputStreams);
    }
}
