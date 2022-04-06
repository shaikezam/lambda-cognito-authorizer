package com.shaizambrovski.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TenantRepository {

    private static Map<String, UUID> tenantIdToClientId = new HashMap<>();

    static {
        tenantIdToClientId.put("6m3vt1rk32ceolmf6htffdq1r4", UUID.fromString("4f45dc62-3551-448e-918e-95c5fb9ecaac"));
        //tenantIdToClientId.put("1c69j5cpjv19p9gm5ueeh93qr2", UUID.fromString("85ff8b45-0242-4bc6-bb1e-732ae952d2fe"));
    }

    public UUID getTenantIdByClientId(String clientId) {
        return tenantIdToClientId.get(clientId);
    }
}
