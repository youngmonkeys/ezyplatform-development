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

package org.youngmonkeys.ezyplatform.test.rx;

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxOperation;

public class ReactiveExample {

    @AllArgsConstructor
    public static class UserInfo {
        private int balance;
        private int visitCount;
    }

    @AllArgsConstructor
    public static class UserController {

        private final UserBalanceService userBalanceService;
        private final UserVisitCountService userVisitCountService;

        public UserInfo getUserInfo() {
            return Reactive.multiple()
                .registerRx(
                    "balance",
                    userBalanceService.sumUserBalance()
                )
                .registerRx(
                    "visitCount",
                    userVisitCountService.sumUserVisitCount()
                )
                .blockingGet(map ->
                    new UserInfo(
                        map.get("balance"),
                        map.get("visitCount")
                    )
                );
        }
    }

    public static class UserBalanceService {
        public RxOperation sumUserBalance() {
            return Reactive.multiple()
                .register(
                    "valueFromDatabase",
                    this::getBalanceFromDatabase
                )
                .register(
                    "valueFromService",
                    this::getBalanceFromMicroService
                )
                .mapBegin(map ->
                    map.get("valueFromDatabase", 0)
                        + map.get("valueFromService", 0)
                );
        }

        private int getBalanceFromDatabase() {
            return 1;
        }

        private int getBalanceFromMicroService() {
            return 2;
        }
    }

    public static class UserVisitCountService {
        public RxOperation sumUserVisitCount() {
            return Reactive.multiple()
                .register(
                    "visitCountFromDatabase",
                    this::getVisitCountFromDatabase
                )
                .register(
                    "visitCountFromService",
                    this::getVisitCountFromMicroService
                )
                .mapBegin(map ->
                    map.get("visitCountFromDatabase", 0)
                        + map.get("visitCountFromService", 0)
                );
        }

        private int getVisitCountFromDatabase() {
            return 1;
        }

        private int getVisitCountFromMicroService() {
            return 2;
        }
    }

    public static void main(String[] args) {
        UserController controller = new UserController(
            new UserBalanceService(),
            new UserVisitCountService()
        );
        UserInfo info = controller.getUserInfo();
    }
}
