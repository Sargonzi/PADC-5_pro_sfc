package com.padcmyanmar.sfc.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import com.padcmyanmar.sfc.data.vo.PublicationVO;

@Dao
public interface PublicationDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPublication(PublicationVO publicationVO);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertPublications(PublicationVO... publicationVOS);
}
