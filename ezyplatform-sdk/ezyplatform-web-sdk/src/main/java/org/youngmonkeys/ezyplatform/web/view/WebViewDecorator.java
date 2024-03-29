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

package org.youngmonkeys.ezyplatform.web.view;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyhttp.server.core.util.HttpServletRequests;
import com.tvd12.ezyhttp.server.core.view.View;
import lombok.Setter;
import org.youngmonkeys.ezyplatform.model.MediaNameModel;
import org.youngmonkeys.ezyplatform.model.UserModel;
import org.youngmonkeys.ezyplatform.service.MediaService;
import org.youngmonkeys.ezyplatform.service.UserService;
import org.youngmonkeys.ezyplatform.web.annotation.UserId;

import javax.servlet.http.HttpServletRequest;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ACCESS_TOKEN;

@Setter
public class WebViewDecorator extends WebViewLanguageDecorator {

    @EzyAutoBind
    private UserService userService;

    @EzyAutoBind
    private MediaService mediaService;

    @Override
    public void decorate(HttpServletRequest request, View view) {
        super.decorate(request, view);
        setUserData(request, view);
    }

    protected void setUserData(HttpServletRequest request, View view) {
        Long userId = (Long) request.getAttribute(UserId.class.getName());
        if (userId == null) {
            String accessToken = HttpServletRequests.getRequestValue(
                request,
                COOKIE_NAME_ACCESS_TOKEN
            );
            if (isNotBlank(accessToken)) {
                userId = userService.getUserIdByAccessToken(accessToken);
            }
        }
        if (userId == null) {
            return;
        }
        view.setVariable("userId", userId);
        UserModel user = view.getVariable("user");
        if (user == null) {
            user = userService.getUserById(userId);
        }
        if (user != null) {
            view.setVariable("user", user);
            view.setVariable("userUuid", user.getUuid());
            view.setVariable("loggedIn", true);
            
            MediaNameModel avatarImage = view.getVariable("avatarImage");
            if (avatarImage == null) {
                avatarImage = mediaService.getMediaNameById(
                    user.getAvatarImageId()
                );
                if (avatarImage == null) {
                    avatarImage = MediaNameModel.builder().build();
                }
                view.setVariable("avatarImage", avatarImage);
            }
            decorateWithUserData(request, view, user);
        }
    }

    protected void decorateWithUserData(
        HttpServletRequest request,
        View view,
        UserModel user
    ) {}
}
