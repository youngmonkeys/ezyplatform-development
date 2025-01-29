package org.youngmonkeys.ezyplatform.result;

import com.tvd12.ezyfox.database.annotation.EzyQueryResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EzyQueryResult
public class UpdatedAtValueResult {
    private LocalDateTime updatedAt;

    public static LocalDateTime updatedAtOrNull(
        UpdatedAtValueResult result
    ) {
        return result != null
            ? result.getUpdatedAt()
            : null;
    }
}
