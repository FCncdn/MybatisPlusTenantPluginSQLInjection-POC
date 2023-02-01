package com.example.demo.common;

public class TenantHolder {
    private static final InheritableThreadLocal<String> CONTEXT_HOLDERS = new InheritableThreadLocal<String>();

    public static final String REQUEST_TID = "tid";

    /**
     * 获取租户ID
     *
     * @return
     */
    public static String getTenantId() {
        return (String) CONTEXT_HOLDERS.get();
    }

    /**
     * 设置租户ID
     *
     * @param tenantId
     */
    public static void setTenantId(String tenantId) {
        CONTEXT_HOLDERS.set(tenantId);
    }
}
