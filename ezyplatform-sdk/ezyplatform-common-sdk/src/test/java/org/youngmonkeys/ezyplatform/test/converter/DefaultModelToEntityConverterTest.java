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

package org.youngmonkeys.ezyplatform.test.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.AccessTokenStatus;
import org.youngmonkeys.ezyplatform.entity.LetterReceiver;
import org.youngmonkeys.ezyplatform.entity.NotificationReceiver;
import org.youngmonkeys.ezyplatform.entity.UserAccessToken;
import org.youngmonkeys.ezyplatform.model.AddLetterModel;
import org.youngmonkeys.ezyplatform.model.AddNotificationModel;
import org.youngmonkeys.ezyplatform.time.ClockProxy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class DefaultModelToEntityConverterTest {

    private ClockProxy clock;

    private DefaultModelToEntityConverter sut;

    @BeforeMethod
    public void setup() {
        clock = mock(ClockProxy.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        sut = new DefaultModelToEntityConverter(clock, objectMapper);
    }

    @Test
    public void toLetterReceiverEntitiesUserIdAndAdminId() {
        // given
        long userId = RandomUtil.randomLong(1L, 10L);
        long adminId = RandomUtil.randomLong(1L, 10L);
        AddLetterModel model = AddLetterModel.builder()
            .toUserId(userId)
            .toAdminId(adminId)
            .build();

        LocalDateTime now = LocalDateTime.now();
        when(clock.nowDateTime()).thenReturn(now);

        // when
        long letterId = RandomUtil.randomLong();
        List<LetterReceiver> actual = sut.toEntities(letterId, model);

        // then
        LetterReceiver entity1 = new LetterReceiver();
        entity1.setLetterId(letterId);
        entity1.setToAdminId(adminId);
        entity1.setSentAt(now);

        LetterReceiver entity2 = new LetterReceiver();
        entity2.setLetterId(letterId);
        entity2.setToUserId(userId);
        entity2.setSentAt(now);

        Asserts.assertEquals(
            actual,
            Arrays.asList(entity1, entity2),
            false
        );

        verify(clock, times(1)).nowDateTime();
    }

    @Test
    public void toLetterReceiverEntitiesWithoutUserIdAndAdminId() {
        // given
        AddLetterModel model = AddLetterModel.builder()
            .build();

        // when
        long letterId = RandomUtil.randomLong();
        List<LetterReceiver> actual = sut.toEntities(letterId, model);

        // then
        Asserts.assertEmpty(actual);
    }

    @Test
    public void toNotificationReceiverEntitiesUserIdAndAdminId() {
        // given
        long userId = RandomUtil.randomLong(1L, 10L);
        long adminId = RandomUtil.randomLong(1L, 10L);
        AddNotificationModel model = AddNotificationModel.builder()
            .toUserId(userId)
            .toAdminId(adminId)
            .build();

        LocalDateTime now = LocalDateTime.now();
        when(clock.nowDateTime()).thenReturn(now);

        // when
        long letterId = RandomUtil.randomLong();
        List<NotificationReceiver> actual = sut.toEntities(letterId, model);

        // then
        NotificationReceiver entity1 = new NotificationReceiver();
        entity1.setNotificationId(letterId);
        entity1.setToAdminId(adminId);
        entity1.setSentAt(now);

        NotificationReceiver entity2 = new NotificationReceiver();
        entity2.setNotificationId(letterId);
        entity2.setToUserId(userId);
        entity2.setSentAt(now);

        Asserts.assertEquals(
            actual,
            Arrays.asList(entity1, entity2),
            false
        );

        verify(clock, times(1)).nowDateTime();
    }

    @Test
    public void toNotificationReceiverEntitiesWithoutUserIdAndAdminId() {
        // given
        AddNotificationModel model = AddNotificationModel.builder()
            .build();

        // when
        long letterId = RandomUtil.randomLong();
        List<NotificationReceiver> actual = sut.toEntities(letterId, model);

        // then
        Asserts.assertEmpty(actual);
    }

    @Test
    public void toUserAccessTokenEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        when(clock.nowDateTime()).thenReturn(now);

        long userId = RandomUtil.randomLong();
        String accessToken = RandomUtil.randomShortAlphabetString();
        long tokenExpiredTime = RandomUtil.randomSmallInt();

        // when
        UserAccessToken actual = sut.toUserAccessTokenEntity(
            userId,
            accessToken,
            tokenExpiredTime,
            TimeUnit.MINUTES,
            AccessTokenStatus.ACTIVATED
        );

        // then
        UserAccessToken userAccessToken = new UserAccessToken();
        userAccessToken.setId(accessToken);
        userAccessToken.setUserId(userId);
        userAccessToken.setStatus(AccessTokenStatus.ACTIVATED);
        userAccessToken.setCreatedAt(now);
        userAccessToken.setExpiredAt(now.plusSeconds(tokenExpiredTime * 60));
        Asserts.assertEquals(actual, userAccessToken);
    }
}
