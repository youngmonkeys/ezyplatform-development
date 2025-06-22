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

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.templatemode.TemplateMode;
import org.youngmonkeys.ezyplatform.manager.EnvironmentManager;

import static org.thymeleaf.standard.expression.StandardExpressions.getExpressionParser;

public class VersionalLinkAttributeTagProcessor extends AbstractAttributeTagProcessor {

    private final String originalAttributeName;
    private final EnvironmentManager environmentManager;

    private static final String PREFIX = "ezy";

    public VersionalLinkAttributeTagProcessor(
        String originalAttributeName,
        EnvironmentManager environmentManager
    ) {
        super(
            TemplateMode.HTML,
            PREFIX,
            null,
            false,
            "v" + originalAttributeName,
            true,
            Integer.MIN_VALUE,
            true
        );
        this.environmentManager = environmentManager;
        this.originalAttributeName = originalAttributeName;
    }

    @Override
    protected void doProcess(
        ITemplateContext context,
        IProcessableElementTag tag,
        AttributeName attributeName,
        String attributeValue,
        IElementTagStructureHandler structureHandler
    ) {
        Object version = context.getVariable("ezyResourceVersion");
        if (version == null) {
            version = environmentManager.getServerStartTime();
        }
        String actualAttributeName = attributeValue;
        if (attributeValue.startsWith("${")) {
            IEngineConfiguration configuration = context.getConfiguration();
            IStandardExpressionParser parser = getExpressionParser(configuration);
            IStandardExpression expression = parser.parseExpression(context, attributeValue);
            actualAttributeName = (String) expression.execute(context);
        }
        structureHandler.setAttribute(
            originalAttributeName,
            actualAttributeName + "?v=" + version
        );
    }
}
