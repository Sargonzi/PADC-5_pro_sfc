package com.padcmyanmar.sfc.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import com.padcmyanmar.sfc.data.vo.NewsVO;

@Dao
public interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNew(NewsVO newsVO);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertNews(NewsVO... newsVOS);


}
