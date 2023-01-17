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

package org.youngmonkeys.ezyplatform.service;

import com.tvd12.ezyhttp.server.core.annotation.Service;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.Letter;
import org.youngmonkeys.ezyplatform.entity.LetterReceiver;
import org.youngmonkeys.ezyplatform.model.AddLetterModel;
import org.youngmonkeys.ezyplatform.model.AddLetterReceiverModel;
import org.youngmonkeys.ezyplatform.repo.LetterReceiverRepository;
import org.youngmonkeys.ezyplatform.repo.LetterRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class DefaultLetterService implements LetterService {

    private final LetterRepository letterRepository;
    private final LetterReceiverRepository letterReceiverRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public long addLetter(AddLetterModel model) {
        Letter letter = modelToEntityConverter.toEntity(model);
        letterRepository.save(letter);
        List<LetterReceiver> letterReceivers = modelToEntityConverter.toEntities(
            letter.getId(),
            model
        );
        if (letterReceivers.size() > 0) {
            letterReceiverRepository.save(letterReceivers);
        }
        return letter.getId();
    }

    @Override
    public long addLetterReceiver(AddLetterReceiverModel model) {
        LetterReceiver entity = modelToEntityConverter.toEntity(model);
        letterReceiverRepository.save(entity);
        return entity.getId();
    }
}
