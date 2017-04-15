package com.cambrian.android.ganarticles;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.fragments.AboutFragment;
import com.cambrian.android.ganarticles.fragments.CategoryFragment;
import com.cambrian.android.ganarticles.fragments.DailyFragment;
import com.cambrian.android.ganarticles.fragments.StarredListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AppFragment.OnFragmentChangeListener {
    //    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;

    /**
     * 当前选中的 navigationView 上的 item id
     */
    private int mDisplayItemId;

    /**
     * 上一次显示的fragment 的type
     */
    private String mLastFragmentType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // NavigationView 的初始化
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            // 显示第一次加载的 Fragment
            initFragment();
        }
    }

    /**
     * 显示初始fragment
     */
    private void initFragment() {
        mDisplayItemId = R.id.nav_today;
        String type = getTypeByItemId(mDisplayItemId);
        FragmentManager fm = getSupportFragmentManager();
        Fragment dailyFragment = fm.findFragmentByTag(type);
        if (dailyFragment == null) {
            dailyFragment = DailyFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.fragment_container, dailyFragment, type)
                    .commit();
        }
        mLastFragmentType = type;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigationView item clicks here.
        mDisplayItemId = item.getItemId();
        if (mDisplayItemId == R.id.nav_about) {
            startActivity(AboutActivity.newIntent(this));
            return true;
        } else {
            displayFragment();
            return true;
        }
    }

    /**
     * 将目前正在显示的 Fragment 的 toolbar 与 DrawerLayout 联系起来
     *
     * @param toolbar toolbar of fragment
     */
    @Override
    public void bindToolbar(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onShowArticleDetail(Article article) {
        // TODO: 2017/4/15
    }

    /**
     * 切换 fragment 的方法， 会将此次 Transaction 存入回退栈
     *
     * @param newFragment 新的fragment
     */
    @Override
    public void onFragmentChange(String type, Fragment newFragment) {
        if (newFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, newFragment, type)
                    .addToBackStack(type)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onFragmentPop() {
        // 回退栈有 fragment 则将其出栈
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * 显示相应的fragment
     */
    private void displayFragment() {
        String type = getTypeByItemId(mDisplayItemId);
        // 确定两次点击的 item 不同
        if (!type.equals(mLastFragmentType)) {
            FragmentManager fm = getSupportFragmentManager();

            Fragment fragment = fm.findFragmentByTag(type);
            // 创建 fragment
            if (fragment == null) {
                fragment = createFragment(mDisplayItemId, type);
            }

            if (fragment.isAdded()) {
                fm.popBackStackImmediate(type, 0);
                // 需要显示的fragment被添加过
                fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(type)
                        .commit();
            } else {
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack(0, 0);
                }
                fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment, type)
                        .addToBackStack(type)
                        .commit();
            }

            // 记录上一次添加的fragment和相应的itemId
            mLastFragmentType = type;
        }
        // 关闭 DrawerLayout
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * 根据item id获得相应fragment的tag
     *
     * @param itemId {@link NavigationView} 的 itemId
     * @return tag of fragment
     */
    @NonNull
    private String getTypeByItemId(int itemId) {
        if (itemId == R.id.nav_today) {
            return getString(R.string.type_daily);
        } else if (itemId == R.id.nav_android) {
            return getString(R.string.type_android);
        } else if (itemId == R.id.nav_ios) {
            return getString(R.string.type_ios);
        } else if (itemId == R.id.nav_front_end) {
            return getString(R.string.type_front_end);
        } else if (itemId == R.id.nav_welfare) {
            return getString(R.string.type_welfare);
        } else if (itemId == R.id.nav_star) {
            return getString(R.string.type_star);
        } else {
            return getString(R.string.type_about);
        }
    }

    /**
     * 根据 String 创建 相应的 Fragment
     *
     * @param type type name of fragment
     * @return Fragment
     */
    private Fragment createFragment(int itemId, String type) {
        switch (itemId) {
            case R.id.nav_today:
                return DailyFragment.newInstance();
            case R.id.nav_star:
                return StarredListFragment.newInstance();
            case R.id.nav_about:
                return AboutFragment.newInstance();
            default:
                return CategoryFragment.newInstance(type);
        }
    }
}
