package com.contag.app.model;

import com.contag.app.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "INTEREST".
 */
public class Interest {

    private Long id;
    private String name;
    private Long contagUserId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient InterestDao myDao;

    private ContagContag contagContag;
    private Long contagContag__resolvedKey;


    public Interest() {
    }

    public Interest(Long id) {
        this.id = id;
    }

    public Interest(Long id, String name, Long contagUserId) {
        this.id = id;
        this.name = name;
        this.contagUserId = contagUserId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInterestDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getContagUserId() {
        return contagUserId;
    }

    public void setContagUserId(Long contagUserId) {
        this.contagUserId = contagUserId;
    }

    /** To-one relationship, resolved on first access. */
    public ContagContag getContagContag() {
        Long __key = this.contagUserId;
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
            contagUserId = contagContag == null ? null : contagContag.getId();
            contagContag__resolvedKey = contagUserId;
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
