/*
 * Copyright 2022 youngmonkeys.org
 * 
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.rx;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyStrings.exceptionsToString;
import static com.tvd12.ezyfox.io.EzyStrings.traceStackToString;

@Getter
public class RxException extends RuntimeException {
    private static final long serialVersionUID = -8667468848025369677L;

    private final List<Exception> exceptions;

    public RxException(Exception exception) {
        super(traceStackToString(exception));
        this.exceptions = Collections.singletonList(exception);
    }

    public RxException(List<Exception> exceptions) {
        super(exceptionsToString(exceptions));
        this.exceptions = exceptions;
    }
}
