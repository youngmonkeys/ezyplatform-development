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

package org.youngmonkeys.ezyplatform.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionFunctions {

    private CollectionFunctions() {}

    public static <E, C extends Collection<E>> C toNullIfEmpty(
        C collection
    ) {
        return collection == null || !collection.isEmpty()
            ? collection
            : null;
    }

    public static <E> Collection<E> toEmptyIfNull(
        Collection<E> collection
    ) {
        return collection == null
            ? Collections.emptyList()
            : collection;
    }

    public static <E, V> List<E> distinctByField(
        Collection<E> collection,
        Function<E, V> fieldValueExtractor
    ) {
        Set<V> distinctValues = new HashSet<>();
        List<E> newList = new ArrayList<>();
        for (E item : collection) {
            V fieldValue = fieldValueExtractor.apply(item);
            if (!distinctValues.contains(fieldValue)) {
                distinctValues.add(fieldValue);
                newList.add(item);
            }
        }
        return newList;
    }

    public static List<String> normalizeList(List<String> value) {
        if (value == null) {
            return Collections.emptyList();
        }
        return value
            .stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(it -> !it.isEmpty())
            .collect(Collectors.toList());
    }
}
