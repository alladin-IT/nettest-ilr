/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.rmbt.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import at.alladin.rmbt.client.db.model.DbHistoryItem;
import at.alladin.rmbt.client.db.model.DbMeasurementItem;

/**
 * Created by lb on 12.09.16.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "nettest.sqlite";
    private static final int DATABASE_VERSION = 1;

    private Dao<DbMeasurementItem, String> measurementDao = null;

    private Dao<DbHistoryItem, String> historyDao = null;

    public DatabaseHelper(Context context) {
        super(context,
                //for use of external storage uncomment line below:
                /* Environment.getExternalStorageDirectory().getAbsolutePath() + "/openrmbt/" + */
                DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "DB onCreate");
            TableUtils.createTable(connectionSource, DbMeasurementItem.class);
            TableUtils.createTable(connectionSource, DbHistoryItem.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "DB error onCreate ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.i(DatabaseHelper.class.getName(), "DB onUpgrade");
        try {
            TableUtils.dropTable(connectionSource, DbMeasurementItem.class, true);
            TableUtils.dropTable(connectionSource, DbHistoryItem.class, true);
            onCreate(db, connectionSource);
        }
        catch(final SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "DB error onUpgrade ", e);
        }
    }

    /**
     * returns DAO for class: {@link DbMeasurementItem}
     * @return
     * @throws SQLException
     */
    public Dao<DbMeasurementItem, String> getMeasurementDao() {
        if (measurementDao == null) {
            try {
                measurementDao = DaoManager.createDao(getConnectionSource(), DbMeasurementItem.class);
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return measurementDao;
    }

    /**
     * returns DAO for class: {@link DbHistoryItem}
     * @return
     * @throws SQLException
     */
    public Dao<DbHistoryItem, String> getHistoryDao() {
        if (historyDao == null) {
            try {
                historyDao = DaoManager.createDao(getConnectionSource(), DbHistoryItem.class);
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return historyDao;
    }

    @Override
    public void close() {
        super.close();
    }
}
