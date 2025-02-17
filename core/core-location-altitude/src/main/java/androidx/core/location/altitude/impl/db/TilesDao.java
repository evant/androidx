/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.core.location.altitude.impl.db;

import androidx.room.Dao;
import androidx.room.Query;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Provides data access for entities within the Tiles table. */
@Dao
public interface TilesDao {

    /** Returns the tile associated with the provided token. */
    @Query("SELECT * FROM Tiles WHERE token = :token LIMIT 1")
    @Nullable TilesEntity get(@NonNull String token);
}
