package com.contag.app.model;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.contag.app.model.Contact;
import com.contag.app.model.ContagContag;
import com.contag.app.model.CustomShare;
import com.contag.app.model.Interest;
import com.contag.app.model.SocialProfile;
import com.contag.app.model.SocialPlatform;

import com.contag.app.model.ContactDao;
import com.contag.app.model.ContagContagDao;
import com.contag.app.model.CustomShareDao;
import com.contag.app.model.InterestDao;
import com.contag.app.model.SocialProfileDao;
import com.contag.app.model.SocialPlatformDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig contactDaoConfig;
    private final DaoConfig contagContagDaoConfig;
    private final DaoConfig customShareDaoConfig;
    private final DaoConfig interestDaoConfig;
    private final DaoConfig socialProfileDaoConfig;
    private final DaoConfig socialPlatformDaoConfig;

    private final ContactDao contactDao;
    private final ContagContagDao contagContagDao;
    private final CustomShareDao customShareDao;
    private final InterestDao interestDao;
    private final SocialProfileDao socialProfileDao;
    private final SocialPlatformDao socialPlatformDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        contactDaoConfig = daoConfigMap.get(ContactDao.class).clone();
        contactDaoConfig.initIdentityScope(type);

        contagContagDaoConfig = daoConfigMap.get(ContagContagDao.class).clone();
        contagContagDaoConfig.initIdentityScope(type);

        customShareDaoConfig = daoConfigMap.get(CustomShareDao.class).clone();
        customShareDaoConfig.initIdentityScope(type);

        interestDaoConfig = daoConfigMap.get(InterestDao.class).clone();
        interestDaoConfig.initIdentityScope(type);

        socialProfileDaoConfig = daoConfigMap.get(SocialProfileDao.class).clone();
        socialProfileDaoConfig.initIdentityScope(type);

        socialPlatformDaoConfig = daoConfigMap.get(SocialPlatformDao.class).clone();
        socialPlatformDaoConfig.initIdentityScope(type);

        contactDao = new ContactDao(contactDaoConfig, this);
        contagContagDao = new ContagContagDao(contagContagDaoConfig, this);
        customShareDao = new CustomShareDao(customShareDaoConfig, this);
        interestDao = new InterestDao(interestDaoConfig, this);
        socialProfileDao = new SocialProfileDao(socialProfileDaoConfig, this);
        socialPlatformDao = new SocialPlatformDao(socialPlatformDaoConfig, this);

        registerDao(Contact.class, contactDao);
        registerDao(ContagContag.class, contagContagDao);
        registerDao(CustomShare.class, customShareDao);
        registerDao(Interest.class, interestDao);
        registerDao(SocialProfile.class, socialProfileDao);
        registerDao(SocialPlatform.class, socialPlatformDao);
    }
    
    public void clear() {
        contactDaoConfig.getIdentityScope().clear();
        contagContagDaoConfig.getIdentityScope().clear();
        customShareDaoConfig.getIdentityScope().clear();
        interestDaoConfig.getIdentityScope().clear();
        socialProfileDaoConfig.getIdentityScope().clear();
        socialPlatformDaoConfig.getIdentityScope().clear();
    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public ContagContagDao getContagContagDao() {
        return contagContagDao;
    }

    public CustomShareDao getCustomShareDao() {
        return customShareDao;
    }

    public InterestDao getInterestDao() {
        return interestDao;
    }

    public SocialProfileDao getSocialProfileDao() {
        return socialProfileDao;
    }

    public SocialPlatformDao getSocialPlatformDao() {
        return socialPlatformDao;
    }

}
