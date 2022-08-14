package org.youngmonkeys.ezyplatform.service;

import com.tvd12.ezyfox.util.Next;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.entity.UserMeta;
import org.youngmonkeys.ezyplatform.repo.UserMetaRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;

@AllArgsConstructor
public class DefaultUserMetaService implements UserMetaService {

    private final UserMetaRepository userMetaRepository;

    @Override
    public void saveUserMeta(
        long userId,
        String metaKey,
        String metaValue
    ) {
        UserMeta entity = new UserMeta();
        entity.setUserId(userId);
        entity.setMetaKey(metaKey);
        entity.setMetaValue(metaValue);
        userMetaRepository.save(entity);
    }

    @Override
    public void saveUserMeta(
        long userId,
        String metaKey,
        List<String> metaValues
    ) {
        List<UserMeta> entities = newArrayList(
            metaValues,
            metaValue -> {
                UserMeta entity = new UserMeta();
                entity.setUserId(userId);
                entity.setMetaKey(metaKey);
                entity.setMetaValue(metaValue);
                return entity;
            }
        );
        userMetaRepository.save(entities);
    }

    @Override
    public boolean containsUserMeta(
        long userId,
        String metaKey,
        String metaValue
    ) {
        return userMetaRepository.findByUserIdAndMetaKeyAndMetaValue(
            userId,
            metaKey,
            metaValue
        ).isPresent();
    }

    @Override
    public long getUserIdByMeta(
        String metaKey,
        String metaValue
    ) {
        return userMetaRepository.findByMetaKeyAndMetaValue(
            metaKey,
            metaValue
        )
            .map(UserMeta::getUserId)
            .orElse(0L);
    }

    @Override
    public String getMetaValueByUserIdAndMetaKey(
        long userId,
        String metaKey
    ) {
        return userMetaRepository.findByUserIdAndMetaKey(
            userId,
            metaKey
        )
            .map(UserMeta::getMetaValue)
            .orElse(null);
    }

    @Override
    public List<String> getMetaValuesByUserIdAndMetaKey(
        long userId,
        String metaKey,
        int limit
    ) {
        return newArrayList(
            userMetaRepository.findByUserIdAndMetaKey(
                userId,
                metaKey,
                Next.limit(limit)
            ),
            UserMeta::getMetaValue
        );
    }

    @Override
    public Map<String, String> getUserMetaValues(long userId) {
        return userMetaRepository.findByUserId(
            userId
        )
            .stream()
            .collect(
                Collectors.toMap(
                    UserMeta::getMetaKey,
                    UserMeta::getMetaValue
                )
            );
    }
}
