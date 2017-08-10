package com.example.parkingwizard.dagger.components;

import com.example.parkingwizard.dagger.modules.AppModule;
import com.example.parkingwizard.dagger.modules.NetworkModule;
import com.example.parkingwizard.dagger.modules.PresenterModule;
import com.example.parkingwizard.ui.screen_map.MapScreenPresenterImpl;
import com.example.parkingwizard.ui.screen_map.MapsActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, PresenterModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(MapsActivity activity);

    void inject(MapScreenPresenterImpl presenterImpl);
}