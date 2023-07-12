package org.iceanarchy.core.common.boiler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Data
public class VerifiedPlayer {

    private final String name;
    private final UUID uniqueId;
}
