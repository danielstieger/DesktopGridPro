package org.modellwerkstatt.addons.desktopgridpro.sample;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;

@Theme("testtheme")
public class MyShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        AppShellConfigurator.super.configurePage(settings);
    }
}
