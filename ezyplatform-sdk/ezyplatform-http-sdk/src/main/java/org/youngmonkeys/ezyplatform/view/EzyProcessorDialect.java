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

package org.youngmonkeys.ezyplatform.view;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.collect.Sets;
import lombok.Setter;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.youngmonkeys.ezyplatform.manager.EnvironmentManager;

import java.util.Set;

@Setter
public class EzyProcessorDialect extends AbstractProcessorDialect {

    @EzyAutoBind
    private EnvironmentManager environmentManager;

    private static final String DIALECT_NAME = "EzyPlatform Dialect";
    private static final String PREFIX = "ezy";
    private static final String ATTRIBUTE_NAME_HREF = "href";
    private static final String ATTRIBUTE_NAME_SRC = "src";

    public EzyProcessorDialect() {
        super(
            DIALECT_NAME,
            PREFIX,
            StandardDialect.PROCESSOR_PRECEDENCE
        );
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return Sets.newHashSet(
            new VersionalLinkAttributeTagProcessor(
                dialectPrefix,
                ATTRIBUTE_NAME_HREF,
                environmentManager
            ),
            new VersionalLinkAttributeTagProcessor(
                dialectPrefix,
                ATTRIBUTE_NAME_SRC,
                environmentManager
            )
        );
    }
}
