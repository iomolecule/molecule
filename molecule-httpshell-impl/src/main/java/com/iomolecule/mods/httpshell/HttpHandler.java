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

package com.iomolecule.mods.httpshell;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class HttpHandler implements io.undertow.server.HttpHandler{

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        log.debug("Received Request for {}",httpServerExchange.getRequestURI());
        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE,"text/plain");
        httpServerExchange.getResponseSender().send("Hello from iomolecule!!");
    }
}
