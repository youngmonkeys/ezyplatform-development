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

package org.youngmonkeys.devtools.test.controller;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyhttp.server.core.annotation.*;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import com.tvd12.ezyhttp.server.core.view.View;
import lombok.AllArgsConstructor;
import org.youngmonkeys.devtools.test.request.MyBodyRequest;
import org.youngmonkeys.devtools.test.request.MyRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller("/")
@AllArgsConstructor
public class ViewController {

    @EzyAutoBind
    EzyBeanContext beanContext;

    @DoGet("/home")
    public View home() {
        return View.of("home");
    }

    @Api
    @DoPost("/hello/{world}/foo/{bar}")
    public List<List<Long>> helloWorldFooBarPost(
        @PathVariable String world,
        @PathVariable String bar,
        @RequestParam List<String> ids,
        @RequestParam List<List<String>> values,
        @RequestParam Map<String, String> param1,
        @RequestParam MyRequest param2,
        @RequestBody MyBodyRequest request
    ) {
        return Collections.emptyList();
    }

    @Api
    @DoPost("/hello/world/foo/bar/one")
    public List<List<Long>> helloWorldFooBarOnePost(
        @RequestBody List<MyBodyRequest> request
    ) {
        return Collections.emptyList();
    }

    @Api
    @DoPost("/hello/world/foo/bar/two")
    public List<List<Long>> helloWorldFooBarTwoPost(
        @RequestBody List<Long> request
    ) {
        return Collections.emptyList();
    }

    @Api
    @DoPost("/hello/world/foo/bar/three")
    public List<List<Long>> helloWorldFooBarThreePost(
        @RequestBody List<List<Long>> request
    ) {
        return Collections.emptyList();
    }

    @Api
    @DoPost("/hello/world/foo/bar/four")
    public Redirect helloWorldFooBarFourPost() {
        return Redirect.to("");
    }

    @Api
    @DoPost("/hello/world/foo/bar/five")
    public long helloWorldFooBarFivePost(
        @RequestBody int request
    ) {
        return 0;
    }
}
