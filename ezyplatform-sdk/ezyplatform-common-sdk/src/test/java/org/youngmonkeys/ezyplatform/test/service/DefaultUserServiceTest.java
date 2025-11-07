/*
 * Copyright 2023 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.service;

import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.devtools.InstanceRandom;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultResultToModelConverter;
import org.youngmonkeys.ezyplatform.entity.AccessTokenStatus;
import org.youngmonkeys.ezyplatform.entity.UserAccessToken;
import org.youngmonkeys.ezyplatform.repo.UserAccessTokenRepository;
import org.youngmonkeys.ezyplatform.repo.UserRepository;
import org.youngmonkeys.ezyplatform.service.DefaultUserService;
import org.youngmonkeys.ezyplatform.service.UserAccessTokenService;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class DefaultUserServiceTest {
    private ClockProxy clockProxy;
    private UserAccessTokenService userAccessTokenService;
    private UserRepository userRepository;
    private UserAccessTokenRepository userAccessTokenRepository;
    private DefaultEntityToModelConverter defaultEntityToModelConverter;
    private DefaultModelToEntityConverter defaultModelToEntityConverter;
    private DefaultResultToModelConverter defaultResultToModelConverter;
    private DefaultUserService instance;

    private final InstanceRandom instanceRandom =
        new InstanceRandom();

    @BeforeMethod
    public void setup() {
        clockProxy = mock(ClockProxy.class);
        userAccessTokenService = mock(UserAccessTokenService.class);
        userRepository = mock(UserRepository.class);
        userAccessTokenRepository = mock(UserAccessTokenRepository.class);
        defaultEntityToModelConverter = mock(DefaultEntityToModelConverter.class);
        defaultResultToModelConverter = mock(DefaultResultToModelConverter.class);
        instance = new DefaultUserService(
            clockProxy,
            userAccessTokenService,
            userRepository,
            userAccessTokenRepository,
            defaultEntityToModelConverter,
            defaultModelToEntityConverter,
            defaultResultToModelConverter
        );
    }

    @AfterMethod
    public void verifyAll() {
        verifyNoMoreInteractions(
            clockProxy,
            userAccessTokenService,
            userRepository,
            userAccessTokenRepository,
            defaultEntityToModelConverter,
            defaultResultToModelConverter
        );
    }

    @Test
    public void validateUserAccessTokenSuccessTest() {
        // given
        long userId = RandomUtil.randomLong(1L, Long.MAX_VALUE);
        String accessToken = RandomUtil.randomShortAlphabetString();

        LocalDateTime now = LocalDateTime.now();
        when(clockProxy.nowDateTime()).thenReturn(now);

        UserAccessToken userAccessToken = instanceRandom
            .randomObject(UserAccessToken.class);
        userAccessToken.setUserId(userId);
        userAccessToken.setId(accessToken);
        userAccessToken.setStatus(AccessTokenStatus.ACTIVATED);
        userAccessToken.setExpiredAt(now.plusDays(1));
        when(
            userAccessTokenRepository.findById(accessToken)
        ).thenReturn(userAccessToken);

        when(
            userAccessTokenService.extractUserId(accessToken)
        ).thenReturn(userId);

        // when
        long actual = instance.validateUserAccessToken(accessToken);

        // then
        Asserts.assertEquals(actual, userId);

        verify(clockProxy, times(1)).nowDateTime();

        verify(
            userAccessTokenRepository, times(1)
        ).findById(accessToken);

        verify(
            userAccessTokenService, times(1)
        ).extractUserId(accessToken);
    }
}
