package com.example.parkingwizard.dagger.modules;

import android.content.Context;

import com.example.parkingwizard.ui.screen_map.MapScreenPresenter;
import com.example.parkingwizard.ui.screen_map.MapScreenPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {
    @Provides
    @Singleton
    MapScreenPresenter provideMapScreenPresenter(Context context) {
        return new MapScreenPresenterImpl(context);
    }
}