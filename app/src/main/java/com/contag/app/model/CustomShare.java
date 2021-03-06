package com.contag.app.model;

import com.contag.app.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "CUSTOM_SHARE".
 */
public class CustomShare {

    private Long id;
    private String field_name;
    private String user_ids;
    private Boolean is_public;
    private Boolean is_private;
    private Long userID;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CustomShareDao myDao;

    private ContagContag contagContag;
    private Long contagContag__resolvedKey;


    public CustomShare() {
    }

    public CustomShare(Long id) {
        this.id = id;
    }

    public CustomShare(Long id, String field_name, String user_ids, Boolean is_public, Boolean is_private, Long userID) {
        this.id = id;
        this.field_name = field_name;
        this.user_ids = user_ids;
        this.is_public = is_public;
        this.is_private = is_private;
        this.userID = userID;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCustomShareDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getUser_ids() {
        return user_ids;
    }

    public void setUser_ids(String user_ids) {
        this.user_ids = user_ids;
    }

    public Boolean getIs_public() {
        return is_public;
    }

    public void setIs_public(Boolean is_public) {
        this.is_public = is_public;
    }

    public Boolean getIs_private() {
        return is_private;
    }

    public void setIs_private(Boolean is_private) {
        this.is_private = is_private;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    /** To-one relationship, resolved on first access. */
    public ContagContag getContagContag() {
        Long __key = this.userID;
        if (contagContag__resolvedKey == null || !contagContag__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContagContagDao targetDao = daoSession.getContagContagDao();
            ContagContag contagContagNew = targetDao.load(__key);
            synchronized (this) {
                contagContag = contagContagNew;
            	contagContag__resolvedKey = __key;
            }
        }
        return contagContag;
    }

    public void setContagContag(ContagContag contagContag) {
        synchronized (this) {
            this.contagContag = contagContag;
            userID = contagContag == null ? null : contagContag.getId();
            contagContag__resolvedKey = userID;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
