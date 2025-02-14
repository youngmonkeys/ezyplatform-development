/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyhttp.server.core.ApplicationContext;
import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;
import com.tvd12.ezyhttp.server.core.annotation.PropertiesSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ComponentsScan({
    "org.youngmonkeys.ezyplatform"
})
@PropertiesSources({
    "config.properties",
    "settings/platform.properties",
    "settings/setup.properties"
})
public class IntegrationTestRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        IntegrationTestRunner.class
    );

    @SuppressWarnings({"unchecked", "CallToPrintStackTrace"})
    public static void run(Class<?> bootstrapClass) throws Exception {
        EzyHttpApplication application = EzyHttpApplication
            .start(
                bootstrapClass,
                IntegrationTestRunner.class
            );
        ApplicationContext context = application.getApplicationContext();
        EzyBeanContext beanContext = context.getBeanContext();
        List<IntegrationTest> its = beanContext.getSingletonsOf(IntegrationTest.class);
        try {
            its.forEach(it -> {
                String testName = it.getClass().getSimpleName();
                LOGGER.info("Start integration test: {}", testName);
                long startTime = System.currentTimeMillis();
                it.test();
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                LOGGER.info(
                    "Finish integration test: {}, elapsed time: {}ms ({}s)",
                    testName,
                    elapsedTime,
                    BigDecimal
                        .valueOf(elapsedTime)
                        .divide(
                            BigDecimal.valueOf(1000),
                            3,
                            RoundingMode.HALF_UP
                        )
                );
            });
            System.exit(0);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
