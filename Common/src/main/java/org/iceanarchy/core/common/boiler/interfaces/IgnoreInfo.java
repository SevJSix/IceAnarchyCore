package org.iceanarchy.core.common.boiler.interfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public interface IgnoreInfo {

    HashMap<UUID, List<UUID>> ignoreMap = new HashMap<>();
}
