package cn.kuzuanpa.organapi.api.install;

import net.minecraft.network.chat.Component;

public record OrganInstallResult(boolean success, Component message) {
    public static OrganInstallResult success(Component message) {
        return new OrganInstallResult(true, message);
    }

    public static OrganInstallResult fail(Component message) {
        return new OrganInstallResult(false, message);
    }
}
