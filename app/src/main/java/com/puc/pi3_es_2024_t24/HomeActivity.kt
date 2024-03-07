package com.puc.pi3_es_2024_t24

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.checkerframework.checker.units.qual.A

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private var currentFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        auth = Firebase.auth

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView =findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null){
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val transation: FragmentTransaction = supportFragmentManager.beginTransaction()
        transation.replace(R.id.fragment_container, fragment)
        transation.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(HomeFragment())
            }
            R.id.bottom_home -> {
                replaceFragment(HomeFragment())
            }
            R.id.nav_locker -> {
                replaceFragment(LockerFragment())
            }
            R.id.bottom_locker -> {
                replaceFragment(LockerFragment())
            }
            R.id.nav_settings -> {
                replaceFragment(SettingsFragment())
            }
            R.id.bottom_settings -> {
                replaceFragment(SettingsFragment())
            }
            R.id.nav_about -> {
                replaceFragment(AboutFragment())
            }
            R.id.bottom_about -> {
                replaceFragment(AboutFragment())
            }
            R.id.nav_logout ->{
                auth.signOut()
                Toast.makeText(this, "saiu da conta", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        if (currentFragment != null) {
            replaceFragment(currentFragment!!)
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)

        }else{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
