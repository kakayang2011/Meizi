package com.exsun.meizi.ui.fragment.meizi;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.exsun.meizi.R;
import com.exsun.meizi.entity.GankCategoryEntity;
import com.exsun.meizi.entity.HomeMixEntity;
import com.exsun.meizi.ui.adapter.AndroidAdapter;
import com.exsun.meizi.widge.OffsetDecoration;
import com.yuyh.library.Base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaokun on 2017/8/2.
 */

public class OtherFragment extends BaseFragment<MeiziPresenter, MeiziModel> implements MeiziContract.View
{
    public static final String ANDROID_CETOGARY = "android_cetogary";
    public static final String IOS_CETOGARY = "ios_cetogary";
    public static final String FRONT_CETOGARY = "front_cetogary";

    @Bind(R.id.home_rv)
    RecyclerView homeRv;
    @Bind(R.id.home_sr)
    SwipeRefreshLayout homeSr;

    public static final String COLUMN_RV = "meizi_column_rv";
    public static final int PRELOAD_SIZE = 6;
    private boolean mIsFirstTimeTouchBottom = true;
    private boolean isClearData = true;
    public int column;
    public int page = 1;
    public int count = 10;
    public AndroidAdapter adapter;
    public List<GankCategoryEntity.ResultsBean> datas;
    public String query1 = "";
    public String query2 = "";
    public String android;
    public String ios;
    public String front;

    public static OtherFragment getInstance(Bundle bundle)
    {
        OtherFragment fragment = new OtherFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_home;
    }

    @Override
    protected void initPresenter()
    {
        mPresenter.setVM(this, mModel);
    }

    @Override
    public void initData(Bundle bundle)
    {
        if (bundle == null)
        {
            return;
        }
        column = bundle.getInt(MeiziFragment.COLUMN_RV);
        android = bundle.getString(ANDROID_CETOGARY);
        ios = bundle.getString(IOS_CETOGARY);
        front = bundle.getString(FRONT_CETOGARY);
    }

    @Override
    public void initView(Bundle savedInstanceState, View view)
    {
        if (!TextUtils.isEmpty(android))
        {
            query2 = android;
        }
        if (!TextUtils.isEmpty(ios))
        {
            query2 = ios;
        }
        if (!TextUtils.isEmpty(front))
        {
            query2 = front;
        }
        final int spacing = getContext().getResources().getDimensionPixelSize(R.dimen.dimen_2_dp);
        homeRv.addItemDecoration(new OffsetDecoration(spacing));
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);
        homeRv.setLayoutManager(layoutManager);
        homeRv.addOnScrollListener(getOnButtomListener(layoutManager));
        homeSr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                isClearData = true;
                page = 1;
                getData(query1, query2, count, page);
            }
        });
    }

    @Override
    public void doBusiness(Context context)
    {
        datas = new ArrayList<>();
//        datas.addAll(homeMixEntities);
        adapter = new AndroidAdapter(mActivity, R.layout.item_category, datas);
        homeRv.setAdapter(adapter);
        getData(query1, query2, count, page);
    }

    public void getData(String query1, String query2, int count, int page)
    {
        homeSr.setRefreshing(true);
//        mPresenter.getMixData(query1, query2, count, page);
        mPresenter.getCategory(query2, count, page);
    }

    private RecyclerView.OnScrollListener getOnButtomListener(final StaggeredGridLayoutManager layoutManager)
    {
        return new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                int[] positions = layoutManager.findLastCompletelyVisibleItemPositions(new int[layoutManager.getSpanCount()]);
                boolean isBottom = positions[layoutManager.getSpanCount() - 1] >= (adapter.getItemCount() - PRELOAD_SIZE);
                if (!homeSr.isRefreshing() && isBottom)
                {
                    if (!mIsFirstTimeTouchBottom)
                    {
                        page++;
                        homeSr.setRefreshing(true);
                        getData(query1, query2, count, page);
                    } else
                    {
                        mIsFirstTimeTouchBottom = false;
                    }
                }
            }
        };
    }

    @Override
    public void getCategorySuccess(List<GankCategoryEntity.ResultsBean> resultsBeanList)
    {
        homeSr.setRefreshing(false);
        if (isClearData)
        {
            datas.clear();
        }
        datas.addAll(resultsBeanList);
        adapter.notifyDataSetChanged();
        isClearData = false;
    }

    @Override
    public void getDataFromDBSuccess(List<HomeMixEntity> homeMixEntities)
    {

    }

    @Override
    public void getMixSuccess(List<HomeMixEntity> homeMixEntities)
    {

    }

    @Override
    public void getCategoryFailed(Throwable e)
    {

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}