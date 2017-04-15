package cn.foxnickel.findyou;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cn.foxnickel.findyou.fragment.ControlFragment;
import cn.foxnickel.findyou.fragment.ControlledFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
