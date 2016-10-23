/**
 * Copyright (C) 2013-2016 Stéphane Péchard.
 *
 * This file is part of PhotoBackup.
 *
 * PhotoBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PhotoBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.s13d.photobackup.media;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.io.Serializable;

import fr.s13d.photobackup.Log;
import fr.s13d.photobackup.PBApplication;

public class PBMedia implements Serializable {
    private final int id;
    private final String path;
    private final long dateAdded;
    private final SharedPreferences mediasPreferences;
    private String errorMessage;
    private PBMediaState state;
    public enum PBMediaState { WAITING, SYNCED, ERROR }


    //////////////////
    // Constructors //
    //////////////////
    public PBMedia(Cursor mediaCursor) {
        this.id = mediaCursor.getInt(mediaCursor.getColumnIndexOrThrow("_id"));
        this.path = mediaCursor.getString(mediaCursor.getColumnIndexOrThrow("_data"));
        this.dateAdded = mediaCursor.getLong(mediaCursor.getColumnIndexOrThrow("date_added"));
        this.mediasPreferences = PBApplication.getApp().getSharedPreferences(PBApplication.PB_MEDIAS_SHARED_PREFS, Context.MODE_PRIVATE);

        // Find state from the shared preferences
        String stateString = mediasPreferences.getString(String.valueOf(this.id), PBMedia.PBMediaState.WAITING.name());
        this.state = PBMedia.PBMediaState.valueOf(stateString);
        this.errorMessage = "";
    }


    ////////////
    // Methods//
    ////////////

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return "PBMedia: " + this.path;
    }


    /////////////////////////////////////////
    // Getters/Setters are the Java fun... //
    /////////////////////////////////////////
    public int getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public long getDateAdded() {
        return this.dateAdded;
    }

    public long getAge() {
        return System.currentTimeMillis() / 1000 - this.dateAdded;
    }

    public String getErrorMessage() { return this.errorMessage; }

    public void setErrorMessage(String newMessage) { this.errorMessage = newMessage; }

    public PBMediaState getState() {
        return this.state;
    }

    public void setState(PBMediaState mediaState) {
        if (this.state != mediaState) {
            this.state = mediaState;
            mediasPreferences.edit().putString(String.valueOf(this.getId()), mediaState.name()).apply();
            Log.i("PBMedia", "Set state " + mediaState.toString() + " to " + this.getPath());
        }
    }

}
