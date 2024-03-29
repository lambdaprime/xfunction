/*
 * Copyright 2020 lambdaprime
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
module id.xfunction {
    requires java.net.http;
    requires java.xml;
    requires java.logging;
    requires java.management;

    exports id.xfunction;
    exports id.xfunction.cli;
    exports id.xfunction.concurrent;
    exports id.xfunction.concurrent.flow;
    exports id.xfunction.function;
    exports id.xfunction.io;
    exports id.xfunction.lang;
    exports id.xfunction.lang.invoke;
    exports id.xfunction.logging;
    exports id.xfunction.net;
    exports id.xfunction.nio.file;
    exports id.xfunction.text;
    exports id.xfunction.util;
    exports id.xfunction.util.stream;
    exports id.xfunction.retry;
}
