/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.fetcher;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.TemplateParametersSchema;
import org.youngmonkeys.ezyplatform.fetcher.TemplateParametersSchemaFetcher;
import org.youngmonkeys.ezyplatform.fetcher.TemplateParametersSchemaFetcherManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemplateParametersSchemaFetcherManagerTest {

    @Test
    public void shouldGetFetcherFromSingletonFactory() {
        // given
        TestTemplateParametersSchemaFetcher emailResetPasswordFetcher =
            new TestTemplateParametersSchemaFetcher(
                "email",
                "reset-password",
                "Reset password email"
            );
        TestTemplateParametersSchemaFetcher smsResetPasswordFetcher =
            new TestTemplateParametersSchemaFetcher(
                "sms",
                "reset-password",
                "Reset password SMS"
            );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(TemplateParametersSchemaFetcher.class))
            .thenReturn(Arrays.asList(
                emailResetPasswordFetcher,
                smsResetPasswordFetcher
            ));

        TemplateParametersSchemaFetcherManager manager =
            new TemplateParametersSchemaFetcherManager(singletonFactory);

        // when
        TemplateParametersSchemaFetcher actualFetcher =
            manager.getTemplateParametersSchemaFetcherByTemplateTypeAndTemplateName(
                "email",
                "reset-password"
            );
        TemplateParametersSchemaFetcher missingFetcher =
            manager.getTemplateParametersSchemaFetcherByTemplateTypeAndTemplateName(
                "email",
                "welcome"
            );

        // then
        Asserts.assertEquals(actualFetcher, emailResetPasswordFetcher);
        Asserts.assertEquals(
            actualFetcher.getSchema().getDescription(),
            "Reset password email"
        );
        Asserts.assertNull(missingFetcher);
    }

    @Test
    public void shouldGetAdditionalFetcherAndReturnAllFetchers() {
        // given
        TestTemplateParametersSchemaFetcher factoryFetcher =
            new TestTemplateParametersSchemaFetcher(
                "email",
                "welcome",
                "Welcome email"
            );
        TestTemplateParametersSchemaFetcher additionalFetcher =
            new TestTemplateParametersSchemaFetcher(
                "push",
                "new-post",
                "New post push"
            );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(TemplateParametersSchemaFetcher.class))
            .thenReturn(Collections.singletonList(factoryFetcher));

        TemplateParametersSchemaFetcherManager manager =
            new TemplateParametersSchemaFetcherManager(singletonFactory);

        // when
        manager.addTemplateParametersSchemaFetcher(additionalFetcher);
        TemplateParametersSchemaFetcher actualAdditionalFetcher =
            manager.getTemplateParametersSchemaFetcherByTemplateTypeAndTemplateName(
                "push",
                "new-post"
            );
        List<TemplateParametersSchemaFetcher> actualFetchers =
            manager.getTemplateParametersSchemaFetchers();

        // then
        Asserts.assertEquals(actualAdditionalFetcher, additionalFetcher);
        Asserts.assertEquals(actualFetchers.size(), 2);
        Asserts.assertTrue(actualFetchers.contains(factoryFetcher));
        Asserts.assertTrue(actualFetchers.contains(additionalFetcher));
    }

    @Test
    public void shouldUseLastFactoryFetcherWhenDuplicated() {
        // given
        TestTemplateParametersSchemaFetcher oldFetcher =
            new TestTemplateParametersSchemaFetcher(
                "email",
                "welcome",
                "Old welcome email"
            );
        TestTemplateParametersSchemaFetcher newFetcher =
            new TestTemplateParametersSchemaFetcher(
                "email",
                "welcome",
                "New welcome email"
            );

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(TemplateParametersSchemaFetcher.class))
            .thenReturn(Arrays.asList(oldFetcher, newFetcher));

        TemplateParametersSchemaFetcherManager manager =
            new TemplateParametersSchemaFetcherManager(singletonFactory);

        // when
        TemplateParametersSchemaFetcher actualFetcher =
            manager.getTemplateParametersSchemaFetcherByTemplateTypeAndTemplateName(
                "email",
                "welcome"
            );

        // then
        Asserts.assertEquals(actualFetcher, newFetcher);
        Asserts.assertEquals(
            actualFetcher.getSchema().getDescription(),
            "New welcome email"
        );
    }

    private static final class TestTemplateParametersSchemaFetcher
        implements TemplateParametersSchemaFetcher {

        private final String templateType;
        private final String templateName;
        private final TemplateParametersSchema schema;

        private TestTemplateParametersSchemaFetcher(
            String templateType,
            String templateName,
            String description
        ) {
            this.templateType = templateType;
            this.templateName = templateName;
            this.schema = TemplateParametersSchema.builder()
                .description(description)
                .build();
        }

        @Override
        public TemplateParametersSchema getSchema() {
            return schema;
        }

        @Override
        public String getTemplateType() {
            return templateType;
        }

        @Override
        public String getTemplateName() {
            return templateName;
        }
    }
}
