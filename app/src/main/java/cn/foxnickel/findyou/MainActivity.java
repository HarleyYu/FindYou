package cn.foxnickel.findyou;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cn.foxnickel.findyou.fragment.ControlFragment;
import cn.foxnickel.findyou.fragment.ControlledFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_PERMISSIONS = 100;
    private final String TAG = getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TextView mTitleText;
    private ControlFragment mControlFragment = null;
    private ControlledFragment mControlledFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*6.0以上申请权限*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*左上角的菜单按钮*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mTitleText = (TextView) findViewById(R.id.toolbar_title);

        setDefaultFragment();

    }


    @TargetApi(Build.VERSION_CODES.M)
    private Boolean requestPermission(String... permissions) {
        for (String permission : permissions) {
            switch (checkSelfPermission(permission)) {
                case PackageManager.PERMISSION_GRANTED:
                    Log.i(TAG, "onCreate: Granted...");
                    break;
                case PackageManager.PERMISSION_DENIED:
                    Log.i(TAG, "onCreate: Denied...");
                    requestPermissions(permissions, REQUEST_PERMISSIONS);//请求权限
                    break;
            }
        }
        return false;
    }

    private void setDefaultFragment() {
        mTitleText.setText("主控模式");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, new ControlFragment());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Snackbar.make(mDrawerLayout, "You clicked the refresh button", Snackbar.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_control:
                Snackbar.make(mDrawerLayout, "主控模式", Snackbar.LENGTH_SHORT).show();
                mTitleText.setText("主控模式");
                if (mControlFragment == null) {
                    mControlFragment = new ControlFragment();
                }
                transaction.replace(R.id.content_main, mControlFragment);
                break;
            case R.id.nav_be_controlled:
                Snackbar.make(mDrawerLayout, "被控模式", Snackbar.LENGTH_SHORT).show();
                mTitleText.setText("被控模式");
                if (mControlledFragment == null) {
                    mControlledFragment = new ControlledFragment();
                }
                transaction.replace(R.id.content_main, mControlledFragment);
                break;
        }
        transaction.commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
