package com.contag.app.model;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import com.contag.app.model.SocialProfile;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SOCIAL_PROFILE".
*/
public class SocialProfileDao extends AbstractDao<SocialProfile, Long> {

    public static final String TABLENAME = "SOCIAL_PROFILE";

    /**
     * Properties of entity SocialProfile.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Social_platform = new Property(1, String.class, "social_platform", false, "SOCIAL_PLATFORM");
        public final static Property Platform_id = new Property(2, String.class, "platform_id", false, "PLATFORM_ID");
        public final static Property Platform_username = new Property(3, String.class, "platform_username", false, "PLATFORM_USERNAME");
        public final static Property ContagUserId = new Property(4, Long.class, "contagUserId", false, "CONTAG_USER_ID");
    };

    private DaoSession daoSession;


    public SocialProfileDao(DaoConfig config) {
        super(config);
    }
    
    public SocialProfileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SOCIAL_PROFILE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"SOCIAL_PLATFORM\" TEXT," + // 1: social_platform
                "\"PLATFORM_ID\" TEXT," + // 2: platform_id
                "\"PLATFORM_USERNAME\" TEXT," + // 3: platform_username
                "\"CONTAG_USER_ID\" INTEGER);"); // 4: contagUserId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SOCIAL_PROFILE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SocialProfile entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String social_platform = entity.getSocial_platform();
        if (social_platform != null) {
            stmt.bindString(2, social_platform);
        }
 
        String platform_id = entity.getPlatform_id();
        if (platform_id != null) {
            stmt.bindString(3, platform_id);
        }
 
        String platform_username = entity.getPlatform_username();
        if (platform_username != null) {
            stmt.bindString(4, platform_username);
        }
 
        Long contagUserId = entity.getContagUserId();
        if (contagUserId != null) {
            stmt.bindLong(5, contagUserId);
        }
    }

    @Override
    protected void attachEntity(SocialProfile entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SocialProfile readEntity(Cursor cursor, int offset) {
        SocialProfile entity = new SocialProfile( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // social_platform
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // platform_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // platform_username
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // contagUserId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SocialProfile entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSocial_platform(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPlatform_id(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPlatform_username(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setContagUserId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SocialProfile entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SocialProfile entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getContagContagDao().getAllColumns());
            builder.append(" FROM SOCIAL_PROFILE T");
            builder.append(" LEFT JOIN CONTAG_CONTAG T0 ON T.\"CONTAG_USER_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected SocialProfile loadCurrentDeep(Cursor cursor, boolean lock) {
        SocialProfile entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        ContagContag contagContag = loadCurrentOther(daoSession.getContagContagDao(), cursor, offset);
        entity.setContagContag(contagContag);

        return entity;    
    }

    public SocialProfile loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<SocialProfile> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<SocialProfile> list = new ArrayList<SocialProfile>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<SocialProfile> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<SocialProfile> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
