package com.padcmyanmar.sfc.data.models;

import android.content.Context;
import android.util.Log;

import com.padcmyanmar.sfc.SFCNewsApp;
import com.padcmyanmar.sfc.data.db.AppDatabase;
import com.padcmyanmar.sfc.data.vo.NewsVO;
import com.padcmyanmar.sfc.events.RestApiEvents;
import com.padcmyanmar.sfc.network.MMNewsDataAgent;
import com.padcmyanmar.sfc.network.MMNewsDataAgentImpl;
import com.padcmyanmar.sfc.network.reponses.GetNewsResponse;
import com.padcmyanmar.sfc.utils.AppConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aung on 12/3/17.
 */

public class NewsModel {

    private static NewsModel objInstance;

    private List<NewsVO> mNews;
    private int mmNewsPageIndex = 1;
    private AppDatabase mAppDatabase;
    private PublishSubject<List<NewsVO>> mNewsListPublisher;

    private NewsModel() {
        EventBus.getDefault().register(this);
        mNews = new ArrayList<>();
    }

    public static NewsModel getInstance() {
        if (objInstance == null) {
            objInstance = new NewsModel();
        }
        return objInstance;
    }

    public void initDatabase(Context context) {
        mAppDatabase = AppDatabase.getNewsSFCDatabase(context);
    }

    public void initPublishSubject(PublishSubject<List<NewsVO>> newSubject) {
        this.mNewsListPublisher = newSubject;
    }

    public void loadMMNews(Context context) {
        final Observable<GetNewsResponse> newsResponseObservable = getNewsListResponseObservable(context);
        newsResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetNewsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GetNewsResponse value) {
                        mNewsListPublisher.onNext(value.getNewsList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        RestApiEvents.ErrorInvokingAPIEvent errorEvent = new RestApiEvents.ErrorInvokingAPIEvent(e.getMessage());
                        EventBus.getDefault().post(errorEvent);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNewsDataLoaded(RestApiEvents.NewsDataLoadedEvent event) {
        mNews.addAll(event.getLoadNews());
        mmNewsPageIndex = event.getLoadedPageIndex() + 1;

        for (NewsVO newsVO : event.getLoadNews()) {
            long pid = mAppDatabase.publicationDao().insertPublication(newsVO.getPublication());
            newsVO.setPublicationId(newsVO.getPublication().getPublicationId());
            long pid2 = mAppDatabase.newsDao().insertNew(newsVO);
            Log.e("test", pid + " and " + pid2);
        }

    }

    private Observable<GetNewsResponse> getNewsListResponseObservable(Context context) {
        SFCNewsApp sfcNewsApp = (SFCNewsApp) context;
        return sfcNewsApp.getTheAPI().loadMMNews(mmNewsPageIndex, AppConstants.ACCESS_TOKEN);
    }
}
